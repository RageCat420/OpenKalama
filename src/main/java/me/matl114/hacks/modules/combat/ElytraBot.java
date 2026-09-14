package me.matl114.hacks.modules.combat;

import java.awt.Color;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import me.matl114.SlimefunHelper;
import me.matl114.accessors.access.PlayerInteractEntityC2SPacketAccess;
import me.matl114.accessors.hacks.PlayerInternalAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.KalamaHelperHelperJ;
import me.matl114.hacks.CombatTasks;
import me.matl114.hacks.RenderTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.move.ElytraExtra;
import me.matl114.hacks.modules.move.ElytraFlight;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.hacks.utils.config.LabelVec3;
import me.matl114.hacks.utils.config.NBTTypes;
import me.matl114.hacks.utils.config.OptionalPrimitive;
import me.matl114.hacks.utils.config.Vec2;
import me.matl114.hacks.utils.config.Vec3;
import me.matl114.hacks.utils.entity.HackUtilHelperE;
import me.matl114.hacks.utils.move.FlightVelocity;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.ConfigEnum;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.CollisionUtil;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.KalamaHelperHelperB;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.entity.PlayerInputUtils;
import me.matl114.utils.entity.PlayerInputUtils$Input;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class ElytraBot extends BaseModule {
   public final FlagRef maceCombatUseExtraAttack;
   boolean uR;
   public final FlagRef render;
   public final DoubleRef combatSmoothFlightArgument11;
   public final DoubleRef combatRange;
   public final NBTRef<OptionalPrimitive<Double>> maceCustomPullUpAngle;
   public final DoubleRef maceHeight;
   public final IntRef maceMaxExtraPullUpTick;
   public final DoubleRef spearPullOverDistance;
   public final FlagRef flyAntiSpearUseSpearResetWhenFollow;
   public final FlagRef autoControlElytra;
   CombatSubHelperV uQ;
   public final DoubleRef maceHeightGround;
   public final FlagRef combatAngleOptimizeFollow;
   public final DoubleRef combatSmoothFlightArgument16;
   Entity uI;
   public final NBTRef<OptionalPrimitive<Vec2>> combatPullUpAngleOptimize;
   public final FlagRef maceHighSpeedAttackCompensation;
   public final FlagRef mxsModeSwitchNotification;
   public final DoubleRef combatSmoothFlightArgument1;
   boolean uU;
   public final DoubleRef minAttackHeight;
   public final FlagRef enable;
   public final FlagRef macePullUpUsePredictor;
   double uT;
   public final FlagRef logSpearHit;
   public final FlagRef skipFollow;
   public final DoubleRef combatSpearRange;
   public final DoubleRef maceMaxFollowHeight;
   public final FlagRef playerOnly;
   final CombatSubHelperSX uM;
   public static ElytraBot INSTANCE;
   final CombatSubHelperSX uK;
   @Nullable
   CombatSubHelperSX uJ;
   public final NBTRef<LabelVec3> combatAngleOptimizeRange;
   public final NBTRef<OptionalPrimitive<Double>> spearNoEntryDistance;
   public final DoubleRef spearAttackDistance;
   public final EnumRef<ElytraBot$Mode> mode;
   public final DoubleRef spearAntiSpearExtraDistance;
   public final NBTRef<OptionalPrimitive<Double>> flyAntiSpearWhenPullUp;
   public final KeyBindRef switchHotkey;
   public final NBTRef<OptionalPrimitive<Double>> combatAngleOptimizeRadicalFollow;
   public final IntRef range;
   public final NBTRef<OptionalPrimitive<Double>> combatMaceChaseFollowYBias;
   public final FlagRef spearAntiSpear;
   public final IntRef mxsMaceHitWaitTicks;
   public final DoubleRef combatSmoothFlightArgument2;
   final CombatSubHelperSX uN;
   final CombatSubHelperSX uL;
   public final FlagRef maceCombatConsiderUse;
   public final FlagRef dynamicTarget;
   Entity uP;
   public final FlagRef combatSmoothFlight3;
   public final FlagRef onlyWhenNoWasd;
   public final FlagRef combatSmoothFlightArgument15;
   public final NBTRef<OptionalPrimitive<Double>> flyAntiSpear;
   public final ModulePath tK = makePath(Configs.k, "combat-bot");
   public final NBTRef<OptionalPrimitive<Double>> maceMinFallDistance;
   public final NBTRef<OptionalPrimitive<Double>> followOnGroundHeightExtra;
   boolean uS;
   public final FlagRef combatAngleOptimize;
   public final DoubleRef mxsSpearSwitchMaceHeight;
   final CombatSubHelperU uO;
   public final FlagRef followerFollowFriend;
   public final KeyBindRef hotkey;
   public final ModulePath tL = this.tK.add("elytra-bot");
   public final NBTRef<OptionalPrimitive<Double>> spearPullBackDistance;
   public final FlagRef spearUsePredictor;
   public final FlagRef combatSmoothFlight;

   public void x(Event<Void> event) {
      CombatSubHelperSX var2 = this.uJ;
      this.uJ = this.DD();
      if (this.uJ != var2) {
         if (var2 != null) {
            var2.j();
         }

         if (this.uJ != null) {
            this.uJ.i();
         }
      }

      if (!checkNull()) {
         if (this.uJ != null) {
            this.uU = mc.world.getDimension().hasCeiling() && mc.player.getY() < mc.world.getDimension().minY() + mc.world.getDimension().logicalHeight();
            this.dd();
            this.DL();
            this.uJ.h();
         }
      }
   }

   public void iq(Event<Void> event) {
      if (this.enable.get()) {
         CombatSubHelperSX var2 = this.uJ;
         if (var2 != null) {
            var2.O(event);
         }
      }
   }

   public void DN(Event<KalamaHelperHelperI<FlightVelocity>> event) {
      if (this.enable.get()) {
         CombatSubHelperSX var2 = this.uJ;
         if (var2 != null) {
            if (this.onlyWhenNoWasd.get()) {
               PlayerInputUtils$Input var3 = PlayerInputUtils.of(mc.options);
               if (var3.rv()) {
                  var2.L();
                  return;
               }
            }

            var2.onElytra(event);
         }
      }
   }

   public void DM(Event<VDrawContext> eventVDraw) {
      if (this.enable.get() && this.render.get() && this.uJ != null && this.uI != null) {
         VDrawContext var2 = (VDrawContext)eventVDraw.b;
         var2.f().pushMatrix();
         var2.f().translate(200.0F, 200.0F);
         var2.A(mc.textRenderer, "Action: %s, Combating: %s".formatted(this.uQ, String.valueOf(this.uR)), 0, 0, -1, true);
         var2.f().popMatrix();
      }
   }

   public boolean DF(Entity entity, boolean maceAttack) {
      return maceAttack
         && this.maceHighSpeedAttackCompensation.get()
         && this.DG()
         && entity != null
         && entity == this.uI
         && mc.player != null
         && (mc.player.isFallFlying() || mc.player.getAbilities().flying);
   }

   public void De() {
      if (this.skipFollow.get()) {
         this.mode.set(this.mode.get() == ElytraBot$Mode.MACE_ARUA ? ElytraBot$Mode.SPEAR_ARUA : ElytraBot$Mode.MACE_ARUA);
      } else {
         this.mode.next();
      }

      this.logI18N("message.module.elytra-bot.mode-switch", new Object[]{this.mode.get().resultAsString()});
   }

   public boolean DA() {
      return this.enable.get() && this.autoControlElytra.get() && this.uJ != null && this.uJ.u != null && this.uJ.u.lengthSquared() > 1.0E-9;
   }

   public boolean DC() {
      return this.uI instanceof PlayerEntity var2 && SpearEnhance.isUsingSpear(var2);
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.S(), this::x);
      this.registerListener(Listener.bx().c(FlightVelocity.class), this::DN);
      this.registerListener(Listener.ap().getChannel(PlayerInteractEntityC2SPacket.class), this::DO);
      this.registerListener(Listener.bd(), this::iq);
      this.registerListener(Listener.ap().getChannel(EntityStatusS2CPacket.class), this::hV);
      this.registerListener(RenderListener.q(), this::B);
      if (SlimefunHelper.DEV_ENV) {
         this.registerListener(RenderListener.r(), this::DM);
      }

      this.registerListener(Listener.ap().getChannel(EntityDamageS2CPacket.class), this::jt);
   }

   public boolean DH() {
      return this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.SPEAR_ARUA}) || this.mode.get() == ElytraBot$Mode.MXS && !this.uO.V();
   }

   public Box DI(Entity entity) {
      if (!this.DF(entity, true)) {
         return entity.getBoundingBox();
      } else {
         Vec3d var2 = PositionPredict.INSTANCE.acK(entity);
         return entity.getBoundingBox().offset(var2.multiply(this.DJ(entity)));
      }
   }

   public void dd() {
      if (!EntityUtils.isEntityValid(this.uI) || this.uI.getPos().squaredDistanceTo(mc.player.getPos()) > this.range.get()) {
         this.uI = null;
      }

      if (this.uI == null || this.dynamicTarget.get()) {
         this.uI = this.uJ != null ? this.uJ.searchTarget() : null;
      }
   }

   public void jt(Event<EntityDamageS2CPacket> e) {
      if (!checkNull()) {
         if (this.uJ instanceof CombatSubHelperG var3
            && this.enable.get()
            && ((EntityDamageS2CPacket)e.b).sourceCauseId() == mc.player.getId()
            && mc.world.getEntityById(((EntityDamageS2CPacket)e.b).entityId()) == this.uI) {
            RegistryKey var4 = (RegistryKey)((EntityDamageS2CPacket)e.b).sourceType().getKey().orElse(null);
            if (KalamaHelperHelperB.a(var4, "mace_smash")) {
               var3.onHit(1);
               return;
            }

            var3.onHit(0);
         }
      }
   }

   @Override
   public void onCreate() {
      super.onCreate();
   }

   public void DL() {
      if (this.uI != this.uP) {
         this.uP = this.uI;
         this.uQ = null;
      }

      if (this.uI != null) {
         double var1 = this.DE();
         this.uR = TargetSelector.INSTANCE.akm(mc.player.getPos(), this.uI.getBoundingBox(), var1);
         this.uS = this.uI.isOnGround() || CollisionUtil.af(this.uI);
         if (this.uI instanceof PlayerEntity var4) {
            if (this.uS) {
               this.uQ = CombatSubHelperV.Ro;
            } else {
               List var30 = ((PlayerInternalAccess)(Object)this.uI).getPredictorImpl().getLastKnownPositions(3);
               if (var30.size() < 2) {
                  this.uQ = CombatSubHelperV.Rn;
               } else {
                  int var5 = Tasks.b();
                  HackUtilHelperE var6 = (HackUtilHelperE)var30.get(0);
                  if (var5 - var6.tick() > 20) {
                     this.uQ = CombatSubHelperV.Rp;
                  } else {
                     Vec3d var7 = var6.vec3d();
                     Vec3d var8 = ((HackUtilHelperE)var30.get(1)).vec3d();
                     Vec3d var9 = ((HackUtilHelperE)var30.get(2)).vec3d();
                     double var10 = var7.distanceTo(var8);
                     double var12 = var8.distanceTo(var9);
                     if (var10 < 1.0E-6 && var12 < 1.0E-6) {
                        this.uQ = CombatSubHelperV.Rp;
                     } else if (var10 < 0.75 && var12 < 0.75) {
                        this.uQ = CombatSubHelperV.Ro;
                     } else if (var30.size() >= 3) {
                        Vec3d var14 = var8.subtract(var7);
                        Vec3d var15 = var9.subtract(var8);
                        double var16 = var14.dotProduct(var15);
                        double var18 = var14.length();
                        double var20 = var15.length();
                        double var22 = Math.acos(Math.min(1.0, Math.max(-1.0, var16 / (var18 * var20))));
                        double var24 = Math.toDegrees(var22);
                        if (var24 < 60.0) {
                           Vec3d var26 = mc.player.getPos();
                           Vec3d var27 = var26.subtract(var9);
                           double var28 = var15.normalize().dotProduct(var27.normalize());
                           if (var28 > 0.0) {
                              this.uQ = CombatSubHelperV.Rm;
                           } else {
                              this.uQ = CombatSubHelperV.Rl;
                           }
                        } else {
                           this.uQ = CombatSubHelperV.Rn;
                        }
                     } else {
                        this.uQ = CombatSubHelperV.Rm;
                     }
                  }
               }
            }
         } else {
            this.uQ = CombatSubHelperV.Ro;
         }
      }
   }

   public boolean DK(Entity entity) {
      double var2 = CombatTasks.j().getAttackAtTargetRange(entity);
      if (!this.DF(entity, true)) {
         return TargetSelector.INSTANCE.akm(mc.player.getPos(), entity.getBoundingBox(), var2);
      } else {
         Vec3d var4 = mc.player.getPos().add(mc.player.getVelocity().multiply(this.DJ(entity)));
         return TargetSelector.INSTANCE.akm(var4, this.DI(entity), var2);
      }
   }

   public void hV(Event<EntityStatusS2CPacket> statusS2CPacketEvent) {
      EntityStatusS2CPacket var2 = (EntityStatusS2CPacket)statusS2CPacketEvent.b;
      if (this.uJ instanceof CombatSubHelperG var4
         && mc.player != null
         && mc.world != null
         && this.enable.get()
         && var2.getEntity(mc.world) == mc.player
         && var2.getStatus() == 2) {
         if (this.logSpearHit.get() && this.DH()) {
            this.logI18N("message.module.elytra-bot.spear-hit", new Object[0]);
         }

         var4.onHit(2);
      }
   }

   public void DO(Event<PlayerInteractEntityC2SPacket> attack) {
      if (this.enable.get() && this.uJ != null && PlayerInteractEntityC2SPacketAccess.of((PlayerInteractEntityC2SPacket)attack.b).isAttack()) {
         Entity var2 = mc.world.getEntityById(PlayerInteractEntityC2SPacketAccess.of((PlayerInteractEntityC2SPacket)attack.b).getEntityId());
         if (var2 != null) {
            this.uJ.f(var2);
         }
      }
   }

   public boolean DG() {
      return this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA}) || this.mode.get() == ElytraBot$Mode.MXS && this.uO.V();
   }

   public boolean DB() {
      return this.uU || this.uS;
   }

   public double DE() {
      return this.uI instanceof PlayerEntity var2 && SpearEnhance.isUsingSpear(var2) ? this.combatSpearRange.get() : this.combatRange.get();
   }

   public void B(Event<MatrixStack> event) {
      if (this.enable.get() && this.render.get()) {
         RenderUtils.startDrawVirtual((MatrixStack)event.b);

         try {
            MatrixStack var2 = (MatrixStack)event.b;
            if (this.uJ != null) {
               Vec3d var3 = this.uJ.u.add(mc.player.getPos());
               if (var3 != null) {
                  RenderUtils.drawOutlinedBox(var2, var3.add(RenderTasks.l), var3.add(RenderTasks.n), Color.MAGENTA);
               }
            }
         } finally {
            RenderUtils.stopDrawVirtual((MatrixStack)event.b);
         }
      }
   }

   @Override
   public void addCustomWidgets(Consumer<DrawableWidget> acceptor, int dx, int dy, int dblank) {
      super.addCustomWidgets(acceptor, dx, dy, dblank);
      DrawableWidget var5 = this.createTitleLabel("widget.attack.attack.use-argument", 0, dblank, dx, dy);
      acceptor.accept(new KalamaHelperHelperJ<>(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA}) ? var5 : null, 0, 0));
   }

   public CombatSubHelperSX DD() {
      if (this.enable.get()) {
         return (CombatSubHelperSX)(switch ((ElytraBot$Mode)this.mode.get()) {
            case FOLLOW -> this.uK;
            case MACE_ARUA -> this.DB() ? this.uM : this.uL;
            case SPEAR_ARUA -> this.uN;
            case MXS -> this.uO;
         });
      } else {
         return null;
      }
   }

   private int DJ(Entity entity) {
      int var2 = 0;
      if (mc.getNetworkHandler() != null) {
         PlayerListEntry var3 = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
         if (var3 != null) {
            var2 = var3.getLatency();
         }
      }

      int var7 = Math.max(1, Math.min(3, (var2 + 49) / 50));
      double var4 = PositionPredict.INSTANCE.acK(entity).length();
      int var6 = Math.max(0, Math.min(3, (int)Math.ceil((var4 - 1.0) / 1.5)));
      return var7 + var6;
   }

   public ElytraBot() {
      super("ElytraBot");
      this.mode = this.builder(this.tL.add("mode"), ElytraBot$Mode.class).defaultValue(ElytraBot$Mode.FOLLOW).build();
      this.enable = this.flagBuilder(this.tL.add("enable")).build();
      this.hotkey = this.moduleEntry(this.tL.add("hotkey"), new MultiKeyBind(), this.tL.add("enable"), moduleMeta(() -> this.mode)).build();
      this.range = this.intBuilder(this.tL.add("range")).defaultValue(80).validator(Configs.e).build();
      this.playerOnly = this.builder(this.tL.add("player-only"), FlagRef.TYPE).defaultValue(true).build();
      this.autoControlElytra = this.flagBuilder(this.tL.add("auto-control-elytra")).build();
      this.dynamicTarget = this.flagBuilder(this.tL.add("dynamic-target")).build();
      this.onlyWhenNoWasd = this.flagBuilder(this.tL.add("only-when-no-wasd")).build();
      this.combatRange = this.builder(this.tL.add("combat-range"), DoubleRef.TYPE).defaultValue(10.0).build();
      this.combatSpearRange = this.builder(this.tL.add("combat-spear-range"), DoubleRef.TYPE).defaultValue(11.0).build();
      this.followerFollowFriend = this.flagBuilder(this.tL.add("follower-follow-friend"))
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.FOLLOW}))
         .build();
      this.followOnGroundHeightExtra = this.builder(this.tL.add("follow-on-ground-height-extra"), OptionalPrimitive.DOUBLE_TYPE)
         .defaultValue(new OptionalPrimitive<>(true, NBTTypes.e, 2.0))
         .show(() -> this.mode.get().isNotIn(new ConfigEnum[]{ElytraBot$Mode.SPEAR_ARUA}))
         .build();
      this.maceCombatUseExtraAttack = this.builder(this.tL.add("mace-combat-use-extra-attack"), Boolean.class)
         .defaultValue(true)
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA}))
         .build();
      this.maceCombatConsiderUse = this.flagBuilder(this.tL.add("mace-combat-consider-use"))
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA}))
         .build();
      this.maceHeight = this.builder(this.tL.add("mace-height"), DoubleRef.TYPE)
         .defaultValue(10.0)
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA}))
         .build();
      this.maceHeightGround = this.builder(this.tL.add("mace-height-ground"), DoubleRef.TYPE)
         .defaultValue(10.0)
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA}))
         .build();
      this.minAttackHeight = this.builder(this.tL.add("min-attack-height"), Double.class)
         .defaultValue(4.0)
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA}))
         .build();
      this.maceMaxExtraPullUpTick = this.builder(this.tL.add("mace-max-extra-pull-up-tick"), IntRef.TYPE)
         .defaultValue(20)
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA}))
         .build();
      this.maceMinFallDistance = this.builder(this.tL.add("mace-min-fall-distance"), OptionalPrimitive.DOUBLE_TYPE)
         .defaultValue(new OptionalPrimitive<>(false, NBTTypes.e, 1.5))
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA}))
         .build();
      this.maceMaxFollowHeight = this.doubleBuilder(this.tL.add("mace-max-follow-height"))
         .defaultValue(1.5)
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA}))
         .build();
      this.macePullUpUsePredictor = this.flagBuilder(this.tL.add("mace-pull-up-use-predictor"))
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA}))
         .build();
      this.maceCustomPullUpAngle = this.builder(this.tL.add("mace-custom-pull-up-angle"), OptionalPrimitive.DOUBLE_TYPE)
         .defaultValue(new OptionalPrimitive<>(false, NBTTypes.e, 45.0))
         .validator(s -> s.getValue() > 0.0 && s.getValue() < 90.0)
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA}))
         .build();
      this.maceHighSpeedAttackCompensation = this.flagBuilder(this.tL.add("mace-high-speed-attack-compensation"))
         .defaultValue(false)
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA}))
         .build();
      this.combatSmoothFlight = this.flagBuilder(this.tL.add("combat-smooth-flight"))
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA}))
         .build();
      this.combatSmoothFlightArgument1 = this.doubleBuilder(this.tL.add("combat-smooth-flight-argument-1"))
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA}))
         .defaultValue(1.0)
         .build();
      this.combatSmoothFlightArgument11 = this.doubleBuilder(this.tL.add("combat-smooth-flight-argument-1-1"))
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA}))
         .defaultValue(1.0)
         .build();
      this.combatSmoothFlightArgument16 = this.doubleBuilder(this.tL.add("combat-smooth-flight-argument-1-6"))
         .defaultValue(16.0)
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA}))
         .build();
      this.combatSmoothFlightArgument15 = this.flagBuilder(this.tL.add("combat-smooth-flight-argument-1-5"))
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA}))
         .build();
      this.combatSmoothFlight3 = this.flagBuilder(this.tL.add("combat-smooth-flight-3"))
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA}))
         .build();
      this.combatSmoothFlightArgument2 = this.doubleBuilder(this.tL.add("combat-smooth-flight-argument-2"))
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA}))
         .defaultValue(1.5)
         .build();
      this.combatAngleOptimize = this.builder(this.tL.add("combat-angle-optimize"), Boolean.class)
         .defaultValue(false)
         .show(
            () -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA})
               && ElytraExtra.INSTANCE.autoRescaleFireworkBox.get()
               && ElytraFlight.INSTANCE.useAutoRescale.get()
         )
         .build();
      this.combatAngleOptimizeFollow = this.builder(this.tL.add("combat-angle-optimize-follow"), Boolean.class)
         .defaultValue(false)
         .show(
            () -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA})
               && ElytraExtra.INSTANCE.autoRescaleFireworkBox.get()
               && ElytraFlight.INSTANCE.useAutoRescale.get()
         )
         .build();
      this.combatAngleOptimizeRange = this.builder(this.tL.add("combat-angle-optimize-range"), LabelVec3.class)
         .defaultValue(
            new LabelVec3(
               "widget.elytra-bot.angle.normal-flight",
               "widget.elytra-bot.angle.spear-flight",
               "widget.elytra-bot.angle.anti-spear-flight",
               new Vec3(4.0, 4.0, 4.0)
            )
         )
         .show(
            () -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA})
               && ElytraExtra.INSTANCE.autoRescaleFireworkBox.get()
               && ElytraFlight.INSTANCE.useAutoRescale.get()
         )
         .build();
      this.combatAngleOptimizeRadicalFollow = this.builder(this.tL.add("combat-angle-optimize-radical-follow"), OptionalPrimitive.DOUBLE_TYPE)
         .defaultValue(new OptionalPrimitive<>(true, NBTTypes.e, 75.0))
         .validator(s -> s.getValue() >= 0.0 && s.getValue() <= 90.0)
         .show(
            () -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA})
               && ElytraExtra.INSTANCE.autoRescaleFireworkBox.get()
               && ElytraFlight.INSTANCE.useAutoRescale.get()
         )
         .build();
      this.combatPullUpAngleOptimize = this.builder(this.tL.add("combat-pull-up-angle-optimize"), OptionalPrimitive.type(Vec2.class))
         .defaultValue(new OptionalPrimitive<>(false, NBTTypes.p, new Vec2(6.0, 4.0)))
         .show(
            () -> SlimefunHelper.DEV_ENV
               && this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA})
               && ElytraExtra.INSTANCE.autoRescaleFireworkBox.get()
               && ElytraFlight.INSTANCE.useAutoRescale.get()
         )
         .build();
      this.combatMaceChaseFollowYBias = this.builder(this.tL.add("combat-mace-chase-follow-y-bias"), OptionalPrimitive.DOUBLE_TYPE)
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA}))
         .defaultValue(new OptionalPrimitive<>(false, NBTTypes.e, 3.0))
         .build();
      this.flyAntiSpear = this.builder(this.tL.add("fly-anti-spear"), OptionalPrimitive.DOUBLE_TYPE)
         .defaultValue(new OptionalPrimitive<>(true, NBTTypes.e, 1.0))
         .show(() -> this.mode.get().isNotIn(new ConfigEnum[]{ElytraBot$Mode.SPEAR_ARUA}))
         .build();
      this.logSpearHit = this.flagBuilder(this.tL.add("log-spear-hit")).show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.SPEAR_ARUA})).build();
      this.spearAntiSpear = this.flagBuilder(this.tL.add("spear-anti-spear"))
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.SPEAR_ARUA}))
         .build();
      this.spearAntiSpearExtraDistance = this.doubleBuilder(this.tL.add("spear-anti-spear-extra-distance"))
         .defaultValue(0.0)
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.SPEAR_ARUA}))
         .build();
      this.spearUsePredictor = this.flagBuilder(this.tL.add("spear-use-predictor"))
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.SPEAR_ARUA}))
         .build();
      this.spearAttackDistance = this.doubleBuilder(this.tL.add("spear-attack-distance"))
         .defaultValue(8.0)
         .validator(Configs.doubleRange(0.0, 100.0))
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.SPEAR_ARUA}))
         .build();
      this.spearNoEntryDistance = this.builder(this.tL.add("spear-no-entry-distance"), OptionalPrimitive.DOUBLE_TYPE)
         .defaultValue(new OptionalPrimitive<>(false, NBTTypes.e, 3.0))
         .validator(s -> s.getValue() >= 0.0 && s.getValue() <= 100.0)
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.SPEAR_ARUA}))
         .build();
      this.spearPullBackDistance = this.builder(this.tL.add("spear-pull-back-distance"), OptionalPrimitive.DOUBLE_TYPE)
         .defaultValue(new OptionalPrimitive<>(false, NBTTypes.e, 8.0))
         .validator(s -> s.getValue() >= 0.0 && s.getValue() <= 100.0)
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.SPEAR_ARUA}))
         .build();
      this.spearPullOverDistance = this.doubleBuilder(this.tL.add("spear-pull-over-distance"))
         .defaultValue(8.0)
         .validator(Configs.doubleRange(0.0, 100.0))
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.SPEAR_ARUA}))
         .build();
      this.flyAntiSpearWhenPullUp = this.builder(this.tL.add("fly-anti-spear-when-pull-up"), OptionalPrimitive.DOUBLE_TYPE)
         .defaultValue(new OptionalPrimitive<>(false, NBTTypes.e, 1.0))
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA}))
         .build();
      this.flyAntiSpearUseSpearResetWhenFollow = this.flagBuilder(this.tL.add("fly-anti-spear-use-spear-reset-when-follow"))
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MACE_ARUA}))
         .build();
      this.render = this.flagBuilder(this.tL.add("render")).build();
      this.switchHotkey = this.hotkey(this.tL.add("switch-hotkey")).defaultValue(new MultiKeyBind()).registerHotkey(HotKeyUtils.b(this::De)).build();
      this.skipFollow = this.flagBuilder(this.tL.add("skip-follow")).defaultValue(false).build();
      this.mxsModeSwitchNotification = this.flagBuilder(this.tL.add("mxs-mode-switch-notification"))
         .defaultValue(true)
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MXS}))
         .build();
      this.mxsMaceHitWaitTicks = this.intBuilder(this.tL.add("mxs-mace-hit-wait-ticks"))
         .defaultValue(1)
         .validator(Configs.e)
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MXS}))
         .build();
      this.mxsSpearSwitchMaceHeight = this.doubleBuilder(this.tL.add("mxs-spear-switch-mace-height"))
         .defaultValue(0.0)
         .validator(Configs.doubleRange(0.0, 100.0))
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{ElytraBot$Mode.MXS}))
         .build();
      this.uK = new CombatSubHelperO().R(this);
      this.uL = new CombatSubHelperFX().R(this);
      this.uM = new CombatSubHelperDX().R(this);
      this.uN = new CombatSubHelperIX().R(this);
      this.uT = 1.0;
      this.uU = false;
      this.bindFlag(this.enable);
      INSTANCE = this;
      this.uO = new CombatSubHelperU();
      this.uO.R(this);
   }
}
