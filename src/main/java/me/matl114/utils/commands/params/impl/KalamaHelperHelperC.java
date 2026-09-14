package me.matl114.utils.commands.params.impl;

import org.jetbrains.annotations.Nullable;

record KalamaHelperHelperC(@Nullable Double min, @Nullable Double max) {
   @Nullable
   public Double tx() {
      return this.min;
   }

   public double tv(double fallback) {
      return this.min == null ? fallback : this.min;
   }

   public double tu(double fallback) {
      return this.max == null ? fallback : this.max;
   }

   public boolean testSquared(double squared) {
      return this.max != null && squared < this.max * this.max ? false : this.min == null || squared <= this.min * this.min;
   }

   @Nullable
   public Double tw() {
      return this.max;
   }
}
