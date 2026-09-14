package me.matl114.hooks.impl.xaerowaypoints;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public interface IXWaypointAccess {
   String getName();

   void addTo(List<IXWaypoint> var1);

   void add(IXWaypoint var1, boolean var2);

   void add(IXWaypoint var1);

   void addAll(Collection<IXWaypoint> var1, boolean var2);

   void addAll(Collection<IXWaypoint> var1);

   void remove(IXWaypoint var1);

   IXWaypoint remove(int var1);

   void removeAll(Collection<IXWaypoint> var1);

   void clear();

   boolean isEmpty();

   int size();

   void update(IXWaypoint var1, Consumer<IXWaypoint> var2);

   IXWaypoint get(int var1);

   IXWaypoint set(int var1, IXWaypoint var2);

   void requestRefresh();
}
