package me.matl114.versioned.impl;

public record KalamaHelperHelperF(int maxDurationTicks, float minSpeed, float minRelativeSpeed) {
   public float minRelativeSpeed() {
      return this.minRelativeSpeed;
   }

   public KalamaHelperHelperF(int maxDurationTicks, float minSpeed, float minRelativeSpeed) {
      this.maxDurationTicks = maxDurationTicks;
      this.minSpeed = minSpeed;
      this.minRelativeSpeed = minRelativeSpeed;
   }

   public int maxDurationTicks() {
      return this.maxDurationTicks;
   }

   public static KalamaHelperHelperF Cy(int maxDurationTicks, float minRelativeSpeed) {
      return new KalamaHelperHelperF(maxDurationTicks, 0.0F, minRelativeSpeed);
   }

   public static KalamaHelperHelperF Cx(int maxDurationTicks, float minSpeed) {
      return new KalamaHelperHelperF(maxDurationTicks, minSpeed, 0.0F);
   }

   public float minSpeed() {
      return this.minSpeed;
   }
}
