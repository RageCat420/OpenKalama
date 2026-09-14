package me.matl114.mixins.access;

import java.util.OptionalInt;
import me.matl114.accessors.access.FireworkRocketEntityAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin({FireworkRocketEntity.class})
public class FireworkRocketEntityMixin extends Entity implements FireworkRocketEntityAccess {
   @Shadow
   @Final
   private static TrackedData<OptionalInt> field_7611;
   @Shadow
   private int field_7613;

   public FireworkRocketEntityMixin(EntityType<?> type, World world) {
      super(type, world);
   }

   @Unique
   @Override
   public boolean isFallFlyingAccelerator() {
      return ((OptionalInt)(Object)this.dataTracker.get(field_7611)).isPresent();
   }

   @Unique
   @Override
   public int getLiveTicks() {
      return this.field_7613;
   }

   public void writeCustomDataToNbt(Object arg0) { }

}
