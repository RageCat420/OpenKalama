package me.matl114.hacks.modules.move;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.OptionalInt;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Predicate;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.accessors.access.FireworkRocketEntityAccess;
import me.matl114.accessors.access.PlayerInteractItemC2SPacketAccess;
import me.matl114.accessors.hacks.EntityInternalAccess;
import me.matl114.accessors.hacks.PlayerInteractionAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.PacketManager;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.ACTasks;
import me.matl114.hacks.CombatTasks;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.modules.ac.PacketOrderManager;
import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.hacks.utils.config.NBTTypes;
import me.matl114.hacks.utils.config.OptionalPrimitive;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.hacks.utils.config.WrapEnum;
import me.matl114.hacks.utils.entity.HackUtilHelperD;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.hacks.utils.move.HackUtilHelperF;
import me.matl114.hacks.utils.tasks.CounterExecutor;
import me.matl114.hacks.utils.tasks.StateExecutor;
import me.matl114.hooks.BaritoneHooks;
import me.matl114.hooks.ViaFabricPlusHooks;
import me.matl114.managers.Configs;
import me.matl114.managers.Configs$BypassMode;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.ConfigEnum;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.KalamaHelperHelperB;
import me.matl114.utils.NetworkUtils;
import me.matl114.utils.WorldUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.entity.PlayerInputUtils;
import me.matl114.utils.entity.PlayerInputUtils$Input;
import me.matl114.versioned.api.VItem;
import me.matl114.versioned.api.VPacket;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.data.DataTracker.SerializedEntry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MaceItem;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.ApiStatus.Experimental;

public class ElytraExtra extends BaseModule implements HackUtilHelperJ {
   final int PX;
   private boolean Pz;
   int Qd;
   public final KeyBindRef enableHotkey;
   int PP;
   public final FlagRef armorFlyFixGrimBadPackets1;
   public final EnumRef<ElytraExtra$ArmorFlyMode> armorMode;
   @Experimental
   public final KeyBindRef switchAutoRescaleFireworkAlKey;
   public final ModulePath JY = makePath(Configs.m, "elytra");
   public final FlagRef autoSwitch;
   public final IntRef fireworkDelayMultiplyVanilla;
   public final FlagRef speedLimitEnable;
   public final NBTRef<OptionalPrimitive<WrapEnum<Configs$BypassMode>>> noKineticMode;
   public boolean PJ;
   private static HackUtilHelperD Oz;
   public final FlagRef noFallWhenLanding;
   public final FlagRef autoRescaleFireworkBox;
   Deque<Integer> PD;
   FireworkRocketEntity PT;
   public final NBTRef<Regex> fireworkItemId;
   public final ModulePath OF;
   public final EnumRef<ElytraExtra$Al> autoRescaleFireworkAl;
   public final FlagRef logNoFireworks;
   private final CounterExecutor PC;
   boolean PS;
   public final FlagRef speedDropOptimization;
   public final IntRef autoTakeOffRetryTicks;
   public int PH;
   public final DoubleRef fireworkBoostSpeed;
   public final KeyBindRef OS;
   public final FlagRef poseFix;
   int PU;
   int PR;
   public final NBTRef<OptionalPrimitive<WrapEnum<Configs$BypassMode>>> maceHitFixMode;
   boolean Py;
   int PY;
   public final IntRef period;
   boolean PF;
   public final FlagRef autoStartFlyJoinServer;
   public boolean PL;
   StateExecutor PN;
   public final FlagRef enableLiquidFly;
   int PZ;
   public final DoubleRef autoRescaleAxisZeroPointThree;
   public int PA;
   public final FlagRef liquidFlyOnlyFireworks;
   EntityDimensions PQ;
   public Deque<ItemStack> PE;
   public final FlagRef fireworkLiquidFly;
   public final FlagRef landAutoSneak;
   public final FlagRef enable2;
   public final FlagRef landAutoClose;
   public final FlagRef autoTakeOffFireworksFromGround;
   public final FlagRef IA;
   public int PK;
   public final FlagRef fireworkBoostEnable;
   public static ElytraExtra INSTANCE;
   public final FlagRef onGroundFlyOnlyFireworks;
   private static final double OA = 0.002;
   public final ModulePath OC = this.JY.add("elytra-tweaks");
   boolean PO;
   public int PI;
   public final FlagRef forceNoElytra;
   Vec3d Qf;
   int PG;
   public final FlagRef renderFix;
   Vec3d Qe;
   public final DoubleRef autoRescaleFireworkAmount;
   public final FlagRef fireworkAutoUseVanilla;
   public final FlagRef resetVanilla;
   Packet<?> Qc;
   public final FlagRef enable;
   public final DoubleRef speedLimit;
   public final FlagRef flyRocketOnFirstOff;
   public final KeyBindRef fireworkBoostHotkey;
   public final KeyBindRef flyRocketOnFirstOffHotkey;
   boolean Qa;
   boolean Qb;
   boolean PM;
   public final KeyBindRef clickFireworkUse;
   int PV;
   private final CounterExecutor PB;
   public final FlagRef fireworkBoostUseRescale;
   int PW;
   public final NBTRef<OptionalPrimitive<Integer>> closeContinueFly;
   public final ModulePath OE;
   public final MoveSubHelperAX Pi;
   private static HackUtilHelperD cy;
   private static final HackUtilHelperJ OB = new MoveSubHelperB();
   public final ModulePath OD = this.JY.add("unbreakable-elytra");
   public final KeyBindRef autoTakeOff;

   public int afL(ItemStack stack) {
      if (stack.isOf(Items.FIREWORK_ROCKET)) {
         FireworksComponent var2 = (FireworksComponent)stack.get(DataComponentTypes.FIREWORKS);
         if (var2 != null) {
            return 1 + var2.flightDuration();
         }
      }

      return 1;
   }

   @Override
   public void iC(Event<LegalMovementManager> movementManagerEvent) {
      ClientPlayerEntity var2 = ((LegalMovementManager)movementManagerEvent.b).c.a;
      if (var2.isFallFlying()) {
         var2.horizontalCollision = false;
         if (this.IA.get() && this.PM) {
            PlayerInputUtils$Input var3 = PlayerInputUtils.a(var2);
            var3.rx(false).ry(false).rz(false).rA(false).rB(true);
            var3.applyInput(var2);
         }
      }

      if (this.agb()) {
         ClientPlayerAccess.of(var2).resyncPos();
      }

      this.PM = false;
      NoFall var6 = MovTasks.ap();
      boolean var4 = false;
      if (var2.isFallFlying()
         && this.noFallWhenLanding.get()
         && !this.Qb
         && var6.entityStage == 1
         && (var2.getY() <= var6.Uw - var6.UF || Tasks.b() < this.Qd + 3)
         && !MovTasks.aA().ae.get()) {
         boolean var5 = var2.isOnGround() && !((LegalMovementManager)movementManagerEvent.b).c.b;
         if (var5) {
            if (EntityUtils.isEntityValid(this.PT)) {
               this.Qc = VPacket.g(
                  var2.getX(), ((LegalMovementManager)movementManagerEvent.b).c.g.y + 9.0E-8, var2.getZ(), false, mc.player.horizontalCollision
               );
               movementManagerEvent.cancel();
               ClientPlayerAccess.of(var2).resyncPos();
               var2.setOnGround(false);
               MovTasks.ap().setLastOnGroundHeight(((LegalMovementManager)movementManagerEvent.b).c.g.y + 9.0E-8);
               var4 = true;
            } else if (this.enable2.get() && this.PI != -1) {
               var2.setOnGround(false);
               ((LegalMovementManager)movementManagerEvent.b).c.restorePos();
               FloatingUtils.INSTANCE.SB(true);
               var4 = true;
            } else {
               this.Qc = VPacket.g(
                  var2.getX(), ((LegalMovementManager)movementManagerEvent.b).c.g.y + 9.0E-8, var2.getZ(), false, mc.player.horizontalCollision
               );
               movementManagerEvent.cancel();
               ClientPlayerAccess.of(var2).resyncPos();
               var2.setOnGround(false);
               var4 = true;
            }
         }
      }

      if (!var4) {
         this.Qb = false;
      } else {
         this.Qb = true;
      }
   }

