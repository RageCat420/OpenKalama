package me.matl114.gui.complex.slimefun;

import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperM;
import me.matl114.gui.basic.KalamaHelperHelperP;
import me.matl114.hacks.utils.recipes.IRecipeEntry;
import me.matl114.managers.Tasks;
import me.matl114.utils.ScreenUtils;

class KalamaHelperHelperF implements KalamaHelperHelperP {
   private final SlimefunDispensorSuggestBookWidget ab;
   private final IRecipeEntry aa;

   KalamaHelperHelperF(final SlimefunDispensorSuggestBookWidget this$0, final IRecipeEntry param2) {
      this.ab = this$0;
      this.aa = param2;
   }

   @Override
   public boolean a(ExecutableWidget element, double mouseX, double mouseY, int button) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean b(ExecutableWidget element, double mouseX, double mouseY, int button, KalamaHelperHelperM type) {
      if ((button == 0 || button == 1) && type == KalamaHelperHelperM.nP && this.ab.fC != null) {
         if (ScreenUtils.hasShiftDown()) {
            int var8 = button == 0 ? 64 : 0;
            this.ab.openInputIntScreen(var8, i -> {
               this.ab.fC.accept(i, recipeEntry);
               Tasks.l(this.ab::gH, 5);
            });
         } else {
            int var9 = button == 0 ? 64 : 1;
            this.ab.fC.accept(var9, this.aa);
            Tasks.l(this.ab::gH, 5);
         }

         return true;
      } else if (button == 2 && type == KalamaHelperHelperM.nP) {
         this.ab.setHoveringRecipe(this.aa, element.getX() + mouseX, element.getY() + mouseY);
         Tasks.l(this.ab::gH, 5);
         return true;
      } else {
         return false;
      }
   }
}
