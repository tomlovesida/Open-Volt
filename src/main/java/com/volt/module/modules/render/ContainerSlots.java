package com.volt.module.modules.render;

import com.volt.module.Category;
import com.volt.module.Module;
import com.volt.module.setting.ColorSetting;
import com.volt.module.setting.ModeSetting;

import java.awt.Color;

public class ContainerSlots extends Module {
    public static final ModeSetting fontMode = new ModeSetting("Font", "Inter", "Inter", "MC");
    public static final ColorSetting color = new ColorSetting("Color", new Color(15, 115, 225));

    public ContainerSlots() {
        super("Container Slots", "Renders container indices", Category.RENDER);
        addSettings(fontMode, color);
    }
}
