package me.matl114.gui.presets.single;

import java.util.function.BooleanSupplier;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.DynamicSubScreenWidget;
import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.presets.choices.ConfirmingBigScreen;
import me.matl114.utils.config.ValueAccessor;
import net.minecraft.text.Text;

public class ConfirmingWidgetScreen extends ConfirmingBigScreen {
   BooleanSupplier bf;
   DrawableWidget be;
   Runnable bg;

   @Override
   protected void init() {
      super.init();
      DynamicSubScreenWidget var1 = new DynamicSubScreenWidget(ValueAccessor.ofIgnore(this::cA), ValueAccessor.ofIgnore(this::cB));
      var1.Q(this.be);
      var1.addTo(this);
   }

   @Override
   protected boolean canConfirm(ElementHandler elementHandler) {
      return this.bf != null && this.bf.getAsBoolean();
   }

   protected int cB() {
      return CONTENT_START_Y + (this.content_end_y - CONTENT_START_Y - this.be.getHeight()) / 2 - this.be.getY();
   }

   @Override
   protected void c() {
      if (this.bg != null) {
         this.bg.run();
      }

      this.close();
   }

   public ConfirmingWidgetScreen(Text title, DrawableWidget widget, BooleanSupplier confirm, Runnable callback) {
      super(title);
      this.be = widget;
      this.bf = confirm;
      this.bg = callback;
   }

   protected int cA() {
      return (this.width - this.be.getWidth()) / 2 - this.be.getX();
   }
}
