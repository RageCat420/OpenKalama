package me.matl114.hacks.modules.move;

import java.util.Objects;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.accessors.events.EntityAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.hooks.ViaFabricPlusHooks;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.Debug;
import me.matl114.utils.entity.PlayerInputUtils;
import me.matl114.utils.entity.PlayerInputUtils$Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.util.math.Vec3d;

public class MovExtra extends BaseModule {
   public final FlagRef grimac1212InputFeatures;
   public final FlagRef grimacSprintFeatures;
   public static MovExtra INSTANCE;
   public final FlagRef disableStepheightFeature;
   public final ModulePath gQ = makePath(Configs.m, "move-safety");
   public final KeyBindRef toggleFlying;
   public final ModulePath hl = this.gQ.add("flight");

   public void sendPacketsForPreStartFallFlying() {
      if (this.grimac1212InputFeatures.get() && ViaFabricPlusHooks.isSupportEndTick() && !PlayerStateManager.INSTANCE.jJ.rI()) {
         PlayerInputUtils$Input var1 = PlayerInputUtils.a(mc.player).rB(true);
         var1.rl();
         var1.applyInput(mc.player);
      }
   }

   private void Xi(ClientPlayerEntity player) {
      if (PlayerStateManager.INSTANCE.jJ.rt() || PlayerStateManager.INSTANCE.jJ.rK()) {
         PlayerInputUtils$Input var2 = PlayerInputUtils.a(player);
         var2.rA(false).rz(false).rx(false).ry(false).rB(false).rD(false);
         if (!Objects.equals(PlayerStateManager.INSTANCE.jJ, var2)) {
            var2.rl();
            ClientPlayerAccess.of(player).resyncInput();
         }
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void le(Event<KalamaHelperHelperI<ModulePreset>> presetEvent) {
      switch ((ModulePreset)((KalamaHelperHelperI)presetEvent.b).b()) {
         case fg:
         case fh:
            this.grimac1212InputFeatures.set(true);
            break;
         default:
            this.grimac1212InputFeatures.set(false);
      }
   }

   public MovExtra() {
      super("MovExtra");
      this.grimac1212InputFeatures = this.flagBuilder(this.gQ.add("grimac-1-21-2-input-features")).build();
      this.grimacSprintFeatures = this.builder(this.gQ.add("grimac-sprint-features"), Boolean.class).defaultValue(true).build();
      this.disableStepheightFeature = this.flagBuilder(this.gQ.add("disable-stepheight-feature")).build();
      this.toggleFlying = this.hotkey(this.hl.add("toggle-flying"))
         .defaultValue(new MultiKeyBind())
         .registerHotkey(HotKeyUtils.b(this::onFlightToggle))
         .build();
      INSTANCE = this;
   }

   public void sendSprintPacketsForInventoryAction() {
      if (this.grimacSprintFeatures.get()) {
         ClientPlayerEntity var1 = mc.player;
         if (PlayerStateManager.INSTANCE.jp) {
            mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.STOP_SPRINTING));
            ClientPlayerAccess.of(var1).setLastSprintFlag(false);
         }
      }
   }

   public void sendPacketsForInventoryAction() {
      if (this.grimac1212InputFeatures.get() && ViaFabricPlusHooks.isSupportEndTick()) {
         ClientPlayerEntity var1 = mc.player;
         this.Xi(var1);
      }
   }

   public void Xf() {
      this.sendSprintPacketsForInventoryAction();
      if (this.grimac1212InputFeatures.get() && ViaFabricPlusHooks.isSupportEndTick()) {
         this.Xi(mc.player);
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.bx().c(ModulePreset.class), this::le);
   }

   public void Xj() {
      if (this.grimac1212InputFeatures.get() && ViaFabricPlusHooks.isSupportEndTick() && PlayerStateManager.INSTANCE.jJ.rI()) {
         PlayerInputUtils$Input var1 = PlayerInputUtils.a(mc.player).rB(false);
         var1.rl();
         var1.applyInput(mc.player);
      }
   }

   public void onFlightToggle() {
      if (mc.player != null) {
         if (mc.player.isFallFlying()) {
            mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
            EntityAccess.of(mc.player).setDataFlag(7, false);
         } else if (mc.player.getAbilities().flying) {
            mc.player.getAbilities().flying = false;
         } else if (mc.player.getAbilities().allowFlying) {
            mc.player.getAbilities().flying = true;
            Vec3d var1 = mc.player.getVelocity();
            mc.player.setVelocity(var1.x, 0.0, var1.z);
            mc.player.setOnGround(false);
         } else {
            Debug.b("You are not allowed to fly");
         }
      }
   }
}
