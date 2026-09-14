package me.matl114.hacks;

import com.google.common.base.Predicates;
import com.mojang.brigadier.tree.CommandNode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import me.matl114.accessors.gui.ScreenAccess;
import me.matl114.commands.MainCommand;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.gui.complex.invcache.InventoryViewScreen;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModuleEntry;
import me.matl114.hacks.api.ModuleGroup;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.modules.HackModules;
import me.matl114.hacks.modules.inv.ChestHistory;
import me.matl114.hacks.modules.move.MoveSubHelperL;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.modules.survival.SeedOre;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.Config;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.ClientUtils;
import me.matl114.utils.CommonUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.KalamaHelperHelperAk;
import me.matl114.utils.KalamaHelperHelperPX;
import me.matl114.utils.WorldUtils;
import me.matl114.utils.commands.commandGroup.AbstractMainCommand;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperH;
import me.matl114.utils.commands.commandGroup.SubCommand;
import me.matl114.utils.commands.commandGroup.TreeSubCommand;
import me.matl114.utils.commands.params.ArgumentInputStream;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.inventory.ItemStackSample;
import me.matl114.versioned.api.VEntity;
import me.matl114.versioned.api.VRecord;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.visitor.NbtTextFormatter;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;

public class KalamaHelperHelperNX extends AbstractMainCommand {
   List<String> i;
   TreeSubCommand g = this.bC().a("").k();
   List<String> j;
   List<String> h;

   private void onResource0(String name, List datas) {
      Debug.b(Text.literal(name + "所拥有的数据:").formatted(Formatting.GREEN));

      for (Object var4 : datas) {
         Debug.b(var4);
      }
   }

   public void onResource(ArgumentInputStream re) {
      String var2 = re.q(this.i);
      String var3 = re.n();
      Identifier var4 = Identifier.tryParse(var3);
      boolean var5 = var3.contains(":");
      new ArrayList();
      switch (var2) {
         case "world":
            List var11 = ChatTasks.j
               .getNetworkHandler()
               .getWorldKeys()
               .stream()
               .<Identifier>map(RegistryKey::getValue)
               .filter(u -> var4 == null || u.getPath().contains(var4.getPath()) && (!var5 || u.getNamespace().contains(var4.getNamespace())))
               .toList();
            this.onResource0(var2, var11);
            break;
         case "command":
            List var10 = ChatTasks.j
               .getNetworkHandler()
               .getCommandDispatcher()
               .getRoot()
               .getChildren()
               .stream()
               .<String>map(CommandNode::getName)
               .filter(u -> u.contains(var3))
               .sorted(String::compareTo)
               .toList();
            this.onResource0(var2, var10);
            break;
         case "seed":
            List var9 = List.of(
               Text.literal("服务端加密种子: ").append(ChatUtils.getDisplayedLong(ChatTasks.j.world.getBiomeAccess().seed)),
               Text.literal("当前绑定种子: ")
                  .append((Text)(SeedOre.INSTANCE.hasCurrentSeed() ? ChatUtils.getDisplayedLong(SeedOre.INSTANCE.getCurrentSeed()) : Text.literal("暂未输入")))
            );
            this.onResource0(var2, var9);
            break;
         case "plugins":
            Debug.b(Text.literal("导出Command Namespace获取的数据:").formatted(Formatting.GREEN));
            List var6 = ClientUtils.getServerCommands().stream().map(n -> {
               String[] var1 = n.split(":");
               return var1.length >= 2 ? var1[0] : null;
            }).filter(Objects::nonNull).filter(u -> u.contains(var3)).distinct().sorted(String::compareTo).toList();
            this.onResource0(var2, var6);
            Debug.b(Text.literal("导出Version Tab获取的数据:").formatted(Formatting.GREEN));
            ClientUtils.c()
               .thenAccept(
                  list -> this.onResource0(
                     var2, list.stream().map(str -> str.toLowerCase(Locale.ROOT)).filter(u -> u.contains(var3)).distinct().sorted(String::compareTo).toList()
                  )
               );
            break;
         default:
            Debug.b(Text.literal("不支持的资源: " + var2).formatted(Formatting.RED));
      }
   }

