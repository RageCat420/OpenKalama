package me.matl114.gui.complex.slimefun;

import me.matl114.gui.PageSwitchSubScreen;
import me.matl114.gui.basic.ContentDelegateWidget;
import net.minecraft.text.Text;

public abstract class SlimefunPageScreen extends SlimefunScreen {
   protected ContentDelegateWidget<PageSwitchSubScreen> aO;
   protected final PageSwitchSubScreen aN = new PageSwitchSubScreen(0, 20, this.backgroundWidth, 12, this.getPageContentHeight(), i -> this.bC());
   protected static final int PAGE_LABEL_HEIGHT = 12;

   protected void bE() {
      this.aO = new ContentDelegateWidget<PageSwitchSubScreen>(this.x, this.y, 0, 0).setContentDelegate(this.aN).addTo(this);
   }

   protected abstract int getPageContentHeight();

   protected abstract void bC();

   public SlimefunPageScreen(Text title) {
      super(title);
   }
}
