package org.example.maurice.mauriceplugin.Utils;

import net.kyori.adventure.text.format.TextColor;

public enum CustomColor {
    LIGHT_RED(TextColor.color(180, 82, 82)),
    STRONG_RED(TextColor.color(255, 0, 0)),
    STRONG_GREEN(TextColor.color(0, 204, 0));

    private final TextColor color;

    CustomColor(TextColor color) {
        this.color = color;
    }

    public TextColor getVal(){
        return color;
    }
}
