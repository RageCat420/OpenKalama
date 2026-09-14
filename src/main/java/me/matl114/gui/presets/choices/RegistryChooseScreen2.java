package me.matl114.gui.presets.choices;

import java.util.Set;
import java.util.function.Consumer;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.presets.lists.ListRegistryMultiSelectWidget;
import me.matl114.utils.config.ValueAccessor;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class RegistryChooseScreen2<T> extends ConfirmingBigScreen {
   ContentDelegateWidget<ListRegistryMultiSelectWidget<T>> as;
   Registry<T> aq;
   ListRegistryMultiSelectWidget<T> cm;
   Consumer<Set<T>> c;
   protected static final int WIDTH = 240;

   public RegistryChooseScreen2(Registry<T> registry, Set<T> currentSelection, Consumer<Set<T>> callback) {
      super(Text.empty());
      this.aq = registry;
      this.c = callback;
      this.setTitleLabel(Text.translatable("widget.gui.registry-select-screen.title").formatted(Formatting.AQUA));
      this.cm = ListRegistryMultiSelectWidget.registry(this.aq, currentSelection, ValueAccessor.holder(""), 0, CONTENT_START_Y + 20, 240, 240, 20);
   }

   @Override
   protected void init() {
      super.init();
      this.as = new ContentDelegateWidget<ListRegistryMultiSelectWidget<T>>(this.x + this.backgroundWidth / 2 - 120, this.y, 240, 240)
         .setContentDelegate(this.cm)
         .addTo(this);
   }

   @Override
   protected boolean canConfirm(ElementHandler elementHandler) {
      return true;
   }

   @Override
   protected void c() {
      Set var1 = this.cm.getSelectedRegistries();
      if (var1 != null && this.c != null) {
         this.c.accept(var1);
      }

      this.close();
   }
}
