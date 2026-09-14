package me.matl114.hacks.modules.move;

public record MoveSubHelperV(boolean forward, boolean backward, boolean left, boolean right) {
    public boolean backward() {
        return this.backward;
    }

    public boolean left() {
        return this.left;
    }

    public boolean right() {
        return this.right;
    }

    boolean aar() {
        return this.backward || this.right || this.forward || this.left;
    }

    public boolean forward() {
        return this.forward;
    }
}
