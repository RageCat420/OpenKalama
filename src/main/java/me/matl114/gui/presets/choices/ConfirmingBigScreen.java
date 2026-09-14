package me.matl114.gui.presets.choices;

import me.matl114.gui.GenericBackGroundScreen;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.elements.ButtonElement;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ConfirmingBigScreen extends GenericBackGroundScreen {
   protected int content_end_y;
   private static final Text cX = Text.translatable("widget.gui.confirming-big-screen.cancel").formatted(Formatting.RED);
   protected ExecutableWidget cU;
   protected ExecutableWidget cT;
   protected static int CONTENT_START_Y = 40;
   private static final Text aK = Text.translatable("widget.gui.confirming-big-screen.confirm").formatted(Formatting.GREEN);

   protected abstract void c();

   @Override
   protected void init() {
      super.init();
      this.content_end_y = this.backgroundHeight - 30;
      this.cT = ExecutableWidget.instance(this.x + 150, this.y + this.content_end_y + 5, 80, 20)
         .<ExecutableWidget>eV(new ButtonElement(TextProvider.c(cX), ButtonAction.a(this::dh)))
         .addTo(this);
      this.cU = ExecutableWidget.instance(this.x + 250, this.y + this.content_end_y + 5, 80, 20)
         .<ExecutableWidget>eV(new ButtonElement(TextProvider.c(aK), ButtonAction.a(this::c)).ah(this::canConfirm))
         .addTo(this);
   }

   protected void dh() {
      this.close();
   }

   protected ConfirmingBigScreen(Text title) {
      super(title, 480, 360);
   }

   protected abstract boolean canConfirm(ElementHandler var1);
}
