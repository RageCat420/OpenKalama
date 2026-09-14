package me.matl114.gui.complex.itemEdit;

import com.google.common.base.Preconditions;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.IconElement;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.config.kv.NbtAttrKeyValue;
import me.matl114.versioned.api.VItem;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class KalamaHelperHelperE extends KalamaHelperHelperJ {
    private final ItemEditScreen bw;

    EditBoxWidget bu;
    protected static final Identifier FORMAT_TEXTURE_SPRITE = me.matl114.gui.KalamaHelperHelperB.e;
    ItemStack bs;
    NbtAttrKeyValue<ItemStack> br;
    ExecutableWidget bt;

    @Override
    protected void ah() {
        this.bu = (EditBoxWidget) (Object) this.br
                .generateEditBox(
                        this.bw.ci.getX() + 10,
                        this.bw.ci.getY() + 10,
                        this.bw.ci.getTextureWidth() - 20,
                        this.bw.ci.getTextureHeight() - 20)
                .ef();
        this.bw.ch.setContentDelegate(this.bu);
    }

    public KalamaHelperHelperE(final ItemEditScreen this$0) {
        super(this$0);
        this.bw = this$0;
        this$0.setTitleLabel(
                Text.translatable("widget.gui.item-edit-screen.snbt-editor").formatted(Formatting.GREEN));
        this.af();
    }

    protected void init() {
        this.br.validateAndUpdate();
        this.bw.v = this.bs.copy();
    }

    @Override
    protected boolean canConfirm() {
        return this.br.isValidate();
    }

    protected void af() {
        this.bs = this.bw.v.copy();
        NbtCompound var1 = VItem.w().k(this.bs, ItemStackUtils.registry());
        this.br = new NbtAttrKeyValue<>("", var1, this::validateItemStack);
        if (!this.br.validateAndUpdate()) {
            this.cK();
        }

        this.bu = (EditBoxWidget) (Object) this.br
                .generateEditBox(
                        this.bw.ci.getX() + 10,
                        this.bw.ci.getY() + 10,
                        this.bw.ci.getTextureWidth() - 20,
                        this.bw.ci.getTextureHeight() - 20)
                .ef();
        this.bt = ExecutableWidget.instance(141, -19, 18, 18)
                .<ExecutableWidget>eV(IconElement.cm(
                                FORMAT_TEXTURE_SPRITE,
                                ButtonAction.a(() -> this.br.applyFormatting(str -> {
                                    if (this.bu != null) {
                                        this.bu.setText(str);
                                    }
                                })))
                        .aO(TooltipHandler.ap(
                                ChatUtils.parseTranslation("widget.gui.item-edit-screen.formatter.tooltips", "")))
                        .ah(icon -> {
                            if (icon instanceof IconElement) {
                                if (this.br.isValidate()) {
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
                .addToSub(this);
    }

    protected ItemStack validateItemStack(NbtElement element) {
        ItemStack var2 = VItem.w().j((NbtCompound) element, ItemStackUtils.registry());
        Preconditions.checkArgument(var2 != ItemStack.EMPTY);
        this.bs = var2;
        return var2;
    }

    protected void cK() {
        throw new RuntimeException("Error while parsing itemStack snbt");
    }

    @Override
    public void ag() {}
}
