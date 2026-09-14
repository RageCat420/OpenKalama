package me.matl114.hacks.utils.tasks;

public class CounterExecutor {
    int counter = 0;

    public boolean e(int limit, Runnable runnable) {
        if (this.counter >= limit) {
            runnable.run();
            return true;
        } else {
            return false;
        }
    }

    public void h() {
        this.counter = 0;
    }

    public boolean c(int limit, Runnable runnable) {
        if (this.counter >= limit) {
            runnable.run();
            this.counter = 0;
            return true;
        } else {
            return false;
        }
    }

    public boolean d(int limit) {
        return this.counter >= limit;
    }

    public void f() {
        this.counter++;
    }

    public boolean b(int limit) {
        if (++this.counter >= limit) {
            this.counter = 0;
            return true;
        } else {
            return false;
        }
    }

    public boolean a(int limit, Runnable runnable) {
        if (++this.counter >= limit) {
            runnable.run();
            this.counter = 0;
            return true;
        } else {
            return false;
        }
    }

    public void count(int count) {
        this.counter += count;
    }
}
