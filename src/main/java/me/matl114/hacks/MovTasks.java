package me.matl114.hacks;

import net.minecraft.util.math.BlockPos;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.annotations.Modifiable;
import me.matl114.hacks.api.ModuleGroup;
import me.matl114.hacks.api.ModuleManager;
import me.matl114.hacks.modules.HackModules;
import me.matl114.hacks.modules.move.AntiChunkLag;
import me.matl114.hacks.modules.move.AntiLiquid;
import me.matl114.hacks.modules.move.AutoResync;
import me.matl114.hacks.modules.move.ElytraExtra;
import me.matl114.hacks.modules.move.ElytraFlight;
import me.matl114.hacks.modules.move.ElytraGrimAcc;
import me.matl114.hacks.modules.move.ElytraJump;
import me.matl114.hacks.modules.move.ElytraSlowFall;
import me.matl114.hacks.modules.move.Flight;
import me.matl114.hacks.modules.move.FloatingUtils;
import me.matl114.hacks.modules.move.ForwardTp;
import me.matl114.hacks.modules.move.LegacySnapRotManager;
import me.matl114.hacks.modules.move.MovExtra;
import me.matl114.hacks.modules.move.MovTest;
import me.matl114.hacks.modules.move.MoveTimer;
import me.matl114.hacks.modules.move.NoFall;
import me.matl114.hacks.modules.move.NoSlowDown;
import me.matl114.hacks.modules.move.PlayerInputManager;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.modules.move.SetBackLog;
import me.matl114.hacks.modules.move.Sprint;
import me.matl114.hacks.modules.move.StepHeight;
import me.matl114.hacks.modules.move.TargetCommand;
import me.matl114.hacks.modules.move.TpaCommand;
import me.matl114.hacks.modules.move.Travel;
import me.matl114.hacks.modules.move.Velocity;
import me.matl114.hacks.utils.entity.EntityMovementStatus;
import me.matl114.hacks.utils.entity.HackUtilHelperF;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.managers.Tasks;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.CollisionUtil;
import me.matl114.utils.Debug;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.commands.interruption.LogicalError;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.ArgumentType;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.commands.params.api.InputArgument;
import me.matl114.utils.commands.params.api.KalamaHelperHelperF;
import me.matl114.utils.commands.params.impl.EntityArgumentType;
import me.matl114.utils.commands.params.types.EntitySelector;
import me.matl114.versioned.api.VPacket;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.GameMode;

public class MovTasks {
   private static MoveTimer x;
   private static NoFall r;
   public static final KalamaHelperHelperMX i = new KalamaHelperHelperJX();
   public static Vec3d d;
   public static final KalamaHelperHelperF O = KalamaHelperHelperF.g(
      () -> MovTasks.a.crosshairTarget != null && MovTasks.a.crosshairTarget.getType() == Type.ENTITY
         ? Stream.of("@" + ((EntityHitResult)MovTasks.a.crosshairTarget).getEntity().getUuidAsString())
         : Stream.empty()
   );
   private static final double maxYDelta = 190.0;
   public static Map<String, KalamaHelperHelperA> Q = new LinkedHashMap<>();
   private static ElytraExtra z;
   private static PlayerStateManager m;
   private static ElytraGrimAcc B;
   public static final KalamaHelperHelperF L = KalamaHelperHelperF.g(MovTasks::ag);
   public static final KalamaHelperHelperF N = KalamaHelperHelperF.g(
      () -> MovTasks.a.world != null ? EntityUtils.getWorldPlayerNames(false).map(name -> "@" + name) : Stream.empty()
   );
   private static FloatingUtils F;
   public static Vec3d R = null;
   public static final ArgumentType<?> P = me.matl114.utils.commands.params.KalamaHelperHelperA.<EntityArgumentType, EntitySelector>b(EntityArgumentType::new)
      .B("target")
      .c(N)
      .c(O)
      .v();
   public static final ArgumentType<String> M = me.matl114.utils.commands.params.KalamaHelperHelperA.a().B("special_type").c(L).v();
   private static StepHeight y;
   private static Travel K;
   private static ElytraFlight A;
   private static final Random g = Random.create();
   private static final ThreadLocal<Boolean> f = ThreadLocal.withInitial(() -> Boolean.FALSE);
   private static ElytraSlowFall G;
   public static final KalamaHelperHelperMX h = new KalamaHelperHelperR();
   private static Flight v;
   private static NoSlowDown q;
   public static final HackUtilHelperF j = new KalamaHelperHelperKX(0);
   private static ForwardTp p;
   private static AntiLiquid D;
   private static AutoResync t;
   public static final MinecraftClient a = MinecraftClient.getInstance();
   private static Velocity E;
   public static final ModuleGroup k = new ModuleGroup("Move");
   private static MovTest H;
   private static ElytraJump C;
   private static MovExtra l;
   public static boolean doingTp = false;
   private static SetBackLog s;
   private static LegacySnapRotManager o;
   public static Vec3d e;
   private static TpaCommand I;
   private static TargetCommand J;
   private static Sprint w;
   private static AntiChunkLag u;
   private static PlayerInputManager n;

   public static MovTest aF() {
      return H;
   }

   @Modifiable
   public static void Y() {
      ar().alV(Optional.empty());
   }

   public static MovExtra aj() {
      return l;
   }

   private static int validMovToEntity(KalamaHelperHelperMX engin, Vec3d currentPos, Vec3d currentTry, Box to, double avRS, List<Vec3d> vec) {
      Vec3d var7 = engin.simulateMovement(a.player, currentPos, currentTry);
      Vec3d var8 = currentPos.add(var7);
      Vec3d var9 = Vec3d.ZERO.subtract(var7);
      Vec3d var10 = engin.simulateMovement(a.player, var8, var9);
      if (validMovementAsServer(var9, var10)) {
         if (to.squaredMagnitude(var8) < avRS && !engin.checkEnvironmentCollision(a.player, var8, false)) {
            vec.add(var8);
            return 2;
         }

         if (validMovementAsServer(currentTry, var7)) {
            Vec3d var11 = currentPos.add(currentTry);
            if (!engin.checkEnvironmentCollision(a.player, var11, false)) {
               vec.add(var11);
               return 1;
            }
         }
      }

      return 0;
   }

   public static PlayerInputManager al() {
      return n;
   }

   public static double searchFirstNoYConflictYHeight(
      double originY, List<Box> collisionsBB, List<VoxelShape> collisionShapes, double min, double max, boolean upOrDown
   ) {
      ArrayList<Box> var9 = new ArrayList(collisionsBB);

      for (VoxelShape var11 : collisionShapes) {
         var9.addAll(var11.getBoundingBoxes());
      }

      var9.sort(Comparator.comparingDouble(b -> b.minY * (upOrDown ? 1.0 : -1.0)));
      RenderTasks.g = true;
      RenderTasks.b = Color.BLUE;

      for (Box var21 : var9) {
         RenderTasks.debugBox(var21);
      }

      RenderTasks.g = false;
      if (upOrDown) {
         double var22 = originY + min;
         double var23 = a.player.dimensions.height() + 2.0E-7;

         for (Box var25 : var9) {
            if (var22 + var23 > var25.minY && var22 < var25.maxY) {
               if (!(var25.maxY < originY + max)) {
                  break;
               }

               var22 = var25.maxY;
            } else if (var22 + var23 <= var25.minY) {
               break;
            }
         }

         return var22 - originY;
      } else {
         double var12 = originY - min;
         double var14 = a.player.dimensions.height() + 2.0E-7;

         for (Box var17 : var9) {
            if (var12 < var17.maxY && var12 + var14 > var17.minY) {
               double var18 = var17.minY - var14;
               if (!(var18 > originY - max)) {
                  break;
               }

               var12 = var18;
            } else if (var12 >= var17.maxY) {
               break;
            }
         }

         return var12 - originY;
      }
   }

