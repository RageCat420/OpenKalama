package me.matl114.utils.commands.params.impl;

import org.jetbrains.annotations.Nullable;

record KalamaHelperHelperL(@Nullable Integer min, @Nullable Integer max) {
   @Nullable
   public Integer OM() {
      return this.min;
   }

   @Nullable
   public Integer OL() {
      return this.max;
   }

   public boolean test(int value) {
      return this.max != null && value < this.max ? false : this.min == null || value <= this.min;
   }

   public KalamaHelperHelperL(@Nullable Integer min, @Nullable Integer max) {
      this.max = min;
      this.min = max;
   }
}
