package me.matl114.gui.basic;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import me.matl114.gui.elements.AdvancedScrollElement;
import me.matl114.utils.config.ValueAccessor;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;

public class ScrollableListWidget extends DrawableWidget implements SubSelectable {
   protected KalamaHelperHelperCX am;
   DrawableWidget an;
   ExecutableWidget al;
   DrawableWidget ao;
   double percentage = 0.0;
   protected List<DrawableWidget> ap = new ArrayList<>();
   int maxHeight;

   private void resizePose(double percentage) {
      this.percentage = MathHelper.clamp(percentage, 0.0, 1.0);
   }

   @Override
   public void renderInDefaultMatrix(VDrawContext context, int mouseX, int mouseY, float delta, boolean disableSelect) {
      super.renderInDefaultMatrix(context, mouseX, mouseY, delta, disableSelect);
      int var6 = mouseX - this.getX();
      int var7 = this.getCurrentPose();
      int var8 = mouseY - this.getY() + var7;
      boolean var9 = false;

      for (DrawableWidget var11 : this.ap) {
         if (var11.getY() + var11.getHeight() > var7 && var11.getY() < var7 + this.getHeight()) {
            if (this.selected) {
               boolean var12 = true;
               if (!var9 && var11.canSelect() && var11.isMouseOver(var6, var8)) {
                  var12 = false;
                  var9 = true;
               }

               var11.render0(context, var6, var8, delta, var12);
            } else {
               var11.render0(context, var6, var8, delta, true);
            }
         }
      }
   }

   public ScrollableListWidget addScrollingWidget(DrawableWidget widget) {
      Preconditions.checkArgument(!(widget instanceof ScrollableListWidget), "Recursive Scrolling is not supported");
      this.ap.add(widget);
      this.aP();
      return this;
   }

   @Override
   public void setFocused(boolean focused) {
      super.setFocused(focused);
      if (this.an != null) {
         this.an.setFocused(focused);
      }
   }

   @Override
   public boolean startDrag(Screen screen, double mouseX, double mouseY) {
      if (this.al != null && this.al.startDrag(screen, mouseX, mouseY)) {
         this.ao = this.al;
         return true;
      } else {
         if (this.isMouseOver(mouseX, mouseY)) {
            double var6 = mouseX - this.getX();
            int var8 = this.getCurrentPose();
            double var9 = mouseY - this.getY() + var8;

            for (DrawableWidget var12 : this.ap) {
               if (var12.startDrag(screen, var6, var9)) {
                  this.ao = var12;
                  return true;
               }
            }
         }

         return this.am.startDrag(screen, mouseX, mouseY);
      }
   }

