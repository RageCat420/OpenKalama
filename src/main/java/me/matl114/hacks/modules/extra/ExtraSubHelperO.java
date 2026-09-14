package me.matl114.hacks.modules.extra;

import me.matl114.gui.GenericScreen;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import net.minecraft.text.Text;

class ExtraSubHelperO extends GenericScreen {
   private final ServerScanner dV;
   private final DrawableWidget dU;
   private final DrawableWidget dW;
   private final Text dT;

   public void close() {
      super.close();
      this.dV.saveServerList();
      this.dV.closeResources();
   }

   ExtraSubHelperO(
      final ServerScanner this$0, Text title, int backgroundWidth, int backgroundDefaultHeight, final DrawableWidget param5, final DrawableWidget nullx
   ) {
      super(title, backgroundWidth, backgroundDefaultHeight);
      this.dV = this$0;
      this.dT = title;
      this.dW = param5;
      this.dU = nullx;
   }

   @Override
   protected void init() {
      super.init();
      KalamaHelperHelperCX var1 = new KalamaHelperHelperCX(this.x, this.y, 400, 360);
      var1.Q(me.matl114.gui.basic.DisplayWidget.instance(0, 0, 400, 20).setRenderHandler(me.matl114.gui.elements.LabelElement.instance(this.dT)));
      var1.Q(this.dW);
      var1.Q(this.dV.wv);
      var1.Q(this.dU);
      this.addDrawableChild(var1);
   }
}
