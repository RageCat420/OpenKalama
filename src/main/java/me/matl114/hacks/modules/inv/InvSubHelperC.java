package me.matl114.hacks.modules.inv;

import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public record InvSubHelperC(
        Optional<Pair<BlockPos, BlockHitResult>> placePos,
        Optional<Slot> playerScreenSlot,
        Consumer<ScreenHandler> successCallback,
        Runnable failureCallback,
        boolean useZeroTick) {
    public Optional<Slot> playerScreenSlot() {
        return this.playerScreenSlot;
    }

    public boolean useZeroTick() {
        return this.useZeroTick;
    }

    public Runnable failureCallback() {
        return this.failureCallback;
    }

    public Optional<Pair<BlockPos, BlockHitResult>> placePos() {
        return this.placePos;
    }

    public Consumer<ScreenHandler> successCallback() {
        return this.successCallback;
    }

    public InvSubHelperC rV(Runnable failureCallback) {
        return this.failureCallback == failureCallback
                ? this
                : new InvSubHelperC(
                        this.placePos, this.playerScreenSlot, this.successCallback, failureCallback, this.useZeroTick);
    }

    public InvSubHelperC rS(Optional<Pair<BlockPos, BlockHitResult>> placePos) {
        return this.placePos == placePos
                ? this
                : new InvSubHelperC(
                        placePos, this.playerScreenSlot, this.successCallback, this.failureCallback, this.useZeroTick);
    }

    public InvSubHelperC withUseZeroTick(boolean useZeroTick) {
        return this.useZeroTick == useZeroTick
                ? this
                : new InvSubHelperC(
                        this.placePos, this.playerScreenSlot, this.successCallback, this.failureCallback, useZeroTick);
    }

    public InvSubHelperC rU(Consumer<ScreenHandler> successCallback) {
        return this.successCallback == successCallback
                ? this
                : new InvSubHelperC(
                        this.placePos, this.playerScreenSlot, successCallback, this.failureCallback, this.useZeroTick);
    }

    public InvSubHelperC rT(Optional<Slot> playerScreenSlot) {
        return this.playerScreenSlot == playerScreenSlot
                ? this
                : new InvSubHelperC(
                        this.placePos, playerScreenSlot, this.successCallback, this.failureCallback, this.useZeroTick);
    }
}
