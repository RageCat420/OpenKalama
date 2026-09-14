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
        boolean swingHand) {
    public boolean elytraDelaySwitch() {
        return this.elytraDelaySwitch;
    }

    public CombatSubHelperPX yZ(boolean selectWeapon) {
        return this.selectWeapon == selectWeapon
                ? this
                : new CombatSubHelperPX(
                        this.useTp,
                        this.maceSwap,
                        this.invSwap,
                        selectWeapon,
                        this.antiShieldSwap,
                        this.useAttack,
                        this.elytraDelaySwitch,
                        this.criticalSprint,
                        this.maceVClip,
                        this.swingHand);
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
                : new CombatSubHelperPX(
                        this.useTp,
                        this.maceSwap,
                        this.invSwap,
                        this.selectWeapon,
                        antiShieldSwap,
                        this.useAttack,
                        this.elytraDelaySwitch,
                        this.criticalSprint,
                        this.maceVClip,
                        this.swingHand);
    }

    public boolean useTp() {
        return this.useTp;
    }

    public CombatSubHelperPX zb(boolean useAttack) {
        return this.useAttack == useAttack
                ? this
                : new CombatSubHelperPX(
                        this.useTp,
                        this.maceSwap,
                        this.invSwap,
                        this.selectWeapon,
                        this.antiShieldSwap,
                        useAttack,
                        this.elytraDelaySwitch,
                        this.criticalSprint,
                        this.maceVClip,
                        this.swingHand);
    }

    public CombatSubHelperPX zf(boolean swingHand) {
        return this.swingHand == swingHand
                ? this
                : new CombatSubHelperPX(
                        this.useTp,
                        this.maceSwap,
                        this.invSwap,
                        this.selectWeapon,
                        this.antiShieldSwap,
                        this.useAttack,
                        this.elytraDelaySwitch,
                        this.criticalSprint,
                        this.maceVClip,
                        swingHand);
    }

    public boolean invSwap() {
        return this.invSwap;
    }

    public CombatSubHelperPX zc(boolean elytraDelaySwitch) {
        return this.elytraDelaySwitch == elytraDelaySwitch
                ? this
                : new CombatSubHelperPX(
                        this.useTp,
                        this.maceSwap,
                        this.invSwap,
                        this.selectWeapon,
                        this.antiShieldSwap,
                        this.useAttack,
                        elytraDelaySwitch,
                        this.criticalSprint,
                        this.maceVClip,
                        this.swingHand);
    }

    public boolean criticalSprint() {
        return this.criticalSprint;
    }

    public CombatSubHelperPX yW(boolean useTp) {
        return this.useTp == useTp
                ? this
                : new CombatSubHelperPX(
                        useTp,
                        this.maceSwap,
                        this.invSwap,
                        this.selectWeapon,
                        this.antiShieldSwap,
                        this.useAttack,
                        this.elytraDelaySwitch,
                        this.criticalSprint,
                        this.maceVClip,
                        this.swingHand);
    }

    public boolean selectWeapon() {
        return this.selectWeapon;
    }

    public CombatSubHelperPX ze(boolean maceVClip) {
        return this.maceVClip == maceVClip
                ? this
                : new CombatSubHelperPX(
                        this.useTp,
                        this.maceSwap,
                        this.invSwap,
                        this.selectWeapon,
                        this.antiShieldSwap,
                        this.useAttack,
                        this.elytraDelaySwitch,
                        this.criticalSprint,
                        maceVClip,
                        this.swingHand);
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
        return this.maceSwap == maceSwap
                ? this
                : new CombatSubHelperPX(
                        this.useTp,
                        maceSwap,
                        this.invSwap,
                        this.selectWeapon,
                        this.antiShieldSwap,
                        this.useAttack,
                        this.elytraDelaySwitch,
                        this.criticalSprint,
                        this.maceVClip,
                        this.swingHand);
    }

    public boolean maceSwap() {
        return this.maceSwap;
    }

    public boolean yU() {
        return !this.selectWeapon && !this.useAttack && !this.maceSwap && !this.useTp;
    }

    public CombatSubHelperPX zd(boolean criticalSprint) {
        return this.criticalSprint == criticalSprint
                ? this
                : new CombatSubHelperPX(
                        this.useTp,
                        this.maceSwap,
                        this.invSwap,
                        this.selectWeapon,
                        this.antiShieldSwap,
                        this.useAttack,
                        this.elytraDelaySwitch,
                        criticalSprint,
                        this.maceVClip,
                        this.swingHand);
    }

    public CombatSubHelperPX yY(boolean invSwap) {
        return this.invSwap == invSwap
                ? this
                : new CombatSubHelperPX(
                        this.useTp,
                        this.maceSwap,
                        invSwap,
                        this.selectWeapon,
                        this.antiShieldSwap,
                        this.useAttack,
                        this.elytraDelaySwitch,
                        this.criticalSprint,
                        this.maceVClip,
                        this.swingHand);
    }
}
