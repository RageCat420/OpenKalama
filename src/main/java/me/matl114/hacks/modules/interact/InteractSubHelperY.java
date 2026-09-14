package me.matl114.hacks.modules.interact;

public class InteractSubHelperY {
    Float b;
    Float a = null;

    public InteractSubHelperY() {
        this.b = null;
    }

    public float c(float currentYaw) {
        return this.b != null ? this.b : currentYaw;
    }

    public boolean hasDeceive() {
        return this.a != null || this.b != null;
    }

    public float b(float currentPitch) {
        return this.a != null ? this.a : currentPitch;
    }
}
