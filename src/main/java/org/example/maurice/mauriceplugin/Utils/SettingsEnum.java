package org.example.maurice.mauriceplugin.Utils;

public enum SettingsEnum {

    TIME_TO_HIDE("time_to_hide"),
    TIME_TO_SEARCH("time_to_search"),
    START_POS("start_pos");

    private String name;

    SettingsEnum(String name){
        this.name = name;
    }

    public static SettingsEnum getFromName(String toFind){
        for (SettingsEnum se : values()){
            if (se.name.equals(toFind)) {
                return se;
            }
        }
        return null;
    }
}
