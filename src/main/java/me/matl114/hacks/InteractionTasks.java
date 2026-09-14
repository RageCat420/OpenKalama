package me.matl114.hacks;

import com.google.common.util.concurrent.Runnables;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.events.Listener;
import me.matl114.events.annotations.Modifiable;
import me.matl114.events.catchers.PacketCatcherImpl;
import me.matl114.hacks.api.ModuleGroup;
import me.matl114.hacks.api.ModuleManager;
import me.matl114.hacks.modules.HackModules;
import me.matl114.hacks.modules.ac.Disabler;
import me.matl114.hacks.modules.interact.Airplace;
import me.matl114.hacks.modules.interact.AutoClick;
import me.matl114.hacks.modules.interact.AutoEat;
import me.matl114.hacks.modules.interact.AutoPlate;
import me.matl114.hacks.modules.interact.AutoRide;
import me.matl114.hacks.modules.interact.AutoSlab;
import me.matl114.hacks.modules.interact.AutoSurround;
import me.matl114.hacks.modules.interact.AutoUse;
import me.matl114.hacks.modules.interact.BlockRotate;
import me.matl114.hacks.modules.interact.GuiInteract;
import me.matl114.hacks.modules.interact.Interact;
import me.matl114.hacks.modules.interact.InteractExtra;
import me.matl114.hacks.modules.interact.InteractManager;
import me.matl114.hacks.modules.interact.NoInteract;
import me.matl114.hacks.modules.interact.PrinterRewrite;
import me.matl114.hacks.modules.interact.Scaffold;
import me.matl114.hacks.modules.interact.TpInteract;
import me.matl114.hacks.modules.move.LegacySnapRotManager;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.managers.Configs$LegalInteractMode;
import me.matl114.managers.Tasks;
import me.matl114.utils.AttributeUtils;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.InteractUtils;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RaycastUtils;
import me.matl114.utils.collections.FlagEntry;
import net.minecraft.block.AmethystClusterBlock;
import net.minecraft.block.BellBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.EndRodBlock;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.LightningRodBlock;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.RotatedInfestedBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.WallHangingSignBlock;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.ChestType;
import net.minecraft.block.enums.SlabType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import me.matl114.events.Event;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.apache.commons.lang3.mutable.MutableObject;

public class InteractionTasks {
   private static AutoSlab q;
   private static AutoRide r;
   private static TpInteract j;
   private static AutoPlate p;
   private static InteractManager u;
   private static InteractExtra e;
   private static AutoUse t;
   private static Entity b = null;
   public static MinecraftClient a = MinecraftClient.getInstance();
   private static BlockRotate m;
   private static int c = -1;
   private static Airplace k;
   private static NoInteract o;
   private static AutoSurround l;
   private static AutoEat s;
   @Modifiable
   public static final ModuleGroup d = new ModuleGroup("Interaction");
   private static AutoClick g;
   private static PrinterRewrite n;
   private static GuiInteract f;
   private static Interact h;
   private static Scaffold i;

   public static void d(Vec3d look3d, Vec3d eyePos, Runnable callback) {
      ClientPlayerAccess.of(a.player).getLegalMovementManager().i(new KalamaHelperHelperJ(look3d, eyePos, callback));
   }

