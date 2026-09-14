package me.matl114.gui.elements;

import me.matl114.gui.basic.AbstractElement;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperM;
import me.matl114.gui.basic.RenderHandler;
import me.matl114.utils.config.ValueAccessor;
import me.matl114.versioned.api.VDrawContext;

public class AdvancedScrollElement extends AbstractElement {
   boolean backGround = false;
   double startDragDeltaY;
   ValueAccessor<Integer> bB;
   ValueAccessor<Integer> bA;
   ValueAccessor<Double> bC;
   private static final double MIN_RENDER_PERCENTAGE = 0.05;

   public double cS(int elementHeight) {
      double var2 = this.cR(elementHeight);
      return this.cM() * var2;
   }

   @Override
   public boolean onAction(ExecutableWidget element, double mouseX, double mouseY, int button, KalamaHelperHelperM type) {
      if (this.shouldActive()) {
         if (type == KalamaHelperHelperM.nP && element.isMouseOver(mouseX, mouseY) && !this.isMouseOverBar(element, mouseY)) {
         }

         if (type == KalamaHelperHelperM.nR) {
            if (element.isMouseOver(mouseX, mouseY)) {
               if (this.isMouseOverBar(element, mouseY)) {
                  double var14 = this.cS(element.getHeight());
                  this.startDragDeltaY = mouseY - var14 - element.getY();
               } else {
                  double var15 = mouseY - element.getY();
                  double var10 = var15 / this.bA.getValue().intValue();
                  double var12 = this.cQ(element.getHeight());
                  this.startDragDeltaY = var12 * var10;
                  this.locateBarAt(var15 - this.startDragDeltaY, element.getHeight());
               }

               return true;
            } else {
               return false;
            }
         } else {
            if (type == KalamaHelperHelperM.nS) {
               double var8 = mouseY - this.startDragDeltaY - element.getY();
               this.locateBarAt(var8, element.getHeight());
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public AdvancedScrollElement(ValueAccessor<Integer> screenY, ValueAccessor<Integer> allY, ValueAccessor<Double> percentage) {
      this.bA = screenY;
      this.bB = allY;
      this.bC = percentage;
   }

   public AdvancedScrollElement setBackGround(boolean backGround) {
      this.backGround = backGround;
      return this;
   }

   public void renderCentered0(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
      if (this.backGround || this.shouldActive()) {
         int var12;
         int var13;
         if ((double)(Object)this.bA.getValue().intValue() / this.bB.getValue().intValue() < 0.05) {
            double var8 = this.cS(element.getTextureHeight());
            double var10 = this.bA.getValue().intValue() * 0.05;
            if (var8 + var10 < this.bA.getValue().intValue()) {
               var12 = (int)var8;
               var13 = (int)(var8 + var10);
            } else {
               var13 = this.bA.getValue();
               var12 = (int)(var13 - var10);
            }
         } else {
            double var14 = this.cS(element.getTextureHeight());
            double var15 = this.bA.getValue().intValue() - this.cR(element.getTextureHeight());
            var12 = (int)var14;
            var13 = (int)(var14 + var15);
         }

         context.fill(1, var12 + 1, element.getTextureWidth() - 1, var13 - 1, -8355712);
         if (element.isDragging() || element.isMouseOver(mouseX, mouseY) && this.isMouseOverBar(element, mouseY)) {
            RenderHandler.K(context, 0, var12, element.getTextureWidth(), var13 - var12, -1);
         }
      }
   }

   private boolean isMouseOverBar(DrawableWidget element, double mouseY) {
      mouseY -= element.getY();
      double var4 = this.cS(element.getHeight());
      double var6 = this.cQ(element.getHeight());
      return mouseY >= var4 && mouseY <= var4 + var6;
   }

   @Override
   public boolean onScroll(ExecutableWidget widget, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (this.shouldActive()) {
         double var10 = this.cS(widget.getHeight());
         this.locateBarAt(var10 - verticalAmount * 5.0, widget.getHeight());
         return true;
      } else {
         return false;
      }
   }

   public double cR(int elementHeight) {
      return elementHeight * (1.0 - this.cP());
   }

   public void locateBarAt(double location, int height) {
      double var4 = Math.clamp(location / this.cR(height), 0.0, 1.0);
      this.setPercentage(var4);
   }

   public double cP() {
      return Math.min(1.0, (double)(Object)this.bA.getValue().intValue() / this.bB.getValue().intValue());
   }

   public double cM() {
      return Math.clamp(this.bC.getValue(), 0.0, 1.0);
   }

   public double cQ(int elementHeight) {
      return elementHeight * this.cP();
   }

   public boolean shouldActive() {
      return this.bA.getValue() < this.bB.getValue();
   }

   public void setPercentage(double percentage) {
      this.bC.setValue(Math.clamp(percentage, 0.0, 1.0));
   }
}
