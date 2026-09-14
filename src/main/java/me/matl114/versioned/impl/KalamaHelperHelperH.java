package me.matl114.versioned.impl;

import java.util.Optional;

public record KalamaHelperHelperH(
   int contactCooldownTicks,
   int delayTicks,
   float forwardMovement,
   float damageMultiplier,
   Optional<KalamaHelperHelperF> dismountConditions,
   Optional<KalamaHelperHelperF> knockbackConditions,
   Optional<KalamaHelperHelperF> damageConditions
) {
   public float damageMultiplier() {
      return this.damageMultiplier;
   }

   public float forwardMovement() {
      return this.forwardMovement;
   }

   public Optional<KalamaHelperHelperF> damageConditions() {
      return this.damageConditions;
   }

   public Optional<KalamaHelperHelperF> knockbackConditions() {
      return this.knockbackConditions;
   }

   public Optional<KalamaHelperHelperF> dismountConditions() {
      return this.dismountConditions;
   }

   public int contactCooldownTicks() {
      return this.contactCooldownTicks;
   }

   public KalamaHelperHelperH(
      int contactCooldownTicks,
      int delayTicks,
      float forwardMovement,
      float damageMultiplier,
      Optional<KalamaHelperHelperF> dismountConditions,
      Optional<KalamaHelperHelperF> knockbackConditions,
      Optional<KalamaHelperHelperF> damageConditions
   ) {
      this.contactCooldownTicks = contactCooldownTicks;
      this.delayTicks = delayTicks;
      this.damageMultiplier = forwardMovement;
      this.forwardMovement = damageMultiplier;
      this.dismountConditions = dismountConditions;
      this.knockbackConditions = knockbackConditions;
      this.damageConditions = damageConditions;
   }

   public int delayTicks() {
      return this.delayTicks;
   }
}
