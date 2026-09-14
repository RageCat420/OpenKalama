package me.matl114.gui.basic;

public interface SubSelectable {
   DrawableWidget getSelected();

   <T extends SubSelectable> T setSelected(DrawableWidget var1);
}