   public boolean u(PlayerEntity player, ArgumentInputStream s, ArgumentReader reader) {
      String var4 = s.n();
      String[] var5 = reader.k();
      CompletableFuture.runAsync(() -> {
         try {
            MainTasks.runSpecialTask(var4, var5);
         } catch (Throwable var3) {
            Debug.chat("运行Task出现错误!:", var3.getMessage());
            Debug.f(var3);
         }
      });
      return true;
   }

   public void C(ArgumentInputStream re) {
      String var2 = re.o();

      for (ModuleGroup var4 : HackModules.getModuleGroups()) {
         for (BaseModule var6 : var4.getModules()) {
            for (ModuleEntry var8 : var6.getModuleEntries().toList()) {
               String var9 = ChatUtils.H(var8.getToggleKey());
               if (Objects.equals(var2, var9)) {
                  HotKeyUtils.f(var8.getPath(), var8.getFlagRef()).run();
                  return;
               }
            }
         }
      }

      Debug.b(ChatUtils.textFromLegacyString("&e找不到模块项: " + var2));
   }

   public boolean t(PlayerEntity player, ArgumentInputStream s, ArgumentReader reader) {
      String var4 = s.n();
      String[] var5 = reader.k();

      try {
         MainTasks.runSpecialTask(var4, var5);
      } catch (Throwable var7) {
         Debug.chat("运行Task出现错误!:", var7.getMessage());
         Debug.f(var7);
      }

      return true;
   }

   public void onListRegistry(ArgumentInputStream re) {
      Identifier var2 = Identifier.tryParse(re.n());
      RegistryKey var3 = RegistryKey.ofRegistry(var2);
      Registry var4 = (Registry)ItemStackUtils.registry().getOptional(var3).orElse(null);
      if (var4 != null) {
         String var5 = re.n();
         Debug.b(Text.literal(var2.toString() + "所拥有的注册项:").formatted(Formatting.GREEN));
         Identifier var6 = Identifier.tryParse(var5);
         boolean var7 = var5.contains(":");

         for (Object var9 : var4.getKeys()) {
            Identifier var10 = ((RegistryKey)var9).getValue();
            String var11 = var10.getPath();
            if (var6 == null || var11.contains(var6.getPath()) && (!var7 || var10.getNamespace().contains(var6.getNamespace()))) {
               Debug.b(var10);
            }
         }
      } else {
         Debug.b(Text.literal("不存在的注册表: " + var2).formatted(Formatting.RED));
      }
   }

