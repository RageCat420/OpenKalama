package me.matl114.managers.task;

import java.util.function.BooleanSupplier;

public class KalamaHelperHelperH extends RepeatTask {
    BooleanSupplier f;

    public KalamaHelperHelperH(BooleanSupplier task, int delay, int period) {
        super(delay, period);
        this.f = task;
    }

    public KalamaHelperHelperH(Runnable runnable, int delay, int period) {
        super(delay, period);
        this.f = () -> {
            runnable.run();
            return false;
        };
    }

    @Override
    public boolean b() {
        return this.f.getAsBoolean();
    }
}