   public int afC() {
      if (ClientPlayerAccess.of(mc.player).getServerScreenHandler() == mc.player.playerScreenHandler) {
         KalamaHelperHelperK var1 = InventoryUtils.findPlayerItem(
            item -> VItem.w().a(item) && mc.player.getPreferredEquipmentSlot(item) == EquipmentSlot.CHEST && ElytraItem.isUsable(item), true, false, false
         );
         if (var1 != null) {
            return mc.player.playerScreenHandler.getSlotIndex(mc.player.getInventory(), var1.index()).orElse(-1);
         }
      }

      return -1;
   }

   @Override
   public void bb(Event<LegalMovementManager> movementManagerEvent) {
      ClientPlayerEntity var2 = ((LegalMovementManager)movementManagerEvent.b).c.a;
      this.agt();
      if (this.enable2.get() && var2.isFallFlying()) {
         if (this.afs()) {
            this.PN.state(true);
         }

         boolean var3 = afO();
         if (this.armorMode.get() != ElytraExtra$ArmorFlyMode.LAZY && this.PH == -1) {
            if (this.PL) {
               this.PL = false;
               if (this.afV()) {
                  mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
                  this.PM = true;
                  this.agc();
                  MovTasks.aj().sendPacketsForPreStartFallFlying();
                  if (this.armorFlyFixGrimBadPackets1.get()) {
                     mc.getNetworkHandler().sendPacket(new CommonPongC2SPacket(Integer.MIN_VALUE));
                  }
               } else {
                  this.agd();
                  EntityInternalAccess.of(mc.player).setDataFlag(7, false);
                  this.afX(false);
               }
            } else if (this.armorMode.get() == ElytraExtra$ArmorFlyMode.TICK_LEGACY) {
               if (var3) {
                  if (!VItem.w().a(var2.getEquippedStack(EquipmentSlot.CHEST))) {
                     int var4 = this.afC();
                     if (var4 != -1) {
                        this.afN(var4);
                        this.PH = var4;
                        this.PI = this.PH;
                     }
                  } else {
                     this.PH = this.PI;
                  }
               } else {
                  this.afX(false);
               }
            }
         }
      } else {
         this.afX(false);
      }

      if (!mc.player.isFallFlying() && this.Pz) {
         this.Pz = false;
         if (this.aft()) {
            if (this.PA != -1) {
               this.afN(this.PA);
            }

            if (afO()) {
               this.logI18N("message.module.elytra-extra.unexpected-unbreakable-state", new Object[0]);
               mc.player.startFallFlying();
               mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
               this.PM = true;
               if (this.PA != -1) {
                  this.agc();
               }

               MovTasks.aj().sendPacketsForPreStartFallFlying();
            } else {
               this.agd();
            }
         }

         this.PA = -1;
      }

      if (this.PK != -1 && !var2.isFallFlying()) {
         this.afN(this.PK);
         this.PK = -1;
      }

      if (this.agg() && PlayerStateManager.INSTANCE.jI && mc.player.getPitch() > 0.0F) {
         mc.player.setPitch(0.0F);
         ((LegalMovementManager)movementManagerEvent.b).pushImportantRotation(true, false);
         ((LegalMovementManager)movementManagerEvent.b).c();
      }

      if (var2.isFallFlying() && this.fireworkBoostEnable.get() && this.agk(0) && !BaritoneHooks.getInstance().isBaritoneElytraProcessing()) {
         Vec2f var6 = new Vec2f(var2.getPitch(), var2.getYaw());
         Vec3d var7 = EntityUtils.pitchYawToRotation(var6.x, var6.y).multiply(this.fireworkBoostSpeed.get());
         var2.setVelocity(var7);
         if (this.fireworkBoostUseRescale.get()) {
            mc.player.setVelocity(this.agA(var7, var6.x, var6.y, mc.player.hasNoGravity()));
            float var5 = mc.player.getYaw();
            if (Tasks.b() % 2 == 0) {
               PlayerStateManager.nT(mc.player, var5 + 0.01F);
            } else {
               PlayerStateManager.nT(mc.player, var5 - 0.01F);
            }
         }
      }
   }

   public boolean afz() {
      if (!this.maceHitFixMode.get().isPresent() || !mc.player.isFallFlying()) {
         return false;
      } else {
         return this.afr() ? this.armorMode.get() == ElytraExtra$ArmorFlyMode.LAZY : true;
      }
   }

   public void afY(int value) {
      if (this.PI == -1) {
         if (value != -1) {
            this.PI = value;
            this.PK = -1;
            this.afN(value);
            this.PA = -1;
         } else if (this.PA != -1) {
            this.PI = this.PA;
            this.PA = -1;
            this.PK = -1;
            this.Pz = false;
         } else if (afQ()) {
            ItemStack var2 = mc.player.getEquippedStack(EquipmentSlot.CHEST);
            if (VItem.w().a(var2)) {
               KalamaHelperHelperK var3 = InventoryUtils.findBestPlayerInventory(
                  entry -> {
                     if (entry.index() >= 36 && entry.index() != 40) {
                        return null;
                     } else {
                        ItemStack var1 = entry.val();
                        if (var1.isEmpty()) {
                           return -3.0;
                        } else {
                           return mc.player.getPreferredEquipmentSlot(var1) == EquipmentSlot.CHEST
                              ? KalamaHelperHelperB.c(mc.player, var1, EquipmentSlot.CHEST) * KalamaHelperHelperB.d(mc.player, var1, EquipmentSlot.CHEST)
                              : null;
                        }
                     }
                  },
                  true,
                  true
               );
               if (var3 != null) {
                  int var4 = mc.player.playerScreenHandler.getSlotIndex(mc.player.getInventory(), var3.index()).orElse(-1);
                  if (var4 != -1) {
                     this.PI = var4;
                     this.PK = -1;
                     this.afN(var4);
                  }
               }
            }
         }
      }
   }

   @Override
   public void jI(Event<LegalMovementManager> movementManagerEvent, Event<Vec3d> moveEvent) {
      if (!movementManagerEvent.d()) {
         ClientPlayerEntity var3 = ((LegalMovementManager)movementManagerEvent.b).c.a;
         if (this.afr()) {
            if (this.poseFix.get()) {
               var3.setPose(EntityPose.STANDING);
            } else {
               this.PQ = var3.dimensions;
               var3.dimensions = var3.getDimensions(EntityPose.STANDING);
               var3.setBoundingBox(var3.dimensions.getBoxAt(var3.getPos()));
            }
         }

         this.PS = false;
         if (this.noKineticMode.get().isPresent() && this.PI == -1 && var3.isFallFlying()) {
            boolean var4 = EntityUtils.isEntityValid(this.PT);
            if (this.noKineticMode.get().getValue().get().hasAc()) {
               Vec3d var5 = mc.player.getVelocity().multiply(4.0);
               Vec3d var6 = MovTasks.simulateMovement(var3, mc.player.getPos(), var5, false);
               if ((!(var5.horizontalLength() > 0.3) || MathHelper.approximatelyEquals(var6.x, var5.x)) && MathHelper.approximatelyEquals(var6.z, var5.z)) {
                  if (this.PR > 0) {
                     this.PR--;
                     ((LegalMovementManager)movementManagerEvent.b).c();
                     if (var4) {
                        EntityUtils.setEntityPitchSafe(var3, -89.999F);
                        PlayerStateManager.nT(var3, var3.getYaw() + 180.0F);
                     } else {
                        PlayerStateManager.nT(var3, var3.getYaw() + 180.0F);
                     }
                  }
               } else {
                  ((LegalMovementManager)movementManagerEvent.b).c();
                  this.PR = 2;
                  if (var4) {
                     EntityUtils.setEntityPitchSafe(var3, -89.999F);
                     PlayerStateManager.nT(var3, var3.getYaw() + 180.0F);
                  } else {
                     PlayerStateManager.nT(var3, var3.getYaw() + 180.0F);
                  }
               }
            } else {
               this.PS = true;
            }
         }
      }
   }

   public void afU(Event<Boolean> booleanEvent) {
      if (!booleanEvent.d()) {
         if ((Boolean)booleanEvent.b) {
            this.PG = Tasks.b();
         }
      }
   }

   public static boolean afR() {
      return InventoryUtils.p(vv -> mc.player.getPreferredEquipmentSlot(vv) == EquipmentSlot.CHEST, false, false) != null;
   }

