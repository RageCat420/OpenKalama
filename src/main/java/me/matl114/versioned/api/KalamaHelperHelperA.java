package me.matl114.versioned.api;

import net.minecraft.client.font.TextRenderer.TextLayerType;

public record KalamaHelperHelperA(boolean shadow, TextLayerType layerType, int backgroundColor, int light) {
   public KalamaHelperHelperA(boolean shadow, TextLayerType layerType, int backgroundColor, int light) {
      this.shadow = shadow;
      this.layerType = layerType;
      this.backgroundColor = backgroundColor;
      this.light = light;
   }

   public TextLayerType layerType() {
      return this.layerType;
   }

   public KalamaHelperHelperA dI(int light) {
      return this.light == light ? this : new KalamaHelperHelperA(this.shadow, this.layerType, this.backgroundColor, light);
   }

   public int light() {
      return this.light;
   }

   public KalamaHelperHelperA dH(int backgroundColor) {
      return this.backgroundColor == backgroundColor ? this : new KalamaHelperHelperA(this.shadow, this.layerType, backgroundColor, this.light);
   }

   public int backgroundColor() {
      return this.backgroundColor;
   }

   public KalamaHelperHelperA withShadow(boolean shadow) {
      return this.shadow == shadow ? this : new KalamaHelperHelperA(shadow, this.layerType, this.backgroundColor, this.light);
   }

   public boolean shadow() {
      return this.shadow;
   }

   public KalamaHelperHelperA withLayerType(TextLayerType layerType) {
      return this.layerType == layerType ? this : new KalamaHelperHelperA(this.shadow, layerType, this.backgroundColor, this.light);
   }
}
