package me.matl114.hacks.modules.combat;

import com.google.common.base.Preconditions;
import com.google.common.collect.Streams;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.events.annotations.Modifiable;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.ACTasks;
import me.matl114.hacks.CombatTasks;
import me.matl114.hacks.KalamaHelperHelperIX;
import me.matl114.hacks.KalamaHelperHelperV;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.MovTasks$MovInfo;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.hacks.modules.move.ElytraExtra;
import me.matl114.hacks.modules.move.LegacySnapRotManager;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.config.NBTTypes;
import me.matl114.hacks.utils.config.OptionalPrimitive;
import me.matl114.hacks.utils.config.WrapColor;
import me.matl114.managers.Configs;
import me.matl114.managers.Configs$LegalTargetingMode;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.ConfigEnum;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ColorUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.KalamaHelperHelperB;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RaycastUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.entity.PlayerInputUtils;
import me.matl114.utils.entity.PlayerInputUtils$Input;
import me.matl114.versioned.api.VItem;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MaceItem;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus.Experimental;

public class Attack extends BaseModule {
   public boolean delayAttacking;
   public final FlagRef swingHand;
   public final FlagRef attackSelectBestWeapon;
   public final FlagRef attackPostFix;
   @Experimental
   public final NBTRef<OptionalPrimitive<Double>> maceSwap;
   private static int shieldExceptionspam = 0;
   public static Attack INSTANCE;
   public final FlagRef useDelayMovementPosPredict;
   public final NBTRef<OptionalPrimitive<Double>> maceHeightMultiply;
   public final NBTRef<WrapColor> renderTargetColor;
   public final FlagRef autoAntiShield;
   public final FlagRef exactTp;
   public final FlagRef attackInvSwap;
   public final Random DO;
   public final FlagRef alwaysAtt;
   public final FlagRef autoHandleUseWhenAttack;
   private Entity JG;
   public final FlagRef renderTarget;
   public final EnumRef<Configs$LegalTargetingMode> legalTargeting;
   public final NBTRef<OptionalPrimitive<Double>> tpReach;
   private int lastTick;
   public final ModulePath Ju = makePath(Configs.k, "att-bot");
   public final KeyBindRef alwaysAttHotkey;

   private static KalamaHelperHelperK<ItemStack> Yb() {
      return InventoryUtils.p(ex -> VItem.w().g(ex), false, false);
   }

   private static boolean Ya(LivingEntity target, PlayerEntity player) {
      return true;
   }

   @Modifiable
   public static void Yf(PlayerEntity player, Entity target, boolean criticSprint) {
      attackWithCritic(player, target, criticSprint, true);
   }

   public boolean Yj() {
      return this.willUseMaceAttack(this.maceSwap.get().isPresent() && this.maceSwap.get().getValue() <= PlayerStateManager.INSTANCE.jh);
   }

   private boolean processLegacySnapAttack(Entity target, CombatSubHelperPX settings) {
      boolean var3 = mc.player.isSprinting();
      double var4 = CombatTasks.j().getAttackAtTargetRange(target);
      boolean var6 = RaycastUtils.x(mc.player, PlayerStateManager.INSTANCE.jl, PlayerStateManager.INSTANCE.jm, target, var4);
      if (settings.yV() && var6) {
         Ye(mc.player, target, settings);
         return false;
      } else {
         Vec3d var7 = mc.player.getPos();
         Vec3d var8 = TargetSelector.INSTANCE.akn(var7, target.getBoundingBox());
         boolean var9 = TargetSelector.INSTANCE.akm(var7, mc.player.getBoundingBox(), var4);
         if (this.tpReach.get().positive() && !var9) {
            Vec3d var10 = MovTasks.tpAttackSearch(var7, target.getBoundingBox(), var4, 9.9, 1).stream().findFirst().orElse(null);
            if (var10 != null && var10.squaredDistanceTo(var7) > 1.0E-7) {
               mc.player.setPosition(var10.add(0.0, 9.0E-8, 0.0));
               var8 = TargetSelector.INSTANCE.akn(var7, target.getBoundingBox());
            }

            if (!TargetSelector.INSTANCE.akm(var7, mc.player.getBoundingBox(), var4)) {
               var9 = false;
               mc.player.setPosition(var7);
            }
         }

         if (var9) {
            boolean var17 = false;
            if (settings.maceSwap()) {
               var17 = true;
               mc.player.stopUsingItem();
               mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
            }

            Vec3d var11 = target.getEyePos();
            Vec3d var12 = target.getPos();
            double var13 = this.DO.nextDouble(0.8, 1.0);
            Vec3d var15 = var12.add(var11.subtract(var12).multiply(var13));
            var15.add(this.DO.nextDouble(-0.05, 0.05), this.DO.nextDouble(-0.05, 0.05), this.DO.nextDouble(-0.05, 0.05));
            Vec3d var16 = var15.subtract(var8).normalize();
            var16 = this.fixRayCastBigBox(var8, target.getBoundingBox(), var16, var4);
            LegacySnapRotManager.INSTANCE.ahs(var16, false);
            Ye(mc.player, target, settings);
         }

         mc.player.setSprinting(var3);
         return false;
      }
   }

