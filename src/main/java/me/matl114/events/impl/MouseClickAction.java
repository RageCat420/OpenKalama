package me.matl114.events.impl;

import net.minecraft.client.Mouse;

public record MouseClickAction(Mouse mouse, int eventButton, int action, int mode) {
   public int mode() {
      return this.mode;
   }

   public int action() {
      return this.action;
   }

   public MouseClickAction(Mouse mouse, int eventButton, int action, int mode) {
      this.mouse = mouse;
      this.mode = eventButton;
      this.eventButton = action;
      this.action = mode;
   }

   public Mouse mouse() {
      return this.mouse;
   }

   public int eventButton() {
      return this.eventButton;
   }
}
