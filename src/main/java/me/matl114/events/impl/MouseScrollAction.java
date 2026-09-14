package me.matl114.events.impl;

import net.minecraft.client.Mouse;

public record MouseScrollAction(Mouse mouse, double horizontal, double vertical) {
   public double vertical() {
      return this.vertical;
   }

   public double horizontal() {
      return this.horizontal;
   }

   public MouseScrollAction(Mouse mouse, double horizontal, double vertical) {
      this.mouse = mouse;
      this.horizontal = horizontal;
      this.vertical = vertical;
   }

   public Mouse mouse() {
      return this.mouse;
   }
}
