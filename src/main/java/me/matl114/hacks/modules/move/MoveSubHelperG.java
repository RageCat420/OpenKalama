package me.matl114.hacks.modules.move;


import net.minecraft.client.MinecraftClient;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.events.Event;
import me.matl114.hacks.MovTasks$MovInfo;
import me.matl114.hacks.utils.entity.EntityMovementStatus;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.managers.Tasks;
import me.matl114.versioned.api.VPacket;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class MoveSubHelperG extends MoveSubHelperY {
   Vec3d vr;
   boolean vv;
   int vt;
   int vz;
   int vx;
   boolean vq;
   int vw;
   int vo = -1;
   boolean vu;
   Vec3d vy;
   private static final int latency = 10;
   Boolean vs;
   boolean AN;
   int vp = -1;

   @Override
   public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
      if (this.vs != null) {
         if (!this.Md) {
            ((LegalMovementManager)movementManagerEvent.b).c.a.setOnGround(this.vs);
         }

         this.vs = null;
      }

      if (this.Md) {
      }

      return true;
   }

   public void applyBeforeMovementPacketModify(Event<LegalMovementManager> movementManagerEvent) {
      if (this.module.isActive()) {
         EntityMovementStatus var2 = ((LegalMovementManager)movementManagerEvent.b).c;
         this.AN = false;
         if (this.vr != null
            && Math.abs(this.vr.y - ((ClientPlayerEntity)var2.a).getY()) < 0.01
            && ((ClientPlayerEntity)var2.a).getPos().squaredDistanceTo(this.vr) < 1.0
            && this.vp + 10 >= Tasks.b()) {
            this.AN = true;
         }

         boolean var3;
         if (this.AN) {
            var3 = true;
         } else {
            var3 = this.vp + 10 >= Tasks.b() || this.vq && this.vp + 100 >= Tasks.b() || ((ClientPlayerEntity)var2.a).getY() <= this.module.Uw - this.module.UF;
         }

         boolean var4 = this.AN || this.vp + 10 >= Tasks.b();
         if (!var4) {
            this.vt = 0;
         }

         if (var3 && !this.Md) {
            if (!var4) {
               this.vq = false;
            }

            if (!var2.b && ((ClientPlayerEntity)var2.a).isOnGround()) {
               if (this.AN) {
                  this.vt++;
               } else {
                  this.vt = 1;
               }

               if (this.vt > 20) {
                  this.vp = -1;
                  this.vr = null;
                  this.vt = 0;
                  return;
               }

               this.vq = false;
               this.Md = true;
               this.vp = Tasks.b();
               this.counter = 0;
               this.vr = ((ClientPlayerEntity)var2.a).getPos();
               this.module.Uw = var2.g.getY();
               MinecraftClient.getInstance().getNetworkHandler().sendPacket(VPacket.g(var2.g.getX(), var2.g.getY() + 9.0E-8, var2.g.getZ(), false, var2.c));
               this.Mc = true;
               return;
            }
         }
      }
   }

   @Override
   public void ji(Event<MovTasks$MovInfo> setBack) {
      this.vq = true;
      this.vo = Tasks.b();
      Vec3d var2 = ((MovTasks$MovInfo)setBack.b).vec3d();
      boolean var3 = this.vy != null && this.vy.squaredDistanceTo(var2) < 1.0E-12;
      if (var3) {
         this.vz++;
         if (this.vz > 3) {
            this.vz = 0;
            this.vt = 100;
         }
      } else {
         this.vz = 0;
      }

      this.vy = var2;
      super.ji(setBack);
   }

   public void onVelocity(Event<Vec3d> playerVec) {
      if (this.module.isActive() && Tasks.b() <= this.vp + 10) {
         Vec3d var2 = (Vec3d)playerVec.e();
         if (var2.y < 0.0) {
            playerVec.context(new Vec3d(var2.x, 0.0, var2.z));
         }
      }
   }

   public MoveSubHelperG(NoFall module) {
      super(module);
      this.vr = null;
      this.AN = false;
      this.vt = 0;
      this.vz = 0;
      this.vq = true;
      this.vt = 0;
   }

   public void applyAfterInputTick(Event<LegalMovementManager> movementManagerEvent) {
      if (this.module.isActive() && this.vt >= 1) {
         Input var2 = ((LegalMovementManager)movementManagerEvent.b).c.a.input;
         var2.movementForward = 0.0F;
         var2.movementSideways = 0.0F;
      }
   }

   public void applyPreTickModify(Event<LegalMovementManager> movementManagerEvent) {
      ClientPlayerEntity var2 = ((LegalMovementManager)movementManagerEvent.b).c.a;
      boolean var3 = ClientPlayerAccess.of(var2).isForceNoFall();
      if (var3) {
         this.Md = true;
         this.counter = 0;
         this.module.Uw = this.module.Uz;
         MinecraftClient.getInstance().getNetworkHandler().sendPacket(VPacket.g(var2.getX(), this.module.Uz + 9.0E-8, var2.getZ(), false, var2.horizontalCollision));
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
