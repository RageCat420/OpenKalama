package me.matl114.hacks.modules.slimefun;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import me.matl114.gui.FilterService;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperP;
import me.matl114.gui.complex.slimefun.SavedItemWidget;
import me.matl114.gui.complex.slimefun.SlimefunChoiceScreen;
import me.matl114.gui.complex.slimefun.SlimefunEntryListScreen;
import me.matl114.gui.elements.SlotElement;
import me.matl114.gui.presets.choices.KalamaHelperHelperA;
import me.matl114.gui.presets.choices.QuestionScreen;
import me.matl114.hacks.InvTasks;
import me.matl114.hacks.KalamaHelperHelperC;
import me.matl114.hacks.RecipeTasks;
import me.matl114.hacks.SlimefunTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.utils.recipes.IRecipeEntry;
import me.matl114.hacks.utils.recipes.RecipeIngredient;
import me.matl114.managers.TaskManagers;
import me.matl114.utils.Debug;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.ScreenUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class SlimefunGuide extends BaseModule {
    private static final Text NF = Text.literal("全部保存物品");
    private static final Text ND = Text.literal("全部记录配方类型");
    public static BiPredicate<String, SlimefunSubHelperQ> NH = (str, i) -> FilterService.nameMatch(i.id(), str);
    public static final List<Text> NG =
            List.of(Text.literal("左键获得一组该物品(仅限创造)"), Text.literal("shift左键拷贝/give指令"), Text.literal("右键打开物品编辑器"));
    public static final String Nx = "slime-guide";
    private static final Text NB = Text.literal("全部记录物品");
    public static final List<Text> NC = List.of(
            Text.literal("左键查看当前物品合成表"),
            Text.literal("右键查看包含当前物品的合成表"),
            Text.literal("Shift右键的时候会同时显示原版物品配方"),
            Text.literal("中键的时候会尝试获取物品"));
    private final List<KalamaHelperHelperA> QUESTION_SOLUTIONS;
    private boolean reject = false;
    private static final Text NE = Text.literal("全部原版配方");
    private static final Text Nz = Text.literal("您当前并未启用配方记录功能,无法体验完整版GUIDE功能,请问您该如何选择?");

    @Override
    public void registerAll() {
        super.registerAll();
        TaskManagers.b().register("button-task.slime-guide", this::adZ);
    }

    public void aea() {
        if (!this.handleNotEnable()) {
            SlimefunTasks.openOrSwitch(new SlimefunChoiceScreen<>(
                            NF,
                            NG,
                            () -> InvTasks.aq().getSavedItemDataMap().keySet().stream()
                                    .map(SlimefunTasks::e)
                                    .toList(),
                            entry -> new ExecutableWidget(0, 0, 16, 16)
                                    .eV(SlotElement.instance(entry.copyWithCount(1), (item, button) -> {
                                        if (button == 0) {
                                            this.tryGetItemStack(item);
                                            return true;
                                        } else if (button == 1) {
                                            SlimefunTasks.openOrSwitch(SlimefunEntryListScreen.mapToWidget(
                                                    List.of(entry), iv -> new SavedItemWidget(0, 0, iv, null)));
                                            return true;
                                        } else {
                                            return false;
                                        }
                                    })),
                            Function.identity())
                    .h(FilterService.b));
        }
    }

    public void adY(SlimefunSubHelperQ type) {
        if (!this.handleNotEnable()) {
            SlimefunTasks.openOrSwitch(SlimefunEntryListScreen.eF(SlimefunTasks.n().values().stream()
                    .filter(i -> Objects.equals(i.Co(), type.id()))
                    .map(IRecipeEntry.class::cast)
                    .toList()));
        }
    }

    public void onClickItemStack(ItemStack item, boolean isLeft) {
        if (!this.handleNotEnable()) {
            if (!item.isEmpty()) {
                ArrayList var3 = new ArrayList();
                boolean var4 = ScreenUtils.hasShiftDown();
                if (isLeft) {
                    String var5 = SlimefunTasks.generateId(item);

                    for (IRecipeEntry var7 : SlimefunTasks.n().values()) {
                        if (var4) {
                            if (Objects.equals(var5, SlimefunTasks.generateId(var7.Ct()))) {
                                var3.add(var7);
                            }
                        } else if (ItemStackUtils.matchItemWithout(var7.Ct(), item, false, false, false)) {
                            var3.add(var7);
                        }
                    }

                    for (KalamaHelperHelperC var20 : RecipeTasks.getAllRecipe().values()) {
                        if (var4) {
                            if (var20.output().isOf(item.getItem())) {
                                var3.add(var20);
                            }
                        } else if (ItemStackUtils.matchItemWithout(var20.output(), item, false, false, false)) {
                            var3.add(var20);
                        }
                    }
                } else {
                    String var16 = SlimefunTasks.generateId(item);

                    for (SlimefunSubHelperJ var21 : SlimefunTasks.u().WK().values()) {
                        for (ItemStack var11 : var21.Zb()) {
                            if (var4) {
                                if (Objects.equals(var16, SlimefunTasks.generateId(var11))) {
                                    var3.add(var21);
                                    break;
                                }
                            } else if (ItemStackUtils.matchItemWithout(item, var11, false, false, false)) {
                                var3.add(var21);
                                break;
                            }
                        }
                    }

                    label87:
                    for (KalamaHelperHelperC var22 : RecipeTasks.getAllRecipe().values()) {
                        for (RecipeIngredient var26 : var22.ingredient()) {
                            if (var4) {
                                if (var26.testItemType(item)) {
                                    var3.add(var22);
                                    break;
                                }
                            } else if (!item.isEmpty()) {
                                for (ItemStack var15 : var26.matchingStack()) {
                                    if (ItemStackUtils.matchItemWithout(var15, item, false, false, false)) {
                                        var3.add(var22);
                                        continue label87;
                                    }
                                }
                            }
                        }
                    }
                }

                if (!var3.isEmpty()) {
                    SlimefunTasks.openOrSwitch(SlimefunEntryListScreen.eF(var3));
                }
            }
        }
    }

    public void adV() {
        Debug.b("您仍旧可以继续使用GUIDE功能,在这次启动中该弹窗将不再弹出");
        this.reject = true;
    }

    public void handleAutoEnable() {
        RecipeDatabase var1 = SlimefunTasks.u();
        var1.ae.set(true);
        var1.saveData.set(true);
        Debug.b("配方自动记录功能已开启,请使用ctrl+G打开Slimefun settings设置具体参数");
        Debug.b(Text.literal("注意: 在1.20.5以上的物品数据和1.20.4及以下不互通,如果你进入了via支持的服务器,请注意这一点!")
                .formatted(Formatting.YELLOW));
    }

    public void aec() {
        if (!this.handleNotEnable()) {
            SlimefunTasks.openOrSwitch(new SlimefunChoiceScreen<>(
                            NE,
                            NC,
                            () -> RecipeTasks.getAllRecipe().values().stream().toList(),
                            rp -> new ExecutableWidget(0, 0, 16, 16)
                                    .eV(SlotElement.instance(rp.Ct().copyWithCount(1), (item, button) -> {
                                        if (button == 0) {
                                            this.adX(rp);
                                            return true;
                                        } else if (button == 1) {
                                            this.onClickItemStack(rp.Ct(), ScreenUtils.hasShiftDown());
                                            return true;
                                        } else if (button == 2) {
                                            this.tryGetItemStack(rp.Ct());
                                            return true;
                                        } else {
                                            return false;
                                        }
                                    })),
                            KalamaHelperHelperC::Ct)
                    .h((java.util.function.BiPredicate<String, KalamaHelperHelperC>)
                            (java.util.function.BiPredicate) FilterService.a));
        }
    }

    public void tryGetItemStack(ItemStack item) {
        if (ScreenUtils.hasShiftDown()) {
            Debug.b(Text.literal("拷贝了物品的Give指令到剪切板").formatted(Formatting.YELLOW));
            InvTasks.copyGiveCommand(item.copy());
        } else if (mc.player != null
                && mc.interactionManager.getCurrentGameMode().isCreative()) {
            InvTasks.creativeAddItem(item.copy(), 64);
        } else {
            Debug.b(Text.literal("当前并不处于创造模式,无法获取保存物品!").formatted(Formatting.YELLOW));
            Debug.b(Text.literal("请使用Shift点击来获取物品的Give指令!").formatted(Formatting.YELLOW));
        }
    }

    public SlimefunGuide() {
        super("SlimefunGuide");
        this.QUESTION_SOLUTIONS = List.of(
                KalamaHelperHelperA.of(Text.literal("我已知晓该功能,一键启用"), this::handleAutoEnable),
                KalamaHelperHelperA.of(Text.literal("我已知晓该功能,但不启用"), this::adV),
                KalamaHelperHelperA.of(Text.literal("我已知晓该功能,一键启用"), this::handleAutoEnable),
                KalamaHelperHelperA.of(Text.literal("我已知晓该功能,但不启用"), this::adV),
                KalamaHelperHelperA.of(Text.literal("我已知晓该功能,一键启用"), this::handleAutoEnable));
    }

    public void aed(String type, boolean isLeft) {
        if (!this.handleNotEnable()) {
            List var4;
            if (RecipeTasks.isVanillaRecipeType(type)) {
                Map<Identifier, KalamaHelperHelperC> var3 = RecipeTasks.getAllRecipe();
                var4 = var3.values().stream()
                        .filter(i -> Objects.equals(i.rid(), type))
                        .map(IRecipeEntry.class::cast)
                        .toList();
            } else {
                var4 = SlimefunTasks.n().values().stream()
                        .filter(i -> i.Co().equals(type))
                        .toList();
            }

            if (!var4.isEmpty()) {
                SlimefunTasks.openOrSwitch(SlimefunEntryListScreen.eF(var4));
            }
        }
    }

    public void adX(IRecipeEntry recipe) {
        if (!this.handleNotEnable()) {
            SlimefunTasks.openOrSwitch(SlimefunEntryListScreen.eF(List.of(recipe)));
        }
    }

    public boolean handleNotEnable() {
        RecipeDatabase var1 = SlimefunTasks.u();
        if ((!var1.ae.get() || !var1.saveData.get()) && !this.reject) {
            SlimefunTasks.openOrSwitch(new QuestionScreen(Nz, this.QUESTION_SOLUTIONS));
            return true;
        } else {
            return false;
        }
    }

    public void aeb() {
        if (!this.handleNotEnable()) {
            SlimefunTasks.openOrSwitch(new SlimefunChoiceScreen<>(
                            ND,
                            SlimefunTasks.u().WJ().values().stream().toList(),
                            ct -> new ExecutableWidget(0, 0, 16, 16)
                                    .eV(SlotElement.aI(ct.amY()).cF(KalamaHelperHelperP.au(t -> this.adY(ct)))),
                            SlimefunSubHelperQ::amY)
                    .h(NH));
        }
    }

    public void adZ() {
        if (!this.handleNotEnable()) {
            SlimefunTasks.openOrSwitch(new SlimefunChoiceScreen<>(
                            NB,
                            NC,
                            () -> SlimefunTasks.u().WK().values().stream().toList(),
                            entry -> new ExecutableWidget(0, 0, 16, 16)
                                    .eV(SlotElement.instance(entry.Ct().copyWithCount(1), (item, button) -> {
                                        if (button == 0) {
                                            this.adX(entry);
                                            return true;
                                        } else if (button == 1) {
                                            this.onClickItemStack(item, ScreenUtils.hasShiftDown());
                                            return true;
                                        } else if (button == 2) {
                                            this.tryGetItemStack(item);
                                            return true;
                                        } else {
                                            return false;
                                        }
                                    })),
                            SlimefunSubHelperJ::Ct)
                    .h((java.util.function.BiPredicate<String, SlimefunSubHelperJ>)
                            (java.util.function.BiPredicate) FilterService.a));
        }
    }
}
