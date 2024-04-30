package org.example.maurice.mauriceplugin.Listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.logging.Logger;

public class TestListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Logger.getLogger("TestListener").info("Prout");
    }
}
