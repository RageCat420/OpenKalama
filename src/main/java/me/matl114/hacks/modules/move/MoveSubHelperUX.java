package me.matl114.hacks.modules.move;


import net.minecraft.client.MinecraftClient;
import com.google.common.util.concurrent.AtomicDouble;
import java.util.concurrent.atomic.AtomicInteger;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.catchers.TimedPacketCatcherImpl;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.managers.Tasks;
import me.matl114.utils.Debug;
import me.matl114.utils.EntityUtils;
import me.matl114.versioned.api.VPacket;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.math.Vec3d;

class MoveSubHelperUX extends MoveSubHelperAj {
   boolean rn;
   boolean rm = false;

   @Override
   public TravellingControl$Type getType() {
      return TravellingControl$Type.ELYTRA_GRIM_FLY40;
   }

   @Override
   public void onStart(MoveSubHelperSX state) {
      super.onStart(state);
      this.rm = false;
      this.rn = false;
   }

   public MoveSubHelperUX(Travel control) {
      super(control);
      this.rn = false;
   }

   @Override
   public void bb(Event<LegalMovementManager> movementManagerEvent) {
      if (this.QM != null && !this.QM.shouldNotRun()) {
         ClientPlayerEntity var2 = ((LegalMovementManager)movementManagerEvent.b).c.a;
         this.QL.QX(this.QM, var2.getY());
         this.ahH();
         int var3 = this.QL.pitch40PitchPositive.get();
         if (this.QN) {
            if (MinecraftClient.getInstance().player.isFallFlying()) {
               ((LegalMovementManager)movementManagerEvent.b).pushImportantRotation(true, true);
               Vec3d var4 = MinecraftClient.getInstance().player.getPos();
               Vec3d var5 = this.QM.getCurrentFlyingTarget().subtract(var4);
               float var6 = EntityUtils.q(var5.normalize()).y;
               this.QO++;
               switch (this.QM.d) {
                  case mt:
                     if (!this.rm) {
                        AtomicInteger var12 = new AtomicInteger(20);
                        AtomicDouble var13 = new AtomicDouble(-999.0);
                        Listener.C(new TimedPacketCatcherImpl(EntityVelocityUpdateS2CPacket.class, 200, event -> {
                           Vec3d var4x = VPacket.getVelocity((EntityVelocityUpdateS2CPacket)event.b);
                           if (!(var4x.y <= var13.get()) && !(var4x.y > 3.0) && var12.getAndDecrement() >= 0) {
                              var13.set(var4x.y);
                              return false;
                           } else {
                              Tasks.l(() -> this.rn = false, 0);
                              return true;
                           }
                        }));
                     }

                     this.rm = true;
                     PlayerStateManager.nT(var2, var6);
                     EntityUtils.setEntityPitchSafe(
                        var2,
                        Math.min(
                           -this.QL.pitch40PitchNegative.get() + this.QO * (float)(Object)this.QL.pitch40NegativeDelta.get(), (float)(Object)this.QL.pitch40PitchPositive.get()
                        )
                     );
                     break;
                  case mr:
                  case ms:
                     PlayerStateManager.nT(var2, var6);
                     double var7 = MinecraftClient.getInstance().player.getY();
                     double var9 = this.QQ[this.QR];
                     boolean var11 = var7 < var9;
                     if (this.QL.pitch40HeightLimit.get().test(s -> var7 > s)) {
                        var11 = true;
                     }

                     if (this.rm && var11) {
                        this.rm = false;
                        Debug.chat("[Pitch40] Current Height", var9);
                     }

                     if (!this.rm) {
                        this.QO = 0;
                        this.rn = true;
                        EntityUtils.setEntityPitchSafe(var2, var3);
                     } else {
                        EntityUtils.setEntityPitchSafe(
                           var2,
                           Math.min(
                              -this.QL.pitch40PitchNegative.get() + this.QO * (float)(Object)this.QL.pitch40NegativeDelta.get(),
                              (float)(Object)this.QL.pitch40PitchPositive.get()
                           )
                        );
                     }
               }

               if (AntiChunkLag.INSTANCE.currentMayFaceLagChunk) {
                  FloatingUtils.INSTANCE.SB(true);
                  this.rn = false;
               }
            } else {
               this.ahJ();
            }
         }

         if (this.rn) {
            MovTasks.az().Zo();
         }
      }
   }
}
