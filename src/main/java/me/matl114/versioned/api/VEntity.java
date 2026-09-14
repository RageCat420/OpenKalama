package me.matl114.versioned.api;

import me.matl114.versioned.impl.Entity_v1_21_1;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;

public interface VEntity {
   VEntity INSTANCE = new Entity_v1_21_1();

   NbtCompound c(Entity var1);

   static VEntity getInstance() {
      return INSTANCE;
   }

   static NbtCompound b(Entity entity) {
      return getInstance().c(entity);
   }
}
