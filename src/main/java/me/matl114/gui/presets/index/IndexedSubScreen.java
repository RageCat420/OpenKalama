package me.matl114.gui.presets.index;

import java.util.List;
import java.util.Objects;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.presets.lists.ListEntryWidgetController;
import me.matl114.gui.presets.lists.ListUnmodifiableWidget;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;

public class IndexedSubScreen<T, W extends Element & Drawable & Selectable> extends KalamaHelperHelperCX {
   protected int eT;
   protected int eU;
   protected ListUnmodifiableWidget eR;
   protected List<T> b;
   private ContentDelegateWidget<W> eS;
   protected T eV = (T)null;

   public void bc() { }

   public void selectIndexToDisplay(T key, boolean force) {
      if (!Objects.equals(this.eV, key) || force) {
         if (this.eV != null) {
            this.bc();
         }

         this.eV = (T)key;
         this.eS.setContentDelegate(key == null ? null : this.bd((T)key));
      }
   }

   protected IndexedSubScreen(List<T> list, int x, int y, int dx, int dy, int indexDx, int indexDy) {
      super(x, y, dx, dy);
      this.b = list;
      this.eT = indexDx;
      this.eU = indexDy;
      this.af();
   }

   public T bf() { }

   protected void af() {
      ListEntryWidgetController var1 = ListEntryWidgetController.immutable(
         this.b, str -> ExecutableWidget.instance(0, 0, this.eT, this.eU).eV(this.be(str)), this.eU, this.eT
      );
      this.eR = new ListUnmodifiableWidget(var1, 0, 0, this.eT + 4, this.dy).addToSub(this);
      this.eS = new ContentDelegateWidget(this.eT, 0, this.dx - this.eT, this.dy).addToSub(this);
      Object var2 = this.bf();
      this.selectIndexToDisplay((T)var2, false);
   }

   protected abstract W bd(T var1);

   protected abstract ElementHandler be(T var1);

   public W gl() {
      return this.eS.ef();
   }

   public void setGlobal(T var1) { }
}
