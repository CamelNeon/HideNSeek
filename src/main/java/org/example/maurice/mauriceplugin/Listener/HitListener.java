package org.example.maurice.mauriceplugin.Listener;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.example.maurice.mauriceplugin.HideNSeek;

public class HitListener implements Listener{
    private static boolean PLAYING = false;
    private static String SEEKER_ID;
    private static HideNSeek GAME;

    @EventHandler
    public void entityDamage(EntityDamageByEntityEvent event){
        if (event.getDamager() instanceof Firework fw) {
            if (fw.hasMetadata("nodamage")) {
                event.setCancelled(true);
            }
        }
        if (!PLAYING || GAME == null || SEEKER_ID == null){return;}
        if (event.getDamager() instanceof Player && ((Player) event.getDamager()).identity().uuid().toString().equals(SEEKER_ID)){
            if (event.getEntity() instanceof Player){
                GAME.playerFound((Player)event.getEntity());
            }
        }
    }

    public static void setPLAYING(boolean p){
        PLAYING = p;
    }

    public static void setSeekerId(String seekerId) {
        SEEKER_ID = seekerId;
    }

    public static void setGAME(HideNSeek game) {
        GAME = game;
    }
}
