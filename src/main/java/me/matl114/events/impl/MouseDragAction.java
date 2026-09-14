package me.matl114.events.impl;

import net.minecraft.client.Mouse;

public record MouseDragAction(Mouse mouse, double mouseX, double mouseY, double deltaX, double deltaY) {
   public double deltaX() {
      return this.deltaX;
   }

   public double mouseX() {
      return this.mouseX;
   }

   public MouseDragAction(Mouse mouse, double mouseX, double mouseY, double deltaX, double deltaY) {
      this.mouse = mouse;
      this.deltaX = mouseX;
      this.deltaY = mouseY;
      this.mouseY = deltaX;
      this.mouseX = deltaY;
   }

   public Mouse mouse() {
      return this.mouse;
   }

   public double deltaY() {
      return this.deltaY;
   }

   public double mouseY() {
      return this.mouseY;
   }
}
