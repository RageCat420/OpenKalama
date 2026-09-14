package me.matl114.gui.presets.choices;

import com.google.common.base.Predicates;
import java.util.List;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.presets.lists.ListRegistrySelectWidget;
import me.matl114.utils.config.ValueAccessor;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class RegistryChooseResultScreen<T> extends ConfirmingBigScreen {
    ListRegistrySelectWidget<T> ar;
    Registry<T> aq;
    ContentDelegateWidget<ListRegistrySelectWidget<T>> as;
    protected static final int WIDTH = 240;

    public RegistryChooseResultScreen(Registry<T> registry, List<T> selects, String showString) {
        super(Text.empty());
        this.aq = registry;
        this.setTitleLabel(Text.translatable("widget.gui.registry-choose-result-screen.title")
                .formatted(Formatting.AQUA));
        this.ar = (ListRegistrySelectWidget<T>) ListRegistrySelectWidget.bK(
                        selects, this.aq, ValueAccessor.holder(showString), 0, CONTENT_START_Y + 20, 240, 240, 20)
                .bP(Predicates.alwaysTrue());
    }

    @Override
    protected void init() {
        super.init();
        this.as = new ContentDelegateWidget<ListRegistrySelectWidget<T>>(
                        this.x + this.backgroundWidth / 2 - 120, this.y, 240, 240)
                .setContentDelegate(this.ar)
                .addTo(this);
    }

    @Override
    protected boolean canConfirm(ElementHandler elementHandler) {
        return true;
    }

    @Override
    protected void c() {
        this.close();
    }
}