   public void onInfo(ArgumentInputStream re) {
      String var2 = re.q(this.j);
      String var3 = re.n();
      PlayerEntity var4 = Objects.equals("#me", var3) ? (PlayerEntity)ChatTasks.j.player : EntityUtils.getPlayerByName(var3);
      if (var4 != null) {
         Debug.chat("Information about player : ", var4.getNameForScoreboard());
      }

      switch (var2) {
         case "death":
            if (var4 != null) {
               Optional var20 = var4.getLastDeathPos();
               if (var20.isPresent()) {
                  GlobalPos var25 = (GlobalPos)var20.get();
                  RegistryKey var28 = var25.dimension();
                  Debug.chat("Last Death Point [World:", var28.getValue(), ",Pos:", ChatUtils.getDisplayedLocation(Vec3d.of(var25.pos())), "]");
               } else {
                  Debug.b("Last Death Point Not Present");
               }
            } else {
               Debug.chat("找不到玩家", var3);
            }
            break;
         case "spawn":
            Debug.b("当前世界的出生点:");
            BlockPos var19 = ChatTasks.j.world.getSpawnPos();
            RegistryKey var24 = ChatTasks.j.world.getRegistryKey();
            Debug.chat("World Spawn Point [World:", var24.getValue(), ",Pos:", ChatUtils.getDisplayedLocation(Vec3d.of(var19)), "]");
            break;
         case "nbt":
            if (var4 != null) {
               NbtCompound var18 = VEntity.b((Entity)var4);
               var18.remove("Inventory");
               var18.remove("EnderItems");
               Debug.b(new NbtTextFormatter("").apply(var18));
            } else {
               Debug.chat("找不到玩家", var3);
            }
            break;
         case "inventory":
            if (var4 != null) {
               PlayerInventory var17 = var4.getInventory();
               Tasks.l(
                  () -> ScreenAccess.of(new InventoryViewScreen(var17, Text.literal("背包预览 - " + var4.getNameForScoreboard()), new ItemStack(Items.CHEST)))
                     .openFromCurrent(),
                  2
               );
            } else {
               Debug.chat("找不到玩家", var3);
            }
            break;
         case "trackinventory":
            if (var4 != null) {
               MoveSubHelperL var16 = PlayerStateManager.INSTANCE.nZ((PlayerEntity)var4);
               List var23;
               if (var16 != null) {
                  var23 = var16.j.stream().map(ItemStackSample::fS).toList();
               } else {
                  var23 = List.of();
               }

               Tasks.l(
                  () -> ScreenAccess.of(
                        new InventoryViewScreen(InventoryUtils.d(var23), Text.literal("背包追踪预览 - " + var4.getNameForScoreboard()), new ItemStack(Items.BARRIER))
                     )
                     .openFromCurrent(),
                  2
               );
            } else {
               Debug.chat("找不到玩家", var3);
            }
            break;
         case "ender":
            if (var4 != null) {
               net.minecraft.inventory.Inventory var15 = var4 == ChatTasks.j.player ? ChestHistory.INSTANCE.PY() : var4.getEnderChestInventory();
               Tasks.l(
                  () -> ScreenAccess.of(
                        new InventoryViewScreen(var15, Text.literal("末影箱预览 - " + var4.getNameForScoreboard()), new ItemStack(Items.ENDER_CHEST))
                     )
                     .openFromCurrent(),
                  2
               );
            } else {
               Debug.chat("找不到玩家", var3);
            }
            break;
         case "plist":
            Debug.b(Text.literal("当前可视的玩家列表").formatted(Formatting.GREEN));
            ChatTasks.j
               .getNetworkHandler()
               .getPlayerList()
               .stream()
               .sorted(Comparator.comparing(e -> VRecord.getName(e.getProfile())))
               .map(
                  entry -> {
                     MutableText var1 = Text.literal("%-16s (Display: ".formatted(VRecord.getName(entry.getProfile())))
                        .append((Text)(entry.getDisplayName() == null ? Text.literal("null") : entry.getDisplayName()))
                        .append(Text.literal(", GameMode: " + entry.getGameMode().name() + ")"));
                     Debug.f(var1);
                     return var1;
                  }
               )
               .forEach(Debug::b);
            break;
         case "team":
            String var14 = Objects.equals(var3, "#me") ? ChatTasks.j.player.getNameForScoreboard() : var3;
            PlayerListEntry var22 = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(var14);
            if (var22 != null) {
               Team var27 = var22.getScoreboardTeam();
               if (var27 != null) {
                  Debug.chat("该玩家所在Team: ", var27.getName());
                  Debug.chat(Text.literal("展示名称: ").formatted(Formatting.GRAY), var27.getDisplayName() == null ? "" : var27.getDisplayName());
                  Debug.chat(Text.literal("前缀: ").formatted(Formatting.GRAY), var27.getPrefix() == null ? "" : var27.getPrefix());
                  Debug.chat(Text.literal("后缀: ").formatted(Formatting.GRAY), var27.getSuffix() == null ? "" : var27.getSuffix());
                  Debug.chat(Text.literal("颜色: ").formatted(Formatting.GRAY), var27.getColor() == null ? "" : var27.getColor());
                  Debug.chat(Text.literal("友伤: ").formatted(Formatting.GRAY), var27.isFriendlyFireAllowed());
                  Debug.chat(Text.literal("显示隐身队友: ").formatted(Formatting.GRAY), var27.shouldShowFriendlyInvisibles());
                  Debug.b(Text.literal("队员列表:").formatted(Formatting.GRAY));
                  Debug.b(Text.literal("-------------------").formatted(Formatting.GREEN));

                  for (String var11 : var27.getPlayerList()) {
                     Debug.b(var11);
                  }
               } else {
                  Debug.b("该玩家没有Team");
               }
            } else {
               Debug.chat("找不到玩家", var3);
            }
            break;
         case "pentry":
            String var13 = Objects.equals(var3, "#me") ? ChatTasks.j.player.getNameForScoreboard() : var3;
            PlayerListEntry var21 = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(var13);
            if (var21 != null) {
               Debug.b("查询到PlayerEntry");
               Debug.chat(Text.literal("名字: ").formatted(Formatting.GRAY), VRecord.getName(var21.getProfile()));
               Debug.chat(
                  Text.literal("UUID: ").formatted(Formatting.GRAY), ChatUtils.y(VRecord.getId(var21.getProfile()).toString()).formatted(Formatting.GREEN)
               );
               Debug.chat(
                  Text.literal("Property: ").formatted(Formatting.GRAY),
                  ChatUtils.getHoverShowText("[点击查看具体数据]", List.of(Text.literal(VRecord.getProperties(var21.getProfile()).toString())))
               );
               Debug.chat(Text.literal("GameMode: ").formatted(Formatting.GRAY), var21.getGameMode().name());
               Debug.chat(
                  Text.literal("DisplayName: ").formatted(Formatting.GRAY), var21.getDisplayName() == null ? Text.literal("null") : var21.getDisplayName()
               );
               ArrayList var26 = new ArrayList();
               var26.add(Text.literal("Latency: " + var21.getLatency()));
               var26.add(Text.literal("MessageVerifier: " + var21.getMessageVerifier()));
               var26.add(Text.literal("SkinTextures: " + var21.getSkinTextures()));
               var26.add(Text.literal("Session: " + var21.getSession()));
               Debug.chat(Text.literal("More: ").formatted(Formatting.GRAY), ChatUtils.getHoverShowText("[点击查看具体数据]", var26));
            } else {
               Debug.b("该玩家没有PlayerEntry");
            }
            break;
         case "server":
            Debug.b("当前服务器:");
            String var12 = CommonUtils.getServerName();
            Debug.chat(ChatUtils.y(var12).formatted(Formatting.GREEN), "|", ChatTasks.j.world.getRegistryKey().getValue());
            break;
         case "waypoint":
            Debug.b("查询中");
            PlayerListEntry var7;
            Object var8;
            if ((var7 = ChatTasks.j.getNetworkHandler().getPlayerListEntry(var3)) != null) {
               String var9 = VRecord.getId(var7.getProfile()).toString();
               var8 = (Predicate<KalamaHelperHelperAk>)s -> var9.equalsIgnoreCase((String)s.b().map(UUID::toString, Function.identity()));
            } else {
               var8 = Predicates.alwaysTrue();
            }

            WorldUtils.d()
               .filter((Predicate<? super KalamaHelperHelperAk>)var8)
               .forEach(
                  s -> {
                     Debug.chat("Information about waypoint:", s.b().map(UUID::toString, Function.identity()));
                      Optional<PlayerListEntry> var1 = s.b().left().isPresent()
                         ? Optional.ofNullable(ChatTasks.j.getNetworkHandler().getPlayerListEntry(s.b().left().get()))
                         : Optional.ofNullable(ChatTasks.j.getNetworkHandler().getPlayerListEntry(s.b().right().get()));
                      var1.ifPresent(playerListEntry -> Debug.b("Potential Owner: " + VRecord.getName(playerListEntry.getProfile())));
                     Debug.b("config: ");
                     Debug.b(new NbtTextFormatter("").apply(s.c()));
                     Debug.b("type: " + s.d().type());
                     String var2x = s.d().type();
                     switch (var2x) {
                        case "Pos":
                           Vec3d var6 = ((me.matl114.utils.KalamaHelperHelperDX)s.d()).jk();
                           Debug.chat("Pos :", var6.x, var6.y, var6.z);
                           break;
                        case "Chunk":
                           ChunkPos var5 = ((me.matl114.utils.KalamaHelperHelperE)s.d()).Im();
                           Debug.chat("Chunk :", var5.x, var5.z);
                           break;
                        case "Direction":
                           float var4x = ((KalamaHelperHelperPX)s.d()).azimuth();
                           Debug.chat("Azimuth :", var4x);
                     }
                  }
               );
      }
   }

