package me.matl114.hacks.modules.extra;

import com.mojang.serialization.JavaOps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import me.matl114.commands.MainCommand;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.KalamaHelperHelperE;
import me.matl114.hacks.KalamaHelperHelperEX;
import me.matl114.hacks.KalamaHelperHelperUX;
import me.matl114.hacks.KalamaHelperHelperZX;
import me.matl114.hacks.api.BaseModule;
import me.matl114.managers.FileManager;
import me.matl114.managers.file.FileStorage;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.CommonUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperD;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperH;
import me.matl114.utils.commands.commandGroup.SubCommand;
import me.matl114.utils.commands.commandGroup.TaskSubCommand;
import me.matl114.utils.commands.commandGroup.TreeSubCommand;
import me.matl114.utils.commands.params.ArgumentInputStream;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.KalamaHelperHelperA;
import me.matl114.utils.commands.params.api.ArgumentType;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.commands.params.api.KalamaHelperHelperF;
import me.matl114.utils.commands.params.types.ExecutePos;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.mutable.MutableObject;
import org.joml.Vector3d;

public class Warps extends BaseModule {
   public static final String sA = "$";
   public final Map<String, Map<String, Map<String, Vec3d>>> sz;
   private static final String sv = "\\|";
   private static final String sw = "warp-entries";
   private static final String su = "|";
   public final List<String> sy;
   private final ArgumentType<String> sB;
   private final FileStorage sx = FileManager.getInstance().o("warp-saves.nbt");

   @Nonnull
   public Map<String, Vec3d> BE(String world) {
      String var2 = CommonUtils.getServerName();
      return this.sz.getOrDefault(var2, Map.of()).getOrDefault(world, Map.of());
   }

   private Object computeEmpty(Map map, String[] k, int idx) {
      if (idx == k.length - 1) {
         return map.remove(k[idx]);
      } else {
         MutableObject var4 = new MutableObject();
         map.compute(k[idx], (ks, v) -> {
            if (v instanceof Map var6 && !var6.isEmpty()) {
               var4.setValue(this.computeEmpty(var6, k, idx + 1));
               if (!var6.isEmpty()) {
                  return var6;
               }
            }

            return null;
         });
         return var4.getValue();
      }
   }

   private void onList(ArgumentInputStream re) {
      String var2 = re.s(CommonUtils::getServerName);
      Debug.b(Text.literal("==".repeat(10)).formatted(Formatting.GREEN));
      Debug.b(Text.literal("== 当前服务器" + var2 + "传送点列表 ==").formatted(Formatting.GREEN));
      Map var3 = this.BF(var2);
      int var4 = 0;

      for (Entry var6 : ((java.util.Set<Entry>)(var3).entrySet())) {
         for (Entry var8 : ((java.util.Set<Entry>)(((Map)var6.getValue())).entrySet())) {
            Debug.chat(
               Text.literal(++var4 + ":").formatted(Formatting.YELLOW),
               var6.getKey(),
               ChatUtils.textFromLegacyString("$" + (String)var8.getKey()),
               " ",
               ChatUtils.t((Vec3d)var8.getValue())
            );
         }
      }

      Debug.chat();
      Debug.b(Text.literal("==".repeat(10)).formatted(Formatting.GREEN));
   }

   public Stream<String> BH() {
      return this.BG().map(Map::keySet).stream().flatMap(Collection::stream);
   }

   private boolean onRemove(CommandExecution context, ArgumentInputStream re, ArgumentReader rest) {
      String var4 = re.n();
      if (mc.world != null && this.unregisterWarp(getCurrentWorldName().get(), var4)) {
         context.sm("&a移除传送点 " + var4 + " 成功");
      } else {
         context.sm("&c移除传送点失败");
      }

      return true;
   }

   public Optional<Map<String, Vec3d>> BG() {
      return getCurrentWorldName().map(this::BE);
   }

