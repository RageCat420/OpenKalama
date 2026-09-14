package me.matl114.hacks.modules.extra;

import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.IntPrimitiveList;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.hacks.utils.render.NotifyType;
import me.matl114.hooks.BaritoneHooks;
import me.matl114.hooks.impl.baritone.BaritoneFuture;
import me.matl114.hooks.impl.baritone.BaritoneLanding;
import me.matl114.managers.Configs;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.WindowUtils;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;

public class EventNotify extends BaseModule {
   public final ModulePath iE = makePath(Configs.j, "other.queue-notify");
   public final FlagRef enableDisconnectServer;
   public final FlagRef minOnly;
   public final FlagRef enableBaritoneEnd;
   int lastOrder;
   public final EnumRef<NotifyType> mode;
   public final FlagRef enablePopTotem;
   public final FlagRef ae = this.flagBuilder(this.iE.addEnable()).build();
   public final NBTRef<IntPrimitiveList> order;
   public final DoubleRef minWidth;
   public final FlagRef enableQueue;
   public final NBTRef<Regex> queue3cTitleRegex;
   public final KeyBindRef J = this.moduleEntry(this.iE.addHotkey(), new MultiKeyBind(), this.iE.addEnable()).build();

   public void adM(Event<BaritoneFuture> event) {
      if (this.ae.get() && this.enableBaritoneEnd.get()) {
         if (this.checkMin()) {
            return;
         }

         this.notify("[Kalama]Baritone提醒", "Baritone落地: " + event.<BaritoneLanding>getArgs(0).name());
      }
   }

   public void notify(String title, String message) {
      switch ((NotifyType)(Object)this.mode.get()) {
         case TRAY:
            WindowUtils.c(title, message);
            break;
         case PS_WINDOW:
            WindowUtils.b(title, message);
      }
   }

   public void QJ(Event<Void> eventReconfiguration) {
      if (this.ae.get() && this.enableQueue.get() && !eventReconfiguration.<Boolean>getArgs(0) && this.lastOrder != 0) {
         this.lastOrder = 0;
         if (this.checkMin()) {
            return;
         }

         this.notify("[Kalama]排队提醒", "你已经完成排队进入服务器!");
      }
   }

   @Override
   public void addCustomWidgets(Consumer<DrawableWidget> acceptor, int dx, int dy, int dblank) {
      super.addCustomWidgets(acceptor, dx, dy, dblank);
      acceptor.accept(this.createExecuteButton("widget.event-notify.test-usage", ButtonAction.a(this::registerAll), 0, dblank, dx, dy));
   }

   public void onTitle(Event<SubtitleS2CPacket> eventTitle) {
      if (this.ae.get() && this.enableQueue.get()) {
         String var2 = ChatUtils.l(((SubtitleS2CPacket)eventTitle.b).text());
         if (this.queue3cTitleRegex.get().test(var2)) {
            Matcher var3 = Pattern.compile("\\d+").matcher(var2);

            while (var3.find()) {
               if (var3.start() != 0) {
                  try {
                     int var4 = Integer.parseInt(var3.group());
                     if (this.lastOrder != var4) {
                        this.lastOrder = var4;
                        if (this.order.get().list().contains(var4)) {
                           if (this.checkMin()) {
                              return;
                           }

                           this.notify("[Kalama]排队提醒", "你已经抵达队列位置: " + var4);
                           return;
                        }
                     }
                  } catch (NumberFormatException var5) {
                  }
               }
            }
         }
      }
   }

   public boolean checkMin() {
      return this.minOnly.get() && mc.getWindow().getWidth() > this.minWidth.get();
   }

   public void onTotemPop(Event<EntityStatusS2CPacket> event) {
      if (!checkNull()) {
         if (this.ae.get()
            && this.enablePopTotem.get()
            && ((EntityStatusS2CPacket)event.b).getStatus() == 35
            && ((EntityStatusS2CPacket)event.b).getEntity(mc.world) == mc.player) {
            if (this.checkMin()) {
               return;
            }

            this.notify("[Kalama]图腾提醒", "你触发了不死图腾");
         }
      }
   }

   public EventNotify() {
      super("EventNotify");
      this.mode = this.builder(this.iE.add("mode"), NotifyType.class).defaultValue(NotifyType.TRAY).build();
      this.minOnly = this.flagBuilder(this.iE.add("min-only")).build();
      this.minWidth = this.doubleBuilder(this.iE.add("min-width")).defaultValue(0.0).build();
      this.enableQueue = this.flagBuilder(this.iE.add("enable-queue")).build();
      this.queue3cTitleRegex = this.builder(this.iE.add("queue-3c-title-regex"), Regex.class)
         .defaultValue(new Regex(".*(正在游玩.*队列位置|Position.*queue)[：:]\\s*(\\d+)"))
         .build();
      this.order = this.builder(this.iE.add("order"), IntPrimitiveList.class).defaultValue(new IntPrimitiveList(List.of(5, 10))).build();
      this.enableDisconnectServer = this.flagBuilder(this.iE.add("enable-disconnect-server")).build();
      this.enablePopTotem = this.flagBuilder(this.iE.add("enable-pop-totem")).build();
      this.enableBaritoneEnd = this.flagBuilder(this.iE.add("enable-baritone-end")).build();
      this.lastOrder = 0;
      this.bindFlag(this.ae);
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.ar().getChannel(SubtitleS2CPacket.class), this::onTitle);
      this.registerListener(Listener.ar().getChannel(EntityStatusS2CPacket.class), this::onTotemPop);
      this.registerListener(Listener.O(), this::QJ);
      this.registerListener(Listener.P(), this::adL);
      this.registerListener(BaritoneHooks.getLandingEvent(), this::adM);
   }

   public void adL(Event<Void> eventLeave) {
      if (this.ae.get() && this.enableDisconnectServer.get()) {
         if (this.checkMin()) {
            return;
         }

         this.notify("[Kalama]离线提醒", "你离开了服务器");
      }
   }
}