   public static boolean validMovementAsServer(Vec3d expect, Vec3d sim) {
      return a.interactionManager.getCurrentGameMode().isCreative() || MathUtils.a(expect.x - sim.x) + MathUtils.a(expect.z - sim.z) < 0.0625;
   }

   public static boolean M(Entity entity, Vec3d movement) {
      boolean var2 = aj().disableStepheightFeature.get();
      aj().disableStepheightFeature.set(false);
      f.set(false);
      RenderTasks.g = true;
      Vec3d var3 = collide(entity, movement);
      RenderTasks.g = false;
      boolean var4 = f.get();
      aj().disableStepheightFeature.set(var2);
      return var4;
   }

   @Modifiable
   public static void scheduleTpInternal(
      KalamaHelperHelperIX context, Vec3d target, double farawayThreshold, boolean command, boolean fastMode, boolean considerNoFall
   ) {
      if (a.player != null) {
         if (command) {
            Debug.chat("正在向", ChatUtils.getDisplayedLocation(target), "执行tp行为");
         }

         Vec3d var7 = (Vec3d)context.from().getValue();
         Vec3d var8 = target.subtract(var7);
         List var9 = z(var7, target, command, farawayThreshold, true);
         if (var9.size() == 2) {
            boolean var10 = var8.y < -4.0;
            Vec3d var11 = (Vec3d)var9.get(1);
            MovTasks$MovInfo var12 = new MovTasks$MovInfo(var11, var10 ? Boolean.FALSE : null, true, null);
            List var13 = List.of(var12);
            scheduleFarawayMoveInternal(var13, true, context, considerNoFall);
            if (considerNoFall) {
               ClientPlayerAccess.of(a.player).setForceNoFall(true);
            }
         } else {
            if (var9.size() != 4) {
               return;
            }

            if (command) {
               Debug.b("执行TP序列");
            }

            Vec3d var17 = (Vec3d)var9.get(0);
            Vec3d var18 = (Vec3d)var9.get(1);
            Vec3d var19 = (Vec3d)var9.get(2);
            Vec3d var20 = (Vec3d)var9.get(3);
            boolean var14 = var17.y > var18.y + 4.0;
            boolean var15 = var19.y > var20.y + 4.0;
            context.from().setValue(var17);
            a.player.setOnGround(false);
            if (fastMode) {
               ArrayList var16 = new ArrayList();
               var16.add(MovTasks$MovInfo.adA(var18));
               if (var14) {
                  var16.add(MovTasks$MovInfo.adz(var18));
               }

               var16.add(MovTasks$MovInfo.adz(var19));
               var16.add(MovTasks$MovInfo.adA(var20));
               if (var15) {
                  var16.add(MovTasks$MovInfo.adz(var20));
               }

               scheduleFarawayMoveInternal(var16, true, context, considerNoFall);
            } else {
               context.from().setValue(var17);
               scheduleFarawayMoveInternal(List.of(MovTasks$MovInfo.adA(var18)), false, context, true);
               doingTp = true;
               Tasks.l(() -> {
                  doingTp = false;
                  scheduleFarawayMoveInternal(List.of(MovTasks$MovInfo.adz(var19)), false, context.mS(), false);
                  doingTp = true;
               }, 2);
               Tasks.l(() -> {
                  doingTp = false;
                  scheduleFarawayMoveInternal(List.of(var15 ? MovTasks$MovInfo.adA(var20) : MovTasks$MovInfo.adz(var20)), false, context.mS(), false);
                  if (considerNoFall && var15 && a.player != null) {
                     ClientPlayerAccess.of(a.player).setForceNoFall(true);
                     a.player.setOnGround(false);
                  }
               }, 3);
            }
         }

         if (command) {
            Debug.b("tp行为已经执行, 若出现回弹或者位置不变,则目标位置不可达");
         }
      }
   }

   @Modifiable
   public static List<Vec3d> generateTpSequence(KalamaHelperHelperIX context, Vec3d target, boolean considerEnvironment) {
      return z((Vec3d)context.from().getValue(), target, false, 200.0, considerEnvironment);
   }

   public static Vec3d resolveCommandSpecialPositions(String type, ArgumentReader reader, PlayerEntity var1, Consumer<Text> errMsg) {
      if (type.startsWith("#")) {
         String var4 = type.substring(1);
         if (Q.containsKey(var4)) {
            KalamaHelperHelperA var5 = Q.get(var4);
            return var5.resolvePosition(reader, var1, errMsg);
         }

         errMsg.accept(Text.literal("不存在这样的特殊位置: " + type).formatted(Formatting.RED));
      }

      return null;
   }

   private static boolean C(PlayerMoveC2SPacket packet) {
      return !doingTp;
   }

   public static TargetCommand aH() {
      return J;
   }

   @Modifiable
   public static List<MovTasks$MovInfo> l(List<Vec3d> vec3ds) {
      return vec3ds.stream().map(MovTasks::j).collect(Collectors.toCollection(ArrayList::new));
   }

   public static Optional<Vec3d> ab(CommandExecution p, List<InputArgument<?>> args, ArgumentReader reader, Consumer<Text> errMsg) {
      KalamaHelperHelperEX var4 = new KalamaHelperHelperEX(reader, p, args, errMsg);
      me.matl114.events.impl.KalamaHelperHelperI var5 = new me.matl114.events.impl.KalamaHelperHelperI<>(KalamaHelperHelperZX.class, var4);
      Listener.bx().broadcast(var5);
      return var5.b() instanceof KalamaHelperHelperEX var7 && var7.hasResolved() ? var7.a : null;
   }

   public static double searchFirstNoCollisionSpaceYHeight(Vec3d origin, double min, double max, boolean upOrDown) {
      Box var6 = a.player.dimensions.getBoxAt(origin);
      double var7 = upOrDown ? min : -max;
      double var9 = upOrDown ? max : -min;
      Box var11 = CollisionUtil.resetY(var6, origin.y + var7, origin.y + var9);
      RenderTasks.g = true;
      RenderTasks.b = Color.RED;
      RenderTasks.debugBox(var11);
      RenderTasks.g = false;
      ArrayList var12 = new ArrayList();
      ArrayList var13 = new ArrayList();
      CollisionUtil.getCollisions(a.world, a.player, var11, var13, var12, 4, null, null, null);
      return searchFirstNoYConflictYHeight(origin.y, var12, var13, min, max, upOrDown);
   }

   @Modifiable
   public static List<MovTasks$MovInfo> createMovInfoList(List<Vec3d> vec3ds, boolean onGround, boolean updateplayerpos, Vec2f rotation) {
      return vec3ds.stream().map(v -> createMovInfo(v, onGround, updateplayerpos, rotation)).collect(Collectors.toCollection(ArrayList::new));
   }

