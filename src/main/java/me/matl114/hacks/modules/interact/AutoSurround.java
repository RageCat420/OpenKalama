package me.matl114.hacks.modules.interact;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.InteractionTasks;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.modules.ac.Disabler;
import me.matl114.hacks.modules.combat.Attack;
import me.matl114.hacks.modules.combat.TargetSelector;
import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.hacks.modules.mine.MineSubHelperD;
import me.matl114.hacks.modules.mine.MineSubHelperH;
import me.matl114.hacks.modules.mine.MiningProgressManager;
import me.matl114.hacks.modules.mine.PacketMine;
import me.matl114.hacks.modules.move.PlayerInputManager;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.config.NBTTypes;
import me.matl114.hacks.utils.config.OptionalPrimitive;
import me.matl114.hacks.utils.entity.HackUtilHelperD;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.hooks.ViaFabricPlusHooks;
import me.matl114.managers.Configs;
import me.matl114.managers.Configs$LegalInteractMode;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.CollisionUtil;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.InteractUtils;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RaycastUtils;
import me.matl114.utils.collections.FlagEntry;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.entity.PlayerInputUtils;
import me.matl114.utils.entity.PlayerInputUtils$Input;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

public class AutoSurround extends BaseModule implements HackUtilHelperJ {
   public final FlagRef offhandEnable;
   boolean cW;
   public final FlagRef autoAttackCrystal;
   public final IntRef delay;
   int[] cR;
   BlockPos cU;
   public final NBTRef<EntrySet<Item>> whiteList;
   public final FlagRef onlyGround;
   Direction[] dd;
   public final FlagRef swingHand;
   BlockPos cX;
   boolean cQ;
   public final FlagRef onlyBlastResistance;
   int delayTicks;
   static HackUtilHelperD instance;
   public final FlagRef ae;
   public final FlagRef useWhiteList;
   public final EnumRef<Configs$LegalInteractMode> mode;
   boolean cV;
   public final IntRef multiply;
   public final FlagRef airPlace;
   public final NBTRef<OptionalPrimitive<Double>> onlyPlayerNear;
   public final FlagRef antiPacketMine;
   public final FlagRef upper;
   public final FlagRef autoCenter;
   int[] cS;
   public final ModulePath cz = makePath(Configs.n, "place-utils.auto-surround");
   public final KeyBindRef J;
   public final FlagRef autoSneak;