   public boolean Yh(Entity target) {
      return this.Yi(target, this.createAttackSettings());
   }

   @Modifiable
   public static void attackWithCritic(PlayerEntity player, Entity target, boolean criticSprint, boolean swing) {
      mc.interactionManager.attackEntity(mc.player, target);
      if (swing) {
         mc.player.swingHand(Hand.MAIN_HAND);
      }
   }

   public static void Ye(PlayerEntity player, Entity target, CombatSubHelperPX attackSettings) {
      PlayerInputUtils$Input var3 = null;
      if (player.hasVehicle()) {
         var3 = PlayerInputUtils.a(mc.player);
         if (var3.ru()) {
            PlayerInputUtils$Input var4 = var3.rw().rx(false).ry(false).rz(false).rA(false);
            var4.sendPlayerInputAsRiding();
         } else {
            var3 = null;
         }
      }

      Runnable var8 = null;
      KalamaHelperHelperK var5;
      if (attackSettings.antiShieldSwap() && shouldUseAntiShield(target) && (var5 = Yb()) != null) {
         var8 = InvExtra.INSTANCE.swapInventoryIndexToHand(var5.index());
      } else if (attackSettings.useTp()
         && !VItem.w().c(mc.player.getStackInHand(Hand.MAIN_HAND))
         && target instanceof LivingEntity var6
         && (var5 = InventoryUtils.v(ex -> {
            if (VItem.w().c(ex)) {
               Integer var3x = VItem.w().i(ex);
               return var3x == null ? null : -(var3x.intValue() * 1.0E8) + KalamaHelperHelperB.i(player, var6, ex) * KalamaHelperHelperB.e(player, ex);
            } else {
               return null;
            }
         }, false, false)) != null) {
         var8 = InvExtra.INSTANCE.swapInventoryIndexToHand(var5.index());
      } else if (attackSettings.invSwap()
         && target instanceof LivingEntity var7
         && (var5 = InventoryUtils.v(ex -> ex.getItem() == Items.MACE ? KalamaHelperHelperB.k(var7, ex) : null, false, false)) != null) {
         var8 = InvExtra.INSTANCE.swapInventoryIndexToHand(var5.index());
      } else if (attackSettings.invSwap()
         && !mc.player.getStackInHand(Hand.MAIN_HAND).isEmpty()
         && target instanceof LivingEntity
         && (
               var5 = InventoryUtils.v(
                  ex -> ex.isOf(mc.player.getStackInHand(Hand.MAIN_HAND).getItem())
                     ? KalamaHelperHelperB.i(player, target, ex) * KalamaHelperHelperB.e(player, ex)
                     : null,
                  false,
                  false
               )
            )
            != null) {
         var8 = InvExtra.INSTANCE.swapInventoryIndexToHand(var5.index());
      }

      attackWithCritic(player, target, attackSettings.maceSwap(), attackSettings.antiShieldSwap());
      if (var8 != null) {
         var8.run();
      }

      if (var3 != null) {
         var3.sendPlayerInputAsRiding();
      }
   }

   public List<Entity> XX() {
      return CombatTasks.l().akC(this.getTpSelectRange());
   }

   public boolean Yi(Entity target, CombatSubHelperPX settings) {
      return this.legalTargeting.get().isLegal() ? this.processLegalAttack(target, settings) : this.processIllegalAttack(target, settings);
   }

