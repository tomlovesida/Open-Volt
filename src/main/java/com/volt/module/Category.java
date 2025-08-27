package com.volt.module;

public enum Category {
    COMBAT("Combat"), PLAYER("Player"), MOVEMENT("Movement"), RENDER("Render"), MISC("Misc"), CLIENT("Client");
    public final String name;

    Category(String name) {
        this.name = name;
    }
}
