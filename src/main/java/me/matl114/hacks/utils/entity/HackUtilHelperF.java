package me.matl114.hacks.utils.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import me.matl114.events.Event;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class HackUtilHelperF implements HackUtilHelperJ {
    private final List<Supplier<HackUtilHelperJ>> Ek = new ArrayList<>();
    private ClientPlayerEntity Ej;
    private final List<HackUtilHelperJ> El = new ArrayList<>();
    private final int da;

    public HackUtilHelperF(int p) {
        this.da = p;
    }

    @Override
    public void iC(Event<LegalMovementManager> movementManagerEvent) {
        for (HackUtilHelperJ var3 : this.El) {
            var3.iC(movementManagerEvent);
        }
    }

    @Override
    public int priority() {
        return this.da;
    }

    @Override
    public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
        for (HackUtilHelperJ var4 : this.El) {
            var4.postModify(movementManagerEvent, enabledThisTick);
        }

        return true;
    }

    public void SJ(Supplier<HackUtilHelperJ> movementModifier) {
        this.Ek.add(movementModifier);
        this.SI((HackUtilHelperJ) movementModifier.get());
    }

    @Override
    public void iB(Event<LegalMovementManager> movementManagerEvent) {
        for (HackUtilHelperJ var3 : this.El) {
            var3.iB(movementManagerEvent);
        }
    }

    public void SH(ClientPlayerEntity currentEntity) {
        this.El.clear();
        this.Ej = currentEntity;

        for (Supplier var3 : this.Ek) {
            HackUtilHelperJ var4 = (HackUtilHelperJ) var3.get();
            this.SI(var4);
        }
    }

    @Override
    public void jJ(Event<LegalMovementManager> movementManagerEvent, Event<Vec3d> moveEvent) {
        for (HackUtilHelperJ var4 : this.El) {
            var4.jJ(movementManagerEvent, moveEvent);
        }
    }

    @Override
    public void bb(Event<LegalMovementManager> movementManagerEvent) {
        for (HackUtilHelperJ var3 : this.El) {
            var3.bb(movementManagerEvent);
        }
    }

    @Override
    public void gz(Event<LegalMovementManager> movementManagerEvent) {
        for (HackUtilHelperJ var3 : this.El) {
            var3.gz(movementManagerEvent);
        }
    }

    @Override
    public void jI(Event<LegalMovementManager> movementManagerEvent, Event<Vec3d> moveEvent) {
        for (HackUtilHelperJ var4 : this.El) {
            var4.jI(movementManagerEvent, moveEvent);
        }
    }

    private void SI(HackUtilHelperJ movementModifier) {
        if (movementModifier != null && this.Ej != null) {
            int var2 = movementModifier.priority();
            int var3 = 0;

            while (var3 < this.El.size() && this.El.get(var3).priority() <= var2) {
                var3++;
            }

            this.El.add(var3, movementModifier);
        }
    }
}
