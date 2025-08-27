package com.volt.profiles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.volt.Volt;
import com.volt.module.Module;
import com.volt.module.ModuleManager;
import com.volt.module.setting.*;
import com.volt.utils.mc.ChatUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public final class ProfileManager {

    private final ModuleManager moduleManager = Volt.INSTANCE.getModuleManager();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File profileDir = new File(System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming" + File.separator + ".minecraft" + File.separator + "Volt" + File.separator + "profiles");

    public ProfileManager() {
        createProfileDirectoryIfNeeded();
    }

    private void createProfileDirectoryIfNeeded() {
        if (!profileDir.exists()) {
            profileDir.mkdirs();
        }
    }

    public void loadProfile(String profileName) {
        File profileFile = new File(profileDir, profileName + ".json");

        if (!profileFile.exists()) {
            ChatUtils.addChatMessage("Profile not found: " + profileName);
            return;
        }

        resetProfile();
        readProfileFromFile(profileFile);
    }

    private void readProfileFromFile(File profileFile) {
        try (FileReader reader = new FileReader(profileFile)) {
            JsonObject json = gson.fromJson(reader, JsonObject.class);

            for (Module module : moduleManager.getModules()) {
                if (json.has(module.getName())) {
                    JsonObject moduleJson = json.getAsJsonObject(module.getName());
                    loadModuleSettings(module, moduleJson);
                }
            }

            ChatUtils.addChatMessage("Profile loaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadModuleSettings(Module module, JsonObject moduleJson) {
        module.setEnabled(moduleJson.get("enabled").getAsBoolean());
        module.setKey(moduleJson.get("bind").getAsInt());

        module.getSettings().forEach(setting -> loadSettingValue(setting, moduleJson));
    }

    private void loadSettingValue(Setting setting, JsonObject moduleJson) {
        String settingName = setting.getName();

        if (!moduleJson.has(settingName)) {
            return;
        }

        switch (setting) {
            case BooleanSetting booleanSetting -> booleanSetting.setValue(moduleJson.get(settingName).getAsBoolean());
            case NumberSetting numberSetting -> numberSetting.setValue(moduleJson.get(settingName).getAsDouble());
            case ModeSetting modeSetting -> modeSetting.setMode(moduleJson.get(settingName).getAsString());
            case KeybindSetting keybindSetting -> keybindSetting.setKey(moduleJson.get(settingName).getAsInt());
            case ColorSetting colorSetting -> {
                try {
                    if (moduleJson.get(settingName).isJsonPrimitive() && moduleJson.get(settingName).getAsJsonPrimitive().isString()) {
                        String hex = moduleJson.get(settingName).getAsString().trim();
                        if (hex.startsWith("#")) hex = hex.substring(1);
                        if (hex.length() == 6 || hex.length() == 8) {
                            int r = Integer.parseInt(hex.substring(0, 2), 16);
                            int g = Integer.parseInt(hex.substring(2, 4), 16);
                            int b = Integer.parseInt(hex.substring(4, 6), 16);
                            if (colorSetting.hasAlpha() && hex.length() == 8) {
                                int a = Integer.parseInt(hex.substring(6, 8), 16);
                                colorSetting.setValue(r, g, b, a);
                            } else {
                                colorSetting.setValue(r, g, b);
                            }
                        }
                    } else if (moduleJson.get(settingName).isJsonPrimitive() && moduleJson.get(settingName).getAsJsonPrimitive().isNumber()) {
                        int argb = moduleJson.get(settingName).getAsInt();
                        int a = (argb >> 24) & 0xFF;
                        int r = (argb >> 16) & 0xFF;
                        int g = (argb >> 8) & 0xFF;
                        int b = argb & 0xFF;
                        if (colorSetting.hasAlpha()) colorSetting.setValue(r, g, b, a); else colorSetting.setValue(r, g, b);
                    }
                } catch (Exception ignored) {
                }
            }
            default -> {
            }
        }
    }

    public void saveProfile(String profileName) {
        saveProfile(profileName, false);
    }
    
    public void saveProfile(String profileName, boolean forceOverride) {
        File profileFile = new File(profileDir, profileName + ".json");
        
        if (profileFile.exists() && !forceOverride) {
            ChatUtils.addChatMessage("§eProfile '" + profileName + "' already exists. Use .save <name> -override to overwrite it.");
            return;
        }

        try {
            if (profileFile.exists()) profileFile.delete();
            profileFile.createNewFile();
            if (forceOverride) {
                ChatUtils.addChatMessage("§aProfile '" + profileName + "' overridden successfully.");
            } else {
                ChatUtils.addChatMessage("§aProfile '" + profileName + "' saved successfully.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            ChatUtils.addChatMessage("§cFailed to save profile: " + e.getMessage());
            return;
        }

        writeProfileToFile(profileFile);
    }

    private void writeProfileToFile(File profileFile) {
        JsonObject json = new JsonObject();

        moduleManager.getModules().forEach(module -> {
            JsonObject moduleJson = new JsonObject();
            moduleJson.addProperty("enabled", module.isEnabled());
            moduleJson.addProperty("bind", module.getKey());

            module.getSettings().forEach(setting -> saveSettingValue(setting, moduleJson));
            json.add(module.getName(), moduleJson);
        });

        try (FileWriter writer = new FileWriter(profileFile)) {
            gson.toJson(json, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveSettingValue(Setting setting, JsonObject moduleJson) {
        String settingName = setting.getName();
        switch (setting) {
            case BooleanSetting booleanSetting -> moduleJson.addProperty(settingName, booleanSetting.getValue());
            case NumberSetting numberSetting -> moduleJson.addProperty(settingName, numberSetting.getValue());
            case ModeSetting modeSetting -> moduleJson.addProperty(settingName, modeSetting.getMode());
            case KeybindSetting keybindSetting -> moduleJson.addProperty(settingName, keybindSetting.getKey());
            case ColorSetting colorSetting -> {
                String hex = String.format("#%02X%02X%02X", colorSetting.getRed(), colorSetting.getGreen(), colorSetting.getBlue());
                if (colorSetting.hasAlpha()) {
                    hex += String.format("%02X", colorSetting.getAlpha());
                }
                moduleJson.addProperty(settingName, hex);
            }
            default -> {
            }
        }
    }

    public void resetProfile() {
        moduleManager.getModules().forEach(module -> {
            module.setEnabled(false);
            module.setKey(0);

            module.getSettings().forEach(this::resetSettingValue);
        });
    }

    private void resetSettingValue(Setting setting) {
        if (setting instanceof BooleanSetting) {
            ((BooleanSetting) setting).setValue(false);
        } else if (setting instanceof NumberSetting) {
            ((NumberSetting) setting).setValue(((NumberSetting) setting).getMin());
        } else if (setting instanceof ModeSetting) {
            ((ModeSetting) setting).setMode(((ModeSetting) setting).getModes().getFirst());
        } else if (setting instanceof KeybindSetting) {
            ((KeybindSetting) setting).setKey(((KeybindSetting) setting).getKey());
        }
    }
}
