package me.matl114.accessors.gui;

import me.matl114.gui.basic.KalamaHelperHelperIX;
import me.matl114.utils.config.PropertyTracker;
import net.minecraft.client.gui.widget.ClickableWidget;

public interface TextFieldAccess {
   void setListener(PropertyTracker<TextFieldAccess, String> var1);

   void setBorderColorProvider(KalamaHelperHelperIX var1);

   boolean canStartDrag(double var1, double var3);

   void dragSelect(int var1, int var2, boolean var3);

   void resetSelect();

   static TextFieldAccess of(ClickableWidget clickableWidget) {
      return (TextFieldAccess)clickableWidget;
   }
}
