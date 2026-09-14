package me.matl114.hacks.modules.extra;

import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;

public class AutoReconnect extends BaseModule {
   public final IntRef delay;
   int counter;
   public final FlagRef enable;
   public final ModulePath JI = makePath(Configs.j, "other.auto-reconnect");
   public final FlagRef enableButtons;
   ServerInfo JK;

   public AutoReconnect() {
      super("AutoReconnect");
      this.enable = this.flagBuilder(this.JI.add("enable")).build();
      this.enableButtons = this.flagBuilder(this.JI.add("enable-buttons")).build();
      this.delay = this.intBuilder(this.JI.add("delay")).defaultValue(5).validator(Configs.e).build();
      this.counter = 0;
      this.bindFlag(this.enable);
   }

   public void YF(Event<DisconnectedScreen> event) {
      if (this.enable.get()) {
         DisconnectedScreen var2 = (DisconnectedScreen)event.b;
         this.counter = this.delay.get() * 20;
         Tasks.m(() -> {
            if (this.counter > 0) {
               this.counter--;
               return false;
            } else if (this.enable.get() && mc.currentScreen instanceof DisconnectedScreen) {
               this.reconect(var2.parent);
               return true;
            } else {
               return mc.currentScreen != null;
            }
         }, 1, 1);
      }
   }

   public void reconect(Screen screen) {
      if (this.JK != null) {
         ConnectScreen.connect(screen, mc, ServerAddress.parse(this.JK.address), this.JK, false, null);
      }
   }

   public void onScreenInitialize(Event<DisconnectedScreen> event) {
      if (this.enableButtons.get()) {
         DisconnectedScreen var2 = (DisconnectedScreen)event.b;
         KalamaHelperHelperCX var3 = new KalamaHelperHelperCX(0, 0, 200, 50);
         var3.Q(
            ExecutableWidget.instance(0, 25, 200, 20)
               .eV(
                  new ButtonElement(
                     el -> this.enable.get()
                        ? Text.literal("Toggle Auto reconnect off (%d sec)".formatted(Math.max(this.counter, 0) / 20))
                        : Text.literal("Toggle Auto reconnect on"),
                     ButtonAction.a(this.enable::toggle)
                  )
               )
         );
         var3.Q(ExecutableWidget.instance(0, 0, 200, 20).eV(new ButtonElement(TextProvider.c(Text.literal("Reconnect")), ButtonAction.a(() -> {
            if (this.enable.get() && this.counter > 0) {
               this.counter = 0;
            } else {
               Tasks.l(() -> this.reconect(var2.parent), 0);
            }
         }))));
         var2.grid.add(var3);
         var3.addTo(var2);
         var2.grid.refreshPositions();
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.ai().c(DisconnectedScreen.class), this::onScreenInitialize);
      this.registerListener(Listener.af().c(DisconnectedScreen.class), this::YF);
      this.registerListener(Listener.Q(), this::YE);
   }

   public void YE(Event<ServerAddress> event) {
      this.JK = event.getArgs(0);
      this.counter = -999;
   }
}
