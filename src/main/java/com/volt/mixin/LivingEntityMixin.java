package com.volt.mixin;

import com.volt.Volt;
import com.volt.module.modules.render.SwingSpeedModule;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(
            method = "getHandSwingDuration",
            at = @At("HEAD"),
            cancellable = true)
    public void getHandSwingDurationInject(CallbackInfoReturnable<Integer> cir) {
        if (Volt.INSTANCE != null && Volt.mc != null) {
            if (Objects.requireNonNull(Volt.INSTANCE.getModuleManager().getModule(SwingSpeedModule.class)).isEnabled()) {
                cir.setReturnValue(SwingSpeedModule.swingSpeed.getValueInt());
            }
        }
    }
}
