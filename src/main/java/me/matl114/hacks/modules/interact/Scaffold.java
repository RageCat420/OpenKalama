package me.matl114.hacks.modules.interact;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import me.matl114.accessors.moonrise.MoonriseBlockStateBaseAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.InteractionTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.managers.Configs;
import me.matl114.managers.Configs$LegalInteractMode;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.InteractUtils;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.collections.FlagEntry;
import me.matl114.utils.collections.KalamaHelperHelperK;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.EmptyBlockView;

public class Scaffold extends BaseModule {
   public final KeyBindRef el;
   public final FlagRef swapHand;
   public final IntRef delay;
   int delayTick;
   public final IntRef expandInteractRange;
   public final FlagRef ae;
   public final IntRef expandInteractYDepth;
   List<Vec3i> xb;
   public final FlagRef offhandEnable;
   public final FlagRef swingHand;
   private Set<Item> xh;
   public final EnumRef<Configs$LegalInteractMode> legalTargeting;
   public final FlagRef airPlace;
   final ModulePath xc = makePath(Configs.n, "interact-scaffold");

   public BlockHitResult guessTheBestPlacePositionForTargetingBlock(Vec3d predictedEyePos, BlockPos pos) {
      if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == Type.BLOCK) {
         BlockHitResult var3 = (BlockHitResult)mc.crosshairTarget;
         BlockPos var4 = var3.getBlockPos();
         Direction var5 = var3.getSide();
         BlockPos var6 = var4.offset(var5);
         if (InteractUtils.canCubePlace(mc.player, var6) && Objects.equals(var6, pos)) {
            return var3;
         }
      }

      BlockState var10 = mc.world.getBlockState(pos);
      if (var10.isReplaceable() && InteractUtils.canCubePlace(mc.player, pos)) {
         FlagEntry var8 = InteractionTasks.n(predictedEyePos, pos, this.airPlace.get(), !this.legalTargeting.get().isLegal());
         if (var8 != null && InteractUtils.C(mc.player, var8)) {
            return (BlockHitResult)var8.val();
         }
      }

      for (Vec3i var13 : this.xb) {
         if (var13.getY() >= -this.expandInteractYDepth.get()) {
            BlockPos var7 = pos.add(var13);
            var10 = mc.world.getBlockState(var7);
            if (var10.isReplaceable() && InteractUtils.canCubePlace(mc.player, var7)) {
               FlagEntry var9 = InteractionTasks.l(var7, !this.legalTargeting.get().isLegal(), !this.legalTargeting.get().isLegal());
               if (var9 != null) {
                  return (BlockHitResult)var9.val();
               }
            }
         }
      }

