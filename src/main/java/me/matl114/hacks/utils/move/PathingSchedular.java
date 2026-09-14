package me.matl114.hacks.utils.move;

import com.mojang.datafixers.util.Pair;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.matl114.accessors.access.ChunkAccess;
import me.matl114.accessors.interfaces.TileInventory;
import me.matl114.events.Event;
import me.matl114.hacks.modules.interact.Interact;
import me.matl114.hacks.modules.interact.InteractExtra;
import me.matl114.hacks.modules.inv.ChestHistory;
import me.matl114.hacks.modules.inv.InvSubHelperV;
import me.matl114.hacks.modules.inv.KitReplenish;
import me.matl114.hacks.modules.survival.SchedularSettings;
import me.matl114.hacks.utils.config.Vec3;
import me.matl114.hacks.utils.move.goal.GoalDirection;
import me.matl114.hacks.utils.move.goal.GoalNear;
import me.matl114.hacks.utils.move.goal.IPathGoal;
import me.matl114.hacks.utils.render.RenderCollectors;
import me.matl114.hacks.utils.tasks.TimerExecutor;
import me.matl114.managers.Tasks;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.algorithms.StateMachine;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.render.RenderCollector;
import me.matl114.utils.world.ContainerPosition;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.chunk.WorldChunk;

public class PathingSchedular {
   public static KalamaHelperHelperK<PathingSchedular> b = null;
   @Nullable
   BiPredicate<ContainerPosition, Inventory> l;
   ContainerPosition C;
   ClientPlayerEntity q;
   boolean p;
   final TimerExecutor x;
   @Nullable
   Supplier<IPathGoal> f;
   HashSet<ContainerPosition> t;
   @Nullable
   Supplier<Iterable<ContainerPosition>> k;
   BlockPos D;
   ContainerPosition A;
   HashSet<ContainerPosition> s;
   final StateMachine n;
   static final MinecraftClient a = MinecraftClient.getInstance();
   @Nullable
   BooleanSupplier h;
   HashSet<ContainerPosition> u;
   RenderCollector<Box> v;
   @Nullable
   Predicate<ItemStack> j;
   private static final int d = 6;
   HashSet<ContainerPosition> y;
   ContainerPosition z;
   ContainerPosition B;
   @Nullable
   BooleanSupplier g;
   private static final int c = 4;
   @Nonnull
   HackUtilHelperD o;
   @Nullable
   BooleanSupplier e;
   boolean w;
   @Nullable
   Predicate<HandledScreen<?>> i;
   @Nullable
   BiPredicate<ContainerPosition, Inventory> m;
   @Nonnull
   BlockPos r = BlockPos.ORIGIN;

   public PathingSchedular U(boolean autoAfk) {
      this.w = autoAfk;
      return this;
   }

