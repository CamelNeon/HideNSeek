package org.example.maurice.mauriceplugin.Utils;

import org.bukkit.Location;
import org.example.maurice.mauriceplugin.MauricePlugin;

import java.io.*;
import java.util.logging.Logger;

public class SettingsHandler {

    static File dataFolder;

    public static void getDataFile(){
        dataFolder = new File(MauricePlugin.getInstance().getDataFolder(), "data"); // get folder "plugins/MyPlugin/data"
        dataFolder.mkdirs();
    }

    public static void saveToFile(Settings settings) {
        try {
            File file = new File(dataFolder, settings.name + ".log");
            if(!file.exists()) {
                boolean created = file.createNewFile();
                if (!created){
                    throw new RuntimeException("File to save settings could not be created");
                }
            }
            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream o = new ObjectOutputStream(f);

            o.writeObject(settings);
            Logger.getLogger("oui").info(dataFolder.getAbsolutePath());

            o.close();
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not save settings to file", e);
        }
    }

    public static Settings readFromFile(String name) {
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

            Settings currentSettings = (Settings) oi.readObject();

            oi.close();
            fi.close();

            return currentSettings;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not read settings from file", e);
        }
    }
}
