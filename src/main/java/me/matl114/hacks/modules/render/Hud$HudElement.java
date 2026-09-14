package me.matl114.hacks.modules.render;

import me.matl114.utils.commands.params.impl.StringArgumentResult;
import net.minecraft.text.Text;

public enum Hud$HudElement implements StringArgumentResult {
   UX,
   UV,
   UY,
   UP,
   UT,
   UU,
   US,
   UQ,
   UW,
   UR;
   // $VF: synthetic field
   private static final Hud$HudElement[] $VALUES = amm();

   @Override
   public Text resultAsString() {
      return Text.literal(this.name());
   }
}
