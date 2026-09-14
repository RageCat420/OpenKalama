package me.matl114.gui.elements;

import java.util.function.BooleanSupplier;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.utils.ChatUtils;
import net.minecraft.util.Identifier;

public class KalamaHelperHelperD extends IconElement$SimpleIconElement {
    public static final Identifier o = new Identifier("kalama", "gui/reset");

    public KalamaHelperHelperD(BooleanSupplier canReset, Runnable reset) {
        super(ButtonElement.bJ, ButtonElement.bH, true, ButtonAction.a(() -> {
            if (canReset.getAsBoolean()) {
                reset.run();
            }
        }));
        this.cw(el -> canReset.getAsBoolean());
        this.cD((element, context, mouseX, mouseY, delta, alpha, shouldHighlight) -> {
            int var7 = element.getTextureWidth();
            int var8 = element.getTextureHeight();
            int var9 = var7 / 4;
            int var10 = var8 / 4;
            context.V(o, var9, var10, var7 - 2 * var9, var8 - 2 * var10);
        });
        this.aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.gui.reset-button-element.reset.tooltips", "")));
    }
}
