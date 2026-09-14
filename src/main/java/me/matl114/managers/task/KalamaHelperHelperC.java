package me.matl114.managers.task;

public class KalamaHelperHelperC extends TimedTask {
    Runnable task;

    public boolean b() {
        this.task.run();
        return true;
    }

    public KalamaHelperHelperC(Runnable runnable, int delay) {
        super(delay);
        this.task = runnable;
    }
}
