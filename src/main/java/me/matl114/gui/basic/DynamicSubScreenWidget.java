package me.matl114.gui.basic;

import me.matl114.utils.config.ValueAccessor;

public class DynamicSubScreenWidget extends KalamaHelperHelperCX {
   ValueAccessor<Integer> ck;
   ValueAccessor<Integer> cj;
   ValueAccessor<Float> cl;

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
         return true;
      } else if (this.isMouseOver(mouseX, mouseY)) {
         this.ck.setValue(this.ck.getValue() + (int)(verticalAmount * 10.0));
         return true;
      } else {
         return false;
      }
   }

   @Override
   public int getY() {
      return this.ck.getValue();
   }

   public DynamicSubScreenWidget(ValueAccessor<Integer> xCoord, ValueAccessor<Integer> yCoord, ValueAccessor<Float> scale) {
      super(0, 0, 0, 0);
      this.cj = xCoord;
      this.ck = yCoord;
      this.cl = scale;
   }

   @Override
   public float getTextureScale() {
      return this.cl.getValue();
   }

   public DynamicSubScreenWidget(ValueAccessor<Integer> xCoord, ValueAccessor<Integer> yCoord) {
      this(xCoord, yCoord, ValueAccessor.of(1.0F));
   }

   @Override
   public int getX() {
      return this.cj.getValue();
   }

   @Override
   public <T extends DrawableWidget> T setTextureScale(float scale) {
      this.cl.setValue(scale);
      return (T)(Object)this;
   }
}
