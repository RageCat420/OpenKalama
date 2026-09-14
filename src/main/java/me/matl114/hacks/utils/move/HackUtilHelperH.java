package me.matl114.hacks.utils.move;

import java.util.Objects;
import me.matl114.hacks.utils.move.goal.IPathGoal;
import me.matl114.hooks.BaritoneHooks;
import me.matl114.utils.config.ValueAccessor;

public class HackUtilHelperH implements HackUtilHelperD {
    boolean d;
    IPathGoal lastSumitGoal = null;
    boolean e;
    boolean b = false;

    @Override
    public IPathGoal getCurrentGoal() {
        return this.lastSumitGoal;
    }

    @Override
    public void e() {
        if (this.e) {
            this.e = false;
            ValueAccessor var1 = BaritoneHooks.getInstance().getSetting("allowBreak");
            var1.setValue(this.d);
        }
    }

    @Override
    public void setCanMine(boolean canMine) {
        this.b = canMine;
    }

    public HackUtilHelperH() {
        this.e = false;
    }

    @Override
    public void d() {
        if (!this.e) {
            this.e = true;
            ValueAccessor var1 = BaritoneHooks.getInstance().getSetting("allowBreak");
            this.d = (Boolean) var1.getValue();
            var1.setValue(this.b);
        }
    }

    @Override
    public boolean isPathing() {
        return this.lastSumitGoal != null && BaritoneHooks.getInstance().isBaritoneGoalPathingActive();
    }

    @Override
    public void a() {}

    @Override
    public void sumitGoal(IPathGoal pos) {
        if (pos != null && pos.isInGoal(PathingSchedular.a.player.getPos())) {
            pos = null;
        }

        if (!Objects.equals(pos, this.lastSumitGoal)
                || pos != null != BaritoneHooks.getInstance().isBaritoneGoalPathingActive()) {
            this.lastSumitGoal = pos;
            BaritoneHooks.getInstance().setBaritoneCurrentGoal(pos);
        }
    }
}