   public void r(ArgumentInputStream args) {
      String var2 = args.o();
      byte var4 = -1;
      switch (var2.hashCode()) {
         case 906460851:
            if (var2.equals("clickgui")) {
               var4 = 0;
            }
         default:
            switch (var4) {
               case 0:
                  Tasks.l(MainTasks.w()::resetGui, 1);
            }
      }
   }

   public Stream<String> supplyModule() {
      return HackModules.getModuleGroups()
         .stream()
         .flatMap(s -> s.getModules().stream())
         .flatMap(b -> b.getModuleEntries().map(ModuleEntry::getToggleKey).map(ChatUtils::H));
   }

   public Stream<String> onInfoTab(String string) {
      return switch (string) {
         case "nbt", "inventory", "trackinventory", "ender" -> EntityUtils.getWorldPlayerNames(true);
         case "pentry", "team" -> WorldUtils.getPlayerListNames();
         case "waypoint" -> WorldUtils.c();
         default -> Stream.empty();
      };
   }

   public void A(ArgumentInputStream re) {
      ModulePreset var2 = re.p(ModulePreset.class);
      Listener.bx().b(new Event<>(new me.matl114.events.impl.KalamaHelperHelperI<>(ModulePreset.class, var2), false, false));
      Debug.e("已经加载", var2.name(), "配置预设");
      Config.launchSaveTasks();
   }

