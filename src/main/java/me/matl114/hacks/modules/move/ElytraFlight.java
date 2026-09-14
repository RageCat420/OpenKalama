package me.matl114.hacks.modules.move;

import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.modules.combat.ElytraBot;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.hacks.utils.config.NBTTypes;
import me.matl114.hacks.utils.config.OptionalPrimitive;
import me.matl114.hacks.utils.entity.HackUtilHelperD;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.hacks.utils.move.FlightVelocity;
import me.matl114.hacks.utils.move.HackUtilHelperA;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.ConfigEnum;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.entity.PlayerInputUtils;
import me.matl114.utils.entity.PlayerInputUtils$Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction.Axis;

public class ElytraFlight extends BaseModule implements HackUtilHelperJ {
   public FlagRef enableControl;
   int TD;
   public final FlagRef horizontalNoGravity;
   public KeyBindRef enableControlHotkey;
   public final KeyBindRef autoFlyHotkey;
   private static HackUtilHelperD cy;
   public final FlagRef useAutoRescale;
   public final FlagRef landAutoClose;
   public final FlagRef autoFlyLandAutoClose;
   public final NBTRef<OptionalPrimitive<Double>> overrideDownwardAngle;
   Boolean TE;
   public final FlagRef autoFly;
   public final NBTRef<OptionalPrimitive<Double>> overridePullupAngle;
   boolean Ty;
   public static ElytraFlight INSTANCE;
   public final ModulePath Tj;
   public final FlagRef motionLerpStarting;
   public final EnumRef<ElytraExtra$MotionMode> motionMode;
   Vec3d fT;
   public final DoubleRef motionAmount;
   public final EnumRef<ElytraFlight$Mode> flightMode;
   public final KeyBindRef flightToggle;
   public final FlagRef pauseWhenAccelerate;
   public final FlagRef useFloatingUtils;
   public final NBTRef<OptionalPrimitive<Integer>> holdJumpTakeoffTicks;
   public final FlagRef autoFlyAutoJumpOff;
   public final ModulePath JY = makePath(Configs.m, "elytra");
   public final ModulePath Tk;
   public final DoubleRef motionLerpArgument;

   @Override
   public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
      if (this.TE != null) {
         ((LegalMovementManager)movementManagerEvent.e()).c.a.setNoGravity(this.TE);
         this.TE = null;
      }

