package me.matl114.gui.complex.itemEdit;

import com.mojang.datafixers.util.Pair;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.IconElement;
import me.matl114.gui.elements.LabelElement;
import me.matl114.gui.presets.choices.ConfirmingBigScreen;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.config.kv.NbtAttrKeyValue;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.component.ComponentType;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryOps;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public class KalamaHelperHelperN<T> extends ConfirmingBigScreen {
    final Consumer<NbtElement> c;
    EditBoxWidget bu;
    final ComponentType<T> ea;
    ExecutableWidget bt;
    NbtAttrKeyValue<Optional<T>> dY;
    ExecutableWidget eb;
    boolean removal;

    protected KalamaHelperHelperN(ComponentType<T> type, NbtElement currentValue, Consumer<NbtElement> callback) {
        super(Text.empty());
        this.ea = type;
        this.removal = currentValue == null;
        this.dY = new NbtAttrKeyValue<Optional<T>>(
                        "",
                        currentValue,
                        nbt -> nbt == null
                                ? Optional.empty()
                                : Optional.of((T) ((Pair) (Object) this.ea
                                                .getCodec()
                                                .decode(RegistryOps.of(NbtOps.INSTANCE, ItemStackUtils.registry()), nbt)
                                                .getOrThrow())
                                        .getFirst()))
                .setEnableNull(true);
        this.c = callback;
        this.setTitleLabel(
                Text.translatable("widget.gui.item-edit-screen.nbt-editor.component.component-edit-screen.title")
                        .append(Text.literal(String.valueOf(Registries.DATA_COMPONENT_TYPE.getId(type))))
                        .formatted(Formatting.GREEN));
    }

    @Override
    protected void c() {
        this.c.accept(this.dY.getOriginValue());
        this.close();
    }

    @Override
    protected void init() {
        super.init();
        URI var1 = null;

        try {
            String var2 = "https://zh.minecraft.wiki/w/%E6%95%B0%E6%8D%AE%E7%BB%84%E4%BB%B6#"
                    + Registries.DATA_COMPONENT_TYPE.getId(this.ea).getPath();
            var1 = Util.validateUri(var2);
        } catch (Throwable var4) {
        }

        URI var5 = var1;
        MutableText var3 =
                Text.translatable("widget.gui.item-edit-screen.nbt-editor.component.component-edit-screen.open-wiki");
        this.eb = ExecutableWidget.instance(this.x + 5, this.y + 22, this.backgroundWidth - 10, 12)
                .<ExecutableWidget>eV(LabelElement.instance(var3)
                        .cF(me.matl114.gui.basic.KalamaHelperHelperP.aA(() -> {
                            if (var5 != null) {
                                Util.getOperatingSystem().open(var5);
                            }
                        }))
                        .aO(TooltipHandler.ar(() -> List.of(Text.literal(var5 == null ? "" : var5 + "")))))
                .addTo(this);
        this.bu = (EditBoxWidget) (Object) this.dY
                .generateEditBox(
                        this.x + ItemEditScreen.CONTENT_START_X + 10,
                        this.y + CONTENT_START_Y + 30,
                        this.backgroundWidth - 2 * ItemEditScreen.CONTENT_START_X - 20,
                        this.content_end_y - CONTENT_START_Y - 40)
                .<ContentDelegateWidget>addTo(this)
                .ef();
        this.bt = ExecutableWidget.instance(
                        this.x + ItemEditScreen.CONTENT_START_X + 1, this.y + CONTENT_START_Y + 1, 18, 18)
                .<ExecutableWidget>eV(IconElement.cm(
                                KalamaHelperHelperE.FORMAT_TEXTURE_SPRITE,
                                ButtonAction.a(() -> this.dY.applyFormatting(str -> {
                                    if (this.bu != null) {
                                        this.bu.setText(str);
                                    }
                                })))
                        .aO(TooltipHandler.ap(
                                ChatUtils.parseTranslation("widget.gui.item-edit-screen.formatter.tooltips", "")))
                        .ah(icon -> {
                            if (icon instanceof IconElement) {
                                if (this.dY.isValidate()) {
                                    this.bt.setAlpha(1.0F);
                                    return true;
                                } else {
                                    this.bt.setAlpha(0.4F);
                                    return false;
                                }
                            } else {
                                return true;
                            }
                        }))
                .addTo(this);
    }

    @Override
    protected boolean canConfirm(ElementHandler elementHandler) {
        return this.dY.isValidate();
    }
}
