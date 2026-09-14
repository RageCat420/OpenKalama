package me.matl114.gui;

import me.matl114.accessors.gui.TextFieldAccess;
import me.matl114.gui.basic.ContentDelegateWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;

public class KalamaHelperHelperE<T extends ClickableWidget> extends ContentDelegateWidget<T> {
   boolean Z = false;

   @Override
   public boolean isDragging() {
      return this.ef() != null && this.Z;
   }

   @Override
   public void releaseDrag(Screen screen, double mouseX, double mouseY) {
      this.Z = false;
   }

   @Override
   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      if (this.Z && this.ef() != null) {
         ClickableWidget var10 = this.ef();
         float var11 = this.getTextureScale();
         TextFieldAccess.of(var10).dragSelect((int)((mouseX - this.getX() - var10.getX()) / var11), (int)((mouseY - this.getY() - var10.getY()) / var11), true);
      }

      return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
   }

   public KalamaHelperHelperE(int x, int y, T widget) {
      super(x, y, 0, 0);
      this.setContentDelegate((T)widget);
   }

   @Override
   public boolean startDrag(Screen screen, double mouseX, double mouseY) {
      if (this.ef() != null && TextFieldAccess.of(this.ef()).canStartDrag(mouseX - this.getX(), mouseY - this.getY())) {
         this.Z = true;
         return true;
      } else {
         return false;
      }
   }
}