      return true;
   }

   public boolean alu() {
      this.flightMode.next();
      Debug.chat(ChatUtils.textFromLegacyString("&c[ElytraFlight] &fMode switch to"), this.flightMode.get().resultAsString());
      return false;
   }

   @Override
   public void bb(Event<LegalMovementManager> movementManagerEvent) {
      ClientPlayerEntity var2 = ((LegalMovementManager)movementManagerEvent.b).c.a;
      if (var2.isFallFlying()) {
         this.Ty = true;
      }

      if (mc.options.jumpKey.isPressed()) {
         this.TD++;
      } else {
         this.TD = 0;
      }

      if (this.alv()) {
         if (!var2.isFallFlying()
            && this.holdJumpTakeoffTicks.get().isPresent()
            && this.TD >= this.holdJumpTakeoffTicks.get().getValue()
            && !mc.player.isOnGround()
            && mc.player.checkFallFlying()) {
            MovExtra.INSTANCE.Xj();
            mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
            MovExtra.INSTANCE.sendPacketsForPreStartFallFlying();
         }

         if (var2.isFallFlying()) {
            if (this.fT == null) {
               this.fT = Vec3d.ZERO;
            }

            if (MovTasks.az().enable.get()) {
               return;
            }

            Vec3d var3 = new Vec3d(0.0, 0.0, 0.0);
            boolean var4 = false;
            double var5 = this.motionAmount.get();
            boolean var7 = false;
            PlayerInputUtils$Input var8 = PlayerInputUtils.of(mc.options);
            switch ((ElytraFlight$Mode)this.flightMode.get()) {
               case CONTROL:
                  boolean var20 = true;
                  Vec3d var23 = new Vec3d(var8.rp(), var8.rq(), var8.ro());
                  Vec3d var26 = EntityUtils.movementInputToVelocity(var23, 1.0F, var2.getYaw());
                  if (var23.horizontalLengthSquared() > 0.0) {
                     if (var23.y > 0.0) {
                        if (this.overridePullupAngle.get().isPresent()) {
                           double var12 = this.overridePullupAngle.get().getValue();
                           double var14 = Math.tan(Math.abs(var12)) * var26.horizontalLength();
                           var26 = var26.withAxis(Axis.Y, var14);
                        }
                     } else if (var23.y < 0.0 && this.overrideDownwardAngle.get().isPresent()) {
                        double var27 = this.overrideDownwardAngle.get().getValue();
                        double var28 = Math.tan(Math.abs(var27)) * var26.horizontalLength();
                        var26 = var26.withAxis(Axis.Y, -var28);
                     }
                  }

                  if (this.motionMode.get() == ElytraExtra$MotionMode.FIRE_WORKS) {
                     var20 = false;
                     var7 = true;
                     if (MovTasks.ax().agj()) {
                        var20 = true;
                     }
                  }

                  var3 = var26;
                  if (var20) {
                     var4 = true;
                  }
                  break;
               case ROTATION:
                  boolean var9 = true;
                  Vec3d var10 = EntityUtils.lookCoordToPos(var2.getPitch(), var2.getYaw(), var8.rp(), 0.0, var8.ro());
                  Vec3d var11 = new Vec3d(0.0, var8.rq(), 0.0);
                  var10 = var10.add(var11);
                  if (this.motionMode.get() == ElytraExtra$MotionMode.FIRE_WORKS) {
                     var9 = false;
                     var7 = true;
                     if (MovTasks.ax().agj()) {
                        var9 = true;
                     }
                  }

                  var3 = var10;
                  if (var9) {
                     var4 = true;
                  }
            }

            if (var3.lengthSquared() < 1.0E-6 && this.autoFly.get()) {
               var3 = mc.player.getRotationVector();
            }

            Vec3d var21 = var3.normalize();
            Vec3d var24 = var21.multiply(var5);
            double var16 = this.motionLerpArgument.get();
            if (var16 != 1.0 && (this.motionLerpStarting.get() || this.fT.lengthSquared() > 0.01 * var5 * var5)) {
               var24 = var24.multiply(var16).add(this.fT.multiply(1.0 - var16));
               if (var21.lengthSquared() > 1.0E-6) {
                  var24 = var24.normalize().multiply(var5);
               }
            }

            FlightVelocity var18 = new FlightVelocity(var24, var5, HackUtilHelperA.rj);
            Listener.bx().broadcast(new KalamaHelperHelperI<>(FlightVelocity.class, var18));
            var24 = var18.b();
            this.fT = var24;
            if (this.fT.length() < 0.1 * var5) {
               this.fT = Vec3d.ZERO;
            }

            if (FloatingUtils.INSTANCE.SG()) {
               var24 = Vec3d.ZERO;
               var4 = true;
               var7 = false;
            } else if (this.useFloatingUtils.get() && var24.lengthSquared() < 1.0E-4) {
               if (!MovTasks.ax().agj()) {
                  if (ElytraExtra.INSTANCE.agg() && PlayerStateManager.INSTANCE.jI) {
                     var4 = true;
                     var7 = false;
                  } else {
                     var24 = Vec3d.ZERO;
                     FloatingUtils.INSTANCE.SB(true);
                     var4 = true;
                     var7 = false;
                  }
               } else {
                  var4 = true;
                  var7 = false;
               }
            }

            if (ElytraExtra.INSTANCE.agg() && PlayerStateManager.INSTANCE.jI && var24.y < 0.0) {
               var24 = var24.withAxis(Axis.Y, 0.0);
            }

            if (this.motionMode.get() == ElytraExtra$MotionMode.FIRE_WORKS && var24.lengthSquared() > 0.005) {
               if (!((LegalMovementManager)movementManagerEvent.b).d()) {
                  if (var24.horizontalLengthSquared() > 0.005) {
                     ((LegalMovementManager)movementManagerEvent.b).pushImportantRotation(true, true);
                     Vec2f var19 = EntityUtils.q(var24.normalize());
                     ((LegalMovementManager)movementManagerEvent.b).c();
                     EntityUtils.setEntityPitchSafe(mc.player, var19.x);
                     PlayerStateManager.nT(mc.player, var19.y);
                  } else {
                     ((LegalMovementManager)movementManagerEvent.b).pushImportantRotation(true, false);
                     float var29 = EntityUtils.rotationToPitch(var24.normalize());
                     ((LegalMovementManager)movementManagerEvent.b).c();
                     EntityUtils.setEntityPitchSafe(mc.player, var29);
                  }
               }

               if (this.useAutoRescale.get()) {
                  float var30 = mc.player.getYaw();
                  if (Tasks.b() % 2 == 0) {
                     PlayerStateManager.nT(mc.player, var30 + 0.01F);
                  } else {
                     PlayerStateManager.nT(mc.player, var30 - 0.01F);
                  }
               }
            }

            if (var4) {
               if (Math.abs(var24.y) <= 0.01 && this.horizontalNoGravity.get() && !mc.player.isOnGround()) {
                  this.TE = var2.hasNoGravity();
                  var2.setNoGravity(true);
               }

               mc.player
                  .setVelocity(
                     this.motionMode.get() == ElytraExtra$MotionMode.FIRE_WORKS && this.useAutoRescale.get()
                        ? ElytraExtra.INSTANCE.agA(var24, mc.player.getPitch(), mc.player.getYaw(), !mc.player.hasNoGravity())
                        : var24
                  );
            }

            if (var7) {
               MovTasks.ax().agr(mc.player.getPitch(), mc.player.getYaw());
            }
         } else {
            this.fT = null;
            if (this.autoFly.get() && this.autoFlyAutoJumpOff.get()) {
               ElytraExtra.INSTANCE.afo();
            }
         }
      } else {
         this.fT = null;
      }

      if (this.Ty && !mc.player.isFallFlying() && mc.player.isOnGround()) {
         if (this.enableControl.get() && this.landAutoClose.get()) {
            HotKeyUtils.f(this.Tj.add("enable-control").toPath(), this.enableControl).run();
         }

         if (this.autoFly.get() && this.autoFlyLandAutoClose.get()) {
            HotKeyUtils.f(this.Tj.add("auto-fly").toPath(), this.autoFly).run();
         }

         this.Ty = false;
      }
   }

   public ElytraFlight() {
      super("ElytraFlight");
      this.Tj = this.JY.add("simple-flight-control");
      this.Tk = this.JY.add("custom-fireworks");
      this.enableControl = this.flagBuilder(this.Tj.add("enable-control")).build();
      this.enableControlHotkey = this.moduleEntry(
            this.Tj.add("enable-control-hotkey"), new MultiKeyBind(), this.Tj.add("enable-control"), moduleMeta(() -> this.flightMode)
         )
         .build();
      this.motionAmount = this.builder(this.Tk.add("motion-amount"), DoubleRef.TYPE).defaultValue(0.05).validator(Configs.doubleRange(0.0, 10000.0)).build();
      this.motionMode = this.builder(this.Tj.add("motion-mode"), ElytraExtra$MotionMode.class).defaultValue(ElytraExtra$MotionMode.VOID).build();
      this.flightMode = this.builder(this.Tj.add("flight-mode"), ElytraFlight$Mode.class).defaultValue(ElytraFlight$Mode.CONTROL).build();
      this.flightToggle = this.hotkey(this.Tj.add("flight-toggle")).defaultValue(new MultiKeyBind()).registerHotkey(HotKeyUtils.c(this::alu)).build();
      this.useFloatingUtils = this.flagBuilder(this.Tj.add("use-floating-utils")).build();
      this.horizontalNoGravity = this.builder(this.Tj.add("horizontal-no-gravity"), Boolean.class).defaultValue(true).build();
      this.pauseWhenAccelerate = this.builder(this.Tj.add("pause-when-accelerate"), Boolean.class).defaultValue(true).build();
      this.landAutoClose = this.flagBuilder(this.Tj.add("land-auto-close")).build();
      this.holdJumpTakeoffTicks = this.builder(this.Tj.add("hold-jump-takeoff-ticks"), OptionalPrimitive.INT_TYPE)
         .defaultValue(new OptionalPrimitive<>(false, NBTTypes.c, 10))
         .build();
      this.motionLerpArgument = this.builder(this.Tj.add("motion-lerp-argument"), DoubleRef.TYPE).defaultValue(1.0).build();
      this.motionLerpStarting = this.builder(this.Tj.add("motion-lerp-starting"), Boolean.class).defaultValue(false).build();
      this.useAutoRescale = this.flagBuilder(this.Tj.add("use-auto-rescale")).show(() -> ElytraExtra.INSTANCE.autoRescaleFireworkBox.get()).build();
      this.overridePullupAngle = this.builder(this.Tj.add("override-pullup-angle"), OptionalPrimitive.DOUBLE_TYPE)
         .defaultValue(new OptionalPrimitive<>(false, NBTTypes.e, 45.0))
         .validator(s -> s.getValue() > 0.0 && s.getValue() < 90.0)
         .show(() -> this.flightMode.get().isIn(new ConfigEnum[]{ElytraFlight$Mode.CONTROL}))
         .build();
      this.overrideDownwardAngle = this.builder(this.Tj.add("override-downward-angle"), OptionalPrimitive.DOUBLE_TYPE)
         .defaultValue(new OptionalPrimitive<>(false, NBTTypes.e, 45.0))
         .validator(s -> s.getValue() > 0.0 && s.getValue() < 90.0)
         .show(() -> this.flightMode.get().isIn(new ConfigEnum[]{ElytraFlight$Mode.CONTROL}))
         .build();
      this.Ty = false;
      this.autoFly = this.flagBuilder(this.Tj.add("auto-fly")).build();
      this.autoFlyHotkey = this.moduleEntry(this.Tj.add("auto-fly-hotkey"), new MultiKeyBind(), this.Tj.add("auto-fly")).build();
      this.autoFlyAutoJumpOff = this.flagBuilder(this.Tj.add("auto-fly-auto-jump-off")).build();
      this.autoFlyLandAutoClose = this.flagBuilder(this.Tj.add("auto-fly-land-auto-close")).build();
      this.fT = null;
      this.TD = 0;
      this.TE = null;
      if (cy == null) {
         cy = new HackUtilHelperD(this::cast);
         MovTasks.j.SJ(() -> cy);
      }

      cy.mN(this::cast);
      this.bindFlag(this.enableControl);
      INSTANCE = this;
   }

   @Override
   public int priority() {
      return 0;
   }

   public void le(Event<KalamaHelperHelperI<ModulePreset>> presetEvent) {
      switch ((ModulePreset)((KalamaHelperHelperI)presetEvent.b).b()) {
         case fd:
         case fe:
         case ff:
            this.motionMode.set(ElytraExtra$MotionMode.VOID);
            break;
         case fj:
            this.motionMode.set(ElytraExtra$MotionMode.VOID);
            if (this.motionAmount.get() > 2.5) {
               this.motionAmount.set(2.5);
            }
            break;
         case fh:
         case fg:
         case fi:
            this.motionMode.set(ElytraExtra$MotionMode.FIRE_WORKS);
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.bx().c(ModulePreset.class), this::le);
   }

   public boolean alv() {
      return this.enableControl.get() || ElytraBot.INSTANCE.DA();
   }

   public boolean alw() {
      if (this.alv()) {
         if (this.autoFly.get()) {
            return true;
         } else {
            return PlayerInputUtils.of(mc.options).rv() ? true : !this.useFloatingUtils.get();
         }
      } else {
         return false;
      }
   }
}