   private boolean processIllegalAttack(Entity target, CombatSubHelperPX settings) {
      ClientPlayerEntity var3 = mc.player;
      if (var3 == null) {
         return false;
      } else {
         double var4 = CombatTasks.j().getAttackRange();
         boolean var6 = RaycastUtils.w(mc.player, PlayerStateManager.INSTANCE.jl, PlayerStateManager.INSTANCE.jm, target);
         ArrayDeque var7 = new ArrayDeque();
         ArrayDeque var8 = new ArrayDeque();
         Vec3d var9 = mc.player.getPos();
         var7.addLast(MovTasks$MovInfo.adB(mc.player.getPos()));
         var8.addFirst(MovTasks$MovInfo.adB(mc.player.getPos()));
         boolean var10 = var6 || TargetSelector.INSTANCE.akm(var3.getPos(), target.getBoundingBox(), var4);
         boolean var11 = settings.selectWeapon() && this.exactTp.get() && (!var10 || CombatTasks.m().considerAntiShield(target));
         boolean var12 = false;
         boolean var13 = true;
         boolean var14 = false;
         boolean var15 = false;
         if (var13) {
            var14 = processVanillaAttack(var3, target, var7, var8, var6);
         }

         if (var13 && var11 && this.processExactAttack(var3, target, var7, var8, var14, settings)) {
            var15 = true;
         }

         if (var13 && !var15 && !var14) {
            var13 &= this.processCommonTpAttack(var3, target, var7, var8, var6, settings);
         }

         boolean var16 = false;
         if (var13 && this.processMaceAttack(var3, target, var7, var8, settings)) {
            var16 = true;
            int var17 = var11 ? 100 : 140;
            if (this.maceHeightMultiply.get().getValue() > var17) {
               Debug.b(Text.literal("[Attack Bot] 当前参数中,不建议将MaceHack范围设置在%d以上!".formatted(var17)));
            }
         }

         if (var13) {
            Iterator var31 = var7.iterator();
            Preconditions.checkArgument(var31.hasNext());
            Vec3d var18 = ((MovTasks$MovInfo)var31.next()).vec3d();
            KalamaHelperHelperIX var19 = KalamaHelperHelperIX.create(var18);
            ArrayList var20 = new ArrayList();
            var31.forEachRemaining(var20::add);
            var8.removeFirst();
            int var21 = var20.size();
            var20.addAll(var8);
            List var22 = MovTasks.createMovingPacketsForMovSequence(var19, var20, true, false);

            for (int var23 = 0; var23 < var21; var23++) {
               ((KalamaHelperHelperV)var22.get(var23)).run();
            }

            Ye(var3, target, settings);

            for (int var32 = var21; var32 < var22.size(); var32++) {
               if (!((KalamaHelperHelperV)var22.get(var32)).success) {
                  List var24 = var20.subList(var32, var20.size());
                  Tasks.l(() -> MovTasks.scheduleFarawayMoveInternal(var24, false, var19.mS(), true), 1);
                  break;
               }

               ((KalamaHelperHelperV)var22.get(var32)).run();
            }

            if ((settings.selectWeapon() || settings.useAttack()) && (!var8.isEmpty() || !var7.isEmpty())) {
               mc.player.setPosition(var9);
               MovTasks.Y();
            }

            List var33 = Streams.concat(new Stream[]{var7.stream(), var8.stream()}).toList();
            int var34 = var33.size();
            if (var34 > 1) {
               double var25 = -2.1474836E9F;
               double var27 = 2.147483647E9;

               for (int var29 = 0; var29 < var34 - 1; var29++) {
                  var25 = Math.max(var25, ((MovTasks$MovInfo)var33.get(var29)).vec3d().y);
                  var27 = Math.min(var27, ((MovTasks$MovInfo)var33.get(var29)).vec3d().y);
               }

               if (Math.abs(var25 - var27) > var3.getAttributeValue(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE) - 1.0) {
                  ClientPlayerAccess.of(var3).setForceNoFall(true);
                  var3.setOnGround(false);
               }
            }
         }

         return false;
      }
   }

   @Deprecated
   private static Vec3d Yw(Vec3d from, Box target) {
      return from;
   }

