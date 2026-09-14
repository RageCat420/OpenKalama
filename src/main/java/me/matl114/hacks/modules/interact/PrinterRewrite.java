package me.matl114.hacks.modules.interact;

import com.mojang.datafixers.util.Pair;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.KalamaHelperHelperJ;
import me.matl114.hacks.InteractionTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.modules.ac.Disabler;
import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.hacks.modules.mine.QueueMine;
import me.matl114.hacks.modules.move.PlayerInputManager;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.config.WrapColor;
import me.matl114.hacks.utils.entity.EntityMovementStatus;
import me.matl114.hacks.utils.render.RenderCollectors;
import me.matl114.hooks.LitematicaHooks;
import me.matl114.managers.Configs;
import me.matl114.managers.Configs$BypassMode;
import me.matl114.managers.Configs$LegalInteractMode;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.InteractUtils;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RaycastUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.collections.FlagEntry;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.render.RenderCollector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.CakeBlock;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.FlowerbedBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.PumpkinBlock;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class PrinterRewrite extends BaseModule {
   final RenderCollector<Box> Du;
   public final FlagRef render;
   public final FlagRef swingHand;
   public final FlagRef supportWaterPlace;
   public final DoubleRef range;
   Map<BlockPos, Integer> Dw;
   public final ModulePath Ax = makePath(Configs.n, "block-rotate");
   public final FlagRef autoSneak;
   public final IntRef delay;
   public final NBTRef<WrapColor> renderFailColor;
   public final FlagRef useIceToFormWater;
   public final IntRef multiply;
   public final FlagRef supportReplaceBlock;
   int countDown;
   public final FlagRef enable;
   public List<Vec3i> Dp;
   boolean cQ;
   public final NBTRef<WrapColor> renderSuccessColor;
   public final EnumRef<Configs$LegalInteractMode> mode;
   public final FlagRef airPlace;
   boolean Dv;
   public final ModulePath Dl = this.Ax.add("litematica-printer-rewrite");
   public final KeyBindRef hotkey;

   public void RI(BlockPos pos) {
      this.Du.submit(MathUtils.r(pos), this.renderSuccessColor.get().withAlpha(255));
   }

   private boolean isDesyncStateInteractTransition(BlockState currentState, BlockState targetState) {
      if (currentState != null && targetState != null) {
         Block var3 = currentState.getBlock();
         Block var4 = targetState.getBlock();
         if (var3 instanceof NoteBlock) {
            return true;
         } else if (var3 instanceof LeverBlock) {
            return true;
         } else if (var3 instanceof ButtonBlock) {
            return true;
         } else if (var3 instanceof CandleBlock
            && var4 instanceof CandleBlock
            && (Boolean)currentState.get(CandleBlock.LIT)
            && targetState.equals(currentState.with(CandleBlock.LIT, false))) {
            return true;
         } else if (var3 instanceof CandleBlock
            && var4 instanceof CandleBlock
            && !(Boolean)currentState.get(CandleBlock.LIT)
            && !(Boolean)currentState.get(CandleBlock.WATERLOGGED)
            && targetState.equals(currentState.with(CandleBlock.LIT, true))) {
            return true;
         } else if (var3 instanceof CakeBlock && var4 instanceof CakeBlock) {
            return true;
         } else {
            return var3 instanceof CakeBlock && var4 instanceof CandleCakeBlock ? true : var3 instanceof CandleCakeBlock && var4 instanceof CakeBlock;
         }
      } else {
         return false;
      }
   }

   public void RR() {
   }

   public int supplyLiquid(Fluid fluid) {
      Item var2 = fluid == Fluids.WATER ? Items.WATER_BUCKET : (fluid == Fluids.LAVA ? Items.LAVA_BUCKET : null);
      if (var2 == null) {
         return -1;
      } else {
         KalamaHelperHelperK var3 = InventoryUtils.p(itemStack -> itemStack.getItem() == var2, true, false);
         return var3 == null ? -1 : var3.index();
      }
   }

   public boolean doMultiReplace(BlockPos pos, BlockState currentState, BlockState targetState) {
      Pair var4 = InteractUtils.getNextInteractionStep(currentState, targetState).stream().findAny().orElse(null);
      if (var4 != null) {
         BlockState var5 = (BlockState)var4.getFirst();
         Predicate var6 = (Predicate)var4.getSecond();
         KalamaHelperHelperK var7 = InventoryUtils.p(var6, true, true);
         if (var7 != null) {
            FlagEntry var8;
            if (var5.getBlock() == currentState.getBlock() && !var6.test(ItemStack.EMPTY)) {
               var8 = InteractionTasks.q(pos, targetState, this.airPlace.get(), !this.mode.get().isLegal());
            } else {
               var8 = new FlagEntry<>(false, RaycastUtils.g(pos, mc.player.getEyePos()));
            }

            if (InteractUtils.C(mc.player, var8)) {
               InvExtra.INSTANCE.swapInventoryIndexToHand(var7.index());
               InteractionTasks.f(this.mode.get(), (BlockHitResult)var8.val(), Hand.MAIN_HAND);
               this.RI(pos);
               if (this.isDesyncStateInteractTransition(currentState, targetState)) {
                  this.Dw.put(pos, Tasks.b() + 20);
               }

               return true;
            }

            this.RH(pos);
         } else {
            this.RH(pos);
         }
      }

      return false;
   }

   public boolean doPlace(BlockPos pos, BlockState targetState, boolean useAirPlace, boolean usePositionPlace) {
      Block var5 = targetState.getBlock();
      if (var5 instanceof CandleCakeBlock) {
         var5 = Blocks.CAKE;
      } else if (var5 instanceof FlowerPotBlock var6 && var6.getContent() != Blocks.AIR) {
         var5 = Blocks.FLOWER_POT;
      }

      int var19 = this.supplyBlocks(var5);
      if (var19 == -1) {
         this.RH(pos);
         return false;
      } else {
         FlagEntry var7 = InteractionTasks.r(mc.player.getFacing(), pos, targetState, useAirPlace, usePositionPlace);
         if (var7 != null
            && InteractExtra.INSTANCE.fE(mc.player.getPos(), ((BlockHitResult)var7.val()).getBlockPos(), this.range.get())
            && InteractUtils.getBlockPlacement(var5, mc.player, mc.world, (BlockHitResult)var7.val()) != null) {
            if (var7.flag()) {
               this.cQ = true;
               if (!InteractUtils.C(mc.player, var7)) {
                  this.RH(pos);
                  return false;
               }
            }

            FlagRef var8 = InteractionTasks.K().enable3;
            FlagRef var9 = InteractionTasks.K().legalLook;
            EnumRef var10 = InteractionTasks.K().rotateBypassMode;
            boolean var11 = var8.get();
            boolean var12 = var9.get();
            Configs$BypassMode var13 = (Configs$BypassMode)var10.get();
            if (!var11) {
               var8.set(true);
            }

            if (!var12) {
               var9.set(true);
            }

            var10.set(Configs$BypassMode.NO_BYPASS);

            boolean var15;
            try {
               Runnable var14 = InvExtra.INSTANCE.swapInventoryIndexToHand(var19);
               if (var14 != null) {
                  this.handlePlace((BlockHitResult)var7.val());
                  this.RI(pos);
                  return true;
               }

               this.RH(pos);
               var15 = false;
            } finally {
               if (!var11) {
                  var8.set(false);
               }

               if (!var12) {
                  var9.set(false);
               }

               var10.set(var13);
            }

            return var15;
         } else {
            this.RH(pos);
            return false;
         }
      }
   }

   public void onPreInputEvent(Event<Void> event) {
      if (++this.countDown > this.delay.get()) {
         this.countDown = 0;
         this.RE();
         this.Du.clear();
         if (this.enable.get() && LitematicaHooks.getInstance().isEnabled()) {
            World var2 = LitematicaHooks.getInstance().getSchematicWorld();
            BlockPos var3 = mc.player.getSteppingPos();
            BlockPos var4 = var3.add(0, 1, 0);
            int var5 = Disabler.INSTANCE.isMultiRotPlaceCheckDisabled(this.mode.get().canMultiRotPlace()) ? this.multiply.get() : 1;
            int var6 = 0;

            for (Vec3i var8 : this.Dp) {
               BlockPos var9 = var4.add(var8);
               if (LitematicaHooks.getInstance().isPositionWithinRange(var9)) {
                  BlockState var10 = var2.getBlockState(var9);
                  if (!var10.isAir()) {
                     BlockState var11 = mc.world.getBlockState(var9);
                     if (this.supportReplaceBlock.get()
                        && var11 != var10
                        && !var11.isAir()
                        && !var11.isLiquid()
                        && !var11.isReplaceable()
                        && !this.Dw.containsKey(var9)
                        && this.RL(var11, var10)
                        && this.doMultiReplace(var9, var11, var10)) {
                        if (++var6 >= var5) {
                           break;
                        }
                     }

                     if (this.supportWaterPlace.get()
                        && var11 != var10
                        && (
                           var11.isAir() && (var10.isLiquid() || var10.getFluidState().getFluid() == Fluids.WATER)
                              || var11.getBlock() == var10.getBlock() && var11.getFluidState() != var10.getFluidState()
                        )
                        && !this.Dw.containsKey(var9)) {
                        FluidState var12 = var11.getFluidState();
                        FluidState var13 = var10.getFluidState();
                        if ((var12.getFluid() == Fluids.EMPTY || var12.getFluid() == Fluids.FLOWING_WATER || var12.getFluid() == Fluids.FLOWING_LAVA)
                           && (var13.getFluid() == Fluids.LAVA || var13.getFluid() == Fluids.WATER)
                           && this.doLiquidPlace(var9, var10)) {
                           if (++var6 >= var5) {
                              break;
                           }
                           continue;
                        }
                     }

                     if (!var10.isLiquid()
                        && (var11.isAir() || var11.isLiquid() || var11.isReplaceable())
                        && var11 != var10
                        && this.doPlace(var9, var10, this.airPlace.get(), !this.mode.get().isLegal())) {
                        if (++var6 >= var5) {
                           break;
                        }
                     }
                  }
               }
            }
         }

         if (this.cQ) {
            this.cQ = false;
            if (this.autoSneak.get()) {
               PlayerInputManager.INSTANCE.ZZ(0, true, Math.max(this.delay.get() - 1, 0), 2);
            }
         }

         if (this.Dv) {
            this.RR();
         }
      }
   }

   private BlockHitResult createStateInteractHitResult(BlockPos pos) {
      Vec2f var2 = EntityUtils.q(pos.toCenterPos().subtract(mc.player.getEyePos()).normalize());
      Direction var3 = EntityUtils.pitchYawToDirection(var2).getOpposite();
      return new BlockHitResult(pos.toCenterPos(), var3, pos, false);
   }

   @Override
   public void addCustomWidgets(Consumer<DrawableWidget> acceptor, int dx, int dy, int dblank) {
      super.addCustomWidgets(acceptor, dx, dy, dblank);
      DrawableWidget var5 = this.createTitleLabel("widget.queue-mine.mine.use-argument", 0, dblank, dx, dy);
      acceptor.accept(new KalamaHelperHelperJ<>(() -> this.supportWaterPlace.get() && this.useIceToFormWater.get() ? var5 : null, 0, 0));
   }

   public void handlePlace(BlockHitResult result) {
      if (!this.mode.get().isLegal()) {
         InteractionTasks.b(Hand.MAIN_HAND, result, this.swingHand.get());
      } else {
         InteractionTasks.g(this.mode.get(), result, Hand.MAIN_HAND, this.swingHand.get());
      }
   }

   public void IU(Event<KalamaHelperHelperI<ModulePreset>> event) {
      this.mode.set(Configs$LegalInteractMode.getFromPreset((ModulePreset)((KalamaHelperHelperI)event.b).b()));
      this.airPlace.set(!((ModulePreset)((KalamaHelperHelperI)event.b).b()).hasAC());
   }

   public void RE() {
      if (!this.Dw.isEmpty()) {
         int var1 = Tasks.b();
         Iterator var2 = this.Dw.entrySet().iterator();

         while (var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            if ((Integer)var3.getValue() < var1) {
               var2.remove();
            }
         }
      }
   }

   public void RH(BlockPos pos) {
      this.Du.submit(MathUtils.r(pos), this.renderFailColor.get().withAlpha(255));
   }

   public PrinterRewrite() {
      super("Printer");
      this.enable = this.flagBuilder(this.Dl.add("enable")).build();
      this.hotkey = this.toggleHotkey(this.Dl.add("hotkey"), new MultiKeyBind(), this.Dl.add("enable")).build();
      this.mode = this.builder(this.Dl.add("mode"), Configs$LegalInteractMode.class).defaultValue(Configs$LegalInteractMode.DELAY_MOVEMENT).build();
      this.airPlace = this.flagBuilder(this.Dl.add("air-place")).build();
      this.delay = this.builder(this.Dl.add("delay"), IntRef.TYPE).defaultValue(5).validator(Configs.e).build();
      this.multiply = this.builder(this.Dl.add("multiply"), IntRef.TYPE).defaultValue(1).validator(Configs.e).build();
      this.autoSneak = this.builder(this.Dl.add("auto-sneak"), Boolean.class).defaultValue(true).build();
      this.supportWaterPlace = this.builder(this.Dl.add("support-water-place"), Boolean.class).defaultValue(true).build();
      this.useIceToFormWater = this.flagBuilder(this.Dl.add("use-ice-to-form-water")).show(this.supportWaterPlace::get).build();
      this.supportReplaceBlock = this.builder(this.Dl.add("support-replace-block"), Boolean.class).defaultValue(true).build();
      this.swingHand = this.builder(this.Dl.add("swing-hand"), Boolean.class).defaultValue(true).build();
      this.Dp = new ArrayList<>();
      this.range = this.builder(this.Dl.add("range"), DoubleRef.TYPE)
         .defaultValue(5.0)
         .validator(Configs.doubleRange(0.0, 100.0))
         .updateListener(this::updateBlocks)
         .build();
      this.render = this.builder(this.Dl.add("render"), Boolean.class).defaultValue(true).build();
      this.renderSuccessColor = this.builder(this.Dl.add("render-success-color"), WrapColor.class).defaultValue(new WrapColor(Color.GREEN)).build();
      this.renderFailColor = this.builder(this.Dl.add("render-fail-color"), WrapColor.class).defaultValue(new WrapColor(Color.RED)).build();
      this.Du = RenderCollectors.createBoxCollector(true, false, false);
      this.cQ = false;
      this.Dv = false;
      this.Dw = new HashMap<>();
      this.bindFlag(this.enable);
   }

   public void B(Event<MatrixStack> event) {
      MatrixStack var2 = (MatrixStack)event.e();
      if (this.enable.get() && this.render.get()) {
         RenderUtils.startDrawVirtual(var2);

         try {
            this.Du.a(var2);
         } finally {
            RenderUtils.stopDrawVirtual(var2);
         }
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.bd(), this::onPreInputEvent);
      this.registerListener(Listener.ar().getChannel(BlockUpdateS2CPacket.class), this::onBlockUpdate);
      this.registerListener(RenderListener.q(), this::B);
      this.registerListener(Listener.bx().c(ModulePreset.class), this::IU);
   }

   private int supplyInteractionItem(BlockState currentState, BlockState targetState) {
      if (currentState != null && targetState != null) {
         Block var3 = currentState.getBlock();
         Block var4 = targetState.getBlock();
         if (var3 instanceof RespawnAnchorBlock
            && var4 instanceof RespawnAnchorBlock
            && (Integer)currentState.get(RespawnAnchorBlock.CHARGES) < (Integer)targetState.get(RespawnAnchorBlock.CHARGES)) {
            KalamaHelperHelperK var12 = InventoryUtils.p(stack -> stack.isOf(Items.GLOWSTONE), true, false);
            return var12 == null ? -1 : var12.index();
         } else if (var3 instanceof CandleBlock
            && var4 instanceof CandleBlock
            && !(Boolean)currentState.get(CandleBlock.LIT)
            && (Boolean)targetState.get(CandleBlock.LIT)) {
            KalamaHelperHelperK var11 = InventoryUtils.p(stack -> stack.isOf(Items.FLINT_AND_STEEL) || stack.isOf(Items.FIRE_CHARGE), true, false);
            return var11 == null ? -1 : var11.index();
         } else if (var3 instanceof PumpkinBlock && var4 == Blocks.CARVED_PUMPKIN) {
            KalamaHelperHelperK var10 = InventoryUtils.p(stack -> stack.isOf(Items.SHEARS), true, false);
            return var10 == null ? -1 : var10.index();
         } else if (var3 instanceof FlowerPotBlock var5 && var4 instanceof FlowerPotBlock var13) {
            if (var5.getContent() == Blocks.AIR && var13.getContent() != Blocks.AIR) {
               Item var7 = var13.getContent().asItem();
               if (var7 == Items.AIR) {
                  return -1;
               } else {
                  KalamaHelperHelperK var8 = InventoryUtils.p(stack -> stack.isOf(var7), true, false);
                  return var8 == null ? -1 : var8.index();
               }
            } else {
               return -2;
            }
         } else if (var3 instanceof CakeBlock && var4 instanceof CandleCakeBlock) {
            Item var9 = var4.asItem();
            if (var9 == Items.AIR) {
               return -1;
            } else {
               KalamaHelperHelperK var6 = InventoryUtils.p(stack -> stack.isOf(var9), true, false);
               return var6 == null ? -1 : var6.index();
            }
         } else {
            return -2;
         }
      } else {
         return -2;
      }
   }

   private void onBlockUpdate(Event<BlockUpdateS2CPacket> event) {
      if (this.enable.get() && !this.Dw.isEmpty()) {
         BlockPos var2 = ((BlockUpdateS2CPacket)event.b).getPos();
         Integer var3 = this.Dw.get(var2);
         if (var3 != null) {
            this.Dw.put(var2, Math.min(var3, Tasks.b() + 2));
         }
      }
   }

   public boolean doLiquidPlace(BlockPos pos, BlockState targetState) {
      FluidState var3 = targetState.getFluidState();
      BlockState var4 = mc.world.getBlockState(pos);
      if (this.useIceToFormWater.get()
         && var3.getFluid() == Fluids.WATER
         && (var4.isAir() || var4.isLiquid() || var4.isReplaceable())
         && this.doPlace(pos, Blocks.ICE.getDefaultState(), this.airPlace.get(), !this.mode.get().isLegal())) {
         if (Disabler.INSTANCE.ajC()) {
            QueueMine.INSTANCE.sumitMine(pos);
         } else {
            Tasks.p(() -> QueueMine.INSTANCE.sumitMine(pos), 0);
         }

         this.Dw.put(pos, Tasks.b() + 20);
         return true;
      } else if (var3.getFluid() != Fluids.WATER && var3.getFluid() != Fluids.LAVA) {
         return false;
      } else {
         int var5 = this.supplyLiquid(var3.getFluid());
         if (var5 == -1) {
            this.RH(pos);
            if (var3.getFluid() == Fluids.WATER) {
               this.Dv = true;
            }

            return false;
         } else {
            boolean var6 = false;
            FlagEntry var7 = InteractionTasks.u(mc.player.getEyePos(), pos, targetState);
            if (var7 == null) {
               this.RH(pos);
               return false;
            } else {
               if (var7.flag() == mc.player.isSneaking()) {
                  Vec2f var8 = (Vec2f)var7.val();
                  Runnable var9 = InvExtra.INSTANCE.swapInventoryIndexToHand(var5);
                  if (var9 != null) {
                     EntityMovementStatus var10 = new EntityMovementStatus(mc.player);
                     EntityUtils.setEntityPitchSafe(mc.player, var8.x);
                     PlayerStateManager.nT(mc.player, var8.y);
                     mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                     var10.d();
                     var6 = true;
                     this.Dw.put(pos, Tasks.b() + 20);
                  }
               }

               if (var6) {
                  this.RI(pos);
               } else {
                  this.RH(pos);
               }

               if (var7.flag()) {
                  this.cQ = true;
               }

               return var6;
            }
         }
      }
   }

   private boolean isStackedPlacementTransition(BlockState currentState, BlockState targetState) {
      if (currentState == null || targetState == null) {
         return false;
      } else if (!currentState.isOf(targetState.getBlock())) {
         return false;
      } else {
         Block var3 = currentState.getBlock();
         if (var3 instanceof SlabBlock) {
            return currentState.get(SlabBlock.TYPE) != SlabType.DOUBLE && targetState.get(SlabBlock.TYPE) == SlabType.DOUBLE;
         } else if (var3 instanceof SnowBlock) {
            return (Integer)currentState.get(SnowBlock.LAYERS) < (Integer)targetState.get(SnowBlock.LAYERS);
         } else if (var3 instanceof CandleBlock) {
            return (Integer)currentState.get(CandleBlock.CANDLES) < (Integer)targetState.get(CandleBlock.CANDLES);
         } else if (var3 instanceof SeaPickleBlock) {
            return (Integer)currentState.get(SeaPickleBlock.PICKLES) < (Integer)targetState.get(SeaPickleBlock.PICKLES);
         } else {
            return var3 instanceof FlowerbedBlock
               ? (Integer)currentState.get(FlowerbedBlock.FLOWER_AMOUNT) < (Integer)targetState.get(FlowerbedBlock.FLOWER_AMOUNT)
               : false;
         }
      }
   }

   public boolean RL(BlockState fromState, BlockState toState) {
      return InteractUtils.E(fromState, toState);
   }

   public int supplyBlocks(Block needBlock) {
      Item var2 = needBlock.asItem();
      if (var2 == Items.AIR) {
         return -1;
      } else {
         KalamaHelperHelperK var3 = InventoryUtils.p(item -> item.getItem() == var2, true, false);
         return var3 == null ? -1 : var3.index();
      }
   }

   public void updateBlocks(double i) {
      this.Dp = new ArrayList<>();
      ArrayList<Vec3i> var3 = new ArrayList();
      int var4 = (int)i;

      for (int var5 = -var4; var5 <= var4; var5++) {
         for (int var6 = -var4; var6 <= var4; var6++) {
            var3.add(new Vec3i(var5, 0, var6));
         }
      }

      var3.sort(Comparator.comparingDouble(v -> v.getX() * v.getX() + v.getZ() * v.getZ()));

      for (int var8 = -var4; var8 <= var4; var8++) {
         for (Vec3i var7 : var3) {
            this.Dp.add(new Vec3i(var7.getX(), var8, var7.getZ()));
         }
      }
   }
}
