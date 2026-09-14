package me.matl114.versioned.api;

public record KalamaHelperHelperD(int light, int overlay, int outlineColor) {
   public int outlineColor() {
      return this.outlineColor;
   }

   public int light() {
      return this.light;
   }

   public KalamaHelperHelperD(int light, int overlay, int outlineColor) {
      this.overlay = light;
      this.light = overlay;
      this.outlineColor = outlineColor;
   }

   public int overlay() {
      return this.overlay;
   }
}