   public void onRenderTarget(Event<MatrixStack> stackE) {
      MatrixStack var2 = (MatrixStack)stackE.b;
      if (this.alwaysAtt.get() && mc.player != null && this.renderTarget.get()) {
         float var3 = (Float)stackE.c[0];
         if (mc.player.isUsingItem()) {
            if (mc.player.getActiveHand() == Hand.MAIN_HAND) {
               return;
            }

            if (mc.player.getActiveItem().getItem() instanceof RangedWeaponItem var12) {
               return;
            }
         }

         if (CombatTasks.notSuitableForAttack(mc.player.getMainHandStack())) {
            return;
         }

         if (this.lastTick != Tasks.b()) {
            this.lastTick = Tasks.b();
            this.JG = CombatTasks.l().searchAttackEntity(this.getTpSelectRange(), true);
         }

         if (!EntityUtils.isEntityValid(this.JG)) {
            this.JG = null;
            return;
         }

         RenderUtils.startDrawVirtual(var2);

         try {
            Entity var5 = this.JG;
            if (var5 != null) {
               float var11 = var5.distanceTo(mc.player);
               float var6 = Math.min(0.6F, 0.1F + var11 * 0.02F);
               Box var7 = RenderUtils.getLerpedBox(var5, var3);
               RenderUtils.r(var2, var7.getMinPos(), var7.getMaxPos(), ColorUtils.h(this.renderTargetColor.get().color(), var6));
            }
         } finally {
            RenderUtils.stopDrawVirtual(var2);
         }
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public int getModePredictTicks() {
      if (this.useDelayMovementPosPredict.get() && this.legalTargeting.get().isLegal()) {
         switch ((Configs$LegalTargetingMode)this.legalTargeting.get()) {
            case DELAY_MOVEMENT:
               if (mc.player.isFallFlying() && this.Yj() && ElytraExtra.INSTANCE.afA()) {
                  return 2;
               }

               return 1;
            case LEGACY_SLIENT_ROT:
               return 0;
            default:
               return 0;
         }
      } else {
         return 0;
      }
   }

   public static boolean passCriticalPredicate(PlayerEntity player) {
      boolean var1 = player.getAttackCooldownProgress(0.5F) > 0.9F
         && !player.isOnGround()
         && !player.isClimbing()
         && !player.isTouchingWater()
         && !player.hasStatusEffect(StatusEffects.BLINDNESS)
         && !player.hasVehicle();
      return var1 && !player.isSprinting();
   }

   private boolean processDelayMovementAttack(Entity target, CombatSubHelperPX settings) {
      ElytraExtra var3 = MovTasks.ax();
      double var4 = CombatExtra.INSTANCE.getAttackAtTargetRange(target);
      boolean var6 = var3.afA() && this.willUseMaceAttack(settings.invSwap());
      boolean var7 = ElytraBot.INSTANCE.DF(target, var6);
      Box var8 = var7 ? ElytraBot.INSTANCE.DI(target) : target.getBoundingBox();
      boolean var9 = RaycastUtils.x(mc.player, PlayerStateManager.INSTANCE.jl, PlayerStateManager.INSTANCE.jm, target, var4);
      if (settings.yV() && var9) {
         Ye(mc.player, target, settings);
         return false;
      } else {
         int var10 = -1;
         boolean var11 = var3.afr();
         if (var6 && !var11) {
            var10 = var3.afE();
            if (var10 != -1) {
               var3.afN(var10);
            }
         }

         if (settings.maceSwap()) {
            MovTasks.ao().Vj();
         }

         boolean var13 = false;
         this.delayAttacking = true;
         ClientPlayerAccess.of(mc.player)
            .getLegalMovementManager()
            .i(new CombatSubHelperZX(this, var6, var7, target, var4, settings, var8, var13, var11, var3, var10));
         return true;
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.bm(), this::onAttack, -999);
      this.registerListener(RenderListener.q(), this::onRenderTarget);
      this.registerListener(Listener.bx().c(ModulePreset.class), this::gA);
   }

   public static boolean shouldUseAntiShield(Entity target) {
      return target instanceof LivingEntity var1
         && var1.isUsingItem()
         && var1.getActiveItem().getItem() instanceof ShieldItem var3
         && Ya(var1, mc.player)
         && Yb() != null;
   }

   public void applyPostAttack(Entity target, CombatSubHelperPX settings) {
      if (this.attackPostFix.get()) {
         ACTasks.c(ch -> Ye(mc.player, target, settings));
      } else {
         Ye(mc.player, target, settings);
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void gA(Event<KalamaHelperHelperI<ModulePreset>> event) {
      ModulePreset var2 = (ModulePreset)((KalamaHelperHelperI)event.e()).b();
      this.legalTargeting.set(Configs$LegalTargetingMode.getFromPreset(var2));
      switch (var2) {
         case fd:
         case fe:
            if (this.tpReach.get().getValue() < 0.0) {
               this.tpReach.set(this.tpReach.get().withValue(-this.tpReach.get().getValue()));
            }
            break;
         default:
            if (this.tpReach.get().getValue() > 0.0) {
               this.tpReach.set(this.tpReach.get().withValue(-this.tpReach.get().getValue()));
            }
      }
   }

   public double getTpSelectRange() {
      return CombatTasks.j().getAttackRange() + (this.Sd() ? Math.max(0.0, this.tpReach.get().getValue()) : 0.0);
   }

   public boolean willUseMaceAttack(boolean autoMace) {
      return mc.player.getMainHandStack().getItem() instanceof MaceItem var3
         || autoMace && InventoryUtils.p(ex -> ex.getItem() == Items.MACE, false, false) != null;
   }

   public Attack() {
      super("Attack");
      this.alwaysAtt = this.flagBuilder(this.Ju.add("always-att")).build();
      this.alwaysAttHotkey = this.toggleHotkey(this.Ju.add("always-att-hotkey"), new MultiKeyBind(), this.Ju.add("always-att")).build();
      this.legalTargeting = this.builder(this.Ju.add("legal-targeting"), Configs$LegalTargetingMode.class)
         .defaultValue(Configs$LegalTargetingMode.DELAY_MOVEMENT)
         .build();
      this.tpReach = this.builder(this.Ju.add("tp-reach"), OptionalPrimitive.DOUBLE_TYPE).defaultValue(new OptionalPrimitive<>(true, NBTTypes.e, 0.0)).build();
      this.maceHeightMultiply = this.builder(this.Ju.add("mace-height-multiply"), OptionalPrimitive.DOUBLE_TYPE)
         .defaultValue(new OptionalPrimitive<>(false, NBTTypes.e, 30.0))
         .show(() -> !this.legalTargeting.get().isLegal())
         .build();
      this.exactTp = this.flagBuilder(this.Ju.add("exact-tp")).show(() -> !this.legalTargeting.get().isLegal() && this.tpReach.get().isPresent()).build();
      this.useDelayMovementPosPredict = this.flagBuilder(this.Ju.add("use-delay-movement-pos-predict"))
         .show(() -> !this.legalTargeting.get().isLegal() && this.legalTargeting.get().isIn(new ConfigEnum[]{Configs$LegalTargetingMode.DELAY_MOVEMENT}))
         .build();
      this.attackPostFix = this.builder(this.Ju.add("attack-post-fix"), Boolean.class)
         .defaultValue(true)
         .show(() -> !this.legalTargeting.get().isLegal() && this.legalTargeting.get().isIn(new ConfigEnum[]{Configs$LegalTargetingMode.DELAY_MOVEMENT}))
         .build();
      this.autoAntiShield = this.flagBuilder(this.Ju.add("auto-anti-shield")).build();
      this.attackInvSwap = this.flagBuilder(this.Ju.add("attack-inv-swap")).build();
      this.attackSelectBestWeapon = this.flagBuilder(this.Ju.add("attack-select-best-weapon")).build();
      this.autoHandleUseWhenAttack = this.flagBuilder(this.Ju.add("auto-handle-use-when-attack")).build();
      this.maceSwap = this.builder(this.Ju.add("mace-swap"), OptionalPrimitive.DOUBLE_TYPE)
         .defaultValue(new OptionalPrimitive<>(false, NBTTypes.e, 6.0))
         .build();
      this.swingHand = this.builder(this.Ju.add("swing-hand"), Boolean.class).defaultValue(true).build();
      this.renderTarget = this.flagBuilder(this.Ju.add("render-target")).build();
      this.renderTargetColor = this.builder(this.Ju.add("render-target-color"), WrapColor.class).defaultValue(new WrapColor(Formatting.GREEN)).build();
      this.DO = new Random();
      this.delayAttacking = false;
      this.bindFlag(this.alwaysAtt);
      INSTANCE = this;
   }

   public void onAttack(Event<HitResult> hitResult) {
      if (!hitResult.d()) {
         if (this.alwaysAtt.get()) {
            ClientPlayerEntity var2 = mc.player;
            if (var2 != null && mc.world != null) {
               if (this.tryAttack(false)) {
                  mc.attackCooldown = 1;
                  hitResult.cancel();
               } else if (((HitResult)hitResult.e()).getType() == Type.ENTITY) {
                  mc.attackCooldown = 0;
                  hitResult.cancel();
               }
            }
         }
      }
   }

   private boolean processMaceAttack(
      PlayerEntity player, Entity target, Deque<MovTasks$MovInfo> movementStack, Deque<MovTasks$MovInfo> shouldMoveBackStack, CombatSubHelperPX attackSettings
   ) {
      if (attackSettings.useAttack() && this.willUseMaceAttack(attackSettings.invSwap())) {
         double var6 = this.maceHeightMultiply.get().getValue();
         player.setOnGround(false);
         Vec3d var8 = ((MovTasks$MovInfo)movementStack.peekLast()).vec3d();
         if (mc.world.getBlockState(BlockPos.ofFloored(var8)).getBlock() == Blocks.WATER) {
            Debug.b(Text.literal("[Attack Bot] 目标攻击位置位于水中,无法执行MaceAttack!"));
            return false;
         }

         double var9 = target.getY() - var8.y;
         double var11 = MovTasks.searchFirstNoCollisionSpaceYHeight(var8.add(0.0, var6, 0.0), 0.0, var6 - 2.0 - var9, false);
         double var13 = var6 + var11;
         double var15 = 0.0;
         if (var13 - var15 > 2.0) {
            Debug.b(Text.literal("[Attack Bot] Mace Attack Simulation: simulate height %.2f".formatted(var13)).formatted(Formatting.GREEN));
            movementStack.addLast(MovTasks$MovInfo.adB(var8.add(0.0, var13, 0.0)));
            movementStack.addLast(MovTasks$MovInfo.adB(var8.add(0.0, var15, 0.0)));
            if (Math.abs(var15) > 1.0E-7) {
               shouldMoveBackStack.addFirst(MovTasks$MovInfo.adB(var8.add(0.0, var15, 0.0)));
            }

            return true;
         }
      }

      return false;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private boolean processLegalAttack(Entity target, CombatSubHelperPX settings) {
      ClientPlayerEntity var3 = mc.player;
      if (var3 == null) {
         return false;
      } else {
         return switch ((Configs$LegalTargetingMode)this.legalTargeting.get()) {
            case DELAY_MOVEMENT -> this.processDelayMovementAttack(target, settings);
            case LEGACY_SLIENT_ROT -> this.processLegacySnapAttack(target, settings);
            case NONE -> {
               Ye(mc.player, target, settings);
               yield false;
            }
         };
      }
   }

   public boolean tryAttack(boolean auto) {
      if (mc.player == null) {
         return false;
      } else {
         Entity var2 = CombatTasks.l().akJ(this.getTpSelectRange(), auto, this.getModePredictTicks());
         return var2 != null ? this.Yi(var2, this.createAttackSettings()) : false;
      }
   }

   public boolean Sd() {
      return this.tpReach.get().positive();
   }

   public boolean XU() {
      return this.tpReach.get().positive();
   }

   public Vec3d fixRayCastBigBox(Vec3d usingEyePos, Box targetBox, Vec3d currentRayCast, double currentAttackRange) {
      Vec3d var6 = currentRayCast.normalize().multiply(currentAttackRange - 0.009178);
      Optional var7 = targetBox.raycast(usingEyePos, usingEyePos.add(var6));
      if (var7.isPresent()) {
         return currentRayCast;
      } else {
         Box var8 = targetBox.expand(-1.0E-7, -1.0E-7, -1.0E-7);
         Vec3d var9 = MathUtils.magnitudePoint(var8, usingEyePos);
         return var9.subtract(usingEyePos).normalize();
      }
   }

   private boolean processCommonTpAttack(
      PlayerEntity player,
      Entity target,
      Deque<MovTasks$MovInfo> movementStack,
      Deque<MovTasks$MovInfo> shouldMoveBackStack,
      boolean alreadyAtTarget,
      CombatSubHelperPX settings
   ) {
      Vec3d var7 = ((MovTasks$MovInfo)movementStack.peekLast()).vec3d();
      double var8 = CombatTasks.j().getAttackRange();
      if (alreadyAtTarget) {
         return true;
      } else if (settings.selectWeapon() && target.getBoundingBox().squaredMagnitude(var7.add(0.0, mc.player.getStandingEyeHeight(), 0.0)) > MathUtils.a(var8)) {
         List<Vec3d> var10 = MovTasks.tpAttackSearch(var7, target.getBoundingBox(), var8 - 0.25, 135.0, 1);
         if (!var10.isEmpty() && target.getBoundingBox().squaredMagnitude((Vec3d)var10.get(var10.size() - 1)) < MathUtils.a(var8)) {
            for (Vec3d var12 : var10) {
               movementStack.addLast(MovTasks$MovInfo.adA(var12));
               shouldMoveBackStack.addFirst(MovTasks$MovInfo.adA(var12));
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static boolean processVanillaAttack(
      PlayerEntity player, Entity target, Deque<MovTasks$MovInfo> movementStack, Deque<MovTasks$MovInfo> shouldMoveBackStack, boolean alreadAtTarget
   ) {
      Vec3d var5 = ((MovTasks$MovInfo)movementStack.peekLast()).vec3d();
      return alreadAtTarget
         ? true
         : target.getBoundingBox().squaredMagnitude(var5.add(0.0, mc.player.getStandingEyeHeight(), 0.0)) <= MathUtils.a(CombatTasks.j().getAttackRange());
   }

   public CombatSubHelperPX createAttackSettings() {
      boolean var1 = this.Sd();
      boolean var2 = this.maceSwap.get().isPresent() && PlayerStateManager.INSTANCE.jh >= this.maceSwap.get().getValue();
      boolean var3 = this.attackInvSwap.get();
      boolean var4 = this.attackSelectBestWeapon.get();
      boolean var5 = this.autoAntiShield.get();
      boolean var6 = this.autoHandleUseWhenAttack.get() && mc.player.isUsingItem();
      boolean var7 = ElytraExtra.INSTANCE.afz() && ElytraExtra.INSTANCE.afA() && this.willUseMaceAttack(var2);
      boolean var8 = this.legalTargeting.get().isLegal();
      boolean var9 = !var8 && mc.player.isSprinting();
      boolean var10 = this.XU() && !var8;
      if (var10 && this.maceSwap.get().isPresent() && this.maceHeightMultiply.get().getValue() >= this.maceSwap.get().getValue()) {
         var2 = true;
      }

      return new CombatSubHelperPX(var1, var2, var3, var4, var5, var6, var7, var9, var10, this.swingHand.get());
   }

   private boolean processExactAttack(
      PlayerEntity player,
      Entity target,
      Deque<MovTasks$MovInfo> movementStack,
      Deque<MovTasks$MovInfo> shouldMoveBackStack,
      boolean vanillaSuccess,
      CombatSubHelperPX settings
   ) {
      PositionPredict var7 = CombatTasks.m();
      if (vanillaSuccess && !var7.considerAntiShield(target)) {
         return true;
      } else if (!settings.selectWeapon()) {
         return false;
      } else {
         double var8 = this.getTpSelectRange();
         Vec3d var10 = player.getPos();
         Vec3d var11 = var7.getExactAttackPosition(target);
         if (var11 != null) {
            List var12 = MovTasks.z(var10, var11, false, 1.5 * var8, true);
            List var13 = MovTasks.z(var11, var10, false, 1.5 * var8, true);
            if ((var12.size() == 2 || var12.size() == 4) && (var13.size() == 2 || var13.size() == 4)) {
               if (var12.size() == 2) {
                  movementStack.addLast(MovTasks$MovInfo.adA((Vec3d)var12.get(1)));
               } else {
                  movementStack.addLast(MovTasks$MovInfo.adA((Vec3d)var12.get(1)));
                  movementStack.addLast(MovTasks$MovInfo.adA((Vec3d)var12.get(2)));
                  movementStack.addLast(MovTasks$MovInfo.adA((Vec3d)var12.get(3)));
                  Vec3d var14 = (Vec3d)var12.get(3);
               }

               int var15 = var13.size();

               for (int var16 = var15 - 2; var16 >= 0; var16--) {
                  shouldMoveBackStack.addFirst(MovTasks$MovInfo.adz((Vec3d)var13.get(var16)));
               }

               return true;
            }

            Debug.b("[Attack Bot] Exact Attack failed, fall back to common mode");
         }

         return vanillaSuccess;
      }
   }
}
