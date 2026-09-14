package me.matl114.gui;

import java.util.List;
import java.util.function.Function;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;

public class GridSubScreen<W extends Element & Drawable & Selectable> extends KalamaHelperHelperCX {
   final int bI;
   final int bJ;
   final int bH;
   final int bK;
   final int bF;
   final ContentDelegateWidget<W>[] gridWidgets;
   final int bG;

   public <R> void refreshPage(List<R> values, Function<R, W> function, int page) {
      int var4 = values.size();
      int var5 = (page - 1) * this.gridWidgets.length;

      for (int var6 = 0; var6 < this.bI; var6++) {
         for (int var7 = 0; var7 < this.bH; var7++) {
            int var8 = var6 * this.bH + var7;
            if (this.gridWidgets[var8] == null) {
               this.gridWidgets[var8] = new ContentDelegateWidget(this.bJ + this.bF * var7, this.bK + this.bG * var6, this.bF, this.bG).addToSub(this);
            }

            int var9 = var5 + var8;
            if (var9 >= var4) {
               this.gridWidgets[var8].setContentDelegate(null);
            } else {
               this.gridWidgets[var8].setContentDelegate((W)function.apply(values.get(var9)));
            }
         }
      }
   }

   public GridSubScreen(int x, int y, int dx, int dy, int elementDx, int elementDy) {
      super(x, y, dx, dy);
      this.bF = elementDx;
      this.bG = elementDy;
      dx -= 2;
      this.bH = Math.max(1, dx / elementDx);
      this.bJ = 1 + dx % elementDx / 2;
      dy -= 2;
      this.bI = Math.max(1, dy / elementDy);
      this.bK = 1 + dy % elementDy / 2;
      this.gridWidgets = new ContentDelegateWidget[this.bI * this.bH];
   }

   public int getEntryPerPage() {
      return this.gridWidgets.length;
   }
}
