package org.example.maurice.mauriceplugin.Utils;

import org.bukkit.entity.Player;

import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class ScoreboardHandler extends FastBoard{
    private int countdownValue;
    private final PlayerType type;

    public ScoreboardHandler(Player player, PlayerType type) {
        super(player);
        this.type = type;

        // Set the title
        updateTitle(text("Hide 'N Seek", GOLD));

        // Change the lines
        updateLines(
                text(""),
                text("Vous etes", GREEN),
                text(type.toString(), GREEN),
                text(""),
                text("La recherche", RED),
                text("commence dans", RED),
                text(SettingsEnum.TIME_TO_HIDE.getValue() / 20).append(text(" sec")).color(RED)
        );
    }

    public void setCountdownValue(int countdownValue) {
        this.countdownValue = countdownValue;
    }

    public void updateCountdown(int lineNumber){
        updateLine(lineNumber, text(countdownValue--).append(text(" sec")).color(RED));
    }

    public PlayerType getType() {
        return type;
    }
}