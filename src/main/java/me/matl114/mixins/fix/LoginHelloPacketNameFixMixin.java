package me.matl114.mixins.fix;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Environment(EnvType.CLIENT)
@Mixin({LoginHelloC2SPacket.class})
public class LoginHelloPacketNameFixMixin {
   @ModifyArg(
      method = {"write"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/PacketByteBuf;writeString(Ljava/lang/String;I)Lnet/minecraft/network/PacketByteBuf;"
      ),
      index = 0
   )
   private String onChangeNameSend(String name) {
      return name == null ? "null" : name.substring(0, Math.min(16, name.length()));
   }
}
