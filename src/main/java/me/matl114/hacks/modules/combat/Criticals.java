package me.matl114.hacks.modules.combat;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.accessors.access.PlayerInteractEntityC2SPacketAccess;
import me.matl114.accessors.access.PlayerMoveC2SPacketAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.PacketManager;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.CombatTasks;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.hacks.modules.move.FloatingUtils;
import me.matl114.hacks.modules.move.LegacySnapRotManager;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.entity.HackUtilHelperD;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.hacks.utils.tasks.TimerExecutor;
import me.matl114.hooks.ViaFabricPlusHooks;
import me.matl114.managers.Configs;
import me.matl114.managers.Configs$SetBackTriggerType;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.ConfigEnum;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.CollisionUtil;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RaycastUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.entity.PlayerInputUtils;
import me.matl114.utils.entity.PlayerInputUtils$Input;
import me.matl114.versioned.api.VPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction.Axis;

public class Criticals extends BaseModule implements HackUtilHelperJ {
   public final FlagRef inWallPacket;
   boolean Sy;
   boolean Sw;
   public final FlagRef targetOnly;
   ItemStack SA;
   int SB;
   public final IntRef criticalCooldown;
   public final EnumRef<Configs$SetBackTriggerType> setBackMode;
   int SF;
   Vec3d SJ;
   public final FlagRef groundOnly;
   int SC;
   public final EnumRef<Criticals$Mode> mode;
   public final FlagRef movementOkFreeze;
   static HackUtilHelperD instance;
   public final FlagRef inWallFreeze;
   public final DoubleRef customInWallHeight;
   public final KeyBindRef hotkey;
   public final FlagRef delaySwap;
   public final ModulePath KE = makePath(Configs.k, "att-bot");
   public static final double SH = 1.0E-5;
   public final FlagRef movementOkGround;
   public final FlagRef enable;
   boolean SD;
   boolean SE;
   PlayerInteractEntityC2SPacket Sz;
   public final FlagRef autoWalkResync;
   public static final double SG = 1.0E-4;
   public final FlagRef autoFakeGroundHeight;
   int Sv;
   public final FlagRef inAirFreeze;
   boolean SI;
   public final ModulePath Sg = this.KE.add("criticals");
   TimerExecutor Sx;
   int SK;
   public final DoubleRef customInWallPacketHeight;

