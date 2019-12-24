package CDNL;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
import java.util.List;

enum Operation {
    ADD,REMOVE
}

public class Controller {
    
    public HBox breadcrumbs;
    public AnchorPane RktList;
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
                bpl = new Button(rocket.Title);
                treeView.prefWidthProperty().bind(RktList.widthProperty());

                bpl.getStyleClass().add("btn-list");
                bpl.getStyleClass().add("grey");

                TreeItem<Button> rootItem = new TreeItem<Button>(bpl);
                //bpl.prefWidthProperty().bind(RktList.widthProperty());
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

                for(int i = 0; i < initials.length; i++) {
                    Button btn = new Button(initials[i]);
                    btn.getStyleClass().add("btn-list");
                    btn.getStyleClass().add("grey");
                    treeView.getRoot().getChildren().add(
                            new TreeItem<Button>(btn)
                    );
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

    public void ChangeState() {

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

    @FXML
    public void initialize() {

    }
}
