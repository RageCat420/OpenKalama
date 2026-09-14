package me.matl114.hacks.modules.move;

import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.events.Event;
import me.matl114.hacks.MovTasks$MovInfo;
import me.matl114.hacks.utils.entity.EntityMovementStatus;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.versioned.api.VPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class MoveSubHelperW extends MoveSubHelperY {
    boolean afterSetbackFlag;
    int noFallCnt = -1;
    Boolean shouldApplyOnGroundReverseNextTick;

    public void applyBeforeMovementPacketModify(Event<LegalMovementManager> movementManagerEvent) {
        if (this.module.isActive()) {
            EntityMovementStatus var2 = ((LegalMovementManager) movementManagerEvent.b).c;
            boolean var3 = ((ClientPlayerEntity) var2.a).getY() <= this.module.Uw - this.module.UF;
            if ((this.afterSetbackFlag || var3) && !this.Md) {
                if (!var3) {
                    this.afterSetbackFlag = false;
                }

                if (!var2.b && ((ClientPlayerEntity) var2.a).isOnGround()) {
                    this.afterSetbackFlag = false;
                    this.Md = true;
                    this.counter = 0;
                    this.module.Uw = var2.g.getY();
                    MinecraftClient.getInstance()
                            .getNetworkHandler()
                            .sendPacket(VPacket.g(var2.g.getX(), var2.g.getY() + 9.0E-8, var2.g.getZ(), false, var2.c));
                    this.Mc = true;
                    return;
                }
            }
        }
    }

    @Override
    public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
        if (this.shouldApplyOnGroundReverseNextTick != null) {
            if (!this.Md) {
                ((LegalMovementManager) movementManagerEvent.b)
                        .c.a.setOnGround(this.shouldApplyOnGroundReverseNextTick);
            }

            this.shouldApplyOnGroundReverseNextTick = null;
        }

        if (this.Md) {}

        return true;
    }

    @Override
    public void ji(Event<MovTasks$MovInfo> setBack) {
        this.afterSetbackFlag = true;
        super.ji(setBack);
    }

    public MoveSubHelperW(NoFall module) {
        super(module);
        this.afterSetbackFlag = false;
    }

    public void applyPreTickModify(Event<LegalMovementManager> movementManagerEvent) {
        ClientPlayerEntity var2 = ((LegalMovementManager) movementManagerEvent.b).c.a;
        boolean var3 = ClientPlayerAccess.of(var2).isForceNoFall();
        if (var3) {
            this.Md = true;
            this.counter = 0;
            this.module.Uw = this.module.Uz;
            MinecraftClient.getInstance()
                    .getNetworkHandler()
                    .sendPacket(VPacket.g(
                            var2.getX(), this.module.Uz + 9.0E-8, var2.getZ(), false, var2.horizontalCollision));
            this.Mc = true;
            ClientPlayerAccess.of(var2).setForceNoFall(false);
        } else if (this.module.isActive()) {
            this.counter++;
        }

        if (this.counter > 100) {
            this.Mc = false;
        }
    }
}
