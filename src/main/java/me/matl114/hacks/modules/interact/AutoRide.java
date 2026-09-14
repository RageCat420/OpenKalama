package me.matl114.hacks.modules.interact;

import java.util.List;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.combat.CombatExtra;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.InteractUtils;
import me.matl114.utils.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;

public class AutoRide extends BaseModule {
   public final IntRef interactCooldown;
   Entity mS;
   public final FlagRef ae;
   public final ModulePath mR = makePath(Configs.n, "interaction-tweaks.auto-ride");
   public final KeyBindRef J;
   int cd1;

   public AutoRide() {
      super("AutoRide");
      this.ae = this.flagBuilder(this.mR.addEnable()).build();
      this.J = this.toggleHotkey(this.mR.addHotkey(), new MultiKeyBind(), this.mR.addEnable()).build();
      this.interactCooldown = this.intBuilder(this.mR.add("interact-cooldown")).defaultValue(4).build();
      this.cd1 = 0;
      this.bindFlag(this.ae);
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.bd(), this::handleInput);
   }

   public void handleInput(Event<Void> event) {
      if (this.ae.get() && !mc.player.hasVehicle() && ++this.cd1 > this.interactCooldown.get()) {
         this.cd1 = 0;
         double var2 = CombatExtra.INSTANCE.getAttackRange();
         Box var4 = mc.player.getBoundingBox().expand(var2 + 3.0, var2 + 3.0, var2 + 3.0);
         List<Entity> var5 = mc.world.getOtherEntities(mc.player, var4, re -> re instanceof VehicleEntity);
         Entity var6 = null;

         for (Entity var8 : var5) {
            if (var8 == this.mS) {
               var6 = var8;
            }
         }

         if (var6 == null) {
            for (Entity var11 : var5) {
               if (var11.getBoundingBox().squaredMagnitude(mc.player.getEyePos()) < MathUtils.a(var2)) {
                  var6 = var11;
               }
            }
         }

         this.mS = var6;
         if (var6 != null) {
            Box var10 = this.mS.getBoundingBox();
            EntityHitResult var12 = new EntityHitResult(this.mS, var10.getCenter().add(0.0, var10.getLengthY() / 2.0, 0.0));
            InteractUtils.simulateInteract(var12);
         }
      }
   }
}
