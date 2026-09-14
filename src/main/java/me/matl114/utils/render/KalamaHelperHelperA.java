package me.matl114.utils.render;

public record KalamaHelperHelperA(int val) implements ColorQuad {
   public int Kq() {
      return this.val;
   }

   @Override
   public int get(int idx) {
      return this.val;
   }
}
