package me.matl114.hacks.modules.move;


import net.minecraft.client.MinecraftClient;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.events.Event;
import me.matl114.hacks.MovTasks$MovInfo;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.hooks.ViaFabricPlusHooks;
import me.matl114.utils.Debug;
import me.matl114.versioned.api.VPacket;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class MoveSubHelperEX extends MoveSubHelperY {
   boolean fm;
   boolean fl = false;

   @Override
   public void ji(Event<MovTasks$MovInfo> setBack) {
      super.ji(setBack);
      if (this.fl) {
         this.fl = false;
         this.fm = true;
      }
   }

   public void applyBeforeMovementPacketModify(Event<LegalMovementManager> movementManagerEvent) {
      ClientPlayerEntity var2 = ((LegalMovementManager)movementManagerEvent.b).c.a;
      if (this.module.isActive()) {
         boolean var3 = var2.getY() <= this.module.Uw - this.module.UF;
         if (MinecraftClient.getInstance().player.isOnGround()) {
            this.fl = false;
         }

         if (var3 && !var2.isOnGround()) {
            ((LegalMovementManager)movementManagerEvent.b).c.restorePos();
            this.fl = true;
            this.module.Uw = var2.getY();
            Vec3d var4 = var2.getRotationVector();
            MinecraftClient.getInstance().player.setOnGround(true);
            MinecraftClient.getInstance().player.setPosition(MinecraftClient.getInstance().player.getPos().add(0.0, 9.0E-8, 0.0));
            MinecraftClient.getInstance().getNetworkHandler().sendPacket(LegacySnapRotManager.INSTANCE.ahv(var4, true));
            LegacySnapRotManager.INSTANCE.ahs(var4, true);
            movementManagerEvent.cancel();
         }

         if (this.fl) {
            MinecraftClient.getInstance().player.setOnGround(true);
         }
      }
   }

   public void applyPreTickModify(Event<LegalMovementManager> movementManagerEvent) {
      this.checkVersion();
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

      if (this.fm) {
         Vec3d var4 = var2.getRotationVector();
         MinecraftClient.getInstance().getNetworkHandler().sendPacket(LegacySnapRotManager.INSTANCE.ahv(var4, true));
         MinecraftClient.getInstance().player.setOnGround(false);
         this.fm = false;
      }
   }

   @Override
   public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
      return true;
   }

   public MoveSubHelperEX(NoFall module) {
      super(module);
      this.fm = false;
   }

   public void checkVersion() {
      if (!ViaFabricPlusHooks.isSupportDupRot()) {
         Debug.b("[NoFall] 该模式需要via切换至1.20.6以下,已自动切换至其他模式");
         this.module.bypassMode.set(NoFall$Mode.LAZY_GRIM_PLUS);
      }
   }
}
