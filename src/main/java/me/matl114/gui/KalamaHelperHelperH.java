package me.matl114.gui;

import me.matl114.gui.basic.DynamicSubScreenWidget;
import me.matl114.utils.config.ValueAccessor;
import me.matl114.versioned.api.VDrawContext;

class KalamaHelperHelperH extends DynamicSubScreenWidget {
   @Override
   public void render0(VDrawContext context, int mouseX, int mouseY, float delta, boolean disableSelect) {
      context.enableScissor(0, 0, this.aL, this.aM);
      super.render0(context, mouseX, mouseY, delta, disableSelect);
      context.C();
   }

   KalamaHelperHelperH(ValueAccessor var1, ValueAccessor var2, int var3, int var4) {
      super(var1, var2);
      this.aL = var3;
      this.aM = var4;
   }
   int aL;
   int aM;
}