   public static void i(Configs$LegalInteractMode mode, Vec3d targetCenter, List<Pair<BlockHitResult, Hand>> resultList, boolean swingHand) {
      switch (mode) {
         case USEITEM_PACKET:
            Vec2f var12 = EntityUtils.q(targetCenter.subtract(a.player.getEyePos()).normalize());
            int var15 = -1;

            for (Pair var21 : resultList) {
               Hand var23 = (Hand)var21.getRight();
               BlockHitResult var9 = (BlockHitResult)var21.getLeft();
               if (var15 == -1) {
                  var15 = InventoryUtils.getSelectedSlot();
                  a.interactionManager.sendSequencedPacket(a.world, i -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, i, var12.y, var12.x));
               } else {
                  h();
               }

               b(var23, var9, swingHand);
            }
            break;
         case DELAY_MOVEMENT:
            int var11 = -1;

            for (Pair var17 : resultList) {
               if (var11 == -1) {
                  var11 = InventoryUtils.getSelectedSlot();
               } else {
                  h();
               }

               Hand var20 = (Hand)var17.getRight();
               BlockHitResult var22 = (BlockHitResult)var17.getLeft();
               b(var20, var22, swingHand);
            }

            d(targetCenter, a.player.getEyePos(), Runnables.doNothing());
         case MOVEMENT_POST:
         default:
            break;
         case LEGACY_SLIENT_ROT:
            int var10 = -1;

            for (Pair var16 : resultList) {
               if (var10 == -1) {
                  var10 = InventoryUtils.getSelectedSlot();
               } else {
                  h();
               }

               Hand var19 = (Hand)var16.getRight();
               BlockHitResult var8 = (BlockHitResult)var16.getLeft();
               LegacySnapRotManager.INSTANCE.ahs(var8.getBlockPos().toCenterPos().subtract(a.player.getEyePos()).normalize(), false);
               b(var19, var8, swingHand);
            }
            break;
         case NONE:
            for (Pair var5 : resultList) {
               Hand var6 = (Hand)var5.getRight();
               BlockHitResult var7 = (BlockHitResult)var5.getLeft();
               b(var6, var7, swingHand);
            }
      }
   }

   public static TpInteract H() {
      return j;
   }

   public static Airplace I() {
      return k;
   }

   @Nonnull
   public static List<FlagEntry<BlockHitResult>> p(
      Vec3d playerPos, BlockPos blockPos, Direction preferredDirection, boolean enableAirPlace, boolean enablePositionPlace
   ) {
      ArrayList<FlagEntry<BlockHitResult>> var5 = new ArrayList<>();
      Direction var6 = preferredDirection;
      ArrayList<Direction> var7 = new ArrayList<>();
      var7.add(preferredDirection);

      for (Direction var11 : new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST}) {
         if (var11 != var6) {
            var7.add(var11);
         }
      }

      Vec3d var17 = blockPos.toCenterPos();
      if (enableAirPlace && !var7.isEmpty()) {
         Direction var18 = (Direction)var7.get(0);
         Vec3d var20 = var17.offset(var18, 0.5);
         var5.add(new FlagEntry<>(false, new BlockHitResult(var20, var18.getOpposite(), blockPos, false)));
      }

      Object var19 = null;

      for (Direction var22 : var7) {
         Vec3d var12 = var17.offset(var22, 0.5);
         Vec3d var13 = var17.offset(var22, 1.0);
         BlockPos var14 = BlockPos.ofFloored(var13);
         BlockState var15 = a.world.getBlockState(var14);
         if (!var15.isAir() && !var15.isLiquid()) {
            boolean var16 = InteractUtils.s(a.world, a.player, var14, var15);
            if (j(var14, playerPos)) {
               var5.add(new FlagEntry<>(var16, new BlockHitResult(var12, var22.getOpposite(), var14, true)));
            } else if (enablePositionPlace || k(blockPos, var22.getOpposite(), playerPos)) {
               var5.add(new FlagEntry<>(var16, new BlockHitResult(var12, var22.getOpposite(), var14, false)));
            }
         }
      }

      return var5;
   }

   private static Vec3d e(BlockPos pos, Vec3d bestEyePos) {
      Vec3d var2 = pos.toCenterPos();
      Box var3 = new Box(pos);
      Vec3d var4 = var2.subtract(bestEyePos).normalize().multiply(e.getBlockReachDistance() - 0.09178);
      if (var3.raycast(bestEyePos, bestEyePos.add(var4)).isPresent()) {
         return var4.normalize();
      } else {
         Box var5 = var3.expand(-1.0E-7, -1.0E-7, -1.0E-7);
         Vec3d var6 = MathUtils.magnitudePoint(var5, bestEyePos);
         return var6.subtract(bestEyePos).normalize();
      }
   }

   private static void A(ModuleManager m) {
      e = new InteractExtra().register(m);
      f = new GuiInteract().register(m);
      g = new AutoClick().register(m);
      h = new Interact().register(m);
      i = new Scaffold().register(m);
      j = new TpInteract().register(m);
      k = new Airplace().register(m);
      l = new AutoSurround().register(m);
      InteractionTasks.m = new BlockRotate().register(m);
      n = new PrinterRewrite().register(m);
      p = new AutoPlate().register(m);
      q = new AutoSlab().register(m);
      o = new NoInteract().register(m);
      r = new AutoRide().register(m);
      s = new AutoEat().register(m);
      t = new AutoUse().register(m);
      u = new InteractManager().register(m);
   }

   public static GuiInteract D() {
      return f;
   }

   public static Scaffold G() {
      return i;
   }

   public static void a() {
   }

   private static List<Vec3d> v(BlockPos pos, BlockState state, Direction side) {
      ArrayList<Vec3d> var3 = new ArrayList<>();

      for (Box var5 : state.getOutlineShape(a.world, pos).getBoundingBoxes()) {
         Box var6 = var5.offset(pos).expand(-1.0E-7, -1.0E-7, -1.0E-7);
         if (!(var6.getLengthX() <= 0.0) && !(var6.getLengthY() <= 0.0) && !(var6.getLengthZ() <= 0.0)) {
            w(var3, var6, side);
         }
      }

      return var3;
   }

   public static void f(Configs$LegalInteractMode mode, BlockHitResult result, Hand hand) {
      g(mode, result, hand, true);
   }

   public static AutoPlate N() {
      return p;
   }

   public static AutoSlab O() {
      return q;
   }

   public static void g(Configs$LegalInteractMode mode, BlockHitResult result, Hand hand, boolean swingHand) {
      Vec3d var4 = InteractExtra.INSTANCE.fG(a.player.getPos(), result);
      switch (mode) {
         case USEITEM_PACKET:
            Vec2f var8 = EntityUtils.q(e(result.getBlockPos(), var4).normalize());
            a.interactionManager.sendSequencedPacket(a.world, i -> new PlayerInteractItemC2SPacket(hand, i, var8.y, var8.x));
            b(hand, result, swingHand);
            break;
         case DELAY_MOVEMENT:
            b(hand, result, swingHand);
            d(result.getBlockPos().toCenterPos(), var4, Runnables.doNothing());
            break;
         case MOVEMENT_POST:
            MutableObject var7 = new MutableObject();
            Listener.B(new PacketCatcherImpl<>(PlayerInteractBlockC2SPacket.class, (Event<PlayerInteractBlockC2SPacket> eve) -> {
               if (eve.d()) {
                  return true;
               } else {
                  var7.setValue(eve.b);
                  eve.cancel();
                  return true;
               }
            }));
            b(hand, result, swingHand);
            if (var7.getValue() != null) {
               PlayerInteractBlockC2SPacket var6 = (PlayerInteractBlockC2SPacket)var7.getValue();
               d(result.getBlockPos().toCenterPos(), var4, () -> a.getNetworkHandler().sendPacket(var6));
            }
            break;
         case LEGACY_SLIENT_ROT:
            Vec2f var5 = EntityUtils.q(e(result.getBlockPos(), var4));
            LegacySnapRotManager.INSTANCE.snapAt(var5.x, var5.y, false);
            b(hand, result, swingHand);
            break;
         case NONE:
            b(hand, result, swingHand);
      }
   }

   public static ModuleGroup B() {
      return d;
   }

   private static void y(PlayerInteractEntityC2SPacket packet) {
      if (packet.type.getType().name().equals("INTERACT")) {
         b = a.world.getEntityById(packet.entityId);
         c = Tasks.b();
      }
   }

   private static void w(List<Vec3d> points, Box box, Direction side) {
      double var3 = box.minX;
      double var5 = (box.minX + box.maxX) * 0.5;
      double var7 = box.maxX;
      double var9 = box.minY;
      double var11 = (box.minY + box.maxY) * 0.5;
      double var13 = box.maxY;
      double var15 = box.minZ;
      double var17 = (box.minZ + box.maxZ) * 0.5;
      double var19 = box.maxZ;
      switch (side) {
         case DOWN:
            x(points, box.minY, var3, var5, var7, var15, var17, var19, Axis.Y, true);
            break;
         case UP:
            x(points, box.maxY, var3, var5, var7, var15, var17, var19, Axis.Y, true);
            break;
         case NORTH:
            x(points, box.minZ, var3, var5, var7, var9, var11, var13, Axis.Z, false);
            break;
         case SOUTH:
            x(points, box.maxZ, var3, var5, var7, var9, var11, var13, Axis.Z, false);
            break;
         case WEST:
            x(points, box.minX, var9, var11, var13, var15, var17, var19, Axis.X, false);
            break;
         case EAST:
            x(points, box.maxX, var9, var11, var13, var15, var17, var19, Axis.X, false);
      }
   }

   public static FlagEntry<BlockHitResult> l(BlockPos blockPos, boolean enableAirPlace, boolean enablePositionPlace) {
      return o(a.player.getPos(), blockPos, a.player.getFacing(), enableAirPlace, enablePositionPlace);
   }

   private static FlagEntry<BlockHitResult> s(
      Direction preferredDirection, BlockPos placeTargetBlock, BlockState currentState, BlockState targetState, boolean enablePositionPlace
   ) {
      if (currentState != null && targetState != null && !currentState.isAir() && !currentState.isLiquid()) {
         Vec3d var5 = a.player.getPos();
         ArrayList<Direction> var6 = new ArrayList<>(6);
         Direction var7 = preferredDirection.getOpposite();
         var6.add(var7);

         for (Direction var11 : new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST}) {
            if (var11 != var7) {
               var6.add(var11);
            }
         }

         FlagEntry var18 = null;
         boolean var19 = j(placeTargetBlock, var5);

         for (Direction var21 : var6) {
            Vec3d var12 = t(placeTargetBlock, currentState, targetState, var21);
            BlockHitResult var13 = new BlockHitResult(var12, var21, placeTargetBlock, false);
            ItemPlacementContext var14 = new ItemPlacementContext(a.player, Hand.MAIN_HAND, new ItemStack(targetState.getBlock().asItem()), var13);
            if (var14.canReplaceExisting() && (var19 || enablePositionPlace || !k(placeTargetBlock, var21, var5))) {
               var13 = new BlockHitResult(var12, var21, placeTargetBlock, var19);
               BlockState var15 = InteractUtils.getBlockPlacement(targetState.getBlock(), a.player, a.world, var13);
               if (targetState.equals(var15)) {
                  boolean var16 = InteractUtils.s(a.world, a.player, placeTargetBlock, currentState);
                  FlagEntry var17 = new FlagEntry<>(var16, var13);
                  if (!var17.flag()) {
                     return var17;
                  }

                  if (var18 == null) {
                     var18 = var17;
                  }
               }
            }
         }

         return var18;
      } else {
         return null;
      }
   }

   public static AutoUse R() {
      return t;
   }

   public static AutoEat Q() {
      return s;
   }

   public static FlagEntry<BlockHitResult> o(
      Vec3d playerPos, BlockPos blockPos, Direction preferredDirection, boolean enableAirPlace, boolean enablePositionPlace
   ) {
      Direction var5 = preferredDirection;
      ArrayList<Direction> var6 = new ArrayList<>();
      var6.add(preferredDirection);

      for (Direction var10 : new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST}) {
         if (var10 != var5) {
            var6.add(var10);
         }
      }

      Vec3d var17 = blockPos.toCenterPos();
      if (enableAirPlace) {
         if (var6.isEmpty()) {
            return null;
         } else {
            Direction var19 = (Direction)var6.get(0);
            Vec3d var21 = var17.offset(var19, 0.5);
            return new FlagEntry<>(false, new BlockHitResult(var21, var19.getOpposite(), blockPos, false));
         }
      } else {
         FlagEntry var18 = null;

         for (Direction var22 : var6) {
            Vec3d var11 = var17.offset(var22, 0.5);
            Vec3d var12 = var17.offset(var22, 1.0);
            BlockPos var13 = BlockPos.ofFloored(var12);
            BlockState var14 = a.world.getBlockState(var13);
            if (!var14.isAir() && !var14.isLiquid()) {
               boolean var15 = InteractUtils.s(a.world, a.player, var13, var14);
               if (j(var13, playerPos)) {
                  FlagEntry var23 = new FlagEntry<>(var15, new BlockHitResult(var11, var22.getOpposite(), var13, true));
                  if (!var23.flag()) {
                     return var23;
                  }

                  if (var18 == null) {
                     var18 = var23;
                  }
               } else if (enablePositionPlace || k(var13, var22.getOpposite(), playerPos)) {
                  FlagEntry var16 = new FlagEntry<>(var15, new BlockHitResult(var11, var22.getOpposite(), var13, false));
                  if (!var16.flag()) {
                     return var16;
                  }

                  if (var18 == null) {
                     var18 = var16;
                  }
               }
            }
         }

         return var18;
      }
   }

   public static NoInteract M() {
      return o;
   }

   public static boolean j(BlockPos targetPos, Vec3d playerPos) {
      Box var2 = Box.from(Vec3d.of(targetPos));
      return e.fB(playerPos).anyMatch(var2::contains);
   }

   public static FlagEntry<BlockHitResult> q(BlockPos placeTargetBlock, BlockState targetState, boolean enableAirPlace, boolean enablePositionPlace) {
      return r(a.player.getFacing(), placeTargetBlock, targetState, enableAirPlace, enablePositionPlace);
   }

   public static AutoRide P() {
      return r;
   }

   public static FlagEntry<BlockHitResult> r(
      Direction preferredDirection, BlockPos placeTargetBlock, BlockState targetState, boolean enableAirPlace, boolean enablePositionPlace
   ) {
      boolean var5 = a.player.shouldCancelInteraction();
      HashSet<Direction> var6 = new HashSet<>(List.of(Direction.values()));
      Block var7 = targetState.getBlock();
      BlockState var8 = a.world.getBlockState(placeTargetBlock);
      Vec3d var9 = placeTargetBlock.toCenterPos();
      Vec3d var10 = a.player.getPos();
      ArrayList<Direction> var11 = new ArrayList(6);
      FlagEntry var12 = s(preferredDirection, placeTargetBlock, var8, targetState, enablePositionPlace);
      if (var12 != null && var5 == var12.flag()) {
         return var12;
      } else {
         if (var7 instanceof StairsBlock) {
            BlockHalf var13 = (BlockHalf)targetState.get(StairsBlock.HALF);
            var11.add(var13 == BlockHalf.TOP ? Direction.UP : Direction.DOWN);
            var11.addAll(Arrays.asList(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST));

            for (Direction var15 : var11) {
               Vec3d var16 = var9.offset(var15, 0.5);
               Vec3d var17 = var9.offset(var15, 1.0);
               BlockPos var18 = BlockPos.ofFloored(var17);
               Vec3d var19 = var15 != Direction.DOWN && var15 != Direction.UP ? var16.add(0.0, 0.25 * (var13 == BlockHalf.TOP ? 1 : -1), 0.0) : var16;
               if (enableAirPlace) {
                  return new FlagEntry<>(false, new BlockHitResult(var19, var15.getOpposite(), placeTargetBlock, false));
               }

               BlockState var20 = a.world.getBlockState(var18);
               if (!var20.isAir() && !var20.isLiquid()) {
                  boolean var21 = InteractUtils.s(a.world, a.player, var18, var20);
                  if (j(var18, var10)) {
                     FlagEntry var86 = new FlagEntry<>(var21, new BlockHitResult(var19, var15.getOpposite(), var18, true));
                     if (var5 == var86.flag()) {
                        return var86;
                     }

                     if (var12 == null) {
                        var12 = var86;
                     }
                  } else if (enablePositionPlace || k(var18, var15.getOpposite(), var10)) {
                     FlagEntry var22 = new FlagEntry<>(var21, new BlockHitResult(var19, var15.getOpposite(), var18, false));
                     if (var5 == var22.flag()) {
                        return var22;
                     }

                     if (var12 == null) {
                        var12 = var22;
                     }
                  }
               }
            }
         } else if (var7 instanceof SlabBlock) {
            SlabType var28 = (SlabType)targetState.get(SlabBlock.TYPE);
            byte var31;
            if (var28 == SlabType.DOUBLE) {
               var11.add(Direction.UP);
               var11.add(Direction.DOWN);
               var31 = 0;
            } else if (var28 == SlabType.TOP) {
               var11.add(Direction.UP);
               var31 = 1;
            } else if (var28 == SlabType.BOTTOM) {
               var11.add(Direction.DOWN);
               var31 = -1;
            } else {
               var31 = 0;
            }

            var11.addAll(Arrays.asList(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST));

            for (Direction var53 : var11) {
               Vec3d var59 = var9.offset(var53, 0.5);
               Vec3d var65 = var9.offset(var53, 1.0);
               BlockPos var71 = BlockPos.ofFloored(var65);
               BlockState var76 = a.world.getBlockState(var71);
               if (!var76.isOf(targetState.getBlock())
                  || var76.get(SlabBlock.TYPE) == SlabType.DOUBLE
                  || var76.get(SlabBlock.TYPE) == targetState.get(SlabBlock.TYPE)) {
                  Vec3d var81 = var53 != Direction.DOWN && var53 != Direction.UP ? var59.add(0.0, 0.25 * var31, 0.0) : var59;
                  if (enableAirPlace) {
                     return new FlagEntry<>(false, new BlockHitResult(var81, var53.getOpposite(), placeTargetBlock, false));
                  }

                  if (!var76.isAir() && !var76.isLiquid()) {
                     boolean var87 = InteractUtils.s(a.world, a.player, var71, var76);
                     if (j(var71, var10)) {
                        FlagEntry var94 = new FlagEntry<>(var87, new BlockHitResult(var81, var53.getOpposite(), var71, true));
                        if (var5 == var94.flag()) {
                           return var94;
                        }

                        if (var12 == null) {
                           var12 = var94;
                        }
                     } else if (enablePositionPlace || k(var71, var53.getOpposite(), var10)) {
                        FlagEntry var23 = new FlagEntry<>(var87, new BlockHitResult(var81, var53.getOpposite(), var71, false));
                        if (var5 == var23.flag()) {
                           return var23;
                        }

                        if (var12 == null) {
                           var12 = var23;
                        }
                     }
                  }
               }
            }
         } else if (var7 instanceof TrapdoorBlock) {
            BlockHalf var29 = (BlockHalf)targetState.get(TrapdoorBlock.HALF);
            if (var29 == BlockHalf.BOTTOM) {
               var11.add(Direction.DOWN);
               var6.remove(Direction.UP);
            } else {
               var11.add(Direction.UP);
               var6.remove(Direction.DOWN);
            }

            var11.addAll(Arrays.asList(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST));

            for (Direction var44 : var11) {
               if (!var44.getAxis().isHorizontal() || targetState.get(TrapdoorBlock.FACING) == var44.getOpposite()) {
                  Vec3d var54 = var9.offset(var44, 0.5);
                  Vec3d var60 = var9.offset(var44, 1.0);
                  BlockPos var66 = BlockPos.ofFloored(var60);
                  BlockState var72 = a.world.getBlockState(var66);
                  Vec3d var77;
                  if (var44 != Direction.DOWN && var44 != Direction.UP) {
                     double var24 = var29 == BlockHalf.TOP ? 0.25 : -0.25;
                     var77 = var54.add(0.0, var24, 0.0);
                  } else {
                     var77 = var54;
                  }

                  if (enableAirPlace) {
                     return new FlagEntry<>(false, new BlockHitResult(var77, var44.getOpposite(), placeTargetBlock, false));
                  }

                  if (!var72.isAir() && !var72.isLiquid()) {
                     boolean var82 = InteractUtils.s(a.world, a.player, var66, var72);
                     if (j(var66, var10)) {
                        FlagEntry var89 = new FlagEntry<>(var82, new BlockHitResult(var77, var44.getOpposite(), var66, true));
                        if (var5 == var89.flag()) {
                           return var89;
                        }

                        if (var12 == null) {
                           var12 = var89;
                        }
                     } else if (enablePositionPlace || k(var66, var44.getOpposite(), var10)) {
                        FlagEntry var88 = new FlagEntry<>(var82, new BlockHitResult(var77, var44.getOpposite(), var66, false));
                        if (var5 == var88.flag()) {
                           return var88;
                        }

                        if (var12 == null) {
                           var12 = var88;
                        }
                     }
                  }
               }
            }
         } else {
            boolean var30 = false;
            if (var7 instanceof EndRodBlock) {
               Direction var41 = (Direction)targetState.get(EndRodBlock.FACING);
               var6.removeIf(dir -> dir != var41);
            } else if (!(var7 instanceof ChestBlock)) {
               if (var7 instanceof BellBlock) {
                  Direction var34 = (Direction)targetState.get(BellBlock.FACING);
                  if (var34.getAxis().isHorizontal()) {
                     Direction var46 = var34.getOpposite();
                     var6.removeIf(dir -> dir != var46);
                  }
               } else if (var7 instanceof LightningRodBlock) {
                  Direction var35 = (Direction)targetState.get(LightningRodBlock.FACING);
                  var6.removeIf(dir -> dir != var35);
               } else if (var7 instanceof ShulkerBoxBlock) {
                  Direction var36 = (Direction)targetState.get(ShulkerBoxBlock.FACING);
                  var6.removeIf(dir -> dir != var36);
               } else if (var7 instanceof HopperBlock) {
                  Direction var37 = (Direction)targetState.get(HopperBlock.FACING);
                  if (var37 == Direction.DOWN) {
                     var6.removeIf(dir -> dir != Direction.UP && dir != Direction.DOWN);
                  } else {
                     Direction var47 = var37.getOpposite();
                     var6.removeIf(dir -> dir != var47);
                  }
               } else if (var7 instanceof RotatedInfestedBlock) {
                  Axis var38 = (Axis)targetState.get(PillarBlock.AXIS);
                  var6.removeIf(dir -> dir.getAxis() != var38);
               } else if (var7 instanceof AmethystClusterBlock) {
                  Direction var39 = (Direction)targetState.get(AmethystClusterBlock.FACING);
                  var6.removeIf(dir -> dir != var39);
               } else if (!(var7 instanceof WallHangingSignBlock) && var7 instanceof WallMountedBlock) {
                  BlockFace var40 = (BlockFace)targetState.get(WallMountedBlock.FACE);
                  if (var40 == BlockFace.WALL) {
                     Direction var48 = (Direction)targetState.get(WallMountedBlock.FACING);
                     var6.removeIf(dir -> dir != var48);
                  } else {
                     Direction var49 = var40 == BlockFace.CEILING ? Direction.DOWN : Direction.UP;
                     var6.removeIf(direction -> direction != var49);
                  }
               }
            } else {
               ChestType var33 = (ChestType)targetState.get(ChestBlock.CHEST_TYPE);
               Direction var45 = (Direction)targetState.get(ChestBlock.FACING);
               BlockPos var55 = placeTargetBlock;
               if (a.player.shouldCancelInteraction()) {
                  if (var33 != ChestType.SINGLE) {
                     Direction var62 = var45.rotateYCounterclockwise();
                     Direction var68 = var45.rotateYClockwise();
                     boolean var74 = false;

                     for (Direction var96 : new Direction[]{var62, var68}) {
                        BlockPos var97 = var55.offset(var96);
                        BlockState var27 = a.world.getBlockState(var97);
                        if (var27.getBlock() instanceof ChestBlock
                           && var27.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE
                           && var27.get(ChestBlock.FACING) == var45) {
                           var74 = true;
                           break;
                        }
                     }

                     if (!var74) {
                        var33 = ChestType.SINGLE;
                     }
                  }

                  if (var33 == ChestType.SINGLE) {
                     var6.removeIf(side -> {
                        if (!side.getAxis().isHorizontal()) {
                           return false;
                        } else {
                           Direction var2 = side.getOpposite();
                           BlockPos var3 = var55.offset(var2);
                           BlockState var4 = a.world.getBlockState(var3);
                           if (!(var4.getBlock() instanceof ChestBlock)) {
                              return false;
                           } else if (var4.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE) {
                              return false;
                           } else {
                              Direction var5x = (Direction)var4.get(ChestBlock.FACING);
                              return var5x.getAxis() != side.getAxis();
                           }
                        }
                     });
                  } else {
                     var6.removeIf(side -> {
                        if (!side.getAxis().isHorizontal()) {
                           return true;
                        } else {
                           Direction var4 = side.getOpposite();
                           BlockPos var5x = var55.offset(var4);
                           BlockState var6x = a.world.getBlockState(var5x);
                           if (!(var6x.getBlock() instanceof ChestBlock)) {
                              return true;
                           } else if (var6x.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE) {
                              return true;
                           } else {
                              Direction var7x = (Direction)var6x.get(ChestBlock.FACING);
                              if (var7x.getAxis() == side.getAxis()) {
                                 return true;
                              } else {
                                 ChestType var8x = var7x.rotateYCounterclockwise() == side.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
                                 return var7x != var45 || var8x != var33;
                              }
                           }
                        }
                     });
                  }
               } else if (var33 == ChestType.SINGLE) {
                  Direction var61 = var45.rotateYCounterclockwise();
                  Direction var67 = var45.rotateYClockwise();
                  boolean var73 = false;

                  for (BlockPos var95 : new BlockPos[]{placeTargetBlock.offset(var61), placeTargetBlock.offset(var67)}) {
                     BlockState var26 = a.world.getBlockState(var95);
                     if (var26.getBlock() instanceof ChestBlock
                        && var26.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE
                        && var26.get(ChestBlock.FACING) == var45) {
                        var73 = true;
                        break;
                     }
                  }

                  if (var73) {
                     var30 = true;
                  }
               }
            }

            Direction var42 = preferredDirection;
            if (var6.contains(preferredDirection.getOpposite())) {
               var11.add(preferredDirection);
            }

            for (Direction var69 : new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST}) {
               if (var69 != var42 && var6.contains(var69.getOpposite())) {
                  var11.add(var69);
               }
            }

            if (enableAirPlace) {
               if (var11.isEmpty()) {
                  return null;
               }

               Direction var52 = (Direction)var11.get(0);
               Vec3d var58 = var9.offset(var52, 0.5);
               return new FlagEntry<>(var30, new BlockHitResult(var58, var52.getOpposite(), placeTargetBlock, false));
            }

            for (Direction var57 : var11) {
               Vec3d var64 = var9.offset(var57, 0.5);
               Vec3d var70 = var9.offset(var57, 1.0);
               BlockPos var75 = BlockPos.ofFloored(var70);
               BlockState var80 = a.world.getBlockState(var75);
               if (!var80.isAir() && !var80.isLiquid()) {
                  boolean var85 = InteractUtils.s(a.world, a.player, var75, var80);
                  if (j(var75, var10)) {
                     FlagEntry var93 = new FlagEntry<>(var85 || var30, new BlockHitResult(var64, var57.getOpposite(), var75, true));
                     if (var5 == var93.flag()) {
                        return var93;
                     }

                     if (var12 == null) {
                        var12 = var93;
                     }
                  } else if (enablePositionPlace || k(var75, var57.getOpposite(), var10)) {
                     FlagEntry var92 = new FlagEntry<>(var85 || var30, new BlockHitResult(var64, var57.getOpposite(), var75, false));
                     if (var5 == var92.flag()) {
                        return var92;
                     }

                     if (var12 == null) {
                        var12 = var92;
                     }
                  }
               }
            }
         }

         return var12;
      }
   }

   public static void c(PlayerEntity player, Entity entity, Hand hand, boolean swing) {
      ActionResult var4 = a.interactionManager.interactEntityAtLocation(a.player, entity, RaycastUtils.createRealHitResult(entity, player.getEyePos()), hand);
      if (!var4.isAccepted()) {
         var4 = a.interactionManager.interactEntity(player, entity, hand);
      }

      if (swing) {
         InteractUtils.swingHandIfSuccess(var4, hand);
      }
   }

   public static void b(Hand hand, BlockHitResult result, boolean swing) {
      Vec2f var3 = new Vec2f(a.player.getPitch(), a.player.getYaw());
      a.player.setPitch(PlayerStateManager.INSTANCE.jl);
      a.player.setYaw(PlayerStateManager.INSTANCE.jm);
      ActionResult var4 = a.interactionManager.interactBlock(a.player, hand, result);
      a.player.setPitch(var3.x);
      a.player.setYaw(var3.y);
      if (swing) {
         InteractUtils.swingHandIfSuccess(var4, hand);
      }
   }

   public static boolean k(BlockPos pos, Direction face, Vec3d playerPos) {
      Vec3d var3 = pos.toCenterPos().offset(face, 0.5);
      Vec3d var4 = Vec3d.of(face.getVector());
      return e.fB(playerPos).anyMatch(eye -> eye.subtract(var3).dotProduct(var4) > 0.0);
   }

   public static PrinterRewrite L() {
      return n;
   }

   public static FlagEntry<BlockHitResult> m(BlockPos blockPos, Direction preferredDirection, boolean enableAirPlace, boolean enablePositionPlace) {
      return o(a.player.getPos(), blockPos, preferredDirection, enableAirPlace, enablePositionPlace);
   }

   static {
      d.registerFactories(InteractionTasks::A);
      HackModules.registerModuleGroup(d);
      Listener.n(PlayerInteractEntityC2SPacket.class, InteractionTasks::y);
   }

   public static InteractExtra C() {
      return e;
   }

   public static void h() {
      Disabler.INSTANCE.ajA();
   }

   public static FlagEntry<Vec2f> u(Vec3d eyePos, BlockPos pos, BlockState targetState) {
      if (a.world != null && a.player != null) {
         boolean var3 = targetState.isLiquid() && targetState.getFluidState().isIn(FluidTags.WATER);
         boolean var4 = !targetState.isLiquid() && targetState.getFluidState().isIn(FluidTags.WATER);
         double var5 = AttributeUtils.getPlayerBlockInteractionRange(a.player);
         Direction var7 = a.player.getFacing().getOpposite();
         ArrayList var8 = new ArrayList();
         var8.add(var7);

         for (Direction var12 : new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST}) {
            if (var12 != var7) {
               var8.add(var12);
            }
         }

         FlagEntry var25 = null;
         Iterator var26 = var8.iterator();

         while (true) {
            Direction var13;
            boolean var14;
            BlockState var15;
            Direction var27;
            BlockPos var28;
            while (true) {
               if (!var26.hasNext()) {
                  return var25;
               }

               var27 = (Direction)var26.next();
               if (var4) {
                  var28 = pos;
                  var13 = var27;
                  var14 = false;
                  var15 = a.world.getBlockState(pos);
                  break;
               }

               if (var3) {
                  var28 = pos.offset(var27.getOpposite());
                  var13 = var27;
                  var15 = a.world.getBlockState(var28);
                  var14 = var15.getBlock() instanceof FluidFillable var17
                     && var17.canFillWithFluid(a.player, a.world, var28, var15, targetState.getFluidState().getFluid());
                  break;
               }
            }

            Vec3d var30 = var28.toCenterPos().subtract(eyePos);
            Vec3i var29 = var27.getVector();
            if (!(new Vec3d(var29.getX(), var29.getY(), var29.getZ()).dotProduct(var30) > 0.0) && !var15.isAir() && !var15.isLiquid()) {
               for (Vec3d var19 : v(var28, var15, var13)) {
                  Vec3d var20 = var19.subtract(eyePos);
                  if (!(var20.lengthSquared() < 1.0E-12) && !(var20.lengthSquared() > var5 * var5)) {
                     Vec2f var21 = EntityUtils.q(var20.normalize());
                     Vec3d var22 = EntityUtils.pitchYawToRotation(var21.x, var21.y);
                     BlockHitResult var23 = a.world
                        .raycast(new RaycastContext(eyePos, eyePos.add(var22.multiply(var5)), ShapeType.OUTLINE, FluidHandling.NONE, a.player));
                     if (var23.getType() == Type.BLOCK && var23.getBlockPos().equals(var28) && var23.getSide() == var13) {
                        FlagEntry var24 = new FlagEntry<>(var14, var21);
                        if (var24.flag() == a.player.isSneaking()) {
                           return var24;
                        }

                        if (var25 == null) {
                           var25 = var24;
                        }
                     }
                  }
               }
            }
         }
      } else {
         return null;
      }
   }

   public static Interact F() {
      return h;
   }

   public static Entity z(Predicate<Entity> targetBlock) {
      int var1 = Tasks.b();
      return var1 < c + 20 && b != null && b.isAlive() && targetBlock.test(b) ? b : RaycastUtils.rayTraceSpecificEntity(targetBlock).orElse(null);
   }

   public static AutoSurround J() {
      return l;
   }

   public static InteractManager S() {
      return u;
   }

   public static BlockRotate K() {
      return m;
   }

   public static FlagEntry<BlockHitResult> n(Vec3d playerPos, BlockPos blockPos, boolean enableAirPlace, boolean enablePositionPlace) {
      return o(playerPos, blockPos, a.player.getFacing(), enableAirPlace, enablePositionPlace);
   }

   public static AutoClick E() {
      return g;
   }

   private static Vec3d t(BlockPos placeTargetBlock, BlockState currentState, BlockState targetState, Direction side) {
      Vec3d var4 = placeTargetBlock.toCenterPos().offset(side, 0.5);
      if (currentState.getBlock() instanceof SlabBlock && currentState.isOf(targetState.getBlock()) && side.getAxis().isHorizontal()) {
         SlabType var5 = (SlabType)currentState.get(SlabBlock.TYPE);
         if (var5 == SlabType.BOTTOM) {
            return var4.add(0.0, 0.25, 0.0);
         } else {
            return var5 == SlabType.TOP ? var4.add(0.0, -0.25, 0.0) : var4;
         }
      } else {
         return var4;
      }
   }

   private static void x(
      List<Vec3d> points, double fixed, double minA, double midA, double maxA, double minB, double midB, double maxB, Axis axis, boolean horizontalPlane
   ) {
      if (horizontalPlane) {
         points.add(new Vec3d(midA, fixed, midB));
         points.add(new Vec3d(minA, fixed, midB));
         points.add(new Vec3d(maxA, fixed, midB));
         points.add(new Vec3d(midA, fixed, minB));
         points.add(new Vec3d(midA, fixed, maxB));
         points.add(new Vec3d(minA, fixed, minB));
         points.add(new Vec3d(minA, fixed, maxB));
         points.add(new Vec3d(maxA, fixed, minB));
         points.add(new Vec3d(maxA, fixed, maxB));
      } else {
         switch (axis) {
            case X:
               points.add(new Vec3d(fixed, midA, midB));
               points.add(new Vec3d(fixed, minA, midB));
               points.add(new Vec3d(fixed, maxA, midB));
               points.add(new Vec3d(fixed, midA, minB));
               points.add(new Vec3d(fixed, midA, maxB));
               points.add(new Vec3d(fixed, minA, minB));
               points.add(new Vec3d(fixed, minA, maxB));
               points.add(new Vec3d(fixed, maxA, minB));
               points.add(new Vec3d(fixed, maxA, maxB));
               break;
            case Z:
               points.add(new Vec3d(midA, midB, fixed));
               points.add(new Vec3d(minA, midB, fixed));
               points.add(new Vec3d(maxA, midB, fixed));
               points.add(new Vec3d(midA, minB, fixed));
               points.add(new Vec3d(midA, maxB, fixed));
               points.add(new Vec3d(minA, minB, fixed));
               points.add(new Vec3d(minA, maxB, fixed));
               points.add(new Vec3d(maxA, minB, fixed));
               points.add(new Vec3d(maxA, maxB, fixed));
         }
      }
   }
}
