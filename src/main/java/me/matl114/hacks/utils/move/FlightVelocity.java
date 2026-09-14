package me.matl114.hacks.utils.move;

import net.minecraft.util.math.Vec3d;

public class FlightVelocity {
   final HackUtilHelperA mode;
   double a;
   final double d;
   double b;
   double c;

   public Vec3d b() {
      return new Vec3d(this.a, this.b, this.c);
   }

   public HackUtilHelperA g() {
      return this.mode;
   }

   public double e() {
      return this.c;
   }

   @Override
   public String toString() {
      return "FlightVelocity(x=" + this.c() + ", y=" + this.d() + ", z=" + this.e() + ", maxVelocity=" + this.f() + ", mode=" + this.g() + ")";
   }

   public FlightVelocity i(double y) {
      this.b = y;
      return this;
   }

   public FlightVelocity(Vec3d vec, double maxVelocity, HackUtilHelperA mode) {
      this(vec.x, vec.y, vec.z, maxVelocity, mode);
   }

   public FlightVelocity h(double x) {
      this.a = x;
      return this;
   }

   public FlightVelocity(double x, double y, double z, double maxVelocity, HackUtilHelperA mode) {
      this.a = x;
      this.b = y;
      this.c = z;
      this.d = maxVelocity;
      this.mode = mode;
   }

   public double f() {
      return this.d;
   }

   public FlightVelocity j(double z) {
      this.c = z;
      return this;
   }

   public double c() {
      return this.a;
   }

   public double d() {
      return this.b;
   }

   public void velocity(Vec3d vec3d) {
      this.a = vec3d.x;
      this.b = vec3d.y;
      this.c = vec3d.z;
   }
}
