package me.matl114.versioned.accessors;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;

public interface PlayerEntityRendererStateAccess {
   Arm a();

   void d(ItemStack var1);

   void b(Arm var1);

   ItemStack c();
}
