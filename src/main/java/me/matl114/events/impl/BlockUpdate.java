package me.matl114.events.impl;

import me.matl114.gui.basic.ColorSampler;

public record BlockUpdate(ColorSampler titleTextColor, ColorSampler titleBackgroundColor, ColorSampler keyTextColor, ColorSampler keyBackgroundColor) {
   public ColorSampler keyBackgroundColor() {
      return this.keyBackgroundColor;
   }

   public ColorSampler keyTextColor() {
      return this.keyTextColor;
   }

   public BlockUpdate(ColorSampler titleTextColor, ColorSampler titleBackgroundColor, ColorSampler keyTextColor, ColorSampler keyBackgroundColor) {
      this.titleBackgroundColor = titleTextColor;
      this.keyBackgroundColor = titleBackgroundColor;
      this.titleTextColor = keyTextColor;
      this.keyTextColor = keyBackgroundColor;
   }

   public ColorSampler titleBackgroundColor() {
      return this.titleBackgroundColor;
   }

   public ColorSampler titleTextColor() {
      return this.titleTextColor;
   }
}