   public Vec3d agA(Vec3d currentMotion, float pitch, float yaw, boolean applyGravity) {
      if (!this.autoRescaleFireworkBox.get()) {
         return currentMotion;
      } else {
         return switch ((ElytraExtra$Al)this.autoRescaleFireworkAl.get()) {
            case V1 -> this.agB(currentMotion, pitch, yaw, applyGravity);
            case V2 -> this.agC(currentMotion, pitch, yaw);
            case V3 -> this.agD(currentMotion, pitch, yaw);
         };
      }
   }

   public static boolean afx(ItemStack stack) {
      return stack.getDamage() < stack.getMaxDamage() - 1;
   }

   public static boolean afQ() {
      return VItem.w().a(mc.player.getEquippedStack(EquipmentSlot.CHEST));
   }

   public boolean agn() {
      if (this.PT != null) {
         return EntityUtils.isEntityValid(this.PT) ? false : this.agl() >= 1;
      } else {
         return true;
      }
   }

   public boolean agk(int extraTicks) {
      if (this.PT != null) {
         return EntityUtils.isEntityValid(this.PT) ? true : this.agl() < extraTicks;
      } else {
         return false;
      }
   }

   public void afF(ItemStack stack) {
      this.Pi.d();
      if (!stack.isEmpty() && !stack.isOf(Items.FIREWORK_ROCKET)) {
         this.PE.add(stack);
      } else {
         this.PE.removeIf(s -> s.isEmpty() || s.isOf(Items.FIREWORK_ROCKET));
         this.PE.add(stack);
      }
   }

   public void afB(Event<PlayerInteractEntityC2SPacket> interactPacket) {
      if (!interactPacket.d()) {
         PlayerInteractEntityC2SPacket var2 = (PlayerInteractEntityC2SPacket)interactPacket.e();
         if (this.maceHitFixMode.get().isPresent()
            && ((Enum<?>)var2.type.getType()).name().equals("ATTACK")
            && mc.player.getMainHandStack().getItem() instanceof MaceItem
            && mc.player.isFallFlying()
            && this.afz()
            && !this.afA()) {
            mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
            if (!this.afr()) {
               PacketManager.d((PlayerInteractEntityC2SPacket)interactPacket.b, () -> this.PD.add(Tasks.b() + 20));
            }
         }
      }
   }

   @Override
   public int priority() {
      return 0;
   }

   public boolean agg() {
      return mc.player.isFallFlying() && this.enable2.get() && this.PI != -1 && this.onGroundFlyOnlyFireworks.get();
   }

   public void age(Event<Boolean> takeOff) {
      if ((Boolean)takeOff.b && (this.flyRocketOnFirstOff.get() || MovTasks.ay().alw() || this.autoTakeOffFireworksFromGround.get() && this.Qa)) {
         this.PP = Tasks.b();
      }
   }

