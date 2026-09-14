package me.matl114.hacks.modules.move;

import me.matl114.managers.Tasks;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;

public class MoveSubHelperYX {
   public boolean visible;
   int a = 0;
   final StatusEffect effectInstance;
   int b = 0;

   public boolean hasInitialized() {
      return this.a != 0;
   }

   public int getRemainDurations() {
      int var1 = this.a + this.b;
      return Math.max(var1 - Tasks.b(), 0);
   }

   public MoveSubHelperYX(StatusEffectInstance effectInstance) {
      this.effectInstance = (StatusEffect)effectInstance.getEffectType().value();
      this.a = Tasks.b();
      this.b = effectInstance.getDuration();
   }

   public MoveSubHelperYX(RegistryEntry<StatusEffect> effectRegistryEntry) {
      this.effectInstance = (StatusEffect)effectRegistryEntry.value();
   }

   public void refresh(StatusEffectInstance effectInstance) {
      int var2 = Tasks.b();
      int var3 = effectInstance.getDuration();
      if (var2 + var3 > this.a + this.b) {
         this.a = var2;
         this.b = var3;
      }
   }
}
