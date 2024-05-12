package org.example.maurice.mauriceplugin.Utils;

import org.bukkit.Location;

public class StructData {

    String name;
    String pos;

    public StructData(String name, String pos){
        this.name = name;
        this.pos = pos;
    }

    public String getName() {
        return name;
    }

    public String getPos() {
        return pos;
    }
}