   public boolean agz(Vec3d requested, Vec3d fallback, Vec3d boundary) {
      if (requested != null
         && fallback != null
         && boundary != null
         && Double.isFinite(requested.x)
         && Double.isFinite(requested.y)
         && Double.isFinite(requested.z)
         && Double.isFinite(boundary.x)
         && Double.isFinite(boundary.y)
         && Double.isFinite(boundary.z)) {
         double var4 = requested.length();
         if (Double.isFinite(var4) && !(var4 < 1.0E-6)) {
            double var6 = requested.x / var4;
            double var8 = requested.y / var4;
            double var10 = requested.z / var4;
            double var12 = boundary.x * var6 + boundary.y * var8 + boundary.z * var10;
            double var14 = fallback.x * var6 + fallback.y * var8 + fallback.z * var10;
            return var12 + 1.0E-4 >= var14;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void afZ(boolean armorFly) {
      if (mc.player != null && mc.player.isFallFlying()) {
         if (!armorFly) {
            this.afW(this.closeContinueFly.get().isPresent());
         } else {
            this.afY(-1);
         }
      }
   }

   public void agt() {
      if (!mc.player.isFallFlying()) {
         this.Qe = Vec3d.ZERO;
      }
   }

   public void afW(boolean continueFly) {
      if (this.PI != -1) {
         boolean var2 = continueFly && afO();
         boolean var3 = false;
         if (this.PH == -1 && this.PL && this.armorMode.get().isNotIn(new ConfigEnum[]{ElytraExtra$ArmorFlyMode.LAZY})) {
            this.PL = false;
            var3 = true;
         }

         if (var2) {
            this.afN(this.PI);
            if (var3) {
               mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
               this.agc();
               MovTasks.aj().sendPacketsForPreStartFallFlying();
               if (this.armorFlyFixGrimBadPackets1.get()) {
                  mc.getNetworkHandler().sendPacket(new CommonPongC2SPacket(Integer.MIN_VALUE));
               }
            } else {
               this.PD.add(Tasks.b() + this.closeContinueFly.get().getValue());
               this.Py = true;
            }
         } else if (var3) {
            this.agd();
            EntityInternalAccess.of(mc.player).setDataFlag(7, false);
         }

         this.afX(var2);
      }
   }

   public int afE() {
      if (ClientPlayerAccess.of(mc.player).getServerScreenHandler() == mc.player.playerScreenHandler) {
         Predicate<ItemStack> var1 = this::afD;
         if (this.PK != -1
            && mc.player.playerScreenHandler.slots.size() > this.PK
            && var1.test(((Slot)mc.player.playerScreenHandler.slots.get(this.PK)).getStack())) {
            return this.PK;
         }

         KalamaHelperHelperK var2 = InventoryUtils.findBestPlayerInventory(
            item -> (item.index() < 36 || item.index() == 40) && var1.test(item.val())
               ? KalamaHelperHelperB.c(mc.player, item.val(), EquipmentSlot.CHEST) * KalamaHelperHelperB.d(mc.player, item.val(), EquipmentSlot.CHEST)
               : null,
            true,
            true
         );
         if (var2 != null) {
            return mc.player.playerScreenHandler.getSlotIndex(mc.player.getInventory(), var2.index()).orElse(-1);
         }
      }

      return -1;
   }

   public void GX(Event<SerializedEntry<?>> firework) {
      if (((SerializedEntry)firework.e()).id() == 9
         && firework.getArgs(0) instanceof FireworkRocketEntity var3
         && mc.player != null
         && mc.player.isFallFlying()
         && ((SerializedEntry)firework.e()).value() instanceof OptionalInt var4
         && var4.isPresent()
         && var4.getAsInt() == mc.player.getId()) {
         this.PT = var3;
         this.PU = Tasks.b();
         this.PV = 0;
      }
   }

   private void afM(float pitch, float yaw) {
      this.Pi.d();
      ItemStack var3 = mc.player.getStackInHand(Hand.MAIN_HAND);
      if (this.afG(var3)) {
         mc.interactionManager.sendSequencedPacket(mc.world, s -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, s, yaw, pitch));
         this.agp();
      } else {
         var3 = mc.player.getStackInHand(Hand.OFF_HAND);
         if (this.afG(var3)) {
            mc.interactionManager.sendSequencedPacket(mc.world, s -> new PlayerInteractItemC2SPacket(Hand.OFF_HAND, s, yaw, pitch));
            this.agp();
         } else {
            KalamaHelperHelperK var4 = InventoryUtils.y(this::afG, false, true);
            if (var4 != null) {
               Runnable var5 = InvExtra.INSTANCE.uj(var4.index());
               if (var5 != null) {
                  mc.interactionManager.sendSequencedPacket(mc.world, s -> new PlayerInteractItemC2SPacket(Hand.OFF_HAND, s, yaw, pitch));
                  var5.run();
               }

               this.agp();
            } else {
               this.ago();
            }
         }
      }
   }

   private boolean afD(ItemStack item) {
      return item.isEmpty() || !VItem.w().a(item) && mc.player.getPreferredEquipmentSlot(item) == EquipmentSlot.CHEST;
   }

   public void afv(Event<ClientPlayerEntity> event) {
      if (this.enable2.get() && mc.player.isFallFlying() && this.PI != -1) {
         if (this.armorMode.get() == ElytraExtra$ArmorFlyMode.TICK_LEGACY) {
            this.PC.h();
            this.PJ = false;
            return;
         }

         ItemStack var2 = mc.player.getEquippedStack(EquipmentSlot.CHEST);
         if (VItem.w().a(var2)) {
            this.PC.f();
            if (this.PC.d(5)) {
               this.PJ = true;
            } else {
               this.PJ = false;
            }
         } else {
            this.PC.h();
            this.PJ = false;
         }
      } else {
         this.PC.h();
         this.PJ = false;
      }
   }

   public int agl() {
      return EntityUtils.isEntityValid(this.PT) ? 0 : Tasks.b() - this.PU - this.PV;
   }

   public boolean afV() {
      if (!this.afP()) {
         return false;
      } else {
         ItemStack var1 = mc.player.getEquippedStack(EquipmentSlot.CHEST);
         if (!VItem.w().a(var1)) {
            int var2 = this.afC();
            if (var2 != -1) {
               this.afN(var2);
               this.PH = var2;
               this.PI = this.PH;
               this.PM = true;
               return true;
            } else {
               return false;
            }
         } else {
            this.PH = -1;
            return true;
         }
      }
   }

   public boolean aga(boolean stopSprint) {
      ItemStack var2 = mc.player.getEquippedStack(EquipmentSlot.CHEST);
      if (!VItem.w().a(var2)) {
         int var3 = this.afC();
         if (var3 != -1) {
            this.PK = var3;
            this.afN(var3);
            this.PM = true;
            return true;
         } else {
            return false;
         }
      } else {
         this.PK = -1;
         return true;
      }
   }

   public boolean agj() {
      return this.agk(3);
   }

   public void jy(Event<Vec3d> velocity) {
   }

   @Override
   public void jJ(Event<LegalMovementManager> movementManagerEvent, Event<Vec3d> moveEvent) {
      ClientPlayerEntity var3 = ((LegalMovementManager)movementManagerEvent.b).c.a;
      if (this.PQ != null) {
         var3.dimensions = this.PQ;
      }

      this.PQ = null;
      if (this.PS) {
         boolean var4 = false;
         boolean var5 = EntityUtils.isEntityValid(this.PT);
         Vec3d var6 = mc.player.getRotationVector().multiply(5.1);
         Vec3d var7 = MovTasks.simulateMovement(var3, mc.player.getPos(), var6, false);
         if (!MathHelper.approximatelyEquals(var7.x, var6.x) || !MathHelper.approximatelyEquals(var7.z, var6.z)) {
            this.PR = 1;
            ((LegalMovementManager)movementManagerEvent.b).c();
            if (var5) {
               EntityUtils.setEntityPitchSafe(var3, this.PR % 2 == 0 ? -89.999F : 89.999F);
            } else {
               PlayerStateManager.nT(var3, var3.getYaw() + 180.0F);
            }

            var4 = true;
         }

         if (!var4 && !var5) {
            var6 = mc.player.getVelocity().multiply(6.0);
            var7 = MovTasks.simulateMovement(var3, mc.player.getPos(), var6, false);
            if (!MathHelper.approximatelyEquals(var7.x, var6.x) || !MathHelper.approximatelyEquals(var7.z, var6.z)) {
               this.PR = 2;
               ((LegalMovementManager)movementManagerEvent.b).c();
               if (var5) {
                  EntityUtils.setEntityPitchSafe(var3, this.PR % 2 == 0 ? -89.999F : 89.999F);
               } else {
                  PlayerStateManager.nT(var3, var3.getYaw() + 180.0F);
               }

               var4 = true;
            }
         }

         if (!var4 && this.PR > 0) {
            this.PR--;
            ((LegalMovementManager)movementManagerEvent.b).c();
            if (var5) {
               EntityUtils.setEntityPitchSafe(var3, this.PR % 2 == 0 ? -89.999F : 89.999F);
            } else {
               PlayerStateManager.nT(var3, var3.getYaw() + 180.0F);
            }

            var4 = true;
         }
      }
   }

   public void agr(float pitch, float yaw) {
      if (this.agq(true)) {
         this.Pi.d();
         ACTasks.c(s -> this.afJ(pitch, yaw));
      }
   }

   @Override
   public void mP(Event<LegalMovementManager> movementManagerEvent) {
   }

   public void agc() {
      this.PF = true;
      int var1 = InventoryUtils.getSelectedSlot();
      Runnable var2 = null;

      try {
         float var3 = PlayerStateManager.INSTANCE.jm;
         float var4 = PlayerStateManager.INSTANCE.jl;

         while (!this.PE.isEmpty()) {
            ItemStack var5 = this.PE.poll();
            if (!var5.isEmpty()) {
               KalamaHelperHelperK var6 = InventoryUtils.p(it -> ItemStack.areItemsAndComponentsEqual(var5, it), false, false);
               if (var6 != null) {
                  if (var6.index() == InventoryUtils.getSelectedSlot()) {
                     this.Pi.d();
                     Listener.sendPacketNoEvents(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, NetworkUtils.generateNextSequence(), var3, var4));
                  } else {
                     var2 = InvExtra.INSTANCE.uh(var6.index());
                     if (var2 != null) {
                        this.Pi.d();
                        Listener.sendPacketNoEvents(new PlayerInteractItemC2SPacket(Hand.OFF_HAND, NetworkUtils.generateNextSequence(), var3, var4));
                        var2.run();
                     }
                  }
               }
            } else {
               this.afM(var3, var4);
            }
         }
      } finally {
         this.PF = false;
         PlayerInteractionAccess.of(mc.interactionManager).syncSelectedHotbar(var1);
      }
   }

   public void ago() {
      if (this.PY < Tasks.b() - 200) {
         this.PY = Tasks.b();
         if (this.logNoFireworks.get()) {
            this.logI18NSub("Firework", "message.module.elytra-extra.firework-not-found", new Object[0]);
         }
      }
   }

   public Vec3d agC(Vec3d currentMotion, float pitch, float yaw) {
      if (!this.autoRescaleFireworkBox.get()) {
         this.agu(null);
         return currentMotion;
      } else if (this.PO) {
         this.PO = false;
         this.agu(null);
         return currentMotion;
      } else if (currentMotion.lengthSquared() < 1.0E-6) {
         this.agu(null);
         return currentMotion;
      } else {
         Vec3d var4 = EntityUtils.pitchYawToRotation(pitch, yaw);
         Vec3d var5 = PlayerStateManager.INSTANCE.js;
         Vec3d var6 = !PlayerStateManager.INSTANCE.jz && !PlayerStateManager.INSTANCE.jy
            ? EntityUtils.calculateGlidingVelocity(mc.player, var5, var4, true)
            : EntityUtils.simulateTravelInFluidVelocity(var5, PlayerStateManager.INSTANCE.jz, PlayerStateManager.INSTANCE.jy, true);
         Vec3d var7 = EntityUtils.pitchYawToRotation(PlayerStateManager.INSTANCE.jl, PlayerStateManager.INSTANCE.jm);
         double var8 = 0.05;
         Vec3d var10 = var4.normalize();
         Vec3d var11 = var7.normalize();
         double var12 = Math.min(-var8, var10.getX()) + Math.min(-var8, var11.getX());
         double var14 = Math.min(-var8, var10.getY()) + Math.min(-var8, var11.getY());
         double var16 = Math.min(-var8, var10.getZ()) + Math.min(-var8, var11.getZ());
         double var18 = Math.max(var8, var10.getX()) + Math.max(var8, var11.getX());
         double var20 = Math.max(var8, var10.getY()) + Math.max(var8, var11.getY());
         double var22 = Math.max(var8, var10.getZ()) + Math.max(var8, var11.getZ());
         double var24 = Math.min(this.autoRescaleFireworkAmount.get(), currentMotion.length());
         var12 *= var24;
         var18 *= var24;
         var14 *= var24;
         var20 *= var24;
         var16 *= var24;
         var22 *= var24;
         var12 = Math.max(-var24, var12);
         var18 = Math.min(var24, var18);
         var14 = Math.max(-var24, var14);
         var20 = Math.min(var24, var20);
         var16 = Math.max(-var24, var16);
         var22 = Math.min(var24, var22);
         double var28 = Math.min(0.0, var12 - var5.x);
         double var30 = Math.max(0.0, var18 - var5.x);
         double var32 = Math.min(0.0, var14 - var5.y);
         double var34 = Math.max(0.0, var20 - var5.y);
         double var36 = Math.min(0.0, var16 - var5.z);
         double var38 = Math.max(0.0, var22 - var5.z);
         double var40 = 0.0;
         double var42 = var6.x + var28 - var40;
         double var44 = var6.x + var30 + var40;
         double var46 = var6.y + var32;
         double var48 = var6.y + var34;
         double var50 = var6.z + var36 - var40;
         double var52 = var6.z + var38 + var40;
         if (!ViaFabricPlusHooks.isSupportEndTick() && var48 > 1.0E-6) {
            double var54 = currentMotion.length();
            double var56 = currentMotion.horizontalLength();
            if (var56 < 0.04 * currentMotion.y) {
               double var58 = EntityUtils.calculateGlidingVelocity(
                     mc.player, currentMotion.multiply((var54 + this.autoRescaleAxisZeroPointThree.get()) / var54), var4, true
                  )
                  .y;
               var48 = Math.max(var48, var58);
            }
         }

         Vec3d var60;
         if (this.speedDropOptimization.get()) {
            var60 = this.agy(currentMotion, var6, var42, var46, var50, var44, var48, var52);
            if (!this.agz(currentMotion, var6, var60)) {
               this.agu(null);
               return currentMotion;
            }
         } else {
            var60 = new Vec3d(
               agx(currentMotion.x, var6.x, var42, var44), agx(currentMotion.y, var6.y, var46, var48), agx(currentMotion.z, var6.z, var50, var52)
            );
            var60 = this.agw(var60);
         }

         this.agu(var60);
         return currentMotion;
      }
   }

   public void afy(Event<SerializedEntry<?>> serializedEntryUpdateEvent) {
      if (!serializedEntryUpdateEvent.d()) {
         if (serializedEntryUpdateEvent.f().length > 0
            && serializedEntryUpdateEvent.getArgs(0) instanceof ClientPlayerEntity var3
            && var3 == mc.player
            && ((SerializedEntry)serializedEntryUpdateEvent.b).id() == 0
            && var3.isFallFlying()) {
            if (!this.PD.isEmpty()) {
               SerializedEntry var8 = (SerializedEntry)serializedEntryUpdateEvent.e();
               byte var4 = (Byte)var8.value();
               if ((var4 & 128) == 0) {
                  while (!this.PD.isEmpty()) {
                     Integer var5 = this.PD.poll();
                     if (var5 > Tasks.b()) {
                        if (afO()) {
                           if (afQ()) {
                              serializedEntryUpdateEvent.context(new SerializedEntry(var8.id(), var8.handler(), (byte)(var4 | 128)));
                              MovTasks.aj().Xj();
                              mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
                              MovTasks.aj().sendPacketsForPreStartFallFlying();
                           }
                        } else {
                           this.PD.clear();
                        }
                        break;
                     }
                  }
               }
            }

            boolean var9 = false;
            boolean var10 = false;
            SerializedEntry var11 = (SerializedEntry)serializedEntryUpdateEvent.e();
            byte var6 = (Byte)var11.value();
            if ((var6 & 128) == 0) {
               this.PB.h();
               if (this.Pz) {
                  this.Pz = false;
                  var9 = true;
               }

               var10 = true;
            }

            if (this.aft() && var9) {
               SerializedEntry var14 = (SerializedEntry)serializedEntryUpdateEvent.e();
               var6 = (Byte)var14.value();
               if (this.PA != -1) {
                  this.afN(this.PA);
               }

               if (afO()) {
                  serializedEntryUpdateEvent.context(new SerializedEntry(var14.id(), var14.handler(), (byte)(var6 | 128)));
                  mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
                  this.PM = true;
                  if (this.PA != -1) {
                     this.agc();
                  }

                  MovTasks.aj().sendPacketsForPreStartFallFlying();
               } else {
                  this.agd();
               }

               this.PA = -1;
            } else if (this.enable2.get() && this.PI != -1 && var10) {
               SerializedEntry var7 = (SerializedEntry)serializedEntryUpdateEvent.e();
               var6 = (Byte)var7.value();
               if (this.armorMode.get() == ElytraExtra$ArmorFlyMode.LAZY) {
                  if (this.afV()) {
                     serializedEntryUpdateEvent.context(new SerializedEntry(var7.id(), var7.handler(), (byte)(var6 | 128)));
                     mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
                     this.PM = true;
                     this.agc();
                  } else {
                     this.agd();
                  }

                  if (this.PH != -1) {
                     this.afN(this.PH);
                     this.PH = -1;
                  }

                  MovTasks.aj().sendPacketsForPreStartFallFlying();
               } else if (this.armorMode.get() == ElytraExtra$ArmorFlyMode.TICK_LEGACY) {
                  if (this.afV()) {
                     serializedEntryUpdateEvent.context(new SerializedEntry(var7.id(), var7.handler(), (byte)(var6 | 128)));
                     mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
                     this.PM = true;
                     this.agc();
                  } else {
                     this.agd();
                  }

                  MovTasks.aj().sendPacketsForPreStartFallFlying();
               } else {
                  this.PL = true;
                  serializedEntryUpdateEvent.context(new SerializedEntry(var7.id(), var7.handler(), (byte)(var6 | 128)));
               }
            }
         }
      }
   }

   public Vec3d agw(Vec3d velocity) {
      if (this.speedLimitEnable.get() && velocity != null) {
         double var2 = this.speedLimit.get() / 20.0;
         if (Double.isFinite(var2) && !(var2 <= 0.0)) {
            double var4 = velocity.lengthSquared();
            return Double.isFinite(var4) && !(var4 <= var2 * var2) ? velocity.multiply(var2 / Math.sqrt(var4)) : velocity;
         } else {
            return var2 == 0.0 ? Vec3d.ZERO : velocity;
         }
      } else {
         return velocity;
      }
   }

   @Override
   public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
      if (this.Qc != null) {
         mc.getNetworkHandler().sendPacket(this.Qc);
         this.Qc = null;
      }

      this.PN.a(false, () -> {
         if (ViaFabricPlusHooks.isSupportEndTick() && PacketOrderManager.INSTANCE.bM) {
            ACTasks.c(s -> this.agc());
         } else {
            this.agc();
         }
      });
      if (this.PH != -1) {
         int var3 = this.PH;
         this.afN(var3);
      }

      if (this.PG == Tasks.b() && this.enable2.get() && this.forceNoElytra.get() && this.PI == -1) {
         this.afY(-1);
      }

      if (this.afr() && this.poseFix.get()) {
         mc.player.setPose(EntityPose.STANDING);
      }

      this.PH = -1;
      return true;
   }

