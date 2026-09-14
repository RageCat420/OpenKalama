package me.matl114.utils.collections;

import it.unimi.dsi.fastutil.BidirectionalIterator;

class KalamaHelperHelperT<TYPE> implements BidirectionalIterator<TYPE> {
   LinkNode<TYPE> A;
   LinkNode<TYPE> B;
   LinkNode<TYPE> D;

   public boolean hasNext() {
      return this.B.c != null;
   }

   public TYPE next() {
      return (this.A = this.B = this.B.c).a;
   }

   public boolean hasPrevious() {
      return this.B != this.A;
   }

   public void remove() {
      LinkNode var1 = this.A.c;
      if (var1 != null) {
         var1.b = this.A.b;
      }

      LinkNode var2 = this.A.b == null ? this.A : this.A.b;
      if (this.B == this.A) {
         this.B = var2;
      }

      var2.c = var1;
      this.A.b = this.A.c = null;
      this.A = null;
   }

   KalamaHelperHelperT(LinkNode var1, LinkNode var2) {
      this.A = var1;
      this.D = var2;
      this.B = this.A;
      this.B.c = this.D;
   }

   public TYPE previous() {
      this.A = this.B;
      this.B = this.B.b;
      if (this.B == null) {
         this.B = this.A;
      }

      return this.A.a;
   }
}
