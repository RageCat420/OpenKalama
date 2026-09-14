package me.matl114.gui.presets.single;

import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.RenderHandler;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class KalamaHelperHelperF<T> implements RenderHandler {
   RegistryDisplays$IIcon<T> J;
   Text H;
   T K;
   Identifier I;

   public KalamaHelperHelperF(Text name, Identifier identifier, RegistryDisplays$IIcon<T> icon, T value) {
      this.H = name;
      this.I = identifier;
      this.J = icon;
      this.K = (T)value;
   }

   @Override
   public void renderAtCentered(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
      int var8 = (element.getTextureHeight() - 16) / 2;
      this.J.a(var8, var8, context, this.K);
      RenderHandler.G(context, mc.textRenderer, this.H, 20, 1, 200, 10, -16711936, -1);
      RenderHandler.G(context, mc.textRenderer, Text.literal(this.I.toString()), 20, 10, 200, 19, -16711936, -1);
   }
}
