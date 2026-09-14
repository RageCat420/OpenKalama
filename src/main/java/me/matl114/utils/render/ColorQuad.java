package me.matl114.utils.render;

import java.awt.Color;

public interface ColorQuad {
   static ColorQuad Ln(int color1, int color2, int color3, int color4) {
      return new KalamaHelperHelperF(color1, color2, color3, color4);
   }

   static ColorQuad Ll(int color1, int color2) {
      return new KalamaHelperHelperF(color1, color1, color2, color2);
   }

   static ColorQuad ofGradient(Color color1, Color color2, Color color3, Color color4) {
      return new KalamaHelperHelperF(color1.getRGB(), color2.getRGB(), color3.getRGB(), color4.getRGB());
   }

   int get(int var1);

   static ColorQuad Lj(Color color1, Color color2) {
      return new KalamaHelperHelperF(color1.getRGB(), color1.getRGB(), color2.getRGB(), color2.getRGB());
   }

   static ColorQuad Lk(Color color1, Color color2) {
      return new KalamaHelperHelperF(color1.getRGB(), color2.getRGB(), color2.getRGB(), color1.getRGB());
   }

   static ColorQuad of(int color) {
      return new KalamaHelperHelperA(color);
   }

   static ColorQuad Lg(Color color) {
      return new KalamaHelperHelperA(color.getRGB());
   }

   static ColorQuad Lm(int color1, int color2) {
      return new KalamaHelperHelperF(color1, color2, color2, color1);
   }

   static ColorQuad Lh(int r, int g, int b, int a) {
      return new KalamaHelperHelperA((a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF);
   }
}