   @Modifiable
   public static boolean checkEnvironmentCollision(Entity entity, Vec3d pos, boolean checkLiquid, boolean ignoreChunkBorder) {
      ArrayList var4 = new ArrayList();
      ArrayList var5 = new ArrayList();
      Box var6 = entity.getBoundingBox();
      Box var7 = entity.dimensions.getBoxAt(pos);
      BiFunction<BlockState, BlockPos, Box> var8 = checkLiquid ? (BlockState blockstate, BlockPos blockpos) -> {
         if (blockstate != null) {
            Block var4x = blockstate.getBlock();
            if (var4x == Blocks.LAVA || var4x == Blocks.SOUL_FIRE || var4x == Blocks.FIRE) {
               Box var5x = Box.enclosing(blockpos, blockpos);
               if (CollisionUtil.voxelShapeIntersectHorizontal(var5x, var7) || CollisionUtil.voxelShapeIntersectHorizontal(var5x, var6)) {
                  return var5x;
               }
            }
         }

         return null;
      } : null;
      CollisionUtil.getCollisions(entity.getWorld(), entity, var7, var5, var4, ignoreChunkBorder ? 4 : 6, null, null, var8);
      int var9 = 0;

      for (int var10 = var4.size(); var9 < var10; var9++) {
         Box var11 = (Box)var4.get(var9);
         if (!CollisionUtil.h(var11, var6)) {
            return true;
         }
      }

      var9 = 0;

      for (int var13 = var5.size(); var9 < var13; var9++) {
         VoxelShape var14 = (VoxelShape)var5.get(var9);
         if (!CollisionUtil.voxelShapeIntersectNoEmpty(var14, var6)) {
            return true;
         }
      }

      return false;
   }

   @Modifiable(
      optional = true
   )
   public static void farawayMove(Vec3d vec3d, boolean updatePlayer) {
      farawayMoveFromTo(a.player.getPos(), a.player.getPos().add(vec3d), null, updatePlayer);
   }

   public static void moveToWithPackets(Vec3d to, Boolean onGroundOverride) {
      Entity var2 = a.player.getRootVehicle();
      if (Objects.equals(to, a.player.getPos())) {
         if (a.player.hasVehicle()) {
            a.getNetworkHandler().sendPacket(VPacket.i(var2));
         } else {
            a.getNetworkHandler().sendPacket(VPacket.f(onGroundOverride != null ? onGroundOverride : a.player.isOnGround(), false));
         }
      } else {
         double var3 = var2.getY() - a.player.getY();
         var2.setPosition(to.add(0.0, var3, 0.0));
         a.player.setPosition(to);
         if (a.player.hasVehicle()) {
            a.getNetworkHandler().sendPacket(VPacket.i(var2));
         } else {
            a.getNetworkHandler()
               .sendPacket(VPacket.g(to.getX(), to.getY(), to.getZ(), onGroundOverride != null ? onGroundOverride : a.player.isOnGround(), false));
         }
      }
   }

   public static void Z(Event<ClientPlayerEntity> playerEvent) {
      ClientPlayerEntity var1 = (ClientPlayerEntity)playerEvent.e();
      LegalMovementManager var2 = ClientPlayerAccess.of(var1).getLegalMovementManager();
      j.SH(var1);
      var2.i(j);
   }

   @Modifiable
   public static MovTasks$MovInfo j(Vec3d to) {
      return MovTasks$MovInfo.adz(to);
   }

   public static List<String> ac(CommandExecution p, List<InputArgument<?>> args) {
      KalamaHelperHelperE var2 = new KalamaHelperHelperE(p, args);
      me.matl114.events.impl.KalamaHelperHelperI var3 = new me.matl114.events.impl.KalamaHelperHelperI<>(KalamaHelperHelperZX.class, var2);
      Listener.bx().broadcast(var3);
      return var3.b() instanceof KalamaHelperHelperE var5 ? var5.tab : List.of();
   }

   public static List<Vec3d> generateTpSequenceInternal(Vec3d current, Vec3d target, boolean command, double farawayTp, boolean considerEnvironment) {
      boolean var6 = checkEnvironmentCollision(a.player, target, false, true);
      if (var6) {
         if (command) {
            Debug.b("目标位置存在方块碰撞冲突, 无法执行tp");
         }

         return List.of();
      } else {
         Vec3d var7 = target.subtract(current);
         Vec3d var8 = simulateMovement(a.player, current, var7, true);
         if (validMovementAsServer(var7, var8)) {
            return List.of(current, current.add(var7));
         } else {
            Vec3d var9 = new Vec3d(var7.x, 0.0, var7.z);
            double var10 = var9.length();
            Box var12;
            Box var13;
            if (Math.abs(var7.x) > Math.abs(var7.z)) {
               var12 = a.player.dimensions.getBoxAt(current).stretch(var7.x, 0.0, 0.0);
               var13 = a.player.dimensions.getBoxAt(current).offset(var7.x, 0.0, 0.0).stretch(0.0, 0.0, var7.z);
            } else {
               var12 = a.player.dimensions.getBoxAt(current).stretch(0.0, 0.0, var7.z);
               var13 = a.player.dimensions.getBoxAt(current).offset(0.0, 0.0, var7.z).stretch(var7.x, 0.0, 0.0);
            }

            double var14 = current.y;
            double var16 = target.y;
            ClientWorld var18 = a.world;
            if (!(var10 <= farawayTp)) {
               if (command) {
                  Debug.b("水平差距过大,当前tp模式无法完成");
               }

               return List.of();
            } else {
               double var36;
               if (var14 >= var18.getBottomY() && var14 <= var18.getBottomY() + var18.getHeight()) {
                  Box var19 = a.player.dimensions.getBoxAt(current);
                  Box var20 = a.player.dimensions.getBoxAt(target);
                  Box var21 = CollisionUtil.resetY(var12, var18.getBottomY(), var18.getBottomY() + var18.getHeight());
                  Box var22 = CollisionUtil.resetY(var13, var18.getBottomY(), var18.getBottomY() + var18.getHeight());
                  boolean var23 = RenderTasks.g;
                  RenderTasks.b = Color.CYAN;
                  RenderTasks.debugBox(var12);
                  RenderTasks.debugBox(var13);
                  RenderTasks.b = Color.MAGENTA;
                  RenderTasks.debugBox(var21);
                  RenderTasks.debugBox(var22);
                  ArrayList<Box> var24 = new ArrayList();
                  ArrayList var25 = new ArrayList();
                  BiFunction<BlockState, BlockPos, Box> var26 = considerEnvironment ? (BlockState blockstate, BlockPos blockpos) -> {
                     if (blockstate != null) {
                        Block var4 = blockstate.getBlock();
                        if (var4 == Blocks.LAVA || var4 == Blocks.SOUL_FIRE || var4 == Blocks.FIRE) {
                           Box var5 = Box.enclosing(blockpos, blockpos);
                           if (CollisionUtil.voxelShapeIntersectHorizontal(var5, var19) || CollisionUtil.voxelShapeIntersectHorizontal(var5, var20)) {
                              return var5;
                           }
                        }
                     }

                     return null;
                  } : null;
                  CollisionUtil.getCollisions(var18, a.player, var22, var25, var24, 4, null, null, var26);
                  CollisionUtil.getCollisions(var18, a.player, var21, var25, var24, 4, null, null, var26);
                  var23 = RenderTasks.g;
                  RenderTasks.g = true;
                  RenderTasks.b = Color.BLUE;

                  for (Box var28 : var24) {
                     if (var28.minY > 20.0) {
                        RenderTasks.debugBox(var28);
                     }
                  }

                  if (considerEnvironment) {
                  }

                  RenderTasks.g = var23;
                  RenderTasks.b = Color.CYAN;
                  if (var24.isEmpty() && var25.isEmpty()) {
                     var36 = var14;
                  } else {
                     double var29 = searchFirstNoYConflictYHeight(var14, var24, var25, 0.0, 180.0, true);
                     double var31 = searchFirstNoYConflictYHeight(var14, var24, var25, 0.0, 180.0, false);
                     double var33;
                     if (Math.abs(var29) > Math.abs(var31)) {
                        var33 = var31;
                     } else {
                        var33 = var29;
                     }

                     new Vec3d(0.0, var33, 0.0);
                     boolean var35 = RenderTasks.g;
                     RenderTasks.g = true;
                     RenderTasks.b = Color.WHITE;
                     RenderTasks.debugBox(var12.offset(0.0, var33, 0.0));
                     RenderTasks.debugBox(var13.offset(0.0, var33, 0.0));
                     RenderTasks.g = var35;
                     var36 = var33 + var14;
                  }
               } else {
                  var36 = var14;
               }

               if (!(Math.abs(var36 - var14) > 190.0) && !(Math.abs(var36 - var16) > 190.0)) {
                  Vec3d var38 = current.add(new Vec3d(0.0, var36 - var14, 0.0));
                  Vec3d var39 = var38.add(var9);
                  Vec3d var40 = var39.add(new Vec3d(0.0, var16 - var36, 0.0));
                  return List.of(current, var38, var39, var40);
               } else {
                  if (command) {
                     Debug.b("y 差距过大,当前tp模式无法完成");
                  }

                  return List.of();
               }
            }
         }
      }
   }

