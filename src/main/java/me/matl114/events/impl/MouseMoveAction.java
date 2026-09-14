package me.matl114.events.impl;

import net.minecraft.client.Mouse;

public record MouseMoveAction(Mouse mouse, double mouseX, double mouseY) {
   public double mouseY() {
      return this.mouseY;
   }

   public double mouseX() {
      return this.mouseX;
   }

   public MouseMoveAction(Mouse mouse, double mouseX, double mouseY) {
      this.mouse = mouse;
      this.mouseX = mouseX;
      this.mouseY = mouseY;
   }

   public Mouse mouse() {
      return this.mouse;
   }
}
