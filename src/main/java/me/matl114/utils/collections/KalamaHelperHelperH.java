package me.matl114.utils.collections;

public class KalamaHelperHelperH<S> implements Cloneable {
   public boolean state;
   public S value = (S)null;
   private static final KalamaHelperHelperH INSTANCE = new KalamaHelperHelperH();

   public KalamaHelperHelperH(S value, boolean state) {
      this.state = false;
      this.value = (S)value;
      this.state = state;
   }

   public KalamaHelperHelperH<S> wE() {
      try {
         return (KalamaHelperHelperH<S>)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new AssertionError();
      }
   }

   public static <T> KalamaHelperHelperH<T> wD() {
      return INSTANCE.wE();
   }

   public KalamaHelperHelperH() {
      this.state = false;
   }
}
