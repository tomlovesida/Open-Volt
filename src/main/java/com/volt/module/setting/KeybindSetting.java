package com.volt.module.setting;

public final class KeybindSetting extends Setting {
    private final boolean moduleKey;
    private final int originalKey;
    private int keyCode;
    private boolean listening;

    public KeybindSetting(String name, int key, boolean moduleKey) {
        super(name);
        this.keyCode = key;
        this.originalKey = key;
        this.moduleKey = moduleKey;
    }

    public boolean isModuleKey() {
        return moduleKey;
    }

    public boolean isListening() {
        return listening;
    }

    public void setListening(boolean listening) {
        this.listening = listening;
    }

    public int getOriginalKey() {
        return originalKey;
    }

    public int getKey() {
        return keyCode;
    }

    public void setKey(int key) {
        this.keyCode = key;
    }

    public void toggleListening() {
        this.listening = !listening;
    }
}