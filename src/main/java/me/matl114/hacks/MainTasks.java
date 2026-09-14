package me.matl114.hacks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import me.matl114.accessors.gui.ScreenAccess;
import me.matl114.events.Listener;
import me.matl114.events.annotations.Modifiable;
import me.matl114.gui.complex.config.ConfigurateNewStyleScreen;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModuleEntry;
import me.matl114.hacks.api.ModuleGroup;
import me.matl114.hacks.api.ModuleManager;
import me.matl114.hacks.api.WrapperConfigRef;
import me.matl114.hacks.modules.HackModules;
import me.matl114.hacks.modules.task.BindCommand;
import me.matl114.hacks.modules.task.ClickGui;
import me.matl114.hacks.modules.task.ConfigManager;
import me.matl114.hacks.modules.task.EventCommand;
import me.matl114.hacks.modules.task.IQBoost;
import me.matl114.hacks.modules.task.Modules;
import me.matl114.hacks.modules.task.Proxy;
import me.matl114.hacks.modules.task.ServerStorage;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.Config;
import me.matl114.managers.config.ConfigEnum;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.InventoryUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

public class MainTasks {
   private static ServerStorage i;
   private static BindCommand g;
   public static IQBoost k;
   private static Proxy j;
   private static final MinecraftClient a = MinecraftClient.getInstance();
   private static ClickGui e;
   private static ConfigManager f;
   private static Modules d;
   public static final Text b = Text.translatable("multiplayer.status.quitting");
   private static EventCommand h;
   private static final ModuleGroup c = new ModuleGroup("Tasks");

   public static void a() {
   }

   public static IQBoost C() {
      return k;
   }

   public static ServerStorage A() {
      return i;
   }

   static {
      c.registerFactories(MainTasks::t);
      HackModules.registerModuleGroup(c);
      MineTasks.init();
      ChatTasks.init();
      RenderTasks.init();
      InvTasks.a();
      CombatTasks.init();
      WorldTasks.a();
      MovTasks.a();
      InteractionTasks.a();
      SurvivalTasks.a();
      SlimefunTasks.a();
      ModelTasks.init();
      ACTasks.init();
      ExtraTasks.a();
   }

   @Modifiable
   public static void disconnect() {
      if (a.world != null) {
         a.world.disconnect();
      }

      a.disconnect(new ProgressScreen(true), false);
      TitleScreen var0 = new TitleScreen();
      a.setScreen(new MultiplayerScreen(var0));
   }

   public static void generateWritableBookContent(String[] args) {
      if (a.player != null) {
         if (a.player.getMainHandStack().getItem() == Items.WRITABLE_BOOK) {
            Debug.b("生成了书内容");
            String var1 = "§b§k" + "1a锕β".repeat(250);
            a.getNetworkHandler()
               .sendPacket(
                  new BookUpdateC2SPacket(
                     InventoryUtils.getSelectedSlot(),
                     Collections.nCopies(100, var1),
                     args.length > 0 ? Optional.of(String.join("\n", args)) : Optional.empty()
                  )
               );
         } else {
            Debug.b("手持物品不是书");
         }
      }
   }

   @Modifiable
   public static void o(Config config) {
      ConfigurateNewStyleScreen var1 = new ConfigurateNewStyleScreen(Config.getConfigs().stream().toList());
      var1.setGlobal(config);
      ScreenAccess.of(var1).openFromCurrent();
   }

   public static ModuleGroup u() {
      return c;
   }

   @Modifiable
   public static void p(BaseModule module) {
      e.pn(module);
   }

   @Modifiable
   public static void runSpecialTask(String taskId, String[] args) {
      try {
         switch (taskId) {
            case "xray_demo":
               int var4 = Integer.parseInt(args[0]);
               int var5 = Integer.parseInt(args[1]);
               int var6 = Integer.parseInt(args[2]);
               break;
            case "writable_book_generate":
               generateWritableBookContent(args);
               break;
            case "strider_fix":
               e(args);
               break;
            case "client_crash":
               f(args);
               break;
            case "client_lite_crash":
               g(args);
               break;
            case "check_translation_key":
               checkTranslationKey(args);
               break;
            case "show_window":
               l(args);
         }
      } catch (Throwable var7) {
         Debug.f(var7);
      }
   }

   public static void e(String[] args) {
   }

   public static ClickGui w() {
      return e;
   }

   public static Proxy B() {
      return j;
   }

   @Modifiable
   public static void n() {
      ScreenAccess.of(new ConfigurateNewStyleScreen(Config.getConfigs().stream().toList())).openFromCurrent();
   }

   public static void m() {
   }

   public static void f(String[] args) {
      Tasks.l(() -> {
         a.world = null;
         throw new CrashException(new CrashReport("test crash", new NullPointerException()));
      }, 1);
   }