   public void Ah(Event<PlayerPositionLookS2CPacket> setbackPacket) {
      if (!setbackPacket.d()) {
         this.PO = true;
         this.agu(null);
      }
   }

   public boolean afr() {
      return this.enable2.get() && this.PI != -1 && !this.PJ;
   }

   public void js(Event<Vec3d> velocity) {
   }

   private static double agx(double requested, double fallback, double min, double max) {
      if (requested > 1.0E-6) {
         return Math.max(min, max - 0.002);
      } else {
         return requested < -1.0E-6 ? Math.min(max, min + 0.002) : MathHelper.clamp(fallback, min, max);
      }
   }

   public void y(Event<World> event) {
      if (EntityUtils.isEntityValid(this.PT)) {
         this.agi(this.PT);
      }

      this.PT = null;
      this.PE.clear();
   }

   public void afw(Event<Integer> tickEvent) {
      if (mc.player.isFallFlying()) {
         if (this.afr()) {
            this.PB.h();
         } else {
            this.PB.f();
         }
      } else {
         this.PB.h();
      }

      if (this.aft() && this.PB.d(this.period.get()) && afO() && mc.player != null && mc.player.isFallFlying()) {
         if (this.Pz && !VItem.w().a(mc.player.getEquippedStack(EquipmentSlot.CHEST))) {
            this.PB.h();
         } else {
            this.PA = this.afu();
            if (this.PA == -1) {
               mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
               this.Pz = true;
               this.PB.h();
               if (this.resetVanilla.get()) {
                  tickEvent.context(0);
               }
            } else {
               this.afN(this.PA);
               this.Pz = true;
               this.PB.h();
               if (this.resetVanilla.get()) {
                  tickEvent.context(0);
               }
            }
         }
      }
   }

   public boolean agq(boolean vanillaCd) {
      boolean var2 = this.fireworkAutoUseVanilla.get() && this.PT != null;
      ItemStack var3 = this.afK();
      if (var3 != null) {
         int var4 = this.afL(var3);
         MoveSubHelperAX var5 = this.Pi;
         if (var5.canFire()) {
            boolean var6 = false;
            if (var2) {
               if (EntityUtils.isEntityValid(this.PT)) {
                  return false;
               }

               if (Tasks.b() > this.PU + this.PV + 1) {
                  var6 = true;
               }
            }

            if (!var6 && (!this.fireworkAutoUseVanilla.get() || this.agn()) && (!vanillaCd || var5.tryFire(var4))) {
               var6 = true;
            }

            return var6;
         }

         this.agp();
      } else {
         this.ago();
      }

      return false;
   }

