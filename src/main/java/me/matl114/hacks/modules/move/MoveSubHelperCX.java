package me.matl114.hacks.modules.move;


import net.minecraft.client.MinecraftClient;
import me.matl114.events.Event;
import me.matl114.hacks.ACTasks;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.utils.Debug;
import me.matl114.utils.entity.PlayerInputUtils;
import me.matl114.utils.entity.PlayerInputUtils$Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket.InteractAtHandler;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

class MoveSubHelperCX implements HackUtilHelperJ {
   private final NoSlowDown eu;
   private final Entity et;

   Vec3d es;

   @Override
   public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
      if (!enabledThisTick) {
         return false;
      } else {
         PlayerInputUtils$Input var3 = PlayerInputUtils.a(MinecraftClient.getInstance().player);
         ACTasks.c(
            han -> {
               var3.rC(true).sendPlayerSneakUpdatePacket();
               var3.rC(false).sendPlayerSneakUpdatePacket();
               MinecraftClient.getInstance().getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
               MinecraftClient.getInstance()
                  .interactionManager
                  .sendSequencedPacket(
                     MinecraftClient.getInstance().world,
                     seq -> new PlayerInteractEntityC2SPacket(target.getId(), true, new InteractAtHandler(Hand.MAIN_HAND, MinecraftClient.getInstance().player.getPos()))
                  );
               MinecraftClient.getInstance().getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
               Debug.b("[NoSlow] 成功伪造状态");
               this.eu.Gs = true;
            }
         );
         return false;
      }
   }

   public void applyPreTickModify(Event<LegalMovementManager> movementManagerEvent) {
      ClientPlayerEntity var2 = ((LegalMovementManager)movementManagerEvent.b).c.a;
      this.es = var2.getVelocity();
      Vec3d var3 = this.et.getEyePos();
      Vec3d var4 = this.et.getPos();
      Vec3d var5 = var4.add(var3.subtract(var4).multiply(0.8));
      Vec3d var6 = var5.subtract(var2.getEyePos()).normalize();
      ((LegalMovementManager)movementManagerEvent.b).pushImportantRotation(true, true);
      PlayerStateManager.setPlayerRotationSafe(var2, var6);
      var2.setVelocity(this.es);
      ((LegalMovementManager)movementManagerEvent.b).c();
   }

   MoveSubHelperCX(final NoSlowDown this$0, final Entity param2) {
      this.eu = this$0;
      this.et = param2;
   }

   @Override
   public int priority() {
      return -100000;
   }
}
