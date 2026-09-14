package me.matl114.gui.complex.config;

import java.util.List;
import me.matl114.events.Listener;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.presets.index.IndexedScreen;
import me.matl114.managers.config.Config;
import me.matl114.managers.config.StringRef;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ConfigurateNewStyleScreen extends IndexedScreen<Config, ConfigureListWidget> {
   private static final int aw = 220;
   private static final int ax = 20;
   private static final int au = 100;
   private static Config selectingConfig;
   private static final int av = 140;
   private StringRef filterWidget;

   public ConfigurateNewStyleScreen(List<Config> list) {
      super(list, 400, 320);
   }

   @Override
   public void bc() {
      if (this.bS != null) {
         ConfigureListWidget var1 = this.bS.gl();
         if (var1 != null) {
            var1.saveSelected();
         }
      }
   }

   protected ConfigureListWidget ba(Config val) {
      if (this.filterWidget == null) {
         this.filterWidget = new StringRef("");
      }

      return ConfigureListWidget.createConfigConfigure(val, 20, 0, 100, 140, 0, 220, 20, this.width - 100 - 30, this.height - 20, this.filterWidget);
   }

   static {
      Listener.bv().k(iHotKeyEvent -> {
         if (MinecraftClient.getInstance().currentScreen instanceof ConfigurateNewStyleScreen) {
            iHotKeyEvent.cancel();
         }
      });
   }

   public void setGlobal(Config config) {
      if (config != selectingConfig) {
         selectingConfig = config;
         this.bb();
      }
   }

   @Override
   protected void bb() {
      super.bb();
      this.filterWidget = new StringRef("");
   }

   protected ElementHandler createIndexHandler(Config val) {
      return new ButtonElement(TextProvider.c(Text.literal(val.getConfigName())), ButtonAction.a(() -> this.setGlobal(val)))
         .cA(ButtonElement.bH)
         .cC(ButtonElement.bI)
         .cw(el -> this.getGlobal() == val);
   }

   public Config getGlobal() {
      return selectingConfig;
   }



   @Override
   public ElementHandler be(Object arg0) { return null; }

}
