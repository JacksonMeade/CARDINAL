package CDNL;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Controller {
    
    public HBox breadcrumbs;
    public AnchorPane RktList;
    public TabPane Tabulate;
    public Pane FlightReconstruction;
    public Pane FlowReconstruction;
    public Text Debugger;
    public MenuItem ImportRocket;

    public void HelloWorld() {
        Debugger.setText("Hello World!");
    }

    public void addRocket() throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Import a Rocket");
        chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("BRRDS Payload Files",".BRD"));
        File file = chooser.showOpenDialog(Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null));

        if (file != null) {
            ArrayList<String> imp = Model.ImportRkt(file);

            rkt(Operation.ADD, imp);
        }
    }

    public void rkt(Operation op, ArrayList<String> RocketInfo) throws IOException {
        switch (op) {
            case ADD:

                Rocket rocket = new Rocket();
                rocket.Title = RocketInfo.get(0);

                    ArrayList<String> temp = new ArrayList<>();
                    Model.ProjectRockets.forEach(x ->
                    {
                        if (x.Title.equals(rocket.Title)) {
                            temp.add(x.Title);
                        }
                    });

                if (temp.size() > 0) {
                    rocket.Title += " (" + temp.size() + ")";
                }

                Button bpl;

                TreeView treeView = new TreeView<>();
                treeView.setId(rocket.Title);
                bpl = new Button(rocket.Title);
                treeView.setPrefWidth(300);
                treeView.maxWidthProperty().bind(RktList.prefWidthProperty());

                bpl.getStyleClass().add("btn-list");
                bpl.getStyleClass().add("feature");

                TreeItem<Button> rootItem = new TreeItem<Button>(bpl);
                bpl.maxWidthProperty().bind(treeView.widthProperty());
                rootItem.setExpanded(false);

                treeView.setRoot(rootItem);

                treeView.getSelectionModel().selectedItemProperty()
                        .addListener(new ChangeListener() {
                            @Override
                            public void changed(ObservableValue ob, Object ol, Object nv) {
                                Model.State.clear();
                                Model.StateRocket = rocket;
                                Model.State.add(rocket.Title);

                                bc(Operation.ADD,Model.State);
                            }
                        });
                String initials[] = new String[]{"Inertial","Pressure","Camera","Add Data Source"};

                for(int i = 0;i < initials.length;i++) {

                    String btnName = initials[i];
                    String old = btnName;
                    if(RocketInfo.size() >= (2*i + 4)) {
                        btnName += " [Click to Enable]";
                    }
                    else {
                        btnName += " [No Data Detected]";
                    }

                    Button btn = new Button(btnName);


                    String finalBtnName = btnName;

                    TreeItem spec = new TreeItem<Button>(btn);

                    btn.getStyleClass().add("btn-list");
                    btn.getStyleClass().add("grey");
                    treeView.getRoot().getChildren().add(
                            spec
                    );
                    btn.setId(initials[i].toLowerCase());
                    btn.maxWidthProperty().bind(treeView.widthProperty());
                    btn.setOnAction((event) ->
                    {
                        if (finalBtnName.contains(" [Click to Enable]")) {
                            Button e = ((Button) event.getSource());

                            AddData(DataType.valueOf(old.toUpperCase()),rocket,RocketInfo,e,spec,treeView);

                            e.getStyleClass().removeAll();
                            e.getStyleClass().add("btn-list");
                            e.getStyleClass().add(old.toLowerCase());
                            e.setText(old);
                            e.setOnAction((ev) ->
                            {
                                try {
                                    OpenTab("Data",rocket.Title + ": " + old + " Data", DataType.INERTIAL);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            });
                        }
                    });
                }
                    RktList.getChildren().add(treeView);

                    BorderPane bp = new BorderPane();
                    FlightReconstruction.getChildren().add(
                            bp
                    );
                    bp.setPrefHeight(FlightReconstruction.getHeight());
                    bp.setPrefWidth(FlightReconstruction.getWidth());
                    bp.setStyle("-fx-background-color: transparent; -fx-background-image: url('CDNL/FlightReconstructionBackground.png'); -fx-background-size:cover;");

                        Button bpc = new Button("Reconstruct Flight");
                        bpc.getStyleClass().add("btn-primary");
                    bp.setCenter(
                        bpc
                    );

                    bp = new BorderPane();
                    FlowReconstruction.getChildren().add(
                            bp
                    );
                    bp.setPrefHeight(FlowReconstruction.getHeight());
                    bp.setPrefWidth(FlowReconstruction.getWidth());
                    bp.setStyle("-fx-background-color: transparent; -fx-background-image: url('CDNL/FlowReconstructionBackground.png'); -fx-background-size:cover;");

                        bpc = new Button("Reconstruct Flow");
                        bpc.getStyleClass().add("btn-primary");
                    bp.setCenter(
                            bpc
                    );


                    Model.State.clear();
                    Model.State.add(0, rocket.Title);
                    Model.StateRocket = rocket;

                    bc(Operation.ADD,Model.State);

                    Model.ProjectRockets.add(rocket);

                    Debugger.setText(String.valueOf(Model.State) + " " + String.valueOf(Model.StateRocket));
                break;
            default:

                break;
        }
    }

    public void AddData(DataType dataType, Rocket rocket, ArrayList<String> data, Button instigator, TreeItem nest, TreeView hierarchy) {
        Model.author(dataType, rocket, data);

        ArrayList<String> initials = new ArrayList<>();
         //NEST NEW SUB
        switch(dataType) {
            case INERTIAL:
                initials.addAll(Arrays.asList("Position","Orientation","Linear Velocity","Angular Velocity","Linear Acceleration","Angular Acceleration"));
                    break;
        }

        for(int i = 0;i < initials.size();i++) {

            String btnName = initials.get(i);

            Button btn = new Button(btnName);
            btn.setOnAction((event) ->
            {

            });

            btn.getStyleClass().add("btn-list");
            btn.getStyleClass().add("grey");
            nest.getChildren().add(
                    new TreeItem<Button>(btn)
            );
            btn.setId(initials.get(i).toLowerCase());
            btn.maxWidthProperty().bind(hierarchy.widthProperty());
        }
    }

    public void ChangeState(ArrayList<String> states) {
        Model.State.clear();
        Model.State.addAll(states);
    }

    public void bc(Operation op, ArrayList<String> states) {
        switch (op) {
            case ADD:
                breadcrumbs.getChildren().removeAll(breadcrumbs.getChildren());
                states.forEach(state ->  {
                    Button btn = new Button(state);
                    btn.setId(state);
                    btn.setOnAction((event) -> {
                        for (int i = (Model.State.size() - states.indexOf(state) - 1);i>0;i--) {
                            Model.State.remove(i);
                        }
                    });
                    breadcrumbs.getChildren().add(btn);
                });
                break;
            case REMOVE:
                break;
            default:
                break;
        }
    }

    public void OpenTab(String id, String title, DataType dataType) throws IOException {
        switch (id) {
            case "Data":
                Tab tab = (Tab)FXMLLoader.load(this.getClass().getResource("dataTab.fxml"));
                tab.setText(title);
                
                if (!Tabulate.getTabs().contains(tab)) { Tabulate.getTabs().add(tab); }
                Tabulate.getSelectionModel().select(tab);
                break;
        }
    }

    @FXML
    public void initialize() {

    }
}
