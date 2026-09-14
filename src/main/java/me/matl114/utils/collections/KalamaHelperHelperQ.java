package me.matl114.utils.collections;

import java.util.Iterator;
import java.util.List;

public class KalamaHelperHelperQ<T> implements Iterator<T> {
   int index;
   List<T> delegate;

   public KalamaHelperHelperQ(List<T> delegate) {
      this.delegate = delegate;
      this.index = 0;
   }

   @Override
   public void remove() {
      this.delegate.remove(--this.index);
   }

   @Override
   public boolean hasNext() {
      return this.index < this.delegate.size();
   }

   @Override
   public T next() {
      return this.delegate.get(this.index++);
   }
}
