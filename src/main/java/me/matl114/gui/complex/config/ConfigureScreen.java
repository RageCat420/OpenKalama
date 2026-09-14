package me.matl114.gui.complex.config;

import me.matl114.gui.GenericScreen;
import me.matl114.managers.config.Config;
import me.matl114.managers.config.StringRef;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ConfigureScreen extends GenericScreen {
   private final StringRef ay = new StringRef("");
   private Config dx;
   ConfigureListWidget dy;
   private static final int aw = 160;
   private static final int ax = 20;

   public void resize(MinecraftClient client, int width, int height) {
      this.eR();
      super.resize(client, width, height);
   }

   public void close() {
      super.close();
      this.eR();
   }

   @Override
   protected void init() {
      super.init();
      if (this.dy != null) {
         this.dy.saveSelected();
         Config.launchSaveTasks();
      }

      this.dy = ConfigureListWidget.createConfigConfigure(this.dx, 0, 10, 160, 160, 20, 160, 20, this.width - 20, this.height - 40, this.ay);
      this.addDrawableChild(this.dy);
   }

   public void eR() {
      if (this.dy != null) {
         this.dy.saveSelected();
      }

      Config.launchSaveTasks();
   }

   @Deprecated
   public ConfigureScreen(Config config, Text title) {
      super(title, 400, 320);
      this.loadConfig(config);
   }

   public void loadConfig(Config config) {
      this.dx = config;
   }
}
