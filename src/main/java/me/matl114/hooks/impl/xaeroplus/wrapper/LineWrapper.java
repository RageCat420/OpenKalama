package me.matl114.hooks.impl.xaeroplus.wrapper;

public class LineWrapper<T> extends ElementWrapper<T> {
   public int x1;
   public int z1;
   public int x2;
   public int z2;

   public LineWrapper(int x1, int z1, int x2, int z2) {
      this.x1 = x1;
      this.z1 = z1;
      this.x2 = x2;
      this.z2 = z2;
   }
}
