package me.matl114.gui.complex.slimefun;

import com.google.common.collect.Streams;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import java.util.stream.Stream;
import me.matl114.accessors.interfaces.TileInventory;
import me.matl114.gui.FilterService;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.KalamaHelperHelperP;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.elements.LabelElement;
import me.matl114.gui.elements.PageButtonElement;
import me.matl114.gui.elements.SlotElement;
import me.matl114.gui.presets.single.IntFastInputWidget;
import me.matl114.hacks.SlimefunTasks;
import me.matl114.hacks.modules.slimefun.MultiBlockHelper;
import me.matl114.hacks.utils.recipes.IRecipeEntry;
import me.matl114.managers.Tasks;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.ScreenUtils;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.BaseAttrKeyValue;
import me.matl114.utils.config.ValueAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class SlimefunDispensorSuggestBookWidget extends KalamaHelperHelperCX {
    protected static final ContentDelegateWidget<ExecutableWidget>[] contents = new ContentDelegateWidget[40];
    protected ExecutableWidget fs;
    protected Collection<String> fH;
    protected static final ItemStack fj = new ItemStack(Items.KNOWLEDGE_BOOK);
    protected volatile List<IRecipeEntry> fA;
    protected ExecutableWidget fr;
    protected static final Text fn =
            Text.translatable("widget.gui.slimefun-dispensor-suggest-book-widget.multiblock-execute");
    int aX;
    protected static final Text fm =
            Text.translatable("widget.gui.slimefun-dispensor-suggest-book-widget.refresh-soft");
    protected ExecutableWidget fw;
    protected static final int fh = 68;
    protected static final int fg = 8;
    protected TileInventory fI;
    protected static String fK;
    protected static final int ff = 74;
    protected static final int fL = 96;
    protected static final int y = 75;
    protected ExecutableWidget fp;
    protected static final Text fo =
            Text.translatable("widget.gui.slimefun-dispensor-suggest-book-widget.multiblock-auto");
    protected ExecutableWidget fq;
    protected ExecutableWidget ft;
    protected ExecutableWidget fx;
    protected boolean onlyShowRelated = true;
    protected static boolean fJ;
    protected static final int x = 168;
    protected static final int fi = 56;
    protected ContentDelegateWidget<DrawableWidget> fz;
    protected static final Text bz = Text.translatable("widget.gui.slimefun-dispensor-suggest-book-widget.title");
    protected static final int fe = 10;
    protected ContentDelegateWidget<DrawableWidget> ba;
    protected ExecutableWidget fu;
    protected static final int fE = 40;
    protected static final ItemStack fk = new ItemStack(Items.STRUCTURE_VOID);
    protected DrawableWidget fy;
    protected ExecutableWidget fv;
    protected static boolean fG;
    protected BiConsumer<Integer, IRecipeEntry> fC;
    protected static final int fM = 42;
    int aW;
    protected volatile List<IRecipeEntry> fB;
    protected static final Text fl =
            Text.translatable("widget.gui.slimefun-dispensor-suggest-book-widget.refresh-hard");

    public void gH() {
        CompletableFuture.runAsync(this::calculateMatchingRecipes)
                .thenRun(this::refreshFilter)
                .thenRun(this::resetPage);
    }

    public int cn() {
        return this.aW;
    }

    protected boolean active(ElementHandler el) {
        return fG;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public synchronized boolean refreshFilter() {
        List<IRecipeEntry> var1 = this.fA;
        if (fK != null && !fK.isEmpty()) {
            this.fB = Streams.concat(
                            Stream.of(IRecipeEntry.EMPTY), var1.stream().filter(t -> FilterService.a.test(fK, t)))
                    .toList();
            return true;
        } else if (this.fB != var1) {
            this.fB = var1;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.onlyShowRelated) {
            Tasks.l(this::gH, 5);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void gA() {
        if (fG) {
            this.fz.setContentDelegate(this.fy);
            Tasks.l(this::gH, 4);
        } else {
            this.fz.setContentDelegate(null);
        }
    }

    @Override
    public <T extends DrawableWidget> T addToSub(KalamaHelperHelperCX screen) {
        this.fy.addToSub(screen);
        return super.addToSub(screen);
    }

    protected void openInputIntScreen(int originValue, IntConsumer intCallback) {
        if (this.ba != null) {
            BaseAttrKeyValue var3 = AttrKeyValue.clampedInt(
                    "widget.gui.slimefun-dispensor-suggest-book-widget.input-count", originValue, 0, 64);
            IntFastInputWidget var4 = IntFastInputWidget.instance(
                            var3, attr -> intCallback.accept(attr.getOriginValue()), 0, 16, 96, 30, 64)
                    .setFinishRunning(() -> this.ba.setContentDelegate(null));
            this.ba.setContentDelegate(var4);
        }
    }

    static {
        for (int var0 = 0; var0 < 40; var0++) {
            if (var0 < 20) {
                int var1 = var0 % 4;
                int var2 = var0 / 4;
                contents[var0] = new ContentDelegateWidget<>(var1 * 12, 10 + var2 * 12, 12, 12);
            } else {
                int var11 = var0 - 20;
                int var12 = var11 % 4;
                int var3 = var11 / 4;
                contents[var0] = new ContentDelegateWidget<>(120 + var12 * 12, 10 + var3 * 12, 12, 12);
            }
        }

        fJ = true;
        fK = "";
    }

    public ExecutableWidget gE(IRecipeEntry recipeEntry) {
        return ExecutableWidget.instance(0, 0, 12, 12)
                .eV(SlotElement.aI(recipeEntry.Ct())
                        .cF(new KalamaHelperHelperF(this, recipeEntry))
                        .ag(this::active));
    }

    public void setPage(int p) {
        int var2 = this.aX;
        this.aX = MathHelper.clamp(p, 1, this.aW);
        if (this.aX != var2) {
            this.resetPage();
        }
    }

    public void resetPage() {
        List<IRecipeEntry> var1 = this.fB;
        if (var1 != null && !var1.isEmpty()) {
            int var5 = var1.size();
            this.aW = (var5 - 1) / 40 + 1;
            this.aX = MathHelper.clamp(this.aX, 1, this.aW);
            int var3 = (this.aX - 1) * 40;

            for (int var4 = var3; var4 < var3 + 40; var4++) {
                if (var4 < var5) {
                    contents[var4 - var3].setContentDelegate(this.gE((IRecipeEntry) var1.get(var4)));
                } else {
                    contents[var4 - var3].setContentDelegate(null);
                }
            }
        } else {
            this.aW = 1;
            this.aX = 1;

            for (int var2 = 0; var2 < 40; var2++) {
                contents[var2].setContentDelegate(null);
            }
        }
    }

    @Override
    public <T extends DrawableWidget> T addTo(Screen screen) {
        this.fz.addTo(screen);
        return super.addTo(screen);
    }

    public int cq() {
        return this.aX;
    }

    protected void setHoveringRecipe(IRecipeEntry entry, double mouseX, double mouseY) {
        if (this.ba != null) {
            SlimefunRecipeWidget var6 = SlimefunEntryListScreen.generateRecipeEntryContent(entry, -24, 0)
                    .setCancelCallback(() -> this.ba.setContentDelegate(null));
            this.ba.setContentDelegate(var6);
        }
    }

    protected void gz() {
        fG = !fG;
        this.gA();
    }

    public SlimefunDispensorSuggestBookWidget(
            TileInventory tile,
            int x,
            int y,
            Collection<String> optionalType,
            BiConsumer<Integer, IRecipeEntry> callback) {
        super(x, y, 168, 75);
        this.aX = 1;
        this.aW = 1;
        this.fC = callback;
        this.fH = optionalType;
        this.fI = tile;
        this.af();
    }

    protected boolean gB() {
        return !fG;
    }

    public void af() {
        this.fp = ExecutableWidget.instance(0, 0, 8, 8)
                .<ExecutableWidget>eV(SlotElement.aI(fj)
                        .cF(KalamaHelperHelperP.aA(this::gz))
                        .aO(TooltipHandler.ap(ChatUtils.parseTranslation(
                                "widget.gui.slimefun-dispensor-suggest-book-widget.book.tooltips", ""))))
                .addToSub(this);
        this.ba = new ContentDelegateWidget(36, 14, 96, 42).addToSub(this, 500);
        this.fs = ExecutableWidget.instance(12, 0, 18, 8)
                .<ExecutableWidget>eV(new ButtonElement(el -> fJ ? fl : fm, ButtonAction.a(() -> {
                            fJ = !fJ;
                            Tasks.l(this::gH, 5);
                        }))
                        .aO(TooltipHandler.ap(ChatUtils.parseTranslation(
                                "widget.gui.slimefun-dispensor-suggest-book-widget.refresh-rule.tooltips", ""))))
                .addToSub(this);
        this.ft = ExecutableWidget.instance(30, 0, 18, 8)
                .<ExecutableWidget>eV(new ButtonElement(TextProvider.c(fn), ButtonAction.a(() -> {
                            SlimefunTasks.v()
                                    .onMultiBlockExecute(MinecraftClient.getInstance().currentScreen, false, false);
                            Tasks.l(this::gH, 2);
                        }))
                        .aO(TooltipHandler.ap(ChatUtils.parseTranslation(
                                "widget.gui.slimefun-dispensor-suggest-book-widget.multiblock-execute-one.tooltips",
                                "")))
                        .ah(el -> MinecraftClient.getInstance().currentScreen instanceof TileInventory var2
                                && !var2.isVirtual()))
                .addToSub(this);
        this.fq = ExecutableWidget.instance(52, 0, 8, 8)
                .<ExecutableWidget>eV(
                        PageButtonElement.aa(this::cn, this::cq, this::setPage).ag(this::active))
                .addToSub(this);
        this.fr = ExecutableWidget.instance(108, 0, 8, 8)
                .<ExecutableWidget>eV(
                        PageButtonElement.ab(this::cn, this::cq, this::setPage).ag(this::active))
                .addToSub(this);
        this.fu = ExecutableWidget.instance(60, 0, 48, 8)
                .<ExecutableWidget>eV(new LabelElement(bz, -1)
                        .cF(KalamaHelperHelperP.aA(() -> {
                            this.onlyShowRelated = !this.onlyShowRelated;
                            Tasks.l(this::gH, 5);
                        }))
                        .aO(TooltipHandler.ap(ChatUtils.parseTranslation(
                                "widget.gui.slimefun-dispensor-suggest-book-widget.title.show-related.tooltips", "")))
                        .ag(this::active))
                .addToSub(this);
        this.fx = ExecutableWidget.instance(160, 0, 8, 8)
                .<ExecutableWidget>eV(SlotElement.aI(fk)
                        .cF(KalamaHelperHelperP.aA(this::gH))
                        .aO(TooltipHandler.ap(ChatUtils.parseTranslation(
                                "widget.gui.slimefun-dispensor-suggest-book-widget.refresh.tooltips", "")))
                        .ag(this::active))
                .addToSub(this);
        this.fv = ExecutableWidget.instance(120, 0, 18, 8)
                .<ExecutableWidget>eV(new ButtonElement(TextProvider.c(fn), ButtonAction.a(() -> {
                            SlimefunTasks.v()
                                    .onMultiBlockExecute(
                                            MinecraftClient.getInstance().currentScreen,
                                            true,
                                            ScreenUtils.hasShiftDown());
                            Tasks.l(this::gH, 5);
                        }))
                        .aO(TooltipHandler.ap(ChatUtils.parseTranslation(
                                "widget.gui.slimefun-dispensor-suggest-book-widget.multiblock-execute.tooltips", "")))
                        .ah(el -> MinecraftClient.getInstance().currentScreen instanceof TileInventory var2
                                && !var2.isVirtual()))
                .addToSub(this);
        this.fw = ExecutableWidget.instance(138, 0, 18, 8)
                .<ExecutableWidget>eV(new ButtonElement(TextProvider.c(fo), (element, widget, mouseButton) -> {
                            if (MinecraftClient.getInstance().currentScreen instanceof TileInventory var4) {
                                MultiBlockHelper var5 = SlimefunTasks.v();
                                if (var5.isMultiBlockExecuting(var4)) {
                                    widget.setAlpha(0.4F);
                                    var5.toggleMultiBlockAutoExecuteState(var4, false);
                                } else {
                                    widget.setAlpha(1.0F);
                                    var5.toggleMultiBlockAutoExecuteState(var4, true);
                                }
                            }

                            return true;
                        })
                        .aO(TooltipHandler.ap(ChatUtils.parseTranslation(
                                "widget.gui.slimefun-dispensor-suggest-book-widget.multiblock-auto.tooltips", "")))
                        .ah(el -> MinecraftClient.getInstance().currentScreen instanceof TileInventory var2
                                && !var2.isVirtual()))
                .<DrawableWidget>setAlpha(SlimefunTasks.v().isMultiBlockExecuting(this.fI) ? 1.0F : 0.4F)
                .addToSub(this);

        for (int var1 = 0; var1 < 40; var1++) {
            contents[var1].addToSub(this);
        }

        this.fy = FilterService.createFilter(
                ValueAccessor.of(() -> fK, s -> fK = s),
                v -> {
                    if (this.refreshFilter()) {
                        this.resetPage();
                    }
                },
                57,
                68,
                56,
                7);
        this.fz = new ContentDelegateWidget<DrawableWidget>(0, 0, 0, 0)
                .setContentDelegate(fG ? this.fy : (DrawableWidget) null)
                .addToSub(this);
        this.gA();
    }

    public synchronized void calculateMatchingRecipes() {
        ArrayList<IRecipeEntry> var1 = new ArrayList<>();
        List<IRecipeEntry> var2;
        if (this.onlyShowRelated) {
            var2 = MinecraftClient.getInstance().player != null
                    ? SlimefunTasks.getInventoryRelativeRecipes(MinecraftClient.getInstance().currentScreen, fJ)
                    : SlimefunTasks.o().toList();
        } else {
            var2 = SlimefunTasks.o().toList();
        }

        if (this.fH != null && !this.fH.isEmpty()) {
            var2 = var2.stream().filter(it -> this.fH.contains(it.Co())).toList();
        }

        var1.add(IRecipeEntry.EMPTY);
        var1.addAll(var2);
        this.fA = var1;
    }
}
