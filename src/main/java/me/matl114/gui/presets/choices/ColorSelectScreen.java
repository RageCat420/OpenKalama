package me.matl114.gui.presets.choices;

import java.awt.Color;
import me.matl114.gui.WidgetUtils;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.complex.RawTextElement;
import me.matl114.utils.ColorUtils;
import me.matl114.utils.config.ValueAccessor;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public class ColorSelectScreen extends ConfirmingBigScreen {
   int cs;
   ValueAccessor<TextColor> source;
   int ct;
   int cr;

   protected ColorSelectScreen(ValueAccessor<TextColor> color) {
      super(Text.translatable("widget.gui.color-select-screen.title").formatted(Formatting.GREEN));
      this.source = color;
      TextColor var2 = (TextColor)color.getValue();
      Color var3 = new Color(var2.getRgb(), true);
      this.cr = var3.getRed();
      this.cs = var3.getGreen();
      this.ct = var3.getBlue();
   }

   protected void onConfirmButton() {
      int var1 = ColorUtils.c(this.cr, this.cs, this.ct);

      for (Formatting var5 : Formatting.values()) {
         if (var5.isColor() && var5.getColorValue() == var1) {
            TextColor var6 = TextColor.fromFormatting(var5);
            this.source.setValue(var6);
            this.close();
            return;
         }
      }

      this.source.setValue(TextColor.fromRgb(var1));
      this.close();
   }

   @Override
   protected void init() {
      super.init();
      KalamaHelperHelperCX var1 = new KalamaHelperHelperCX(this.x, this.y, this.backgroundWidth, this.backgroundHeight);
      new ContentDelegateWidget<DrawableWidget>(30, 30, 0, 0)
         .setContentDelegate(
            ExecutableWidget.instance(0, 0, 255, 255)
               .<ExecutableWidget>eT(WidgetUtils.j(v -> this.cr = v, v -> this.cs = v))
               .setRenderHandler(
                  (element, context, mouseX, mouseY, delta, alpha, shouldHighlight) -> {
                     context.fillGuiGradient(
                        0,
                        0,
                        element.getTextureWidth(),
                        element.getTextureHeight(),
                        ColorUtils.c(0, 0, this.ct),
                        ColorUtils.c(0, 255, this.ct),
                        ColorUtils.c(255, 255, this.ct),
                        ColorUtils.c(255, 0, this.ct),
                        0
                     );
                     context.fill(this.cr - 1, this.cs - 1, this.cr + 1, this.cs + 1, -1);
                  }
               )
         )
         .addToSub(var1);
      new ContentDelegateWidget<DrawableWidget>(30, 300, 0, 0)
         .setContentDelegate(
            ExecutableWidget.instance(0, 0, 255, 20)
               .<ExecutableWidget>eT(WidgetUtils.j(vl -> this.ct = vl, vl -> {}))
               .setRenderHandler(
                  (element, context, mouseX, mouseY, delta, alpha, shouldHighlight) -> {
                     context.fillGuiGradient(
                        0,
                        0,
                        element.getTextureWidth(),
                        element.getTextureHeight(),
                        ColorUtils.c(0, 0, 0),
                        ColorUtils.c(0, 0, 0),
                        ColorUtils.c(0, 0, 255),
                        ColorUtils.c(0, 0, 255),
                        0
                     );
                     context.fill(this.ct - 1, 0, this.ct + 1, element.getTextureHeight(), -1);
                  }
               )
         )
         .addToSub(var1);
      ExecutableWidget.instance(350, 60, 40, 40)
         .<ExecutableWidget>eV(new ColorSelectIcon(ValueAccessor.of(() -> TextColor.fromRgb(ColorUtils.c(this.cr, this.cs, this.ct))), ButtonAction.c()))
         .addToSub(var1);
      ExecutableWidget.instance(310, 120, 120, 30)
         .<ExecutableWidget>eV(
            new RawTextElement(el -> Text.literal("R: %d, G: %d, B: %d".formatted(this.cr, this.cs, this.ct)), ColorUtils.getColorInt(0, 0, 0, 255), 0)
         )
         .addToSub(var1);
      var1.addTo(this);
   }

   @Override
   protected boolean canConfirm(ElementHandler elementHandler) {
      return ColorUtils.getColorInt(this.cr, this.cs, this.ct, 0) != this.source.getValue().getRgb();
   }

   @Override
   public void c() { }

}
