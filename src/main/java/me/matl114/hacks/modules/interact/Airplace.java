package me.matl114.hacks.modules.interact;

import java.awt.Color;
import java.util.function.Predicate;
import me.matl114.events.Event;
import me.matl114.events.KalamaHelperHelperG;
import me.matl114.events.Listener;
import me.matl114.events.PacketManager;
import me.matl114.events.RenderListener;
import me.matl114.events.catchers.PacketCatcherImpl;
import me.matl114.events.packets.PacketStorage;
import me.matl114.hacks.InteractionTasks;
import me.matl114.hacks.RenderTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.ac.Disabler;
import me.matl114.hacks.modules.move.FloatingUtils;
import me.matl114.managers.Configs;
import me.matl114.managers.config.ConfigEnum;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.Debug;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.algorithms.StateMachine;
import me.matl114.utils.entity.PlayerInputUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;

public class Airplace extends BaseModule {
   static final int tG = 1;
   public final ModulePath fZ = makePath(Configs.n, "interaction-tweaks");
   public final IntRef maxBatchPlace;
   static final int tF = 0;
   public final IntRef invSleepTick;
   public final EnumRef<Airplace$Mode> mode;
   static final int tH = 2;
   public final FlagRef render;
   public final ModulePath ty = this.fZ.add("air-place");
   InteractSubHelperJX tD;
   static final int tI = 3;
   public final FlagRef ae = this.flagBuilder(this.ty.add("enable")).build();
   public final DoubleRef range;
   int tE;
   int tC;
   public final KeyBindRef J = this.toggleHotkey(this.ty.add("hotkey"), new MultiKeyBind(), this.ty.add("enable")).build();
   BlockPos ei;
   StateMachine tJ;

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.bn(), this::onInteract);
      this.registerListener(Listener.bd(), this::gt);
      this.registerListener(RenderListener.q(), this::onRenderPos);
      this.registerListener(PacketManager.z().c(NetworkSide.CLIENTBOUND), this::Dh);
      this.registerListener(Listener.T(), this::hZ);
      this.registerListener(PacketManager.A(), this::Dd);
   }

   public int onPlace(StateMachine machine) {
      Mutable var2 = this.tD.startPos();
      int var3 = this.tD.targetPos().getY();
      int var4 = Math.min(this.maxBatchPlace.get(), this.tD.selectedSlot() - 1);
      int var5 = 0;

      while (var2.getY() != var3) {
         BlockPos var6 = var2.toImmutable();
         Direction var7 = this.tD.way() < 0 ? Direction.UP : Direction.DOWN;
         BlockHitResult var8 = new BlockHitResult(var6.toCenterPos().offset(var7, 0.5), var7, var6, false);
         mc.interactionManager.sendSequencedPacket(mc.world, seq -> new PlayerInteractBlockC2SPacket(this.tD.hand(), var8, seq));
         var2.move(0, -this.tD.way(), 0);
         if (++var5 >= var4) {
            this.tE = 0;
            return 2;
         }
      }

      this.tE = 0;
      machine.e();
      return 3;
   }

   public Airplace() {
      super("Airplace");
      this.range = this.builder(this.ty.add("range"), DoubleRef.TYPE).defaultValue(5.0).validator(Configs.doubleRange(0.0, 10000.0)).build();
      this.render = this.flagBuilder(this.ty.add("render")).build();
      this.mode = this.builder(this.ty.add("mode"), Airplace$Mode.class).defaultValue(Airplace$Mode.VANILLA).updateListener(s -> this.De()).build();
      this.maxBatchPlace = this.intBuilder(this.ty.add("max-batch-place"))
         .defaultValue(64)
         .validator(Configs.e)
         .show(() -> this.mode.get().isIn(new ConfigEnum[]{Airplace$Mode.GRIM_FAST_GHOST_BLOCK_WALL}))
         .build();
      this.invSleepTick = this.intBuilder(this.ty.add("inv-sleep-tick")).defaultValue(2).validator(Configs.e).show(this::isState).build();
      this.ei = null;
      this.tC = 0;
      this.tE = 0;
      this.bindFlag(this.ae);
   }

   public void Dd(Event<Void> event) {
      this.Df();
   }

   public void onGrimFastWall(BlockHitResult hitResult, Hand hand) {
      this.Dj();
      if (Disabler.INSTANCE.ajv()) {
         if (!Disabler.INSTANCE.autoFlushMultiPlaceQueue.get()) {
            Debug.chat("[AirWall] 请先在", Text.translatable("config.index.disablers"), "中启用配置项: ", Text.translatable("disablers.auto-flush-multi-place-queue"));
            return;
         }

         ItemStack var3 = mc.player.getStackInHand(hand);
         if (var3.isEmpty()) {
            return;
         }

         if (var3.getCount() < 2) {
            Debug.b("[AirWall] 手上物品太少,无法执行,该模式下手上尽可能有足够多的方块");
            return;
         }

         int var4 = Math.min(this.maxBatchPlace.get(), 48);
         if (var3.getCount() < var4) {
            Debug.b("[AirWall] 提示: 我们推荐该模式手上最好有足够多(>= %d)的方块,当前数量可能会导致放置较慢".formatted(var4));
         }

         BlockPos var5 = hitResult.getBlockPos();
         Vec3d var6 = var5.toCenterPos();
         int var7 = var6.y < mc.player.getEyePos().y ? -1 : 1;
         BlockPos var8 = null;

         for (int var9 = 1; var9 < 256; var9++) {
            BlockPos var10 = var5.add(0, var7 * var9, 0);
            BlockState var11 = mc.world.getBlockState(var10);
            if (!var11.isAir() && !var11.isLiquid()) {
               var8 = var10;
               break;
            }
         }

         if (var8 != null) {
            this.tD = new InteractSubHelperJX(var8.mutableCopy(), var5, var3.getCount(), var3.copy(), InventoryUtils.getSelectedSlot(), hand, var7);
            this.tJ = this.Dn();
         }
      } else {
         Debug.b("[AirWall] 当前暂未禁用GrimSelfCheck,无法执行");
      }
   }

   public int onWait300MS(StateMachine machine) {
      machine.e();
      this.tE++;
      if (this.tE > 8) {
         this.tJ = null;
         BlockPos var2 = this.tD.targetPos();
         Direction var3 = this.tD.way() < 0 ? Direction.UP : Direction.DOWN;
         BlockHitResult var4 = new BlockHitResult(var2.toCenterPos().offset(var3, 0.5), var3, var2, false);
         mc.interactionManager.sendSequencedPacket(mc.world, seq -> new PlayerInteractBlockC2SPacket(this.tD.hand(), var4, seq));
         this.tD = null;
         Debug.b("[AirWall] 任务完成");
         return 0;
      } else {
         return 3;
      }
   }

   public void onInputFastWall() {
      if (this.ae.get() && this.tD != null && this.tJ != null) {
         BlockPos var1 = this.tD.targetPos();
         ItemStack var2 = this.tD.item();
         Hand var3 = this.tD.hand();
         ItemStack var4 = mc.player.getStackInHand(var3);
         if (ItemStack.areItemsEqual(var4, var2)) {
            if (var1.toCenterPos().subtract(mc.player.getEyePos()).horizontalLengthSquared() <= MathUtils.a(mc.player.getBlockInteractionRange() + 1.0)) {
               this.tJ.f();
            } else {
               Debug.b("[AirWall] 你移动的位置太多了, 终止任务");
               this.tD = null;
               this.tJ = null;
            }
         } else {
            Debug.b("[AirWall] 手上的物品被切换了，终止任务");
            this.tD = null;
            this.tJ = null;
         }
      }
   }

   public void onInteract(Event<HitResult> event) {
      if (!event.d() && this.ae.get()) {
         Hand var2 = event.getArgs(0);
         ItemStack var3 = mc.player.getStackInHand(var2);
         if (!var3.isEmpty() && var3.getItem() instanceof BlockItem) {
            HitResult var4 = (HitResult)event.e();
            if (var4.getType() == Type.MISS) {
               HitResult var5 = this.getCameraEntity().raycast(this.range.get(), 0.0F, false);
               if (var5.getType() == Type.MISS && var5 instanceof BlockHitResult var6) {
                  switch ((Airplace$Mode)this.mode.get()) {
                     case VANILLA:
                        BlockHitResult var7 = new BlockHitResult(var6.getPos(), var6.getSide(), var6.getBlockPos(), var6.isInsideBlock());
                        event.context(var7);
                        return;
                     case GRIM_GHOST_BLOCK_WALL:
                        this.onGrimAirWall(var6);
                        return;
                     case GRIM_FAST_GHOST_BLOCK_WALL:
                        this.onGrimFastWall(var6, var2);
                        return;
                  }
               }
            }
         }
      }
   }

   public void De() {
      this.Df();
   }

   public Entity getCameraEntity() {
      return (Entity)(mc.getCameraEntity() != null ? mc.getCameraEntity() : mc.player);
   }

   public boolean isState() {
      return this.tD != null;
   }

   public void Dj() {
      this.tD = null;
      this.tJ = null;
   }

   public void Df() {
      this.ei = null;
      this.tC = 0;
   }

   public void gt(Event<Void> event) {
      this.onInputGrimWall();
      this.onInputFastWall();
   }

   public void Dh(Event<PacketStorage> packet) {
      if (this.ae.get() && this.ei != null) {
         PacketStorage var2 = (PacketStorage)packet.b;
         if (PacketManager.t(var2.packetType())) {
            return;
         }

         this.tC++;
         packet.cancel();
      }
   }

   public void xu() {
      this.tC--;
      if (this.ei == null && this.tC == 0) {
         PacketManager.k();
      } else {
         if (this.tC > 3) {
            this.tC = 3;
         }

         PacketManager.l(packetStorage -> {
            long var1 = packetStorage.timestampMS();
            long var3 = System.currentTimeMillis();
            return var3 > var1 + 50L ? KalamaHelperHelperG.NU : KalamaHelperHelperG.NV;
         });
      }
   }

   public void onRenderPos(Event<MatrixStack> event) {
      if (this.ae.get()) {
         if (mc.player.getStackInHand(Hand.MAIN_HAND).isEmpty() && mc.player.getStackInHand(Hand.OFF_HAND).isEmpty()) {
            return;
         }

         MatrixStack var2 = (MatrixStack)event.e();
         if (mc.crosshairTarget.getType() == Type.MISS) {
            HitResult var3 = this.getCameraEntity().raycast(this.range.get(), 0.0F, false);
            if (var3.getType() == Type.MISS && var3 instanceof BlockHitResult var4) {
               RenderUtils.startDrawVirtual(var2);

               try {
                  BlockPos var5 = var4.getBlockPos();
                  RenderUtils.drawOutlinedBox(var2, Vec3d.of(var5), Vec3d.of(var5.add(1, 1, 1)), Color.RED);
               } finally {
                  RenderUtils.stopDrawVirtual(var2);
               }
            }
         }
      }
   }

   public StateMachine Dn() {
      return new StateMachine(1, this::onUpdate, state -> 0, this::onPlace, this::onWaitSlotUpdate, this::onWait300MS);
   }

   public void onGrimAirWall(BlockHitResult hitResult) {
      this.Df();
      if (Disabler.INSTANCE.ajv()) {
         this.ei = hitResult.getBlockPos();
      } else {
         Debug.b("[AirWall] 当前暂未禁用GrimSelfCheck,无法执行");
      }
   }

   public int onUpdate(StateMachine machine, int t) {
      if (this.tD == null) {
         this.tJ = null;
         machine.e();
         return 0;
      } else {
         return t;
      }
   }

   public void onInputGrimWall() {
      if (this.ae.get()
         && this.ei != null
         && mc.player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof BlockItem var2
         && var2 != Items.AIR
         && this.ei.toCenterPos().subtract(mc.player.getEyePos()).horizontalLengthSquared() <= MathUtils.a(mc.player.getBlockInteractionRange() + 1.0)) {
         for (int var6 = 1; var6 < 256; var6++) {
            BlockPos var3 = this.ei.add(0, -var6, 0);
            BlockState var4 = mc.world.getBlockState(var3);
            if (!var4.isAir() && !var4.isLiquid()) {
               if (var6 == 1) {
                  this.ei = null;
               }

               RenderTasks.drawBox(Box.from(new BlockBox(var3)), 50, Color.MAGENTA);
               InteractionTasks.b(Hand.MAIN_HAND, new BlockHitResult(var3.toBottomCenterPos().add(0.0, 1.0, 0.0), Direction.UP, var3, false), false);
               mc.player.swingHand(Hand.MAIN_HAND);
               if (!PlayerInputUtils.of(mc.options).ru()) {
                  FloatingUtils.INSTANCE.SB(true);
               }

               return;
            }
         }
      } else {
         this.ei = null;
      }
   }

   public int onWaitSlotUpdate(StateMachine machine) {
      int var2 = this.invSleepTick.get();
      if (this.tE == var2) {
         ItemStack var3 = mc.player.getStackInHand(this.tD.hand()).copy();
         mc.player.setStackInHand(this.tD.hand(), ItemStack.EMPTY);

         try {
            int var4 = mc.player.currentScreenHandler.getSlotIndex(mc.player.getInventory(), this.tD.itemCount()).orElse(-1);
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, var4, 40, SlotActionType.SWAP, mc.player);
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, var4, 40, SlotActionType.SWAP, mc.player);
         } finally {
            mc.player.setStackInHand(this.tD.hand(), var3);
         }

         Predicate var8 = event -> {
            if (machine.getState() == 2 && this.tE < 20) {
               machine.c(1);
            }

            return true;
         };
         Listener.C(new PacketCatcherImpl(ScreenHandlerSlotUpdateS2CPacket.class, var8));
         Listener.C(new PacketCatcherImpl(InventoryS2CPacket.class, var8));
      }

      this.tE++;
      machine.e();
      return this.tE >= 20 ? 1 : 2;
   }

   public void hZ(Event<Void> event) {
      if (this.tC > 0) {
         this.xu();
      }
   }

   @Override
   public void onDisableModule() {
      super.onDisableModule();
      this.Df();
   }
}
