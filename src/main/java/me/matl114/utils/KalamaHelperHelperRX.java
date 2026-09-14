package me.matl114.utils;

public class KalamaHelperHelperRX implements KalamaHelperHelperAl {
   double b;
   double a;
   double c;

   public KalamaHelperHelperRX(double a, double b, double c) {
      this.a = a;
      this.b = b;
      this.c = c;
   }

   @Override
   public double f(double x) {
      return this.a * x * x + this.b * x + this.c;
   }
}
