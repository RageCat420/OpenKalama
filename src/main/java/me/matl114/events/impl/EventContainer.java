package me.matl114.events.impl;

import java.util.Locale;
import java.util.function.Supplier;
import me.matl114.managers.config.ConfigEnum;
import me.matl114.managers.config.EnumRef;
import net.minecraft.text.Text;

public class EventContainer implements Supplier<Text> {
   String a;

   EventContainer(Supplier var1) {
      this.b = var1;
   }

   public Text get() {
      if (this.a == null) {
         ConfigEnum var1 = (ConfigEnum)((EnumRef)(Object)this.b.get()).get();
         this.a = "module-meta." + var1.getConfigEnumType().replace("_", "-") + ".";
      }

      return Text.translatable(this.a + ((ConfigEnum)((EnumRef)(Object)this.b.get()).get()).cast().name().toLowerCase(Locale.ROOT));
   }
   Supplier b;
}
