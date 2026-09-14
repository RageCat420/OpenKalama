package me.matl114.hacks.modules.render;

import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.elements.LabelElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

class RenderSubHelperBX extends Screen implements RenderSubHelperWX {
   Text i;

   public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
   }

   protected RenderSubHelperBX(final SleepMode param1, Text title, Text displayMessage) {
      super(title);
      this.j = var1;
      this.i = displayMessage;
   }

   protected void init() {
      super.init();
      this.j.Ri = this;
      DisplayWidget.instance(40, 40, this.width - 80, this.height - 80).<DrawableWidget>setRenderHandler(LabelElement.instance(this.i)).addTo(this);
      this.j.Rk = true;
   }
   SleepMode j;
}