   public void applyBeforeMovementPacketModify(Event<LegalMovementManager> movementManagerEvent) {
      boolean var2 = this.SE;
      this.SE = mc.player.isOnGround() && !((LegalMovementManager)movementManagerEvent.b).c.b;
      if (this.enable.get() && this.mode.get() == Criticals$Mode.FREEZE && this.akb() && !mc.player.isFallFlying()) {
         boolean var3;
         label121: {
            var3 = false;
            if (this.inWallFreeze.get() && (PlayerStateManager.INSTANCE.jH || PlayerStateManager.INSTANCE.jG)) {
               double var4 = this.customInWallHeight.get() / 4.0;
               Box var6 = mc.player.dimensions.getBoxAt(((LegalMovementManager)movementManagerEvent.b).c.g);
               HashSet var7 = new HashSet<>(CollisionUtil.getIntersectingBlockPositions(mc.world, var6, false));
               List var8 = CollisionUtil.getIntersectingBlockPositions(mc.world, var6.offset(0.0, -var4, 0.0), false);
               boolean var9 = var8.stream().anyMatch(pos -> !var7.contains(pos));
               if (var9) {
                  double var10 = mc.player.getY() + var4 * 4.0;
                  ((LegalMovementManager)movementManagerEvent.b).c.restorePos();
                  mc.player.setPosition(mc.player.getPos().withAxis(Axis.Y, var10));
                  mc.player.setOnGround(false);
                  this.SI = true;
                  break label121;
               }

               if (PlayerStateManager.INSTANCE.jh > 0.0) {
                  mc.player.setOnGround(false);
                  var3 = true;
                  break label121;
               }

               if (this.SI) {
                  this.SI = false;
                  ((LegalMovementManager)movementManagerEvent.b).c.restorePos();
                  mc.player.setPosition(mc.player.getPos().add(0.0, -var4, 0.0));
                  mc.player.setOnGround(false);
                  break label121;
               }
            }

            if (this.inAirFreeze.get()) {
               if (this.groundOnly.get()) {
                  var3 = var2 || this.SE;
                  if (var3) {
                     this.SE = true;
                  }
               } else {
                  var3 = mc.player.getY() < ((LegalMovementManager)movementManagerEvent.b).c.g.y && this.SD;
               }
            }
         }

         if (var3) {
            FloatingUtils.INSTANCE.SB(true);
            mc.player.setOnGround(false);
            mc.player.setSprinting(false);
         }
      }

      this.SD = mc.player.getY() < ((LegalMovementManager)movementManagerEvent.b).c.g.y;
      if (this.SB > 0) {
         this.SB--;
      }

      if (this.enable.get()
         && this.mode.get() == Criticals$Mode.GRIM_GROUND_SIMULATION
         && !this.canNotCrit()
         && this.SK + 2 <= Tasks.b()
         && this.shouldApplyGrimGroundSimulationAutoFakeGround()) {
         double var12 = mc.player.getY();
         if (this.SB <= 0) {
            boolean var21 = mc.player.getPos().subtract(((LegalMovementManager)movementManagerEvent.b).c.g).horizontalLengthSquared() > MathUtils.a(2.0E-4)
               || Math.abs(mc.player.getPos().y - ((LegalMovementManager)movementManagerEvent.b).c.g.y) > 2.0E-4;
            double var14 = 1.0E-5;
            double var16 = 1.0E-4;
            double var22 = 1.0 / var16;
            double var18 = (int)(var12 * var22) * var16 + var14;
            Vec3d var20 = mc.player.getPos();
            mc.player.setPosition(var20.withAxis(Axis.Y, var18));
            if (!Objects.equals(var20, mc.player.getPos())) {
               if (!var21 && ViaFabricPlusHooks.isSupportDupRot()) {
                  this.SJ = mc.player.getRotationVector();
                  PlayerStateManager.INSTANCE.restoreLastRotation(mc.player);
               }

               ClientPlayerAccess.of(mc.player).resyncPos();
            }
         }
      }

      if (this.Sy) {
         movementManagerEvent.cancel();
         ((LegalMovementManager)movementManagerEvent.b).c.restorePos();
         this.Sy = false;
      }
   }

   public boolean hasNoMovement() {
      PlayerInputUtils$Input var1 = PlayerInputUtils.of(mc.options);
      return this.ake() ? !var1.rI() && !var1.rJ() : !var1.rt() && !var1.rJ();
   }

   public boolean akd() {
      if (this.targetOnly.get()) {
         Entity var1 = CombatTasks.l().searchAttackEntity(CombatTasks.j().getAttackRange(), false);
         return var1 != null;
      } else {
         return true;
      }
   }

   public void gA(Event<KalamaHelperHelperI<ModulePreset>> eventModule) {
      switch ((ModulePreset)((KalamaHelperHelperI)eventModule.b).b()) {
         case fh:
            if (this.mode.get().isIn(new ConfigEnum[]{Criticals$Mode.PACKET})) {
               this.mode.set(Criticals$Mode.GRIM_GROUND_SIMULATION);
            }

            this.autoFakeGroundHeight.set(false);
            this.autoWalkResync.set(false);
            break;
         case fg:
            if (this.mode.get().isIn(new ConfigEnum[]{Criticals$Mode.PACKET})) {
               this.mode.set(Criticals$Mode.GRIM_GROUND_SIMULATION);
            }

            this.autoFakeGroundHeight.set(true);
            this.autoWalkResync.set(true);
            break;
         case fd:
         case fe:
            this.mode.set(Criticals$Mode.PACKET);
      }
   }

   public boolean shouldApplyGrimGroundSimulationAutoFakeGround() {
      return (this.autoFakeGroundHeight.get() || this.Sw) && mc.player.isOnGround() && this.akb();
   }

   public boolean akb() {
      return this.hasNoMovement() && this.akd();
   }

   public boolean ake() {
      return switch ((Criticals$Mode)this.mode.get()) {
         case FREEZE -> this.movementOkFreeze.get();
         case GRIM_GROUND_SIMULATION -> this.movementOkGround.get();
         default -> false;
      };
   }

