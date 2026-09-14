package me.matl114.hacks.modules.move;

import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.accessors.hacks.KeyBindAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.hacks.utils.entity.HackUtilHelperD;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.hacks.utils.move.FlightVelocity;
import me.matl114.hacks.utils.move.HackUtilHelperA;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.Debug;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.entity.PlayerInputUtils;
import me.matl114.utils.entity.PlayerInputUtils$Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.util.math.Vec3d;

public class Flight extends BaseModule implements HackUtilHelperJ {
   public final ModulePath gQ = makePath(Configs.m, "move-safety");
   public final KeyBindRef toggleFlightSpeed;
   double hL;
   public final DoubleRef walkSpeedOverride;
   public final KeyBindRef toggleWalkSpeed;
   private int hG;
   public final KeyBindRef flightEnableHotkey;
   private int hC;
   public final FlagRef flySpeed;
   int hM;
   private double hJ;
   public boolean hB;
   public final DoubleRef flySpeedSurvival;
   private static HackUtilHelperD instance;
   public final KeyBindRef flightModeSwitchHotkey;
   private int hD;
   private int hE;
   public final IntRef antikickPeriod;
   public final ModulePath hm;
   private double hH;
   private boolean hI;
   private boolean hK;
   public final EnumRef<Flight$Mode> flightMode;
   public final ModulePath hl = this.gQ.add("flight");
   public final FlagRef walkSpeed;
   public final FlagRef flightEnable;
   private static final double antiKickOffset = 0.032;
   public final FlagRef antikick;
   public final DoubleRef flySpeedCreative;
   public final FlagRef ongroundWhenMine;

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.e(PlayerAbilitiesS2CPacket.class), this::onAbility);
      this.registerListener(Listener.e(UpdatePlayerAbilitiesC2SPacket.class), this::kQ);
      this.registerListener(Listener.bx().c(ModulePreset.class), this::le);
      this.registerListener(Listener.ap().getChannel(PlayerActionC2SPacket.class), this::onStartMine);
      this.registerListener(Listener.ap().getChannel(PlayerInputC2SPacket.class), this::lb);
   }

   @Override
   public void gz(Event<LegalMovementManager> movementManagerEvent) {
      HackUtilHelperJ.super.gz(movementManagerEvent);
   }

   public void onAbility(Event<PlayerAbilitiesS2CPacket> event) {
      PlayerAbilitiesS2CPacket var2 = (PlayerAbilitiesS2CPacket)event.e();
      this.hB = var2.allowFlying();
      if (mc.player != null) {
         PlayerAbilities var3 = mc.player.getAbilities();
         var3.creativeMode = var2.isCreativeMode();
         var3.invulnerable = var2.isInvulnerable();
         if (!this.isActive()) {
            var3.allowFlying = var2.allowFlying();
         }

         if (!this.flySpeed.get()) {
            var3.setFlySpeed(var2.getFlySpeed());
         }

         var3.setWalkSpeed(var2.getWalkSpeed());
      } else {
         Tasks.l(() -> this.onAbility(event), 10);
      }

      event.cancel();
   }

   private void setMotionY(double motionY) {
      mc.options.sneakKey.setPressed(false);
      mc.options.jumpKey.setPressed(false);
      Vec3d var3 = mc.player.getVelocity();
      mc.player.setVelocity(var3.x, motionY, var3.z);
   }

   public void antiKick(ClientPlayerEntity player, boolean fakeGliding) {
      if (MovTasks.seenAsFloating(fakeGliding)) {
         this.hG++;
      } else {
         this.hG = 0;
      }

      if (this.hG > this.antikickPeriod.get()) {
         this.hG = 0;
         this.hI = !this.shouldResetMotion();
         this.hJ = player.getVelocity().y;
         this.setMotionY(-0.032);
         this.hK = true;
         this.hH = 0.024;
      } else {
         if (!this.hI) {
            if (this.hK) {
               this.setMotionY(-0.032);
               this.hH += 0.024;
               this.hK = false;
            } else {
               this.setMotionY(this.hH + this.hJ - 0.0);
               this.hH = 0.0;
               this.hJ = 0.0;
               Tasks.l(this::restoreKeyPresses, 1);
               this.hI = true;
            }
         }
      }
   }

   public void applyBeforeMovementPacketModify(Event<LegalMovementManager> movementManagerEvent) {
      ClientPlayerEntity var2 = ((LegalMovementManager)movementManagerEvent.b).c.a;
      boolean var3 = var2.isOnGround();
      if (!var3 && this.ongroundWhenMine.get() && this.isInMiningAction() && !var2.getAbilities().creativeMode) {
         this.hC = 2;
         var2.setOnGround(true);
         ClientPlayerAccess.of(var2).resyncOnGround();
         this.hD = 2;
      } else if (this.hC > 0) {
         this.hC--;
         var2.setOnGround(true);
         ClientPlayerAccess.of(var2).resyncOnGround();
         this.hD = var3 ? 1 : 2;
      }
   }

   public void le(Event<KalamaHelperHelperI<ModulePreset>> presetEvent) {
      ModulePreset var2 = (ModulePreset)((KalamaHelperHelperI)presetEvent.e()).b();
      switch (var2) {
         case fg:
         case fh:
         case fi:
            this.flightEnable.set(false);
            break;
         default:
            this.flightEnable.set(true);
      }

      switch (var2) {
         case fg:
         case fh:
            this.flySpeed.set(false);
            this.walkSpeed.set(false);
         default:
            switch (var2) {
               case fd:
                  this.ongroundWhenMine.set(true);
                  break;
               default:
                  this.ongroundWhenMine.set(false);
            }
      }
   }

   public boolean shouldResetMotion() {
      double var1 = mc.player.getY();
      if (this.hM == 0) {
         this.hM = 1;
         this.hL = var1;
         return false;
      } else if (Math.abs(var1 - this.hL) > 0.096) {
         double var3 = this.hL;
         this.hL = var1;
         this.hM = 1;
         return var3 < this.hL ? true : MovTasks.h.checkEnvironmentCollision(mc.player, mc.player.getPos().add(0.0, -0.096, 0.0), false);
      } else {
         this.hL = var1;
         this.hM++;
         return this.hM > 10;
      }
   }

   public void lb(Event<PlayerInputC2SPacket> inputPacketEvent) {
   }

   public Vec3d dispatchAntiKickMotion(Vec3d controlMotion) {
      boolean var2 = false;
      return !var2 && (!this.isActive() || !this.antikick.get()) ? controlMotion : this.processAntiKickMotion(controlMotion, var2);
   }

   public Vec3d processAntiKickMotion(Vec3d controlMotion, boolean fakeGlide) {
      if (MovTasks.seenAsFloating(fakeGlide)) {
         this.hG++;
      } else {
         this.hG = 0;
      }

      if (this.hG > MovTasks.at().antikickPeriod.get()) {
         this.hG = 0;
         this.hI = !this.shouldResetMotion();
         this.hJ = controlMotion.y;
         this.hK = true;
         this.hH = 0.024;
         return new Vec3d(controlMotion.x, -0.032, controlMotion.z);
      } else if (!this.hI) {
         if (this.hK) {
            this.hH += 0.024;
            this.hK = false;
            return new Vec3d(controlMotion.x, -0.032, controlMotion.z);
         } else {
            this.hH = 0.0;
            this.hJ = 0.0;
            this.hI = true;
            return new Vec3d(controlMotion.x, this.hH + this.hJ - 0.0, controlMotion.z);
         }
      } else {
         return controlMotion;
      }
   }

   public Flight() {
      super("Flight");
      this.hm = makePath(Configs.m, "move-speed");
      this.flightEnable = this.flagBuilder(this.hl.add("flight-enable")).defaultValue(false).build();
      this.flightMode = this.builder(this.hl.add("flight-mode"), Flight$Mode.class).defaultValue(Flight$Mode.CREATIVE).build();
      this.flightEnableHotkey = this.moduleEntry(
            this.hl.add("flight-enable-hotkey"), new MultiKeyBind(), this.hl.add("flight-enable"), moduleMeta(() -> this.flightMode)
         )
         .build();
      this.flightModeSwitchHotkey = this.hotkey(this.hl.add("flight-mode-switch-hotkey"), new MultiKeyBind()).registerHotkey(HotKeyUtils.b(this::kT)).build();
      this.antikick = this.builder(this.hl.add("antikick"), Boolean.class).defaultValue(true).build();
      this.antikickPeriod = this.intBuilder(this.hl.add("antikick-period")).defaultValue(60).build();
      this.flySpeed = this.builder(this.hm.add("fly-speed"), Boolean.class).defaultValue(false).build();
      this.toggleFlightSpeed = this.toggleHotkey(this.hm.add("toggle-flight-speed"), new MultiKeyBind(), this.hm.add("fly-speed")).build();
      this.flySpeedCreative = this.builder(this.hm.add("fly-speed-creative"), Double.class).defaultValue(0.8).build();
      this.flySpeedSurvival = this.builder(this.hm.add("fly-speed-survival"), Double.class).defaultValue(0.4).build();
      this.walkSpeed = this.flagBuilder(this.hm.add("walk-speed")).build();
      this.toggleWalkSpeed = this.toggleHotkey(this.hm.add("toggle-walk-speed"), new MultiKeyBind(), this.hm.add("walk-speed")).build();
      this.walkSpeedOverride = this.builder(this.hm.add("walk-speed-override"), Double.class).defaultValue(0.1).build();
      this.ongroundWhenMine = this.flagBuilder(this.hl.add("onground-when-mine")).build();
      this.hB = false;
      this.hC = 0;
      this.hD = 0;
      this.hE = 0;
      this.hG = 0;
      this.hI = false;
      this.hJ = 0.0;
      this.hL = 0.0;
      this.hM = 0;
      this.bindFlag(this.flightEnable);
      if (instance == null) {
         instance = new HackUtilHelperD(this::cast);
         MovTasks.j.SJ(() -> instance);
      }

      instance.mN(this::cast);
   }

   @Override
   public void onEnableModule() {
      super.onEnableModule();
      if (mc.player != null) {
         this.hB = mc.player.getAbilities().allowFlying;
      }
   }

   @Override
   public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
      this.hD = 0;
      return true;
   }

   public double getOverridingFlySpeed() {
      return mc.player != null && mc.interactionManager.getCurrentGameMode().isCreative() ? this.flySpeedCreative.get() : this.flySpeedSurvival.get();
   }

   public void kQ(Event<UpdatePlayerAbilitiesC2SPacket> event) {
      if (this.isActive() && !this.hB) {
         event.cancel();
      }
   }

   public double kS() {
      return this.walkSpeedOverride.get();
   }

   public void kT() {
      Flight$Mode var1 = this.flightMode.get();
      int var2 = var1.ordinal() + 1;
      Flight$Mode[] var3 = Flight$Mode.values();
      Flight$Mode var4 = var3[var2 % var3.length];
      this.flightMode.set(var4);
      Debug.chat("toggle flight mode to", var4.resultAsString());
   }

   public void kU(ClientPlayerEntity player) {
      boolean var2 = false;
      if (this.isActive() && this.antikick.get()) {
         this.antiKick(player, var2);
      }
   }

   public void applyPreTickModify(Event<LegalMovementManager> movementManagerEvent) {
      ClientPlayerEntity var2 = ((LegalMovementManager)movementManagerEvent.b).c.a;
      if (this.isActive()) {
         if (!var2.getAbilities().allowFlying) {
            var2.getAbilities().allowFlying = true;
         }
      } else {
         var2.getAbilities().allowFlying = this.hB;
      }
      boolean var7 = switch ((Flight$Mode)this.flightMode.get()) {
         case CREATIVE -> {
            this.kU(var2);
            yield true;
         }
         case MOTION -> {
            if (this.isActive() && mc.player.getAbilities().flying) {
               PlayerInputUtils$Input var8 = PlayerInputUtils.of(mc.options);
               Vec3d var10 = new Vec3d(var8.rp(), var8.rq(), var8.ro());
               Vec3d var11 = EntityUtils.movementInputToVelocity(var10, (float)(5.0 * this.getOverridingFlySpeed()), var2.getYaw());
               var2.setVelocity(var11);
               var11 = this.dispatchAntiKickMotion(var11);
               FlightVelocity var6 = new FlightVelocity(var11, 5.0 * this.getOverridingFlySpeed(), HackUtilHelperA.rk);
               Listener.bx().broadcast(new KalamaHelperHelperI<>(FlightVelocity.class, var6));
               var11 = var6.b();
               var2.setVelocity(var11);
               yield true;
            } else {
               yield false;
            }
         }
         case JETPACK -> {
            if (this.isActive()) {
               PlayerInputUtils$Input var3 = PlayerInputUtils.of(mc.options);
               if (var3.rI()) {
                  Vec3d var4 = new Vec3d(0.0, 1.0, 0.0);
                  var4 = this.dispatchAntiKickMotion(var4);
                  if (var4.y > 0.8) {
                     var2.jump();
                  } else {
                     Vec3d var5 = var2.getVelocity();
                     var2.setVelocity(var5.x, var4.y, var5.z);
                  }

                  yield true;
               }
            }

            yield false;
         }
      };
      if (!var7 && this.isActive() && mc.player.getAbilities().flying) {
         this.kU(var2);
      }
   }

   public boolean isInMiningAction() {
      return mc.interactionManager.isBreakingBlock() || this.hE + 1 >= Tasks.b();
   }

   private void restoreKeyPresses() {
      if (mc.currentScreen == null) {
         KeyBindAccess.of(mc.options.jumpKey).resetKeyState();
         KeyBindAccess.of(mc.options.sneakKey).resetKeyState();
      }
   }

   @Override
   public void onCreate() {
      super.onCreate();
   }

   @Override
   public void onDisableModule() {
      super.onDisableModule();
      if (mc.player != null) {
         mc.player.getAbilities().allowFlying = this.hB;
         if (mc.player.getAbilities().flying && !this.hB) {
            mc.player.getAbilities().flying = false;
         }
      }
   }

   public void onStartMine(Event<PlayerActionC2SPacket> actionPacket) {
      if (this.ongroundWhenMine.get() && ((PlayerActionC2SPacket)actionPacket.e()).getAction() == Action.START_DESTROY_BLOCK) {
         this.hE = Tasks.b();
      }
   }
}
