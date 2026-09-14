package me.matl114.hacks.modules.survival;

import java.util.List;
import java.util.function.Consumer;
import me.matl114.accessors.hacks.PlayerInteractionAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.interact.Interact;
import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.render.HackUtilHelperB;
import me.matl114.hacks.utils.render.RenderCollectors;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.AttributeUtils;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.render.RenderCollector;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class BlockFarm extends BaseModule {
   public final NBTRef<EntrySet<Item>> whiteList;
   public final FlagRef swingHand;
   RenderCollector<HackUtilHelperB> lg;
   public final IntRef delay;
   BlockItem lf;
   public final KeyBindRef J;
   public final FlagRef render;
   int timer;
   public final IntRef multiply;
   public final ModulePath aD = makePath(Configs.o, "survival-mine-utils.block-farm");
   public final FlagRef ae;
   public final FlagRef whiteListEnable;

   public KalamaHelperHelperK<ItemStack> supplyItems(BlockItem blockItem) {
      return InventoryUtils.v(s -> s.getItem() == blockItem ? (double)(-s.getCount()) : null, true, false);
   }

   public void onPlayerMineAttackBlock(Event<HitResult> hitResultEvent) {
      if (this.ae.get()) {
         this.lf = null;
         if (hitResultEvent.b instanceof BlockHitResult var3) {
            BlockPos var7 = var3.getBlockPos();
            BlockState var4;
            if (var7 != null && !(var4 = mc.world.getBlockState(var7)).isAir() && !var4.isLiquid()) {
               Item var5 = var4.getBlock().asItem();
               if (var5 instanceof BlockItem var6 && var6 != Items.AIR && (!this.whiteListEnable.get() || this.whiteList.get().test(var5))) {
                  this.lf = var6;
               }
            }
         }
      } else {
         this.lf = null;
      }
   }

   public BlockFarm() {
      super("BlockFarm");
      portConfigs(makePath(Configs.g, "mine-utils.block-farm"), this.aD);
      this.ae = this.flagBuilder(this.aD.addEnable()).build();
      this.J = this.toggleHotkey(this.aD.addHotkey(), new MultiKeyBind(), this.aD.addEnable()).build();
      this.delay = this.intBuilder(this.aD.add("delay")).defaultValue(6).build();
      this.multiply = this.intBuilder(this.aD.add("multiply")).defaultValue(9).build();
      this.whiteListEnable = this.flagBuilder(this.aD.add("white-list-enable")).build();
      this.whiteList = this.builder(this.aD.add("white-list"), EntrySet.<Item>parameter())
         .defaultValue(new EntrySet<Item>(Registries.ITEM, List.of(Items.ENDER_CHEST, Items.BOOKSHELF)))
         .build();
      this.swingHand = this.builder(this.aD.add("swing-hand"), Boolean.class).defaultValue(true).build();
      this.render = this.flagBuilder(this.aD.add("render")).build();
      this.lg = RenderCollectors.f();
      this.bindFlag(this.ae);
   }

   public void onPreInput(Event<Void> event) {
      if (!checkNull()) {
         this.lg.clear();
         if (this.ae.get() && this.lf != null) {
            PlayerInteractionAccess var2 = PlayerInteractionAccess.of(mc.interactionManager);
            BlockPos var3 = var2.getCurrentMiningPos();
            BlockState var4 = mc.world.getBlockState(var3);
            if (var4.getBlock() == this.lf.getBlock()) {
               if (this.timer++ > this.delay.get()
                  && new Box(var3).squaredMagnitude(mc.player.getEyePos()) < MathUtils.a(AttributeUtils.getPlayerBlockInteractionRange(mc.player))) {
                  this.timer = 0;
                  this.tickMineAndPlace(this.lf);
               }

               this.lg
                  .submit(
                     new HackUtilHelperB(
                        Text.literal("Farm: %s".formatted(Registries.ITEM.getId(this.lf).getPath())), var3.toCenterPos().add(0.0, 0.6, 0.0), 0.66F
                     ),
                     -1
                  );
            }
         }
      }
   }

   public void tickMineAndPlace(BlockItem blockItem) {
      int var2 = this.multiply.get();
      Runnable var3 = null;
      PlayerInteractionAccess var4 = PlayerInteractionAccess.of(mc.interactionManager);

      for (int var5 = 0; var5 < var2 && var4.breakIfComplete(); var5++) {
         if (mc.player.getStackInHand(Hand.MAIN_HAND).getItem() != blockItem) {
            if (var3 != null) {
               var3.run();
               var3 = null;
            }

            KalamaHelperHelperK var6 = this.supplyItems(blockItem);
            if (var6 == null) {
               break;
            }

            var3 = InvExtra.INSTANCE.swapInventoryIndexToHand(var6.index());
         }

         BlockPos var8 = var4.getCurrentMiningPos();
         if (!Interact.INSTANCE.placeBlock(var8)) {
            break;
         }
      }

      if (var3 != null) {
         var3.run();
         Object var7 = null;
      }
   }

   @Override
   public void addCustomWidgets(Consumer<DrawableWidget> acceptor, int dx, int dy, int dblank) {
      super.addCustomWidgets(acceptor, dx, dy, dblank);
      acceptor.accept(this.createTitleLabel("widget.interact.interact-block.use-argument", 0, dblank, dx, dy));
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.bl(), this::onPlayerMineAttackBlock);
      this.registerListener(Listener.bd(), this::onPreInput);
      this.registerListener(RenderListener.r(), this::sg);
   }

   public void sg(Event<VDrawContext> vdraw) {
      if (this.ae.get() && this.render.get()) {
         ((VDrawContext)vdraw.b).b();

         try {
            this.lg.b((VDrawContext)vdraw.b);
         } finally {
            ((VDrawContext)vdraw.b).c();
         }
      }
   }
}
