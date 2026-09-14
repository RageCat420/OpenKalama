package me.matl114.hacks.api;

import java.util.Arrays;
import java.util.Objects;
import me.matl114.managers.config.Config;
import net.minecraft.text.Text;

public class ModulePath {
   String[] splits;
   Config config;

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else {
         return !(obj instanceof ModulePath var2) ? false : var2.config == this.config && Arrays.equals((Object[])this.splits, (Object[])var2.splits);
      }
   }

   public Text toTranslationKey() {
      return Text.translatable(this.asString());
   }

   public Config getConfig() {
      return this.config;
   }

   public ModulePath(Config config, String[] splits) {
      this.config = config;
      this.splits = splits;
   }

   public String[] toPath() {
      return this.splits;
   }

   public String asString() {
      return String.join(".", this.splits);
   }

   public ModulePath addHotkey() {
      return this.add("hotkey");
   }

   public ModulePath add(String path) {
      String[] var2 = new String[this.splits.length + 1];
      System.arraycopy(this.splits, 0, var2, 0, this.splits.length);
      var2[this.splits.length] = path;
      return new ModulePath(this.config, var2);
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.config, Arrays.hashCode((Object[])this.splits));
   }

   public ModulePath addEnable() {
      return this.add("enable");
   }
}
