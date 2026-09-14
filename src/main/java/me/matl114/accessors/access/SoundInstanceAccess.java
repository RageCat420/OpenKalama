package me.matl114.accessors.access;

import net.minecraft.client.sound.AbstractSoundInstance;

public interface SoundInstanceAccess {
   void setScale(double var1);

   static SoundInstanceAccess of(AbstractSoundInstance instance) {
      return (SoundInstanceAccess)instance;
   }
}
