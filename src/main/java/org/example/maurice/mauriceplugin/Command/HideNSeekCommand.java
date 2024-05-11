package org.example.maurice.mauriceplugin.Command;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.example.maurice.mauriceplugin.HideNSeek;
import org.example.maurice.mauriceplugin.MsgSender;
import org.example.maurice.mauriceplugin.Utils.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class HideNSeekCommand {
    public HideNSeekCommand(){
        List<String> alias = Arrays.stream(new String[]{"hns"}).toList();
        HashMap<String, ArrayList<String>> structMap = CommandStructParser.readFile("hidenseek_struct");
        new CommandBase("hidenseek",1, 4, true, "Description", "usage", alias) {
            private HashMap<String, HideNSeek> games = new HashMap<>();
            @Override
            public boolean onCommand(@NotNull CommandSender sender, String[] args) {
                String senderID = Objects.requireNonNull(Bukkit.getPlayer(sender.getName())).getUniqueId().toString();
                if (args[0].equals("setup")){
                    if (args.length >= 4){
                        Settings settings = new Settings(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), Objects.requireNonNull(Bukkit.getPlayer(sender.getName())).getLocation());
                        SettingsHandler.saveToFile(settings);
                    }
                    return true;
                }

                if (args[0].equals("tools")){
                    ItemStack selectAxe = new ItemStack(Material.GOLDEN_AXE);
                    selectAxe.lore(List.of(text("Use this to select game area")));
                    Objects.requireNonNull(Bukkit.getPlayer(sender.getName())).getInventory().addItem(selectAxe);
                    return true;
                }

                HideNSeek game = games.get(senderID);

                if (args[0].equals("play") && args.length == 2) {
                    if (game != null){
                        if (game.getCreator().equals(sender)){
                            MsgSender.send(sender, "En tant que createur de la partie vous y participez deja");
                        }
                        else {
                            MsgSender.send(sender, text("Quittez votre partie actuelle pour en accepter une autre", RED));
                        }
                    }
                    else if (games.get(args[1]) != null) {
                        game = games.get(Objects.requireNonNull(Bukkit.getPlayer(args[1])).getUniqueId().toString());
                        game.addPlayer(sender, true);
                        games.put(senderID, game);
                    }
                    else {
                        MsgSender.send(sender, text("Cette partie n'existe plus", RED));
                    }
                    return true;
                }

                if (args[0].equals("create") && args.length == 2){
                    if (game != null && !game.isGameStopped()){
                        MsgSender.send(sender, text("Une partie est deja en cours, elle doit etre annulee pour en creer une nouvelle").color(CustomColor.LIGHT_RED.getVal()));
                    }
                    else {
                        games.put(senderID, new HideNSeek(sender, args[1]));
                    }
                    return true;
                }

                if (game == null || game.isGameStopped()){
                    game = null;
                    games.put(senderID, null);
                    MsgSender.send(sender, text("Aucune partie en cours :(").color(CustomColor.LIGHT_RED.getVal()));
                    return true;
                }

                if (args[0].equals("info")){
                    MsgSender.send(sender, text("Configuration ", GREEN).append(text(game.getName(), WHITE)).append(text(" chargee \n", GREEN))
                            .append(text("Time to hide : ", GREEN)).append(text(game.getTimeToHide(), WHITE)).append(text("\n"))
                            .append(text("Time to search : ", GREEN)).append(text(game.getTimeToSearch(), WHITE)).append(text("\n"))
                            .append(text("Starting position : ", GREEN)).append(text(game.getStartPos(), WHITE)));
                    return true;
                }
                if (args[0].equals("load")){
                    if (game.isCreator(sender)) {
                        if (args.length == 2) {
                            Settings newSettings = SettingsHandler.readFromFile(args[1]);
                            game.setSettings(newSettings);
                            MsgSender.send(sender, text(String.format("Configuration chargee pour %s", args[1]), GREEN));
                        } else {
                            MsgSender.send(sender, getUsage());
                        }
                    }
                    else {
                        MsgSender.send(sender, text("Vous devez etre le createur de la partie pour utiliser cette commande", RED));
                    }
                    return true;
                }
                if (args[0].equals("save")){
                    if (game.isCreator(sender)) {
                        game.save();
                    }
                    else {
                        MsgSender.send(sender, text("Vous devez etre le createur de la partie pour utiliser cette commande", RED));
                    }
                    return true;
                }
                if (args[0].equals("settings")){
                    if (game.isCreator(sender)) {
                        if (args.length >= 3 && args[1].equals("set")) {
                            try {
                                SettingsEnum toUpdate = Objects.requireNonNull(SettingsEnum.getFromName(args[2].toLowerCase()));
                                switch (toUpdate) {
                                    case TIME_TO_HIDE -> game.setTimeToHide(Integer.parseInt(args[3]));
                                    case TIME_TO_SEARCH -> game.setTimeToSearch(Integer.parseInt(args[3]));
                                    case START_POS -> game.setStartPos(Objects.requireNonNull(Bukkit.getPlayer(sender.getName())).getLocation());
                                }
                                if (args.length == 4) {
                                    MsgSender.send(sender, text(toUpdate.toString(), WHITE).append(text(" updated to ", GREEN)).append(text(args[3]).color(WHITE)));
                                }
                                if (args.length == 3) {
                                    MsgSender.send(sender, text(toUpdate.toString(), WHITE).append(text(" updated", GREEN)));
                                }
                            } catch (NullPointerException ex) {
                                MsgSender.send(sender, getSettingsValues());
                            }
                        } else if (args.length == 3 && args[1].equals("get")) {
                            SettingsEnum toGet = Objects.requireNonNull(SettingsEnum.getFromName(args[2].toLowerCase()));
                            MsgSender.send(sender, text(toGet.toString(), WHITE)
                                    .append(text(" is set to ", GREEN))
                                    .append(text(game.get(toGet), WHITE)));
                        } else {
                            MsgSender.send(sender, getSettingsUsage());
                        }
                    }
                    else {
                        MsgSender.send(sender, text("Vous devez etre le createur de la partie pour utiliser cette commande", RED));
                    }
                    return true;
                }
                if (args[0].equals("cancel") || args[0].equals("stop")){
                    if (game.isCreator(sender)) {
                        game.stopGame(PlayerType.NONE);
                        games.remove(senderID);
                        for (Map.Entry<String, HideNSeek> e : games.entrySet()){
                            if (e.getValue().equals(game)){
                                games.remove(e.getKey());
                            }
                        }
                    } else {
                        MsgSender.send(sender, text("Vous devez etre le createur de la partie pour utiliser cette commande", RED));
                    }
                    return true;
                }
                if (args[0].equals("refuse") && args.length == 2) {
                    game = games.get(args[1]);
                    game.addPlayer(sender, false);
                    if (games.get(senderID).equals(game)){
                        games.remove(senderID);
                    }
                    return true;
                }
                if (args[0].equals("list")){
                    MsgSender.send(sender, text("Créateur :", GREEN));
                    MsgSender.send(sender, text(game.getCreator().getName()));
                    MsgSender.send(sender, text("Joueurs participants :", GREEN));
                    ArrayList<CommandSender> players = game.listPlayer();
                    for (CommandSender cs : players){
                        MsgSender.send(sender, text(cs.getName()));
                    }
                    return true;
                }
                if (args[0].equals("start")){
                    if (game.isCreator(sender)) {
                        if (game.listPlayer().size() < 2) {
                            MsgSender.send(sender, text("Trop peu de joueur ont accepte la partie pour commencer", RED));
                        } else {
                            game.startGame();
                        }
                    }
                    else {
                        MsgSender.send(sender, text("Vous devez etre le createur de la partie pour utiliser cette commande", RED));
                    }
                    return true;
                }
                MsgSender.send(sender, getUsage());
                return false;
            }

            @Override
            public String getUsage(){
                return "/hidenseek";
            }

            @Override
            public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
                StringBuilder prefix = new StringBuilder("hidenseek");
                if (args.length != 0){
                    for (int i = 0; i < args.length - 1; i++){
                        if (!structMap.containsKey(prefix + args[i])){
                            prefix.append("*");
                        }
                        else prefix.append(args[i]);
                    }
                }
                List<String> hints = structMap.get(prefix.toString());
                if (hints == null || hints.isEmpty()) return new ArrayList<String>();
                return hints.stream().filter(str -> str.startsWith(args[args.length-1])).toList();
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
