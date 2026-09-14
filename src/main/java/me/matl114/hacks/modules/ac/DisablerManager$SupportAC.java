package me.matl114.hacks.modules.ac;

import me.matl114.managers.config.ConfigEnum;
import net.minecraft.text.Text;

public enum DisablerManager$SupportAC implements ConfigEnum {
   NONE,
   GRIM,
   MATRIX;

   @Override
   public String getConfigEnumType() {
      return "support_disabler_ac";
   }

   @Override
   public Text resultAsString() {
      return Text.literal(this.name());
   }
}
