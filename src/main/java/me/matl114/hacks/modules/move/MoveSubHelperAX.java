package me.matl114.hacks.modules.move;

import me.matl114.managers.Tasks;
import me.matl114.managers.config.IntRef;

public class MoveSubHelperAX {
    IntRef fireTicks;
    int lastTimeFire = 0;
    boolean lastTimeWasAuto = false;

    public void c() {
        this.lastTimeWasAuto = true;
    }

    public void d() {
        this.lastTimeWasAuto = false;
        this.lastTimeFire = Tasks.b();
    }

    public boolean canFire() {
        return Tasks.b() > this.lastTimeFire + 10;
    }

    public MoveSubHelperAX(IntRef fireTicks) {
        this.fireTicks = fireTicks;
    }

    public boolean tryFire(int level) {
        return this.lastTimeWasAuto ? true : this.lastTimeFire + this.fireTicks.get() * level < Tasks.b();
    }
}