   public boolean checkSurround() {
      if (this.onlyGround.get() && !mc.player.isOnGround() && !CollisionUtil.isEntitySupported(mc.player, 1.5)) {
         return false;
      } else {
         if (this.onlyPlayerNear.get().isPresent()) {
            List var1 = TargetSelector.INSTANCE.akC(this.onlyPlayerNear.get().getValue());
            if (var1.isEmpty()) {
               return false;
            }
         }

         boolean var16 = this.mode.get().isLegal();
         int var2 = Disabler.INSTANCE.isMultiRotPlaceCheckDisabled(this.mode.get().canMultiRotPlace()) ? this.multiply.get() : 1;
         int var3 = 0;
         Runnable var4 = null;
         HashSet var5 = new HashSet();
         boolean var6 = this.offhandEnable.get();
         Set<BlockPos> var7 = this.getTargetingPos();
         Set var8 = Set.of();
         if (this.antiPacketMine.get()) {
            var8 = MiningProgressManager.INSTANCE
               .getBreakingMap()
               .values()
               .stream()
               .map(MineSubHelperH::f)
               .filter(Objects::nonNull)
               .collect(Collectors.toSet());
         }

         HashSet<BlockPos> var9 = new HashSet();

         for (BlockPos var11 : var7) {
            BlockState var12 = mc.world.getBlockState(var11);
            if (var12.isAir() || var12.isReplaceable()) {
               FlagEntry var13 = InteractionTasks.l(var11, this.airPlace.get(), !var16);
               if (var13 != null && var13.flag()) {
                  if (!this.cQ && this.autoSneak.get() && ViaFabricPlusHooks.isSupportInstaSneak() && !mc.player.isSneaking()) {
                     PlayerInputUtils.a(mc.player).rC(true).sendPlayerSneakUpdatePacket().applyInput(mc.player);
                  }

                  this.cQ = true;
               }

               boolean var14 = InteractUtils.C(mc.player, var13);
               if (var14) {
                  if (this.autoAttackCrystal.get()) {
                     mc.world.getOtherEntities(null, MathUtils.r(var11), e -> e instanceof EndCrystalEntity).forEach(endCrystalEntity -> {
                        if (endCrystalEntity instanceof EndCrystalEntity var2x && !Attack.INSTANCE.Yh(endCrystalEntity)) {
                           var5.add(var2x);
                        }
                     });
                  }

                  if (this.canCubePlace(mc.player, var11, var5)) {
                     if (var3 == 0) {
                        KalamaHelperHelperK var15 = this.gy();
                        if (var15 == null) {
                           break;
                        }

                        var2 = Math.min(var2, ((ItemStack)var15.val()).getCount());
                        var6 |= var15.index() == 40;
                        var4 = var6 ? InvExtra.INSTANCE.uh(var15.index()) : InvExtra.INSTANCE.swapInventoryIndexToHand(var15.index());
                     }

                     InteractionTasks.g(this.mode.get(), (BlockHitResult)var13.val(), var6 ? Hand.OFF_HAND : Hand.MAIN_HAND, this.swingHand.get());
                     if (++var3 >= var2) {
                        break;
                     }
                  }
               }
            } else if (this.antiPacketMine.get() && var8.contains(var11)) {
               var9.add(var11);
            }
         }

         if (var3 < var2) {
            for (BlockPos var18 : var9) {
               BlockHitResult var19 = RaycastUtils.g(var18, mc.player.getEyePos());
               if (var3 == 0) {
                  KalamaHelperHelperK var20 = this.gy();
                  if (var20 == null) {
                     break;
                  }

                  var2 = Math.min(var2, ((ItemStack)var20.val()).getCount());
                  var6 |= var20.index() == 40;
                  var4 = var6 ? InvExtra.INSTANCE.uh(var20.index()) : InvExtra.INSTANCE.swapInventoryIndexToHand(var20.index());
               }

               InteractionTasks.g(this.mode.get(), var19, var6 ? Hand.OFF_HAND : Hand.MAIN_HAND, this.swingHand.get());
               if (++var3 >= var2) {
                  break;
               }
            }
         }

         if (var4 != null) {
            var4.run();
         }

         return var3 > 0;
      }
   }

   @Override
   public void onDisableModule() {
      super.onDisableModule();
      this.cV = false;
   }

   @Override
   public int priority() {
      return 2147483646;
   }