   private static String joinPath(String[] path, int length) {
      if (length <= 0) {
         return "";
      } else {
         StringBuilder var2 = new StringBuilder(path[0]);

         for (int var3 = 1; var3 < length; var3++) {
            var2.append('.').append(path[var3]);
         }

         return var2.toString();
      }
   }

   private static void checkTranslationText(Text text, Set<String> checkedKeys, List<String> missingKeys) {
      if (text instanceof MutableText var3 && var3.getContent() instanceof TranslatableTextContent var5) {
         j(var5.getKey(), checkedKeys, missingKeys);
      }
   }

   public static ConfigManager x() {
      return f;
   }

   public static void checkTranslationKey(String[] args) {
      LinkedHashSet var1 = new LinkedHashSet();
      ArrayList<String> var2 = new ArrayList();
      int var3 = 0;
      int var4 = 0;
      int var5 = 0;
      int var6 = 0;
      int var7 = 0;

      for (ModuleGroup var9 : HackModules.getModuleGroups()) {
         for (BaseModule var11 : var9.getModules()) {
            for (WrapperConfigRef var13 : var11.getEditableConfig()) {
               if (!var13.isExperimental()) {
                  var3++;
                  j(var13.getKeyName(), var1, var2);
               }
            }

            for (ModuleEntry var28 : var11.getModuleEntries().toList()) {
               var4++;
               j(var28.getToggleKey(), var1, var2);
            }

            var6++;
            if (var11.hasEditableConfig()) {
               checkTranslationText(e.getModuleName(var11), var1, var2);
            }
         }
      }

      for (Map var17 : ConfigEnum.registeredConfigs.values()) {
         for (ConfigEnum var22 : var17.values()) {
            var5++;
            checkTranslationText(var22.resultAsString(), var1, var2);
         }
      }

      for (Config var18 : Config.getConfigs()) {
         LinkedHashSet<String> var21 = new LinkedHashSet();

         for (String var26 : var18.getVisiblePaths()) {
            String[] var29 = Config.cutToPath(var26);
            if (var29.length > 0) {
               var21.add(var29[0]);
            }

            if (var29.length > 1) {
               var21.add(joinPath(var29, var29.length - 1));
            }
         }

         for (String var27 : var21) {
            var7++;
            j("config.index." + var27, var1, var2);
         }
      }

      if (var2.isEmpty()) {
         Debug.b(
            Text.literal(
                  "翻译检查完成，WrapperConfig=" + var3 + "，快捷键入口=" + var4 + "，ConfigEnum=" + var5 + "，ClickGui模块名=" + var6 + "，config.index=" + var7 + "，未发现缺失翻译"
               )
               .formatted(Formatting.GREEN)
         );
      } else {
         Debug.b(
            Text.literal(
                  "翻译检查完成，WrapperConfig="
                     + var3
                     + "，快捷键入口="
                     + var4
                     + "，ConfigEnum="
                     + var5
                     + "，ClickGui模块名="
                     + var6
                     + "，config.index="
                     + var7
                     + "，共发现缺失翻译 "
                     + var2.size()
                     + " 个"
               )
               .formatted(Formatting.YELLOW)
         );

         for (String var19 : var2) {
            Debug.b(Text.literal(" - " + var19).formatted(Formatting.RED));
         }
      }
   }

   public static Modules v() {
      return d;
   }

   @Modifiable
   public static void q() {
      Tasks.l(MainTasks::disconnectImmediately, 0);
   }

   public static void g(String[] args) {
      Tasks.l(() -> {
         throw new CrashException(new CrashReport("test crash", new NullPointerException()));
      }, 1);
   }

   public static EventCommand z() {
      return h;
   }

   @Modifiable
   public static void disconnectImmediately() {
      disconnect();
      if (Listener.L() != null && Listener.L().isOpen()) {
         Listener.L().disconnect(b);
      }
   }

   public static void l(String[] args) {
   }

   public static BindCommand y() {
      return g;
   }

   private static void t(ModuleManager m) {
      d = new Modules().register(m);
      e = new ClickGui().register(m);
      f = new ConfigManager().register(m);
      g = new BindCommand().register(m);
      h = new EventCommand().register(m);
      i = new ServerStorage().register(m);
      j = new Proxy().register(m);
      k = new IQBoost().register(m);
   }

   private static void j(String translationKey, Set<String> checkedKeys, List<String> missingKeys) {
      if (translationKey != null && !translationKey.isEmpty() && checkedKeys.add(translationKey)) {
         if (!ChatUtils.hasTranslation(translationKey)) {
            missingKeys.add(translationKey);
            Debug.e("Missing translation key for", translationKey);
         }
      }
   }

   public static List<String> b() {
      return List.of("xray_demo", "writable_book_generate", "strider_fix", "client_crash", "client_lite_crash", "check_translation_key", "show_window");
   }
}
