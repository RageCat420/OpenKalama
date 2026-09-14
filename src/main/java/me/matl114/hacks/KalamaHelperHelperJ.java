package me.matl114.hacks;

import me.matl114.events.Event;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.utils.EntityUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

class KalamaHelperHelperJ implements HackUtilHelperJ {
    KalamaHelperHelperJ(Vec3d var1, Vec3d var2, Runnable var3) {
        this.Cr = var1;
        this.Cs = var2;
        this.Ct = var3;
    }

    @Override
    public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
        this.Ct.run();
        return false;
    }

    @Override
    public int priority() {
        return -100000;
    }

    public void applyPreTickModify(Event<LegalMovementManager> movementManagerEvent) {
        ClientPlayerEntity var2 = ((LegalMovementManager) movementManagerEvent.b).c.a;
        Vec2f var3 = EntityUtils.q(this.Cr.subtract(this.Cs.add(InteractionTasks.a.player.getVelocity()))
                .normalize());
        ((LegalMovementManager) movementManagerEvent.b).pushImportantRotation(true, true);
        PlayerStateManager.nT(var2, var3.y);
        EntityUtils.setEntityPitchSafe(var2, var3.x);
        ((LegalMovementManager) movementManagerEvent.b).s();
        ((LegalMovementManager) movementManagerEvent.b).c();
    }

    Vec3d Cr;
    Vec3d Cs;
    Runnable Ct;
}
