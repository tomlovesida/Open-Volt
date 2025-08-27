package com.volt.module.setting;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("all")
public class ModeSetting extends Setting {
    private final String mode;
    private final List<String> modes;
    public int index;

    public ModeSetting(String name, String defaultMode, String... modes) {
        super(name);
        this.mode = defaultMode;
        this.modes = Arrays.asList(modes);
        this.index = this.modes.indexOf(defaultMode);
        if (this.index == -1) {
            this.index = 0;
        }
    }

    public String getMode() {
        if (index < 0 || index >= modes.size()) {
            index = 0;
        }
        return modes.get(index);
    }

    public void setMode(String mode) {
        int newIndex = modes.indexOf(mode);
        if (newIndex != -1) {
            index = newIndex;
        }
    }

    public void cycle() {
        if (index < modes.size() - 1) {
            index++;
        } else {
            index = 0;
        }
    }

    public boolean isMode(String mode) {
        return index == modes.indexOf(mode);
    }

    public List<String> getModes() {
        return modes;
    }
}
