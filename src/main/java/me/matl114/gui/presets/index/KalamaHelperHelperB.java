package me.matl114.gui.presets.index;

import java.util.List;
import me.matl114.gui.basic.ElementHandler;

class KalamaHelperHelperB<T, W> extends IndexedSubScreen<T, W> {
   private final IndexedScreen this$0;

   @Override
   protected W bd(T val) {
      return (W)(Object)this.this$0.bd(val);
   }

   public T getGlobal() {
      return (T)(Object)this.this$0.getGlobal();
   }

   @Override
   public void setGlobal(T config) {
      this.this$0.setGlobal(config);
   }

   public void saveSelected() {
      this.this$0.bc();
   }

   KalamaHelperHelperB(final IndexedScreen this$0, List list, int x, int y, int dx, int dy, int indexDx, int indexDy) {
      super(list, x, y, dx, dy, indexDx, indexDy);
      this.this$0 = this$0;
   }

   @Override
   protected ElementHandler be(T val) {
      return this.this$0.be(val);
   }



   @Override
   public T bf() { return null; }

}