   public Criticals() {
      super("Criticals");
      this.enable = this.flagBuilder(this.Sg.add("enable")).build();
      this.hotkey = this.moduleEntry(this.Sg.add("hotkey"), new MultiKeyBind(), this.Sg.add("enable"), moduleMeta(() -> this.mode)).build();
      this.mode = this.builder(this.Sg.add("mode"), Criticals$Mode.class).defaultValue(Criticals$Mode.PACKET).build();
      this.groundOnly = this.flagBuilder(this.Sg.add("ground-only"))
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{Criticals$Mode.FREEZE, Criticals$Mode.GRIM_GROUND_SIMULATION}))
         .build();
      this.targetOnly = this.flagBuilder(this.Sg.add("target-only"))
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{Criticals$Mode.FREEZE, Criticals$Mode.GRIM_GROUND_SIMULATION}))
         .build();
      this.movementOkFreeze = this.flagBuilder(this.Sg.add("movement-ok-freeze"))
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{Criticals$Mode.FREEZE}))
         .build();
      this.movementOkGround = this.flagBuilder(this.Sg.add("movement-ok-ground"))
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{Criticals$Mode.GRIM_GROUND_SIMULATION}))
         .build();
      this.autoFakeGroundHeight = this.builder(this.Sg.add("auto-fake-ground-height"), Boolean.class)
         .defaultValue(true)
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{Criticals$Mode.GRIM_GROUND_SIMULATION}))
         .build();
      this.autoWalkResync = this.builder(this.Sg.add("auto-walk-resync"), Boolean.class)
         .defaultValue(true)
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{Criticals$Mode.GRIM_GROUND_SIMULATION}))
         .build();
      this.setBackMode = this.builder(this.Sg.add("set-back-mode"), Configs$SetBackTriggerType.class)
         .defaultValue(Configs$SetBackTriggerType.SIMULATION)
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{Criticals$Mode.GRIM_GROUND_SIMULATION}))
         .build();
      this.delaySwap = this.flagBuilder(this.Sg.add("delay-swap"))
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{Criticals$Mode.GRIM_GROUND_SIMULATION}))
         .build();
      this.inWallPacket = this.flagBuilder(this.Sg.add("in-wall-packet"))
         .show(() -> this.mode.get().isNotIn(new ConfigEnum[]{Criticals$Mode.FREEZE, Criticals$Mode.PACKET}))
         .build();
      this.inAirFreeze = this.builder(this.Sg.add("in-air-freeze"), Boolean.class)
         .defaultValue(true)
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{Criticals$Mode.FREEZE}))
         .build();
      this.inWallFreeze = this.builder(this.Sg.add("in-wall-freeze"), Boolean.class)
         .defaultValue(true)
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{Criticals$Mode.FREEZE}))
         .build();
      this.customInWallHeight = this.builder(this.Sg.add("custom-in-wall-height"), Double.class)
         .defaultValue(0.05)
         .validator(Configs.doubleRange(0.0, 1.0))
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{Criticals$Mode.FREEZE}))
         .build();
      this.customInWallPacketHeight = this.builder(this.Sg.add("custom-in-wall-packet-height"), Double.class)
         .defaultValue(0.05)
         .validator(Configs.doubleRange(0.0, 1.0))
         .show(() -> this.mode.get().isNotIn(new ConfigEnum[]{Criticals$Mode.FREEZE}))
         .build();
      this.criticalCooldown = this.builder(this.Sg.add("critical-cooldown"), Integer.class).defaultValue(10).build();
      this.Sv = 0;
      this.Sw = false;
      this.Sx = new TimerExecutor();
      this.SB = 0;
      this.SC = 0;
      this.SD = false;
      this.SE = false;
      this.SF = 0;
      this.SI = false;
      this.SK = 0;
      if (instance == null) {
         instance = new HackUtilHelperD(this::cast);
         MovTasks.j.SJ(() -> instance);
      }

      instance.mN(this::cast);
      this.bindFlag(this.enable);
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.ap().getChannel(PlayerInteractEntityC2SPacket.class), this::onPlayerAttack);
      this.registerListener(Listener.ap().getChannel(HandSwingC2SPacket.class), this::ajZ);
      this.registerListener(Listener.ap().getChannel(PlayerMoveC2SPacket.class), this::onTeleportConfirmPre);
      this.registerListener(Listener.bx().c(ModulePreset.class), this::gA);
   }

   public void handleCritical(Event<PlayerInteractEntityC2SPacket> event) {
      if (this.inWallPacket.get()
         && (PlayerStateManager.INSTANCE.jG || PlayerStateManager.INSTANCE.jH)
         && this.mode.get().isNotIn(new ConfigEnum[]{Criticals$Mode.FREEZE, Criticals$Mode.PACKET})) {
         this.handleCriticalWall(event);
      } else {
         double var2 = mc.player.getX();
         double var4 = mc.player.getY();
         double var6 = mc.player.getZ();
         switch ((Criticals$Mode)this.mode.get()) {
            case PACKET:
               if (!CombatTasks.n().Sd()) {
                  if (mc.player.isSprinting()) {
                     mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.STOP_SPRINTING));
                     ClientPlayerAccess.of(mc.player).setLastSprintFlag(false);
                  }

                  mc.getNetworkHandler().sendPacket(VPacket.g(var2, var4 + 5.0E-4, var6, false, false));
                  mc.getNetworkHandler().sendPacket(VPacket.g(var2, var4 + 1.0E-4, var6, false, false));
               }
               break;
            case FREEZE:
               if (this.SE) {
                  mc.getNetworkHandler().sendPacket(VPacket.h(mc.player.getYaw(), mc.player.getPitch(), false, mc.player.horizontalCollision));
               }
               break;
            case GRIM_GROUND_SIMULATION:
               if (mc.player.isOnGround()) {
                  if (mc.player.isSprinting()) {
                     mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.STOP_SPRINTING));
                     ClientPlayerAccess.of(mc.player).setLastSprintFlag(false);
                  }

                  if (!this.shouldApplyGrimGroundSimulationAutoFakeGround()) {
                     mc.getNetworkHandler().sendPacket(VPacket.g(var2, var4 + 1.0E-5, var6, true, false));
                  }

                  switch ((Configs$SetBackTriggerType)this.setBackMode.get()) {
                     case CRASH_PACKETS:
                        mc.getNetworkHandler()
                           .sendPacket(
                              PlayerMoveC2SPacketAccess.setCause(
                                 VPacket.g(var2, Double.POSITIVE_INFINITY, var6, false, false), PlayerMoveC2SPacketAccess.Cause.TRIGGER_SIMULATION
                              )
                           );
                        break;
                     case SIMULATION:
                        mc.getNetworkHandler()
                           .sendPacket(
                              PlayerMoveC2SPacketAccess.setCause(
                                 VPacket.g(var2, var4 + 1.0, var6, false, false), PlayerMoveC2SPacketAccess.Cause.TRIGGER_SIMULATION
                              )
                           );
                  }

                  event.cancel();
                  this.Sz = (PlayerInteractEntityC2SPacket)event.b;
                  this.SA = mc.player.getStackInHand(Hand.MAIN_HAND).copy();
                  this.SC = Tasks.b();
                  this.Sy = true;
               }
               break;
            case GRIM_WALL:
               if (mc.player.isOnGround() && (PlayerStateManager.INSTANCE.jG || PlayerStateManager.INSTANCE.jH)) {
                  this.handleCriticalWall(event);
               }
         }
      }
   }

   public void handleCriticalWall(Event<PlayerInteractEntityC2SPacket> event) {
      double var2 = mc.player.getX();
      double var4 = mc.player.getY();
      double var6 = mc.player.getZ();
      mc.getNetworkHandler().sendPacket(VPacket.g(var2, var4 + this.customInWallPacketHeight.get(), var6, false, false));
      mc.getNetworkHandler().sendPacket(VPacket.g(var2, var4 + this.customInWallPacketHeight.get() * 0.75, var6, false, false));
      ClientPlayerAccess.of(mc.player).resyncPos();
   }

   @Override
   public void bb(Event<LegalMovementManager> movementManagerEvent) {
      this.Sw = CombatTasks.n().delayAttacking;
   }

   @Override
   public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
      if (this.SJ != null) {
         PlayerStateManager.setPlayerRotationSafe(mc.player, this.SJ);
         LegacySnapRotManager.INSTANCE.ahs(this.SJ, false);
         this.SJ = null;
      }

      return true;
   }

   public void onPlayerAttack(Event<PlayerInteractEntityC2SPacket> event) {
      if (!checkNull() && !event.d() && !this.canNotCrit()) {
         if (!(PlayerStateManager.INSTANCE.jh > 1.0E-6)) {
            boolean var2 = this.Sx.a(this.criticalCooldown.get());
            this.Sx.f();
            if (var2) {
               if (this.enable.get()
                  && PlayerInteractEntityC2SPacketAccess.of((PlayerInteractEntityC2SPacket)event.b).isAttack()
                  && mc.world != null
                  && mc.world.getEntityById(PlayerInteractEntityC2SPacketAccess.of((PlayerInteractEntityC2SPacket)event.b).getEntityId()) instanceof LivingEntity var4
                  )
                {
                  this.handleCritical(event);
               }
            }
         }
      }
   }

   public void ajZ(Event<HandSwingC2SPacket> eventSwing) {
      this.Sx.f();
      if (this.Sz != null) {
         eventSwing.cancel();
      }
   }

   private boolean canNotCrit() {
      return mc.player.isTouchingWater() || mc.player.hasVehicle() || PlayerStateManager.INSTANCE.jE;
   }

   public void applyAfterInputTick(Event<LegalMovementManager> movementManagerEvent) {
      PlayerInputUtils$Input var2 = PlayerInputUtils.a(mc.player);
      if (this.SB > 0 && this.mode.get() == Criticals$Mode.GRIM_GROUND_SIMULATION && this.autoWalkResync.get() && !var2.ru()) {
         this.Sv++;
         if (this.Sv % 2 == 0) {
            var2.rz(true);
         } else {
            var2.rA(true);
         }
      }

      if (this.SC + 3 >= Tasks.b()) {
         var2.rD(false).rx(false);
         mc.player.setSprinting(false);
      }

      var2.applyInput(mc.player);
      HackUtilHelperJ.super.gz(movementManagerEvent);
   }

   public void onTeleportConfirmPre(Event<PlayerMoveC2SPacket> event) {
      if (this.Sz != null && event.b instanceof PlayerMoveC2SPacketAccess var3 && var3.getCause() == PlayerMoveC2SPacketAccess.Cause.SET_BACK) {
         this.SB = 1;
         Entity var11 = mc.world.getEntityById(PlayerInteractEntityC2SPacketAccess.of(this.Sz).getEntityId());
         PlayerMoveC2SPacket var4 = (PlayerMoveC2SPacket)event.b;
         Vec2f var5 = null;
         if (var11 != null) {
            boolean var6 = RaycastUtils.w(mc.player, var4.getPitch(PlayerStateManager.INSTANCE.jl), var4.getYaw(PlayerStateManager.INSTANCE.jm), var11);
            if (!var6) {
               Vec3d var7 = mc.player.getEyePos();
               Vec3d var8 = var11.getEyePos();
               Vec3d var9 = var8.subtract(var7).normalize();
               Vec2f var10 = EntityUtils.q(var9);
               if (ViaFabricPlusHooks.isSupportDupRot()) {
                  var5 = var10;
               } else {
                  var3.setPitch(var10.x);
                  var3.setYaw(EntityUtils.i(PlayerStateManager.INSTANCE.jm, var10.y));
               }
            }
         }

         PlayerInteractEntityC2SPacket var12 = this.Sz;
         ItemStack var13 = this.SA;
         Vec2f var14 = var5;
         PacketManager.b((PlayerMoveC2SPacket)event.b, () -> {
            ItemStack var4x = mc.player.getStackInHand(Hand.MAIN_HAND);
            if (var14 != null) {
               LegacySnapRotManager.INSTANCE.snapAt(var14.x, var14.y, false);
            }

            Runnable var5x = null;
            if (this.delaySwap.get() && var13 != null && !var13.isEmpty() && !ItemStack.areItemsAndComponentsEqual(var4x, var13)) {
               KalamaHelperHelperK var6x = InventoryUtils.p(it -> ItemStack.areItemsAndComponentsEqual(it, var13), true, false);
               if (var6x != null) {
                  var5x = InvExtra.INSTANCE.swapInventoryIndexToHand(var6x.index());
               }
            }

            Listener.sendPacketNoEvents(var12);
            mc.player.swingHand(Hand.MAIN_HAND);
            if (var5x != null) {
               var5x.run();
            }
         });
         this.Sz = null;
         this.SA = null;
         this.SK = Tasks.b();
      }
   }
}
