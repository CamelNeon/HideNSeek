package org.example.maurice.mauriceplugin.Utils;

public enum SettingsEnum {

    TIME_TO_HIDE("time_to_hide", 60*20),
    TIME_TO_SEARCH("time_to_search", 300*20);

    private String name;
    private int value;

    SettingsEnum(String name, int value){
        this.name = name;
        this.value = value;
    }

    public static SettingsEnum getFromName(String toFind){
        for (SettingsEnum se : values()){
            if (se.name.equals(toFind)) {
                return se;
            }
        }
        return null;
    }
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
