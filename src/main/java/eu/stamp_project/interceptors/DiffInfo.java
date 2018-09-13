package eu.stamp_project.interceptors;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;


public class DiffInfo {


    private List<String> fileEntries;
    //private Map<String, Collection<Integer>> fileEntries;

    public DiffInfo(BufferedReader reader) {
        try {

            fileEntries = new LinkedList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                int colonIndex = line.indexOf(':');
                fileEntries.add((colonIndex < 0)?line:line.substring(0, colonIndex));
            }
        }
        catch (IOException exc) {
            throw new RuntimeException("Error while reading the diff file", exc);
        }
    }

    public boolean contains(String path) {

        for(String entry: fileEntries){
            if(entry.endsWith(path)) {
                return true;
            }
        }
        return false;
    }

    public static DiffInfo fromFile(String path) {
        try {
            return new DiffInfo(Files.newBufferedReader(Paths.get(path)));
        }
        catch (IOException exc) {
            throw new RuntimeException("Could not open file: " + path, exc);
        }
    }

}
