package me.matl114.gui.presets.single;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.KalamaHelperHelperP;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.managers.input.KeyCode;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.config.AttrKeyValue;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class KeyBindConfigurateWidget extends KalamaHelperHelperCX {
    ExecutableWidget dL;
    AttrKeyValue<MultiKeyBind> dJ;
    ExecutableWidget dM;
    MultiKeyBind dK;

    private void onAnyKeyPressed(int keyCode) {
        String var2 = KeyCode.getNameForKey(keyCode);
        List var3 = this.getKeys();
        if (var3.isEmpty() || !Objects.equals(var3.get(var3.size() - 1), var2)) {
            var3.add(var2);
            this.ackChange(
                    var3, this.dJ.getOriginValue().j(), this.dJ.getOriginValue().k());
        }
    }

    private void init() {
        this.dL = ExecutableWidget.instance(0, 1, this.dx - 3 * this.dy - 2, this.dy - 2)
                .<ExecutableWidget>eV(new ButtonElement(this::createKeyDisplay, (element, widget, mouseButton) -> {
                            if (this.isFocused() && widget == this.B) {
                                this.onAnyKeyPressed(KeyCode.getKeyCodeFromMouseAction(mouseButton));
                            }

                            return true;
                        })
                        .cs((widget, isFocused) -> {
                            if (this.dJ.isValidate()) {
                                return isFocused ? -1 : null;
                            } else {
                                return -65536;
                            }
                        })
                        .cF(KalamaHelperHelperP.av((widget, keyCode, scanCode, modifiers, isPress) -> {
                            if (this.isFocused() && widget == this.B && isPress) {
                                this.onAnyKeyPressed(keyCode);
                                return true;
                            } else {
                                return false;
                            }
                        }))
                        .aO(TooltipHandler.ap(ChatUtils.parseTranslation(
                                "widget.gui.key-bind-configurate-widget.keycode-configure.tooltips", ""))))
                .addToSub(this);
        ExecutableWidget.instance(this.dx - 3 * this.dy - 1, 1, this.dy - 2, this.dy - 2)
                .<ExecutableWidget>eV(
                        new ButtonElement(TextProvider.c(Text.literal("T")), (element, widget, mouseButton) -> {
                                    this.fg();
                                    return false;
                                })
                                .cw(v -> this.dJ.getOriginValue().j())
                                .aO(TooltipHandler.ap(ChatUtils.parseTranslation(
                                        "widget.gui.key-bind-configurate-widget.keycode-t.tooltips", ""))))
                .addToSub(this);
        ExecutableWidget.instance(this.dx - 2 * this.dy - 1, 1, this.dy - 2, this.dy - 2)
                .<ExecutableWidget>eV(
                        new ButtonElement(TextProvider.c(Text.literal("V")), (element, widget, mouseButton) -> {
                                    this.fh();
                                    return false;
                                })
                                .cw(v -> this.dJ.getOriginValue().k())
                                .aO(TooltipHandler.ap(ChatUtils.parseTranslation(
                                        "widget.gui.key-bind-configurate-widget.keycode-v.tooltips", ""))))
                .addToSub(this);
        this.dM = ExecutableWidget.instance(this.dx - this.dy - 1, 1, this.dy - 2, this.dy - 2)
                .<ExecutableWidget>eV(
                        new ButtonElement(TextProvider.c(Text.literal("D")), (element, widget, mouseButton) -> {
                                    this.fi();
                                    return false;
                                })
                                .aO(TooltipHandler.ap(ChatUtils.parseTranslation(
                                        "widget.gui.key-bind-configurate-widget.keycode-d.tooltips", ""))))
                .addToSub(this);
    }

    private void fi() {
        List var1 = this.getKeys();
        if (!var1.isEmpty()) {
            var1.remove(var1.size() - 1);
            this.ackChange(
                    var1, this.dJ.getOriginValue().j(), this.dJ.getOriginValue().k());
        }
    }

    public KeyBindConfigurateWidget(int x, int y, int dx, int dy, AttrKeyValue<MultiKeyBind> config) {
        super(x, y, dx, dy);
        this.dJ = config;
        this.dK = this.dJ.getOriginValue();
        this.init();
    }

    private void fj() {
        ArrayList var1 = new ArrayList();
        var1.addAll(Arrays.asList(this.dK.getKeys()));
        this.ackChange(var1, this.dK.j(), this.dK.k());
    }

    private List<String> getKeys() {
        String[] var1 = this.dJ.getOriginValue().getKeys();
        return new ArrayList<>(Arrays.asList(var1));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        return this.isMouseOver(mouseX, mouseY);
    }

    private void fh() {
        MultiKeyBind var1 = this.dJ.getOriginValue();
        this.dJ.valueChangeInternal(this, var1.m(!var1.k()));
    }

    private void fg() {
        MultiKeyBind var1 = this.dJ.getOriginValue();
        this.dJ.valueChangeInternal(this, var1.l(!var1.j()));
    }

    private Text createKeyDisplay(DrawableWidget el) {
        List var2 = this.getKeys();
        String var3 = var2.isEmpty() ? "None" : String.join(",", var2);
        return this.isFocused() && el == this.B
                ? Text.literal("> " + var3 + " <").formatted(Formatting.GOLD)
                : Text.literal(var3);
    }

    private void ackChange(List<String> keyCodes, boolean toggleOnBindRelease, boolean vanilla) {
        this.dJ.valueChangeInternal(this, new MultiKeyBind(keyCodes, toggleOnBindRelease, vanilla));
    }
}
