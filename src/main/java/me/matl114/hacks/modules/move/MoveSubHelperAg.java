package me.matl114.hacks.modules.move;

import me.matl114.managers.Tasks;
import me.matl114.utils.entity.PlayerInputUtils$Input;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.NotNull;

public class MoveSubHelperAg implements Comparable<MoveSubHelperAg> {
    private final int Qt;
    private final int Qs;
    private final MoveSubHelperBX modifier;

    public int compareTo(@NotNull MoveSubHelperAg timedModifier) {
        return Integer.compare(this.modifier.ba(), timedModifier.modifier.ba());
    }

    public MoveSubHelperAg(int startTicks, int lastTicks, MoveSubHelperBX modifier) {
        this.Qs = Tasks.b() + startTicks;
        this.Qt = Tasks.b() + startTicks + lastTicks;
        this.modifier = modifier;
    }

    public boolean ahA(PlayerInputUtils$Input input) {
        if (this.isExpired()) {
            return true;
        } else {
            if (Tasks.b() >= this.Qs) {
                this.modifier.gS(input);
            }

            return false;
        }
    }

    public MoveSubHelperAg(int lastTicks, MoveSubHelperBX modifier) {
        this(0, lastTicks, modifier);
    }

    public boolean isExpired() {
        return Tasks.b() > this.Qt;
    }

    public boolean ahB(ClientPlayerEntity player) {
        if (this.isExpired()) {
            return false;
        } else if (Tasks.b() >= this.Qs && this.modifier.gU()) {
            this.modifier.gT(player);
            return true;
        } else {
            return false;
        }
    }
}
