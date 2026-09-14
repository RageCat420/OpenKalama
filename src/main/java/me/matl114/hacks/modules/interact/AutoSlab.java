package me.matl114.hacks.modules.interact;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.matl114.accessors.moonrise.MoonriseBlockStateBaseAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.InteractionTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.modules.ac.Disabler;
import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.hacks.utils.config.WrapColor;
import me.matl114.hacks.utils.render.RenderCollectors;
import me.matl114.managers.Configs;
import me.matl114.managers.Configs$LegalInteractMode;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.InteractUtils;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.WorldUtils;
import me.matl114.utils.collections.FlagEntry;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.render.RenderCollector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.SlabBlock;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.SpawnHelper;

public class AutoSlab extends BaseModule {
   public final Set<Block> Uc;
   public final FlagRef render;
   public final FlagRef useBlockRotate;
   public final FlagRef slabOnly;
   public final FlagRef swingHand;
   List<BlockPos> Ud;
   public final ModulePath Mj = makePath(Configs.n, "place-utils.auto-slab");
   public final DoubleRef interactRange;
   public final IntRef delay;
   public final Set<Item> Ua;
   public final FlagRef useBlockEntities;
   public final IntRef multiply;
   public final NBTRef<EntrySet<Block>> TZ;
   public final Set<Block> Ub;
   public final KeyBindRef J;
   public final FlagRef ghostHandSwapBack;
   public final RenderCollector<Box> gN;
   public final NBTRef<WrapColor> renderColor;
   public final EnumRef<Configs$LegalInteractMode> mode;
   public final FlagRef airPlace;
   int timer;
   public final FlagRef ae = this.flagBuilder(this.Mj.addEnable()).build();
   public List<Vec3i> Dp;

   public void initializeMap() {
      if (this.Uc.isEmpty()) {
         for (Block var2 : Registries.BLOCK) {
            try {
               if (!this.Ub.contains(var2)) {
                  BlockState var3 = var2.getDefaultState();

                  try {
                     if (!MoonriseBlockStateBaseAccess.of(var3).isConstantCollisionShapeEmpty()) {
                        this.Uc.add(var2);
                     }
                  } catch (Throwable var5) {
                  }

                  if (!SpawnHelper.isClearForSpawn(null, null, var3, var3.getFluidState(), EntityType.CREEPER)) {
                     this.Uc.add(var2);
                  }
               }
            } catch (Throwable var6) {
            }
         }
      }
   }

   public void tickPlace() {
      int var1 = 0;
      int var2 = Disabler.INSTANCE.isMultiRotPlaceCheckDisabled(this.mode.get().canMultiRotPlace()) ? this.multiply.get() : 1;
      ArrayList var3 = new ArrayList(var2);

      for (BlockPos var5 : this.Ud) {
         KalamaHelperHelperK var6 = this.supplyItem();
         if (var6 != null && ((ItemStack)var6.val()).getItem() instanceof BlockItem var8) {
            BlockState var13 = var8.getBlock().getDefaultState();
            FlagEntry var9 = InteractionTasks.q(var5, var13, this.airPlace.get(), !this.mode.get().isLegal());
            if (InteractUtils.C(mc.player, var9)
               && InteractExtra.INSTANCE.fE(mc.player.getPos(), ((BlockHitResult)var9.val()).getBlockPos(), this.interactRange.get())
               && InteractUtils.b(var8, mc.player, mc.world, (BlockHitResult)var9.val()) != null) {
               Runnable var10 = InvExtra.INSTANCE.swapInventoryIndexToHand(var6.index());
               if (var10 == null) {
                  break;
               }

               var3.add(var10);
               if (this.useBlockRotate.get()) {
                  BlockRotate.INSTANCE.OO(var5, var13);
               }

               InteractionTasks.g(this.mode.get(), (BlockHitResult)var9.val(), Hand.MAIN_HAND, this.swingHand.get());
               if (++var1 >= var2) {
                  break;
               }
            }
         }
      }

      if (this.ghostHandSwapBack.get()) {
         int var11 = var3.size();

         for (int var12 = var11 - 1; var12 >= 0; var12--) {
            ((Runnable)var3.get(var12)).run();
         }
      }
   }

   public void bl(Event<Void> event) {
      if (!checkNull()) {
         this.gN.clear();
         if (this.ae.get()) {
            this.refreshBlocks();
            if (++this.timer >= this.delay.get()) {
               this.timer = 0;
               this.tickPlace();
            }
         }
      }
   }

