package me.matl114.hacks.modules.move;


import net.minecraft.client.MinecraftClient;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.events.Event;
import me.matl114.hacks.MovTasks$MovInfo;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.versioned.api.VPacket;
import net.minecraft.client.network.ClientPlayerEntity;

public class MoveSubHelperAc extends MoveSubHelperY {
   @Override
   public void iC(Event<LegalMovementManager> movementManagerEvent) {
   }

   @Override
   public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
      return true;
   }

   @Override
   public void ji(Event<MovTasks$MovInfo> event) {
      if (this.module.isActive() && (((MovTasks$MovInfo)event.b).oGroundOverride() == null || this.Mc != ((MovTasks$MovInfo)event.b).oGroundOverride())) {
         MovTasks$MovInfo var2 = (MovTasks$MovInfo)event.e();
         event.context(new MovTasks$MovInfo(var2.vec3d(), this.Mc, false, var2.rotationOverride()));
      }

      super.ji(event);
   }

   public MoveSubHelperAc(NoFall module) {
      super(module);
   }

   public void applyPreTickModify(Event<LegalMovementManager> movementManagerEvent) {
      ClientPlayerEntity var2 = ((LegalMovementManager)movementManagerEvent.b).c.a;
      boolean var3 = ClientPlayerAccess.of(var2).isForceNoFall();
      if (this.module.isActive() || var3) {
         if (!var3 && !this.module.amh()) {
            this.counter++;
         } else if (!this.module.holdingMace) {
            this.Md = true;
            this.counter = 0;
            this.module.Uw = this.module.Uz;
            var2.setPosition(var2.getPos().add(0.0, 1.0E-8, 0.0));
            MinecraftClient.getInstance()
               .getNetworkHandler()
               .sendPacket(VPacket.g(var2.getX(), this.module.Uz, var2.getZ(), !var3 && var2.isOnGround(), var2.horizontalCollision));
            this.Mc = true;
         }

         if (var3) {
            ClientPlayerAccess.of(var2).setForceNoFall(false);
         }

         if (this.counter > 100) {
            this.Mc = false;
         }
      }
   }
}
