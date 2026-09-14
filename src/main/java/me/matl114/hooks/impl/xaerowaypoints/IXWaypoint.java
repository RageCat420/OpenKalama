package me.matl114.hooks.impl.xaerowaypoints;

public interface IXWaypoint {
   int getX();

   int getY();

   int getZ();

   void setX(int var1);

   void setY(int var1);

   void setZ(int var1);

   String getName();

   void setName(String var1);

   String getInitials();

   int getColor();

   void setColor(int var1);

   int getPurpose();

   void setPurpose(int var1);

   boolean isTemp();

   boolean isYInclude();

   long getCreatedAt();
}
