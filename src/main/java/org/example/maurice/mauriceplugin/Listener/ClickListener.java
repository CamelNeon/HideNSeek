package org.example.maurice.mauriceplugin.Listener;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.example.maurice.mauriceplugin.HideNSeek;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.text;

public class ClickListener implements Listener {

    private static final List<Sound> tauntSound = List.of(Sound.ENTITY_ENDER_DRAGON_DEATH, Sound.ENTITY_ENDER_DRAGON_GROWL, Sound.ENTITY_ENDER_DRAGON_FLAP,
                                                    Sound.AMBIENT_CAVE, Sound.ENTITY_FOX_SCREECH, Sound.ENTITY_GOAT_SCREAMING_PREPARE_RAM, Sound.ENTITY_GOAT_SCREAMING_LONG_JUMP,
                                                    Sound.ENTITY_GHAST_DEATH, Sound.ENTITY_PANDA_DEATH, Sound.ENTITY_ENDERMAN_DEATH);
    private static boolean PLAYING = false;
    private static List<String> HIDERS_ID;
    private static HideNSeek GAME;
    private static final Random rnd = new Random();

    @EventHandler
    public void rightClicking(PlayerInteractEvent event){
        Logger.getLogger("Sound").info("Long before playing sound");
        if (!PLAYING || GAME == null || HIDERS_ID == null){return;}
        Player p = event.getPlayer();
        Logger.getLogger("Sound").info(String.format("before playing sound %s", p.getInventory().getItemInMainHand().lore().toString()));
        if (HIDERS_ID.contains(p.getUniqueId().toString()) && !(p.getInventory().getItemInMainHand().lore() == null) && text("Use this item to taunt!").equals(p.getInventory().getItemInMainHand().lore().get(0))){
            Logger.getLogger("Sound").info("playing sound");
            p.getWorld().playSound(p.getLocation(), getRandomSound(), 3, 1);
        }
    }

    public Sound getRandomSound(){
        return tauntSound.get(rnd.nextInt(tauntSound.size()));
    }

    public static void setPLAYING(boolean p){
        PLAYING = p;
    }

    public static void setHidersId(List<String> hidersID) {
        HIDERS_ID = hidersID;
    }

    public static void setGAME(HideNSeek game) {
        GAME = game;
    }
}
