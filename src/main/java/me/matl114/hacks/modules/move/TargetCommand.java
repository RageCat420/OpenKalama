package me.matl114.hacks.modules.move;

import com.mojang.datafixers.util.Pair;
import java.util.HashMap;
import java.util.Map;
import me.matl114.commands.MainCommand;
import me.matl114.hacks.KalamaHelperHelperUX;
import me.matl114.hacks.api.BaseModule;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.KalamaHelperHelperAd;
import me.matl114.utils.KalamaHelperHelperAk;
import me.matl114.utils.KalamaHelperHelperDX;
import me.matl114.utils.KalamaHelperHelperE;
import me.matl114.utils.KalamaHelperHelperPX;
import me.matl114.utils.WorldUtils;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperD;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperH;
import me.matl114.utils.commands.commandGroup.SubCommand;
import me.matl114.utils.commands.commandGroup.TaskSubCommand;
import me.matl114.utils.commands.commandGroup.TreeSubCommand;
import me.matl114.utils.commands.params.ArgumentInputStream;
import me.matl114.utils.commands.params.KalamaHelperHelperA;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.commands.params.types.ExecutePos;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;

public class TargetCommand extends BaseModule {
   Map<String, Pair<Vec3d, Float>> lastCachedPosition = new HashMap<>();

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerCommandBootstrap(this::bootStrapTargetCommand);
   }

   public void onCalculateWaypoint(CommandExecution p, ArgumentInputStream re) {
      String var3 = re.o();
      KalamaHelperHelperAk var4 = WorldUtils.getWaypoint(var3);
      if (var4 == null) {
         p.sn(Text.literal("找不到这个坐标点").formatted(Formatting.RED));
      } else {
         KalamaHelperHelperAd var5 = var4.d();
         if (var5 instanceof KalamaHelperHelperPX var6) {
            if (this.lastCachedPosition.containsKey(var4.getDisplayName())) {
               Pair var7 = this.lastCachedPosition.remove(var4.getDisplayName());
               Vec3d var8 = (Vec3d)var7.getFirst();
               float var9 = (Float)var7.getSecond();
               Vec3d var10 = mc.player.getPos();
               float var11 = var6.azimuth();
               Vec2f var12 = intersectRays(var8, var9, var10, var11);
               if (var12 != null) {
                  p.sn(Text.literal("计算当前坐标点位置大致位于: ").append(ChatUtils.t(new Vec3d(var12.x, 64.0, var12.y))));
               } else {
                  p.sn(Text.literal("当前位置无法正确推断,请重新选去两点"));
               }
            } else {
               this.lastCachedPosition.put(var4.getDisplayName(), Pair.of(mc.player.getPos(), var6.azimuth()));
               p.sn(Text.literal("记录当前测算位置中,请移动若干位置后重新输入指令").formatted(Formatting.GREEN));
            }
         } else {
            p.sn(Text.literal("当前坐标点已有确定坐标").formatted(Formatting.GREEN));
            if (var5 instanceof KalamaHelperHelperDX var13) {
               p.sn(Text.literal("Pos: ").append(ChatUtils.t(var13.jk())));
            } else if (var5 instanceof KalamaHelperHelperE var14) {
               ChunkPos var15 = var14.Im();
               Vec3d var16 = new Vec3d(var15.x << 4, 64.0, var15.z << 4);
               p.sn(Text.literal("Pos: ").append(ChatUtils.t(var16)));
            }
         }
      }
   }

   public TargetCommand() {
      super("TargetCommand");
   }

   public void bootStrapTargetCommand(MainCommand mainCommand) {
      TreeSubCommand var2 = mainCommand.bC().a("target").k();
      var2.subBuilder(SubCommand.bp())
         .u("calculate")
         .z(
            m -> m.<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
               .u("waypoint")
               .x("message.command.target.calculate.waypoint.help")
               .A(KalamaHelperHelperA.a().B("waypoint").d(WorldUtils::c).v())
               .z(s -> s.executor(KalamaHelperHelperH.m(this::onCalculateWaypoint)))
               .r()
               .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
               .u("pos")
               .x("message.command.target.calculate.pos.help")
               .A(KalamaHelperHelperA.<KalamaHelperHelperUX, ExecutePos>b(KalamaHelperHelperUX::new).B("target").v())
               .z(s -> s.executor(KalamaHelperHelperH.m(this::onCalculatePosition)))
               .r()
               .subBuilder(SubCommand.bo())
               .u("chunk")
               .x("message.command.target.calculate.chunk.help")
               .A(KalamaHelperHelperA.a().B("z").f().v())
               .A(KalamaHelperHelperA.a().B("x").f().v())
               .z(s -> s.executor(KalamaHelperHelperH.m(this::onCalculateChunk)))
               .r()
         )
         .r();
      var2.subBuilder(SubCommand.bp())
         .u("target")
         .z(
            m -> m.<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
               .u("waypoint")
               .x("message.command.target.target.waypoint.help")
               .A(KalamaHelperHelperA.a().B("waypoint").d(WorldUtils::c).v())
               .z(s -> s.executor(KalamaHelperHelperH.m(this::onTargetWaypoint)))
               .r()
               .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
               .u("pos")
               .x("message.command.target.target.pos.help")
               .A(KalamaHelperHelperA.<KalamaHelperHelperUX, ExecutePos>b(KalamaHelperHelperUX::new).B("target").v())
               .z(s -> s.executor(KalamaHelperHelperH.m(this::onTargetPosition)))
               .r()
               .subBuilder(SubCommand.bo())
               .u("chunk")
               .x("message.command.target.target.chunk.help")
               .A(KalamaHelperHelperA.a().B("z").f().v())
               .A(KalamaHelperHelperA.a().B("x").f().v())
               .z(s -> s.executor(KalamaHelperHelperH.m(this::onTargetChunk)))
               .r()
         )
         .r();
   }

   public void onTargetWaypoint(CommandExecution p, ArgumentInputStream re) {
      String var3 = re.o();
      KalamaHelperHelperAk var4 = WorldUtils.getWaypoint(var3);
      if (var4 == null) {
         p.sn(Text.literal("找不到这个坐标点").formatted(Formatting.RED));
      } else {
         KalamaHelperHelperAd var5 = var4.d();
         if (var5 instanceof KalamaHelperHelperDX var6) {
            Vec3d var7 = var6.jk().subtract(mc.player.getEyePos()).normalize();
            PlayerStateManager.setPlayerRotationSafe(mc.player, var7);
         } else if (var5 instanceof KalamaHelperHelperE var8) {
            ChunkPos var11 = var8.Im();
            Vec2f var9 = new Vec2f((float)((var11.x << 4) - mc.player.getX()), (float)((var11.z << 4) - mc.player.getZ()));
            PlayerStateManager.setPlayerYawSafe(mc.player, var9);
         } else if (var5 instanceof KalamaHelperHelperPX var10) {
            PlayerStateManager.nT(mc.player, var10.azimuth() * (float) (180.0 / Math.PI));
         }
      }
   }

   public void onTargetChunk(CommandExecution p, ArgumentInputStream re) {
      int var3 = re.nextInt();
      int var4 = re.nextInt();
      Vec2f var5 = new Vec2f((float)((var3 << 4) - mc.player.getX()), (float)((var4 << 4) - mc.player.getZ()));
      PlayerStateManager.setPlayerYawSafe(mc.player, var5);
   }

   public void onCalculatePosition(CommandExecution p, ArgumentInputStream re) {
      ExecutePos var3 = re.f();
      if (var3 != null) {
         Vector3d var4 = var3.wu(p);
         Vec3d var5 = new Vec3d(var4.x, var4.y, var4.z);
         p.sn(Text.literal("Pos: ").append(ChatUtils.t(var5)));
         p.sn(Text.literal("NetherPos: ").append(ChatUtils.t(var5.multiply(0.125))));
         p.sn(Text.literal("WorldPos: ").append(ChatUtils.t(var5.multiply(8.0))));
         BlockPos var6 = BlockPos.ofFloored(var5);
         p.sn(Text.literal("ChunkPos: ").append(ChatUtils.s(var6.getX() >> 4, var6.getZ() >> 4)));
      } else {
         p.sm("输入了无效坐标!");
      }
   }

   public void onCalculateChunk(CommandExecution p, ArgumentInputStream re) {
      int var3 = re.nextInt();
      int var4 = re.nextInt();
      p.sn(Text.literal("ChunkPos: %d %d".formatted(var3, var4)));
      p.sn(Text.literal("Pos: ").append(ChatUtils.s(var3 << 4, var4 << 4)));
   }

   public void onTargetPosition(CommandExecution p, ArgumentInputStream re) {
      ExecutePos var3 = re.f();
      if (var3 != null) {
         Vector3d var4 = var3.wu(p);
         Vec3d var5 = new Vec3d(var4.x - mc.player.getX(), var4.y - mc.player.getY(), var4.z - mc.player.getZ()).normalize();
         PlayerStateManager.setPlayerRotationSafe(mc.player, var5);
      } else {
         p.sm("输入了无效坐标!");
      }
   }

   public static Vec2f intersectRays(Vec3d posA, float azimuthA, Vec3d posB, float azimuthB) {
      double var4 = posA.x;
      double var6 = posA.z;
      double var8 = posB.x;
      double var10 = posB.z;
      double var12 = Math.sin(azimuthA);
      double var14 = Math.cos(azimuthA);
      double var16 = Math.sin(azimuthB);
      double var18 = Math.cos(azimuthB);
      double var22 = -var14;
      double var26 = -var18;
      double var28 = var8 - var4;
      double var30 = var10 - var6;
      double var32 = var12 * var26 - var22 * var16;
      if (var32 == 0.0) {
         return null;
      } else {
         double var34 = (var28 * var26 - var30 * var16) / var32;
         double var36 = var4 + var34 * var12;
         double var38 = var6 + var34 * var22;
         return new Vec2f((float)var36, (float)var38);
      }
   }
}
