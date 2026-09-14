package me.matl114.gui.presets.lists;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;

public interface ListEntryWidgetController {
   boolean d(int var1);

   static <W, T extends Element & Drawable & Selectable> ListEntryWidgetController immutable(
      List<W> originData, Function<W, T> widgetFactory, int height, int width
   ) {
      return new KalamaHelperHelperJ(originData, widgetFactory, height, width);
   }

   boolean f(int var1);

   int b();

   boolean j();

   void markDirty(boolean var1);

   <T extends Element & Drawable & Selectable> T getEntryWidget(int var1);

   static <W, T extends Element & Drawable & Selectable> ListEntryWidgetController mutable(
      List<W> originData, Supplier<W> newData, Function<W, T> widgetFactory, int height, int width
   ) {
      return new KalamaHelperHelperG(originData, height, width, newData, widgetFactory);
   }

   int a();

   boolean e(int var1);

   int c();

   void resync();

   boolean i();

   boolean h(int var1);

   boolean g(int var1);
}
