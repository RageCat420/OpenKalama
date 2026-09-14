package me.matl114.gui.complex;

import me.matl114.gui.basic.AbstractElement;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.utils.ScreenUtils;

public class BoxElement extends AbstractElement {
   private final ButtonAction action;

   @Override
   public boolean onKey(ExecutableWidget widget, int keyCode, int scanCode, int modifiers, boolean isPress) {
      return this.action != null && widget.isSelected() && ScreenUtils.l(keyCode)
         ? this.action.d(this, widget, 0)
         : super.onKey(widget, keyCode, scanCode, modifiers, isPress);
   }

   public BoxElement(ButtonAction action) {
      this.action = action;
   }

   @Override
   public boolean onClick(ExecutableWidget element, double mouseX, double mouseY, int button) {
      return this.action != null && this.action.d(this, element, button);
   }
}