   public static ModuleGroup ai() {
      return k;
   }

   @Modifiable
   public static MovTasks$MovInfo createMovInfo(Vec3d to, Boolean onGround, boolean updatePlayerPos, Vec2f rotationOverride) {
      return new MovTasks$MovInfo(to, onGround, updatePlayerPos, rotationOverride);
   }

   @Modifiable
   public static MovTasks$MovInfo i(Vec3d vec3) {
      return MovTasks$MovInfo.adA(vec3);
   }

   public static LegacySnapRotManager am() {
      return o;
   }

   @Modifiable
   public static void p(KalamaHelperHelperIX context, List<MovTasks$MovInfo> deltaMovements, boolean allowNextTick, boolean noFall) {
      scheduleFarawayMoveInternal(deltaMovements, allowNextTick, context, noFall);
   }

   public static FloatingUtils aD() {
      return F;
   }

   @Modifiable
   public static List<KalamaHelperHelperV> createMovingPacketsForMovSequence(
      KalamaHelperHelperIX context, List<MovTasks$MovInfo> deltaMovements, boolean allowFailure, boolean considerNoFall
   ) {
      ArrayList var4 = new ArrayList();
      int var5 = 0;

      for (int var6 = 0; var6 <= deltaMovements.size(); var6++) {
         var4.add(new KalamaHelperHelperV());
      }

      boolean var43 = false;
      ArrayList<MovTasks$MovInfo> var7 = new ArrayList();
      Vec3d var8 = (Vec3d)context.from().getValue();

      for (int var9 = 0; var9 < deltaMovements.size(); var9++) {
         if (!var43 && ((MovTasks$MovInfo)deltaMovements.get(var9)).isEmptyTo(var8)) {
            var5++;
         } else {
            var43 = true;
            var7.add((MovTasks$MovInfo)deltaMovements.get(var9));
         }
      }

      if (var7.isEmpty()) {
         return var4;
      } else {
         List var42 = var7;
         Vec3d var44 = (Vec3d)context.tickFirstGoodVec().getValue();
         boolean var45 = a.player.hasVehicle();
         Entity var46 = a.player.getRootVehicle();
         double var10 = var46.getY() - a.player.getY();
         double var12 = ((Vec3d)context.from().getValue()).y;
         double var14 = var12;
         boolean var16 = !considerNoFall && a.player.isOnGround();
         Vec2f var17 = new Vec2f(a.player.getPitch(), a.player.getYaw());
         if (context.currentTokenInTick().get() == 0) {
            int var18 = 0;
            Vec3d var19 = var44;
            int var20 = 0;

            for (MovTasks$MovInfo var22 : var7) {
               var20++;
               Vec3d var23 = var22.vec3d();
               Vec3d var24 = var23.subtract(var19);
               var19 = var23;
               double var25 = var24.length();
               double var27 = var23.subtract(var44).length();
               double var29 = Math.max(var25, var27);
               int var31 = (int)Math.ceil(var29 / 9.9) - var20;
               var18 = Math.max(var18, var31);
            }

            if (var18 > 0) {
               if (var18 > 20 - var7.size()) {
                  return var4;
               }

               for (int var50 = 0; var50 < var18; var50++) {
                  context.currentTokenLimit().set(20);
                  context.currentTokenInTick().incrementAndGet();
                  if (var45) {
                     ((KalamaHelperHelperV)var4.get(var5)).b(VPacket.i(var46));
                  } else {
                     ((KalamaHelperHelperV)var4.get(var5)).b(VPacket.f(var16, false));
                  }
               }
            }
         }

         for (int var47 = 0; var47 < var42.size(); var47++) {
            MovTasks$MovInfo var48 = (MovTasks$MovInfo)var42.get(var47);
            Vec3d var49 = (Vec3d)context.from().getValue();
            Vec3d var51 = var48.vec3d().subtract(var49);
            var14 = Math.max(var14, var48.vec3d().y);
            var12 = Math.min(var12, var48.vec3d().y);
            double var32 = var51.length();
            double var34 = var44.subtract(var48.vec3d()).length();
            double var36 = 9.8;
            if (var32 == 0.0 && var34 == 0.0) {
               context.currentTokenLimit().set(20);
               context.currentTokenInTick().incrementAndGet();
               if (var48.oGroundOverride() != null) {
                  var16 = var48.oGroundOverride();
                  ((KalamaHelperHelperV)var4.get(var47 + var5)).c(() -> a.player.setOnGround(var16));
               }

               boolean var54 = var48.rotationOverride() != null && !Objects.equals(var48.rotationOverride(), var17);
               if (var54) {
                  var17 = new Vec2f(var48.rotationOverride().x, var48.rotationOverride().y);
                  float var55 = var17.x;
                  float var56 = var17.y;
                  ((KalamaHelperHelperV)var4.get(var47 + var5)).c(() -> {
                     a.player.setPitch(var55);
                     a.player.setYaw(var56);
                  });
               }

               if (var45) {
                  ((KalamaHelperHelperV)var4.get(var47 + var5)).b(VPacket.i(var46));
               } else if (var54) {
                  ((KalamaHelperHelperV)var4.get(var47 + var5)).b(VPacket.h(var17.y, var17.x, var16, false));
               } else {
                  ((KalamaHelperHelperV)var4.get(var47 + var5)).b(VPacket.f(var16, false));
               }
            } else {
               int var38 = (int)Math.ceil(Math.max(var32, var34) / var36);
               int var39 = context.currentTokenInTick().get() + 1;
               if (Math.min(var38, var39) >= Math.max(5, context.currentTokenLimit().get()) && allowFailure && context.currentTokenInTick().get() > Math.max(5, context.currentTokenLimit().get())) {
                  ((KalamaHelperHelperV)var4.get(var47 + var5)).a();
                  if (considerNoFall && var47 > 0) {
                     ((KalamaHelperHelperV)var4.get(var47 - 1 + var5)).d(() -> {
                        ClientPlayerAccess.of(a.player).setForceNoFall(true);
                        a.player.setOnGround(false);
                     });
                  }

                  return var4;
               }

               context.currentTokenLimit().decrementAndGet();
               context.currentTokenInTick().incrementAndGet();
               if (var48.oGroundOverride() != null) {
                  var16 = var48.oGroundOverride();
                  ((KalamaHelperHelperV)var4.get(var47 + var5)).c(() -> a.player.setOnGround(var16));
               }

               boolean var40 = var48.rotationOverride() != null && !Objects.equals(var48.rotationOverride(), var17);
               if (var40) {
                  var17 = new Vec2f(var48.rotationOverride().x, var48.rotationOverride().y);
                  float var52 = var17.x;
                  float var41 = var17.y;
                  ((KalamaHelperHelperV)var4.get(var47 + var5)).c(() -> {
                     a.player.setPitch(var52);
                     a.player.setYaw(var41);
                  });
               }

               Vec3d var53 = var48.vec3d();
               if (var45) {
                  ((KalamaHelperHelperV)var4.get(var47 + var5)).c(() -> {
                     var46.setPosition(var53.add(0.0, var10, 0.0));
                     if (var48.updatePlayer()) {
                        a.player.setPosition(var53);
                     }
                  });
                  ((KalamaHelperHelperV)var4.get(var47 + var5)).b(VPacket.i(var46));
                  context.from().setValue(var53);
               } else {
                  PlayerMoveC2SPacket var57 = var40
                     ? VPacket.j(var53.getX(), var53.getY(), var53.getZ(), var17.y, var17.x, var16, false)
                     : VPacket.g(var53.getX(), var53.getY(), var53.getZ(), var16, false);
                  ((KalamaHelperHelperV)var4.get(var47 + var5)).b(var57);
                  if (var48.updatePlayer()) {
                     ((KalamaHelperHelperV)var4.get(var47 + var5)).c(() -> a.player.setPosition(var53));
                  }

                  context.from().setValue(var53);
               }
            }
         }

         if (considerNoFall) {
            ((KalamaHelperHelperV)var4.get(var4.size() - 1)).c(() -> {
               ClientPlayerAccess.of(a.player).setForceNoFall(true);
               a.player.setOnGround(false);
            });
         }

         return var4;
      }
   }