   private boolean onSet(CommandExecution context, ArgumentInputStream re, ArgumentReader rest) {
      String var4 = re.n();
      ExecutePos var5 = re.f();
      Vector3d var6;
      if (var5 == null) {
         var6 = context.sp();
      } else {
         var6 = var5.wu(context);
      }

      Vec3d var7 = new Vec3d(var6.x, var6.y, var6.z);
      if (mc.world != null && this.BC(getCurrentWorldName().get(), var4, var7)) {
         context.sm("&a注册传送点 " + var4 + " 成功");
         context.sn(Text.literal("位置: ").formatted(Formatting.GREEN).append(ChatUtils.t(var7)));
      } else {
         context.sm("&c注册传送点失败!");
      }

      return true;
   }

   private void onCacheUpdate() {
      Map var1 = Map.of("warp-entries", this.sy);
      this.sx.write(var1, JavaOps.INSTANCE);
   }

   public boolean BA(String name) {
      return !name.contains(" ") && !name.contains("|");
   }

   @Nonnull
   public Map<String, Map<String, Vec3d>> BF(String serverName) {
      return this.sz.getOrDefault(serverName, Map.of());
   }

   public void onTpaToWarp(Event<KalamaHelperHelperI<KalamaHelperHelperZX>> tpaRequest) {
      KalamaHelperHelperZX var2 = (KalamaHelperHelperZX)((KalamaHelperHelperI)tpaRequest.b).b();
      switch (var2.d) {
         case UK:
            Stream var8 = this.sB.getTab(var2.e, var2.f);
            if (var8 != null) {
               var8.forEach(((KalamaHelperHelperE)var2).tab::add);
            }
            break;
         case UJ:
            KalamaHelperHelperEX var3 = (KalamaHelperHelperEX)var2;
            if (var3.hasResolved()) {
               return;
            }

            ArgumentReader var4 = var3.c;
            if (var4.hasNext()) {
               String var5 = var4.g();
               if (var5.startsWith("$")) {
                  var4.h();
                  String var6 = var5.substring(1);
                  Vec3d var7 = this.BG().get().get(var6);
                  if (var7 != null) {
                     var3.b.accept(Text.literal("使用传送点 " + var5 + " ").append(ChatUtils.t(var7)));
                     var3.a = Optional.of(var7);
                  } else {
                     var3.b.accept(Text.literal("不存在这样的传送点: " + var5).formatted(Formatting.RED));
                     var3.a = Optional.empty();
                  }
               }
            }
      }
   }

   private boolean By(String serverName, String worldName, String warpName) {
      Object var4 = this.computeEmpty(this.sz, new String[]{serverName, worldName, warpName}, 0);
      if (var4 != null) {
         String var5 = String.join("|", serverName, worldName, warpName) + "|";
         if (var4 instanceof Vec3d var6) {
            var5 = var5 + Long.toString(this.toRangedVec(var6));
         }

         boolean var7 = this.sy.remove(var5);
         this.onCacheUpdate();
         return var7;
      } else {
         return false;
      }
   }

