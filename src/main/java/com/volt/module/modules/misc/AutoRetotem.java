package com.volt.module.modules.misc;

import com.volt.event.impl.player.TickEvent;
import com.volt.module.Category;
import com.volt.module.Module;
import com.volt.module.setting.NumberSetting;
import com.volt.utils.math.TimerUtil;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public final class AutoRetotem extends Module {
    private final NumberSetting delay = new NumberSetting("Delay", 0, 20, 0, 1);

    private final TimerUtil timer = new TimerUtil();
    private boolean opened = false;

    public AutoRetotem() {
        super("Auto Retotem", "Automatically equips a totem in your offhand", -1, Category.MISC);
        addSettings(delay);
    }

    @Override
    public void onEnable() {
        timer.reset();
        opened = false;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (mc.currentScreen instanceof InventoryScreen) {
            mc.setScreen(null);
        }
        opened = false;
        super.onDisable();
    }

    @EventHandler
    private void onTick(TickEvent event) {
        if (isNull()) return;
        if (mc.currentScreen != null && !(mc.currentScreen instanceof InventoryScreen)) return;

        if (hasTotemInOffhand()) {
            if (opened) {
                mc.setScreen(null);
                opened = false;
            }
            return;
        }

        int slot = findTotemSlot();
        if (slot == -1) return;
        if (!timer.hasElapsedTime(delay.getValueInt() * 50L, true)) return;

        int syncId = mc.player.currentScreenHandler.syncId;
        if (!(mc.currentScreen instanceof InventoryScreen)) {
            mc.setScreen(new InventoryScreen(mc.player));
            opened = true;
            return;
        }

        mc.interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, mc.player);

        mc.interactionManager.clickSlot(syncId, 45, 0, SlotActionType.PICKUP, mc.player);

        mc.interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, mc.player);
    }

    private int findTotemSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                return 36 + i;
            }
        }
        for (int i = 9; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                return i;
            }
        }
        return -1;
    }



    private boolean hasTotemInOffhand() {
        ItemStack offhand = mc.player.getOffHandStack();
        return !offhand.isEmpty() && offhand.getItem() == Items.TOTEM_OF_UNDYING;
    }
}
