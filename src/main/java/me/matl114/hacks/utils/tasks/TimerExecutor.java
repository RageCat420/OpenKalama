package me.matl114.hacks.utils.tasks;

import me.matl114.managers.Tasks;

public class TimerExecutor {
    int lastActionTime = 0;

    public boolean c(int cd) {
        if (Tasks.b() >= cd + this.lastActionTime) {
            this.lastActionTime = Tasks.b();
            return true;
        } else {
            return false;
        }
    }

    public boolean e(int cd, Runnable r) {
        if (Tasks.b() < cd + this.lastActionTime) {
            r.run();
            return true;
        } else {
            return false;
        }
    }

    public void mark(int extra) {
        this.lastActionTime = Tasks.b() + extra;
    }

    public boolean d(int cd, Runnable r) {
        if (Tasks.b() >= cd + this.lastActionTime) {
            r.run();
            return true;
        } else {
            return false;
        }
    }

    public void f() {
        this.lastActionTime = Tasks.b();
    }

    public boolean a(int cd) {
        return Tasks.b() >= cd + this.lastActionTime;
    }

    public boolean b(int cd, Runnable r) {
        if (Tasks.b() >= cd + this.lastActionTime) {
            this.lastActionTime = Tasks.b();
            r.run();
            return true;
        } else {
            return false;
        }
    }
}
