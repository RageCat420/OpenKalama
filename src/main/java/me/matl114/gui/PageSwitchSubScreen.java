package me.matl114.gui;

import java.util.function.IntConsumer;
import me.matl114.gui.basic.AbstractElement;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.KalamaHelperHelperP;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.LabelElement;
import me.matl114.gui.elements.PageButtonElement;
import me.matl114.gui.presets.single.IntFastInputWidget;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.BaseAttrKeyValue;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class PageSwitchSubScreen extends KalamaHelperHelperCX {
   private ContentDelegateWidget<IntFastInputWidget> ba;
   protected int aW = 1;
   private int aZ;
   protected int aX = 1;
   protected IntConsumer aY;

   public final int cn() {
      return this.aW;
   }

   protected void updateMaxPage(int page) {
      this.aX = MathHelper.clamp(page, 1, this.aW);
      this.aY.accept(this.aX);
   }

   public int cq() {
      return this.aX;
   }

   public final void co(int val) {
      this.aW = val;
      this.aX = MathHelper.clamp(this.aX, 1, this.aW);
   }

   protected void bE() {
      ExecutableWidget.instance(5 + this.dy + 1, 0, this.dx - 5 - 5 - 2 - 2 * this.dy, this.dy)
         .<ExecutableWidget>eV(
            new AbstractElement().cD(new LabelElement(i -> Text.literal(this.aX + "/" + this.aW), -1, 0)).cF(KalamaHelperHelperP.ax((w, a) -> {
               this.updateMaxPage(this.cq() + (a > 0.0 ? -1 : 1));
               return true;
            })).cF(KalamaHelperHelperP.az(this::cs)).aO(
               TooltipHandler.ap(ChatUtils.parseTranslation("widget.gui.page-switch-sub-screen.page-label.tooltips", ""))
            )
         )
         .addToSub(this);
      ExecutableWidget.instance(5, 0, this.dy, this.dy).<ExecutableWidget>eV(PageButtonElement.aa(this::cn, this::cq, this::updateMaxPage)).addToSub(this);
      ExecutableWidget.instance(this.dx - 5 - this.dy, 0, this.dy, this.dy)
         .<ExecutableWidget>eV(PageButtonElement.ab(this::cn, this::cq, this::updateMaxPage))
         .addToSub(this);
      this.ba = new ContentDelegateWidget(0, 0, this.dx, this.dy).addToSub(this, 500);
   }

   public PageSwitchSubScreen(int x, int y, int dx, int dy, int pageHeight, IntConsumer pageSwitchCallback) {
      super(x, y, dx, dy);
      this.aY = pageSwitchCallback;
      this.aZ = pageHeight;
      this.bE();
   }

   protected void cs() {
      BaseAttrKeyValue var1 = AttrKeyValue.clampedInt("widget.gui.page-switch-sub-screen.input-page", this.aX, 1, this.aW);
      this.ba
         .setContentDelegate(
            IntFastInputWidget.instance(var1, this::hoverInputCallback, (this.dx - 96) / 2, (this.aZ - 30) / 2, 96, 30, 64)
               .setFinishRunning(() -> this.ba.setContentDelegate(null))
         );
   }

   protected void hoverInputCallback(AttrKeyValue<Integer> val) {
      this.ba.setContentDelegate(null);
      this.updateMaxPage((Integer)val.getOriginValue());
   }

   public final void cp(int page) {
      this.aX = MathHelper.clamp(page, 1, this.aW);
   }
}
