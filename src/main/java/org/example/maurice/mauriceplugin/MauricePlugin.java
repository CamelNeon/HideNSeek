package org.example.maurice.mauriceplugin;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.example.maurice.mauriceplugin.Command.HideNSeekCommand;
import org.example.maurice.mauriceplugin.Listener.HitListener;
import org.example.maurice.mauriceplugin.Listener.TestListener;

import static org.bukkit.Bukkit.getServer;

public final class MauricePlugin extends JavaPlugin {
    private static MauricePlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("On y est");
        getServer().getPluginManager().registerEvents(new HitListener(), this);
        new HideNSeekCommand();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static MauricePlugin getInstance(){
        return instance;
    }
}