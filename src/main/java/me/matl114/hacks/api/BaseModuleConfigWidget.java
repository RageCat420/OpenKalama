package me.matl114.hacks.api;

import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.complex.config.KalamaHelperHelperA;
import me.matl114.managers.config.Ref;

class BaseModuleConfigWidget extends KalamaHelperHelperA {
   private final BaseModule name;

   public DrawableWidget getName() {
      return this.name.createRefKeyLabel(this::eK, this::eL, this.dr, this.dy);
   }

   BaseModuleConfigWidget(final BaseModule this$0, int x, int y, int dx, int dy, int dKey, int dblank, int dvalue, Ref kv, String key) {
      super(x, y, dx, dy, dKey, dblank, dvalue, kv, key);
      this.name = this$0;
   }
}