   public AutoSurround() {
      super("AutoSurround");
      this.ae = this.flagBuilder(this.cz.addEnable()).build();
      this.J = this.toggleHotkey(this.cz.addHotkey(), new MultiKeyBind(), this.cz.addEnable()).build();
      this.offhandEnable = this.flagBuilder(this.cz.add("offhand-enable")).build();
      this.delay = this.builder(this.cz.add("delay"), IntRef.TYPE).defaultValue(1).validator(Configs.e).build();
      this.multiply = this.builder(this.cz.add("multiply"), IntRef.TYPE).defaultValue(1).validator(Configs.e).build();
      this.mode = this.builder(this.cz.add("mode"), Configs$LegalInteractMode.class).defaultValue(Configs$LegalInteractMode.DELAY_MOVEMENT).build();
      this.airPlace = this.flagBuilder(this.cz.add("air-place")).build();
      this.onlyPlayerNear = this.builder(this.cz.add("only-player-near"), OptionalPrimitive.DOUBLE_TYPE)
         .defaultValue(new OptionalPrimitive<>(false, NBTTypes.e, 10.0))
         .build();
      this.upper = this.flagBuilder(this.cz.add("upper")).build();
      this.autoAttackCrystal = this.flagBuilder(this.cz.add("auto-attack-crystal")).build();
      this.antiPacketMine = this.flagBuilder(this.cz.add("anti-packet-mine")).build();
      this.onlyGround = this.flagBuilder(this.cz.add("only-ground")).build();
      this.autoCenter = this.flagBuilder(this.cz.add("auto-center")).build();
      this.autoSneak = this.flagBuilder(this.cz.add("auto-sneak")).build();
      this.useWhiteList = this.flagBuilder(this.cz.add("use-white-list")).build();
      this.whiteList = this.builder(this.cz.add("white-list"), EntrySet.<Item>parameter()).defaultValue(new EntrySet<Item>(Registries.ITEM, List.of(Items.OBSIDIAN))).build();
      this.onlyBlastResistance = this.builder(this.cz.add("only-blast-resistance"), Boolean.class).defaultValue(true).build();
      this.swingHand = this.builder(this.cz.add("swing-hand"), Boolean.class).defaultValue(true).build();
      this.cQ = false;
      this.cR = new int[]{0, 0, -1, 1};
      this.cS = new int[]{-1, 1, 0, 0};
      this.dd = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.DOWN};
      this.cV = false;
      this.cW = false;
      if (instance == null) {
         instance = new HackUtilHelperD(this::cast);
         MovTasks.j.SJ(() -> instance);
      }

