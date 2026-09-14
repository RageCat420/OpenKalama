package me.matl114.hooks.impl.xaerowaypoints.impl;

import javax.annotation.Nullable;
import me.matl114.hooks.impl.xaerowaypoints.IXWaypoint;
import me.matl114.hooks.impl.xaerowaypoints.IXWaypointAccess;
import me.matl114.hooks.impl.xaerowaypoints.IXWaypointFactory;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.MinimapWorldManager;

public class XaeroWaypointFactoryImpl implements IXWaypointFactory {
    public static final XaeroWaypointFactoryImpl INSTANCE = new XaeroWaypointFactoryImpl();

    @Override
    public IXWaypoint createWaypoint(
            int x, int y, int z, String name, String initials, int color, int type, boolean temp, boolean yIncluded) {
        return new WaypointWrapper(new Waypoint(x, y, z, name, initials, color, type, temp, yIncluded));
    }

    @Nullable
    @Override
    public IXWaypointAccess getCurrentWaypointSet() {
        MinimapWorldManager world = ((MinimapSession) BuiltInHudModules.MINIMAP.getCurrentSession()).getWorldManager();
        if (world == null) {
            return null;
        } else {
            MinimapWorld set = world.getCurrentWorld();
            if (set == null) {
                return null;
            } else {
                WaypointSet acc = set.getCurrentWaypointSet();
                return acc == null ? null : new XaeroWaypointSetImpl(acc);
            }
        }
    }

    @Nullable
    @Override
    public RegistryKey<World> getCurrentWorld() {
        MinimapWorldManager world = ((MinimapSession) BuiltInHudModules.MINIMAP.getCurrentSession()).getWorldManager();
        if (world == null) {
            return null;
        } else {
            MinimapWorld acc = world.getCurrentWorld();
            return acc == null ? null : acc.getDimId();
        }
    }
}