   @Modifiable
   public static void executeTp(Vec3d target, double farawayTp, boolean command, boolean considerNoFall) {
      if (a.player != null) {
         e = a.player.getPos();
         d = Vec3d.ZERO.add(target);
         scheduleTpInternal(KalamaHelperHelperIX.create(a.player.getPos()), target, farawayTp, command, false, considerNoFall);
      }
   }

   static {
      Listener.aE().k(MovTasks::Z);
      Listener.aE().k(MovTasks::F);
      Listener.o(PlayerMoveC2SPacket.class, MovTasks::C);
      Listener.as().k(MovTasks::E);
      k.registerFactories(MovTasks::aa);
      HackModules.registerModuleGroup(k);
      Q.put("this", (re, var1, errMsg) -> var1.getPos());
      Q.put(
         "near",
         (re, var1, errMsg) -> {
            AbstractClientPlayerEntity var3 = a.world
               .getPlayers()
               .stream()
               .filter(m -> m != var1)
               .sorted(Comparator.comparingDouble(m -> m.getPos().squaredDistanceTo(var1.getPos())))
               .findFirst()
               .orElse(null);
            if (var3 == null) {
               errMsg.accept(Text.literal("附近没有其他玩家!").formatted(Formatting.RED));
               return null;
            } else {
               errMsg.accept(Text.literal("找到附近的玩家: " + var3.getName()).formatted(Formatting.GREEN));
               return var3.getPos();
            }
         }
      );
      Q.put("mark", (re, var1, errMsg) -> {
         if (R != null) {
            Vec3d var3 = Vec3d.ZERO.add(R);
            errMsg.accept(Text.literal("使用记录坐标： ").append(ChatUtils.getDisplayedLocation(var3)));
            return var3;
         } else {
            errMsg.accept(Text.literal("暂未记录坐标!"));
            return null;
         }
      });
      Q.put("back", (re, var1, errMsg) -> {
         if (e != null) {
            errMsg.accept(Text.literal("使用上一个位置: ").append(ChatUtils.getDisplayedLocation(e)));
            return e;
         } else {
            errMsg.accept(Text.literal("找不到上一个位置"));
            return null;
         }
      });
      Q.put("desync", (re, var1, errMsg) -> {
         if (s.ri != null) {
            errMsg.accept(Text.literal("使用上次客户端同步之前的位置").append(ChatUtils.getDisplayedLocation(s.ri)));
            return s.ri;
         } else {
            errMsg.accept(Text.literal("找不到上一次的客户端同步记录"));
            return null;
         }
      });
      Q.put("lasttp", (re, var1, errMsg) -> {
         if (d != null) {
            errMsg.accept(Text.literal("使用上一个TP请求: ").append(ChatUtils.getDisplayedLocation(d)));
            return d;
         } else {
            errMsg.accept(Text.literal("找不到上一个TP请求"));
            return null;
         }
      });
      Q.put("death", (re, var1, errMsg) -> {
         Optional var3 = var1.getLastDeathPos();
         if (var3.isPresent()) {
            if (Objects.equals(((GlobalPos)var3.get()).dimension(), a.world.getRegistryKey())) {
               return ((GlobalPos)var3.get()).pos().toBottomCenterPos();
            }

            errMsg.accept(Text.literal("上次死亡位置不在该世界"));
         } else {
            errMsg.accept(Text.literal("暂未死亡历史记录"));
         }

         return null;
      });
      Q.put("camera", (re, var1, errMsg) -> RenderUtils.getCameraEntityPos());
      Listener.bx().c(KalamaHelperHelperZX.class).l(MovTasks::resolveSpecialType, 0);
      Listener.bx().c(KalamaHelperHelperZX.class).l(MovTasks::resolveEntityTarget, 2147483646);
   }

   public static TpaCommand aG() {
      return I;
   }

   public static Vec3d collideWithTrustedList(
      Box currBoundingBox, Vec3d movement, List<VoxelShape> potentialCollisionsVoxel, List<Box> potentialCollisionsBB, double stepHeight, boolean onGround
   ) {
      if (potentialCollisionsVoxel.isEmpty() && potentialCollisionsBB.isEmpty()) {
         f.set(false);
         return movement;
      } else {
         RenderTasks.b = Color.YELLOW;
         Vec3d var7 = CollisionUtil.performCollisions(movement, currBoundingBox, potentialCollisionsVoxel, potentialCollisionsBB);
         if (!aj().disableStepheightFeature.get()
            && stepHeight > 0.0
            && (onGround || var7.y != movement.y && movement.y < 0.0)
            && (var7.x != movement.x || var7.z != movement.z)) {
            RenderTasks.b = Color.BLUE;
            Vec3d var8 = CollisionUtil.performCollisions(
               new Vec3d(movement.x, stepHeight, movement.z), currBoundingBox, potentialCollisionsVoxel, potentialCollisionsBB
            );
            boolean var9 = RenderTasks.g;
            RenderTasks.g = false;
            Vec3d var10 = CollisionUtil.performCollisions(
               new Vec3d(0.0, stepHeight, 0.0), currBoundingBox.stretch(movement.x, 0.0, movement.z), potentialCollisionsVoxel, potentialCollisionsBB
            );
            if (var10.y < stepHeight) {
               Vec3d var11 = CollisionUtil.performCollisions(
                     new Vec3d(movement.x, 0.0, movement.z), currBoundingBox.offset(var10), potentialCollisionsVoxel, potentialCollisionsBB
                  )
                  .add(var10);
               if (var11.horizontalLengthSquared() > var8.horizontalLengthSquared()) {
                  var8 = var11;
               }
            }

            RenderTasks.g = var9;
            if (var8.horizontalLengthSquared() > var7.horizontalLengthSquared()) {
               f.set(true);
               return var8.add(
                  CollisionUtil.performCollisions(
                     new Vec3d(0.0, -var8.y + movement.y, 0.0), currBoundingBox.offset(var8), potentialCollisionsVoxel, potentialCollisionsBB
                  )
               );
            } else {
               f.set(false);
               return var7;
            }
         } else {
            f.set(false);
            return var7;
         }
      }
   }

