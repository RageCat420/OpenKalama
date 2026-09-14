package me.matl114.hacks.modules.move;

import me.matl114.events.Event;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.utils.Debug;
import me.matl114.utils.EntityUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

class MoveSubHelperKX extends MoveSubHelperAj {
    public MoveSubHelperKX(Travel control) {
        super(control);
    }

    @Override
    public TravellingControl$Type getType() {
        return TravellingControl$Type.ELYTRA_PITCH40;
    }

    @Override
    public void bb(Event<LegalMovementManager> movementManagerEvent) {
        if (this.QM != null && !this.QM.shouldNotRun()) {
            ClientPlayerEntity var2 = ((LegalMovementManager) movementManagerEvent.b).c.a;
            this.QL.QX(this.QM, var2.getY());
            this.ahH();
            if (this.QN) {
                if (MinecraftClient.getInstance().player.isFallFlying()) {
                    ((LegalMovementManager) movementManagerEvent.b).pushImportantRotation(true, true);
                    Vec3d var3 = MinecraftClient.getInstance().player.getPos();
                    Vec3d var4 = this.QM.getCurrentFlyingTarget().subtract(var3);
                    float var5 = EntityUtils.q(var4.normalize()).y;
                    this.QO++;
                    switch (this.QM.d) {
                        case mt:
                            PlayerStateManager.nT(var2, var5);
                            EntityUtils.setEntityPitchSafe(
                                    var2,
                                    Math.min(
                                            -this.QL.pitch40PitchNegative.get()
                                                    + this.QO * (float) this.QL.pitch40NegativeDelta.get(),
                                            (float) this.QL.pitch40PitchPositive.get()));
                            break;
                        case mr:
                        case ms:
                            PlayerStateManager.nT(var2, var5);
                            double var6 = MinecraftClient.getInstance().player.getY();
                            double var8 = this.QQ[this.QR];
                            boolean var10 = var6 < var8;
                            if (this.QL.pitch40HeightLimit.get().test(s -> var6 > s)) {
                                var10 = true;
                            }

                            if (var10) {
                                if (this.QO > 1) {
                                    Debug.chat("[Pitch40] Current Height", var2.getY());
                                }

                                this.QO = 0;
                                EntityUtils.setEntityPitchSafe(var2, this.QL.pitch40PitchPositive.get());
                            } else {
                                EntityUtils.setEntityPitchSafe(
                                        var2,
                                        Math.min(
                                                -this.QL.pitch40PitchNegative.get()
                                                        + this.QO * (float) this.QL.pitch40NegativeDelta.get(),
                                                (float) this.QL.pitch40PitchPositive.get()));
                            }
                    }

                    if (AntiChunkLag.INSTANCE.currentMayFaceLagChunk) {
                        FloatingUtils.INSTANCE.SB(true);
                    }
                } else {
                    this.ahJ();
                }
            }
        }
    }
}
