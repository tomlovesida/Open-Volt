package com.volt.mixin;

import com.volt.Volt;
import com.volt.module.modules.player.FastMineModule;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
    private void modifyBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
        if (!(Objects.requireNonNull(Volt.INSTANCE.getModuleManager().getModule(FastMineModule.class)).isEnabled())) {
            return;
        }

        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player == MinecraftClient.getInstance().player) {
                float modifiedSpeed = (cir.getReturnValue() * Objects.requireNonNull(Volt.INSTANCE.getModuleManager().getModule(FastMineModule.class)).getSpeed());
                cir.setReturnValue(modifiedSpeed);

        }
    }
}
