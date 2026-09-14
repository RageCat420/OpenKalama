package me.matl114.gui.complex.slimefun;

import com.google.common.base.Preconditions;
import java.util.function.BiConsumer;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.KalamaHelperHelperP;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.elements.IconElement;
import me.matl114.gui.elements.OutputSlotElement;
import me.matl114.gui.elements.PlateElement;
import me.matl114.gui.elements.SlotElement;
import me.matl114.hacks.InvTasks;
import me.matl114.hacks.SlimefunTasks;
import me.matl114.hacks.utils.recipes.IRecipeEntry;
import me.matl114.hacks.utils.recipes.RecipeIngredient;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.inventory.MyIngredientImmutableInventory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SlimefunRecipeWidget extends KalamaHelperHelperCX {
    RecipeIngredient[] ingredients;
    BiConsumer<ItemStack, Boolean> ae;
    protected static Identifier CANCEL_GUI_TEXTURE = new Identifier("minecraft", "container/beacon/cancel");
    Runnable ah;
    ItemStack aa;
    protected static final int x = 144;
    protected static final int y = 64;
    Inventory ad;
    String ab;
    BiConsumer<String, Boolean> af;
    ItemStack ac;

    public SlimefunRecipeWidget setCancelCallback(Runnable cancelCallback) {
        this.ah = cancelCallback;
        return this;
    }

    private void aH() {
        if (this.ah != null) {
            this.ah.run();
        }
    }

    public SlimefunRecipeWidget(
            int x,
            int y,
            IRecipeEntry entry,
            BiConsumer<ItemStack, Boolean> itemClickEvent,
            BiConsumer<String, Boolean> rtypeClickEvent) {
        super(x, y, 144, 64);
        this.ab = entry.Co();
        this.aa = SlimefunTasks.g(this.ab);
        this.ac = entry.Ct();
        this.ingredients = entry.ingredient();
        Preconditions.checkArgument(this.ingredients.length == 9);
        this.ad = new MyIngredientImmutableInventory(entry.ingredient());
        this.ae = itemClickEvent;
        this.af = rtypeClickEvent;
        this.init0();
    }

    private final void init0() {
        this.setPriority(1);

        for (int var1 = 0; var1 < 3; var1++) {
            for (int var2 = 0; var2 < 3; var2++) {
                int var3 = 3 * var1 + var2;
                ExecutableWidget.instance(15 + 18 * var2, 5 + 18 * var1, 18, 18)
                        .<ExecutableWidget>eV(new SlotElement(this.ad, var3)
                                .cF(KalamaHelperHelperP.au(t -> this.ae.accept(this.ad.getStack(var3), t))))
                        .addToSub(this);
            }
        }

        ExecutableWidget.instance(109, 23, 18, 18)
                .<ExecutableWidget>eV(
                        new OutputSlotElement(this.ac).cF(KalamaHelperHelperP.au(t -> this.ae.accept(this.ac, t))))
                .addToSub(this);
        ExecutableWidget.instance(78, 23, 18, 18)
                .<ExecutableWidget>eV(
                        SlotElement.aI(this.aa).aK(false).cF(KalamaHelperHelperP.au(t -> this.af.accept(this.ab, t))))
                .addToSub(this);
        boolean var5 = MinecraftClient.getInstance().interactionManager != null
                && MinecraftClient.getInstance()
                        .interactionManager
                        .getCurrentGameMode()
                        .isCreative();
        int var6 = 2 + (var5 ? 1 : 0);
        int var7 = 119 - 6 * var6;
        int var4 = 0;
        if (var5) {
            ExecutableWidget.instance(var7 + var4 * 12, 44, 9, 9)
                    .<ExecutableWidget>eV(new ButtonElement(
                                    TextProvider.c(Text.literal("G")),
                                    ButtonAction.a(() -> InvTasks.creativeAddItem(this.ac.copy(), 64)))
                            .aO(TooltipHandler.ap(ChatUtils.parseTranslation(
                                    "widget.gui.slimefun-recipe-widget.creative-give.tooltips", ""))))
                    .addToSub(this);
            var4++;
        }

        ExecutableWidget.instance(var7 + var4 * 12, 44, 9, 9)
                .<ExecutableWidget>eV(new ButtonElement(
                                TextProvider.c(Text.literal("E")),
                                ButtonAction.a(() -> InvTasks.Y(this.ac.copy(), null)))
                        .aO(TooltipHandler.ap(ChatUtils.parseTranslation(
                                "widget.gui.slimefun-recipe-widget.open-editor.tooltips", ""))))
                .addToSub(this);
        ExecutableWidget.instance(var7 + ++var4 * 12, 44, 9, 9)
                .<ExecutableWidget>eV(
                        new ButtonElement(TextProvider.c(Text.literal("+")), ButtonAction.a(() -> InvTasks.aq()
                                        .removeSavedItem(this.ac.copy())))
                                .aO(TooltipHandler.ap(ChatUtils.parseTranslation(
                                        "widget.gui.slimefun-recipe-widget.save-item.tooltips", ""))))
                .addToSub(this);
        ExecutableWidget.instance(128, 0, 16, 16)
                .<ExecutableWidget>eV(IconElement.cm(CANCEL_GUI_TEXTURE, ButtonAction.a(this::aH))
                        .aO(TooltipHandler.ap(ChatUtils.parseTranslation(
                                "widget.gui.slimefun-recipe-widget.close-sub-screen.tooltips", "")))
                        .ag(i -> this.ah != null))
                .addToSub(this);
        ExecutableWidget.instance(0, 0, 144, 64)
                .<ExecutableWidget>eV(PlateElement.ch())
                .addToSub(this, -1);
    }
}
