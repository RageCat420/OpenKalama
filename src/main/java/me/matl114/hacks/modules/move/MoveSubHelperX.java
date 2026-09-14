package me.matl114.hacks.modules.move;


import net.minecraft.client.MinecraftClient;
import me.matl114.events.Event;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.utils.move.FlightVelocity;
import me.matl114.hacks.utils.move.HackUtilHelperA;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction.Axis;

class MoveSubHelperX implements MoveSubHelperAh {
   int tickCNT;
   private Vec3d LK;

   public MoveSubHelperX(final Travel param1) {
      this.HP = var1;
      this.LK = null;
      this.tickCNT = 0;
   }

   public void onElytra(Event<KalamaHelperHelperI<FlightVelocity>> event) {
      if (this.LK != null) {
         KalamaHelperHelperI var2 = (KalamaHelperHelperI)event.e();
         if (((FlightVelocity)var2.b()).g() == HackUtilHelperA.rj) {
            FlightVelocity var3 = (FlightVelocity)var2.b();
            Vec3d var4 = this.LK.subtract(MinecraftClient.getInstance().player.getPos()).normalize().multiply(this.HP.speed.get());
            var3.h(var4.x).i(var4.y).j(var4.z);
         }
      }
   }

   @Override
   public TravellingControl$Type getType() {
      return TravellingControl$Type.ELYTRASKY;
   }

   @Override
   public boolean onTick(Event<Void> event) {
      MoveSubHelperSX var2 = Travel.CW;
      MinecraftClient.getInstance().player.setOnGround(false);
      this.tickCNT++;
      double var3 = MinecraftClient.getInstance().player.getY();
      double var5 = this.HP.elytraSpeed.get() * 10.0;
      if (var2.d == null) {
         if (var3 < this.HP.minHeight.get()) {
            var2.d = MoveSubHelperOX.mr;
         } else if (var3 > this.HP.maxHeight.get()) {
            var2.d = MoveSubHelperOX.ms;
         } else {
            var2.d = MoveSubHelperOX.mt;
         }
      }

      this.HP.QX(var2, var3);
      if (var2.d == MoveSubHelperOX.mt) {
         if (this.HP.QW(var2)) {
            return true;
         }

         Vec3d var7 = MinecraftClient.getInstance().player.getPos();
         Vec3d var8 = var2.getCurrentFlyingTarget().subtract(var7);
         Vec3d var9 = var8.normalize().withAxis(Axis.Y, 0.0).multiply(var5).add(0.0, -0.05, 0.0);
         this.LK = var7.add(var9.multiply(10.0));
      } else {
         double var10;
         if (var2.d == MoveSubHelperOX.mr) {
            var10 = this.HP.maxHeight.get();
         } else {
            var10 = this.HP.minHeight.get();
         }

         double var14;
         if (this.tickCNT % 20 < 18) {
            double var12 = Math.signum(var10 - var3);
            var14 = var12 * this.HP.elytraSpeed.get() * 10.0;
         } else {
            var14 = -0.3;
         }

         this.LK = MinecraftClient.getInstance().player.getPos().add(0.0, var14, 0.0);
      }

      return false;
   }
   Travel HP;
}
