package me.matl114.gui.complex.config;

import java.util.List;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.ScrollableListWidget;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.IconElement;
import me.matl114.gui.presets.lists.ListEntryWidgetController;
import me.matl114.utils.ChatUtils;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class KalamaHelperHelperD extends ScrollableListWidget {
   private static final Identifier db = new Identifier("kalama", "gui/move_down");
   private static final Identifier dc = new Identifier("kalama", "gui/remove");
   boolean cZ = true;
   ListEntryWidgetController t;
   private static final Identifier da = new Identifier("kalama", "gui/move_up");
   private static final Identifier dd = new Identifier("kalama", "gui/add");

   public KalamaHelperHelperD dP(boolean bl) {
      this.cZ = bl;
      return this;
   }

   private static List<Text> dQ() {
      return ChatUtils.parseTranslation("widget.gui.list-modify-widget.insert.tooltips", "");
   }

   @Override
   public void render0(VDrawContext context, int mouseX, int mouseY, float delta, boolean disableSelect) {
      if (this.t.i()) {
         this.bR();
         this.t.markDirty(false);
      }

      super.render0(context, mouseX, mouseY, delta, disableSelect);
   }

   public KalamaHelperHelperD(ListEntryWidgetController controller, int x, int y, int dx, int dy) {
      super(x, y, dx, dy);
      this.t = controller;
      this.t.markDirty(true);
   }

   protected <T extends Element & Drawable & Selectable> KalamaHelperHelperCX dR(T widget, int listIndex, int startX, int startY) {
      int var5 = this.t.b();
      int var6 = this.t.c();
      Object var8 = widget instanceof DrawableWidget var7 ? var7 : new ContentDelegateWidget(0, 0, var6, var5).setContentDelegate(widget);
      int var11 = startY + var5 * listIndex;
      int var9 = Math.min(20, var5);
      byte var10 = 0;
      return new KalamaHelperHelperCX(startX, var11, var6, var5)
         .Q((DrawableWidget)var8)
         .Q(
            ExecutableWidget.instance(var6 + 1, 1 + var10, var9 - 2, var9 - 2)
               .eV(
                  IconElement.cm(da, ButtonAction.a(() -> this.t.d(listIndex)))
                     .setActive(listIndex != 0)
                     .aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.gui.list-modify-widget.shift-up.tooltips", "")))
               )
         )
         .Q(
            ExecutableWidget.instance(var6 + var9 + 1, 1 + var10, var9 - 2, var9 - 2)
               .eV(
                  IconElement.cm(db, ButtonAction.a(() -> this.t.e(listIndex)))
                     .setActive(listIndex != this.t.a() - 1)
                     .aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.gui.list-modify-widget.shift-down.tooltips", "")))
               )
         )
         .Q(
            ExecutableWidget.instance(var6 + 2 * var9 + 1, 1 + var10, var9 - 2, var9 - 2)
               .eV(
                  IconElement.cm(dc, ButtonAction.a(() -> this.t.f(listIndex)))
                     .aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.gui.list-modify-widget.delete.tooltips", "")))
               )
         )
         .Q(
            ExecutableWidget.instance(var6 + 3 * var9 + 1, 1 + var10, var9 - 2, var9 - 2)
               .eV(IconElement.cm(dd, ButtonAction.a(() -> this.t.g(listIndex))).aO(TooltipHandler.ap(dQ())))
         );
   }

   protected ExecutableWidget dS(int startX, int startY) {
      int var3 = this.t.b();
      int var4 = this.t.c();
      int var5 = Math.min(20, var3);
      return ExecutableWidget.instance(startX + var4 / 2 - var5 / 2 + 2 * var5, startY + var3 * this.t.a(), var5, var5)
         .eV(IconElement.cm(dd, ButtonAction.a(() -> this.t.g(-1))).aO(TooltipHandler.ap(dQ())));
   }

   protected void bR() {
      this.aU();
      int var1 = this.t.a();

      for (int var2 = 0; var2 < var1; var2++) {
         KalamaHelperHelperCX var3 = this.dR(this.t.getEntryWidget(var2), var2, 0, 0);
         this.addScrollingWidget(var3);
      }

      if (this.cZ) {
         this.addScrollingWidget(this.dS(0, 0));
      }
   }
}
