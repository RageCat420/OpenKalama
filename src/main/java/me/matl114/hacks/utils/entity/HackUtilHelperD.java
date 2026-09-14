package me.matl114.hacks.utils.entity;

import java.util.function.Supplier;
import me.matl114.events.Event;
import net.minecraft.util.math.Vec3d;

public class HackUtilHelperD implements HackUtilHelperJ {
    public Supplier<HackUtilHelperJ> delegate;

    @Override
    public int priority() {
        return this.delegate.get().priority();
    }

    @Override
    public void mP(Event<LegalMovementManager> movementManagerEvent) {
        this.delegate.get().mP(movementManagerEvent);
    }

    public void mN(Supplier<HackUtilHelperJ> movementModifierSupplier) {
        this.delegate = movementModifierSupplier;
    }

    @Override
    public void jJ(Event<LegalMovementManager> movementManagerEvent, Event<Vec3d> moveEvent) {
        this.delegate.get().jJ(movementManagerEvent, moveEvent);
    }

    @Override
    public void iC(Event<LegalMovementManager> movementManagerEvent) {
        this.delegate.get().iC(movementManagerEvent);
    }

    @Override
    public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
        return this.delegate.get().postModify(movementManagerEvent, enabledThisTick);
    }

    @Override
    public int compareTo(HackUtilHelperJ var1) {
        return this.delegate.get().compareTo(var1);
    }

    public Supplier<HackUtilHelperJ> getDelegate() {
        return this.delegate;
    }

    @Override
    public boolean shouldApply(Event<LegalMovementManager> movementManagerEvent) {
        return this.delegate.get().shouldApply(movementManagerEvent);
    }

    public HackUtilHelperD(Supplier<HackUtilHelperJ> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void iB(Event<LegalMovementManager> movementManagerEvent) {
        this.delegate.get().iB(movementManagerEvent);
    }

    @Override
    public void jI(Event<LegalMovementManager> movementManagerEvent, Event<Vec3d> moveEvent) {
        this.delegate.get().jI(movementManagerEvent, moveEvent);
    }

    @Override
    public void gz(Event<LegalMovementManager> movementManagerEvent) {
        this.delegate.get().gz(movementManagerEvent);
    }

    @Override
    public void bb(Event<LegalMovementManager> movementManagerEvent) {
        this.delegate.get().bb(movementManagerEvent);
    }
}
