package me.matl114.hacks.modules.move;

import me.matl114.events.Event;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.entity.HackUtilHelperD;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.managers.Configs;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.CollisionUtil;
import me.matl114.utils.entity.PlayerInputUtils;
import me.matl114.utils.entity.PlayerInputUtils$Input;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;

public class ElytraJump extends BaseModule implements HackUtilHelperJ {
   public final FlagRef sneak;
   public final DoubleRef groundHeight;
   int zw;
   public final KeyBindRef J;
   boolean jn;
   public final FlagRef ae;
   boolean zv;
   boolean zx;
   public final FlagRef conditionalSprint;
   static HackUtilHelperD cy;
   ModulePath aD = makePath(Configs.m, "elytra.elytra-flight-legit.elytra-jump");
   public final DoubleRef pitch;

   public ElytraJump() {
      super("ElytraJump");
      this.ae = this.flagBuilder(this.aD.addEnable()).build();
      this.J = this.toggleHotkey(this.aD.addHotkey(), new MultiKeyBind(), this.aD.addEnable()).build();
      this.conditionalSprint = this.flagBuilder(this.aD.add("conditional-sprint")).build();
      this.pitch = this.doubleBuilder(this.aD.add("pitch")).defaultValue(80.0).build();
      this.sneak = this.flagBuilder(this.aD.add("sneak")).build();
      this.groundHeight = this.doubleBuilder(this.aD.add("ground-height")).defaultValue(3.0).build();
      this.zv = false;
      this.jn = false;
      this.zw = 0;
      this.zx = false;
      if (cy == null) {
         cy = new HackUtilHelperD(this::cast);
         MovTasks.j.SJ(() -> cy);
      }

      cy.mN(this::cast);
      this.bindFlag(this.ae);
   }

   @Override
   public void bb(Event<LegalMovementManager> movementManagerEvent) {
      this.zv = false;
      if (this.ae.get()) {
         this.zv = !CollisionUtil.getIntersectingBlockPositions(mc.world, mc.player.getBoundingBox().stretch(0.0, -this.groundHeight.get(), 0.0), false)
            .isEmpty();
      }

      if (this.zv && (mc.player.isFallFlying() || this.zx)) {
         mc.player.setPitch((float)(Object)this.pitch.get());
         ((LegalMovementManager)movementManagerEvent.b).c();
      }
   }

   @Override
   public void gz(Event<LegalMovementManager> movementManagerEvent) {
      if (this.zv) {
         PlayerInputUtils$Input var2 = PlayerInputUtils.a(mc.player);
         if (mc.player.isOnGround()) {
            var2.rB(true).rD(true).rx(true).rC(this.sneak.get()).applyInput(mc.player);
            mc.player.setSprinting(true);
            this.zw = 0;
         } else {
            if (!mc.player.isFallFlying() && mc.player.checkFallFlying()) {
               MovTasks.aj().Xj();
               mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
               MovTasks.aj().sendPacketsForPreStartFallFlying();
            }

            var2.rD(true).rB(false).rx(true).rC(this.sneak.get()).applyInput(mc.player);
         }

         this.zw++;
      }

      this.jn = mc.player.isOnGround();
      this.zx = mc.player.isFallFlying();
   }
}
