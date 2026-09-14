package me.matl114.gui.basic;

import java.awt.Color;

public interface ColorSampler {
   ColorSampler WHITE = of(Color.WHITE.getRGB());

   static ColorSampler of(int color) {
      Color var1 = new Color(color);
      return new KalamaHelperHelperHX(color, var1);
   }

   int getColorInt();

   default Color getColor() {
      return new Color(this.getColorInt());
   }
}
