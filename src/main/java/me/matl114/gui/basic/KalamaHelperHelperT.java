package me.matl114.gui.basic;

import me.matl114.versioned.api.VDrawContext;

class KalamaHelperHelperT implements RenderHandler {
   private final RenderHandler ah;
   private final RenderHandler ag;

   @Override
   public void f(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
      this.ag.f(element, context, mouseX, mouseY, delta, alpha, shouldHighlight);
   }

   KalamaHelperHelperT(final RenderHandler this$0, final RenderHandler param2) {
      this.ah = this$0;
      this.ag = param2;
   }

   @Override
   public void renderAtCentered(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
      this.ah.renderAtCentered(element, context, mouseX, mouseY, delta, alpha, shouldHighlight);
   }
}
