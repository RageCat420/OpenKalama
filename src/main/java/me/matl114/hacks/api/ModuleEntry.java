package me.matl114.hacks.api;

import me.matl114.managers.config.Config;
import me.matl114.managers.config.FlagRef;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ModuleEntry {
   String toggleName;
   Config config;
   FlagRef toggleRef;
   String[] path;
   String[] hotkeyPath;

   public MutableText getDisplay() {
      return Text.translatableWithFallback(this.toggleName, this.toggleName);
   }

   public String[] getPath() {
      return this.path;
   }

   public String getToggleKey() {
      return this.toggleName;
   }

   public ModuleEntry(Config config, String[] path, String[] hotkeyPath) {
      this.config = config;
      this.path = path;
      this.hotkeyPath = hotkeyPath;
      this.toggleName = "module-toggle." + String.join(".", this.path);
   }

   public MutableText getMetaData() {
      return null;
   }

   public String[] getHotkeyPath() {
      return this.hotkeyPath;
   }

   public FlagRef getFlagRef() {
      if (this.toggleRef == null) {
         this.toggleRef = this.config.getBoolean(this.path);
      }

      return this.toggleRef;
   }

   public boolean getActiveState() {
      FlagRef var1 = this.getFlagRef();
      return var1 != null && var1.get();
   }
}
