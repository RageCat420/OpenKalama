package me.matl114.utils.collections;

import java.util.AbstractList;
import java.util.List;

public class KalamaHelperHelperE<T> extends AbstractList<T> {
   int c;
   int b;
   List<T> delegate;

   @Override
   public void add(int index, T element) {
      this.delegate.add(index + this.b, (T)element);
   }

   @Override
   public T remove(int index) {
      return this.delegate.remove(index + this.b);
   }

   @Override
   public int size() {
      return this.delegate.size() - this.c - this.b;
   }

   @Override
   public T set(int index, T element) {
      return this.delegate.set(index + this.b, (T)element);
   }

   public KalamaHelperHelperE(List<T> value, int from, int to) {
      this.delegate = value;
      this.b = from;
      this.c = this.delegate.size() - to;
   }

   @Override
   public T get(int index) {
      return this.delegate.get(index + this.b);
   }
}
