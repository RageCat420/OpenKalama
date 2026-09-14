package me.matl114.hooks.impl.xaeroplus.wrapper;

public class EllipseWrapper<T> extends ElementWrapper<T> {
   public int centerX;
   public int centerZ;
   public int radiusX;
   public int radiusZ;

   public EllipseWrapper(int centerX, int centerZ, int radiusX, int radiusZ) {
      this.centerX = centerX;
      this.centerZ = centerZ;
      this.radiusX = radiusX;
      this.radiusZ = radiusZ;
   }
}