   public KalamaHelperHelperNX() {
      this.g
         .subBuilder(SubCommand.bo())
         .u("reload")
         .x("message.command.sfh.reload.help")
         .A(me.matl114.utils.commands.params.KalamaHelperHelperA.a().B("what").m(List.of("command", "module", "all"), "command").v())
         .z(e -> e.executor(KalamaHelperHelperH.i(this::q)))
         .r();
      this.g
         .subBuilder(SubCommand.bo())
         .u("reset")
         .x("message.command.sfh.reset.help")
         .A(me.matl114.utils.commands.params.KalamaHelperHelperA.a().B("what").k(List.of("clickgui")).v())
         .z(e -> e.executor(KalamaHelperHelperH.i(this::r)))
         .r();
      this.h = List.of("guide", "rtype", "vanilla", "saved", "itemedit", "invcache", "config", "scanner", "clickgui");
      this.g
         .subBuilder(SubCommand.bo())
         .u("openmenu")
         .x("message.command.sfh.openmenu.help")
         .A(me.matl114.utils.commands.params.KalamaHelperHelperA.a().B("page").m(this.h, "guide").v())
         .z(e -> e.executor(KalamaHelperHelperH.i(this::onOpenMenu)))
         .r();
      this.g
         .subBuilder(SubCommand.bo())
         .u("specialtask")
         .x("message.command.sfh.task.help")
         .A(me.matl114.utils.commands.params.KalamaHelperHelperA.a().B("taskid").d(() -> MainTasks.b().stream()).v())
         .z(e -> e.executor(KalamaHelperHelperH.f(this::t)))
         .r();
      this.g
         .subBuilder(SubCommand.bo())
         .u("asyncspecialtask")
         .x("message.command.sfh.asynctask.help")
         .A(me.matl114.utils.commands.params.KalamaHelperHelperA.a().B("taskid").d(() -> MainTasks.b().stream()).v())
         .z(e -> e.executor(KalamaHelperHelperH.f(this::u)))
         .r();
      this.g
         .subBuilder(SubCommand.bo())
         .u("registry")
         .x("message.command.sfh.registry.help")
         .A(
            me.matl114.utils.commands.params.KalamaHelperHelperA.a()
               .B("id")
               .d(
                  () -> ItemStackUtils.registry()
                     .streamAllRegistryKeys()
                     .<Identifier>map(RegistryKey::getValue)
                     .map(i -> "minecraft".equals(i.getNamespace()) ? i.getPath() : i.toString())
               )
               .v()
         )
         .A(me.matl114.utils.commands.params.KalamaHelperHelperA.a().B("filter").l("<namespace_filter>:<path_filter>").b("").v())
         .z(e -> e.executor(KalamaHelperHelperH.i(this::onListRegistry)))
         .r();
      this.i = List.of("world", "command", "seed", "plugins", "version");
      this.g
         .subBuilder(SubCommand.bo())
         .u("resource")
         .x("message.command.sfh.resource.help")
         .A(me.matl114.utils.commands.params.KalamaHelperHelperA.a().B("id").k(this.i).v())
         .A(me.matl114.utils.commands.params.KalamaHelperHelperA.a().B("filter").l("<namespace_filter>:<path_filter>").b("").v())
         .z(e -> e.executor(KalamaHelperHelperH.i(this::onResource)))
         .r();
      this.j = List.of("death", "spawn", "nbt", "inventory", "ender", "trackinventory", "plist", "team", "pentry", "waypoint", "server");
      this.g
         .subBuilder(SubCommand.bo())
         .u("info")
         .x("message.command.sfh.info.help")
         .A(me.matl114.utils.commands.params.KalamaHelperHelperA.a().B("information").k(this.j).v())
         .A(me.matl114.utils.commands.params.KalamaHelperHelperA.a().B("user").s(this::onInfoTab).l("#me").b("#me").v())
         .z(e -> e.executor(KalamaHelperHelperH.i(this::onInfo)))
         .r();
      this.g
         .subBuilder(SubCommand.bo())
         .u("preset")
         .x("message.command.sfh.preset.help")
         .A(me.matl114.utils.commands.params.KalamaHelperHelperA.a().B("preset").p(ModulePreset.class).v())
         .z(e -> e.executor(KalamaHelperHelperH.i(this::A)))
         .r();
      this.g
         .subBuilder(SubCommand.bo())
         .u("runtask")
         .x("message.command.sfh.runtask.help")
         .A(me.matl114.utils.commands.params.KalamaHelperHelperA.a().B("delay").f().v())
         .z(e -> e.executor(new KalamaHelperHelperWX(this)))
         .r();
      this.g
         .subBuilder(SubCommand.bo())
         .u("runrepeat")
         .x("message.command.sfh.runrepeat.help")
         .A(me.matl114.utils.commands.params.KalamaHelperHelperA.a().B("period").f().v())
         .A(me.matl114.utils.commands.params.KalamaHelperHelperA.a().B("time").f().v())
         .z(e -> e.executor(new KalamaHelperHelperQX(this)))
         .r();
      this.g.subBuilder(SubCommand.bo()).u("say").x("message.command.sfh.say.help").z(e -> e.executor((a, b, c) -> {
         ChatTasks.sayMessage(c.j(), false);
         return true;
      })).r();
      this.g.subBuilder(SubCommand.bo()).u("logout").x("message.command.sfh.exit.help").z(e -> e.executor(KalamaHelperHelperH.g(MainTasks::q))).r();
      this.g
         .subBuilder(SubCommand.bo())
         .u("toggle")
         .x("message.command.sfh.toggle.help")
         .A(me.matl114.utils.commands.params.KalamaHelperHelperA.a().B("module").d(this::supplyModule).v())
         .z(e -> e.executor(KalamaHelperHelperH.i(this::C)))
         .r();
   }

