package com.volt;


import com.volt.command.CommandManager;
import com.volt.module.ModuleManager;
import com.volt.module.events.MouseModuleHandler;
import com.volt.profiles.ProfileManager;
import com.volt.utils.font.FontManager;
import io.github.racoondog.norbit.EventBus;
import lombok.Getter;
import meteordevelopment.orbit.IEventBus;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;

import java.lang.invoke.MethodHandles;

@Getter
public final class Volt implements ModInitializer {
    public static Volt INSTANCE;
    public static MinecraftClient mc;
    public final IEventBus VoltEventBus;
    public final ModuleManager moduleManager;
    public final FontManager fontManager;
    public final ProfileManager profileManager;
    public final CommandManager commandManager;
    public final MouseModuleHandler mouseModuleHandler;

    public Volt() {
        INSTANCE = this;
        mc = MinecraftClient.getInstance();
        VoltEventBus = EventBus.threadSafe();
        VoltEventBus.registerLambdaFactory("com.volt", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
        moduleManager = new ModuleManager();
        fontManager = new FontManager();
        profileManager = new ProfileManager();
        commandManager = new CommandManager();
        mouseModuleHandler = new MouseModuleHandler();
        VoltEventBus.subscribe(mouseModuleHandler);
    }

    @Override
    public void onInitialize() {
        new Volt();
    }
}