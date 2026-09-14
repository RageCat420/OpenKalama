package me.matl114.utils.render;

public record KalamaHelperHelperF(int val1, int val2, int val3, int val4) implements ColorQuad {
   public int val2() {
      return this.val2;
   }

   public int val3() {
      return this.val3;
   }

   public KalamaHelperHelperF(int val1, int val2, int val3, int val4) {
      this.val2 = val1;
      this.val4 = val2;
      this.val1 = val3;
      this.val3 = val4;
   }

   public int val4() {
      return this.val4;
   }

   @Override
   public int get(int idx) {
      return switch (idx & 3) {
         case 0 -> this.val2;
         case 1 -> this.val4;
         case 2 -> this.val1;
         case 3 -> this.val3;
         default -> throw new IndexOutOfBoundsException();
      };
   }

   public int val1() {
      return this.val1;
   }
}