      instance.mN(this::cast);
      this.bindFlag(this.ae);
   }

   public void applyAfterInputTick(Event<LegalMovementManager> movementManagerEvent) {
      if (this.cV && mc.player.isOnGround() && this.cW) {
         this.cW = false;
         PlayerInputUtils$Input var2 = PlayerInputUtils.a(mc.player);
         if (!var2.ru()) {
            var2.rx(true).applyInput(mc.player);
         }
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.bd(), this::onInput);
      this.registerListener(Listener.bx().c(ModulePreset.class), this::gA);
      this.registerListener(PacketMine.aW(), this::gu);
   }

   public KalamaHelperHelperK<ItemStack> gy() {
      return InventoryUtils.v(
         item -> {
            if (item.getItem() instanceof BlockItem var3) {
               if (this.useWhiteList.get() && !this.whiteList.get().test(var3)) {
                  return null;
               } else {
                  return this.onlyBlastResistance.get() && var3.getBlock().getBlastResistance() < 600.0F
                     ? null
                     : var3.getBlock().getBlastResistance()
                        + (var3 == Items.OBSIDIAN ? 1.0E8 : 0.0)
                        + (var3.getBlock() instanceof BlockWithEntity ? -1.0E8 : 0.0);
               }
            } else {
               return null;
            }
         },
         true,
         false
      );
   }

   public void gA(Event<KalamaHelperHelperI<ModulePreset>> event) {
      this.mode.set(Configs$LegalInteractMode.getFromPreset((ModulePreset)((KalamaHelperHelperI)event.b).b()));
      this.airPlace.set(!((ModulePreset)((KalamaHelperHelperI)event.b).b()).hasAC());
   }

   public void applyPreTickModify(Event<LegalMovementManager> movementManagerEvent) {
      if (this.cV && mc.player.isOnGround()) {
         if (this.cX == null || !MathUtils.m(this.cX.toCenterPos(), mc.player.getPos(), 1.0)) {
            this.cX = this.cU == null ? PlayerStateManager.INSTANCE.jL : this.cU;
         }

         BlockPos var2 = this.cX;
         boolean var3 = mc.player.getX() - var2.getX() - 0.5 <= 0.2
            && mc.player.getX() - var2.getX() - 0.5 >= -0.2
            && mc.player.getZ() - var2.getZ() - 0.5 <= 0.2
            && mc.player.getZ() - 0.5 - var2.getZ() >= -0.2;
         if (!var3) {
            PlayerInputUtils$Input var4 = PlayerInputUtils.of(mc.options);
            if (!var4.rt() && !((LegalMovementManager)movementManagerEvent.b).d()) {
               this.cW = true;
               Vec3d var5 = var2.toCenterPos().subtract(mc.player.getPos());
               PlayerStateManager.nT(mc.player, EntityUtils.s(var5.normalize()));
               ((LegalMovementManager)movementManagerEvent.b).c();
            }
         } else {
            this.cV = false;
            this.cX = null;
         }
      }
   }

   @Override
   public void onEnableModule() {
      super.onEnableModule();
      this.cV = this.autoCenter.get() && mc.player != null && mc.player.getPose() != EntityPose.SWIMMING;
   }

   public void gu(Event<MineSubHelperD> eventPre) {
      if (this.ae.get() && !eventPre.d()) {
         BlockPos var2 = eventPre.getArgs(0);
         if (this.getTargetingPos().contains(var2)) {
            eventPre.cancel();
         }
      }
   }

   @Override
   public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
      return true;
   }

   public void onInput(Event<Void> inputEvent) {
      if (this.ae.get()) {
         boolean var2 = mc.player.isSneaking();
         if (++this.delayTicks >= this.delay.get()) {
            if (this.checkSurround()) {
               if (this.autoCenter.get() && mc.player.getPose() != EntityPose.SWIMMING) {
                  this.cV = true;
               }

               this.delayTicks = 0;
            } else {
               this.cV = false;
            }
         }

         if (this.cQ) {
            this.cQ = false;
            if (this.autoSneak.get()) {
               if (ViaFabricPlusHooks.isSupportInstaSneak()) {
                  if (mc.player.isSneaking() != var2) {
                     PlayerInputUtils.a(mc.player).rC(var2).sendPlayerSneakUpdatePacket().applyInput(mc.player);
                  }
               } else {
                  PlayerInputManager.INSTANCE.ZZ(0, true, Math.max(this.delay.get() - 1, 0), 1);
               }
            }
         }
      }
   }

   public Set<BlockPos> getTargetingPos() {
      BlockPos var1 = PlayerStateManager.INSTANCE.jL;
      this.cU = var1;
      Box var2 = mc.player.getBoundingBox();
      var2 = var2.withMaxY(Math.max(var2.minY + InteractExtra.INSTANCE.getPotentialEyeHeights().max().orElse(0.0), var2.maxY));
      LinkedHashSet<BlockPos> var3 = new LinkedHashSet<>(MathUtils.getOccupiedBlockPositions(var2));
      LinkedHashSet<BlockPos> var4 = new LinkedHashSet();

      for (BlockPos var6 : var3) {
         var4.add(new BlockPos(var6.getX(), var1.getY(), var6.getZ()));
      }

      int var17 = (int)var2.minY - 1;
      int var18 = (int)var2.maxY + 1;
      LinkedHashSet var7 = new LinkedHashSet();

      for (int var8 = 0; var8 < 4 + (this.upper.get() ? 1 : 0); var8++) {
         Direction var9 = this.dd[var8];
         LinkedHashSet<BlockPos> var10 = new LinkedHashSet();

         for (BlockPos var12 : var4) {
            BlockPos var13 = var12.offset(var9);
            if (!var4.contains(var13)) {
               var10.add(var13);
            }
         }

         int var19 = var9 == Direction.DOWN ? var18 - 2 : var18;

         for (BlockPos var21 : var10) {
            for (int var14 = var17; var14 <= var19; var14++) {
               BlockPos var15 = var21.withY(var14);
               if (!var3.contains(var15)) {
                  var7.add(var15);
               }
            }
         }
      }

      return var7;
   }

   private boolean canCubePlace(ClientPlayerEntity player, BlockPos pos, Set<EndCrystalEntity> pendingRemove) {
      BlockState var4 = Blocks.OBSIDIAN.getDefaultState();
      VoxelShape var5 = var4.getCollisionShape(mc.world, pos, ShapeContext.of(mc.player)).offset(pos.getX(), pos.getY(), pos.getZ());
      return !CollisionUtil.hasAnyIntersects(mc.world, entity -> entity instanceof EndCrystalEntity var2 && pendingRemove.contains(var2), var5);
   }
}
