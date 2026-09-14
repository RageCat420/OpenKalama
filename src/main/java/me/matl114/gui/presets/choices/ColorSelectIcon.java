package me.matl114.gui.presets.choices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import me.matl114.accessors.gui.ScreenAccess;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.RenderHandler;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.complex.BoxElement;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.ColorUtils;
import me.matl114.utils.ScreenUtils;
import me.matl114.utils.config.ValueAccessor;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public class ColorSelectIcon extends BoxElement {
   final ValueAccessor<TextColor> color;
   public static final TextColor[] FORMAT_COLORS;

   public static void swapToNearestColorSwitch(ValueAccessor<TextColor> colorAcc, boolean forward) {
      TextColor var2 = (TextColor)colorAcc.getValue();
      int var3 = Arrays.binarySearch(FORMAT_COLORS, var2, Comparator.comparingInt(TextColor::getRgb));
      int var4;
      if (forward) {
         if (var3 >= 0) {
            var4 = var3 + 1;
         } else {
            var4 = -var3 - 1;
         }

         if (var4 >= FORMAT_COLORS.length) {
            var4 = 0;
         }
      } else {
         if (var3 >= 0) {
            var4 = var3 - 1;
         } else {
            int var5 = -var3 - 1;
            var4 = var5 - 1;
         }

         if (var4 < 0) {
            var4 = FORMAT_COLORS.length - 1;
         }
      }

      TextColor var6 = FORMAT_COLORS[var4];
      colorAcc.setValue(var6);
   }

   public ColorSelectIcon(ValueAccessor<TextColor> color) {
      super(ButtonAction.b(s -> {
         if (ScreenUtils.hasShiftDown()) {
            openColorSelectScreen(color);
         } else {
            swapToNearestColorSwitch(color, s);
         }
      }));
      this.color = color;
      this.cE(TooltipHandler.ar(() -> {
         ArrayList var1 = new ArrayList();
         var1.add(Text.translatable("widget.gui.color-select-icon.current-color", new Object[]{((TextColor)color.getValue()).getName()}));
         var1.addAll(ChatUtils.parseTranslation("widget.gui.color-select-icon.swap-color.tooltips", ""));
         return var1;
      }));
   }

   public static void openColorSelectScreen(ValueAccessor<TextColor> colorAcc) {
      ScreenAccess.of(new ColorSelectScreen(colorAcc)).openFromCurrent();
   }

   public void renderCentered0(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
      RenderHandler.K(context, 0, 0, element.getTextureWidth(), element.getTextureHeight(), -1);
      TextColor var8 = this.color.getValue();
      context.fill(1, 1, element.getTextureWidth() - 1, element.getTextureHeight() - 1, ColorUtils.j(var8.getRgb(), 255));
   }

   public ColorSelectIcon(ValueAccessor<TextColor> color, ButtonAction buttonAction) {
      super(buttonAction);
      this.color = color;
      this.cE(
         TooltipHandler.ar(
            () -> List.of(Text.translatable("widget.gui.color-select-icon.current-color", new Object[]{((TextColor)color.getValue()).getName()}))
         )
      );
   }

   static {
      ArrayList var0 = new ArrayList();

      for (Formatting var4 : Formatting.values()) {
         if (var4.isColor()) {
            var0.add(TextColor.fromFormatting(var4));
         }
      }

      var0.sort(Comparator.comparingInt(TextColor::getRgb));
      FORMAT_COLORS = var0.toArray(TextColor[]::new);
   }
}
