package me.matl114.hooks;

import java.util.ArrayList;
import me.matl114.events.channels.EventChannel;
import me.matl114.hooks.impl.xaeroplus.IMapDrawFactory;
import me.matl114.hooks.impl.xaeroplus.impl.MapDrawFactoryImpl;
import me.matl114.hooks.impl.xaerowaypoints.IXWaypointFactory;
import me.matl114.hooks.impl.xaerowaypoints.impl.XaeroWaypointFactoryImpl;
import me.matl114.hooks.impl.xaeroworldmap.MapClickContext;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.map.WorldMap;
import xaero.minimap.XaeroMinimap;
import xaeroplus.Globals;

public class XaeroHooks implements IHooks {
    public static final XaeroHooks INSTANCE = new XaeroHooks();
    boolean enable;
    XaeroHooks.XaeroWorldMapHooks worldMapHooks;
    XaeroHooks.XaeroPlusHooks plusHooks;
    XaeroHooks.XaeroMiniMapHooks minimap;
    private static final EventChannel<ArrayList<MapClickContext>> worldMapRightClickOption = new EventChannel<>();

    public static XaeroHooks getInstance() {
        return INSTANCE;
    }

    public XaeroHooks() {
        try {
            this.worldMapHooks = new XaeroHooks.XaeroWorldMapImpl();
        } catch (Throwable var4) {
            this.worldMapHooks = new XaeroHooks.XaeroWorldMapHooks();
        }

        try {
            this.plusHooks = new XaeroHooks.XaeroPlusImpl();
        } catch (Throwable var3) {
            this.plusHooks = new XaeroHooks.XaeroPlusHooks();
        }

        try {
            this.minimap = new XaeroHooks.XaeroMiniMapImpl();
        } catch (Throwable var2) {
            this.minimap = new XaeroHooks.XaeroMiniMapHooks();
        }

        this.enable = this.isXaeroPlusEnable() || this.isXaeroMiniMapEnable() || this.isXaeroWorldMapEnable();
    }

    public boolean isXaeroWorldMapEnable() {
        return this.worldMapHooks.isEnabled();
    }

    public boolean isXaeroMiniMapEnable() {
        return this.minimap.isEnabled();
    }

    public boolean isXaeroPlusEnable() {
        return this.plusHooks.isEnabled();
    }

    @Override
    public boolean isEnabled() {
        return this.enable;
    }

    public IMapDrawFactory getMapDrawFactory() {
        return this.plusHooks.getMapDrawFactory();
    }

    public IXWaypointFactory getWaypointFactory() {
        return this.minimap.getWaypointFactory();
    }

    public static EventChannel<ArrayList<MapClickContext>> getWorldMapRightClickOption() {
        return worldMapRightClickOption;
    }

    public static class XaeroMiniMapHooks {
        public boolean isEnabled() {
            return false;
        }

        public IXWaypointFactory getWaypointFactory() {
            return null;
        }
    }

    public static class XaeroMiniMapImpl extends XaeroHooks.XaeroMiniMapHooks {
        IXWaypointFactory waypointFactory;

        public XaeroMiniMapImpl() {
            Class<?> miniMap = XaeroMinimap.class;
            Class<?> access = BuiltInHudModules.class;
            this.waypointFactory = XaeroWaypointFactoryImpl.INSTANCE;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public IXWaypointFactory getWaypointFactory() {
            return this.waypointFactory;
        }
    }

    public static class XaeroPlusHooks {
        public boolean isEnabled() {
            return false;
        }

        public IMapDrawFactory getMapDrawFactory() {
            return null;
        }
    }

    public static class XaeroPlusImpl extends XaeroHooks.XaeroPlusHooks {
        final IMapDrawFactory mapDrawFactory;

        public XaeroPlusImpl() {
            Class<?> main = Globals.class;
            this.mapDrawFactory = MapDrawFactoryImpl.INSTANCE;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public IMapDrawFactory getMapDrawFactory() {
            return this.mapDrawFactory;
        }
    }

    public static class XaeroWorldMapHooks {
        public boolean isEnabled() {
            return false;
        }
    }

    public static class XaeroWorldMapImpl extends XaeroHooks.XaeroWorldMapHooks {
        public XaeroWorldMapImpl() {
            Class<?> main = WorldMap.class;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}
