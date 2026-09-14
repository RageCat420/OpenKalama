package me.matl114.gui.basic;

import java.util.function.UnaryOperator;
import net.minecraft.client.gui.screen.Screen;

public class ExecutableWidget extends DrawableWidget {
   boolean dragging = false;
   protected KalamaHelperHelperP handler;

   @Override
   public boolean startDrag(Screen screen, double mouseX, double mouseY) {
      if (this.handler != null && this.handler.b(this, mouseX, mouseY, 0, KalamaHelperHelperM.nR)) {
         this.dragging = true;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
      return this.handler != null && this.handler.d(this, keyCode, scanCode, modifiers, false);
   }

   public <T extends ExecutableWidget> T eU(UnaryOperator<KalamaHelperHelperP> handlerUnaryOperator) {
      this.handler = handlerUnaryOperator.apply(this.handler);
      return (T)(Object)this;
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      return this.handler != null && this.isMouseOver(mouseX, mouseY) ? this.handler.b(this, mouseX, mouseY, button, KalamaHelperHelperM.nQ) : false;
   }

   public static ExecutableWidget instance(int x, int y, int dx, int dy) {
      return new ExecutableWidget(x, y, dx, dy);
   }

   public ExecutableWidget(int x, int y, int dx, int dy) {
      super(x, y, dx, dy);
   }

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      return this.handler != null && this.handler.d(this, keyCode, scanCode, modifiers, true);
   }

   @Override
   public void releaseDrag(Screen screen, double mouseX, double mouseY) {
      this.dragging = false;
      if (this.handler != null) {
         this.handler.b(this, mouseX, mouseY, 0, KalamaHelperHelperM.nT);
      }
   }

   public <T extends ExecutableWidget> T eT(KalamaHelperHelperP handler) {
      this.handler = handler;
      return (T)(Object)this;
   }

   public KalamaHelperHelperP getHandler() {
      return this.handler;
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      return this.handler != null && this.handler.c(this, mouseX, mouseY, horizontalAmount, verticalAmount);
   }

   @Override
   public boolean charTyped(char chr, int modifiers) {
      return this.handler != null && this.handler.e(this, chr, modifiers);
   }

   public <T extends ExecutableWidget> T eV(ElementHandler handler) {
      this.eT(handler);
      this.setRenderHandler(handler);
      return (T)(Object)this;
   }

   @Override
   public boolean isDragging() {
      return this.dragging;
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      return this.handler != null && this.isMouseOver(mouseX, mouseY) ? this.handler.b(this, mouseX, mouseY, button, KalamaHelperHelperM.nP) : false;
   }

   @Override
   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      return this.handler != null ? this.handler.b(this, mouseX, mouseY, button, KalamaHelperHelperM.nS) : false;
   }
}
