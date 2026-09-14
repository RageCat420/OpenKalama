package me.matl114.gui.presets.single;

import me.matl114.gui.GenericScreen;
import me.matl114.gui.WidgetUtils;
import me.matl114.gui.basic.DrawableWidget;
import net.minecraft.text.Text;

public class CenterScreen extends GenericScreen {
   DrawableWidget widget;

   @Override
   protected void init() {
      super.init();
      DrawableWidget var1 = WidgetUtils.createCenterScreenWidget(this.widget, this.width, this.height);
      var1.addTo(this);
   }

   public CenterScreen(DrawableWidget widget) {
      super(Text.empty(), 0, 0);
      this.widget = widget;
   }

   @Override
   protected void init0() {
      super.init0();
      this.x = 0;
      this.y = 0;
   }
}