   public static void resolveSpecialType(Event<me.matl114.events.impl.KalamaHelperHelperI<KalamaHelperHelperZX>> tpaRequest) {
      KalamaHelperHelperZX var1 = (KalamaHelperHelperZX)((me.matl114.events.impl.KalamaHelperHelperI)tpaRequest.b).b();
      switch (var1.d) {
         case UJ:
            KalamaHelperHelperEX var7 = (KalamaHelperHelperEX)var1;
            if (var7.hasResolved()) {
               return;
            }

            ArgumentReader var3 = var7.c;
            if (var3.hasNext()) {
               String var4 = var3.g();
               if (var4.startsWith("#")) {
                  var3.h();
                  String var5 = var4.substring(1);
                  if (Q.containsKey(var5)) {
                     KalamaHelperHelperA var6 = Q.get(var5);
                     var7.a = Optional.ofNullable(var6.resolvePosition(var3, var1.e.si(), var7.b));
                  } else {
                     var7.b.accept(Text.literal("不存在这样的特殊位置: " + var4).formatted(Formatting.RED));
                     var7.a = Optional.empty();
                  }
               }
            }
            break;
         case UK:
            Stream var2 = M.getTab(var1.e, var1.f);
            if (var2 != null) {
               var2.forEach(((KalamaHelperHelperE)var1).tab::add);
            }
      }
   }

   @Modifiable
   public static void X(Vec3d pos) {
      ar().alV(Optional.of(pos));
   }

   public static MoveTimer av() {
      return x;
   }

   public static StepHeight aw() {
      return y;
   }

   public static void a() {
   }

   public static AutoResync ar() {
      return t;
   }

   private static Vec3d resolveCoord(Entity entity, InputArgument argx, InputArgument argy, InputArgument argz) {
      Vec3d var13;
      if (argx.z().startsWith("^")) {
         if (!argy.z().startsWith("^") || !argz.z().startsWith("^")) {
            throw new LogicalError("Illegal format of look coordinate");
         }

         String var4 = argx.z();
         String var5 = argy.z();
         String var6 = argz.z();
         double var7 = var4.length() == 1 ? 0.0 : me.matl114.utils.a.KalamaHelperHelperA.h(var4.substring(1), argx.i());
         double var9 = var5.length() == 1 ? 0.0 : me.matl114.utils.a.KalamaHelperHelperA.h(var5.substring(1), argy.i());
         double var11 = var6.length() == 1 ? 0.0 : me.matl114.utils.a.KalamaHelperHelperA.h(var6.substring(1), argz.i());
         var13 = EntityUtils.lookCoordToAbsolutePos(entity, var7, var9, var11);
      } else {
         Vec3d var19 = entity.getPos();
         double var14 = 0.0;
         double var20 = 0.0;
         double var21 = 0.0;
         String var16 = argx.z();
         String var17 = argy.z();
         String var18 = argz.z();
         if (var16.startsWith("~")) {
            var14 = var19.x;
            var16 = var16.substring(1);
         }

         if (!var16.isEmpty()) {
            var14 += me.matl114.utils.a.KalamaHelperHelperA.h(var16, argx.i());
         }

         if (var17.startsWith("~")) {
            var20 = var19.y;
            var17 = var17.substring(1);
         }

         if (!var17.isEmpty()) {
            var20 += me.matl114.utils.a.KalamaHelperHelperA.h(var17, argy.i());
         }

         if (var18.startsWith("~")) {
            var21 = var19.z;
            var18 = var18.substring(1);
         }

         if (!var18.isEmpty()) {
            var21 += me.matl114.utils.a.KalamaHelperHelperA.h(var18, argz.i());
         }

         var13 = new Vec3d(var14, var20, var21);
      }

      return var13;
   }

   public static Vec3d collide(Entity entity, Vec3d movement) {
      boolean var2 = movement.x == 0.0;
      boolean var3 = movement.y == 0.0;
      boolean var4 = movement.z == 0.0;
      if (var2 & var3 & var4) {
         return movement;
      } else {
         double var5 = entity.getStepHeight();
         Box var7 = entity.getBoundingBox();
         ArrayList var8 = new ArrayList();
         ArrayList var9 = new ArrayList();
         collectBoxInvolvingInMovements(entity, entity.getPos(), movement, var8, var9, true);
         return collideWithTrustedList(var7, movement, var9, var8, var5, entity.isOnGround());
      }
   }

   public static boolean hasHorizontalCollision(Entity entity, Vec3d move) {
      if (entity.hasPassengers()) {
         return false;
      } else {
         Vec3d var2 = simulateMovement(entity, entity.getPos(), move, true);
         return !MathHelper.approximatelyEquals(var2.x, move.x) || !MathHelper.approximatelyEquals(var2.z, move.z);
      }
   }

   public static void scheduleFarawayMoveInternal(
      List<MovTasks$MovInfo> deltaMovements, boolean allowNextTick, KalamaHelperHelperIX context, boolean considerNoFall
   ) {
      List var4 = createMovingPacketsForMovSequence(context, deltaMovements, allowNextTick, considerNoFall);

      for (int var5 = 0; var5 < var4.size(); var5++) {
         if (((KalamaHelperHelperV)var4.get(var5)).success) {
            ((KalamaHelperHelperV)var4.get(var5)).run();
         } else {
            if (allowNextTick) {
               List var6 = deltaMovements.subList(var5, deltaMovements.size());
               Tasks.l(() -> scheduleFarawayMoveInternal(var6, allowNextTick, context.mS(), considerNoFall), 1);
               return;
            }

            ((KalamaHelperHelperV)var4.get(var5)).run();
         }
      }
   }

   public static AntiLiquid aB() {
      return D;
   }

   @Modifiable(
      optional = true
   )
   public static void farawayMoveFromTo(Vec3d from, Vec3d to, Boolean onGroundOverride, boolean updatePlayer) {
      Vec3d var4 = to.subtract(from);
      double var5 = var4.length();
      if (a.player.hasVehicle()) {
         Entity var7 = a.player.getVehicle();
         double var8 = 9.99;
         if (var5 >= var8) {
            double var10 = 10.0;
            int var12 = (int)((var5 + 1.0) / var10);
            if (var12 > 0) {
               for (int var13 = 0; var13 <= var12; var13++) {
                  a.getNetworkHandler().sendPacket(VPacket.i(var7));
               }
            }
         }

         double var19 = var7.getY() - a.player.getY();
         var7.setPosition(to.add(0.0, var19, 0.0));
         a.getNetworkHandler().sendPacket(VPacket.i(var7));
         if (updatePlayer) {
            a.player.setPosition(to);
         }
      } else {
         double var14 = (a.player.isFallFlying() ? 10.0 * Math.sqrt(3.0) : 10.0) - 0.01;
         if (var5 >= var14) {
            double var16 = 10.0;
            int var18 = (int)((var5 + 1.0) / var16);
            if (var18 > 0) {
               for (int var20 = 0; var20 <= var18; var20++) {
                  a.getNetworkHandler().sendPacket(VPacket.f(a.player.isOnGround(), false));
               }
            }
         }

         if (onGroundOverride != null) {
            a.player.setOnGround(onGroundOverride);
         }

         a.getNetworkHandler().sendPacket(VPacket.g(to.getX(), to.getY(), to.getZ(), a.player.isOnGround(), false));
         if (updatePlayer) {
            a.player.setPosition(to);
         }
      }
   }

