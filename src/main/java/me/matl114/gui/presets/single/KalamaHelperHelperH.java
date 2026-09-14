package me.matl114.gui.presets.single;

import java.util.function.Consumer;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.complex.RawTextElement;
import me.matl114.utils.config.AttrKeyValue;
import net.minecraft.text.Text;

public class KalamaHelperHelperH extends IntFastInputWidget {
   int dB;

   public KalamaHelperHelperH(AttrKeyValue<Integer> keyValue, Consumer<AttrKeyValue<Integer>> callback, int maxValue, int x, int y, int dx, int dy, int dx0) {
      super(keyValue, callback, x, y, dx, dy, dx0);
      this.dB = maxValue;
   }

   @Override
   protected void af() {
      this.aC = this.aB / 2 + 12;
      super.af();
   }

   @Override
   protected void initBackgroundAndText() {
      super.initBackgroundAndText();
      DisplayWidget.instance(this.aC - 2, 12, this.aB - this.aC, this.dy - 16)
         .<DrawableWidget>setRenderHandler(RawTextElement.h(i -> Text.literal("/" + this.dB)).setAlignment(-1))
         .addToSub(this);
   }
}
