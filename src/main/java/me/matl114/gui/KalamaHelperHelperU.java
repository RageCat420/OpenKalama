package me.matl114.gui;

import java.util.Objects;
import me.matl114.accessors.gui.TextFieldAccess;
import me.matl114.utils.config.AttrKeyValue;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class KalamaHelperHelperU<T> extends TextFieldWidget {
    String dD;
    AttrKeyValue<T> dC;

    public String getText() {
        this.checkAttrKeyValueUpdate();
        return super.getText();
    }

    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        this.checkAttrKeyValueUpdate();
        super.renderWidget(context, mouseX, mouseY, delta);
    }

    public void syncChanges(String valueUpdate) {
        if (Objects.equals(this.dD, this.dC.getValue())) {
            this.dC.valueChange(this, valueUpdate);
            String var2 = this.dC.getValue();
            this.dD = var2;
        } else {
            this.dD = this.dC.getValue();
            this.setText(this.dD);
        }
    }

    public KalamaHelperHelperU(
            AttrKeyValue<T> attrKeyValue, TextRenderer textRenderer, int x, int y, int width, int height) {
        super(textRenderer, x, y, width, height, Text.empty());
        this.setMaxLength(32768);
        this.setText(attrKeyValue.getValue());
        this.dC = attrKeyValue;
        this.setChangedListener(this::syncChanges);
        TextFieldAccess.of(this)
                .setBorderColorProvider(McWidgetHelpers.getWrongRedTextBoxColorProvider(this.dC::isValidate));
    }

    private void checkAttrKeyValueUpdate() {
        if (!Objects.equals(this.dD, this.dC.getValue())) {
            this.dD = this.dC.getValue();
            this.setText(this.dD);
        }
    }
}
