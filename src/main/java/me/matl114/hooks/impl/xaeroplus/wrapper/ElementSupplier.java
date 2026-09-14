package me.matl114.hooks.impl.xaeroplus.wrapper;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

@FunctionalInterface
public interface ElementSupplier<T> {
   T supplyElement(int var1, int var2, int var3, RegistryKey<World> var4);
}
