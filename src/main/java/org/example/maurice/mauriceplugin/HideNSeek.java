package org.example.maurice.mauriceplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.example.maurice.mauriceplugin.Listener.ClickListener;
import org.example.maurice.mauriceplugin.Listener.HitListener;
import org.example.maurice.mauriceplugin.Utils.*;

import java.util.*;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class HideNSeek {

    private final CommandSender creator;
    private final Map<CommandSender, Boolean> players = new HashMap<>();
    private final Map<CommandSender, ScoreboardHandler> scoreboards = new HashMap<>();
    private final ArrayList<Player> foundPlayer = new ArrayList<>();
    private int numberOfHider = 0;
    private final Random rnd = new Random();
    private boolean gameStarted = false;
    private boolean gameStopped = false;
    private BukkitTask task;
    private int countdownValue;
    private Settings settings = null;

    public HideNSeek(CommandSender sender, String configName){
        creator = sender;
        players.put(sender, true);
        settings = SettingsHandler.readFromFile(configName);
        Component component = text().content(sender.getName()).color(CustomColor.STRONG_GREEN.getVal())
                .append(text(" a lance une partie de cache-cache, voulez-vous participer ?"))
                .build();
        Component componentOui = text(" [Oui] ", GREEN).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/hidenseek play "
                + Objects.requireNonNull(Bukkit.getPlayer(creator.getName())).getUniqueId()));
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

        for (CommandSender p : listPlayer()){
            Objects.requireNonNull(Bukkit.getPlayer(p.getName())).getInventory().clear();
        }

        if (seekerPlayer == null){
            MsgSender.batchSend(listPlayer(), text("Une erreur inattendue est survenue, le chercheur est null :(").color(CustomColor.STRONG_RED.getVal()));
            return;
        }

        seekerPlayer.setGameMode(GameMode.SURVIVAL);

        seekerPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, settings.getTime_to_hide(), 10, false, false, false));
        seekerPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, settings.getTime_to_hide(), 10, false, false, false));
        seekerPlayer.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, settings.getTime_to_hide(), 128, false, false, false));
        HideNSeek hs = this;

        List<Player> hiders = players.keySet().stream().filter(cs -> !cs.equals(seeker)).map(cs -> Bukkit.getPlayer(cs.getName())).toList();
        List<String> hidersID = hiders.stream().map(p -> p.getUniqueId().toString()).toList();

        Bukkit.getScheduler().scheduleSyncDelayedTask(MauricePlugin.getInstance(), () -> {
            HitListener.setSeekerId(seekerID);
            HitListener.setGAME(hs);
            HitListener.setPLAYING(true);
            ClickListener.setPLAYING(true);
            ClickListener.setHidersId(hidersID);
            ClickListener.setGAME(hs);
        }, settings.getTime_to_hide());

        seekerPlayer.teleport(settings.getDeserializedLocation());
        for (Player h : hiders){
            h.teleport(settings.getDeserializedLocation());
            Objects.requireNonNull(Bukkit.getPlayer(h.getName())).setGameMode(GameMode.SURVIVAL);
            ItemStack tauntItem = new ItemStack(Material.SLIME_BALL, 1);
            tauntItem.lore(List.of(text("Use this item to taunt!")));
            h.getInventory().addItem(tauntItem);
            MsgSender.send(h, Component.text("Ne laisse pas ").append(seeker.name().color(CustomColor.LIGHT_RED.getVal())).append(Component.text(" te trouver!")));
            numberOfHider++;
        }
        setScoreboards(seekerPlayer, hiders);
        gameStarted = true;
    }

    public void stopGame(PlayerType winner){
        HitListener.setPLAYING(false);
        HitListener.setSeekerId(null);
        HitListener.setGAME(null);
        ClickListener.setPLAYING(false);
        ClickListener.setHidersId(null);
        ClickListener.setGAME(null);
        gameStopped = true;
        for (CommandSender p : listPlayer()){
            Objects.requireNonNull(Bukkit.getPlayer(p.getName())).getInventory().clear();
        }
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
        Component componentOui = text(" [Oui] ", GREEN).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/hidenseek create " + settings.getName()));
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
        countdownValue = settings.getTime_to_hide()/20;
        scoreboards.put(seeker, new ScoreboardHandler(seeker, PlayerType.SEEKER, settings.getTime_to_hide()));
        for (Player h : hiders){
            scoreboards.put(h, new ScoreboardHandler(h, PlayerType.HIDER, settings.getTime_to_hide()));
        }
        hideCountdownUpdate();
    }

    private void hideCountdownUpdate(){
        for (ScoreboardHandler sb : scoreboards.values()) {
            sb.setCountdownValue(countdownValue);
        }
        HideNSeek game = this;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (ScoreboardHandler sb : scoreboards.values()) {
                    sb.updateCountdown(6);
                }
                if (countdownValue == 0){
                    task.cancel();
                    searchStart();
                    Objects.requireNonNull(Bukkit.getPlayer(creator.getName())).showTitle(Title.title(text("Go", GREEN),  text("")));
                    for (CommandSender sender : game.listPlayer()){
                        Objects.requireNonNull(Bukkit.getPlayer(sender.getName())).showTitle(Title.title(text("Go", GREEN),  text("")));
                    }
                }
                if (countdownValue <= 5){
                    Objects.requireNonNull(Bukkit.getPlayer(creator.getName())).showTitle(Title.title(text(countdownValue, RED),  text("")));
                    for (CommandSender sender : game.listPlayer()){
                        Objects.requireNonNull(Bukkit.getPlayer(sender.getName())).showTitle(Title.title(text(countdownValue, RED),  text("")));
                    }
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
        countdownValue = settings.getTime_to_search()/20;
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

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public void setTimeToHide(int time){
        settings.setTime_to_hide(time);
    }

    public void setTimeToSearch(int time){
        settings.setTime_to_search(time);
    }

    public void setStartPos(Location loc){
        settings.setStartPos(loc);
    }

    public String get(SettingsEnum se){
        return settings.get(se);
    }

    public int getTimeToHide(){
        return settings.getTime_to_hide();
    }
    public int getTimeToSearch(){
        return settings.getTime_to_search();
    }
    public String getStartPos(){
        return settings.getStartPos();
    }
    public String getName(){
        return settings.getName();
    }
    public void save(){
        settings.save();
    }
    public boolean isCreator(CommandSender sender){
        return creator.equals(sender);
    }

    public CommandSender getCreator() {
        return creator;
    }
}
