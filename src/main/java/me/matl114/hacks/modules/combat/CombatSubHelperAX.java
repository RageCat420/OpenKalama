package me.matl114.hacks.modules.combat;

import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.ACTasks;
import me.matl114.hacks.CombatTasks;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.utils.Debug;
import me.matl114.utils.EntityUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

class CombatSubHelperAX implements HackUtilHelperJ {
   private final float val$initialVelocity;
   private final PlayerActionC2SPacket ac;
   private final Entity aa;

   @Override
   public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
      ACTasks.c(handler -> Listener.G(handler.getConnection(), this.ac));
      return false;
   }

   @Override
   public int priority() {
      return -100000;
   }

   CombatSubHelperAX(final BowEnhance this$0, final Entity param2, final float nullx, final PlayerActionC2SPacket nullxx) {
      this.aa = param2;
      this.val$initialVelocity = nullx;
      this.ac = nullxx;
   }

   public void applyPreTickModify(Event<LegalMovementManager> movementManagerEvent) {
      ClientPlayerEntity var2 = ((LegalMovementManager)movementManagerEvent.e()).c.a;
      Vec3d var3 = CombatTasks.m().predictAimPositionForEntity(this.aa, this.val$initialVelocity);
      Vec3d var4 = var3.subtract(var2.getEyePos());
      Vec2f var5 = CombatTasks.calculatePitchYawPredict(this.val$initialVelocity, var2.getVelocity(), var4);
      if (!Float.isNaN(var5.x) && !Float.isInfinite(var5.x) && !Float.isNaN(var5.y) && !Float.isInfinite(var5.y)) {
         ((LegalMovementManager)movementManagerEvent.b).pushImportantRotation(true, true);
         EntityUtils.setEntityPitchSafe(var2, var5.x);
         PlayerStateManager.nT(var2, var5.y);
         ((LegalMovementManager)movementManagerEvent.b).c();
      } else {
         Debug.b("[Bow Aim] Arrow failed to reach the target");
      }
   }
}
