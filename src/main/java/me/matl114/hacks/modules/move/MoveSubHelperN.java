package me.matl114.hacks.modules.move;


import net.minecraft.client.MinecraftClient;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.events.Event;
import me.matl114.hacks.MovTasks$MovInfo;
import me.matl114.hacks.utils.entity.EntityMovementStatus;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.managers.Tasks;
import me.matl114.utils.entity.PlayerInputUtils;
import me.matl114.utils.entity.PlayerInputUtils$Input;
import me.matl114.versioned.api.VPacket;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction.Axis;

public class MoveSubHelperN extends MoveSubHelperY {
   int Fq;
   Vec3d Fk;
   MoveSubHelperMX Fo;
   Vec3d Fl;
   int Fj = 0;
   int Fp;
   boolean vq;
   PlayerInputUtils$Input Fs;
   private static final int latency = 5;
   boolean vu;
   int vw;
   Packet<?> vD;
   boolean Ft;
   int Fm;
   float modifyYaw;
   int Fu;
   PlayerInputUtils$Input Fn;
   boolean vv;
   int vx;

   @Override
   public void jy(Event<Vec3d> playerVec) {
   }

   @Override
   public void AH(Event<Integer> jumpCooldown) {
      if (this.Ft) {
         jumpCooldown.context(0);
      }
   }

   @Override
   public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
      if (this.vD != null) {
         MinecraftClient.getInstance().player.setOnGround(true);
         MinecraftClient.getInstance().getNetworkHandler().sendPacket(this.vD);
      }

      this.vD = null;
      if (this.Md) {
      }