   @Override
   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      if (this.ao != null) {
         if (this.ao.isDragging()) {
            if (this.ao == this.al) {
               return this.ao.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            } else {
               int var10 = this.getCurrentPose();
               return this.ao.mouseDragged(mouseX - this.getX(), mouseY - this.getY() + var10, button, deltaX, deltaY);
            }
         } else {
            return false;
         }
      } else {
         return this.am.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
      }
   }

   @Override
   public DrawableWidget getSelected() {
      return this.an;
   }

   @Override
   public void releaseDrag(Screen screen, double mouseX, double mouseY) {
      if (this.ao != null) {
         if (this.ao == this.al) {
            this.ao.releaseDrag(screen, mouseX, mouseY);
            this.ao = null;
            return;
         }

         int var6 = this.getCurrentPose();
         this.ao.releaseDrag(screen, mouseX - this.getX(), mouseY - this.getY() + var6);
         this.ao = null;
      } else {
         this.am.releaseDrag(screen, mouseX, mouseY);
      }
   }

   @Override
   public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
      if (this.am != null && this.am.keyReleased(keyCode, scanCode, modifiers)) {
         return true;
      } else {
         for (DrawableWidget var5 : this.ap) {
            if (var5.keyReleased(keyCode, scanCode, modifiers)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public boolean isFocused() {
      return this.an != null && this.an.isFocused();
   }

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.am != null && this.am.keyPressed(keyCode, scanCode, modifiers)) {
         return true;
      } else {
         for (DrawableWidget var5 : this.ap) {
            if (var5.keyPressed(keyCode, scanCode, modifiers)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public boolean charTyped(char chr, int modifiers) {
      if (this.am != null && this.am.charTyped(chr, modifiers)) {
         return true;
      } else {
         for (DrawableWidget var4 : this.ap) {
            if (var4.charTyped(chr, modifiers)) {
               return true;
            }
         }

         return false;
      }
   }

   public int getCurrentPose() {
      return this.percentage == 0.0 ? 0 : (int)(this.percentage * Math.max(this.maxHeight - this.getHeight(), 0));
   }

   private double getPercentage() {
      return this.percentage;
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
      if (this.isMouseOver(mouseX, mouseY) || this.al != null && this.al.isMouseOver(mouseX, mouseY)) {
         this.al.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
      }

      return false;
   }

   private void aT() {
      AdvancedScrollElement var1 = new AdvancedScrollElement(
            ValueAccessor.ofIgnore(this::getHeight), ValueAccessor.ofIgnore(() -> this.maxHeight), ValueAccessor.of(this::getPercentage, this::resizePose)
         )
         .setBackGround(true);
      this.al = new ExecutableWidget(this.getX() + this.dx, this.getY(), 12, this.dy).eV(var1);
      this.am = new KalamaHelperHelperCX(this.getX(), this.getY(), this.dx, this.dy);
   }

   public KalamaHelperHelperCX aV() {
      return this.am;
   }

   public ScrollableListWidget aS(DrawableWidget widget) {
      this.ap.remove(widget);
      this.aP();
      return this;
   }

   public ScrollableListWidget aU() {
      this.ap.clear();
      this.aP();
      return this;
   }

   @Override
   public boolean canSelect() {
      return true;
   }

   @Override
   public <T extends SubSelectable> T setSelected(DrawableWidget subWidget) {
      if (this.an != null) {
         this.an.setFocused(false);
      }

      this.an = subWidget;
      if (this.an != null && super.isFocused()) {
         this.an.setFocused(true);
      }

      return (T)(Object)this;
   }

   @Override
   public void render0(VDrawContext context, int mouseX, int mouseY, float delta, boolean disableSelect) {
      this.selected = !disableSelect && this.isMouseOver(mouseX, mouseY);
      if (this.al != null) {
         this.al.render0(context, mouseX, mouseY, delta, disableSelect);
      }

      if (this.am != null) {
         this.am.render0(context, mouseX, mouseY, delta, disableSelect);
      }

      context.f().pushMatrix();
      context.enableScissor(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight());
      int var6 = this.getCurrentPose();
      context.f().translate(this.getX(), this.getY() - var6);
      if (this.priority != 0) {
         context.d(this.priority);
      }

      float var7 = this.getTextureScale();
      if (var7 != 1.0F) {
         context.f().scale(var7, var7);
      }

      this.renderInDefaultMatrix(context, mouseX, mouseY, delta, disableSelect);
      context.C();
      if (this.priority != 0) {
         context.e();
      }

      context.f().popMatrix();
      this.renderAbsolute(context, mouseX, mouseY, delta, disableSelect);
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.am != null && this.am.mouseClicked(mouseX, mouseY, button)) {
         this.setSelected(this.am);
         return true;
      } else {
         if (this.isMouseOver(mouseX, mouseY)) {
            double var6 = mouseX - this.getX();
            int var8 = this.getCurrentPose();
            double var9 = mouseY - this.getY() + var8;

            for (DrawableWidget var12 : this.ap) {
               if (var12.mouseClicked(var6, var9, button)) {
                  this.setSelected(var12);
                  return true;
               }
            }
         }

         return false;
      }
   }

   @Override
   public boolean isDragging() {
      return this.ao != null ? this.ao.isDragging() : this.am.isDragging();
   }

   public ScrollableListWidget(int x, int y, int dx, int dy) {
      super(x, y, dx, dy);
      this.aP();
      this.aT();
   }

   private void aP() {
      this.maxHeight = this.dy;

      for (DrawableWidget var2 : this.ap) {
         int var3 = var2.getY() + var2.getHeight();
         if (var3 > this.maxHeight) {
            this.maxHeight = var3;
         }
      }

      this.resizePose(this.getPercentage());
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      if (this.am != null && this.am.mouseReleased(mouseX, mouseY, button)) {
         return true;
      } else {
         if (this.isMouseOver(mouseX, mouseY)) {
            double var6 = mouseX - this.getX();
            int var8 = this.getCurrentPose();
            double var9 = mouseY - this.getY() + var8;

            for (DrawableWidget var12 : this.ap) {
               if (var12.mouseReleased(var6, var9, button)) {
                  return true;
               }
            }
         }

         return false;
      }
   }
}