   public void gA(Event<KalamaHelperHelperI<ModulePreset>> event) {
      this.mode.set(Configs$LegalInteractMode.getFromPreset((ModulePreset)((KalamaHelperHelperI)event.b).b()));
      this.airPlace.set(!((ModulePreset)((KalamaHelperHelperI)event.b).b()).hasAC());
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.O(), this::tI);
      this.registerListener(Listener.bd(), this::bl);
      this.registerListener(RenderListener.q(), this::Gn);
      this.registerListener(Listener.bx().c(ModulePreset.class), this::gA);
   }

   public KalamaHelperHelperK<ItemStack> supplyItem() {
      return InventoryUtils.p(s -> this.isAvailable(s.getItem()), true, false);
   }

   public AutoSlab() {
      super("AutoSlab");
      this.J = this.toggleHotkey(this.Mj.addHotkey(), new MultiKeyBind(), this.Mj.addEnable()).build();
      this.Dp = new ArrayList<>();
      this.mode = this.builder(this.Mj.add("mode"), Configs$LegalInteractMode.class).defaultValue(Configs$LegalInteractMode.NONE).build();
      this.airPlace = this.flagBuilder(this.Mj.add("air-place")).build();
      this.delay = this.intBuilder(this.Mj.add("delay")).defaultValue(5).build();
      this.multiply = this.intBuilder(this.Mj.add("multiply")).defaultValue(1).build();
      this.interactRange = this.doubleBuilder(this.Mj.add("interact-range"))
         .defaultValue(5.0)
         .validator(Configs.doubleRange(0.0, 100.0))
         .updateListener(s -> this.Dp = MathUtils.H(s))
         .build();
      this.slabOnly = this.flagBuilder(this.Mj.add("slab-only")).build();
      this.useBlockEntities = this.flagBuilder(this.Mj.add("use-block-entities")).build();
      this.TZ = this.builder(this.Mj.add("black-list-item"), EntrySet.<Block>parameter())
         .defaultValue(new EntrySet<Block>(new Regex("^(ender_chest|chest)$"), Registries.BLOCK))
         .build();
      this.useBlockRotate = this.flagBuilder(this.Mj.add("use-block-rotate")).build();
      this.ghostHandSwapBack = this.flagBuilder(this.Mj.add("ghost-hand-swap-back")).build();
      this.swingHand = this.builder(this.Mj.add("swing-hand"), Boolean.class).defaultValue(true).build();
      this.render = this.flagBuilder(this.Mj.add("render")).build();
      this.renderColor = this.builder(this.Mj.add("render-color"), WrapColor.class).defaultValue(new WrapColor(Color.GREEN)).build();
      this.Ua = new HashSet<>();
      this.Ub = new HashSet<>();
      this.Uc = new HashSet<>();
      this.gN = RenderCollectors.createBoxCollector(true, false, false);

      for (Block var2 : Registries.BLOCK) {
         try {
            if (var2.getDefaultState().allowsSpawning(null, null, EntityType.CREEPER)) {
               this.Ub.add(var2);
            }
         } catch (Throwable var4) {
         }

         if (var2 instanceof SlabBlock var3) {
            this.Ua.add(var2.asItem());
         }
      }

      this.timer = 0;
      this.Ud = new ArrayList<>();
      this.bindFlag(this.ae);
   }

   public void Gn(Event<MatrixStack> event) {
      if (!checkNull()) {
         if (this.ae.get() && this.render.get()) {
            RenderUtils.startDrawVirtual((MatrixStack)event.b);

            try {
               this.gN.a((MatrixStack)event.b);
            } finally {
               RenderUtils.stopDrawVirtual((MatrixStack)event.b);
            }
         }
      }
   }

   public boolean isAvailable(Item item) {
      if (this.slabOnly.get()) {
         return this.Ua.contains(item);
      } else if (item instanceof BlockItem var2) {
         this.initializeMap();
         Block var3 = var2.getBlock();
         if (this.Uc.contains(var3)) {
            return !this.useBlockEntities.get() && var3 instanceof BlockWithEntity var4 ? false : !this.TZ.get().test(var3);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void refreshBlocks() {
      this.Ud.clear();
      BlockPos var1 = mc.player.getBlockPos();

      for (Vec3i var3 : this.Dp) {
         BlockPos var4 = var1.add(var3);
         BlockState var5 = mc.world.getBlockState(var4);
         if ((var5.isAir() || var5.isLiquid() || var5.isReplaceable()) && WorldUtils.canEntitySpawnAt(mc.world, var4, EntityType.CREEPER)) {
            this.gN.submit(new Box(var4), this.renderColor.get().withAlpha(255));
            this.Ud.add(var4);
         }
      }
   }

   public void tI(Event<Void> eventLeave) {
      this.Uc.clear();
   }
}