   public void q(ArgumentInputStream args) {
      String var2 = args.o();
      switch (var2) {
         case "command":
            Tasks.l(MainCommand::reloadCommand, 1);
            break;
         case "module":
            CompletableFuture.runAsync(() -> ChatTasks.j.execute(HackModules::d));
            break;
         case "all":
            CompletableFuture.runAsync(() -> ChatTasks.j.execute(() -> {
               HackModules.d();
               MainCommand.reloadCommand();
            }));
            break;
         default:
            Debug.b("不支持的参数类型: " + var2);
      }
   }

   public void onOpenMenu(ArgumentInputStream s) {
      String var2 = s.q(this.h);
      switch (var2) {
         case "rtype":
            Tasks.l(SlimefunTasks.w()::aeb, 1);
            break;
         case "vanilla":
            Tasks.l(SlimefunTasks.w()::aec, 1);
            break;
         case "saved":
            Tasks.l(SlimefunTasks.w()::aea, 1);
            break;
         case "itemedit":
            Tasks.l(InvTasks::openEditorForPlayer, 1);
            break;
         case "invcache":
            Tasks.l(InvTasks::K, 1);
            break;
         case "config":
            Tasks.l(MainTasks::n, 1);
            break;
         case "scanner":
            Tasks.l(ExtraTasks.j()::openScannerScreen, 1);
            break;
         case "clickgui":
            Tasks.l(MainTasks.w()::oR, 1);
            break;
         default:
            Tasks.l(SlimefunTasks.w()::adZ, 1);
      }

      Debug.b(Text.literal("成功打开界面").formatted(Formatting.GREEN));
   }
}
