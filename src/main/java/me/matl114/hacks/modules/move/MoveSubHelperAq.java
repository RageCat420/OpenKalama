package me.matl114.hacks.modules.move;

import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.events.Event;
import me.matl114.hacks.MovTasks$MovInfo;
import me.matl114.hacks.utils.entity.EntityMovementStatus;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.utils.Debug;
import me.matl114.versioned.api.VPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Direction.Axis;

public class MoveSubHelperAq extends MoveSubHelperY {
    boolean UO = false;
    boolean UN;

    @Override
    public void gz(Event<LegalMovementManager> movementManagerEvent) {}

    public void applyBeforeMovementPacketModify(Event<LegalMovementManager> movementManagerEvent) {
        if (this.module.isActive()) {
            EntityMovementStatus var2 = ((LegalMovementManager) movementManagerEvent.b).c;
            ClientPlayerEntity var3 = (ClientPlayerEntity) var2.a;
            boolean var4 = ((ClientPlayerEntity) var2.a).getY() <= this.module.Uw - this.module.UF;
            if (this.UO && this.Mc && MinecraftClient.getInstance().player.isOnGround()) {
                Debug.b(MinecraftClient.getInstance().player.getY());
                this.UO = false;
                this.Mc = false;
                this.UN = false;
                MinecraftClient.getInstance()
                        .player
                        .setPosition(MinecraftClient.getInstance()
                                .player
                                .getPos()
                                .withAxis(Axis.Y, ((LegalMovementManager) movementManagerEvent.b).c.g.y + 9.0E-5));
                MinecraftClient.getInstance().player.setOnGround(false);
                ClientPlayerAccess.of(MinecraftClient.getInstance().player).resyncPos();
            }

            if (var4
                    && MinecraftClient.getInstance().player.isOnGround()
                    && !((LegalMovementManager) movementManagerEvent.b).c.b) {
                MinecraftClient.getInstance().player.setOnGround(false);
                this.module.Uw = MinecraftClient.getInstance().player.getY();
                MinecraftClient.getInstance()
                        .player
                        .setPosition(((LegalMovementManager) movementManagerEvent.b).c.g.add(0.0, 9.0E-8, 0.0));
                ClientPlayerAccess.of(MinecraftClient.getInstance().player).resyncPos();
                this.UO = true;
            }
        } else {
            this.UO = false;
        }
    }

    @Override
    public void ji(Event<MovTasks$MovInfo> setBack) {
        super.ji(setBack);
        this.Mc = true;
    }

    @Override
    public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
        return true;
    }

    public MoveSubHelperAq(NoFall module) {
        super(module);
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
