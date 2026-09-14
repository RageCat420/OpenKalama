package me.matl114.mixins.fix;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import me.matl114.hacks.ExtraTasks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.handler.DecoderHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin({DecoderHandler.class})
public class DecoderByteBufExceptionFixMixin {
    @WrapOperation(
            method = {"decode"},
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lnet/minecraft/network/codec/PacketCodec;decode(Ljava/lang/Object;)Ljava/lang/Object;")
            })
    public Object onDecodeException(PacketCodec instance, Object object, Operation<Object> original) {
        try {
            return original.call(new Object[] {instance, object});
        } catch (DecoderException var6) {
            if (ExtraTasks.d().noDisconnectOnPacketDecode.get() && object instanceof ByteBuf buf) {
                buf.skipBytes(buf.readableBytes());
            }

            throw var6;
        }
    }
}