      return null;
   }

   public Scaffold() {
      super("Scaffold");
      this.ae = this.flagBuilder(this.xc.addEnable()).build();
      this.el = this.toggleHotkey(this.xc.addHotkey(), new MultiKeyBind(), this.xc.addEnable()).build();
      this.legalTargeting = this.builder(this.xc.add("legal-targeting"), Configs$LegalInteractMode.class)
         .defaultValue(Configs$LegalInteractMode.USEITEM_PACKET)
         .build();
      this.airPlace = this.flagBuilder(this.xc.add("air-place")).build();
      this.delay = this.intBuilder(this.xc.add("delay")).defaultValue(1).build();
      this.offhandEnable = this.flagBuilder(this.xc.add("offhand-enable")).build();
      this.swapHand = this.flagBuilder(this.xc.add("swap-hand")).build();
      this.expandInteractYDepth = this.builder(this.xc.add("expand-interact-y-depth"), IntRef.TYPE).defaultValue(0).validator(Configs.intRange(0, 3)).build();
      this.expandInteractRange = this.builder(this.xc.add("expand-interact-range"), IntRef.TYPE)
         .defaultValue(1)
         .updateListener(this::updateSearchRange)
         .validator(Configs.intRange(0, 3))
         .build();
      this.swingHand = this.builder(this.xc.add("swing-hand"), Boolean.class).defaultValue(true).build();
      this.delayTick = 0;
      this.bindFlag(this.ae);
   }

   public int supplyBlock() {
      if (this.xh == null) {
         this.xh = new HashSet<>();

         for (Item var2 : Registries.ITEM) {
            if (var2 instanceof BlockItem var3
               && !var3.getBlock().getDefaultState().isAir()
               && var3.getBlock().getDefaultState().isFullCube(EmptyBlockView.INSTANCE, BlockPos.ORIGIN)) {
               this.xh.add(var3);
            }
         }
      }

      KalamaHelperHelperK var4 = InventoryUtils.r(item -> this.xh.contains(item.getItem()), true, false, true, true);
      return var4 == null ? -1 : var4.index();
   }

   public void onRightClick(Event<Void> rightClickEvent) {
      if (mc.player != null && this.ae.get()) {
         if (++this.delayTick <= this.delay.get()) {
            return;
         }

         this.delayTick = 0;
         int var2 = this.supplyBlock();
         if (var2 < 0) {
            return;
         }

         Vec3d var3 = mc.player.getPos();
         var3 = new Vec3d(var3.x, mc.player.getY(), var3.z);
         BlockPos var4 = BlockPos.ofFloored(var3.subtract(0.0, 0.500001F, 0.0));
         BlockState var5 = mc.world.getBlockState(var4);
         if (!var5.isAir() && !MoonriseBlockStateBaseAccess.of(var5).isConstantCollisionShapeEmpty()) {
            return;
         }

         if (var5.isReplaceable()) {
            BlockHitResult var6 = this.guessTheBestPlacePositionForTargetingBlock(var3.add(0.0, mc.player.dimensions.eyeHeight(), 0.0), var4);
            if (var6 != null) {
               this.placeBlockLegally(var2, var6);
               return;
            }
         }
      }
   }

   private void placeBlockLegally(int hand, BlockHitResult result) {
      boolean var3 = this.offhandEnable.get() || hand == 40;
      Runnable var4 = var3
         ? InvExtra.INSTANCE.uh(hand)
         : (this.swapHand.get() ? InvExtra.INSTANCE.switchOrSwapInventoryIndexToHand(hand) : InvExtra.INSTANCE.swapInventoryIndexToHand(hand));
      if (var4 != null) {
         try {
            if (!(
               mc.crosshairTarget instanceof BlockHitResult var6
                  && Objects.equals(var6.getBlockPos(), result.getBlockPos())
                  && Objects.equals(var6.getSide(), result.getSide())
                  && Objects.equals(var6.getType(), result.getType())
            )) {
               if (this.legalTargeting.get().isLegal()) {
                  Configs$LegalInteractMode var10 = this.legalTargeting.get();
                  InteractionTasks.g(var10, result, var3 ? Hand.OFF_HAND : Hand.MAIN_HAND, this.swingHand.get());
               } else {
                  InteractionTasks.b(var3 ? Hand.OFF_HAND : Hand.MAIN_HAND, result, this.swingHand.get());
               }

               return;
            }

            InteractionTasks.b(var3 ? Hand.OFF_HAND : Hand.MAIN_HAND, var6, this.swingHand.get());
         } finally {
            var4.run();
         }
      }
   }

   public void updateSearchRange(int range) {
      this.xb = new ArrayList<>();

      for (int var2 = -range; var2 <= range; var2++) {
         for (int var3 = -3; var3 <= 0; var3++) {
            for (int var4 = -range; var4 <= range; var4++) {
               if (var2 != 0 || var3 != 0 || var4 != 0) {
                  this.xb.add(new Vec3i(var2, var3, var4));
               }
            }
         }
      }

      this.xb.sort(Comparator.comparingInt(v -> Math.max(Math.max(Math.abs(v.getX()), Math.abs(v.getY())), Math.abs(v.getZ()))));
   }

   public void IU(Event<KalamaHelperHelperI<ModulePreset>> event) {
      this.legalTargeting.set(Configs$LegalInteractMode.getFromPreset((ModulePreset)((KalamaHelperHelperI)event.b).b()));
      this.airPlace.set(!((ModulePreset)((KalamaHelperHelperI)event.b).b()).hasAC());
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.bd(), this::onRightClick);
      this.registerListener(Listener.bx().c(ModulePreset.class), this::IU);
   }
}