   public static Box makeCollectorBoxInvolvingCollision(Box currBoundingBox, Vec3d movement, double stepHeight, boolean onGround) {
      boolean var5 = movement.x == 0.0;
      boolean var6 = movement.y == 0.0;
      boolean var7 = movement.z == 0.0;
      Box var8;
      if (var5 & var7) {
         if (movement.y > 0.0) {
            var8 = CollisionUtil.cutUpwards(currBoundingBox, movement.y);
         } else {
            var8 = CollisionUtil.cutDownwards(currBoundingBox, movement.y);
         }
      } else if (!aj().disableStepheightFeature.get() && stepHeight > 0.0 && (onGround || movement.y < 0.0)) {
         if (movement.y <= 0.0) {
            var8 = CollisionUtil.y(currBoundingBox.stretch(movement.x, movement.y, movement.z), stepHeight);
         } else {
            var8 = currBoundingBox.stretch(movement.x, Math.max(stepHeight, movement.y), movement.z);
         }
      } else {
         var8 = currBoundingBox.stretch(movement.x, movement.y, movement.z);
      }

      return var8;
   }

   public void onSpeedUp(ClientPlayerEntity player) {
      if (!player.isSneaking() && (player.forwardSpeed != 0.0F || player.sidewaysSpeed != 0.0F)) {
         if (player.forwardSpeed > 0.0F && !player.horizontalCollision) {
            player.setSprinting(true);
         }

         if (player.isOnGround()) {
            Vec3d var2 = player.getVelocity();
            player.setVelocity(var2.x * 1.8, var2.y + 0.1, var2.z * 1.8);
            var2 = player.getVelocity();
            double var3 = Math.sqrt(Math.pow(var2.x, 2.0) + Math.pow(var2.z, 2.0));
            double var5 = 0.66F;
            if (var3 > var5) {
               player.setVelocity(var2.x / var3 * var5, var2.y, var2.z / var3 * var5);
            }
         }
      }
   }

   public static EntityMovementStatus<ClientPlayerEntity> startSimulation() {
      return new EntityMovementStatus(a.player);
   }

