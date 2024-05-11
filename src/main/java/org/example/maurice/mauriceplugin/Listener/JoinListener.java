package org.example.maurice.mauriceplugin.Listener;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.example.maurice.mauriceplugin.MauricePlugin;

public class JoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        MauricePlugin.addPlayer(event.getPlayer());
    }
}
