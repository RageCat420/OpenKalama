package me.matl114.mixins.access;

import me.matl114.accessors.access.PlayerInteractEntityC2SPacketAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket.InteractTypeHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin({PlayerInteractEntityC2SPacket.class})
public abstract class PlayerInteractEntityC2SPacketMixin implements PlayerInteractEntityC2SPacketAccess {
   @Shadow
   @Final
   public InteractTypeHandler type;
   @Shadow
   @Final
   public static InteractTypeHandler ATTACK;

   @Mutable
   @Accessor("entityId")
   @Override
public abstract void setEntityId(int var1) ;

   @Mutable
   @Accessor("entityId")
   @Override
public abstract int getEntityId() ;

   @Mutable
   @Accessor("type")
   @Override
public abstract void setType(InteractTypeHandler var1) ;

   @Mutable
   @Accessor("playerSneaking")
   @Override
public abstract void setPlayerSneaking(boolean var1) ;

   @Override
   public boolean isAttack() {
      return this.type.getType() == ATTACK.getType();
   }
}

