package me.matl114.gui.complex.itemEdit;

import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.elements.LabelElement;
import me.matl114.gui.elements.SlotElement;
import me.matl114.utils.InventoryUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class KalamaHelperHelperL extends KalamaHelperHelperJ {
    public final ItemEditScreen bw;

    KalamaHelperHelperK dR;
    protected static final int SELECT_WIDTH = 120;
    KalamaHelperHelperQ dP;
    ItemStack dO;
    ContentDelegateWidget<KalamaHelperHelperQ> dQ;

    protected void refreshScreen() {
        this.L();

        for (KalamaHelperHelperK var4 : KalamaHelperHelperK.values()) {
            MutableText var5 = Text.translatable(var4.display).formatted(Formatting.YELLOW);
            MutableText var6 = Text.translatable(var4.display);
            ExecutableWidget.instance(20, var4.ordinal() * 30, 80, 30)
                    .<ExecutableWidget>eV(new ButtonElement(
                            el -> this.dR == var4 ? var5 : var6, ButtonAction.a(() -> this.setCurrentAttr(var4))))
                    .addToSub(this);
        }

        DisplayWidget.instance(30, this.bw.ci.getTextureHeight() - 90, 60, 20)
                .<DrawableWidget>setRenderHandler(
                        LabelElement.instance(Text.translatable("widget.gui.item-edit-screen.nbt-editor.refresh-item")))
                .addToSub(this);
        ExecutableWidget.instance(30, this.bw.ci.getTextureHeight() - 70, 60, 60)
                .<ExecutableWidget>eV(new SlotElement(InventoryUtils.b(() -> this.dO), 0, (it, bt) -> {
                    this.saveChanges();
                    return true;
                }))
                .addToSub(this);
        this.dQ.addToSub(this);
        this.setCurrentAttr(this.dR);
    }

    protected void af() {
        this.dQ = new ContentDelegateWidget<>(120, 0, 0, 0);
        this.setCurrentAttr(this.dR == null ? KalamaHelperHelperK.LA : this.dR);
        this.refreshScreen();
    }

    protected KalamaHelperHelperQ generateCurrentAttrScreen() {
        return (KalamaHelperHelperQ)
                (switch (this.dR) {
                    case LD -> new KalamaHelperHelperD(this);
                    case LE -> new KalamaHelperHelperM(this);
                    case LC -> new KalamaHelperHelperH(this);
                    case LA -> new KalamaHelperHelperC(this);
                    case LB -> new KalamaHelperHelperB(this);
                    default -> null;
                });
    }

    protected void saveChanges() {
        if (this.dP != null) {
            this.dP.ag();
        }

        this.bw.v = this.dO.copy();
    }

    public KalamaHelperHelperL(final ItemEditScreen this$0) {
        super(this$0);
        this.bw = this$0;
        this.dR = null;
        this.dO = this$0.v.copy();
        this$0.setTitleLabel(
                Text.translatable("widget.gui.item-edit-screen.nbt-editor").formatted(Formatting.GREEN));
        this.af();
    }

    protected void setCurrentAttr(KalamaHelperHelperK attr) {
        if (this.dP != null) {
            this.dP.ag();
        }

        if (attr == null) {
            this.dP = null;
        } else {
            KalamaHelperHelperK var2 = this.dR;
            this.dR = attr;
            if (var2 != this.dR) {
                this.dP = this.generateCurrentAttrScreen();
            }
        }

        this.dQ.setContentDelegate(this.dP);
        if (this.dP != null) {
            this.dP.ah();
        }
    }

    @Override
    protected boolean canConfirm() {
        return true;
    }

    @Override
    protected void ah() {}

    @Override
    public void ag() {}
}
