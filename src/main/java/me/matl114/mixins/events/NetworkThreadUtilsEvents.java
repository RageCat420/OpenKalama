package me.matl114.mixins.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.matl114.events.Listener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin({NetworkThreadUtils.class})
public class NetworkThreadUtilsEvents {
    @WrapOperation(
            method = {"method_11072"},
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lnet/minecraft/network/packet/Packet;apply(Lnet/minecraft/network/listener/PacketListener;)V")
            })
    private static void wrapPacketHandle(Packet instance, PacketListener t, Operation<Void> original) {
        if (t.getSide() == NetworkSide.SERVERBOUND) {
            original.call(new Object[] {instance, t});
        } else {
            Listener.callPacketHandleEvent(instance, t, (xva$0, xva$1) -> {
                Void var10000 = (Void) original.call(new Object[] {xva$0, xva$1});
            });
        }
    }
}
