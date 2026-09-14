package me.matl114.gui.presets.single;

import java.util.function.Consumer;
import me.matl114.gui.GenericScreen;
import me.matl114.gui.WidgetUtils;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperP;
import me.matl114.gui.complex.RawTextElement;
import me.matl114.utils.collections.KalamaHelperHelperM;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class WidgetPosSelectScreen extends GenericScreen {
   KalamaHelperHelperM by;
   Consumer<KalamaHelperHelperM> bx;

   public WidgetPosSelectScreen(int backgroundWidth, KalamaHelperHelperM initializePoint, Consumer<KalamaHelperHelperM> pointAcceptor) {
      super(Text.translatable("widget.gui.widget-select-screen.title").formatted(Formatting.GREEN), backgroundWidth, 60);
      this.bx = pointAcceptor;
      this.by = initializePoint;
   }

   @Override
   protected void init() {
      super.init();
      DisplayWidget.instance(this.x, this.y, this.backgroundWidth, this.backgroundHeight)
         .<DrawableWidget>setRenderHandler(new RawTextElement(this::getTitleLabel, -1, 0))
         .addTo(this);
      KalamaHelperHelperP var1 = WidgetUtils.j(this.by::c, this.by::d);
      ExecutableWidget.instance(0, 0, this.width, this.height).<ExecutableWidget>eT(KalamaHelperHelperP.ay((element, mouseX, mouseY, button, type) -> {
         if (button == 0) {
            return var1.b(element, mouseX, mouseY, button, type);
         } else {
            this.bx.accept(this.by);
            this.close();
            return true;
         }
      })).<DrawableWidget>setRenderHandler((element, context, mouseX, mouseY, delta, alpha, shouldHighlight) -> {
         context.fill((int)(this.by.a - 5.0), (int)(this.by.b - 5.0), (int)(this.by.a + 5.0), (int)(this.by.b + 5.0), -65536);
         context.fill((int)(this.by.a - 5.0), (int)(this.by.b - 1.0), (int)(this.by.a + 5.0), (int)(this.by.b + 1.0), -16711936);
         context.fill((int)(this.by.a - 1.0), (int)(this.by.b - 5.0), (int)(this.by.a + 1.0), (int)(this.by.b + 5.0), -16711936);
      }).addTo(this);
   }
}
