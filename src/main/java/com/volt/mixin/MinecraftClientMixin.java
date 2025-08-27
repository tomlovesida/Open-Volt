package com.volt.mixin;

import com.volt.IMinecraft;
import com.volt.Volt;
import com.volt.event.impl.player.TickEvent;
import com.volt.event.impl.world.WorldChangeEvent;
import com.volt.gui.ClickGui;
import com.volt.module.modules.client.ClickGUIModule;
import com.volt.module.modules.client.ClientModule;
import com.volt.profiles.ProfileManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements IMinecraft {

    @Shadow
    public ClientWorld world;

    // The null ptr is real...
    @Inject(method = "getWindowTitle", at = @At("HEAD"), cancellable = true)
    public void setTitle(CallbackInfoReturnable<String> cir) {
        if (Volt.INSTANCE != null && ClientModule.title.getValue() && Objects.requireNonNull(Volt.INSTANCE.getModuleManager().getModule(ClientModule.class)).isEnabled() && Volt.mc != null) {
            cir.setReturnValue("Volt 1.21");
        }
    }

    @Inject(method = "run", at = @At("HEAD"))
    public void runInject(CallbackInfo ci) {
        ProfileManager profileManager = new ProfileManager();
        profileManager.loadProfile("default");
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (world != null) {
            Volt.INSTANCE.getVoltEventBus().post(new TickEvent());
        }
        if (Volt.INSTANCE.getModuleManager().getModule(ClickGUIModule.class).isEnabled() && !(mc.currentScreen instanceof ClickGui) && world != null) {
            mc.setScreen(new ClickGui());
        }
    }

    @Inject(method = "stop", at = @At("HEAD"))
    public void stopInject(CallbackInfo ci) {
        ProfileManager profileManager = new ProfileManager();
        profileManager.saveProfile("default");
    }

    @Inject(
            method = "setWorld",
            at = @At("HEAD")
    )
    public void onWorldChangeInject(ClientWorld world, CallbackInfo ci) {
        Volt.INSTANCE.getVoltEventBus().post(new WorldChangeEvent(mc.world));
    }
}
