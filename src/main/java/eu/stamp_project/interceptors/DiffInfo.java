package eu.stamp_project.interceptors;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class DiffInfo {

    private Map<String, Collection<Integer>> fileEntries;

    public DiffInfo(BufferedReader reader) {
        try {
            Pattern pattern = Pattern.compile("^(?<path>[^\\:]+)\\:(?<lines>\\d+(,\\d+)*$)");
            fileEntries = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher match = pattern.matcher(line);
                if (!match.matches())
                    throw new RuntimeException("Line was not matched: " + line);
                fileEntries.put(
                        match.group("path"),
                        Arrays.stream(match.group("lines")
                                .split(","))
                                .map(Integer::getInteger)
                                .sorted()
                                .collect(Collectors.toList()));
            }
        }
        catch (IOException exc) {
            throw new RuntimeException("Error while reading the diff file", exc);
        }
    }

    public int getFirstLineIn(String path, int firstLine, int lastLine) {
        Collection<Integer> lines = fileEntries.getOrDefault(path, null);
        if(lines == null) return -1;
        for(int i : lines)
            if( i >= firstLine && i <= lastLine)
                return i;
        return -1;
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
