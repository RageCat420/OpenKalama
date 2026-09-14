package me.matl114.hacks.modules.move;

import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.events.Event;
import me.matl114.hacks.MovTasks$MovInfo;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.versioned.api.VPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class MoveSubHelperS extends MoveSubHelperY {
    boolean Jc;
    boolean Ja = false;
    int waitTimeout;
    boolean Jb = false;

    public void applyBeforeMovementPacketModify(Event<LegalMovementManager> movementManagerEvent) {
        if (this.Ja) {
            movementManagerEvent.cancel();
            ((LegalMovementManager) movementManagerEvent.b).c.restorePos();
        } else {
            if (this.module.isActive() && this.Md) {
                ClientPlayerEntity var2 = ((LegalMovementManager) movementManagerEvent.b).c.a;
                if (var2.isOnGround()) {
                    this.Md = true;
                    movementManagerEvent.cancel();
                    MinecraftClient.getInstance()
                            .getNetworkHandler()
                            .sendPacket(VPacket.f(true, var2.horizontalCollision));
                    this.Mc = false;
                    ClientPlayerAccess.of(var2).setForceNoFall(false);
                    this.module.Uw = var2.getY();
                    this.Jc = true;
                    this.Ja = true;
                    this.waitTimeout = 0;
                    this.Md = false;
                } else {
                    this.Md = false;
                }
            }
        }
    }

    @Override
    public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
        if (this.Ja) {
            return true;
        } else {
            ClientPlayerEntity var3 = ((LegalMovementManager) movementManagerEvent.e()).c.a;
            if (this.Jb) {
                MinecraftClient.getInstance().options.jumpKey.setPressed(false);
                this.Jb = false;
                var3.setOnGround(false);
            }

            return true;
        }
    }

    @Override
    public void ji(Event<MovTasks$MovInfo> event) {
        this.Jc = false;
        super.ji(event);
    }

    public MoveSubHelperS(NoFall module) {
        super(module);
        this.Jc = false;
        this.waitTimeout = 0;
    }

    public void applyPreTickModify(Event<LegalMovementManager> movementManagerEvent) {
        if (this.Ja) {
            if (!this.Jc) {
                this.Jb = true;
                this.Ja = false;
                MinecraftClient.getInstance().player.setOnGround(true);
                MinecraftClient.getInstance().options.jumpKey.setPressed(true);
            } else {
                this.waitTimeout++;
                if (this.waitTimeout >= 2) {
                    this.waitTimeout = 0;
                    this.Ja = false;
                    this.Jc = false;
                    MinecraftClient.getInstance().player.setOnGround(true);
                } else {
                    MinecraftClient.getInstance().player.setOnGround(false);
                }
            }
        }

        ClientPlayerEntity var2 = ((LegalMovementManager) movementManagerEvent.b).c.a;
        boolean var3 = ClientPlayerAccess.of(var2).isForceNoFall();
        if (this.module.isActive() || var3) {
            if (!var3 && !this.module.amh()) {
                if (var2.isOnGround()) {
                    this.module.Uw = this.module.Ux;
                } else {
                    this.counter++;
                }
            } else {
                if (!var2.isOnGround()) {
                    this.Md = true;
                }

                this.counter = 0;
            }

            if (this.counter >= 100) {
                this.Mc = false;
            }
        }
    }
}
