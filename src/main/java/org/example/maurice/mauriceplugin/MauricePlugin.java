package org.example.maurice.mauriceplugin;

import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import me.lucko.commodore.file.CommodoreFileReader;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.example.maurice.mauriceplugin.Command.HideNSeekCommand;
import org.example.maurice.mauriceplugin.Listener.ClickListener;
import org.example.maurice.mauriceplugin.Listener.HitListener;
import org.example.maurice.mauriceplugin.Listener.JoinListener;
import org.example.maurice.mauriceplugin.Listener.TestListener;
import org.example.maurice.mauriceplugin.Utils.SettingsHandler;
import org.example.maurice.mauriceplugin.Wrapper.CustomPlayer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
import static org.bukkit.Bukkit.getServer;

public final class MauricePlugin extends JavaPlugin {
    private static MauricePlugin instance;
    private static final HashMap<String, CustomPlayer> customPlayerList = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("On y est");

        getServer().getPluginManager().registerEvents(new HitListener(), this);
        getServer().getPluginManager().registerEvents(new ClickListener(), this);
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        SettingsHandler.getDataFile();
        new HideNSeekCommand();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static MauricePlugin getInstance(){
        return instance;
    }

    public static void addPlayer(Player player){
        customPlayerList.put(player.getUniqueId().toString(), new CustomPlayer(player));
    }

    public static CustomPlayer getCustomPlayer(String id){
        return customPlayerList.get(id);
    }

    public static void setFirstPos(UUID id, Location location){
        Objects.requireNonNull(Bukkit.getPlayer(id)).sendMessage(Component.text("first pos set to " + location.toString(), YELLOW));
        customPlayerList.get(id.toString()).setFirst_pos(location);
    }

    public static void setSecondPos(UUID id, Location location){
        Objects.requireNonNull(Bukkit.getPlayer(id)).sendMessage(Component.text("second pos set to " + location.toString(), YELLOW));
        customPlayerList.get(id.toString()).setSecond_pos(location);
    }

    public static boolean playerFirstLocIs(String id, Location loc){
        if (customPlayerList.get(id).getFirst_pos() == null) return false;
        return customPlayerList.get(id).getFirst_pos().equals(loc);
    }

    public static boolean playerSecondLocIs(String id, Location loc){
        if (customPlayerList.get(id).getSecond_pos() == null) return false;
        return customPlayerList.get(id).getSecond_pos().equals(loc);
    }
}
