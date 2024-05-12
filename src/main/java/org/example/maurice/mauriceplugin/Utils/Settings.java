package org.example.maurice.mauriceplugin.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Settings implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    String name ;
    int time_to_hide;
    int time_to_search;
    String startPos;
    ArrayList<StructData> structdatas = new ArrayList<>();

    public Settings(String name, int time_to_hide, int time_to_search, Location startPos){
        this.name = name;
        this.time_to_hide = time_to_hide;
        this.time_to_search = time_to_search;
        this.startPos = getSerializedLocation(startPos);
    }

    public String get(SettingsEnum se){
        switch (se){
            case TIME_TO_HIDE -> {
                return Integer.toString(time_to_hide);
            }
            case TIME_TO_SEARCH -> {
                return Integer.toString(time_to_search);
            }
            case START_POS -> {
                return startPos;
            }
            default -> {
                return null;
            }
        }
    }

    public static String getSerializedLocation(Location loc) { //Converts location -> String
        return loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getWorld().getUID();
        //feel free to use something to split them other than semicolons (Don't use periods or numbers)
    }

    public static Location getDeserializedLocation(String pos) {//Converts String -> Location
        String [] parts = pos.split(";"); //If you changed the semicolon you must change it here too
        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        double z = Double.parseDouble(parts[2]);
        UUID u = UUID.fromString(parts[3]);
        World w = Bukkit.getServer().getWorld(u);
        return new Location(w, x, y, z); //can return null if the world no longer exists
    }

    public String getName() {
        return name;
    }

    public int getTime_to_hide() {
        return time_to_hide;
    }

    public void setTime_to_hide(int time_to_hide) {
        this.time_to_hide = time_to_hide;
    }

    public int getTime_to_search() {
        return time_to_search;
    }

    public void setTime_to_search(int time_to_search) {
        this.time_to_search = time_to_search;
    }

    public String getStartPos() {
        return startPos;
    }

    public void setStartPos(String startPos) {
        this.startPos = startPos;
    }

    public void setStartPos(Location startPos) {
        this.startPos = getSerializedLocation(startPos);
    }

    public void save(){
        SettingsHandler.saveToFile(this);
    }

    public void setStruct(String name, String structPos) {
        structdatas.add(new StructData(name, structPos));
    }

    public ArrayList<StructData> getStructPos() {
        return structdatas;
    }
}