   public void agf(Event<ClientCommandC2SPacket> event) {
      if (!checkNull()) {
         if (this.PP == Tasks.b() && mc.player.isFallFlying() && ((ClientCommandC2SPacket)event.b).getMode() == Mode.START_FALL_FLYING && this.agq(false)) {
            this.PN.state(true);
            this.afF(ItemStack.EMPTY);
            mc.player.setSprinting(false);
            PlayerInputUtils.a(mc.player).rD(false).applyInput(mc.player);
         }
      }
   }

   public boolean afq() {
      if (mc.player.isFallFlying()) {
         this.afJ(mc.player.getPitch(), mc.player.getYaw());
         return true;
      } else {
         return false;
      }
   }

   public void agd() {
      this.PE.clear();
   }

   public void afp(Event<ClientPlayerEntity> joinServer) {
      if (this.autoStartFlyJoinServer.get()) {
         MutableInt var2 = new MutableInt(0);
         Tasks.m(() -> {
            if (mc.player == joinServer.b) {
               if (WorldUtils.o(mc.player.getBlockPos()) && var2.incrementAndGet() > 2) {
                  if (!mc.player.isOnGround() && !mc.player.isFallFlying()) {
                     this.afo();
                  }

                  return true;
               } else {
                  return false;
               }
            } else {
               return true;
            }
         }, 10, 1);
      }
   }

   public boolean aft() {
      return this.enable.get() && mc.player != null && mc.player.getEquippedStack(EquipmentSlot.CHEST).get(DataComponentTypes.UNBREAKABLE) == null;
   }

   public boolean afA() {
      return this.maceHitFixMode.get().getValue().get() == Configs$BypassMode.BYPASS_GRIM;
   }

