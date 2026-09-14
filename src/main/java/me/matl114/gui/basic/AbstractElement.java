package me.matl114.gui.basic;

import java.util.ArrayList;
import java.util.List;
import me.matl114.versioned.api.VDrawContext;

public class AbstractElement implements ElementHandler {
   List<KalamaHelperHelperP> br;
   List<RenderHandler> bp = null;
   protected boolean showTooltips;
   List<RenderHandler> bq = null;

   public AbstractElement cE(RenderHandler handlerAbsolute) {
      if (handlerAbsolute == null) {
         return this;
      } else {
         if (this.bq == null) {
            this.bq = new ArrayList<>();
         }

         if (handlerAbsolute instanceof TooltipHandler var2) {
            this.bq.removeIf(i -> i instanceof TooltipHandler);
         }

         this.bq.add(handlerAbsolute);
         return this;
      }
   }

   public AbstractElement aO(TooltipHandler handler) {
      return this.cE(handler);
   }

   public boolean onTyped(ExecutableWidget widget, char chr, int modifiers) {
      if (this.br != null) {
         for (KalamaHelperHelperP var5 : this.br) {
            if (var5.e(widget, chr, modifiers)) {
               return true;
            }
         }
      }

      return false;
   }

   public void i(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
   }

   public AbstractElement cD(RenderHandler handler) {
      if (this.bp == null) {
         this.bp = new ArrayList<>();
      }

      this.bp.add(handler);
      return this;
   }

   public boolean onAction(ExecutableWidget element, double mouseX, double mouseY, int button, KalamaHelperHelperM type) {
      if (this.br != null) {
         for (KalamaHelperHelperP var9 : this.br) {
            if (var9.b(element, mouseX, mouseY, button, type)) {
               return true;
            }
         }
      }

      return type == KalamaHelperHelperM.nP && this.a(element, mouseX, mouseY, button);
   }

   public boolean onScroll(ExecutableWidget widget, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (this.br != null) {
         for (KalamaHelperHelperP var11 : this.br) {
            if (var11.c(widget, mouseX, mouseY, horizontalAmount, verticalAmount)) {
               return true;
            }
         }
      }

      return false;
   }

   public AbstractElement cF(KalamaHelperHelperP handler) {
      if (this.br == null) {
         this.br = new ArrayList<>();
      }

      this.br.add(handler);
      return this;
   }

   @Override
   public final void renderAtCentered(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
      this.i(element, context, mouseX, mouseY, delta, alpha, shouldHighlight);
      if (this.bp != null) {
         for (RenderHandler var9 : this.bp) {
            var9.renderAtCentered(element, context, mouseX, mouseY, delta, alpha, shouldHighlight);
         }
      }
   }

   @Override
   public final boolean canBeSelected(DrawableWidget element) {
      return this.showTooltips;
   }

   public boolean isShowTooltips() {
      return this.showTooltips;
   }

   public AbstractElement setShowTooltips(boolean showTooltips) {
      this.showTooltips = showTooltips;
      return this;
   }

   @Override
   public final void f(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
      this.aP(element, context, mouseX, mouseY, delta, alpha, shouldHighlight);
      if (this.bq != null) {
         for (RenderHandler var9 : this.bq) {
            var9.f(element, context, mouseX, mouseY, delta, alpha, shouldHighlight);
         }
      }
   }

   public void aP(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
   }

   public AbstractElement() {
      this.br = null;
      this.showTooltips = true;
   }

   public boolean onClick(ExecutableWidget element, double mouseX, double mouseY, int button) {
      return false;
   }

   public boolean onKey(ExecutableWidget widget, int keyCode, int scanCode, int modifiers, boolean isPress) {
      if (this.br != null) {
         for (KalamaHelperHelperP var7 : this.br) {
            if (var7.d(widget, keyCode, scanCode, modifiers, isPress)) {
               return true;
            }
         }
      }

      return false;
   }
   @Override
   public boolean a(ExecutableWidget var1, double var2, double var3, int var4) {
      return this.onClick(var1, var2, var3, var4);
   }

}
