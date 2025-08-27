package com.volt.utils.mc;

import com.volt.IMinecraft;
import lombok.experimental.UtilityClass;
import net.minecraft.text.Text;

@UtilityClass
public final class ChatUtils implements IMinecraft {
    public static void addChatMessage(String text) {
        mc.inGameHud.getChatHud().addMessage(Text.of(text));
    }
}
