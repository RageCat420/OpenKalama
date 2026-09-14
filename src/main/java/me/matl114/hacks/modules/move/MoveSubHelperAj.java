package me.matl114.hacks.modules.move;


import net.minecraft.client.MinecraftClient;
import me.matl114.events.Event;
import me.matl114.hacks.MainTasks;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.managers.Tasks;
import me.matl114.utils.Debug;
import me.matl114.utils.EntityUtils;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;

abstract class MoveSubHelperAj implements MoveSubHelperAh, HackUtilHelperJ {
   double[] QQ;
   int QP;
   MoveSubHelperSX QM = null;
   boolean QN = false;
   Travel QL;
   int QO;
   int QR;
   int zw = 0;

   public MoveSubHelperAj(Travel control) {
      this.QO = 0;
      this.QP = 0;
      this.QQ = new double[]{-999.0, -999.0, -999.0, -999.0, -999.0};
      this.QR = 0;
      this.QL = control;
   }

   @Override
   public boolean onTick(Event<Void> event) {
      if (MinecraftClient.getInstance().player != null && Travel.CW == null) {
         return true;
      } else if (this.QL.CV) {
         if (this.QL.pitch40EndKick.get()) {
            Tasks.m(() -> {
               if (MinecraftClient.getInstance().player != null) {
                  Debug.b("Disconnect because of safety");
                  MainTasks.q();
                  return true;
               } else {
                  return false;
               }
            }, 1, 1);
         }

         return true;
      } else {
         return false;
      }
   }

   protected void ahJ() {
      if (this.QP > 20) {
         this.QP = 0;
      }

      if (this.QP == 0) {
         MovTasks.aj().Xf();
         if (MinecraftClient.getInstance().player.checkFallFlying()) {
            MovExtra.INSTANCE.Xj();
            MinecraftClient.getInstance().getNetworkHandler().sendPacket(new ClientCommandC2SPacket(MinecraftClient.getInstance().player, Mode.START_FALL_FLYING));
            MovExtra.INSTANCE.sendPacketsForPreStartFallFlying();
         }

         this.QP = 1;
         MovTasks.aj().sendPacketsForPreStartFallFlying();
      }
   }

   protected void ahI() {
      EntityUtils.setEntityPitchSafe(MinecraftClient.getInstance().player, -this.QL.pitch40PitchNegative.get());
      if (!ElytraExtra.INSTANCE.agj() && Tasks.b() % 5 == 0 && MinecraftClient.getInstance().player.getVelocity().y < 0.0) {
         ElytraExtra.INSTANCE.afI();
      }
   }

   @Override
   public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
      if (this.QM == null || this.QM.shouldNotRun()) {
         return true;
      } else if (this.QN && !this.ahK()) {
         return true;
      } else {
         this.ahL();
         return true;
      }
   }

   @Override
   public void onStart(MoveSubHelperSX state) {
      this.QM = state;
      this.QM.d = MoveSubHelperOX.mr;
      this.QN = false;
      this.zw = 0;
      this.QO = 0;
      this.QP = 0;
      this.QQ = new double[]{-999.0, -999.0, -999.0, -999.0, -999.0, -999.0, -999.0, -999.0, -999.0, -999.0};
      this.QR = 0;
   }

   @Override
   public int priority() {
      return -100000;
   }

   protected boolean ahK() {
      if (this.QL.pitch40AutoPullUp.get() && MinecraftClient.getInstance().player.getY() < this.QL.minHeight.get() - 16) {
         this.ahI();
      }

      if (this.QL.pitch40EndSafety.get() && MinecraftClient.getInstance().player.getY() < this.QL.minHeight.get() - 32) {
         Debug.b("[Pitch40] 滑翔失控了");
         if (this.QL.pitch40EndSafety.get()) {
            Debug.a("Pitch40 out of control!");
            MainTasks.q();
         }

         this.QN = false;
         this.QL.QS(this.QM);
         return false;
      } else {
         if (MinecraftClient.getInstance().player == null || MinecraftClient.getInstance().player.isFallFlying()) {
            this.QP = 0;
         } else if (this.QP > 0) {
            this.QP++;
         }

         return true;
      }
   }

   protected void ahL() {
      if (this.QL.QW(this.QM)) {
         if (!this.QM.g && this.QL.pitch40EndSafety2.get()) {
            Debug.b("[Pitch40] 我们到达了目的地了");
            Debug.b("[Pitch40] 我们需要自动断线");
            MainTasks.q();
         }

         this.QL.QS(this.QM);
      }

      if (MinecraftClient.getInstance().player != null) {
         this.QQ[this.QR] = MinecraftClient.getInstance().player.getY();
         this.QR = (this.QR + 1) % this.QQ.length;
      }
   }

   @Override
   public void onStop() {
      this.QM = null;
      this.QN = false;
   }

   protected void ahH() {
      if (!this.QN) {
         if (MinecraftClient.getInstance().player.isFallFlying()) {
            if (this.QM.d == MoveSubHelperOX.ms) {
               this.QN = true;
               Debug.b("[Pitch440] 开始工作!");
            } else {
               if (this.QL.pitch40AutoPullUp.get()) {
                  this.ahI();
               }

               if (++this.zw % 60 == 0) {
                  Debug.chat("[Pitch40] 请拉升到MaxHeight以启动:", this.QL.maxHeight.get());
               }
            }
         } else if (this.QL.pitch40AutoPullUp.get()) {
            ElytraExtra.INSTANCE.afo();
         }
      }
   }
}
