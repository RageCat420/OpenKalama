package me.matl114.gui.presets.choices;

import java.util.function.Consumer;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.presets.lists.ListRegistrySelectWidget;
import me.matl114.utils.config.ValueAccessor;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class RegistryChooseScreen<T> extends ConfirmingBigScreen {
   protected ContentDelegateWidget<ListRegistrySelectWidget<T>> as;
   Registry<T> aq;
   protected ListRegistrySelectWidget<T> ar;
   Consumer<T> c;
   protected static final int WIDTH = 240;

   @Override
   protected void c() {
      Object var1 = this.ar.getSelectedRegistry();
      if (this.c != null) {
         this.c.accept((T)var1);
      }

      this.close();
   }

   @Override
   protected void init() {
      super.init();
      this.as = new ContentDelegateWidget<ListRegistrySelectWidget<T>>(this.x + this.backgroundWidth / 2 - 120, this.y, 240, 240)
         .setContentDelegate(this.ar)
         .addTo(this);
   }

   public RegistryChooseScreen(Registry<T> registry, Consumer<T> callback, String filterInput) {
      super(Text.empty());
      this.aq = registry;
      this.c = callback;
      this.setTitleLabel(Text.translatable("widget.gui.registry-choose-screen.title").formatted(Formatting.AQUA));
      this.ar = ListRegistrySelectWidget.registry(this.aq, ValueAccessor.holder(filterInput), 0, CONTENT_START_Y + 20, 240, 240, 20);
   }

   public RegistryChooseScreen(Registry<T> registry, Consumer<T> callback) {
      this(registry, callback, "");
   }

   @Override
   protected boolean canConfirm(ElementHandler elementHandler) {
      return this.ar.getSelectedRegistry() != null;
   }
}
