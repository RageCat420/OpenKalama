package me.matl114.gui.basic;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;

public interface Draggable extends Element {
   boolean startDrag(Screen var1, double var2, double var4);

   boolean isDragging();

   void releaseDrag(Screen var1, double var2, double var4);
}
