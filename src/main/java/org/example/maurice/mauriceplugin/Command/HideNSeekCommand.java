package org.example.maurice.mauriceplugin.Command;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.example.maurice.mauriceplugin.HideNSeek;
import org.example.maurice.mauriceplugin.MsgSender;
import org.example.maurice.mauriceplugin.Utils.CustomColor;
import org.example.maurice.mauriceplugin.Utils.PlayerType;
import org.example.maurice.mauriceplugin.Utils.SettingsEnum;
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
                if (args[0].equals("create")){
                    if (game != null && !game.isGameStopped()){
                        MsgSender.send(sender, text("Une partie est deja en cours, elle doit etre annulee pour en creer une nouvelle").color(CustomColor.LIGHT_RED.getVal()));
                    }
                    else {
                        game = new HideNSeek(sender);
                    }
                    return true;
                }
                if (args[0].equals("settings")){
                    if (args.length == 4 && args[1].equals("set")){
                        try {
                            SettingsEnum toUpdate = Objects.requireNonNull(SettingsEnum.getFromName(args[2].toLowerCase()));
                            toUpdate.setValue(Integer.parseInt(args[3]));
                            MsgSender.send(sender, text(toUpdate.toString(), WHITE).append(text(" updated to ", GREEN)).append(text(args[3]).color(WHITE)));
                        }
                        catch (NullPointerException ex){
                            MsgSender.send(sender, getSettingsValues());
                        }
                    }
                    else if (args.length == 3 && args[1].equals("get")){
                        MsgSender.send(sender, text(Objects.requireNonNull(SettingsEnum.getFromName(args[2].toLowerCase())).getValue()));
                    }
                    else{
                        MsgSender.send(sender, getSettingsUsage());
                    }
                    return true;
                }
                if (game == null){
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
                    game.startGame();
                    return true;
                }
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
