package me.matl114.hacks.modules.move;

import me.matl114.events.Event;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.entity.HackUtilHelperD;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.EntityUtils;

public class ElytraSlowFall extends BaseModule implements HackUtilHelperJ {
   public final ModulePath sn;
   public final FlagRef so;
   public final ModulePath fu = makePath(Configs.m, "velocity-management");
   static HackUtilHelperD cy;
   public final ModulePath sm = this.fu.add("floating-utils");
   public final KeyBindRef sp;

   public ElytraSlowFall() {
      super("ElytraSlowFall");
      this.sn = this.sm.add("elytra-slow-falling");
      this.so = this.flagBuilder(this.sn.addEnable()).build();
      this.sp = this.toggleHotkey(this.sn.addHotkey(), new MultiKeyBind(), this.sn.addEnable()).build();
      if (cy == null) {
         cy = new HackUtilHelperD(this::cast);
         MovTasks.j.SJ(() -> cy);
      }

      cy.mN(this::cast);
      this.bindFlag(this.so);
   }

   @Override
   public void bb(Event<LegalMovementManager> movementManagerEvent) {
      if (this.so.get() && mc.player.isFallFlying() && !mc.player.isOnGround()) {
         boolean var2 = Tasks.b() % 2 == 0;
         ((LegalMovementManager)movementManagerEvent.b).pushImportantRotation(true, var2);
         EntityUtils.setEntityPitchSafe(mc.player, 0.0F);
         if (var2) {
            PlayerStateManager.nT(mc.player, mc.player.getYaw() + 180.0F);
         }

         ((LegalMovementManager)movementManagerEvent.b).c();
      }
   }

   @Override
   public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
      return true;
   }
}
