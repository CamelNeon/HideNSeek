package org.example.maurice.mauriceplugin.Wrapper;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CustomPlayer {
    Player player;
    Location first_pos;
    Location second_pos;

    public CustomPlayer(Player player){
        this.player = player;
    }

    public void setFirst_pos(Location first_pos) {
        this.first_pos = first_pos;
    }

    public void setSecond_pos(Location second_pos) {
        this.second_pos = second_pos;
    }

    public Location getFirst_pos() {
        return first_pos;
    }

    public Location getSecond_pos() {
        return second_pos;
    }

    public Player getPlayer() {
        return player;
    }
}
