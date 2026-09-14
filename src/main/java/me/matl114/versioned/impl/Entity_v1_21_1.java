package me.matl114.versioned.impl;

import me.matl114.versioned.api.VEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;

public class Entity_v1_21_1 implements VEntity {
   public NbtCompound c(Entity entity) {
      NbtCompound var2 = new NbtCompound();
      entity.writeNbt(var2);
      return var2;
   }
}
