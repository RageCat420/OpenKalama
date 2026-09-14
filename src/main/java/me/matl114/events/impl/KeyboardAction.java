package me.matl114.events.impl;

import net.minecraft.client.Keyboard;

public record KeyboardAction(Keyboard keyboard, int keyCode, int scannCode, int action, int modifier) {
   public int action() {
      return this.action;
   }

   public int keyCode() {
      return this.keyCode;
   }

   public KeyboardAction(Keyboard keyboard, int keyCode, int scannCode, int action, int modifier) {
      this.keyboard = keyboard;
      this.action = keyCode;
      this.modifier = scannCode;
      this.scannCode = action;
      this.keyCode = modifier;
   }

   public Keyboard keyboard() {
      return this.keyboard;
   }

   public int modifier() {
      return this.modifier;
   }

   public int scannCode() {
      return this.scannCode;
   }
}
