package org.example.maurice.mauriceplugin.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.example.maurice.mauriceplugin.MauricePlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.logging.Logger;

public class SettingsHandler {

    static File dataFolder;
    static SettingsDTO currentSettings;
    static String currentName;

    public static void getDataFile(){
        dataFolder = new File(MauricePlugin.getInstance().getDataFolder(), "data"); // get folder "plugins/MyPlugin/data"
        dataFolder.mkdirs();
    }

    public static void saveToFile(SettingsDTO settingsDTO) {
        try {
            File file = new File(dataFolder, settingsDTO.name + ".log");
            if(!file.exists()) {
                boolean created = file.createNewFile();
                if (!created){
                    throw new RuntimeException("File to save settings could not be created");
                }
            }
            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream o = new ObjectOutputStream(f);

            o.writeObject(settingsDTO);
            Logger.getLogger("oui").info(dataFolder.getAbsolutePath());

            o.close();
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not save settings to file", e);
        }
    }

    public static void readFromFile(String name) {
        try {
            File file = new File(dataFolder, name + ".log");
            if(!file.exists()) {
                boolean created = file.createNewFile();
                if (!created){
                    throw new RuntimeException("File to save settings could not be created");
                }
            }
            FileInputStream fi = new FileInputStream(file);
            ObjectInputStream oi = new ObjectInputStream(fi);

            currentSettings = (SettingsDTO) oi.readObject();
            currentName = name;

            oi.close();
            fi.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not read settings from file");
        }
    }

    public static String getCurrentName() {
        return currentName;
    }

    public static String get(SettingsEnum se){
        return currentSettings.get(se);
    }

    public static boolean areSettingsLoaded(){
        return !(currentSettings == null);
    }

    public static int getTimeToHide(){
        return currentSettings.time_to_hide;
    }

    public static int getTimeToSearch(){
        return currentSettings.time_to_search;
    }

    public static Location getStartPos(){
        return currentSettings.getDeserializedLocation();
    }
    public static String getStartPosString(){
        return currentSettings.startPos;
    }
    public static void setTimeToHide(int time_to_hide){
        currentSettings.time_to_hide = time_to_hide;
        saveToFile(currentSettings);
    }

    public static void setTimeToSearch(int time_to_search){
        currentSettings.time_to_search = time_to_search;
        saveToFile(currentSettings);
    }

    public static void setStartPos(Location startPos){
        currentSettings.startPos = currentSettings.getSerializedLocation(startPos);
        saveToFile(currentSettings);
    }
}
