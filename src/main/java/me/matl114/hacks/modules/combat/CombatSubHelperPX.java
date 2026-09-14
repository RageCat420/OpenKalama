package me.matl114.hacks.modules.combat;

public record CombatSubHelperPX(
   boolean useTp,
   boolean maceSwap,
   boolean invSwap,
   boolean selectWeapon,
   boolean antiShieldSwap,
   boolean useAttack,
   boolean elytraDelaySwitch,
   boolean criticalSprint,
   boolean maceVClip,
   boolean swingHand
) {
   public boolean elytraDelaySwitch() {
      return this.elytraDelaySwitch;
   }

   public CombatSubHelperPX yZ(boolean selectWeapon) {
      return this.invSwap == selectWeapon
         ? this
         : new CombatSubHelperPX(this.selectWeapon, this.criticalSprint, this.elytraDelaySwitch, selectWeapon, this.antiShieldSwap, this.useTp, this.useAttack, this.swingHand, this.maceSwap, this.maceVClip);
   }

   public boolean yV() {
      return !this.useAttack && !this.useTp;
   }

   public boolean swingHand() {
      return this.swingHand;
   }

   public CombatSubHelperPX za(boolean antiShieldSwap) {
      return this.antiShieldSwap == antiShieldSwap
         ? this
         : new CombatSubHelperPX(this.selectWeapon, this.criticalSprint, this.elytraDelaySwitch, this.invSwap, antiShieldSwap, this.useTp, this.useAttack, this.swingHand, this.maceSwap, this.maceVClip);
   }

   public boolean useTp() {
      return this.useTp;
   }

   public CombatSubHelperPX zb(boolean useAttack) {
      return this.useTp == useAttack ? this : new CombatSubHelperPX(this.selectWeapon, this.criticalSprint, this.elytraDelaySwitch, this.invSwap, this.antiShieldSwap, useAttack, this.useAttack, this.swingHand, this.maceSwap, this.maceVClip);
   }

   public CombatSubHelperPX zf(boolean swingHand) {
      return this.maceVClip == swingHand ? this : new CombatSubHelperPX(this.selectWeapon, this.criticalSprint, this.elytraDelaySwitch, this.invSwap, this.antiShieldSwap, this.useTp, this.useAttack, this.swingHand, this.maceSwap, swingHand);
   }

   public boolean invSwap() {
      return this.invSwap;
   }

   public CombatSubHelperPX zc(boolean elytraDelaySwitch) {
      return this.useAttack == elytraDelaySwitch
         ? this
         : new CombatSubHelperPX(this.selectWeapon, this.criticalSprint, this.elytraDelaySwitch, this.invSwap, this.antiShieldSwap, this.useTp, elytraDelaySwitch, this.swingHand, this.maceSwap, this.maceVClip);
   }

   public boolean criticalSprint() {
      return this.criticalSprint;
   }

   public CombatSubHelperPX yW(boolean useTp) {
      return this.selectWeapon == useTp ? this : new CombatSubHelperPX(useTp, this.criticalSprint, this.elytraDelaySwitch, this.invSwap, this.antiShieldSwap, this.useTp, this.useAttack, this.swingHand, this.maceSwap, this.maceVClip);
   }

   public boolean selectWeapon() {
      return this.selectWeapon;
   }

   public CombatSubHelperPX(
      boolean useTp,
      boolean maceSwap,
      boolean invSwap,
      boolean selectWeapon,
      boolean antiShieldSwap,
      boolean useAttack,
      boolean elytraDelaySwitch,
      boolean criticalSprint,
      boolean maceVClip,
      boolean swingHand
   ) {
      this.selectWeapon = useTp;
      this.criticalSprint = maceSwap;
      this.elytraDelaySwitch = invSwap;
      this.invSwap = selectWeapon;
      this.antiShieldSwap = antiShieldSwap;
      this.useTp = useAttack;
      this.useAttack = elytraDelaySwitch;
      this.swingHand = criticalSprint;
      this.maceSwap = maceVClip;
      this.maceVClip = swingHand;
   }

   public CombatSubHelperPX ze(boolean maceVClip) {
      return this.maceSwap == maceVClip ? this : new CombatSubHelperPX(this.selectWeapon, this.criticalSprint, this.elytraDelaySwitch, this.invSwap, this.antiShieldSwap, this.useTp, this.useAttack, this.swingHand, maceVClip, this.maceVClip);
   }

   public boolean useAttack() {
      return this.useAttack;
   }

   public boolean maceVClip() {
      return this.maceVClip;
   }

   public boolean antiShieldSwap() {
      return this.antiShieldSwap;
   }

   public CombatSubHelperPX yX(boolean maceSwap) {
      return this.criticalSprint == maceSwap ? this : new CombatSubHelperPX(this.selectWeapon, maceSwap, this.elytraDelaySwitch, this.invSwap, this.antiShieldSwap, this.useTp, this.useAttack, this.swingHand, this.maceSwap, this.maceVClip);
   }

   public boolean maceSwap() {
      return this.maceSwap;
   }

   public boolean yU() {
      return !this.selectWeapon && !this.useAttack && !this.maceSwap && !this.useTp;
   }

   public CombatSubHelperPX zd(boolean criticalSprint) {
      return this.swingHand == criticalSprint
         ? this
         : new CombatSubHelperPX(this.selectWeapon, this.criticalSprint, this.elytraDelaySwitch, this.invSwap, this.antiShieldSwap, this.useTp, this.useAttack, criticalSprint, this.maceSwap, this.maceVClip);
   }

   public CombatSubHelperPX yY(boolean invSwap) {
      return this.elytraDelaySwitch == invSwap ? this : new CombatSubHelperPX(this.selectWeapon, this.criticalSprint, invSwap, this.invSwap, this.antiShieldSwap, this.useTp, this.useAttack, this.swingHand, this.maceSwap, this.maceVClip);
   }
}
