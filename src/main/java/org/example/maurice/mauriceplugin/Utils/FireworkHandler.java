package org.example.maurice.mauriceplugin.Utils;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.example.maurice.mauriceplugin.MauricePlugin;

import java.util.Random;

public class FireworkHandler {

    public static void spawnRandomFirework(final Location loc) {
        final Firework firework = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        final FireworkMeta fireworkMeta = firework.getFireworkMeta();
        final Random random = new Random();
        final FireworkEffect effect = FireworkEffect.builder().flicker(random.nextBoolean()).withColor(getColor(random.nextInt(17))).withFade(getColor(random.nextInt(17))).with(FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)]).trail(random.nextBoolean()).build();
        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(0);
        firework.setMetadata("nodamage", new FixedMetadataValue(MauricePlugin.getInstance(), true));
        firework.setFireworkMeta(fireworkMeta);
        firework.detonate();
    }

    private static Color getColor(final int i) {
        return switch (i) {
            case 1 -> Color.AQUA;
            case 2 -> Color.BLACK;
            case 3 -> Color.BLUE;
            case 4 -> Color.FUCHSIA;
            case 5 -> Color.GRAY;
            case 6 -> Color.GREEN;
            case 7 -> Color.LIME;
            case 8 -> Color.MAROON;
            case 9 -> Color.NAVY;
            case 10 -> Color.OLIVE;
            case 11 -> Color.ORANGE;
            case 12 -> Color.PURPLE;
            case 13 -> Color.RED;
            case 14 -> Color.SILVER;
            case 15 -> Color.TEAL;
            case 16 -> Color.WHITE;
            case 0 -> Color.YELLOW;
            default -> null;
        };
    }
}
