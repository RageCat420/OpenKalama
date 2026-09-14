package me.matl114.utils;

public class KalamaHelperHelperLX implements KalamaHelperHelperAl {
   double b;
   double a;

   public KalamaHelperHelperLX(double a, double b) {
      this.a = a;
      this.b = b;
   }

   @Override
   public double f(double x) {
      return this.a * x + this.b;
   }
}
