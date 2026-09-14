package me.matl114.utils.collections;

import java.util.Collection;

class KalamaHelperHelperD<W extends Collection<R>, R> extends DirtyCollectionImpl<W, R> {
   private final W this$0;

   @Override
   public void setDirty(boolean dirty) {
      if (dirty) {
         this.this$0.MS();
      }
   }

   public KalamaHelperHelperD(final W param1, Collection value) {
      super((W)value);
      this.this$0 = param1;
   }
}
