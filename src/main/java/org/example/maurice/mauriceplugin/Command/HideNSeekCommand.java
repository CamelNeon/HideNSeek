package org.example.maurice.mauriceplugin.Command;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.example.maurice.mauriceplugin.HideNSeek;
import org.example.maurice.mauriceplugin.MsgSender;
import org.example.maurice.mauriceplugin.Utils.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class HideNSeekCommand {
    public HideNSeekCommand(){
        new CommandBase("hidenseek",1, 4, true) {
            HideNSeek game;
            @Override
            public boolean onCommand(@NotNull CommandSender sender, String[] args) {
                if (args[0].equals("setup")){
                    if (args.length >= 4){
                        SettingsDTO settings = new SettingsDTO(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), Objects.requireNonNull(Bukkit.getPlayer(sender.getName())).getLocation());
                        SettingsHandler.saveToFile(settings);
                    }
                    return true;
                }
                if (args[0].equals("load")){
                    if (args.length == 2) {
                        SettingsHandler.readFromFile(args[1]);
                        MsgSender.send(sender, text(String.format("Configuration chargee pour %s", args[1]), GREEN));
                    }
                    else {
                        MsgSender.send(sender, getUsage());
                    }
                    return true;
                }
                if (args[0].equals("info")){
                    if (SettingsHandler.areSettingsLoaded()) {
                        MsgSender.send(sender, text("Configuration ", GREEN).append(text(SettingsHandler.getCurrentName(), WHITE)).append(text(" chargee \n", GREEN))
                                .append(text("Time to hide : ", GREEN)).append(text(SettingsHandler.getTimeToHide(), WHITE)).append(text("\n"))
                                .append(text("Time to search : ", GREEN)).append(text(SettingsHandler.getTimeToSearch(), WHITE)).append(text("\n"))
                                .append(text("Starting position : ", GREEN)).append(text(SettingsHandler.getStartPosString(), WHITE)));
                    }
                    else {
                        MsgSender.send(sender, text("Aucune configuration chargee", RED));
                    }
                    return true;
                }
                if (args[0].equals("create")){
                    if (game != null && !game.isGameStopped()){
                        MsgSender.send(sender, text("Une partie est deja en cours, elle doit etre annulee pour en creer une nouvelle").color(CustomColor.LIGHT_RED.getVal()));
                    }
                    else {
                        if (SettingsHandler.areSettingsLoaded()) {
                            game = new HideNSeek(sender);
                        }
                        else {
                            MsgSender.send(sender, text("Aucune configuration chargée", RED));
                        }
                    }
                    return true;
                }
                if (args[0].equals("settings")){
                    if (args.length >= 3 && args[1].equals("set")){
                        try {
                            SettingsEnum toUpdate = Objects.requireNonNull(SettingsEnum.getFromName(args[2].toLowerCase()));
                            switch (toUpdate) {
                                case TIME_TO_HIDE -> SettingsHandler.setTimeToHide(Integer.parseInt(args[3]));
                                case TIME_TO_SEARCH -> SettingsHandler.setTimeToSearch(Integer.parseInt(args[3]));
                                case START_POS -> SettingsHandler.setStartPos(Objects.requireNonNull(Bukkit.getPlayer(sender.getName())).getLocation());
                            }
                            if (args.length == 4){
                                MsgSender.send(sender, text(toUpdate.toString(), WHITE).append(text(" updated to ", GREEN)).append(text(args[3]).color(WHITE)));
                            }
                            if (args.length == 3){
                                MsgSender.send(sender, text(toUpdate.toString(), WHITE).append(text(" updated", GREEN)));
                            }
                        }
                        catch (NullPointerException ex){
                            MsgSender.send(sender, getSettingsValues());
                        }
                    }
                    else if (args.length == 3 && args[1].equals("get")){
                        SettingsEnum toGet = Objects.requireNonNull(SettingsEnum.getFromName(args[2].toLowerCase()));
                        MsgSender.send(sender, text(toGet.toString(), WHITE)
                                .append(text(" is set to ", GREEN))
                                .append(text(SettingsHandler.get(toGet), WHITE)));
                    }
                    else{
                        MsgSender.send(sender, getSettingsUsage());
                    }
                    return true;
                }
                if (game == null || game.isGameStopped()){
                    MsgSender.send(sender, text("Aucune partie en cours :(").color(CustomColor.LIGHT_RED.getVal()));
                    return true;
                }
                if (args[0].equals("cancel") || args[0].equals("stop")){
                    game.stopGame(PlayerType.NONE);
                    game = null;
                    return true;
                }
                if (args[0].equals("play")) {
                    game.addPlayer(sender, true);
                    return true;
                }
                if (args[0].equals("refuse")) {
                    game.addPlayer(sender, false);
                    return true;
                }
                if (args[0].equals("list")){
                    MsgSender.send(sender, text("Joueurs participants :", GREEN));
                    ArrayList<CommandSender> players = game.listPlayer();
                    for (CommandSender cs : players){
                        MsgSender.send(sender, cs.getName());
                    }
                    return true;
                }
                if (args[0].equals("start")){
                    if (game.listPlayer().size() < 2){
                        MsgSender.send(sender, text("Trop peu de joueur ont accepte la partie pour commencer", RED));
                    }
                    else {
                        game.startGame();
                    }
                    return true;
                }
                MsgSender.send(sender, getUsage());
                return true;
            }

            @Override
            public String getUsage(){
                return "/hidenseek";
            }

            public String getSettingsUsage(){
                return "/hidenseek settings";
            }

            public Component getSettingsValues(){
                return text("les parametres possibles sont : \n", GREEN).append(text(Arrays.toString(SettingsEnum.values()), WHITE));
            }
        };
    }
}
