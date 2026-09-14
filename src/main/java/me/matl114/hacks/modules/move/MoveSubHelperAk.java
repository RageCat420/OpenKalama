package me.matl114.hacks.modules.move;


import net.minecraft.client.MinecraftClient;
import me.matl114.events.Event;
import me.matl114.hacks.MovTasks;
import net.minecraft.util.math.Vec3d;

class MoveSubHelperAk implements MoveSubHelperAh {
   private final Travel this$0;

   int HN;
   int HO;

   public MoveSubHelperAk(final Travel param1) {
      this.this$0 = param1;
      this.HO = 0;
   }

   @Override
   public void onStop() {
      MovTasks.doingTp = false;
   }

   @Override
   public TravellingControl$Type getType() {
      return TravellingControl$Type.MOV_VOID;
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
         this.this$0.QX(var2, var3);
         double var5 = this.this$0.speed.get();
         if (var2.d == MoveSubHelperOX.mt) {
            Vec3d var7 = var2.getCurrentFlyingTarget().subtract(MinecraftClient.getInstance().player.getPos());
            Vec3d var8 = new Vec3d(var7.x, 0.0, var7.z).normalize();
            if (this.this$0.QV(var2, var8.multiply(var5).add(0.0, -0.05, 0.0))) {
               return true;
            }

            if (this.this$0.QV(var2, var8.multiply(var5).add(0.0, -0.05, 0.0))) {
               return true;
            }

            if (this.HN % 3 == 0 && this.this$0.QV(var2, var8.multiply(var5).add(0.0, -0.05, 0.0))) {
               return true;
            }
         } else {
            double var9 = var2.d == MoveSubHelperOX.mr ? this.this$0.maxHeight.get() : this.this$0.minHeight.get();
            double var13;
            if (this.HN % 20 < 18) {
               double var11 = Math.signum(var9 - var3);
               var13 = var11 * this.this$0.speed.get();
            } else {
               var13 = -0.3;
            }

            Vec3d var15 = new Vec3d(0.0, var13, 0.0);
            if (this.this$0.QV(var2, var15)) {
               return true;
            }
         }

         MovTasks.doingTp = true;
         return false;
      }
   }
}
