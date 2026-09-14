package me.matl114.hacks.api;

import java.util.function.Supplier;
import me.matl114.managers.config.Config;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class KalamaHelperHelperI extends ModuleEntry {
   Supplier<Text> this$0;

   public MutableText createKeyLabel() {
      return (MutableText)(Object)this.this$0.get();
   }

   public KalamaHelperHelperI(Config config, String[] path, String[] hotkeyPath, Supplier<Text> provider) {
      super(config, path, hotkeyPath);
      this.this$0 = provider;
   }
}
