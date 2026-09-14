package me.matl114.gui.complex.config;

import me.matl114.gui.GenericBackGroundScreen;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.DrawableWidget;
import net.minecraft.text.Text;

public class DefaultedKeyValueInputWidget extends GenericBackGroundScreen {
   DrawableWidget reference;

   @Override
   protected void init() {
      super.init();
      new ContentDelegateWidget<DrawableWidget>(this.x, this.y, 0, 0).setContentDelegate(this.reference).addTo(this);
   }

   public DefaultedKeyValueInputWidget(Text title, int backgroundWidth, int backgroundDefaultHeight, DrawableWidget widget) {
      super(title, backgroundWidth, backgroundDefaultHeight);
      this.reference = widget;
   }
}
