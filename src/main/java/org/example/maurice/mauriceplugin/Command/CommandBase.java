package org.example.maurice.mauriceplugin.Command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import me.lucko.commodore.file.CommodoreFileReader;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.SimplePluginManager;
import org.example.maurice.mauriceplugin.MauricePlugin;
import org.example.maurice.mauriceplugin.MsgSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public abstract class CommandBase extends BukkitCommand implements CommandExecutor {
    private final int minArgument;
    private final int maxArgument;
    private final boolean playerOnly;

    public CommandBase(String command) {
        this(command, 0);
    }

    public CommandBase(String command, boolean playerOnly) {
        this(command, 0, playerOnly);
    }

    public CommandBase(String command, int requiredArguments) {
        this(command, requiredArguments, requiredArguments);
    }

    public CommandBase(String command, int minArgument, int maxArgument){
        this(command, minArgument, maxArgument, false, "", "", new ArrayList<String>());
    }

    public CommandBase(String command, int requiredArguments, boolean playerOnly) {
        this(command, requiredArguments, requiredArguments, playerOnly, "", "", new ArrayList<String>());
    }

    public CommandBase(String command, int minArgument, int maxArgument, boolean playerOnly, String desc, String usage, List<String> alias) {
        super(command, desc, usage, alias);

        this.minArgument = minArgument;
        this.maxArgument = maxArgument;
        this.playerOnly = playerOnly;

        CommandMap commandMap = getCommandMap();
        if(commandMap != null){
            commandMap.register(command, this);
        }

        // get a commodore instance
        Commodore commodore = CommodoreProvider.getCommodore(MauricePlugin.getInstance());

        LiteralCommandNode<?> timeCommand = null;
        try {
            timeCommand = CommodoreFileReader.INSTANCE.parse(MauricePlugin.getInstance().getResource("hidenseek.commodore"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        commodore.register(this, timeCommand);
    }

    public CommandMap getCommandMap() {
        try {
            if (true) {
                Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);

                return (CommandMap) field.get(Bukkit.getPluginManager());
            }
        }
        catch (NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
        }

        return null;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return this.onCommand(sender, args);
    }

    public abstract boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args);

    public abstract String getUsage();

    private void sendHelp(CommandSender sender) {
        MsgSender.send(sender, getUsage());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        Logger.getLogger("test").info(Arrays.toString(args));
        if (args.length < minArgument || (args.length > maxArgument && maxArgument != -1)) {
            sendHelp(sender);
            return true;
        }

        if (playerOnly && !(sender instanceof Player)){
            MsgSender.send(sender, "T'es pas un player");
            return true;
        }

        String permission = getPermission();
        if (permission != null && !sender.hasPermission(permission)){
            MsgSender.send(sender, "T'as pas les permissions");
            return true;
        }

        if (!onCommand(sender, args)){
            sendHelp(sender);
        }

        return false;
    }
}
