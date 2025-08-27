package com.volt.mixin;

import com.volt.Volt;
import com.volt.event.impl.network.EventDisconnect;
import com.volt.event.impl.network.EventPacket;
import com.volt.event.types.TransferOrder;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class MixinClientConnection {
    // Add Recieve packet event here :drool:
    @Inject(
            method = "handlePacket",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void receivePacketEventInject(Packet<?> packet, PacketListener listener, CallbackInfo ci) {
        final EventPacket eventPacket = new EventPacket(packet, TransferOrder.RECEIVE);
        Volt.INSTANCE.getVoltEventBus().post(eventPacket);
        if (eventPacket.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "handleDisconnection",
            at = @At("HEAD")
    )
    public void handleDisconnectionInject(CallbackInfo ci) {
        Volt.INSTANCE.getVoltEventBus().post(new EventDisconnect());
    }

    // Took me wayyyy to long to make..
    @Inject(
            method = "send(Lnet/minecraft/network/packet/Packet;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void sendPacketEventInject(Packet<?> packet, CallbackInfo ci) {
        final EventPacket eventPacket = new EventPacket(packet, TransferOrder.SEND);
        Volt.INSTANCE.getVoltEventBus().post(eventPacket);
        if (eventPacket.isCancelled()) {
            ci.cancel();
        }
    }
}
