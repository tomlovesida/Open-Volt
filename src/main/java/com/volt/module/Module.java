package com.volt.module;

import com.volt.IMinecraft;
import com.volt.Volt;
import com.volt.module.setting.KeybindSetting;
import com.volt.module.setting.Setting;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Setter
@Getter
public abstract class Module implements IMinecraft {

    protected static MinecraftClient mc = MinecraftClient.getInstance();
    private final List<Setting> settings = new ArrayList<>();
    private String name;

    private String suffix;

    private String description;

    private boolean enabled;

    private int key;

    private Category moduleCategory;
    private boolean registered;

    public Module(String name, String description, int key, Category moduleCategory) {
        this.name = name;
        this.description = description;
        enabled = false;
        this.key = key;
        this.moduleCategory = moduleCategory;

        KeybindSetting keybindSetting = new KeybindSetting("Keybind ", key, true);
        addSetting(keybindSetting);
    }

    public Module(String name, String description, Category moduleCategory) {
        this.name = name;
        this.description = description;
        enabled = false;
        this.key = -1;
        this.moduleCategory = moduleCategory;

        KeybindSetting keybindSetting = new KeybindSetting("Keybind ", key, true);
        addSetting(keybindSetting);
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void onEnable() {

    }

    public void onDisable() {

    }

    public boolean isNull() {
        return mc.player == null || mc.world == null;
    }

    public void addSetting(Setting setting) {
        this.settings.add(setting);
    }

    public void addSettings(Setting... settings) {
        this.settings.addAll(Arrays.asList(settings));
    }

    public KeybindSetting getKeybindSetting() {
        if (isNull()) return null;
        
        for (Setting setting : settings) {
            if (setting instanceof KeybindSetting keybindSetting && keybindSetting.isModuleKey()) {
                return keybindSetting;
            }
        }
        return null;
    }

    public int getKey() {
        if (isNull()) return key;
        
        KeybindSetting keybindSetting = getKeybindSetting();
        if (keybindSetting != null) {
            return keybindSetting.getKey();
        }
        return key;
    }

    public void setKey(int key) {
        if (isNull()) return;
        
        this.key = key;
        KeybindSetting keybindSetting = getKeybindSetting();
        if (keybindSetting != null) {
            keybindSetting.setKey(key);
        }
    }

    public final void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;

            if (enabled) {
                onEnable();
                if (this.enabled) {
                    Volt.INSTANCE.getVoltEventBus().subscribe(this);
                    registered = true;
                }
            } else {
                if (registered) {
                    Volt.INSTANCE.getVoltEventBus().unsubscribe(this);
                    registered = false;
                }
                onDisable();
            }
        }
    }
}
