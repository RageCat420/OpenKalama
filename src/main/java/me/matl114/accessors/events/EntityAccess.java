package me.matl114.accessors.events;

import me.matl114.accessors.interfaces.MetadataHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

public interface EntityAccess<T extends Entity> extends MetadataHolder {
   void setDataFlag(int var1, boolean var2);

   boolean getDataFlag(int var1);

   static <T extends Entity> EntityAccess<T> of(T entity) {
      return (EntityAccess<T>)entity;
   }

   default boolean checkClientPlayer() {
      return this == MinecraftClient.getInstance().player;
   }
}
