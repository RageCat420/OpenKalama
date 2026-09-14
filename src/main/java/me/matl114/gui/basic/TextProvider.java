package me.matl114.gui.basic;

import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public interface TextProvider {
    Text a(DrawableWidget var1);

    default OrderedText getLabel(DrawableWidget element) {
        Text var2 = this.a(element);
        return var2 == null ? null : var2.asOrderedText();
    }

    static TextProvider c(Text text) {
        return b -> text;
    }
}
