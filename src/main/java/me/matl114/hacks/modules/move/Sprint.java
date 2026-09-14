package me.matl114.hacks.modules.move;

import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.utils.entity.HackUtilHelperD;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.managers.Configs;
import me.matl114.managers.Configs$BypassMode;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.entity.PlayerInputUtils;
import me.matl114.utils.entity.PlayerInputUtils$Input;
import net.minecraft.client.network.ClientPlayerEntity;

public class Sprint extends BaseModule implements HackUtilHelperJ {
   public final ModulePath hm = makePath(Configs.m, "move-speed");
   public final EnumRef<Configs$BypassMode> bypassMode;
   public final KeyBindRef legalAutoSprintHotkey;
   boolean On;
   boolean Oo;
   public final FlagRef legalAutoSprint;
   public boolean Om;
   public final ModulePath Of = this.hm.add("sprint");
   public final FlagRef allDirectionSprint;
   public final FlagRef logAutoSprint;
   public final FlagRef fakeSprint;
   public final EnumRef<Configs$BypassMode> fakeSprintMode;
   public static HackUtilHelperD instance;

   @Override
   public void onDisableModule() {
      super.onDisableModule();
      this.Om = false;
   }

   @Override
   public void gz(Event<LegalMovementManager> movementManagerEvent) {
      ClientPlayerEntity var2 = ((LegalMovementManager)movementManagerEvent.b).c.a;
      if (this.allDirectionSprint.get() && this.bypassMode.getValue().hasAc()) {
         PlayerInputUtils$Input var3 = PlayerInputUtils.a(var2);
         if (this.On) {
            var3.rB(false);
         }

         var3.applyInput(var2);
      }

      if (this.allDirectionSprint.get() && this.bypassMode.getValue() == Configs$BypassMode.NO_BYPASS) {
         PlayerInputUtils$Input var4 = PlayerInputUtils.a(var2);
         if (this.mayWorkSprint() && var4.rF() && !var4.rE()) {
            this.Om = true;
         }
      }
   }

   public boolean mayWorkSprint() {
      return !mc.player.isFallFlying()
         && !mc.player.isSwimming()
         && !mc.player.isClimbing()
         && !mc.player.isTouchingWater()
         && !mc.player.isSubmergedInWater()
         && (!mc.player.horizontalCollision || mc.player.collidedSoftly);
   }

   @Override
   public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
      this.Om = false;
      this.On = false;
      if (!((LegalMovementManager)movementManagerEvent.b).c.b && ((LegalMovementManager)movementManagerEvent.b).c.a.isOnGround()) {
         this.On = true;
      }

      if (this.Oo) {
         ((LegalMovementManager)movementManagerEvent.b).c.a.setSprinting(true);
         this.Oo = false;
      }

      return true;
   }

   @Override
   public void onEnableModule() {
      super.onEnableModule();
      this.Om = false;
   }

   public void applyBeforeMovementPacketModify(Event<LegalMovementManager> movementManagerEvent) {
      ClientPlayerEntity var2 = ((LegalMovementManager)movementManagerEvent.b).c.a;
      if (var2.isSprinting() && this.fakeSprint.get() && !this.fakeSprintMode.get().hasAc()) {
         this.Oo = true;
         var2.setSprinting(false);
         PlayerInputUtils.a(var2).rD(false).applyInput(var2);
      }
   }

   public Sprint() {
      super("Sprint");
      this.legalAutoSprint = this.flagBuilder(this.Of.add("legal-auto-sprint")).build();
      this.legalAutoSprintHotkey = this.toggleHotkey(this.Of.add("legal-auto-sprint-hotkey"), new MultiKeyBind(), this.Of.add("legal-auto-sprint")).build();
      this.logAutoSprint = this.flagBuilder(this.Of.add("log-auto-sprint")).build();
      this.fakeSprint = this.flagBuilder(this.Of.add("fake-sprint")).build();
      this.fakeSprintMode = this.builder(this.Of.add("fake-sprint-mode"), Configs$BypassMode.class).defaultValue(Configs$BypassMode.NO_BYPASS).build();
      this.allDirectionSprint = this.flagBuilder(this.Of.add("all-direction-sprint")).build();
      this.bypassMode = this.builder(this.Of.add("bypass-mode"), Configs$BypassMode.class).defaultValue(Configs$BypassMode.NO_BYPASS).build();
      this.Om = false;
      this.On = false;
      this.Oo = false;
      if (instance == null) {
         instance = new HackUtilHelperD(this::cast);
         MovTasks.j.SJ(() -> instance);
      }

      instance.mN(this::cast);
   }

   public void applyPreTickModify(Event<LegalMovementManager> movementManagerEvent) {
      if (this.allDirectionSprint.get() && this.bypassMode.getValue().hasAc()) {
         PlayerInputUtils$Input var2 = PlayerInputUtils.of(mc.options);
         ClientPlayerEntity var3 = ((LegalMovementManager)movementManagerEvent.b).c.a;
         if (this.mayWorkSprint() && var2.rK() && !var2.rE() && var2.rF() && !((LegalMovementManager)movementManagerEvent.b).d()) {
            PlayerStateManager.nT(var3, var3.getYaw() + 180.0F);
            ((LegalMovementManager)movementManagerEvent.b).c();
            ((LegalMovementManager)movementManagerEvent.b).a();
         }
      }
   }

   public void gA(Event<KalamaHelperHelperI<ModulePreset>> event) {
      ModulePreset var2 = (ModulePreset)((KalamaHelperHelperI)event.e()).b();
      switch (var2) {
         case fd:
         case fe:
            this.bypassMode.set(Configs$BypassMode.NO_BYPASS);
            break;
         default:
            this.bypassMode.set(Configs$BypassMode.BYPASS_GRIM);
      }

      switch (var2) {
         case fg:
         case fh:
            this.fakeSprintMode.set(Configs$BypassMode.BYPASS_GRIM);
            break;
         default:
            this.fakeSprintMode.set(Configs$BypassMode.NO_BYPASS);
      }
   }

   public void onTick(Event<ClientPlayerEntity> event) {
      if (this.legalAutoSprint.get() && !mc.options.sprintKey.isPressed() && PlayerInputUtils.of(mc.options).ru()) {
         mc.options.sprintKey.setPressed(true);
         if (this.logAutoSprint.get()) {
            this.logI18N("message.module.sprint.toggle-on", new Object[0]);
         }
      }
   }

   @Override
   public int priority() {
      return 10000000;
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.U(), this::onTick);
      this.registerListener(Listener.bx().c(ModulePreset.class), this::gA);
   }
}
