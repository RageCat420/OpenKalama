package me.matl114.hacks.modules.interact;


import net.minecraft.client.MinecraftClient;
import me.matl114.events.Event;
import me.matl114.hacks.KalamaHelperHelperBX;
import me.matl114.hacks.KalamaHelperHelperCX;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.RenderTasks;
import me.matl114.hacks.modules.combat.TargetSelector;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.utils.MathUtils;
import me.matl114.utils.entity.PlayerInputUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;

class InteractSubHelperLX implements HackUtilHelperJ {
   private final Interact mG;
   private final double val$attackRange;
   private final Entity et;

   boolean mE;
   Vec3d mC;
   Vec3d mA;
   boolean mD;
   int max;
   Vec3d es;
   Vec3d mB;

   public void applyAfterInputTick(Event<LegalMovementManager> movementManagerEvent) {
      if (this.mE) {
         ClientPlayerEntity var2 = ((LegalMovementManager)movementManagerEvent.b).c.a;
         if (this.mC != null && !var2.isFallFlying()) {
            PlayerInputUtils.a(MinecraftClient.getInstance().player).rD(false).applyInput(MinecraftClient.getInstance().player);
            ((LegalMovementManager)movementManagerEvent.b).a();
         }
      }
   }

   @Override
   public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
      if (!this.mE) {
         return this.max >= 0;
      } else {
         ClientPlayerEntity var3 = ((LegalMovementManager)movementManagerEvent.b).c.a;
         if (this.mD) {
            this.mG.postInteract(var3, this.et);
            if (this.mA != Vec3d.ZERO) {
               Vec3d var4 = var3.getPos().subtract(this.mB);
               var3.setPosition(this.mA);
               var3.move(MovementType.PLAYER, var4);
               this.mA = this.mB = Vec3d.ZERO;
            }
         }

         return false;
      }
   }

   @Override
   public int priority() {
      return -100000;
   }

   InteractSubHelperLX(final Interact this$0, final Entity param2, final double nullx) {
      this.mG = this$0;
      this.et = param2;
      this.val$attackRange = nullx;
      this.mA = Vec3d.ZERO;
      this.mB = Vec3d.ZERO;
      this.mD = true;
      this.mE = true;
      this.max = 10;
   }

   public void applyPreTickModify(Event<LegalMovementManager> movementManagerEvent) {
      this.mE = true;
      ClientPlayerEntity var2 = ((LegalMovementManager)movementManagerEvent.b).c.a;
      this.es = var2.getVelocity();
      Vec3d var3 = TargetSelector.INSTANCE.akn(MinecraftClient.getInstance().player.getPos(), this.et.getBoundingBox());
      if (var2.isFallFlying()) {
         var3 = var3.add(MinecraftClient.getInstance().player.getVelocity());
      }

      Vec3d var4 = var2.getPos();
      if (this.mG.tpInteractRange.get().positive() && this.et.getBoundingBox().squaredMagnitude(var3) > MathUtils.a(this.val$attackRange)) {
         Vec3d var5 = MovTasks.tpAttackSearch(var4, this.et.getBoundingBox(), this.val$attackRange, 9.9, 1).stream().findFirst().orElse(null);
         if (var5 != null && var5.squaredDistanceTo(var4) > 1.0E-7) {
            this.mA = var4;
            this.mB = var5;
            var2.setPosition(var5.add(0.0, 9.0E-8, 0.0));
            var3 = TargetSelector.INSTANCE.akn(var2.getPos(), this.et.getBoundingBox());
         }

         if (!TargetSelector.INSTANCE.akm(var2.getPos(), this.et.getBoundingBox(), this.val$attackRange)) {
            this.mD = false;
            ((LegalMovementManager)movementManagerEvent.b).c.d();
            var2.setPosition(var4);
         }
      }

      if (this.mD) {
         Vec3d var11 = this.et.getEyePos();
         Vec3d var6 = this.et.getPos();
         double var7 = this.mG.DO.nextDouble(0.8, 1.0);
         Vec3d var9 = var6.add(var11.subtract(var6).multiply(var7));
         var9.add(this.mG.DO.nextDouble(-0.05, 0.05), this.mG.DO.nextDouble(-0.05, 0.05), this.mG.DO.nextDouble(-0.05, 0.05));
         Vec3d var10 = var9.subtract(var3).normalize();
         ((LegalMovementManager)movementManagerEvent.b).pushImportantRotation(true, true);
         PlayerStateManager.setPlayerRotationSafe(var2, var10);
         if (RenderTasks.f) {
            RenderTasks.i(new KalamaHelperHelperCX(RenderTasks.DEBUG_TICK, new KalamaHelperHelperBX(var3, var10)));
         }

         this.mC = var10;
         ((LegalMovementManager)movementManagerEvent.b).c();
      }

      var2.setVelocity(this.es);
   }
}
