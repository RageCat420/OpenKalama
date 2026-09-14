package me.matl114.hooks.impl.xaerowaypoints;

import javax.annotation.Nullable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

public interface IXWaypointFactory {
   IXWaypoint createWaypoint(int var1, int var2, int var3, String var4, String var5, int var6, int var7, boolean var8, boolean var9);

   @Nullable
   IXWaypointAccess getCurrentWaypointSet();

   @Nullable
   RegistryKey<World> getCurrentWorld();
}
