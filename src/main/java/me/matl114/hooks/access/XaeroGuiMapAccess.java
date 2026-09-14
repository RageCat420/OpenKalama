package me.matl114.hooks.access;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

public interface XaeroGuiMapAccess {
   RegistryKey<World> getRightClickDim();

   int getRightClickX();

   int getRightClickY();

   int getRightClickZ();
}