   public long toRangedVec(Vec3d vec3d) {
      BlockPos var2 = BlockPos.ofFloored(vec3d);
      return BlockPos.asLong(var2.getX(), var2.getY(), var2.getZ());
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.bx().c(KalamaHelperHelperZX.class), this::onTpaToWarp);
      this.registerCommandBootstrap(this::BL);
   }

   private boolean putInternal(String serverName, String worldName, String warpName, Vec3d pos) {
      if (this.isInRange(pos)) {
         this.By(serverName, worldName, warpName);
         this.sz.computeIfAbsent(serverName, k -> new LinkedHashMap<>()).computeIfAbsent(worldName, k -> new LinkedHashMap<>()).put(warpName, pos);
         this.sy.add(String.join("|", serverName, worldName, warpName, Long.toString(this.toRangedVec(pos))));
         this.onCacheUpdate();
         return true;
      } else {
         return false;
      }
   }

   public static Optional<String> getCurrentWorldName() {
      return mc.world != null ? Optional.of(mc.world.getRegistryKey().getValue().toString()) : Optional.empty();
   }

   public boolean BC(String world, String name, Vec3d pos) {
      if (this.BA(name)) {
         String var4 = CommonUtils.getServerName();
         return this.putInternal(var4, world, name, pos);
      } else {
         return false;
      }
   }

   private void BO(ArgumentInputStream re) {
      String var2 = re.s(getCurrentWorldName()::get);
      Debug.b(Text.literal("==".repeat(10)).formatted(Formatting.GREEN));
      Debug.b(Text.literal("== 当前世界" + var2 + "传送点列表 ==").formatted(Formatting.GREEN));
      Map var3 = this.BE(var2);
      int var4 = 0;

      for (Entry var6 : ((java.util.Set<Entry>)(var3).entrySet())) {
         Debug.chat(
            Text.literal(++var4 + ":").formatted(Formatting.YELLOW),
            ChatUtils.textFromLegacyString("$" + (String)var6.getKey()),
            " ",
            ChatUtils.t((Vec3d)var6.getValue())
         );
      }

      Debug.chat();
      Debug.b(Text.literal("==".repeat(10)).formatted(Formatting.GREEN));
   }

   public void BL(MainCommand mainCommand) {
      TreeSubCommand var2 = mainCommand.bC().a("warp_command").k();
      var2.subBuilder(SubCommand.bp())
         .u("warp")
         .z(
            m -> m.<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
               .u("list")
               .x("message.command.warp_command.warp.list.help")
               .A(
                  KalamaHelperHelperA.a()
                     .B("world")
                     .d(() -> mc.getNetworkHandler().getWorldKeys().stream().map(RegistryKey::getValue).map(Identifier::toString))
                     .v()
               )
               .z(e -> e.executor(KalamaHelperHelperH.i(this::BO)))
               .r()
               .subBuilder(SubCommand.bo())
               .u("listall")
               .x("message.command.warp_command.warp.listall.help")
               .A(KalamaHelperHelperA.a().B("server").d(() -> this.sz.keySet().stream()).v())
               .z(e -> e.executor(KalamaHelperHelperH.i(this::onList)))
               .r()
         )
         .r();
      var2.subBuilder(SubCommand.bo())
         .u("setwarp")
         .x("message.command.warp_command.setwarp.help")
         .A(KalamaHelperHelperA.a().B("warpname").l("<输入自定义名称>").v())
         .A(KalamaHelperHelperA.<KalamaHelperHelperUX, ExecutePos>b(KalamaHelperHelperUX::new).B("warppos").v())
         .z(e -> e.executor(this::onSet))
         .r();
      var2.subBuilder(SubCommand.bo())
         .u("delwarp")
         .x("message.command.warp_command.delwarp.help")
         .A(KalamaHelperHelperA.a().B("warpname").c(KalamaHelperHelperF.g(this::BH)).v())
         .z(e -> e.executor(this::onRemove))
         .r();
   }

   public Warps() {
      super("Warps");
      this.sy = new ArrayList<>();
      this.sz = new LinkedHashMap<>();
      this.sy.clear();
      this.sz.clear();
      Map var1 = this.sx.b(JavaOps.INSTANCE);

      for (String var3 : List.copyOf(var1.getOrDefault("warp-entries", List.of()))) {
         String[] var4 = var3.split("\\|");

         Vec3d var9;
         String var10;
         String var11;
         String var13;
         try {
            String var5 = var4[var4.length - 1];
            long var6 = Long.parseLong(var5);
            BlockPos var8 = BlockPos.fromLong(var6);
            var9 = new Vec3d(var8.getX(), var8.getY(), var8.getZ());
            var10 = var4[0];
            var11 = var4[1];
            String[] var12 = new String[var4.length - 3];
            System.arraycopy(var4, 2, var12, 0, var12.length);
            var13 = String.join("|", var12);
         } catch (Throwable var14) {
            continue;
         }

         this.putInternal(var10, var11, var13, var9);
      }

      this.sB = KalamaHelperHelperA.a().B("warp_name").d(() -> this.BG().map(Map::keySet).stream().flatMap(Collection::stream).map(s -> "$" + s)).v();
   }

   public boolean isInRange(Vec3d pos) {
      BlockPos var2 = BlockPos.ofFloored(pos);
      return Math.abs(var2.getX()) < 33554432 && Math.abs(var2.getY()) < 2048 && Math.abs(var2.getZ()) < 33554432;
   }

   public boolean unregisterWarp(String world, String name) {
      String var3 = CommonUtils.getServerName();
      return this.By(var3, world, name);
   }
}