      return true;
   }

   public MoveSubHelperN(NoFall module) {
      super(module);
      this.Fk = null;
      this.Fm = 0;
      this.Fo = MoveSubHelperMX.lq;
      this.Fp = 0;
      this.Fq = 0;
      this.modifyYaw = 0.0F;
      this.Fs = null;
      this.Ft = false;
      this.Fu = 0;
      this.vD = null;
      this.vq = true;
   }

   @Override
   public void gz(Event<LegalMovementManager> movementManagerEvent) {
      if (this.Ft && this.Fs != null) {
         if (this.Fp >= 3) {
            this.Fp = 0;
            this.Fu = Tasks.b();
         }

         this.Fs.applyInput(((LegalMovementManager)movementManagerEvent.b).c.a);
      }

      this.Fs = null;
   }

   public void applyBeforeMovementPacketModify(Event<LegalMovementManager> movementManagerEvent) {
      if (this.Fu == Tasks.b()) {
         movementManagerEvent.cancel();
      }

      if (this.Ft) {
         this.Ft = false;
      }

      if (this.module.isActive()) {
         EntityMovementStatus var2 = ((LegalMovementManager)movementManagerEvent.b).c;
         if (this.Fo == MoveSubHelperMX.ls) {
            this.Fo = MoveSubHelperMX.lq;
         } else if (this.Fo != MoveSubHelperMX.lq && this.Fo != null) {
            if (this.Fo == MoveSubHelperMX.lr) {
            }
         } else {
            boolean var3 = ((ClientPlayerEntity)var2.a).getY() <= this.module.Uw - this.module.UF && Tasks.b() > this.Fj + 5;
            if (var3 && !var2.b && ((ClientPlayerEntity)var2.a).isOnGround()) {
               this.vq = false;
               this.counter = 0;
               this.module.Uw = var2.g.getY();
               Vec3d var4 = ((LegalMovementManager)movementManagerEvent.b).c.g;
               this.vD = VPacket.f(true, var2.c);
               movementManagerEvent.cancel();
               this.Fk = MinecraftClient.getInstance().player.getPos();
               this.Fj = Tasks.b();
               MinecraftClient.getInstance().player.setPosition(((LegalMovementManager)movementManagerEvent.b).c.g.withAxis(Axis.Y, MinecraftClient.getInstance().player.getY()));
               this.Fo = MoveSubHelperMX.lr;
               this.Mc = true;
               MinecraftClient.getInstance().player.setOnGround(true);
               this.Fn = PlayerInputUtils.a(MinecraftClient.getInstance().player);
               this.Fn.rx(false).ry(false).rz(false).rA(false).rB(false);
            }
         }
      }
   }

   public void onSetback(Event<MovTasks$MovInfo> setBack) {
      this.vq = true;
      Vec3d var2 = ((MovTasks$MovInfo)setBack.b).vec3d();
      if (this.Fk != null && this.Fk.squaredDistanceTo(var2) < 1.0 && Tasks.b() < this.Fj + 5) {
         this.Fk = null;
         this.Fl = var2;
         this.Fm = Tasks.b();
         this.Fo = MoveSubHelperMX.lr;
      }

      super.ji(setBack);
   }

   public void applyPreTickModify(Event<LegalMovementManager> movementManagerEvent) {
      boolean var2 = false;
      ClientPlayerEntity var3 = ((LegalMovementManager)movementManagerEvent.b).c.a;
      boolean var4 = ClientPlayerAccess.of(var3).isForceNoFall();
      if (var4) {
         this.Md = true;
         this.counter = 0;
         this.module.Uw = this.module.Uz;
         MinecraftClient.getInstance().getNetworkHandler().sendPacket(VPacket.g(var3.getX(), this.module.Uz + 9.0E-8, var3.getZ(), false, var3.horizontalCollision));
         this.Mc = true;
         ClientPlayerAccess.of(var3).setForceNoFall(false);
      } else if (this.module.isActive()) {
         this.counter++;
      }

      if (this.counter > 100) {
         this.Mc = false;
      }

      if (this.module.isActive()) {
         ClientPlayerEntity var5 = ((LegalMovementManager)movementManagerEvent.b).c.a;
         PlayerInputUtils$Input var6 = PlayerInputUtils.of(MinecraftClient.getInstance().options);
         PlayerInputUtils$Input var7 = var6.rw();
         boolean var8 = false;
         if (this.Fo == MoveSubHelperMX.lr) {
            if (this.Fj + 10 >= Tasks.b()) {
               if (this.Fl != null && Tasks.b() <= this.Fm + 1) {
                  var8 = true;
                  this.Fo = MoveSubHelperMX.ls;
                  MinecraftClient.getInstance().player.setPosition(this.Fl);
                  this.Fk = this.Fl;
                  this.Fj = Tasks.b();
                  this.Fl = null;
                  MinecraftClient.getInstance().player.setOnGround(true);
                  var6 = var6.rw();
                  var6.rB(true).rx(false).ry(false).rz(false).rA(false).rD(false);
                  MoveSubHelperV var9 = NoFall.applyInputWay(var5);
                  if (!var9.aar()) {
                     var6.rx(false);
                  } else {
                     var6.rx(true);
                     if (var7.ru()) {
                        if (var7.rE()) {
                           var2 = true;
                           PlayerStateManager.nT(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player.getYaw() + 180.0F);
                        } else if (!var7.rF()) {
                           if (var7.rG()) {
                              var2 = true;
                              PlayerStateManager.nT(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player.getYaw() + 90.0F);
                           } else if (var7.rH()) {
                              var2 = true;
                              PlayerStateManager.nT(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player.getYaw() - 90.0F);
                           }
                        }
                     } else if (var9.forward()) {
                        if (!var9.backward()) {
                           var2 = true;
                           PlayerStateManager.nT(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player.getYaw() + 180.0F);
                        } else if (!var9.left()) {
                           var2 = true;
                           PlayerStateManager.nT(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player.getYaw() - 90.0F);
                        } else if (!var9.forward()) {
                           var2 = true;
                           PlayerStateManager.nT(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player.getYaw() + 90.0F);
                        }
                     }
                  }

                  this.Fn = var6.rw();
                  if (var2) {
                     ((LegalMovementManager)movementManagerEvent.b).pushImportantRotation(false, true);
                     this.modifyYaw = MinecraftClient.getInstance().player.getYaw();
                     this.Fq = Tasks.b();
                  }

                  this.Fs = var6;
                  this.Ft = true;
               } else {
                  if (Tasks.b() < this.Fq + 5) {
                     MinecraftClient.getInstance().player.setYaw(this.modifyYaw);
                     var2 = true;
                     ((LegalMovementManager)movementManagerEvent.b).pushImportantRotation(false, true);
                  }

                  if (this.Fj + 2 >= Tasks.b()) {
                     this.Fs = this.Fn.rw();
                     this.Ft = true;
                  }
               }
            } else {
               this.Fo = MoveSubHelperMX.lq;
            }
         } else if (this.Fo == MoveSubHelperMX.ls) {
            this.Fo = MoveSubHelperMX.lq;
         }

         this.Fp = Math.max(0, this.Fp + (var8 ? 2 : -1));
      }

      if (var2) {
         ((LegalMovementManager)movementManagerEvent.b).c();
      }
   }
}
