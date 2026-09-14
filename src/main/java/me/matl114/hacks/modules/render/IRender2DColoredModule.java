package me.matl114.hacks.modules.render;

import me.matl114.hacks.utils.config.WrapColor;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public abstract class IRender2DColoredModule extends IRender2DModule {
   public FlagRef bold2;
   public NBTRef<WrapColor> ct = this.builder(this.he.add("color"), WrapColor.class)
      .defaultValue(new WrapColor((TextColor)TextColor.parse("#F05BDA").getOrThrow()))
      .build();

   public void drawText(VDrawContext vdraw, Text text) {
      if (this.bold2.get()) {
         text = text.copy().formatted(Formatting.BOLD);
      }

      this.gh(vdraw, text.asOrderedText());
   }

   public void gj(VDrawContext vdraw, String text) {
      MutableText var3 = Text.literal(text);
      if (this.bold2.get()) {
         var3 = var3.formatted(Formatting.BOLD);
      }

      this.gh(vdraw, var3.asOrderedText());
   }

   public IRender2DColoredModule(String name) {
      super(name);
      this.bold2 = this.flagBuilder(this.he.add("bold")).defaultValue(true).build();
   }

   public IRender2DColoredModule() {
      super("IRender2DColoredModule");
      this.bold2 = this.flagBuilder(this.he.add("bold")).defaultValue(true).build();
   }

   public void gh(VDrawContext vdraw, OrderedText text) {
      int var3 = this.ct.get().withAlpha(255);
      if (this.right2.get()) {
         int var4 = mc.textRenderer.getWidth(text);
         vdraw.z(mc.textRenderer, text, -var4, 0, var3, true);
      } else {
         vdraw.z(mc.textRenderer, text, 0, 0, var3, true);
      }

      vdraw.f().translate(0.0F, 9.0F);
   }
}
