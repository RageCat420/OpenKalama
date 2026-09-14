package me.matl114.accessors.access;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public interface ClientAccess {
   static ClientAccess of(MinecraftClient client) {
      return (ClientAccess)client;
   }

   ClientAccess clone();

   void setItemUseCooldown(int var1);

   void setAttackCooldown(int var1);

   int getAttackCooldown();

   int getItemUseCooldown();

   void simulateRightClick();

   void simulateLeftClick();

   ActionResult simulateUseItem(Hand var1);
}
