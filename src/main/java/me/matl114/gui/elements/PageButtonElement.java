package me.matl114.gui.elements;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.utils.ChatUtils;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class PageButtonElement extends ButtonElement {
    protected static final Identifier t = new Identifier("kalama", "gui/arrow_right");
    private IntSupplier p;
    protected static final Identifier s = new Identifier("kalama", "gui/arrow_left");
    private IntSupplier q;
    private int delta;

    public PageButtonElement(
            List<Text> pageSwitch, int maxPage, IntSupplier pageGetter, IntConsumer pageSetter, int delta) {
        this(pageSwitch, () -> maxPage, pageGetter, pageSetter, delta);
    }

    @Override
    public void renderTexture(VDrawContext context, DrawableWidget element, boolean highlight) {
        int var4 = this.q.getAsInt() + this.delta;
        boolean var5 = var4 <= 0 || var4 > this.p.getAsInt();
        context.V(var5 ? bJ : (highlight ? bI : bH), 0, 0, element.getTextureWidth(), element.getTextureHeight());
        float var6 = 0.125F * element.getTextureWidth();
        float var7 = 0.125F * element.getTextureHeight();
        context.u(this.delta < 0 ? s : t, (int) var6, (int) var7, 0, (int) (element.getTextureWidth() - var6), (int)
                (element.getTextureHeight() - var7));
    }

    public static PageButtonElement aa(IntSupplier maxPage, IntSupplier page, IntConsumer set) {
        return new PageButtonElement(
                ChatUtils.parseTranslation("widget.gui.page-button-element.prev.tooltips", ""), maxPage, page, set, -1);
    }

    public PageButtonElement(
            List<Text> pageSwitch, IntSupplier maxPage, IntSupplier pageGetter, IntConsumer pageSetter, int delta) {
        super(TextProvider.c(null), (element, widget, mouseButton) -> {
            int var7 = pageGetter.getAsInt();
            int var8 = MathHelper.clamp(var7 + delta, 1, maxPage.getAsInt());
            pageSetter.accept(var8);
            return true;
        });
        this.p = maxPage;
        this.q = pageGetter;
        this.delta = delta;
        this.aO(new TooltipHandler(pageSwitch));
    }

    public static PageButtonElement ab(IntSupplier maxPage, IntSupplier page, IntConsumer set) {
        return new PageButtonElement(
                ChatUtils.parseTranslation("widget.gui.page-button-element.next.tooltips", ""), maxPage, page, set, 1);
    }

    public PageButtonElement(List<Text> pageSwitch, int maxPage, AtomicInteger page, boolean left) {
        this(pageSwitch, maxPage, page::get, page::set, left ? -1 : 1);
    }
}
