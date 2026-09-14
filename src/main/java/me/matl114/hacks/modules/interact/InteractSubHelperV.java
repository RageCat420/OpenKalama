package me.matl114.hacks.modules.interact;

public class InteractSubHelperV extends InteractSubHelperHX {
    boolean hasRun;

    public InteractSubHelperV(int delay) {
        super(delay);
        this.b = 0;
    }

    @Override
    public int countDown() {
        if (this.hasRun) {
            return 0;
        } else if (super.countDownTimer()) {
            this.hasRun = true;
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public void cancel() {
        this.hasRun = true;
    }

    @Override
    public boolean canRun() {
        return !this.hasRun;
    }
}
