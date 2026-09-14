package me.matl114.gui.basic;

import me.matl114.versioned.api.VDrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.Selectable.SelectionType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;

public class ContentDelegateWidget<W extends Element & Drawable & Selectable> extends DrawableWidget implements Draggable {
   private W delegate;

   @Override
   public boolean isDragging() {
      return this.ef() != null && this.ef() instanceof Draggable var2 && var2.isDragging();
   }

   @Override
   public boolean isMouseOver(double mouseX, double mouseY) {
      float var5 = this.getTextureScale();
      return this.ef() != null && this.ef().isMouseOver((mouseX - this.getX()) / var5, (mouseY - this.getY()) / var5);
   }

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      return this.ef() != null && this.ef().keyPressed(keyCode, scanCode, modifiers);
   }

   @Override
   public void mouseMoved(double mouseX, double mouseY) {
      if (this.ef() != null) {
         int var5 = (int)(mouseX - this.getX());
         int var6 = (int)(mouseY - this.getY());
         float var7 = this.getTextureScale();
         if (var7 != 1.0F) {
            var5 = (int)(var5 / var7);
            var6 = (int)(var6 / var7);
         }

         this.ef().mouseMoved(var5, var6);
      }
   }

   @Override
   public boolean charTyped(char chr, int modifiers) {
      return this.ef() != null && this.ef().charTyped(chr, modifiers);
   }

   public ContentDelegateWidget(int x, int y, int dx, int dy) {
      super(x, y, dx, dy);
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      if (this.ef() != null) {
         int var6 = (int)(mouseX - this.getX());
         int var7 = (int)(mouseY - this.getY());
         float var8 = this.getTextureScale();
         if (var8 != 1.0F) {
            var6 = (int)(var6 / var8);
            var7 = (int)(var7 / var8);
         }

         if (this.ef().mouseReleased(var6, var7, button)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void renderInDefaultMatrix(VDrawContext context, int mouseX, int mouseY, float delta, boolean disableSelect) {
      super.renderInDefaultMatrix(context, mouseX, mouseY, delta, disableSelect);
      if (this.ef() != null) {
         int var6 = mouseX - this.getX();
         int var7 = mouseY - this.getY();
         float var8 = this.getTextureScale();
         if (var8 != 1.0F) {
            var6 = (int)(var6 / var8);
            var7 = (int)(var7 / var8);
         }

         if (this.ef() instanceof DrawableWidget var10) {
            var10.render0(context, var6, var7, delta, disableSelect);
         } else {
            this.ef().render(context.b(), var6, var7, delta);
            context.c();
         }
      }
   }

   @Override
   public void setFocused(boolean focused) {
      if (this.ef() != null) {
         this.ef().setFocused(focused);
      }
   }

   @Override
   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      if (this.ef() != null) {
         int var10 = (int)(mouseX - this.getX());
         int var11 = (int)(mouseY - this.getY());
         float var12 = this.getTextureScale();
         if (var12 != 1.0F) {
            var10 = (int)(var10 / var12);
            var11 = (int)(var11 / var12);
         }

         if (this.ef().mouseDragged(var10, var11, button, deltaX * var12, deltaY * var12)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.ef() != null) {
         int var6 = (int)(mouseX - this.getX());
         int var7 = (int)(mouseY - this.getY());
         float var8 = this.getTextureScale();
         if (var8 != 1.0F) {
            var6 = (int)(var6 / var8);
            var7 = (int)(var7 / var8);
         }

         if (this.ef().mouseClicked(var6, var7, button)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (this.ef() != null) {
         int var9 = (int)(mouseX - this.getX());
         int var10 = (int)(mouseY - this.getY());
         float var11 = this.getTextureScale();
         if (var11 != 1.0F) {
            var9 = (int)(var9 / var11);
            var10 = (int)(var10 / var11);
         }

         if (this.ef().mouseScrolled(var9, var10, horizontalAmount, verticalAmount)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public SelectionType getType() {
      return this.ef() == null ? SelectionType.NONE : this.ef().getType();
   }

   public ContentDelegateWidget<W> setContentDelegate(W delegate) {
      this.delegate = (W)delegate;
      return this;
   }

   @Override
   public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
      return this.ef() != null && this.ef().keyReleased(keyCode, scanCode, modifiers);
   }

   @Override
   public void releaseDrag(Screen screen, double mouseX, double mouseY) {
      if (this.ef() instanceof Draggable var7) {
         float var8 = this.getTextureScale();
         var7.releaseDrag(screen, (mouseX - this.getX()) / var8, (mouseY - this.getY()) / var8);
      }
   }

   @Override
   public boolean isFocused() {
      return this.ef() != null && this.ef().isFocused();
   }

   @Override
   public int getWidth() {
      return this.ef() instanceof Widget var2 ? var2.getWidth() : this.dx;
   }

   @Override
   public boolean canSelect() {
      return this.ef() != null && !(this.ef() instanceof DrawableWidget var2 && !var2.canSelect());
   }

   @Override
   public boolean startDrag(Screen screen, double mouseX, double mouseY) {
      if (this.ef() instanceof Draggable var7) {
         float var8 = this.getTextureScale();
         return var7.startDrag(screen, (mouseX - this.getX()) / var8, (mouseY - this.getY()) / var8);
      } else {
         return false;
      }
   }

   public W ef() {
      return this.delegate;
   }

   @Override
   public int getHeight() {
      return this.ef() instanceof Widget var2 ? var2.getHeight() : this.dy;
   }
}
