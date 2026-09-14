package me.matl114.utils.render;

import java.util.Iterator;
import java.util.stream.Stream;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public class Quad implements Iterable<Vec3d> {
   Vec3d j;
   Vec3d h;
   Vec3d k;
   Vec3d i;

   public void s(Vec3d vec3d4) {
      this.k = vec3d4;
   }

   public Vec3d get(int index) {
      return switch (index & 3) {
         case 0 -> this.h;
         case 1 -> this.i;
         case 2 -> this.j;
         case 3 -> this.k;
         default -> throw new IndexOutOfBoundsException();
      };
   }

   public static Quad rectangularXY(Vec3d min, Vec3d max) {
      return new Quad(min, new Vec3d(min.x, max.y, min.z), max, new Vec3d(max.x, min.y, max.z));
   }

   public Vec3d n() {
      return this.j;
   }

   @NotNull
   @Override
   public Iterator<Vec3d> iterator() {
      return Stream.of(this.h, this.i, this.j, this.k).iterator();
   }

   public Vec3d o() {
      return this.k;
   }

   public Quad(Vec3d vec3d1, Vec3d vec3d2, Vec3d vec3d3, Vec3d vec3d4) {
      this.h = vec3d1;
      this.i = vec3d2;
      this.j = vec3d3;
      this.k = vec3d4;
   }

   public void r(Vec3d vec3d3) {
      this.j = vec3d3;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof Quad var2)) {
         return false;
      } else if (!var2.t(this)) {
         return false;
      } else {
         Vec3d var3 = this.l();
         Vec3d var4 = var2.l();
         if (var3 == null ? var4 == null : var3.equals(var4)) {
            Vec3d var5 = this.m();
            Vec3d var6 = var2.m();
            if (var5 == null ? var6 == null : var5.equals(var6)) {
               Vec3d var7 = this.n();
               Vec3d var8 = var2.n();
               if (var7 == null ? var8 == null : var7.equals(var8)) {
                  Vec3d var9 = this.o();
                  Vec3d var10 = var2.o();
                  return var9 == null ? var10 == null : var9.equals(var10);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public static Quad i(Vec3d min, Vec3d max) {
      return new Quad(new Vec3d(max.x, min.y, max.z), min, new Vec3d(min.x, max.y, min.z), max);
   }

   public static Quad h(int x1, int y1, int x2, int y2, int z) {
      return new Quad(new Vec3d(x1, y1, z), new Vec3d(x1, y2, z), new Vec3d(x2, y2, z), new Vec3d(x2, y1, z));
   }

   public Vec3d m() {
      return this.i;
   }

   public Vec3d l() {
      return this.h;
   }

   public static Quad j(int x1, int y1, int x2, int y2, int z) {
      return new Quad(new Vec3d(x1, y2, z), new Vec3d(x1, y1, z), new Vec3d(x2, y1, z), new Vec3d(x2, y2, z));
   }

   public void q(Vec3d vec3d2) {
      this.i = vec3d2;
   }

   protected boolean t(Object other) {
      return other instanceof Quad;
   }

   public void p(Vec3d vec3d1) {
      this.h = vec3d1;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      Vec3d var3 = this.l();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      Vec3d var4 = this.m();
      var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      Vec3d var5 = this.n();
      var2 = var2 * 59 + (var5 == null ? 43 : var5.hashCode());
      Vec3d var6 = this.o();
      return var2 * 59 + (var6 == null ? 43 : var6.hashCode());
   }

   @Override
   public String toString() {
      return "Quad(vec3d1=" + this.l() + ", vec3d2=" + this.m() + ", vec3d3=" + this.n() + ", vec3d4=" + this.o() + ")";
   }
}
