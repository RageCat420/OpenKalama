package me.matl114.hacks.modules.combat;


import net.minecraft.client.MinecraftClient;
import me.matl114.accessors.hacks.EntityInternalAccess;
import me.matl114.events.Event;
import me.matl114.hacks.ACTasks;
import me.matl114.hacks.KalamaHelperHelperBX;
import me.matl114.hacks.KalamaHelperHelperCX;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.RenderTasks;
import me.matl114.hacks.modules.move.ElytraExtra;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.managers.Tasks;
import me.matl114.utils.MathUtils;
import me.matl114.utils.entity.PlayerInputUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

class CombatSubHelperZX implements HackUtilHelperJ {
   public me.matl114.hacks.modules.move.ElytraExtra elytraExtra;
   private final Attack yO;
   private final boolean yH;
   private final Entity et;
   private final double mF;
   private final CombatSubHelperPX yI;
   private final Box yJ;
   private final boolean yK;
   private final boolean yL;
   private final ElytraExtra yM;
   private final int yN;
   private final boolean yG;

   Vec3d mB;
   Vec3d mC;
   Vec3d mA;
   Vec3d es;
   boolean mE;
   boolean mD;
   boolean yF;
   int max;

   @Override
   public void gz(Event<LegalMovementManager> movementManagerEvent) {
      if (this.mE) {
         ClientPlayerEntity var2 = ((LegalMovementManager)movementManagerEvent.b).c.a;
         if (!var2.isFallFlying()) {
            PlayerInputUtils.a(MinecraftClient.getInstance().player).rD(false).applyInput(MinecraftClient.getInstance().player);
            ((LegalMovementManager)movementManagerEvent.b).a();
         }
      }
   }

   CombatSubHelperZX(
      final Attack this$0,
      final boolean param2,
      final boolean nullx,
      final Entity nullxx,
      final double nullxxx,
      final CombatSubHelperPX nullxxxx,
      final Box nullxxxxx,
      final boolean nullxxxxxx,
      final boolean nullxxxxxxx,
      final ElytraExtra nullxxxxxxxx,
      final int nullxxxxxxxxx
   ) {
      this.yO = this$0;
      this.yG = param2;
      this.yH = nullx;
      this.et = nullxx;
      this.mF = nullxxx;
      this.yI = nullxxxx;
      this.yJ = nullxxxxx;
      this.yK = nullxxxxxx;
      this.yL = nullxxxxxxx;
      this.yM = nullxxxxxxxx;
      this.yN = nullxxxxxxxxx;
      this.mA = Vec3d.ZERO;
      this.mB = Vec3d.ZERO;
      this.mD = true;
      this.yF = false;
      this.mE = true;
      this.max = 10;
   }

   @Override
   public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
      if (!this.mE) {
         return this.max >= 0;
      } else {
         ClientPlayerEntity var3 = ((LegalMovementManager)movementManagerEvent.b).c.a;
         if (this.mD && !this.yF) {
            if (!this.yK) {
               this.yO.applyPostAttack(this.et, this.yI);
            }

            if (this.mA != Vec3d.ZERO) {
               Vec3d var4 = var3.getPos().subtract(this.mB);
               var3.setPosition(this.mA);
               var3.move(MovementType.PLAYER, var4);
               this.mA = this.mB = Vec3d.ZERO;
            }
         }

         if (this.yG && !this.yK) {
            if (this.yL) {
               ACTasks.c(ch -> {
                  if (elytraExtra.afV()) {
                     MinecraftClient.getInstance().getNetworkHandler().sendPacket(new ClientCommandC2SPacket(MinecraftClient.getInstance().player, Mode.START_FALL_FLYING));
                     elytraExtra.afN(elytraExtra.PI);
                     elytraExtra.PH = -1;
                     EntityInternalAccess.of(MinecraftClient.getInstance().player).setDataFlag(7, true);
                  }
               });
            } else if (this.yN != -1) {
               ACTasks.c(ch -> {
                  elytraExtra.afN(elytraSlot);
                  if (!MinecraftClient.getInstance().player.isFallFlying()) {
                     ch.sendPacket(new ClientCommandC2SPacket(MinecraftClient.getInstance().player, Mode.START_FALL_FLYING));
                  }
               });
            }
         }

         Tasks.p(() -> this.yO.delayAttacking = false, 0);
         return false;
      }
   }

   @Override
   public int priority() {
      return -100000;
   }

   @Override
   public void bb(Event<LegalMovementManager> movementManagerEvent) {
      this.mE = true;
      ClientPlayerEntity var2 = ((LegalMovementManager)movementManagerEvent.b).c.a;
      if (this.yG && var2.isFallFlying()) {
         this.max--;
         this.mE = false;
      } else {
         this.es = var2.getVelocity();
         if (this.yH && !this.yF && TargetSelector.INSTANCE.akm(var2.getPos(), this.et.getBoundingBox(), this.mF)) {
            Vec3d var3 = TargetSelector.INSTANCE.akn(var2.getPos(), this.et.getBoundingBox());
            Vec3d var4 = var3.subtract(var2.getEyePos()).normalize();
            ((LegalMovementManager)movementManagerEvent.b).pushImportantRotation(true, true);
            PlayerStateManager.setPlayerRotationSafe(var2, var4);
            ((LegalMovementManager)movementManagerEvent.b).c();
            Attack.Ye(MinecraftClient.getInstance().player, this.et, this.yI);
            this.yF = true;
         }

         Vec3d var7 = TargetSelector.INSTANCE.akn(MinecraftClient.getInstance().player.getPos(), this.yJ);
         if (this.yG || var2.isFallFlying()) {
            var7 = var7.add(MinecraftClient.getInstance().player.getVelocity());
         }

         Vec3d var8 = var2.getPos();
         if (this.yO.tpReach.get().positive() && this.yJ.squaredMagnitude(var7) > MathUtils.a(this.mF)) {
            Vec3d var5 = MovTasks.tpAttackSearch(var8, this.et.getBoundingBox(), this.mF, 9.9, 1).stream().findFirst().orElse(null);
            if (var5 != null && var5.squaredDistanceTo(var8) > 1.0E-7) {
               this.mA = var8;
               this.mB = var5;
               var2.setPosition(var5.add(0.0, 9.0E-8, 0.0));
               var7 = TargetSelector.INSTANCE.akn(var2.getPos(), this.et.getBoundingBox());
            }

            if (!TargetSelector.INSTANCE.akm(var2.getPos(), this.yJ, this.mF)) {
               this.mD = false;
               ((LegalMovementManager)movementManagerEvent.b).c.d();
               var2.setPosition(var8);
            }
         }

         if (this.mD) {
            Vec3d var9 = this.yJ.getCenter().add(this.yO.DO.nextDouble(-0.05, 0.05), this.yO.DO.nextDouble(-0.05, 0.05), this.yO.DO.nextDouble(-0.05, 0.05));
            Vec3d var6 = var9.subtract(var7).normalize();
            if (!(this.et instanceof LivingEntity)) {
               var6 = this.yO.fixRayCastBigBox(var7, this.et.getBoundingBox(), var6, this.mF);
            }

            ((LegalMovementManager)movementManagerEvent.b).pushImportantRotation(true, true);
            PlayerStateManager.setPlayerRotationSafe(var2, var6);
            if (RenderTasks.f) {
               RenderTasks.i(new KalamaHelperHelperCX(RenderTasks.DEBUG_TICK, new KalamaHelperHelperBX(var7, var6)));
            }

            this.mC = var6;
            ((LegalMovementManager)movementManagerEvent.b).c();
         }

         var2.setVelocity(this.es);
      }
   }
}
