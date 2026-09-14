package me.matl114.hooks.impl.xaerowaypoints.impl;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import me.matl114.hooks.impl.xaerowaypoints.IXWaypoint;
import me.matl114.hooks.impl.xaerowaypoints.IXWaypointAccess;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.map.mods.SupportMods;

public record XaeroWaypointSetImpl(WaypointSet waypointSet) implements IXWaypointAccess {
    @Override
    public String getName() {
        return this.waypointSet.getName();
    }

    @Override
    public void addTo(List<IXWaypoint> collector) {
        this.waypointSet.addTo(collector.stream().map(WaypointWrapper::unwrap).toList());
    }

    @Override
    public void add(IXWaypoint IXWaypoint, boolean front) {
        this.waypointSet.add(WaypointWrapper.unwrap(IXWaypoint), front);
    }

    @Override
    public void add(IXWaypoint IXWaypoint) {
        this.waypointSet.add(WaypointWrapper.unwrap(IXWaypoint));
    }

    @Override
    public void addAll(Collection<IXWaypoint> IXWaypoints, boolean front) {
        this.waypointSet.addAll(
                IXWaypoints.stream().map(WaypointWrapper::unwrap).toList(), front);
    }

    @Override
    public void addAll(Collection<IXWaypoint> IXWaypoints) {
        this.waypointSet.addAll(
                IXWaypoints.stream().map(WaypointWrapper::unwrap).toList());
    }

    @Override
    public void remove(IXWaypoint IXWaypoint) {
        this.waypointSet.remove(WaypointWrapper.unwrap(IXWaypoint));
    }

    @Override
    public IXWaypoint remove(int slot) {
        return new WaypointWrapper(this.waypointSet.remove(slot));
    }

    @Override
    public void removeAll(Collection<IXWaypoint> IXWaypoints) {
        this.waypointSet.removeAll(
                IXWaypoints.stream().map(WaypointWrapper::unwrap).toList());
    }

    @Override
    public void clear() {
        this.waypointSet.clear();
    }

    @Override
    public boolean isEmpty() {
        return this.waypointSet.isEmpty();
    }

    @Override
    public int size() {
        return this.waypointSet.size();
    }

    @Override
    public IXWaypoint get(int slot) {
        return new WaypointWrapper(this.waypointSet.get(slot));
    }

    @Override
    public IXWaypoint set(int slot, IXWaypoint IXWaypoint) {
        return new WaypointWrapper(this.waypointSet.set(slot, WaypointWrapper.unwrap(IXWaypoint)));
    }

    @Override
    public void requestRefresh() {
        SupportMods.xaeroMinimap.requestWaypointsRefresh();
    }

    @Override
    public void update(IXWaypoint waypoint, Consumer<IXWaypoint> updater) {
        int index = this.size();
        Waypoint waypoint1 = WaypointWrapper.unwrap(waypoint);

        for (int i = 0; i < index; i++) {
            Waypoint re = this.waypointSet.get(i);
            if (Objects.equals(re, waypoint1)) {
                WaypointWrapper wrapper = new WaypointWrapper(re);
                updater.accept(wrapper);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof XaeroWaypointSetImpl impl && impl.waypointSet() == this.waypointSet;
    }
}
