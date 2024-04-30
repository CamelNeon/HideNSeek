package org.example.maurice.mauriceplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.example.maurice.mauriceplugin.Listener.HitListener;
import org.example.maurice.mauriceplugin.Utils.*;

import java.util.*;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class HideNSeek {

    private final Map<CommandSender, Boolean> players = new HashMap<>();
    private final Map<CommandSender, ScoreboardHandler> scoreboards = new HashMap<>();
    private final ArrayList<Player> foundPlayer = new ArrayList<>();
    private int numberOfHider = 0;
    private final Random rnd = new Random();
    private boolean gameStarted = false;
    private boolean gameStopped = false;
    private BukkitTask task;
    private int countdownValue;

    public HideNSeek(CommandSender sender){
        Component component = text().content(sender.getName()).color(CustomColor.STRONG_GREEN.getVal())
                .append(text(" a lance une partie de cache-cache, voulez-vous participer ?"))
                .build();
        Component componentOui = text(" [Oui] ", GREEN).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/hidenseek play"));
        Component componentNon = text(" [Non] ", RED).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/hidenseek refuse"));
        Component componentFinal = component.append(componentOui).append(componentNon);
        MsgSender.sendAll(componentFinal);
    }

    public void addPlayer(CommandSender player, boolean playing){
        if (gameStarted){
            MsgSender.send(player, Component.text("Impossible de rejoindre ou quitter une partie en cours :("));
        }
        else {
            players.put(player, playing);
            if (playing) MsgSender.send(player, text("Vous avez accepte la partie", GREEN));
            else MsgSender.send(player, text("Vous avez refuse la partie", RED));
        }
    }

    public ArrayList<CommandSender> listPlayer(){
        ArrayList<CommandSender> acceptingPlayers = new ArrayList<>();
        for (Map.Entry<CommandSender, Boolean> cs : players.entrySet()){
            if (cs.getValue()) acceptingPlayers.add(cs.getKey());
        }
        return acceptingPlayers;
    }

    public void startGame(){
        CommandSender seeker = listPlayer().get(rnd.nextInt(players.size()));
        MsgSender.send(seeker, Component.text("A toi de trouver les autres!"));
        Player seekerPlayer = Bukkit.getPlayer(seeker.getName());
        String seekerID = Objects.requireNonNull(Bukkit.getPlayer(seeker.getName())).getUniqueId().toString();

        if (seekerPlayer == null){
            MsgSender.batchSend(listPlayer(), text("Une erreur inattendue est survenue, le chercheur est null :(").color(CustomColor.STRONG_RED.getVal()));
            return;
        }

        seekerPlayer.setGameMode(GameMode.SURVIVAL);

        seekerPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, SettingsEnum.TIME_TO_HIDE.getValue(), 10, false, false, false));
        seekerPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, SettingsEnum.TIME_TO_HIDE.getValue(), 10, false, false, false));
        HideNSeek hs = this;

        Bukkit.getScheduler().scheduleSyncDelayedTask(MauricePlugin.getInstance(), () -> {
            HitListener.setSeekerId(seekerID);
            HitListener.setGame(hs);
            HitListener.setPLAYING(true);
        }, SettingsEnum.TIME_TO_HIDE.getValue());

        List<Player> hiders = players.keySet().stream().filter(cs -> !cs.equals(seeker)).map(cs -> Bukkit.getPlayer(cs.getName())).toList();
        for (Player h : hiders){
            Objects.requireNonNull(Bukkit.getPlayer(h.getName())).setGameMode(GameMode.SURVIVAL);
            MsgSender.send(h, Component.text("Ne laisse pas ").append(seeker.name().color(CustomColor.LIGHT_RED.getVal())).append(Component.text(" te trouver!")));
            numberOfHider++;
        }
        setScoreboards(seekerPlayer, hiders);
        gameStarted = true;
    }

    public void stopGame(PlayerType winner){
        HitListener.setPLAYING(false);
        HitListener.setSeekerId(null);
        HitListener.setGame(null);
        gameStopped = true;
        if (task != null){
            task.cancel();
        }
        if (winner.equals(PlayerType.HIDER)){
            MsgSender.batchSend(listPlayer(), Component.text("L'equipe qui se cache gagne!").color(CustomColor.LIGHT_RED.getVal()));
        }
        else if (winner.equals(PlayerType.SEEKER)){
            MsgSender.batchSend(listPlayer(), Component.text("L'equipe de recherche gagne!").color(CustomColor.LIGHT_RED.getVal()));
        }
        else if (winner.equals(PlayerType.NONE)){
            MsgSender.batchSend(listPlayer(), Component.text("La partie a ete annule").color(CustomColor.LIGHT_RED.getVal()));
        }

        for (ScoreboardHandler sb : scoreboards.values()){
            sb.delete();
        }

        Component component = text("Recommencer une partie ?").color(CustomColor.STRONG_GREEN.getVal());
        Component componentOui = text(" [Oui] ", GREEN).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/hidenseek create"));
        Component componentNon = text(" [Non] ", RED);
        Component componentFinal = component.append(componentOui).append(componentNon);
        MsgSender.batchSend(listPlayer(), componentFinal);
    }

    public void playerFound(Player found){
        found.setGameMode(GameMode.SPECTATOR);
        FireworkHandler.spawnRandomFirework(found.getLocation());
        foundPlayer.add(found);
        if (foundPlayer.size() == numberOfHider){
            stopGame(PlayerType.SEEKER);
        }
    }

    public boolean isGameStopped() {
        return gameStopped;
    }

    private void setScoreboards(Player seeker, List<Player> hiders){
        countdownValue = SettingsEnum.TIME_TO_HIDE.getValue()/20;
        scoreboards.put(seeker, new ScoreboardHandler(seeker, PlayerType.SEEKER));
        for (Player h : hiders){
            scoreboards.put(h, new ScoreboardHandler(h, PlayerType.HIDER));
        }
        hideCountdownUpdate();
    }

    private void hideCountdownUpdate(){
        for (ScoreboardHandler sb : scoreboards.values()) {
            sb.setCountdownValue(countdownValue);
        }
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (ScoreboardHandler sb : scoreboards.values()) {
                    sb.updateCountdown(6);
                }
                if (countdownValue == 0){
                    task.cancel();
                    searchStart();
                }
                countdownValue--;
            }
        }.runTaskTimer(MauricePlugin.getInstance(), 0, 20);
    }

    private void searchCountdownUpdate(){
        for (ScoreboardHandler sb : scoreboards.values()) {
            sb.setCountdownValue(countdownValue);
        }
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (ScoreboardHandler sb : scoreboards.values()) {
                    sb.updateCountdown(5);
                }
                if (countdownValue == 0){
                    stopGame(PlayerType.HIDER);
                    task.cancel();
                }
                countdownValue--;
            }
        }.runTaskTimer(MauricePlugin.getInstance(), 0, 20);
    }

    private void searchStart(){
        countdownValue = SettingsEnum.TIME_TO_SEARCH.getValue()/20;
        for (Map.Entry<CommandSender, ScoreboardHandler> sb : scoreboards.entrySet()) {
            sb.getValue().updateLine(4, text("Il reste", RED));
            if (sb.getValue().getType().equals(PlayerType.SEEKER)) {
                sb.getValue().updateLine(6, text("Pour les trouver", RED));
            }
            else if (sb.getValue().getType().equals(PlayerType.HIDER)) {
                sb.getValue().updateLine(6, text("a se cacher", RED));
            }
        }
        searchCountdownUpdate();
    }
}
