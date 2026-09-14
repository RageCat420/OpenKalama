package me.matl114.gui.presets.lists;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.presets.choices.ConfirmingBigScreen;
import me.matl114.managers.config.NBTType;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.KeyValue;
import me.matl114.utils.config.WidgetFactory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class NBTListModifyScreen<T> extends ConfirmingBigScreen {
   final BiFunction<String, T, AttrKeyValue<T>> d;
   int f;
   Consumer<List<T>> c;
   int g;
   final Predicate<List<T>> a;
   ListEntryWidgetController h;
   List<AttrKeyValue<T>> b;

   @Override
   protected void c() {
      List var1 = this.list();
      if (this.a.test(var1)) {
         this.c.accept(var1);
         this.close();
      }
   }

   private List<T> list() {
      return this.b.stream().map(KeyValue::getOriginValue).collect(Collectors.toList());
   }

   public NBTListModifyScreen(AttrKeyValue<List<T>> attrKeyValue, NBTType<T> type, Supplier<T> newElement, Consumer<List<T>> callback, int dx, int dy) {
      this(
         (List<T>)attrKeyValue.getOriginValue(), attrKeyValue::isValueValid, type::createAttrKeyValue, type::generateValueWidget, newElement, callback, dx, dy
      );
   }

   @Override
   protected boolean canConfirm(ElementHandler elementHandler) {
      ArrayList var2 = new ArrayList();

      for (AttrKeyValue var4 : this.b) {
         if (!var4.isValidate()) {
            return false;
         }

         var2.add(var4.getOriginValue());
      }

      return this.a.test(var2);
   }

   @Override
   protected void init() {
      super.init();
      int var1 = this.f + 80;
      new me.matl114.gui.complex.config.KalamaHelperHelperD(
            this.h, this.x + (this.backgroundWidth - var1) / 2, this.y + CONTENT_START_Y, var1, this.content_end_y - CONTENT_START_Y
         )
         .addTo(this);
   }

   public NBTListModifyScreen(
      List<T> list,
      Predicate<List<T>> listValidator,
      BiFunction<String, T, AttrKeyValue<T>> attrElementFactory,
      WidgetFactory<AttrKeyValue<T>> customWidgetFactory,
      Supplier<T> newElement,
      Consumer<List<T>> callback,
      int dx,
      int dy
   ) {
      super(Text.translatable("widget.gui.nbt-list-modify-screen.title").formatted(Formatting.GREEN));
      this.a = listValidator;
      this.d = attrElementFactory;
      this.b = list.stream().map(s -> this.d.apply("", (T)s)).collect(Collectors.toCollection(ArrayList::new));
      this.c = callback;
      this.f = dx;
      this.g = dy;
      this.h = ListEntryWidgetController.mutable(
         this.b, () -> this.d.apply("", (T)newElement.get()), w -> customWidgetFactory.generateWidget(w, 0, 0, this.f, this.g), this.g, this.f
      );
   }
}
