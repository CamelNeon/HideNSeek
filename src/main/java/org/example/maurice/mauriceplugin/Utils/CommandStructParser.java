package org.example.maurice.mauriceplugin.Utils;

import org.checkerframework.checker.units.qual.A;
import org.example.maurice.mauriceplugin.MauricePlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CommandStructParser {

    public static HashMap<String, ArrayList<String>> readFile(String fileName){

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(MauricePlugin.getInstance().getResource(fileName))))) {
            String prefix = reader.readLine();
            prefix = prefix.substring(0, prefix.length() - 1);
            return read(new HashMap<>(), reader, prefix);
        } catch (IOException ex) {
            throw new RuntimeException("", ex);
        }

    }

    private static HashMap<String, ArrayList<String>> read(HashMap<String, ArrayList<String>> map, BufferedReader reader, String prefix){
        try {
            String line = reader.readLine();
            line = line.replace("\t", "");
            line = line.replace(" ", "");

            while (line != null) {
                line = line.replace("\t", "");
                line = line.replace(" ", "");
                map.computeIfAbsent(prefix, k -> new ArrayList<>());
                map.get(prefix).add(line.substring(0, line.length() - 1));
                if (line.contains(":")) {
                    if (line.contains("<")) read(map, reader, prefix + "*");
                    else read(map, reader, prefix + line.substring(0 , line.length() - 1));
                }
                if (line.contains("!")){
                    return map;
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
}