   public boolean afG(ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      } else if (stack.isOf(Items.FIREWORK_ROCKET)) {
         return true;
      } else {
         String var2 = Registries.ITEM.getId(stack.getItem()).getPath();
         if (this.fireworkItemId.get().test(var2)) {
            return true;
         } else {
            String var3 = ItemStackUtils.aa(stack);
            return var3 != null && this.fireworkItemId.get().test(var3);
         }
      }
   }

   public ItemStack afK() {
      KalamaHelperHelperK var1 = InventoryUtils.y(this::afG, false, true);
      return var1 != null ? ((Slot)var1.val()).getStack() : null;
   }

   public int afu() {
      if (this.PI != -1 && this.PI < InventoryUtils.F()) {
         ItemStack var1 = mc.player.getInventory().getStack(this.PI);
         if (this.afD(var1)) {
            return this.PI;
         }
      }

      int var2 = this.afE();
      if (var2 != -1) {
         if (this.PK != -1) {
            this.PK = var2;
         }

         if (this.PI != -1) {
            this.PI = var2;
         }
      }

      return var2;
   }

   public void agu(Vec3d vec3d) {
      this.Qf = vec3d;
   }

   public int agm() {
      return this.PT != null && this.PT.isAlive() ? Tasks.b() - this.PU : -1;
   }

   public ElytraExtra() {
      super("ElytraExtra");
      this.OE = this.JY.add("armor-fly");
      this.OF = this.JY.add("custom-fireworks");
      this.IA = MovTasks.aj().grimac1212InputFeatures;
      this.enable2 = this.flagBuilder(this.OE.add("enable")).updateListener(this::afZ).build();
      this.armorMode = this.builder(this.OE.add("armor-mode"), ElytraExtra$ArmorFlyMode.class).defaultValue(ElytraExtra$ArmorFlyMode.TICK).build();
      this.noKineticMode = this.builder(this.OC.add("no-kinetic-mode"), OptionalPrimitive.configEnum(Configs$BypassMode.class))
         .defaultValue(new OptionalPrimitive<>(false, NBTTypes.o.cast(), new WrapEnum<>(Configs$BypassMode.NO_BYPASS)))
         .show(() -> !this.enable2.get())
         .build();
      this.maceHitFixMode = this.builder(this.OC.add("mace-hit-fix-mode"), OptionalPrimitive.configEnum(Configs$BypassMode.class))
         .defaultValue(new OptionalPrimitive<>(false, NBTTypes.o.cast(), new WrapEnum<>(Configs$BypassMode.NO_BYPASS)))
         .show(() -> !this.enable2.get() || !this.armorMode.get().isIn(new ConfigEnum[]{ElytraExtra$ArmorFlyMode.TICK}))
         .build();
      this.noFallWhenLanding = this.flagBuilder(this.OC.add("no-fall-when-landing")).build();
      this.autoSwitch = this.flagBuilder(this.OC.add("auto-switch")).build();
      this.autoTakeOff = this.hotkey(this.OC.add("auto-take-off"), new MultiKeyBind()).registerHotkey(HotKeyUtils.c(this::afo)).build();
      this.autoTakeOffFireworksFromGround = this.flagBuilder(this.OC.add("auto-take-off-fireworks-from-ground")).build();
      this.autoTakeOffRetryTicks = this.intBuilder(this.OC.add("auto-take-off-retry-ticks")).defaultValue(3).build();
      this.autoStartFlyJoinServer = this.flagBuilder(this.OC.add("auto-start-fly-join-server")).build();
      this.fireworkLiquidFly = this.flagBuilder(this.OC.add("firework-liquid-fly")).hideConfig().build();
      this.speedLimitEnable = this.flagBuilder(this.OC.add("speed-limit-enable")).defaultValue(false).build();
      this.speedLimit = this.doubleBuilder(this.OC.add("speed-limit"))
         .defaultValue(110.0)
         .validator(Configs.doubleRange(0.0, 10000.0))
         .show(this.speedLimitEnable::get)
         .build();
      this.enable = this.flagBuilder(this.OD.add("enable")).build();
      this.OS = this.moduleEntry(this.OD.addHotkey(), new MultiKeyBind(), this.OD.addEnable()).build();
      this.period = this.intBuilder(this.OD.add("period")).defaultValue(16).validator(Configs.e).build();
      this.resetVanilla = this.flagBuilder(this.OD.add("reset-vanilla")).build();
      this.enableHotkey = this.toggleHotkey(this.OE.add("enable-hotkey"), new MultiKeyBind(), this.OE.add("enable")).build();
      this.forceNoElytra = this.flagBuilder(this.OE.add("force-no-elytra")).build();
      this.enableLiquidFly = this.flagBuilder(this.OE.add("enable-liquid-fly")).build();
      this.liquidFlyOnlyFireworks = this.flagBuilder(this.OE.add("liquid-fly-only-fireworks")).build();
      this.onGroundFlyOnlyFireworks = this.flagBuilder(this.OE.add("on-ground-fly-only-fireworks")).build();
      this.landAutoClose = this.flagBuilder(this.OE.add("land-auto-close")).build();
      this.landAutoSneak = this.flagBuilder(this.OE.add("land-auto-sneak")).build();
      this.closeContinueFly = this.builder(this.OE.add("close-continue-fly"), OptionalPrimitive.INT_TYPE)
         .defaultValue(new OptionalPrimitive<>(true, NBTTypes.c, 20))
         .validator(v -> v.getValue() >= 5)
         .build();
      this.armorFlyFixGrimBadPackets1 = this.flagBuilder(this.OE.add("armor-fly-fix-grim-bad-packets-1"))
         .show(() -> this.armorMode.get().isIn(new ConfigEnum[]{ElytraExtra$ArmorFlyMode.TICK}))
         .build();
      this.poseFix = this.flagBuilder(this.OE.add("pose-fix")).build();
      this.renderFix = this.flagBuilder(this.OE.add("render-fix")).build();
      this.fireworkItemId = this.builder(this.OF.add("firework-item-id"), Regex.class).defaultValue(new Regex("^()$")).build();
      this.fireworkDelayMultiplyVanilla = this.intBuilder(this.OF.add("firework-delay-multiply-vanilla")).defaultValue(10).validator(Configs.d).build();
      this.Pi = new MoveSubHelperAX(this.fireworkDelayMultiplyVanilla);
      this.fireworkAutoUseVanilla = this.flagBuilder(this.OF.add("firework-auto-use-vanilla")).build();
      this.autoRescaleFireworkBox = this.flagBuilder(this.OF.add("auto-rescale-firework-box")).build();
      this.autoRescaleFireworkAmount = this.doubleBuilder(this.OF.add("auto-rescale-firework-amount"))
         .defaultValue(1.65)
         .show(this.autoRescaleFireworkBox::get)
         .build();
      this.autoRescaleFireworkAl = this.builder(this.OF.add("auto-rescale-firework-al"), ElytraExtra$Al.class)
         .defaultValue(ElytraExtra$Al.V1)
         .show(this.autoRescaleFireworkBox::get)
         .build();
      this.speedDropOptimization = this.flagBuilder(this.OF.add("speed-drop-optimization"))
         .defaultValue(true)
         .show(() -> this.autoRescaleFireworkBox.get() && this.autoRescaleFireworkAl.get().isIn(new ConfigEnum[]{ElytraExtra$Al.V2, ElytraExtra$Al.V3}))
         .build();
      this.switchAutoRescaleFireworkAlKey = this.hotkey(this.OF.add("switch-auto-rescale-firework-al-key"), new MultiKeyBind())
         .registerHotkey(HotKeyUtils.b(HackUtilHelperF::a))
         .experimental()
         .build();
      this.autoRescaleAxisZeroPointThree = this.builder(this.OF.add("auto-rescale-axis-zero-point-three"), Double.class)
         .defaultValue(0.03)
         .show(this.autoRescaleFireworkBox::get)
         .build();
      this.fireworkBoostEnable = this.flagBuilder(this.OF.add("firework-boost-enable")).build();
      this.fireworkBoostHotkey = this.moduleEntry(this.OF.add("firework-boost-hotkey"), new MultiKeyBind(), this.OF.add("firework-boost-enable")).build();
      this.fireworkBoostSpeed = this.doubleBuilder(this.OF.add("firework-boost-speed")).defaultValue(1.7).validator(Configs.doubleRange(0.0, 10000.0)).build();
      this.fireworkBoostUseRescale = this.flagBuilder(this.OF.add("firework-boost-use-rescale")).show(this.autoRescaleFireworkBox::get).build();
      this.flyRocketOnFirstOff = this.flagBuilder(this.OF.add("fly-rocket-on-first-off")).build();
      this.flyRocketOnFirstOffHotkey = this.moduleEntry(
            this.OF.add("fly-rocket-on-first-off-hotkey"), new MultiKeyBind(), this.OF.add("fly-rocket-on-first-off")
         )
         .build();
      this.clickFireworkUse = this.hotkey(this.OF.add("click-firework-use")).defaultValue(new MultiKeyBind()).registerHotkey(HotKeyUtils.c(this::afq)).build();
      this.logNoFireworks = this.flagBuilder(this.OF.add("log-no-fireworks")).build();
      this.Py = false;
      this.Pz = false;
      this.PA = -1;
      this.PB = new CounterExecutor();
      this.PC = new CounterExecutor();
      this.PD = new ArrayDeque<>();
      this.PE = new ConcurrentLinkedDeque<>();
      this.PF = false;
      this.PG = 0;
      this.PH = -1;
      this.PI = -1;
      this.PJ = false;
      this.PK = -1;
      this.PL = false;
      this.PM = false;
      this.PN = new StateExecutor();
      this.PO = false;
      this.PP = 0;
      this.PQ = null;
      this.PR = 0;
      this.PS = false;
      this.PU = 0;
      this.PV = 0;
      this.PW = 0;
      this.PX = 10;
      this.PY = -1;
      this.Qb = false;
      this.Qd = 0;
      this.Qe = Vec3d.ZERO;
      this.Qf = null;
      if (cy == null) {
         cy = new HackUtilHelperD(this::cast);
         MovTasks.j.SJ(() -> cy);
      }

      cy.mN(this::cast);
      if (Oz == null) {
         Oz = new HackUtilHelperD(() -> OB);
         MovTasks.j.SJ(() -> Oz);
      }

      INSTANCE = this;
   }

   @Override
   public void gz(Event<LegalMovementManager> movementManagerEvent) {
      ClientPlayerEntity var2 = ((LegalMovementManager)movementManagerEvent.b).c.a;
      if (this.PI != -1 && MovExtra.INSTANCE.grimacSprintFeatures.get()) {
         PlayerInputUtils.a(var2).rD(false).applyInput(var2);
         var2.setSprinting(false);
      }

      if (this.Py) {
         if (var2.isFallFlying()) {
            if (PlayerStateManager.INSTANCE.jO > this.autoTakeOffRetryTicks.get() && !mc.player.isTouchingWater()) {
               this.Qa = false;
               this.Py = false;
            } else if (this.Py) {
               this.Qa = true;
            } else {
               this.Qa = false;
            }
         } else {
            boolean var3 = afR();
            if (var3 && mc.player.isOnGround()) {
               this.Qa = true;
               PlayerInputUtils.a(var2).rB(true).applyInput(var2);
            } else if (var3 && var2.checkFallFlying()) {
               MovExtra.INSTANCE.Xj();
               mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
               MovExtra.INSTANCE.sendPacketsForPreStartFallFlying();
            } else if (!mc.player.isTouchingWater()) {
               this.Py = false;
               this.Qa = false;
            } else {
               this.Qa = true;
            }
         }
      } else {
         this.Qa = false;
      }

      if (!var2.isFallFlying() && this.PZ > 0) {
         this.PZ--;
         if (this.PZ <= 1 && !var2.isSneaking()) {
            PlayerInputUtils.a(var2).rC(true).applyInput(var2);
         }
      }
   }

   public void afT(Event<Boolean> booleanEvent) {
      if (!booleanEvent.<Boolean>getArgs(0) && !booleanEvent.d()) {
         this.PI = -1;
         if (!(Boolean)booleanEvent.e()) {
            if (this.enable2.get()) {
               if (afO() && this.afV()) {
                  booleanEvent.context(Boolean.TRUE);
                  this.PN.state(true);
               }
            } else if (this.autoSwitch.get() && afO() && this.aga(true)) {
               booleanEvent.context(Boolean.TRUE);
            }
         }
      }
   }

   public Vec3d agD(Vec3d currentMotion, float pitch, float yaw) {
      if (!this.autoRescaleFireworkBox.get()) {
         this.agu(null);
         return currentMotion;
      } else if (this.speedDropOptimization.get() && this.PO) {
         this.PO = false;
         this.agu(null);
         return currentMotion;
      } else {
         return HackUtilHelperF.d(currentMotion, pitch, yaw, this.autoRescaleFireworkAmount.get());
      }
   }

   public void afn(Event<Vec3d> velocity) {
   }

   public boolean afs() {
      return this.enable2.get() && this.PI != -1 && this.PJ;
   }

   public void afH(Event<PlayerInteractItemC2SPacket> packet) {
      if (!this.PF && (this.afr() || this.Pz && this.PA != -1)) {
         ItemStack var2 = PlayerInteractItemC2SPacketAccess.of((PlayerInteractItemC2SPacket)packet.b).getItemStack();
         if (var2 != null && !var2.isEmpty() && this.afG(var2)) {
            this.afF(var2.copyWithCount(1));
            packet.cancel();
            NetworkUtils.restoreSequence(((PlayerInteractItemC2SPacket)packet.b).getSequence());
         }
      }
   }

   public void afJ(float pitch, float yaw) {
      if (!this.afr() && (!this.Pz || this.PA == -1)) {
         this.afM(pitch, yaw);
      } else {
         this.afF(ItemStack.EMPTY);
      }

      CombatTasks.y().onFireworkUse();
   }

   public boolean agb() {
      return this.PL || this.PM;
   }

   public static boolean afO() {
      return !mc.player.isTouchingWater()
         && !mc.player.getAbilities().flying
         && !mc.player.isOnGround()
         && !mc.player.hasVehicle()
         && !mc.player.hasStatusEffect(StatusEffects.LEVITATION);
   }

   public void ags(Event<TeleportConfirmC2SPacket> eventLagBack) {
      this.Qd = Tasks.b();
      this.Qb = false;
   }

   public Vec3d agv() {
      if (this.Qf != null) {
         Vec3d var1 = this.Qf;
         this.Qf = null;
         return var1;
      } else {
         return null;
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.aN().c(EntityType.PLAYER), this::afn);
      this.registerListener(Listener.az(), this::js);
      this.registerListener(Listener.aF(), this::afw);
      this.registerListener(Listener.au().c(EntityType.PLAYER), this::afy);
      this.registerListener(Listener.aG(), this::afT);
      this.registerListener(Listener.ar().getChannel(PlayerPositionLookS2CPacket.class), this::Ah);
      this.registerListener(Listener.ap().getChannel(PlayerInteractItemC2SPacket.class), this::afH);
      this.registerListener(Listener.aN().c(EntityType.PLAYER), this::jy);
      this.registerListener(Listener.au().c(EntityType.FIREWORK_ROCKET), this::GX);
      this.registerListener(Listener.aU().c(EntityType.FIREWORK_ROCKET), this::agh);
      this.registerListener(Listener.N(), this::y);
      this.registerListener(Listener.bx().c(ModulePreset.class), this::le);
      this.registerListener(Listener.ap().getChannel(PlayerInteractEntityC2SPacket.class), this::afB);
      this.registerListener(Listener.M(), this::afp);
      this.registerListener(Listener.aG(), this::afU, 2147483646);
      this.registerListener(Listener.aG(), this::age, 2147483646);
      this.registerListener(Listener.ap().getChannel(ClientCommandC2SPacket.class), this::agf);
      this.registerListener(Listener.ap().getChannel(TeleportConfirmC2SPacket.class), this::ags);
      this.registerListener(Listener.V(), this::afv);
   }

   @Override
   public void iB(Event<LegalMovementManager> movementManagerEvent) {
   }

   public void afX(boolean willContinueGliding) {
      if (this.PI != -1) {
         if (mc.player.isOnGround() && !mc.player.isFallFlying() && this.enable2.get() && this.landAutoClose.get()) {
            HotKeyUtils.f(this.OE.add("enable").toPath(), this.enable2).run();
         }

         if (this.landAutoSneak.get() && !mc.player.isSneaking()) {
            this.PZ = 2;
         }

         if (this.autoSwitch.get() && mc.player.isFallFlying() && willContinueGliding) {
            this.PK = this.PI;
         }
      }

      this.PI = -1;
      this.PJ = false;
   }

   public void afI() {
      this.afJ(PlayerStateManager.INSTANCE.jl, PlayerStateManager.INSTANCE.jm);
   }

   public void le(Event<KalamaHelperHelperI<ModulePreset>> presetEvent) {
      switch ((ModulePreset)((KalamaHelperHelperI)presetEvent.b).b()) {
         case fg:
         case fh:
            this.noKineticMode.set(this.noKineticMode.get().withValue(new WrapEnum<>(Configs$BypassMode.BYPASS_GRIM)));
            break;
         default:
            this.noKineticMode.set(this.noKineticMode.get().withValue(new WrapEnum<>(Configs$BypassMode.NO_BYPASS)));
      }

      switch ((ModulePreset)((KalamaHelperHelperI)presetEvent.b).b()) {
         case fg:
         case fh:
            this.maceHitFixMode.set(this.maceHitFixMode.get().withValue(new WrapEnum<>(Configs$BypassMode.BYPASS_GRIM)));
            break;
         default:
            this.maceHitFixMode.set(this.maceHitFixMode.get().withValue(new WrapEnum<>(Configs$BypassMode.NO_BYPASS)));
      }

      switch ((ModulePreset)((KalamaHelperHelperI)presetEvent.b).b()) {
         case fg:
         case fh:
            this.armorFlyFixGrimBadPackets1.set(true);
            break;
         default:
            this.armorFlyFixGrimBadPackets1.set(false);
      }
   }

   private void agi(FireworkRocketEntity rocket) {
      this.PV = FireworkRocketEntityAccess.of(rocket).getLiveTicks();
      if (this.fireworkAutoUseVanilla.get()) {
         this.Pi.c();
      }
   }

   public static boolean afS() {
      return InventoryUtils.y(INSTANCE::afG, false, true) != null;
   }

   public void agp() {
      this.PY = -1;
   }

   public void agh(Event<Entity> entityRemoveEvent) {
      if (entityRemoveEvent.e() instanceof FireworkRocketEntity var3 && var3 == this.PT) {
         this.agi(this.PT);
      }
   }

   public boolean afP() {
      boolean var1;
      if (this.enableLiquidFly.get()) {
         var1 = !mc.player.isTouchingWater() || !this.liquidFlyOnlyFireworks.get() || this.agk(0);
      } else {
         var1 = !mc.player.isTouchingWater();
      }

      boolean var2;
      if (this.onGroundFlyOnlyFireworks.get()) {
         var2 = !PlayerStateManager.INSTANCE.jI || this.agk(0);
      } else {
         var2 = !mc.player.isOnGround();
      }

      return var1 && !mc.player.getAbilities().flying && var2 && !mc.player.hasVehicle() && !mc.player.hasStatusEffect(StatusEffects.LEVITATION);
   }

   public Vec3d agB(Vec3d currentMotion, float pitch, float yaw, boolean applyGravity) {
      if (!this.autoRescaleFireworkBox.get()) {
         return currentMotion;
      } else if (currentMotion.lengthSquared() < 1.0E-6) {
         return currentMotion;
      } else if (PlayerStateManager.INSTANCE.jz) {
         return currentMotion;
      } else {
         Vec3d var5 = EntityUtils.pitchYawToRotation(PlayerStateManager.INSTANCE.jl, PlayerStateManager.INSTANCE.jm);
         double var6 = 0.05;
         Vec3d var8 = EntityUtils.pitchYawToRotation(pitch, yaw);
         Vec3d var9 = var5.normalize();
         double var10 = Math.min(-var6, var8.getX()) + Math.min(-var6, var9.getX());
         double var12 = Math.min(-var6, var8.getY()) + Math.min(-var6, var9.getY());
         double var14 = Math.min(-var6, var8.getZ()) + Math.min(-var6, var9.getZ());
         double var16 = Math.max(var6, var8.getX()) + Math.max(var6, var9.getX());
         double var18 = Math.max(var6, var8.getY()) + Math.max(var6, var9.getY());
         double var20 = Math.max(var6, var8.getZ()) + Math.max(var6, var9.getZ());
         double var22 = Math.min(this.autoRescaleFireworkAmount.get(), currentMotion.length());
         var10 *= var22;
         var12 *= var22;
         var14 *= var22;
         var16 *= var22;
         var18 *= var22;
         var20 *= var22;
         var10 = Math.max(-var22, var10);
         var12 = Math.max(-var22, var12);
         var14 = Math.max(-var22, var14);
         var16 = Math.min(var22, var16);
         var18 = Math.min(var22, var18);
         var20 = Math.min(var22, var20);
         Box var24 = new Box(var10, var12, var14, var16, var18, var20);
         double var25 = EntityUtils.getEffectiveGravity(mc.player);
         Vec3d var27 = currentMotion.add(0.0, -var25, 0.0);
         double var28 = Math.abs(var27.y) < 1.0E-6 ? 0.0 : (var27.y < 0.0 ? (-var27.y - var25 + 0.02) / -var24.minY : (var27.y + var25 + 0.02) / var24.maxY);
         double var30 = Math.abs(var27.x) < 1.0E-6 ? 0.0 : (var27.x < 0.0 ? var27.x / var24.minX : var27.x / var24.maxX);
         double var32 = Math.abs(var27.z) < 1.0E-6 ? 0.0 : (var27.z < 0.0 ? var27.z / var24.minZ : var27.z / var24.maxZ);
         double var34 = Math.max(var28, Math.max(var30, var32));
         if (var34 < 1.0E-6) {
            return currentMotion;
         } else if (var34 > 1.0) {
            return currentMotion;
         } else {
            double var36 = 1.0 / var34;
            return currentMotion.multiply(var36);
         }
      }
   }

   public boolean afo() {
      if (checkNull()) {
         return false;
      } else if (afR()) {
         this.Py = true;
         return true;
      } else {
         return false;
      }
   }

   public Vec3d agy(Vec3d requested, Vec3d fallback, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      return this.agw(new Vec3d(agx(requested.x, fallback.x, minX, maxX), agx(requested.y, fallback.y, minY, maxY), agx(requested.z, fallback.z, minZ, maxZ)));
   }

   public void afN(int idx) {
      byte var2 = 6;
      if (mc.player.playerScreenHandler == ClientPlayerAccess.of(mc.player).getServerScreenHandler()) {
         InvExtra.INSTANCE.swapScreenSlots(var2, idx);
      }
   }
}
