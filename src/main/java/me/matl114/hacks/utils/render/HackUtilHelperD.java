package me.matl114.hacks.utils.render;

import net.minecraft.util.math.Vec3d;

public record HackUtilHelperD(double x0, double y0, double z0, double x1, double y1, double z1) {
   public HackUtilHelperD(Vec3d from, Vec3d to) {
      this(from.x, from.y, from.z, to.x, to.y, to.z);
   }

   public double x0() {
      return this.x0;
   }

   public double z1() {
      return this.z1;
   }

   public double y0() {
      return this.y0;
   }

   public HackUtilHelperD(double x0, double y0, double z0, double x1, double y1, double z1) {
      x0 = RenderElements.normalizeZero(x0);
      y0 = RenderElements.normalizeZero(y0);
      z0 = RenderElements.normalizeZero(z0);
      x1 = RenderElements.normalizeZero(x1);
      y1 = RenderElements.normalizeZero(y1);
      z1 = RenderElements.normalizeZero(z1);
      if (RenderElements.comparePoint(x1, y1, z1, x0, y0, z0) < 0) {
         double var13 = x0;
         double var15 = y0;
         double var17 = z0;
         x0 = x1;
         y0 = y1;
         z0 = z1;
         x1 = var13;
         y1 = var15;
         z1 = var17;
      }

      this.x1 = x0;
      this.z0 = y0;
      this.y1 = z0;
      this.x0 = x1;
      this.y0 = y1;
      this.z1 = z1;
   }

   public double y1() {
      return this.y1;
   }

   public double x1() {
      return this.x1;
   }

   public double z0() {
      return this.z0;
   }

   public HackUtilHelperD offset(Vec3d delta) {
      return new HackUtilHelperD(this.x1 + delta.x, this.z0 + delta.y, this.y1 + delta.z, this.x0 + delta.x, this.y0 + delta.y, this.z1 + delta.z);
   }
}
