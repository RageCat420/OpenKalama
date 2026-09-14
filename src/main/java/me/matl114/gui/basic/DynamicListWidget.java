package me.matl114.gui.basic;

import java.util.ArrayList;
import java.util.List;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.client.gui.screen.Screen;

public class DynamicListWidget extends DrawableWidget implements SubSelectable {
   protected DrawableWidget B;
   private final List<DrawableWidget> eI = new ArrayList<>();
   protected DrawableWidget C;

   @Override
   public boolean isMouseOver(double mouseX, double mouseY) {
      int var5 = 0;

      for (DrawableWidget var7 : this.eI) {
         float var8 = this.getTextureScale();
         if (var7.isMouseOver((mouseX - this.getX()) / var8, (mouseY - this.getY()) / var8 - var5)) {
            return true;
         }

         var5 += var7.getHeight();
      }

      return false;
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      double var9 = mouseX - this.getX();
      double var11 = mouseY - this.getY();
      float var13 = this.getTextureScale();
      if (var13 != 1.0F) {
         var9 = (int)(var9 / var13);
         var11 = (int)(var11 / var13);
      }

      int var14 = 0;

      for (DrawableWidget var16 : this.eI) {
         if (var16.mouseScrolled(var9, var11 - var14, horizontalAmount, verticalAmount)) {
            return true;
         }

         var14 += var16.getY() + var16.getHeight();
      }

      return false;
   }

   @Override
   public boolean isDragging() {
      return this.C != null && this.C.isDragging();
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      double var6 = mouseX - this.getX();
      double var8 = mouseY - this.getY();
      float var10 = this.getTextureScale();
      if (var10 != 1.0F) {
         var6 = (int)(var6 / var10);
         var8 = (int)(var8 / var10);
      }

      int var11 = 0;

      for (DrawableWidget var13 : this.eI) {
         if (var13.mouseClicked(var6, var8 - var11, button)) {
            this.setSelected(var13);
            return true;
         }

         var11 += var13.getY() + var13.getHeight();
      }

      this.setSelected(null);
      return false;
   }

   @Override
   public void releaseDrag(Screen screen, double mouseX, double mouseY) {
      if (this.C != null) {
         int var6 = 0;

         for (DrawableWidget var8 : this.eI) {
            if (var8 == this.C) {
               break;
            }

            var6 += var8.getY() + var8.getHeight();
         }

         double var9 = mouseX - this.getX();
         double var11 = mouseY - this.getY();
         float var13 = this.getTextureScale();
         if (var13 != 1.0F) {
            var9 = (int)(var9 / var13);
            var11 = (int)(var11 / var13);
         }

         this.C.releaseDrag(screen, var9, var11 - var6);
      }
   }

   public DynamicListWidget(int x, int y, int width) {
      super(x, y, width, width);
      this.B = null;
      this.C = null;
   }

   @Override
   public void renderInDefaultMatrix(VDrawContext context, int mouseX, int mouseY, float delta, boolean disableSelect) {
      super.renderInDefaultMatrix(context, mouseX, mouseY, delta, disableSelect);
      int var6 = mouseX - this.getX();
      int var7 = mouseY - this.getY();
      float var8 = this.getTextureScale();
      if (var8 != 1.0F) {
         var6 = (int)(var6 / var8);
         var7 = (int)(var7 / var8);
      }

      DrawableWidget var9 = null;
      if (this.isSelected()) {
         int var10 = 0;

         for (DrawableWidget var12 : this.eI) {
            if (var12.canSelect() && var12.isMouseOver(var6, var7 - var10)) {
               var9 = var12;
               break;
            }

            var10 += var12.getY() + var12.getHeight();
         }
      }

      int var15 = 0;
      context.b();

      for (DrawableWidget var17 : this.eI) {
         boolean var13 = var17 != var9;
         var17.render0(context, var6, var7 - var15, delta, var13);
         int var14 = var17.getY() + var17.getHeight();
         var15 += var14;
         if (var14 != 0) {
            context.f().translate(0.0F, var14);
         }
      }

      context.c();
   }

   public DynamicListWidget ga(DrawableWidget widget) {
      this.eI.add(widget);
      widget.setSubWidget(true);
      return this;
   }

   @Override
   public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
      for (DrawableWidget var5 : this.eI) {
         if (var5.keyReleased(keyCode, scanCode, modifiers)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      double var6 = mouseX - this.getX();
      double var8 = mouseY - this.getY();
      float var10 = this.getTextureScale();
      if (var10 != 1.0F) {
         var6 = (int)(var6 / var10);
         var8 = (int)(var8 / var10);
      }

      int var11 = 0;

      for (DrawableWidget var13 : this.eI) {
         if (var13.mouseReleased(var6, var8 - var11, button)) {
            return true;
         }

         var11 += var13.getY() + var13.getHeight();
      }

      return false;
   }

   public boolean remove(DrawableWidget widget) {
      widget.setSubWidget(false);
      return this.eI.remove(widget);
   }

   @Override
   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      if (this.C != null && this.C.isDragging()) {
         int var10 = 0;

         for (DrawableWidget var12 : this.eI) {
            if (var12 == this.C) {
               break;
            }

            var10 += var12.getY() + var12.getHeight();
         }

         float var13 = this.getTextureScale();
         return this.C.mouseDragged((mouseX - this.getX()) / var13, (mouseY - this.getY()) / var13 - var10, button, deltaX / var13, deltaY / var13);
      } else {
         return false;
      }
   }

   @Override
   public boolean charTyped(char chr, int modifiers) {
      for (DrawableWidget var4 : this.eI) {
         if (var4.charTyped(chr, modifiers)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public DrawableWidget getSelected() {
      return this.B;
   }

   @Override
   public boolean startDrag(Screen screen, double mouseX, double mouseY) {
      double var6 = mouseX - this.getX();
      double var8 = mouseY - this.getY();
      float var10 = this.getTextureScale();
      if (var10 != 1.0F) {
         var6 = (int)(var6 / var10);
         var8 = (int)(var8 / var10);
      }

      int var11 = 0;

      for (DrawableWidget var13 : this.eI) {
         if (var13.startDrag(screen, var6, var8 - var11)) {
            this.C = var13;
            return true;
         }

         var11 += var13.getY() + var13.getHeight();
      }

      return false;
   }

   @Override
   public boolean isFocused() {
      return this.B != null && this.B.isFocused();
   }

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      for (DrawableWidget var5 : this.eI) {
         if (var5.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean canSelect() {
      return true;
   }

   public void clearChildren() {
      this.eI.clear();
   }

   @Override
   public void setFocused(boolean focused) {
      super.setFocused(focused);
      if (this.B != null) {
         this.B.setFocused(focused);
      }
   }

   @Override
   public <T extends SubSelectable> T setSelected(DrawableWidget subWidget) {
      if (this.B != null) {
         this.B.setFocused(false);
      }

      this.B = subWidget;
      if (this.B != null && super.isFocused()) {
         this.B.setFocused(true);
      }

      return (T)(Object)this;
   }

   @Override
   public int getHeight() {
      int var1 = 0;

      for (DrawableWidget var3 : this.eI) {
         var1 += var3.getY() + var3.getHeight();
      }

      return var1;
   }
}
