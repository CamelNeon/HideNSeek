package org.example.maurice.mauriceplugin.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class SettingsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    String name ;
    int time_to_hide;
    int time_to_search;
    String startPos;

    public SettingsDTO(String name, int time_to_hide, int time_to_search, Location startPos){
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

    public String getSerializedLocation(Location loc) { //Converts location -> String
        return loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getWorld().getUID();
        //feel free to use something to split them other than semicolons (Don't use periods or numbers)
    }

    public Location getDeserializedLocation() {//Converts String -> Location
        String [] parts = startPos.split(";"); //If you changed the semicolon you must change it here too
        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        double z = Double.parseDouble(parts[2]);
        UUID u = UUID.fromString(parts[3]);
        World w = Bukkit.getServer().getWorld(u);
        return new Location(w, x, y, z); //can return null if the world no longer exists
    }
}
