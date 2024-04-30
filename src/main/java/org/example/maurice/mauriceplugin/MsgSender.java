package org.example.maurice.mauriceplugin;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class MsgSender {

    public static void send(CommandSender sender, String message) {
        send(sender , message, "&a");
    }

    public static void send(CommandSender sender, String message, String prefix) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    }

    public static void send(CommandSender sender, Component message) {
        sender.sendMessage(message);
    }

    public static void batchSend(ArrayList<CommandSender> senders, Component message){
        for (CommandSender s : senders){
            s.sendMessage(message);
        }
    }

    public static void sendAll(Component message){
        Bukkit.broadcast(message);
    }
}
