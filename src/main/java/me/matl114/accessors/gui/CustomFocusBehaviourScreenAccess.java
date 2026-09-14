package me.matl114.accessors.gui;

import net.minecraft.client.gui.Element;

public interface CustomFocusBehaviourScreenAccess {
   Element getDefaultElement();

   boolean canFocusButtonWhenClicked();

   default boolean autoSelectDefaultElementWhenNotFocused() {
      return true;
   }

   default boolean enableSwitchUsingNavigation() {
      return false;
   }
}
