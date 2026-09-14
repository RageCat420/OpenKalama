package me.matl114.hacks.modules.task;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.gui.KalamaHelperHelperB;
import me.matl114.gui.WidgetUtils;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.complex.config.KalamaHelperHelperD;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.elements.IconElement;
import me.matl114.gui.elements.LabelElement;
import me.matl114.gui.elements.MultiLineTextElement;
import me.matl114.gui.presets.lists.ListEntryWidgetController;
import me.matl114.gui.presets.single.CenterScreen;
import me.matl114.gui.presets.single.ConfirmingWidgetScreen;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.FileManager;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.file.FileStorage;
import me.matl114.utils.Debug;
import me.matl114.utils.collections.MutableRecord;
import net.minecraft.text.Text;
import org.apache.commons.lang3.mutable.MutableObject;

public class Proxy extends BaseModule {
   public FileStorage aA;
   private final ModulePath qB = makePath(Configs.r, "proxy-server");
   TaskSubHelperQ qC;
   public final FlagRef ae = this.flagBuilder(this.qB.add("enable")).build();

   public DrawableWidget createEditRenderHandler(MutableRecord argsMap, MutableObject<MutableRecord> index) {
      KalamaHelperHelperCX var3 = new KalamaHelperHelperCX(0, 0, 220, 20);
      ExecutableWidget.instance(2, 2, 16, 16).<ExecutableWidget>eV(IconElement.co(ButtonElement.bH, ButtonElement.bJ, ButtonAction.a(() -> {
         if (argsMap != index.getValue()) {
            index.setValue(argsMap);
         } else {
            index.setValue(null);
         }
      }), bl -> argsMap == index.getValue())).addToSub(var3);
      DisplayWidget.instance(45, 0, 100, 20)
         .<DrawableWidget>setRenderHandler(new LabelElement(ClickGui.INSTANCE.guiBackgroundStyle.get().withAlpha(64)))
         .addToSub(var3);
      ExecutableWidget.instance(45, 0, 100, 20).<ExecutableWidget>eV(new MultiLineTextElement(el -> {
         TaskSubHelperK var2 = argsMap.j(TaskSubHelperK.class);
         return Text.literal("%s\n(%s:%s:%d)".formatted(var2.name(), var2.type().name(), var2.address(), var2.port()));
      }, ClickGui.INSTANCE.guiConfigStyle.get().withAlpha(255), 0)).addToSub(var3);
      ExecutableWidget.instance(155, 0, 60, 20)
         .<ExecutableWidget>eV(
            new ButtonElement(
               TextProvider.c(Text.translatable("widget.connection-proxy.open-editor")),
               ButtonAction.a(
                  () -> {
                     DrawableWidget var1 = WidgetUtils.createMutableRecordEditScreen(
                        Text.translatable("widget.connection-proxy.open-editor.title"),
                        List::of,
                        argsMap,
                        s -> "widget.connection-proxy." + s,
                        WidgetUtils.a,
                        ClickGui.ky
                     );
                     new CenterScreen(var1).access().openFromCurrent();
                  }
               )
            )
         )
         .addToSub(var3);
      return var3;
   }

   public void zs(TaskSubHelperQ proxyList) {
      this.qC = proxyList;
      this.aA.f(TaskSubHelperQ.CODEC, proxyList);
   }

   public Proxy() {
      super("Proxy");
      this.aA = FileManager.getInstance().o("proxies.nbt");
      this.qC = this.aA.read(TaskSubHelperQ.CODEC, () -> new TaskSubHelperQ(-1, List.of()));
      this.bindFlag(this.ae);
   }

   @Override
   public void addCustomWidgets(Consumer<DrawableWidget> acceptor, int dx, int dy, int dblank) {
      acceptor.accept(
         ExecutableWidget.instance(0, dblank, dx, dy)
            .eV(
               new ButtonElement(
                     el -> {
                        TaskSubHelperK var2 = this.qC.ahk();
                        return var2 != null
                           ? Text.translatable("widget.connection-proxy.proxy-list-editor")
                              .append(Text.literal(var2.name() + "(%s:%s:%d)".formatted(var2.type().name(), var2.address(), var2.port())))
                           : Text.translatable("widget.connection-proxy.proxy-list-editor").append("None");
                     },
                     ButtonAction.a(this::openProxyListEditScreen)
                  )
                  .aO(TooltipHandler.ap(KalamaHelperHelperB.b()))
            )
      );
   }

   public void openProxyListEditScreen() {
      List<MutableRecord> var1 = new ArrayList<>(this.qC.Rk().stream().map(s -> MutableRecord.of(TaskSubHelperK.ds, s)).toList());
      int var2 = this.qC.Rj();
      MutableObject var3 = new MutableObject(var2 >= 0 && var2 < var1.size() ? (MutableRecord)var1.get(var2) : null);
      ListEntryWidgetController var4 = ListEntryWidgetController.mutable(
         var1, () -> MutableRecord.of(TaskSubHelperK.ds, TaskSubHelperK.EMPTY), v -> this.createEditRenderHandler(v, var3), 30, 220
      );
      KalamaHelperHelperD var5 = new KalamaHelperHelperD(var4, 0, 0, 320, 260);
      ConfirmingWidgetScreen var6 = new ConfirmingWidgetScreen(Text.translatable("widget.connection-proxy.proxy-list-editor.title"), var5, () -> true, () -> {
         List<TaskSubHelperK> var3x = var1.stream().map(s -> s.j(TaskSubHelperK.class)).toList();
         this.zs(new TaskSubHelperQ(var3.getValue() == null ? -1 : var1.indexOf(var3.getValue()), var3x));
      });
      var6.access().openFromCurrent();
   }

   public void zv(Event<ChannelPipeline> chEvent) {
      ChannelPipeline var2 = (ChannelPipeline)chEvent.e();
      if (this.isActive() && !chEvent.<Boolean>getArgs(1)) {
         TaskSubHelperK var3 = this.qC.ahk();
         if (var3 != null) {
            int var4 = var3.port();
            if (var4 > 0) {
               String var5 = var3.userName();
               boolean var6 = var5.isEmpty();
               String var7 = var3.password();

               InetSocketAddress var8;
               try {
                  var8 = new InetSocketAddress(var3.address(), var4);
               } catch (Throwable var10) {
                  Debug.e("Invalid address :", var3.address(), var4, var10);
                  Debug.f(var10);
                  return;
               }

               switch (var3.type()) {
                  case SOCKS:
                     if (var7.isEmpty()) {
                        var2.addFirst("socks4ClientProxy", new Socks4ProxyHandler(var8, var6 ? null : var5));
                     } else {
                        var2.addFirst("socks5ClientProxy", new Socks5ProxyHandler(var8, var6 ? null : var5, var7));
                     }
                     break;
                  case HTTP:
                     var2.addFirst("httpClientProxy", new HttpProxyHandler(var8, var6 ? null : var5, var7.isEmpty() ? null : var7));
                  case HTTPS:
               }
            }
         }
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.by(), this::zv);
   }
}
