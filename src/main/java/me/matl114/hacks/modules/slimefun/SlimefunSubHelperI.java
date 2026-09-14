package me.matl114.hacks.modules.slimefun;

import me.matl114.accessors.access.ClientAccess;
import me.matl114.events.Event;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.utils.EntityUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec2f;

class SlimefunSubHelperI implements HackUtilHelperJ {
    private final int val$clickRate;
    private final BlockHitResult JO;
    private final Vec2f JM;

    @Override
    public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
        if (!enabledThisTick) {
            return true;
        } else {
            for (int var3 = 0; var3 < this.val$clickRate; var3++) {
                MinecraftClient.getInstance()
                        .interactionManager
                        .sendSequencedPacket(
                                MinecraftClient.getInstance().world,
                                sequence -> new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, this.JO, sequence));
            }

            ClientAccess.of(MinecraftClient.getInstance()).setItemUseCooldown(0);
            return false;
        }
    }

    @Override
    public int priority() {
        return -100000;
    }

    SlimefunSubHelperI(
            final MultiBlockHelper this$0, final Vec2f param2, final int nullx, final BlockHitResult nullxx) {
        this.JM = param2;
        this.val$clickRate = nullx;
        this.JO = nullxx;
    }

    public void applyPreTickModify(Event<LegalMovementManager> movementManagerEvent) {
        ClientPlayerEntity var2 = ((LegalMovementManager) movementManagerEvent.e()).c.a;
        ((LegalMovementManager) movementManagerEvent.b).pushImportantRotation(true, true);
        EntityUtils.setEntityPitchSafe(var2, this.JM.x);
        PlayerStateManager.nT(var2, this.JM.y);
        ((LegalMovementManager) movementManagerEvent.b).c();
    }
}