   private static boolean isValidLandingSpot(BlockPos supportPos, BlockPos standPos) {
      if (a.world != null && a.player != null) {
         if (supportPos.getY() >= a.world.getBottomY() && standPos.getY() < a.world.getTopY()) {
            BlockState var2 = a.world.getBlockState(supportPos);
            if (!var2.isAir() && !var2.isLiquid()) {
               VoxelShape var3 = var2.getCollisionShape(a.world, supportPos, ShapeContext.of(a.player));
               if (var3.isEmpty()) {
                  return false;
               } else if (!isPassableForPath(standPos)) {
                  return false;
               } else {
                  BlockPos var4 = new BlockPos(standPos.getX(), standPos.getY() + 1, standPos.getZ());
                  return isPassableForPath(var4);
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public int onStateNone(StateMachine machine) {
      this.r = this.q.getSteppingPos().add(0, 1, 0);
      return this.p ? HackUtilHelperI.Td.ordinal() : HackUtilHelperI.Tc.ordinal();
   }

   public PathingSchedular D(@Nullable BooleanSupplier active) {
      this.e = active;
      return this;
   }

   private boolean isEmptyShulkerBox(ItemStack stack) {
      return stack.getItem() instanceof BlockItem var3
         && var3.getBlock() instanceof ShulkerBoxBlock
         && ItemStackUtils.g(stack, DataComponentTypes.CONTAINER)
         && ((ContainerComponent)stack.get(DataComponentTypes.CONTAINER)).streamNonEmpty().findFirst().isEmpty();
   }

   public PathingSchedular G(@Nullable BooleanSupplier discharge) {
      this.h = discharge;
      return this;
   }

   public PathingSchedular Q(HashSet<ContainerPosition> dischargeSource) {
      this.t = dischargeSource;
      return this;
   }

   public PathingSchedular E(@Nullable Supplier<IPathGoal> processGoal) {
      this.f = processGoal;
      return this;
   }

   public HashSet<ContainerPosition> R() {
      return this.t;
   }

   public PathingSchedular L(@Nullable BiPredicate<ContainerPosition, Inventory> dischargeSourcePredicate) {
      this.m = dischargeSourcePredicate;
      return this;
   }

   public PathingSchedular J(@Nullable Supplier<Iterable<ContainerPosition>> containerSourceOverride) {
      this.k = containerSourceOverride;
      return this;
   }

   public void onResetStateToNone(boolean bl) {
      if (bl) {
         this.s.clear();
         this.t.clear();
         this.u.clear();
         this.z = null;
         this.v.clear();
         this.r = BlockPos.ORIGIN;
         this.y = null;
         this.z = null;
         this.B = null;
         this.A = null;
         this.D = null;
         this.C = null;
         this.o.sumitGoal(null);
      }
   }

   public static boolean z() {
      return b != null && b.val().y();
   }

   public void tickPathing(ClientPlayerEntity player) {
      this.v.clear();
      if (this.e != null) {
         this.p = this.e.getAsBoolean();
      }

      if (!this.p) {
         this.A();
      } else if (!this.x()) {
         this.o.sumitGoal(null);
      } else {
         a(this);
         if (this.q != player) {
            this.q = player;
            this.n.c(HackUtilHelperI.Tc.ordinal());
         }

         if (SchedularSettings.INSTANCE.holdReset.get().d()) {
            this.c();
            SchedularSettings.INSTANCE.amE();
         } else if (SchedularSettings.INSTANCE.pause.get()) {
            SchedularSettings.INSTANCE.amD();
            this.o.sumitGoal(null);
         } else {
            this.n.f();
         }

         if (SchedularSettings.INSTANCE.enableRender.get()) {
            int var2 = SchedularSettings.INSTANCE.replenishmentColor.get().withAlpha(255);
            int var3 = SchedularSettings.INSTANCE.dischargeColor.get().withAlpha(255);
            int var4 = SchedularSettings.INSTANCE.shulkerSupportColor.get().withAlpha(255);
            int var5 = SchedularSettings.INSTANCE.goalColor.get().withAlpha(255);

            for (ContainerPosition var7 : this.s) {
               this.v.submit(var7.vG(), var2);
            }

            for (ContainerPosition var11 : this.t) {
               this.v.submit(var11.vG(), var3);
            }

            for (ContainerPosition var12 : this.u) {
               this.v.submit(var12.vG(), var4);
            }

            IPathGoal var10 = this.o.getCurrentGoal();
            if (var10 != null) {
               Vec3d var13 = var10.sample();
               if (var13 != null) {
                  this.v.submit(new Box(var13.add(-0.4, 0.0, -0.4), var13.add(0.4, 0.8, 0.4)), var5);
               }
            }
         }
      }
   }

   public PathingSchedular H(@Nullable Predicate<HandledScreen<?>> replenishAction) {
      this.i = replenishAction;
      return this;
   }

   public PathingSchedular N(boolean enable) {
      this.p = enable;
      return this;
   }

   public PathingSchedular S(HashSet<ContainerPosition> optionalHasShulkerBoxOrChestSource) {
      this.u = optionalHasShulkerBoxOrChestSource;
      return this;
   }

   public PathingSchedular O(HashSet<ContainerPosition> replenishSource) {
      this.s = replenishSource;
      return this;
   }

   public void tickExplore() {
      if (this.z != null) {
         if (!this.isContainerStillValid(this.z)) {
            this.z = null;
         } else {
            InvSubHelperV var1 = ChestHistory.INSTANCE.PU(this.z);
            if (var1 != null) {
               this.analysisContainer(this.z, var1.d());
               this.z = null;
            } else if (a.currentScreen instanceof HandledScreen var3
               && var3 instanceof TileInventory var4
               && Objects.equals(var4.getContainerPosition(), this.z)) {
               this.analysisContainer(this.z, InventoryUtils.i(var3));
               var3.close();
               this.z = null;
            } else {
               this.pathToOrNearStop(this.z.vO().YO(), 1.5);
               if (InteractExtra.INSTANCE.fC(a.player.getPos(), this.z.vO().YO())) {
                  this.x.b(5, () -> Interact.INSTANCE.Sr(this.z.vO().YO()));
               }
            }
         }
      }
   }

   public void pathToOrNearStop(BlockPos pos, double distance) {
      this.o.sumitGoal(pathToOrNearStopGoal(pos, distance));
   }

   public boolean y() {
      return this.o.isPathing();
   }

   public void C(Event<MatrixStack> event) {
      if (SchedularSettings.INSTANCE.enableRender.get()) {
         RenderUtils.startDrawVirtual((MatrixStack)event.b);

         try {
            this.v.a((MatrixStack)event.b);
         } finally {
            RenderUtils.stopDrawVirtual((MatrixStack)event.b);
         }
      }
   }

   public HashSet<ContainerPosition> P() {
      return this.s;
   }

   public int update(StateMachine machine, int state) {
      if (!this.p) {
         machine.e();
         return HackUtilHelperI.Tc.ordinal();
      } else {
         return state;
      }
   }

   private boolean isContainerStillValid(ContainerPosition pos) {
      BlockPos var2 = pos.vO().YO();
      BlockEntity var3 = a.world.getBlockEntity(var2);
      return var3 instanceof BarrelBlockEntity || var3 instanceof ChestBlockEntity || var3 instanceof ShulkerBoxBlockEntity;
   }

   public PathingSchedular K(@Nullable BiPredicate<ContainerPosition, Inventory> replenishmentSourcePredicate) {
      this.l = replenishmentSourcePredicate;
      return this;
   }

   public void c() {
      this.n.c(HackUtilHelperI.Tc.ordinal());
   }

   @Nullable
   private static BlockPos findNearbyStandableStop(BlockPos targetPos) {
      if (a.world != null && a.player != null) {
         int var1 = Math.min(a.world.getTopY() - 2, targetPos.getY() + 6);
         int var2 = a.world.getBottomY();
         if (var1 < var2) {
            return null;
         } else {
            Mutable var3 = new Mutable();
            Mutable var4 = new Mutable();
            double var5 = Double.POSITIVE_INFINITY;
            BlockPos var7 = null;

            for (int var8 = 0; var8 <= 4; var8++) {
               for (int var9 = -var8; var9 <= var8; var9++) {
                  for (int var10 = -var8; var10 <= var8; var10++) {
                     if (Math.max(Math.abs(var9), Math.abs(var10)) == var8) {
                        int var11 = targetPos.getX() + var9;
                        int var12 = targetPos.getZ() + var10;

                        for (int var13 = var1; var13 >= var2; var13--) {
                           var3.set(var11, var13, var12);
                           var4.set(var11, var13 + 1, var12);
                           if (isValidLandingSpot(var3, var4)) {
                              double var14 = var4.toCenterPos().squaredDistanceTo(targetPos.toCenterPos()) + var8 * 0.01;
                              if (var14 < var5) {
                                 var5 = var14;
                                 var7 = var4.toImmutable();
                              }
                              break;
                           }
                        }
                     }
                  }
               }

               if (var7 != null) {
                  return var7;
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   public void k(boolean bl) {
      this.y = null;
   }

   private boolean canUseToExpandStorage(ItemStack stack) {
      return SchedularSettings.INSTANCE.enableAutoExpandShulker.get() && this.isEmptyShulkerBox(stack)
         ? true
         : SchedularSettings.INSTANCE.enableAutoExpandChest.get() && stack.getItem() instanceof BlockItem var3 && var3.getBlock() instanceof ChestBlock;
   }

   private static boolean isPassableForPath(BlockPos pos) {
      if (a.world != null && a.player != null) {
         BlockState var1 = a.world.getBlockState(pos);
         return var1.isLiquid() ? false : var1.getCollisionShape(a.world, pos, ShapeContext.of(a.player)).isEmpty();
      } else {
         return false;
      }
   }

   public int onStateReplenish(StateMachine machine) {
      if (this.A != null && !this.isContainerStillValid(this.A)) {
         if (!this.A.isDouble()) {
            ContainerPosition var2 = ContainerPosition.resolve(a.world, this.A.vO().YO());
            if (var2.isDouble()) {
               this.s.add(var2);
            }
         }

         this.s.remove(this.A);
         this.A = null;
      }

      if (this.s != null && !this.s.isEmpty()) {
         if (this.A == null) {
            this.A = this.s.stream().min(Comparator.comparingDouble(s -> a.player.getPos().squaredDistanceTo(s.vF()))).orElseThrow();
         }

         BlockPos var8 = this.A.vO().YO();
         if (a.currentScreen instanceof TileInventory var4 && Objects.equals(var4.getContainerPosition(), this.A)) {
            HandledScreen var9 = var4.castHandled();
            if (this.i != null) {
               if (!this.i.test(var9)) {
                  this.s.remove(this.A);
                  this.A = null;
                  var9.close();
               }
            } else {
               Inventory var5 = InventoryUtils.i(var9);
               if (var5.isEmpty()) {
                  this.s.remove(this.A);
                  this.A = null;
                  var9.close();
               } else {
                  int var6 = SchedularSettings.INSTANCE.invSpeedLimit.get();

                  for (int var7 = 0; var7 < var5.size(); var7++) {
                     if (!var5.getStack(var7).isEmpty()) {
                        a.interactionManager.clickSlot(var9.getScreenHandler().syncId, var7, 0, SlotActionType.QUICK_MOVE, a.player);
                        if (--var6 == 0) {
                           break;
                        }
                     }
                  }
               }
            }
         } else {
            this.pathToOrNearStop(var8, 1.5);
            if (InteractExtra.INSTANCE.fC(this.q.getPos(), var8)) {
               this.x.b(5, () -> Interact.INSTANCE.Sr(var8));
            }
         }

         machine.e();
         return HackUtilHelperI.Tf.ordinal();
      } else {
         SchedularSettings.INSTANCE.amA();
         machine.e();
         if (SchedularSettings.INSTANCE.enableContainerHotReload.get()) {
            SchedularSettings.INSTANCE.amC();
            return HackUtilHelperI.Tc.ordinal();
         } else {
            return HackUtilHelperI.Tf.ordinal();
         }
      }
   }

   public PathingSchedular() {
      this.s = new HashSet<>();
      this.t = new HashSet<>();
      this.u = new HashSet<>();
      this.v = RenderCollectors.createBoxCollector(true, false, false);
      this.w = false;
      this.x = new TimerExecutor();
      this.o = SchedularSettings.INSTANCE.createEngine();
      this.n = new StateMachine(
         HackUtilHelperI.Tc.ordinal(),
         this::update,
         this::onStateNone,
         this::onStateInitializeAndExploreEnvironment,
         this::onStateReplenish,
         this::o,
         this::onStateDischarge,
         this::onStateExpandStorage
      );
      this.n.registerListener(HackUtilHelperI.Tc.ordinal(), this::onResetStateToNone);
      this.n.registerListener(HackUtilHelperI.Td.ordinal(), this::k);
   }

   public static void b(PathingSchedular schedular) {
      if (b != null && b.val() == schedular) {
         schedular.o.e();
         b = null;
      }
   }

   public static IPathGoal pathToOrNearStopGoal(BlockPos pos, double distance) {
      if (pos == null) {
         return null;
      } else {
         BlockPos var3 = findNearbyStandableStop(pos);
         if (var3 != null) {
            return var3.toCenterPos().squaredDistanceTo(a.player.getPos()) < MathUtils.a(distance) ? null : new GoalNear(var3, distance);
         } else {
            Box var4 = new Box(pos);
            return var4.squaredMagnitude(a.player.getEyePos()) < MathUtils.a(distance) ? null : new GoalNear(pos, distance);
         }
      }
   }

   public int o(StateMachine machine) {
      if (this.h != null && this.h.getAsBoolean()) {
         return HackUtilHelperI.Tg.ordinal();
      } else if (this.g != null && this.g.getAsBoolean()) {
         return HackUtilHelperI.Te.ordinal();
      } else {
         IPathGoal var2 = this.f != null ? this.f.get() : null;
         if (var2 != null) {
            this.o.sumitGoal(var2);
         } else if (this.w) {
            this.pathToOrNearStop(this.r, 1.0);
         } else {
            this.o.sumitGoal(null);
         }

         machine.e();
         return HackUtilHelperI.Tf.ordinal();
      }
   }

   public void A() {
      this.p = false;
      b(this);
      this.c();
   }

   public PathingSchedular M(@Nonnull HackUtilHelperD engine) {
      if (engine == null) {
         throw new NullPointerException("engine is marked non-null but is null");
      } else {
         this.o = engine;
         return this;
      }
   }

   public static void a(PathingSchedular schedular) {
      if (b != null && b.val() != schedular) {
         b.val().o.e();
         b = null;
      }

      b = new KalamaHelperHelperK<>(Tasks.b(), schedular);
      schedular.o.d();
   }

   public HashSet<ContainerPosition> T() {
      return this.u;
   }

   public int onStateDischarge(StateMachine machine) {
      if (this.B != null && !this.isContainerStillValid(this.B)) {
         if (!this.B.isDouble()) {
            ContainerPosition var2 = ContainerPosition.resolve(a.world, this.B.vO().YO());
            if (var2.isDouble()) {
               this.t.add(var2);
            }
         }

         this.t.remove(this.B);
         this.B = null;
      }

      Predicate<ItemStack> var14 = this.j != null ? this.j : stack -> !stack.isEmpty() && stack.getMaxDamage() == 0;
      Predicate<me.matl114.utils.collections.KalamaHelperHelperK<ItemStack>> var3 = entry -> entry.index() >= SchedularSettings.INSTANCE.hotbarProtect.get() && var14.test((ItemStack)entry.val());
      if (InventoryUtils.findInventory(a.player.getInventory(), var3, false) == null) {
         this.B = null;
         machine.e();
         return HackUtilHelperI.Tf.ordinal();
      } else {
         if (this.t != null && !this.t.isEmpty()) {
            if (this.B == null) {
               this.B = this.t
                  .stream()
                  .filter(
                     s -> {
                        InvSubHelperV var2x = ChestHistory.INSTANCE.PU(s);
                        if (var2x == null) {
                           return true;
                        } else {
                           Inventory var3x = var2x.d();
                           return InventoryUtils.G(var3x, Items.AIR) != null
                              ? true
                              : InventoryUtils.streamInventory(a.player.getInventory())
                                 .filter(var14)
                                 .anyMatch(
                                    sample -> InventoryUtils.findItem(
                                          var3x,
                                          stack -> stack.isEmpty()
                                             ? true
                                             : stack.getCount() < stack.getMaxCount() && ItemStack.areItemsAndComponentsEqual(stack, sample),
                                          true
                                       )
                                       != null
                                 );
                        }
                     }
                  )
                  .min(Comparator.comparingDouble(s -> a.player.getPos().squaredDistanceTo(s.vF())))
                  .orElse(null);
            }

            if (this.B != null) {
               BlockPos var4 = this.B.vO().YO();
               if (a.currentScreen instanceof TileInventory var6 && Objects.equals(var6.getContainerPosition(), this.B)) {
                  HandledScreen var15 = var6.castHandled();
                  Inventory var7 = InventoryUtils.i(var15);
                  Inventory var8 = InventoryUtils.k(var15);
                  int var9 = var7.size();
                  int var10 = SchedularSettings.INSTANCE.invSpeedLimit.get();
                  boolean var11 = false;

                  for (int var12 = 0; var12 < var8.size(); var12++) {
                     ItemStack var13 = var8.getStack(var12);
                     if (!var13.isEmpty()
                        && var14.test(var13)
                        && InventoryUtils.findItem(
                              var7, item -> item.isEmpty() || item.getCount() < item.getMaxCount() && ItemStack.areItemsAndComponentsEqual(item, var13), true
                           )
                           != null) {
                        a.interactionManager.clickSlot(var15.getScreenHandler().syncId, var12 + var9, 0, SlotActionType.QUICK_MOVE, a.player);
                        var11 = true;
                        if (--var10 == 0) {
                           break;
                        }
                     }
                  }

                  boolean var16 = InventoryUtils.findItem(var7, item -> item.isEmpty() || item.getCount() < item.getMaxCount(), true) != null;
                  if (var16) {
                     if (!var11) {
                        this.B = null;
                        var15.close();
                     }
                  } else {
                     this.t.remove(this.B);
                     this.B = null;
                     var15.close();
                  }
               } else {
                  this.pathToOrNearStop(var4, 1.5);
                  if (InteractExtra.INSTANCE.fC(this.q.getPos(), var4)) {
                     this.x.b(5, () -> Interact.INSTANCE.Sr(var4));
                  }
               }

               return HackUtilHelperI.Tg.ordinal();
            }

            SchedularSettings.INSTANCE.amB();
         } else {
            SchedularSettings.INSTANCE.amA();
         }

         machine.e();
         if (SchedularSettings.INSTANCE.enableAutoExpandChest.get() || SchedularSettings.INSTANCE.enableAutoExpandShulker.get()) {
            SchedularSettings.INSTANCE.amF();
            return HackUtilHelperI.Th.ordinal();
         } else if (SchedularSettings.INSTANCE.enableContainerHotReload.get()) {
            SchedularSettings.INSTANCE.amC();
            return HackUtilHelperI.Tc.ordinal();
         } else {
            return HackUtilHelperI.Tf.ordinal();
         }
      }
   }

   public boolean x() {
      return b == null || b.val() == this || b.index() < Tasks.b() - 10;
   }

   private void analysisContainer(ContainerPosition pos, Inventory handledScreen) {
      if (this.l != null && this.l.test(pos, handledScreen)) {
         this.s.add(pos);
      }

      if (this.m != null && this.m.test(pos, handledScreen)) {
         this.t.add(pos);
      } else {
         boolean var3 = false;

         for (ItemStack var5 : InventoryUtils.a(handledScreen)) {
            if (!var5.isEmpty()) {
               var3 = true;
               break;
            }
         }

         if (!var3) {
            this.t.add(pos);
         }
      }

      if (SchedularSettings.INSTANCE.enableAutoExpandShulker.get() && InventoryUtils.findItem(handledScreen, this::isEmptyShulkerBox, false) != null) {
         this.u.add(pos);
      }

      if (SchedularSettings.INSTANCE.enableAutoExpandChest.get() && InventoryUtils.G(handledScreen, Items.CHEST) != null) {
         this.u.add(pos);
      }
   }

   public int onStateExpandStorage(StateMachine machine) {
      if (this.C != null && !this.isContainerStillValid(this.C)) {
         this.u.remove(this.C);
         this.C = null;
      }

      if (this.D != null) {
         if (a.world.getBlockEntity(this.D) != null) {
            if (a.currentScreen instanceof TileInventory var12 && var12.getContainerPosition() != null && var12.getContainerPosition().contains(this.D)) {
               this.D = null;
               this.t.add(var12.getContainerPosition());
               machine.e();
               return HackUtilHelperI.Tg.ordinal();
            }

            this.x.b(5, () -> Interact.INSTANCE.Sr(this.D));
            return HackUtilHelperI.Th.ordinal();
         }

         this.D = null;
      }

      KalamaHelperHelperK var3 = InventoryUtils.p(this::canUseToExpandStorage, false, false);
      if (var3 != null) {
         boolean var10 = this.isEmptyShulkerBox((ItemStack)var3.val());
         Pair var15 = (var10 ? KitReplenish.INSTANCE.bJ() : KitReplenish.INSTANCE.bK(false)).findFirst().orElse(null);
         if (var15 != null) {
            Interact.INSTANCE.interactBlock((BlockHitResult)var15.getSecond());
            this.D = (BlockPos)var15.getFirst();
            machine.e();
         } else {
            this.o.sumitGoal(new GoalDirection(Direction.NORTH));
         }

         machine.e();
         return HackUtilHelperI.Th.ordinal();
      } else {
         if (this.C != null && !this.isContainerStillValid(this.C)) {
            this.u.remove(this.C);
            this.C = null;
         }

         if (this.u != null && !this.u.isEmpty()) {
            if (this.C == null) {
               this.C = this.u.stream().min(Comparator.comparingDouble(s -> s.vF().squaredDistanceTo(a.player.getPos()))).orElseThrow();
            }

            if (a.currentScreen instanceof TileInventory var2 && Objects.equals(var2.getContainerPosition(), this.C)) {
               HandledScreen var14 = var2.castHandled();
               Inventory var5 = InventoryUtils.i(var14);
               int var6 = var5.size();
               boolean var7 = false;

               for (int var8 = 0; var8 < var6; var8++) {
                  ItemStack var9 = var5.getStack(var8);
                  if (this.canUseToExpandStorage(var9)) {
                     var7 = true;
                     a.interactionManager.clickSlot(var14.getScreenHandler().syncId, var8, 0, SlotActionType.QUICK_MOVE, a.player);
                     break;
                  }
               }

               if (!var7) {
                  this.u.remove(this.C);
                  this.C = null;
               }
            } else {
               BlockPos var13 = this.C.vO().YO();
               this.pathToOrNearStop(var13, 1.5);
               if (InteractExtra.INSTANCE.fC(this.q.getPos(), var13)) {
                  this.x.b(5, () -> Interact.INSTANCE.Sr(var13));
               }
            }

            machine.e();
            return HackUtilHelperI.Th.ordinal();
         } else {
            SchedularSettings.INSTANCE.amA();
            machine.e();
            return HackUtilHelperI.Tf.ordinal();
         }
      }
   }

   public PathingSchedular F(@Nullable BooleanSupplier replenish) {
      this.g = replenish;
      return this;
   }

   public boolean V() {
      return this.w;
   }

   public PathingSchedular d(boolean bool) {
      this.o.setCanMine(bool);
      return this;
   }

   public int onStateInitializeAndExploreEnvironment(StateMachine machine) {
      if (this.z != null) {
         this.tickExplore();
         machine.e();
         return HackUtilHelperI.Td.ordinal();
      } else {
         if (this.y == null) {
            boolean var2 = false;
            if (this.g != null) {
               var2 = SchedularSettings.INSTANCE.replenish.get();
            }

            boolean var3 = false;
            if (this.h != null) {
               var3 = SchedularSettings.INSTANCE.discharge.get();
            }

            this.y = new HashSet<>();
            if (var2 || var3) {
               if (this.k != null) {
                  this.k.get().forEach(this.y::add);
               } else {
                  Vec3d var4 = this.q.getPos();
                  Vec3 var5 = SchedularSettings.INSTANCE.scannChestRange.get();
                  Box var6 = new Box(var4, var4).expand(var5.x(), var5.y(), var5.z());
                  Vec3d var7 = var6.getMinPos();
                  Vec3d var8 = var6.getMaxPos();
                  ChunkPos var9 = MathUtils.toChunkPos(var7);
                  ChunkPos var10 = MathUtils.toChunkPos(var8);

                  for (int var11 = var9.x; var11 <= var10.x; var11++) {
                     for (int var12 = var9.z; var12 <= var10.z; var12++) {
                        WorldChunk var13 = a.world.getChunkManager().getWorldChunk(var11, var12);
                        if (var13 != null) {
                           for (Entry var15 : ChunkAccess.of(var13).blockEntityEntries()) {
                              BlockPos var16 = (BlockPos)var15.getKey();
                              ContainerPosition var17 = ContainerPosition.resolve(a.world, var16);
                              if (this.isContainerStillValid(var17) && var6.intersects(var17.vG())) {
                                 this.y.add(var17);
                              }
                           }
                        }
                     }
                  }
               }
            }

            Iterator var18 = this.y.iterator();

            while (var18.hasNext()) {
               ContainerPosition var19 = (ContainerPosition)var18.next();
               InvSubHelperV var20 = ChestHistory.INSTANCE.PU(var19);
               if (var20 != null) {
                  var18.remove();
                  this.analysisContainer(var19, var20.d());
               }
            }
         }

         if (this.y.isEmpty()) {
            return HackUtilHelperI.Tf.ordinal();
         } else {
            this.z = this.y.stream().min(Comparator.comparingDouble(s -> s.vF().squaredDistanceTo(a.player.getPos()))).orElse(null);
            this.y.remove(this.z);
            return HackUtilHelperI.Td.ordinal();
         }
      }
   }

   public PathingSchedular I(@Nullable Predicate<ItemStack> dischargePlayerInventory) {
      this.j = dischargePlayerInventory;
      return this;
   }
}
