package me.matl114.hacks.modules.extra;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JavaOps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;
import me.matl114.accessors.gui.ScreenAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.gui.McWidgetHelpers;
import me.matl114.gui.basic.AbstractElement;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.RenderHandler;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.complex.RawTextElement;
import me.matl114.gui.complex.config.KalamaHelperHelperD;
import me.matl114.gui.complex.config.KalamaHelperHelperE;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.elements.IconElement$SimpleIconElement;
import me.matl114.gui.elements.LabelElement;
import me.matl114.gui.elements.MultiLineTextElement;
import me.matl114.gui.presets.lists.ListEntryWidgetController;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.FileManager;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.StringRef;
import me.matl114.managers.file.FileStorage;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.config.PropertyTracker;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.WorldIcon;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.ServerInfo.ServerType;
import net.minecraft.client.network.ServerInfo.Status;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ServerScanner extends BaseModule {
   private ListEntryWidgetController wg;
   public KalamaHelperHelperD wv;
   public Object2IntOpenHashMap<String> wx;
   private final StringRef wh;
   private AtomicBoolean wr;
   private final IntRef wk;
   public final FlagRef ae;
   private final FileStorage wo;
   private final List<String> wp;
   private final IntRef wi;
   private final IntRef wj;
   private WeakReference<ContentDelegateWidget<ExecutableWidget>> ko;
   private static final int updateInterval = 200;
   public final Map<String, WorldIcon> wu;
   private String wt;
   public Map<String, ServerInfo> ww;
   private String ws;
   private final IntRef wl;
   private final FlagRef wm;
   public final ModulePath wf = makePath(Configs.j, "other.server-scanner");
   private final FlagRef wn;
   private Text wq;

   private final NbtList list() {
      NbtCompound var1 = this.wo.c(NbtOps.INSTANCE);
      if (var1.get("save-list") instanceof NbtList var5) {
         return var5;
      } else {
         var1 = var1.copy();
         NbtList var3 = new NbtList();
         var1.put("save-list", var3);
         this.wo.write(var1, NbtOps.INSTANCE);
         return var3;
      }
   }

   public void Hj() {
      this.logInfo("");
      if (this.wr.get()) {
         this.warn("当前任务暂未结束");
      } else {
         Debug.e("start scan with", this.wh.get(), this.wi.get(), this.wj.get());
         int var1 = this.wi.get();
         int var2 = this.wj.get();
         if (var1 > var2) {
            this.warn("PortA需要比PortB小");
         } else {
            String var3 = this.wh.get();
            int var4 = this.wl.get();
            int var5 = this.wk.get();
            boolean var6 = this.wm.get();
            boolean var7 = this.wn.get();

            try {
               new InetSocketAddress(var3, var1);
               new InetSocketAddress(var3, var2);
            } catch (Throwable var10) {
               this.warn("输入的IP地址格式有误");
               return;
            }

            this.wr.set(true);
            Random var8 = new Random();
            HashSet var9 = new HashSet<>(this.wp);
            CompletableFuture.runAsync(() -> {
               MultiplayerServerListPinger var10x = new MultiplayerServerListPinger();
               int var11 = var1;
               boolean var12 = false;

               label47:
               for (int var13 = 0; var13 < var4; var13++) {
                  if (!this.wr.get()) {
                     this.logInfo("任务已终止!");
                     break;
                  }

                  int var14 = 0;

                  String var15;
                  do {
                     var11 = var6 ? var8.nextInt(var1, var2) : var11 + 1;
                     if (var11 > var2) {
                        var12 = true;
                        break label47;
                     }

                     var15 = var3 + ":" + var11;
                     if (++var14 > 10000) {
                        var12 = true;
                        break label47;
                     }
                  } while (var9.contains(var15));

                  this.logInfo("扫描" + var15);
                  var9.add(var15);
                  this.pingServer(var10x, var15, var7);

                  try {
                     this.logInfo("间隔中...");
                     Thread.sleep(var5);
                  } catch (Throwable var17) {
                  }
               }

               if (var12) {
                  this.logInfo("扫描中断");
               } else {
                  this.logInfo("扫描结束");
               }

               this.wr.set(false);
            });
         }
      }
   }

   public void Hq(String ip) {
      CompletableFuture.runAsync(() -> {
         MultiplayerServerListPinger var2 = new MultiplayerServerListPinger();
         this.pingServer(var2, ip, false);
      });
   }

   public DrawableWidget createInputWidget() {
      KalamaHelperHelperCX var1 = new KalamaHelperHelperCX(0, 0, 400, 40);
      var1.Q(new KalamaHelperHelperE<>(0, 0, 200, 20, 30, this.wh.createKeyValue("IP")));
      var1.Q(new KalamaHelperHelperE<>(0, 20, 80, 20, 30, this.wi.createKeyValue("PortA")));
      var1.Q(new KalamaHelperHelperE<>(80, 20, 80, 20, 30, this.wj.createKeyValue("PortB")));
      var1.Q(new KalamaHelperHelperE<>(160, 20, 80, 20, 30, this.wk.createKeyValue("DelayMS")));
      var1.Q(new KalamaHelperHelperE<>(240, 20, 80, 20, 30, this.wl.createKeyValue("Limit")));
      var1.Q(new KalamaHelperHelperE<>(320, 20, 40, 20, 20, this.wm.createKeyValue("R")).eJ(List.of(Text.literal("Random"))));
      var1.Q(new KalamaHelperHelperE<>(360, 20, 40, 20, 20, this.wn.createKeyValue("F")).eJ(List.of(Text.literal("Filter"))));
      var1.Q(ExecutableWidget.instance(200, 0, 40, 20).eV(new ButtonElement(TextProvider.c(Text.literal("Scan")), ButtonAction.a(this::Hj))));
      var1.Q(ExecutableWidget.instance(240, 0, 40, 20).eV(new ButtonElement(TextProvider.c(Text.literal("Stop")), ButtonAction.a(this::Hl))));
      var1.Q(ExecutableWidget.instance(280, 0, 40, 20).eV(new ButtonElement(TextProvider.c(Text.literal("AScan")), ButtonAction.a(this::Hk))));
      var1.Q(ExecutableWidget.instance(320, 0, 80, 20).eV(new LabelElement(s -> this.wq, -1, 0)));
      return var1;
   }

   private Text getServerBrandInfoDisplay(ServerInfo serverInfo) {
      return (Text)(serverInfo.getStatus() == Status.UNREACHABLE ? Text.empty() : serverInfo.version);
   }

   private ElementHandler HE(@Nonnull ServerInfo serverInfo) {
      return new ExtraSubHelperM(this, null, null, false, ButtonAction.a(() -> this.connect(serverInfo)), serverInfo).setActive(false);
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.ai().c(MultiplayerScreen.class), this::onButtonAddWhenInitialize);
   }

   public void closeResources() {
      this.wu.clear();
      this.wx.clear();
   }

   public DrawableWidget createOutputWidget() {
      KalamaHelperHelperCX var1 = new KalamaHelperHelperCX(0, 300, 400, 60);
      var1.Q(DisplayWidget.instance(0, 10, 80, 20).setRenderHandler(new ButtonElement(TextProvider.c(Text.literal("Add Server")), ButtonAction.c())));
      ContentDelegateWidget var2 = McWidgetHelpers.c(80, 10, 100, 20, PropertyTracker.event(s -> this.ws = s), this.ws);
      var1.Q(var2);
      var1.Q(
         ExecutableWidget.instance(180, 10, 20, 20).eV(new ButtonElement(TextProvider.c(Text.literal("+").formatted(Formatting.BOLD)), ButtonAction.a(() -> {
            if (!this.ws.isEmpty()) {
               this.Hq(this.ws);
               this.logInfo("已添加 " + this.ws);
            }
         })).aO(TooltipHandler.ap(List.of(Text.literal("Add Server")))))
      );
      var1.Q(ExecutableWidget.instance(200, 10, 100, 20).eV(new ButtonElement(TextProvider.c(Text.literal("Refresh All")), ButtonAction.a(this::Ho))));
      var1.Q(ExecutableWidget.instance(300, 10, 100, 20).eV(new ButtonElement(TextProvider.c(Text.literal("Copy Server List")), ButtonAction.a(() -> {
         JsonArray var1x = new JsonArray();

         for (String var4 : List.copyOf(this.wp)) {
            JsonObject var5 = new JsonObject();
            var5.addProperty("ip", var4);
            ServerInfo var6 = this.ww.get(var4);
            if (var6 != null) {
               JsonObject var7 = new JsonObject();
               var7.addProperty("version", ChatUtils.q(var6.version));
               var7.addProperty("motd", ChatUtils.q(var6.label));
               var7.addProperty("status", var6.getStatus().name().toLowerCase(Locale.ROOT));
               var7.addProperty("player_count", ChatUtils.q(this.getPlayerListDisplay(var6)));
               List<Text> var8 = var6.playerListSummary;
               if (var8 != null && !var8.isEmpty()) {
                  JsonArray var9 = new JsonArray();

                  for (Text var11 : var8) {
                     var9.add(ChatUtils.q(var11));
                  }

                  var7.add("player_list", var9);
               }

               var5.add("meta", var7);
            }

            var1x.add(var5);
         }

         mc.keyboard.setClipboard(new GsonBuilder().disableHtmlEscaping().create().toJson(var1x));
         this.logInfo("已拷贝IP列表");
      }))));
      var1.Q(ExecutableWidget.instance(0, 30, 80, 20).eV(new ButtonElement(TextProvider.c(Text.literal("Remove Server")), ButtonAction.c())));
      ContentDelegateWidget var3 = McWidgetHelpers.c(80, 30, 100, 20, PropertyTracker.event(s -> this.wt = s), this.wt);
      var1.Q(var3);
      var1.Q(
         ExecutableWidget.instance(180, 30, 20, 20).eV(new ButtonElement(TextProvider.c(Text.literal("-").formatted(Formatting.BOLD)), ButtonAction.a(() -> {
            if (!this.wt.isEmpty()) {
               this.removeAll(this.wt);
               this.logInfo("已移除 " + this.wt);
            }
         })).aO(TooltipHandler.ap(List.of(Text.literal("Remove Server")))))
      );
      var1.Q(ExecutableWidget.instance(200, 30, 100, 20).eV(new ButtonElement(TextProvider.c(Text.literal("Refresh Shown")), ButtonAction.a(() -> {
         ArrayList var1x = new ArrayList();
         ObjectIterator var2x = this.wx.object2IntEntrySet().iterator();

         while (var2x.hasNext()) {
            Entry var3x = (Entry)var2x.next();
            if (var3x.getIntValue() > Tasks.b() - 200) {
               var1x.add((String)var3x.getKey());
            }
         }

         this.refreshServerList(var1x, 100);
      }))));
      var1.Q(ExecutableWidget.instance(300, 30, 100, 20).eV(new ButtonElement(TextProvider.c(Text.literal("Back")), ButtonAction.a(() -> {
         if (mc.currentScreen != null) {
            mc.currentScreen.close();
         }
      }))));
      return var1;
   }

   public void logInfo(String message) {
      this.wq = Text.of(message);
   }

   private List<Text> getServerInfoHover(ServerInfo serverInfo) {
      ArrayList var2 = new ArrayList();
      var2.add(Text.literal("服务器协议号:" + serverInfo.protocolVersion));
      var2.add(Text.literal("服务器玩家:"));
      var2.addAll(serverInfo.playerListSummary);
      return var2;
   }

   private Text getStatusDisplay(Status status) {
      return switch (status) {
         case INITIAL, PINGING -> Text.literal("Pinging...").formatted(Formatting.WHITE);
         case UNREACHABLE -> Text.literal("No Connection").formatted(Formatting.RED);
         case SUCCESSFUL -> Text.literal("Available").formatted(Formatting.GREEN);
         case INCOMPATIBLE -> Text.literal("Outdated").formatted(Formatting.YELLOW);
         default -> throw new MatchException(null, null);
      };
   }

   public void openScannerScreen() {
      this.saveServerList();
      this.closeResources();
      if (this.wg == null || this.wv == null) {
         this.wg = ListEntryWidgetController.mutable(this.wp, this::Hs, this::createJoinServerWidget, 40, 310);
         this.wv = new KalamaHelperHelperD(this.wg, 0, 40, 390, 260).dP(false);
      }

      DrawableWidget var1 = this.createInputWidget();
      DrawableWidget var2 = this.createOutputWidget();
      ExtraSubHelperO var3 = new ExtraSubHelperO(this, Text.literal("Server Scanner"), 400, 360, var1, var2);
      this.logInfo("");
      ScreenAccess.of(var3).openFromCurrent();
      this.refreshServerList(this.wp.subList(0, Math.min(this.wp.size(), 10)), 100);
   }

   public void pingServer(MultiplayerServerListPinger pinger, String ip, boolean filter) {
      ServerInfo var4 = new ServerInfo("Kalama scanner", ip, ServerType.OTHER);

      try {
         ServerAddress var5 = ServerAddress.parse(ip);
         Optional var6 = AllowedAddressResolver.DEFAULT.resolve(var5);
         if (var6.isPresent()) {
            try {
               pinger.add(var4, () -> {}, () -> var4.setStatus(Status.SUCCESSFUL));
               this.addScannResult(ip, var4);
            } catch (Exception var8) {
               var4.setStatus(Status.UNREACHABLE);
               if (!filter) {
                  this.addScannResult(ip, var4);
               } else {
                  this.addScannExceptionResult(ip, var4);
               }
            }
         } else {
            var4.setStatus(Status.UNREACHABLE);
            if (!filter) {
               this.addScannResult(ip, var4);
            } else {
               this.addScannExceptionResult(ip, var4);
            }
         }
      } catch (Throwable var9) {
      }
   }

   public void addScannResult(String ip, ServerInfo serverInfo) {
      mc.execute(() -> {
         this.ww.put(ip, serverInfo);
         boolean var3 = false;

         for (int var4 = 0; var4 < this.wp.size(); var4++) {
            if (Objects.equals(ip, this.wp.get(var4))) {
               var3 = true;
               if (this.wg == null) {
                  break;
               }

               this.wg.h(var4);
            }
         }

         if (!var3) {
            this.wp.add(ip);
            if (this.wg != null) {
               this.wg.h(this.wp.size() - 1);
            }

            this.saveServerList();
         }
      });
   }

   public void refreshServerList(List<String> refreshList, int delay) {
      if (this.wr.get()) {
         this.logInfo("当前任务暂未结束");
      } else {
         this.wr.set(true);
         CompletableFuture.runAsync(() -> {
            this.logInfo("");
            MultiplayerServerListPinger var3 = new MultiplayerServerListPinger();

            for (String var6 : List.copyOf(refreshList)) {
               if (!this.wr.get()) {
                  this.logInfo("刷新中断");
                  return;
               }

               this.logInfo("刷新" + var6 + "中");
               this.pingServer(var3, var6, false);

               try {
                  Thread.sleep(delay);
               } catch (Throwable var8) {
               }
            }

            this.logInfo("已完成刷新");
            this.wr.set(false);
         });
      }
   }

   public void Hk() {
      this.logInfo("");
      if (this.wr.get()) {
         this.warn("当前任务暂未结束");
      } else {
         Debug.e("start scan with", this.wh.get(), this.wi.get(), this.wj.get());
         int var1 = this.wi.get();
         int var2 = this.wj.get();
         if (var1 > var2) {
            this.warn("PortA需要比PortB小");
         } else {
            String var3 = this.wh.get();
            int var4 = this.wl.get();
            boolean var5 = this.wm.get();
            boolean var6 = this.wn.get();

            try {
               new InetSocketAddress(var3, var1);
               new InetSocketAddress(var3, var2);
            } catch (Throwable var9) {
               this.warn("输入的IP地址格式有误");
               return;
            }

            this.wr.set(true);
            Random var7 = new Random();
            Set var8 = Set.copyOf(this.wp);
            CompletableFuture.runAsync(() -> {
               MultiplayerServerListPinger var9x = new MultiplayerServerListPinger();

               try (ThreadPoolExecutor var10 = (ThreadPoolExecutor)Executors.newFixedThreadPool(16)) {
                  ArrayList var11 = new ArrayList(var4);
                  int var12 = var1;
                  KeySetView var13 = ConcurrentHashMap.newKeySet();

                  label53:
                  for (int var14 = 0; var14 < var4; var14++) {
                     String var15 = "";
                     int var16 = 0;

                     do {
                        var12 = var5 ? var7.nextInt(var1, var2) : var12 + 1;
                        if (var12 > var2) {
                           break label53;
                        }

                        var15 = var3 + ":" + var12;
                        if (++var16 > 10000) {
                           break label53;
                        }
                     } while (var8.contains(var15) || var13.contains(var15));

                     var13.add(var15);
                     var11.add(CompletableFuture.runAsync(() -> {
                        if (this.wr.get()) {
                           this.logInfo("扫描" + var15);
                           this.pingServer(var9x, var15, var6);
                        }
                     }, var10));
                  }

                  this.logInfo("异步处理请求中...");
                  CompletableFuture.allOf(var11.toArray(new CompletableFuture[var11.size()])).join();
                  this.logInfo("异步扫描结束");
               }

               this.wr.set(false);
            });
         }
      }
   }

   public void removeAll(String ip) {
      mc.execute(() -> {
         if (this.wp.removeIf(s -> s.startsWith(ip))) {
            this.saveServerList();
            if (this.wg != null) {
               this.wg.resync();
            }
         }
      });
   }

   public String Hs() {
      return this.ws;
   }

   public void onButtonAddWhenInitialize(Event<MultiplayerScreen> screenEvent) {
      if (this.ae.get()) {
         MultiplayerScreen var2 = (MultiplayerScreen)screenEvent.e();
         if (this.ko != null && this.ko.get() != null) {
            ScreenAccess.of(var2).removeChildFrom(this.ko.get());
         }

         this.ko = null;
         ContentDelegateWidget var3 = new ContentDelegateWidget(0, 5, 50, 20);
         ExecutableWidget var4 = ExecutableWidget.instance(0, 0, 50, 20)
            .eV(new ButtonElement(TextProvider.c(Text.literal("Scanner")), ButtonAction.a(this::openScannerScreen)));
         var3.setContentDelegate(var4);
         var3.addTo(var2);
         this.ko = new WeakReference<>(var3);
      }
   }

   @Override
   public void unregisterAll() {
      super.unregisterAll();
      this.wr.set(false);
   }

   public void Hl() {
      this.logInfo("");
      if (!this.wr.get()) {
         this.logInfo("当前无运行中任务");
      } else {
         this.logInfo("任务终止中...");
         this.wr.set(false);
      }
   }

   public void Hn() {
      if (this.wg != null) {
         this.wg.j();
      }

      this.ww.clear();
      this.wp.clear();
      this.saveServerList();
   }

   private Text getServerMotd(ServerInfo serverInfo) {
      return (Text)(serverInfo.getStatus() != Status.UNREACHABLE && serverInfo.label != null ? serverInfo.label : Text.empty());
   }

   public ServerScanner() {
      super("ServerScanner");
      this.ae = this.builder(this.wf.addEnable(), FlagRef.TYPE).defaultValue(true).build();
      this.wh = new StringRef("");
      this.wi = new IntRef(0);
      this.wj = new IntRef(0);
      this.wk = new IntRef(2000);
      this.wl = new IntRef(1000);
      this.wm = new FlagRef(true);
      this.wn = new FlagRef(true);
      this.wo = FileManager.getInstance().o("server-scanner.nbt");
      this.wp = new ArrayList<>();
      this.list().stream().map(s -> ((NbtString)s).asString()).forEach(this.wp::add);
      this.wq = Text.empty();
      this.wr = new AtomicBoolean(false);
      this.ws = "";
      this.wt = "";
      this.wu = new ConcurrentHashMap<>();
      this.ww = new ConcurrentHashMap<>();
      this.wx = new Object2IntOpenHashMap();
      this.bindFlag(this.ae);
   }

   public void warn(String message) {
      this.wq = Text.literal(message).formatted(Formatting.YELLOW);
   }

   public void saveServerList() {
      Map var1 = Map.of("save-list", this.wp);
      this.wo.write(var1, JavaOps.INSTANCE);
      this.wo.l(true);
   }

   private void connect(ServerInfo serverInfo) {
      Screen var2 = mc.currentScreen;
      if (var2 != null) {
         ConnectScreen.connect(var2, mc, ServerAddress.parse(serverInfo.address), serverInfo, false, null);
      }
   }

   private Text getPlayerListDisplay(ServerInfo serverInfo) {
      if (serverInfo.getStatus() == Status.UNREACHABLE) {
         return Text.empty();
      } else {
         return serverInfo.players == null
            ? Text.literal("加载中...")
            : Text.literal(serverInfo.players.online() + "/" + serverInfo.players.max()).formatted(Formatting.GRAY);
      }
   }

   public void Ho() {
      this.refreshServerList(this.wp, this.wk.get());
   }

   private ElementHandler HF(ServerInfo serverInfo) {
      AtomicInteger var2 = new AtomicInteger();
      return new IconElement$SimpleIconElement(null, null, true, (el1, el2, el3) -> {
         int var6 = var2.get();
         if (Tasks.b() < var6 + 10) {
            this.connect(serverInfo);
            return true;
         } else {
            var2.set(Tasks.b());
            return true;
         }
      }).cs((el, h) -> ((DrawableWidget)el).isFocused() ? -1 : null).setShowTooltips(false);
   }

   public void addScannExceptionResult(String ip, ServerInfo info) {
      info.setStatus(Status.UNREACHABLE);
      mc.execute(() -> {
         this.ww.put(ip, info);
         if (this.wg != null) {
            for (int var3 = 0; var3 < this.wp.size(); var3++) {
               if (Objects.equals(ip, this.wp.get(var3))) {
                  if (this.wg == null) {
                     break;
                  }

                  this.wg.h(var3);
               }
            }
         }
      });
   }

   public DrawableWidget createJoinServerWidget(String ip) {
      KalamaHelperHelperCX var2 = new KalamaHelperHelperCX(0, 0, 310, 40);
      AtomicInteger var3 = new AtomicInteger(0);
      var2.Q(
         DisplayWidget.instance(0, 0, 310, 40)
            .setRenderHandler(new AbstractElement().setShowTooltips(false).cD((element, context, mouseX, mouseY, delta, alpha, shouldHighlight) -> {
               if (var3.get() == 0) {
                  var3.set(Tasks.b());
               } else if (Tasks.b() > var3.get() + 40) {
                  if (this.wx.getInt(ip) == 0) {
                     if (!this.ww.containsKey(ip)) {
                        this.Hq(ip);
                     }
                  } else if (this.wx.getInt(ip) + 200 < Tasks.b()) {
                     this.Hq(ip);
                  }

                  this.wx.put(ip, Tasks.b());
               }

               if (element.isMouseOver(mouseX, mouseY)) {
                  RenderHandler.K(context, 0, 0, element.getTextureWidth(), element.getTextureHeight(), -1);
               }
            }))
      );
      var2.Q(
         ExecutableWidget.instance(45, 0, 100, 9)
            .eV(RawTextElement.g(Text.literal(ip)).setAlignment(-1).cF(new ButtonElement(TextProvider.c(Text.empty()), ButtonAction.a(() -> {
               mc.keyboard.setClipboard(ip);
               this.logInfo("成功拷贝ip");
            }))).aO(TooltipHandler.ap(List.of(Text.literal("Click to copy ip")))))
      );
      ServerInfo var4 = this.ww.get(ip);
      if (var4 != null) {
         var2.Q(ExecutableWidget.instance(2, 2, 36, 36).eV(this.HE(var4)));
         var2.Q(ExecutableWidget.instance(1, 1, 310, 38).eV(this.HF(var4)));
         var2.Q(DisplayWidget.instance(260, 0, 50, 40).setRenderHandler(TooltipHandler.ar(() -> this.getServerInfoHover(var4))));
         var2.Q(DisplayWidget.instance(260, 8, 50, 9).setRenderHandler(RawTextElement.h(b -> this.getStatusDisplay(var4.getStatus())).setAlignment(1)));
         var2.Q(DisplayWidget.instance(260, 16, 50, 9).setRenderHandler(RawTextElement.h(b -> this.getPlayerListDisplay(var4)).setAlignment(1)));
         var2.Q(DisplayWidget.instance(260, 24, 50, 9).setRenderHandler(RawTextElement.h(b -> this.getServerBrandInfoDisplay(var4)).setAlignment(1)));
         var2.Q(DisplayWidget.instance(50, 10, 270, 30).setRenderHandler(new MultiLineTextElement(s -> this.getServerMotd(var4), -1, -1)));
      } else {
         var2.Q(DisplayWidget.instance(2, 2, 36, 36).setRenderHandler(LabelElement.instance(Text.empty())));
      }

      return var2;
   }
}
