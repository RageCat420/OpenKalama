package me.matl114.mixins.access;

import me.matl114.accessors.access.PlayerInteractBlockC2SPacketAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin({PlayerInteractBlockC2SPacket.class})
public class PlayerInteractBlockC2SPacketMixin implements PlayerInteractBlockC2SPacketAccess {
   @Unique
   PlayerInteractBlockC2SPacketAccess.UseContext useContext;

   @Mutable
   @Accessor("hand")
   @Override
   public void setHand(Hand var1) { }

   @Mutable
   @Accessor("blockHitResult")
   @Override
   public void setBlockHitResult(BlockHitResult var1) { }

   @Mutable
   @Accessor("sequence")
   @Override
   public void setSequence(int var1) { }

   @Override
   public void setUseContext(PlayerInteractBlockC2SPacketAccess.UseContext useContext) {
      this.useContext = useContext;
   }

   @Override
   public PlayerInteractBlockC2SPacketAccess.UseContext getUseContext() {
      return this.useContext;
   }
}