   private static boolean noBlocksAround(Entity entity) {
      Box var1 = entity.getBoundingBox().expand(0.0625).stretch(0.0, -0.55, 0.0);
      int var2 = MathHelper.floor(var1.minX);
      int var3 = MathHelper.floor(var1.minY);
      int var4 = MathHelper.floor(var1.minZ);
      int var5 = MathHelper.floor(var1.maxX);
      int var6 = MathHelper.floor(var1.maxY);
      int var7 = MathHelper.floor(var1.maxZ);
      Mutable var8 = new Mutable();

      for (int var9 = var3; var9 <= var6; var9++) {
         for (int var10 = var4; var10 <= var7; var10++) {
            for (int var11 = var2; var11 <= var5; var11++) {
               var8.set(var11, var9, var10);
               BlockState var12 = a.world.getBlockState(var8);
               if (var12 != null && !var12.isAir()) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   public static ElytraSlowFall aE() {
      return G;
   }

   @Modifiable
   public static List<Vec3d> z(Vec3d current, Vec3d target, boolean command, double farawayTp, boolean considerEnvironment) {
      return h.generateTpSequence(current, target, command, farawayTp, considerEnvironment);
   }

   public static Stream<String> ag() {
      return Q.keySet().stream().map(s -> "#" + s);
   }

   public static SetBackLog aq() {
      return s;
   }

   public static boolean hasCollidedSoftly(Vec3d adjustedMovement) {
      float var1 = a.player.getYaw() * (float) (Math.PI / 180.0);
      double var2 = MathHelper.sin(var1);
      double var4 = MathHelper.cos(var1);
      double var6 = a.player.sidewaysSpeed * var4 - a.player.forwardSpeed * var2;
      double var8 = a.player.forwardSpeed * var4 + a.player.sidewaysSpeed * var2;
      double var10 = MathHelper.square(var6) + MathHelper.square(var8);
      double var12 = MathHelper.square(adjustedMovement.x) + MathHelper.square(adjustedMovement.z);
      if (!(var10 < 1.0E-5F) && !(var12 < 1.0E-5F)) {
         double var14 = var6 * adjustedMovement.x + var8 * adjustedMovement.z;
         double var16 = Math.acos(var14 / Math.sqrt(var10 * var12));
         return var16 < 0.13962634F;
      } else {
         return false;
      }
   }

   private static boolean D(VehicleMoveC2SPacket packet) {
      return !doingTp;
   }

   public static AntiChunkLag as() {
      return u;
   }

   public static Travel aI() {
      return K;
   }

   public static List<Vec3d> tpAttackSearch(Vec3d from, Box to, double availableRange, double maxAtOnce, int maxAttempt) {
      double var7 = MathUtils.a(availableRange);
      ArrayList var9 = new ArrayList();
      Vec3d var10 = a.player.getVelocity();
      Vec3d var11 = a.player.getPos();
      Vec3d var12 = from;

      label25:
      for (int var13 = 0; var13 < maxAttempt; var13++) {
         if (!var9.isEmpty()) {
            var12 = (Vec3d)var9.get(var9.size() - 1);
         }

         Vec3d var14 = to.getBottomCenter().subtract(var12);
         double var15 = Math.max(1.0, var14.length() - availableRange + 1.0);
         var14 = var14.normalize().multiply(Math.min(maxAtOnce, var15));
         if (var15 >= maxAtOnce) {
            var14 = var14.multiply(maxAtOnce / var15);
         }

         switch (validMovToEntity(h, var12, var14, to, var7, var9)) {
            case 1:
            case 2:
            default:
               break label25;
         }
      }

      a.player.setVelocity(var10);
      a.player.setPosition(var11);
      return var9;
   }

   public static boolean seenAsFloating(boolean fakeGilding) {
      return !v.hB
         && a.player.getVelocity().y >= -0.03125
         && a.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR
         && !a.player.hasStatusEffect(StatusEffects.LEVITATION)
         && (fakeGilding || !a.player.isFallFlying())
         && !a.player.isUsingRiptide()
         && !a.player.isSleeping()
         && !a.player.isRiding()
         && !a.player.isDead()
         && noBlocksAround(a.player);
   }

   public static boolean validMoveTo(KalamaHelperHelperMX engin, Vec3d currentPos, Vec3d currentTry) {
      if (engin.checkEnvironmentCollision(a.player, currentPos, false)) {
         return false;
      } else {
         Vec3d var3 = engin.simulateMovement(a.player, currentPos, currentTry);
         if (validMovementAsServer(currentTry, var3)) {
            Vec3d var4 = currentPos.add(currentTry);
            if (!engin.checkEnvironmentCollision(a.player, var4, false)) {
               return true;
            }
         }

         return false;
      }
   }

   public static NoFall ap() {
      return r;
   }

   @Modifiable
   public static List<MovTasks$MovInfo> n(List<Vec3d> vec3ds) {
      return vec3ds.stream().map(MovTasks::i).collect(Collectors.toCollection(ArrayList::new));
   }

   public static boolean validMoveToAndBack(KalamaHelperHelperMX engin, Vec3d currentPos, Vec3d currentTry) {
      if (engin.checkEnvironmentCollision(a.player, currentPos, false)) {
         return false;
      } else {
         Vec3d var3 = engin.simulateMovement(a.player, currentPos, currentTry);
         if (validMovementAsServer(currentTry, var3)) {
            Vec3d var4 = currentPos.add(currentTry);
            if (!engin.checkEnvironmentCollision(a.player, var4, false)) {
               Vec3d var5 = Vec3d.ZERO.subtract(currentTry);
               Vec3d var6 = engin.simulateMovement(a.player, var4, var5);
               if (validMovementAsServer(var5, var6)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   @Modifiable
   public static boolean isCollidingWithEnvironment(Entity entity, Box box) {
      ArrayList var2 = new ArrayList();
      ArrayList var3 = new ArrayList();
      CollisionUtil.U(a.world, entity, box, var3, var2, 4, null, null);
      return !var3.isEmpty() || !var2.isEmpty();
   }

   @Modifiable
   public static Vec3d simulateMovement(Entity entity, Vec3d from, Vec3d vec3d, boolean serverMode) {
      Entity var4 = entity.getRootVehicle();
      double var5 = var4.getY() - entity.getY();
      Vec3d var7 = var4.getPos();
      var4.setPosition(from.add(0.0, var5, 0.0));
      RenderTasks.g = true;
      Vec3d var8 = collide(var4, vec3d);
      RenderTasks.g = false;
      var4.setPosition(var7);
      return var8;
   }

   public static Flight at() {
      return v;
   }

   private static void E(Event<ClientPlayerEntity> entity) {
      if (doingTp) {
         entity.cancel();
      }
   }

   @Modifiable
   public static KalamaHelperHelperIX createPlayerMovContext() {
      return KalamaHelperHelperIX.create(a.player.getPos());
   }

   private static void F(Event<ClientPlayerEntity> plaayer) {
      doingTp = false;
      ClientPlayerAccess.of((ClientPlayerEntity)plaayer.b).getLegalMovementManager().i(new KalamaHelperHelperX());
   }

   public static ForwardTp an() {
      return p;
   }

   @Modifiable
   public static boolean u(Entity entity) {
      ArrayList var1 = new ArrayList();
      ArrayList var2 = new ArrayList();
      CollisionUtil.U(entity.getEntityWorld(), entity, entity.getBoundingBox(), var2, var1, 4, null, null);
      return !var2.isEmpty() || !var1.isEmpty();
   }

   public static NoSlowDown ao() {
      return q;
   }

   @Modifiable
   public static KalamaHelperHelperIX createMovContext(double a1, double b1, double c1) {
      return KalamaHelperHelperIX.create(new Vec3d(a1, b1, c1));
   }

   public static void o(Vec3d from, List<MovTasks$MovInfo> deltaMovements, boolean allowNextTick, boolean considerNoFall) {
      scheduleFarawayMoveInternal(deltaMovements, allowNextTick, KalamaHelperHelperIX.create(from), considerNoFall);
   }

   public static ElytraFlight ay() {
      return A;
   }

   public static PlayerStateManager ak() {
      return m;
   }

   @Modifiable
   public static KalamaHelperHelperIX g(Vec3d vec3d) {
      return KalamaHelperHelperIX.create(vec3d);
   }

   public static void collectBoxInvolvingInMovements(
      Entity entity, Vec3d startPos, Vec3d movement, List<Box> intoAABB, List<VoxelShape> intoVoxels, boolean ignoreUnloadedChunk
   ) {
      Box var6 = entity.dimensions.getBoxAt(startPos);
      if (!CollisionUtil.isEmpty(var6)) {
         Box var7 = makeCollectorBoxInvolvingCollision(var6, movement, entity.getStepHeight(), entity.isOnGround());
         CollisionUtil.getCollisions(entity.getWorld(), entity, var7, intoVoxels, intoAABB, ignoreUnloadedChunk ? 4 : 6, null, null, null);
         if (RenderTasks.g) {
            for (Box var9 : intoAABB) {
               RenderTasks.debugBox(var9);
            }

            for (VoxelShape var13 : intoVoxels) {
               for (Box var11 : var13.getBoundingBoxes()) {
                  RenderTasks.debugBox(var11);
               }
            }
         }
      }
   }

   private static void aa(ModuleManager m) {
      l = new MovExtra().register(m);
      MovTasks.m = new PlayerStateManager().register(m);
      n = new PlayerInputManager().register(m);
      o = new LegacySnapRotManager().register(m);
      p = new ForwardTp().register(m);
      q = new NoSlowDown().register(m);
      r = new NoFall().register(m);
      s = new SetBackLog().register(m);
      t = new AutoResync().register(m);
      u = new AntiChunkLag().register(m);
      v = new Flight().register(m);
      w = new Sprint().register(m);
      x = new MoveTimer().register(m);
      y = new StepHeight().register(m);
      z = new ElytraExtra().register(m);
      A = new ElytraFlight().register(m);
      B = new ElytraGrimAcc().register(m);
      C = new ElytraJump().register(m);
      D = new AntiLiquid().register(m);
      E = new Velocity().register(m);
      F = new FloatingUtils().register(m);
      G = new ElytraSlowFall().register(m);
      H = new MovTest().register(m);
      I = new TpaCommand().register(m);
      J = new TargetCommand().register(m);
      K = new Travel().register(m);
   }

   public static Velocity aC() {
      return E;
   }

   @Modifiable(
      optional = true
   )
   public static void farawayMoveTo(Vec3d vec3d, boolean updatePlayer) {
      farawayMoveFromTo(a.player.getPos(), vec3d, null, updatePlayer);
   }

   public static ElytraExtra ax() {
      return z;
   }

   public static ElytraJump aA() {
      return C;
   }

   public static Sprint au() {
      return w;
   }

   public static void resolveEntityTarget(Event<me.matl114.events.impl.KalamaHelperHelperI<KalamaHelperHelperZX>> tpaRequest) {
      KalamaHelperHelperZX var1 = (KalamaHelperHelperZX)((me.matl114.events.impl.KalamaHelperHelperI)tpaRequest.b).b();
      switch (var1.d) {
         case UJ:
            KalamaHelperHelperEX var7 = (KalamaHelperHelperEX)var1;
            if (var7.hasResolved()) {
               return;
            }

            ArgumentReader var3 = var7.c;
            if (var3.hasNext()) {
               String var4 = var3.g();
               if (!var4.startsWith("@")) {
                  PlayerEntity var8 = a.world == null ? null : EntityUtils.getPlayerByName(var4);
                  if (var8 == null) {
                     return;
                  }

                  var3.h();
                  var7.a = Optional.of(var8.getPos());
                  return;
               }

               int var5 = var3.b();
               InputArgument var6 = P.consume(var1.e, var1.f, var3);
               if (var6 == null || !var6.k() || var6.g() == null) {
                  var3.c(var5);
                  return;
               }

               var7.a = Optional.ofNullable(((EntitySelector)var6.g()).pos(var1.e));
            }
            break;
         case UK:
            Stream var2 = P.getTab(var1.e, var1.f);
            if (var2 != null) {
               var2.forEach(((KalamaHelperHelperE)var1).tab::add);
            }
      }
   }

   public static ElytraGrimAcc az() {
      return B;
   }
}
