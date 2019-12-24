package CDNL;

import java.io.File;
import java.io.IOException;
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



}
