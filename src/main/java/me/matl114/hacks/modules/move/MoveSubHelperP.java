package me.matl114.hacks.modules.move;


import net.minecraft.client.MinecraftClient;
import me.matl114.events.Event;
import me.matl114.hacks.MovTasks;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;

class MoveSubHelperP implements MoveSubHelperAh {
   boolean catchResyncPackets;
   int HO;
   MoveSubHelperSX HM;
   int HN;

   public void QI(Event<PlayerPositionLookS2CPacket> event) {
      this.catchResyncPackets = true;
   }

   @Override
   public boolean onTick(Event<Void> event) {
      MovTasks.doingTp = false;
      MoveSubHelperSX var2 = Travel.CW;
      MinecraftClient.getInstance().player.setOnGround(false);
      if (++this.HO < 2) {
         return false;
      } else {
         this.HO = 0;
         this.HN++;
         double var3 = MinecraftClient.getInstance().player.getY();
         this.HP.QX(var2, var3);
         double var5 = this.HP.speed.get() - 0.05;
         if (var2.d == MoveSubHelperOX.mt) {
            if (this.HP.QW(var2)) {
               return true;
            }

            Vec3d var7 = var2.getCurrentFlyingTarget().subtract(MinecraftClient.getInstance().player.getPos());
            var7 = new Vec3d(var7.x, 0.0, var7.z);
            double var8 = var7.horizontalLengthSquared();
            Vec3d var10 = var7.normalize();
            Vec3d var11 = var10.multiply(var5).add(0.0, -0.05, 0.0).multiply(this.HP.void2DupPacket.get());
            if (var11.horizontalLengthSquared() > var8) {
               var11 = var7;
            }

            Vec3d var12 = MinecraftClient.getInstance().player.getPos().add(var11);
            if (this.catchResyncPackets) {
               this.catchResyncPackets = false;
            } else {
               MovTasks.executeTp(var12, 200.0, false, false);
               MovTasks.X(var12);
            }
         } else {
            double var13 = var2.d == MoveSubHelperOX.mr ? this.HP.maxHeight.get() : this.HP.minHeight.get();
            double var17;
            if (this.HN % 20 < 18) {
               double var15 = Math.signum(var13 - var3);
               var17 = var15 * this.HP.speed.get();
            } else {
               var17 = -0.3;
            }

            Vec3d var20 = new Vec3d(0.0, var17, 0.0);
            if (this.HP.QV(var2, var20)) {
               return true;
            }
         }

         MovTasks.doingTp = true;
         return false;
      }
   }

   @Override
   public void onStart(MoveSubHelperSX state) {
      this.HM = state;
   }

   @Override
   public void onStop() {
      MovTasks.doingTp = false;
   }

   public MoveSubHelperP(final Travel param1) {
      this.HP = param1;
      this.catchResyncPackets = false;
      this.HN = 0;
      this.HO = 0;
   }

   @Override
   public TravellingControl$Type getType() {
      return TravellingControl$Type.MOV_VOID_2;
   }
   Travel HP;
}
