package CDNL;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Model {

    public static ArrayList<String> State = new ArrayList<String>();
    public static Rocket StateRocket = new Rocket();

    public static ArrayList<Rocket> ProjectRockets = new ArrayList<Rocket>();

    public static ArrayList<String> ImportRkt(File file) throws IOException {
        return readl(file);
    }

    public void VISINAV() {

    }

    public void FADS() {

    }

    public void IMUCorrection() {

    }

    // Take filename, throw exception if invalid, read all the symbols in the file, return those symbols
    public static String read(String fileName) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(fileName)));
        return content;
    }

    //read linewise
    public static ArrayList<String> readl(File file) throws IOException {
        List<String> l = Files.readAllLines(file.toPath());
        ArrayList<String> lines = new ArrayList<String>();
        l.forEach(x -> lines.add(x));
        return lines;
    }

    public static void write(String fileName, String content) throws IOException {
        Files.write(Paths.get(fileName),content.getBytes(), StandardOpenOption.CREATE);
    }

    public static void author(DataType dataType, Rocket rocket, ArrayList<String> data) {

        //REMOVE HEADINGS FROM FILE
        ArrayList<String> cData = data;
        cData.remove(0);
        cData.remove(0);


        //NOW SYNC WITH TIME
        // Now you may ask yourself, how do I work this?
        // And you may ask yourself, where is that large automobile?
        // And you may tell yourself, that is not my beautiful... code?
        // Unfortunately, I've decided to use redundant code here because we need to specify what the array will look like by datatype
        // Also, if the above questions don't make any sense, try out the Talking Heads. They're good listening for this kind of thing.
        switch(dataType) {
            case INERTIAL:

                //STEP 1: Convert our data into a row of strings
                ArrayList<String> row = new ArrayList<String>(Arrays.asList((cData.get(0)).split("\\s*,\\s*")));

                    // STEP 2: Convert that row of strings into a row of floats <- this is datatype dependent
                    ArrayList<Float> fData = new ArrayList<>();
                    for(int j = 0; j < row.size(); j++){
                        fData.add(Float.valueOf(row.get(j)));
                    }

                //STEP 3: Mutate the data so that each timestep is associated with a datatype-dependent vector of data values
                ArrayList<ArrayList<Vector<Float>>> mData = new ArrayList<>();
                    ArrayList<Vector<Float>> fRow = new ArrayList<>();
                    //NOW, it gets messy, but here goes: we take the data, and at every 7th value, make a new row and store the
                    //old one in our array of mutated data
                    for (int k = 0; k < fData.size(); k++) {

                        Vector<Float> relv = new Vector<>();
                        relv.add(fData.get(k));
                        if (k != 0 && k % 3 ==0) {
                            fRow.add(relv);
                            relv.clear();
                        }

                        if ((k != 0 && k % 28 == 0) || (fData.size() - k == 0)) {
                            mData.add(fRow);
                            fRow.clear();
                        }

                    }

                rocket.Inertial = mData;

                    for(int l = 0; l < mData.size(); l++) {
                        rocket.Position.add(mData.get(l).get(0));
                        for (int m = 1; m < mData.get(l).size(); m++) {
                            if (m % 7 == 0) {
                                rocket.Position.add(mData.get(l).get(m));
                            }
                            else if (m % 6 == 0) {
                                rocket.Orientation.add(mData.get(l).get(m));
                            }
                            else if (m % 5 == 0) {
                                rocket.LVelocity.add(mData.get(l).get(m));
                            }
                            else if (m % 4 == 0) {
                                rocket.AVelocity.add(mData.get(l).get(m));
                            }
                            else if (m % 3 == 0) {
                                rocket.LAcceleration.add(mData.get(l).get(m));
                            }
                            else if (m % 2 == 0) {
                                rocket.AAcceleration.add(mData.get(l).get(m));
                            }
                        }
                    }

                break;
            case PRESSURE:
                //SEE ABOVE

                //rocket.Pressure = mData;
                break;
            case CAMERA:
                //MUST DO IMAGE
                //rocket.Camera = mData;
                break;
        }

    }

}
