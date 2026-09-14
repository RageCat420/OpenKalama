package me.matl114.gui;

import java.awt.Color;
import java.util.function.Predicate;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.KalamaHelperHelperIX;
import me.matl114.gui.elements.IconElement;
import net.minecraft.util.Identifier;

public abstract class KalamaHelperHelperD<B extends KalamaHelperHelperD<B>> extends KalamaHelperHelperAX<B> {
   public KalamaHelperHelperIX o;
   public Predicate<IconElement> n;
   public Identifier j;
   public Identifier k;
   public ButtonAction i = ButtonAction.c();
   public boolean m = true;
   public Color p = Color.WHITE;
   public boolean l;

   public KalamaHelperHelperD<B> z(Identifier activeId) {
      this.k = activeId;
      return this;
   }

   public KalamaHelperHelperD<B> A(boolean guiTexture) {
      this.l = guiTexture;
      return this;
   }

   public KalamaHelperHelperD<B> D(KalamaHelperHelperIX highLightColor) {
      this.o = highLightColor;
      return this;
   }

   public B shaderColor(int shaderColor) {
      this.p = new Color(shaderColor, true);
      return this.b();
   }

   public KalamaHelperHelperD<B> C(Predicate<IconElement> activePredicate) {
      this.n = activePredicate;
      return this;
   }

   public B state(Identifier activeId, Identifier inactiveId) {
      this.k = activeId;
      this.j = inactiveId;
      return this.b();
   }

   public KalamaHelperHelperD<B> B(boolean active) {
      this.m = active;
      return this;
   }

   public KalamaHelperHelperD<B> x(ButtonAction action) {
      this.i = action;
      return this;
   }

   public B u(Runnable task) {
      this.i = task == null ? null : ButtonAction.a(task);
      return this.b();
   }

   public KalamaHelperHelperD<B> y(Identifier inactiveId) {
      this.j = inactiveId;
      return this;
   }
}
