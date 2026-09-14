package me.matl114.utils.collections;

import it.unimi.dsi.fastutil.BidirectionalIterator;

public class LinkNode<TYPE> {
   public LinkNode<TYPE> b;
   public TYPE a;
   public LinkNode<TYPE> c;

   public LinkNode<TYPE> b(TYPE value) {
      this.c = new LinkNode<>((TYPE)value, this.c);
      this.c.b = this;
      return this.c;
   }

   public static <TYPE> BidirectionalIterator<TYPE> c(LinkNode<TYPE> curr) {
      return iter0(curr, curr.c);
   }

   public static <TYPE> LinkNode<TYPE> createHead() {
      return new LinkNode<>(null);
   }

   private static <TYPE> BidirectionalIterator<TYPE> iter0(LinkNode<TYPE> origin0, LinkNode<TYPE> curr) {
      return new KalamaHelperHelperT(origin0, curr);
   }

   public LinkNode(TYPE v) {
      this.a = (TYPE)v;
   }

   public LinkNode(TYPE v, LinkNode<TYPE> next) {
      this.a = (TYPE)v;
      this.c = next;
      if (next != null) {
         next.b = this;
      }
   }
}
