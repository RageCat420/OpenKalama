package me.matl114.gui.complex.itemEdit;

import java.util.List;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.elements.IconElement;
import me.matl114.gui.elements.LabelElement;
import me.matl114.utils.ItemStackUtils;
import me.matl114.versioned.api.VHideFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class KalamaHelperHelperF {
   ItemStack sample;

   public DrawableWidget factory(int x, int y) {
      KalamaHelperHelperCX var3 = new KalamaHelperHelperCX(x, y, 0, 0);
      var3.Q(
         DisplayWidget.instance(1, 1, 49, 19)
            .setRenderHandler(LabelElement.instance(Text.translatable("widget.gui.item-edit-screen.nbt-editor.generic.hide-flag")))
      );
      VHideFlag[] var4 = ItemStackUtils.getHideFlags();

      for (int var5 = 0; var5 < var4.length; var5++) {
         VHideFlag var6 = var4[var5];
         var3.Q(
            ExecutableWidget.instance(51 + 20 * var5, 1, 18, 18)
               .eV(
                  IconElement.co(
                        ButtonElement.bH,
                        ButtonElement.bJ,
                        ButtonAction.a(() -> var6.setHideFlag(this.sample, !var6.isHide(this.sample))),
                        bl -> var6.isHide(this.sample)
                     )
                     .aO(TooltipHandler.ap(List.of(Text.literal(var6.displayName()))))
               )
         );
      }

      return var3;
   }

   public KalamaHelperHelperF(ItemStack stack) {
      this.sample = stack.copy();
   }

   public void applyChange(ItemStack stack) {
      for (VHideFlag var5 : ItemStackUtils.getHideFlags()) {
         var5.setHideFlag(stack, var5.isHide(this.sample));
      }
   }
}
