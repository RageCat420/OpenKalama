package me.matl114.gui.presets.lists;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import me.matl114.gui.McWidgetHelpers;
import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.presets.choices.ConfirmingBigScreen;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.KeyValue;
import me.matl114.utils.config.kv.ListAttrKeyValue;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class StringListModifyScreen<T> extends ConfirmingBigScreen {
   private static final int WIDTH = 240;
   ListAttrKeyValue<T> r;
   ListEntryWidgetController t;
   List<AttrKeyValue<T>> b;
   Consumer<ListAttrKeyValue<T>> s;

   public void tick() {
      super.tick();
      this.r.valueChangeInternal(this, this.b.stream().map(KeyValue::getOriginValue).toList());
   }

   @Override
   protected void init() {
      super.init();
      short var1 = 320;
      new me.matl114.gui.complex.config.KalamaHelperHelperD(
            this.t, this.x + (this.backgroundWidth - var1) / 2, this.y + CONTENT_START_Y, var1, this.content_end_y - CONTENT_START_Y
         )
         .addTo(this);
   }

   @Override
   protected void c() {
      this.r.valueChangeInternal(this, this.b.stream().map(KeyValue::getOriginValue).toList());
      if (this.r.isValidate()) {
         this.s.accept(this.r);
      }

      this.close();
   }

   public StringListModifyScreen(ListAttrKeyValue<T> list, Consumer<ListAttrKeyValue<T>> consumer) {
      super(Text.empty());
      this.setTitleLabel(Text.translatable("widget.gui.string-list-modify-screen.title").formatted(Formatting.GREEN));
      this.r = list;
      this.b = new ArrayList<>(this.r.createAttrKeyValueForElements());
      this.s = consumer;
      this.t = ListEntryWidgetController.mutable(
         this.b,
         this.r::createNewAttrKeyValueElement,
         stringAttrKeyValue -> McWidgetHelpers.d(
            0, 1, 240, 18, stringAttrKeyValue, stringAttrKeyValue.getValue(), McWidgetHelpers.getWrongRedTextBoxColorProvider(stringAttrKeyValue::isValidate)
         ),
         20,
         240
      );
   }

   @Override
   protected boolean canConfirm(ElementHandler elementHandler) {
      return this.r.isValidate();
   }
}
