package me.matl114.gui;

import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.elements.ButtonElement;
import net.minecraft.text.Text;

public final class KalamaHelperHelperS extends KalamaHelperHelperD<KalamaHelperHelperS> {
   public TextProvider textProvider = TextProvider.c(Text.empty());

   @Override
   public ElementHandler d(WidgetSupplier factory) {
      return factory.e(this);
   }

   public KalamaHelperHelperS M(TextProvider textProvider) {
      this.textProvider = textProvider;
      return this;
   }

   public KalamaHelperHelperS() {
      this.l = true;
      this.j = ButtonElement.bJ;
      this.k = ButtonElement.bH;
   }

   public static KalamaHelperHelperS builder() {
      return new KalamaHelperHelperS();
   }

   public KalamaHelperHelperS L(Text text) {
      this.textProvider = text == null ? null : TextProvider.c(text);
      return this;
   }
}
