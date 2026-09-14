package me.matl114.hacks.modules.interact;

import me.matl114.managers.Tasks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class InteractSubHelperO {
    int expireTick;
    BlockPos a;
    BlockState c;

    public boolean expire() {
        return Tasks.b() > this.expireTick;
    }

    public InteractSubHelperO(BlockPos blockPos, int lastTicks, BlockState targetState) {
        this.a = blockPos;
        this.expireTick = lastTicks + Tasks.b();
        this.c = targetState;
    }
}
