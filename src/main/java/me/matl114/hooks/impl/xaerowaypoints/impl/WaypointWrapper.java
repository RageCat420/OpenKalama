package me.matl114.hooks.impl.xaerowaypoints.impl;

import me.matl114.hooks.impl.xaerowaypoints.IXWaypoint;
import xaero.common.minimap.waypoints.Waypoint;

public record WaypointWrapper(Waypoint waypoint) implements IXWaypoint {
   public static WaypointWrapper asWrapper(IXWaypoint waypoint) {
      return waypoint instanceof WaypointWrapper wrapper
         ? wrapper
         : (WaypointWrapper)XaeroWaypointFactoryImpl.INSTANCE
            .createWaypoint(
               waypoint.getX(),
               waypoint.getY(),
               waypoint.getZ(),
               waypoint.getName(),
               waypoint.getInitials(),
               waypoint.getColor(),
               waypoint.getPurpose(),
               waypoint.isTemp(),
               waypoint.isYInclude()
            );
   }

   public static Waypoint unwrap(IXWaypoint waypoint) {
      return waypoint instanceof WaypointWrapper wrapper
         ? wrapper.waypoint()
         : new Waypoint(
            waypoint.getX(),
            waypoint.getY(),
            waypoint.getZ(),
            waypoint.getName(),
            waypoint.getInitials(),
            waypoint.getColor(),
            waypoint.getPurpose(),
            waypoint.isTemp(),
            waypoint.isYInclude()
         );
   }

   @Override
   public int getX() {
      return this.waypoint.getX();
   }

   @Override
   public int getY() {
      return this.waypoint.getY();
   }

   @Override
   public int getZ() {
      return this.waypoint.getZ();
   }

   @Override
   public void setX(int x) {
      this.waypoint.setX(x);
   }

   @Override
   public void setY(int y) {
      this.waypoint.setY(y);
   }

   @Override
   public void setZ(int z) {
      this.waypoint.setZ(z);
   }

   @Override
   public String getName() {
      return this.waypoint.getName();
   }

   @Override
   public void setName(String name) {
      this.waypoint.setName(name);
   }

   @Override
   public String getInitials() {
      return this.waypoint.getInitials();
   }

   @Override
   public int getColor() {
      return this.waypoint.getColor();
   }

   @Override
   public void setColor(int color) {
      this.waypoint.setColor(color);
   }

   @Override
   public int getPurpose() {
      return this.waypoint.getWaypointType();
   }

   @Override
   public void setPurpose(int purpose) {
      this.waypoint.setType(purpose);
   }

   @Override
   public boolean isTemp() {
      return this.waypoint.isTemporary();
   }

   @Override
   public boolean isYInclude() {
      return this.waypoint.isYIncluded();
   }

   @Override
   public long getCreatedAt() {
      return this.waypoint.getCreatedAt();
   }
}
