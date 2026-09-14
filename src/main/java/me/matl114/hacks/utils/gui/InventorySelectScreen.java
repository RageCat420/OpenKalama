package me.matl114.hacks.utils.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import me.matl114.accessors.gui.ScreenAccess;
import me.matl114.accessors.interfaces.TileInventory;
import me.matl114.gui.FilterService;
import me.matl114.gui.GenericBackGroundScreen;
import me.matl114.gui.a.e.KalamaHelperHelperA;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperP;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.complex.invcache.InventoryViewScreen;
import me.matl114.gui.elements.SlotElement;
import me.matl114.hacks.InvTasks;
import me.matl114.hacks.KalamaHelperHelperCX;
import me.matl114.hacks.KalamaHelperHelperDX;
import me.matl114.hacks.KalamaHelperHelperSX;
import me.matl114.hacks.RenderTasks;
import me.matl114.hacks.modules.inv.InvSubHelperV;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.WorldUtils;
import me.matl114.utils.config.ValueAccessor;
import me.matl114.utils.world.ContainerPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

public class InventorySelectScreen extends GenericBackGroundScreen {
    private static final List<Text> bD =
            List.of(Text.literal("点击切换容器过滤规则"), Text.empty(), Text.literal("当前过滤规则: 拒绝虚拟容器(即不存在实体方块的容器)"));
    private static final List<Text> bC =
            List.of(Text.literal("点击切换容器过滤规则"), Text.empty(), Text.literal("当前过滤规则: 接受虚拟容器(即不存在实体方块的容器)"));
    private static final List<Text> bA = List.of();
    private final KalamaHelperHelperA<InvSubHelperV> grid;
    private static final Text bz = Text.literal("缓存物品界面预览");
    public static String o = "";
    private boolean filterVirtual = true;
    private static final int PAGE_LABEL_HEIGHT = 12;

    protected DrawableWidget makeIcon(InvSubHelperV screen) {
        ArrayList var2 = new ArrayList();
        var2.add(Text.literal("容器标题: ").append(screen.g().orElse(Text.empty())));
        var2.add(Text.literal("左键点击预览容器内容"));
        var2.add(Text.literal("右键点击渲染容器位置(如果有)"));
        var2.add(Text.empty());
        ItemStack var3 = screen.h().<ItemStack>map(ItemStack::new).orElse(InvTasks.d);
        if (screen.i().isPresent()) {
            ContainerPosition var4 = screen.i().get();
            BlockPos var5 = var4.vO().YO();
            var2.add(Text.literal("记录位置: ")
                    .append(Text.literal("[%d, %d, %d]".formatted(var5.getX(), var5.getY(), var5.getZ()))
                            .formatted(Formatting.GREEN)));
            var2.add(Text.literal("记录世界: ")
                    .append(Text.literal(var4.world().getValue().toString())));
        } else {
            var2.add(Text.literal("虚拟容器").formatted(Formatting.YELLOW));
        }

        return ExecutableWidget.instance(0, 0, 16, 16)
                .eV(SlotElement.aI(var3 == null ? InvTasks.d : var3)
                        .aK(false)
                        .cF(KalamaHelperHelperP.au(l -> {
                            if (l) {
                                this.openInventoryViewScreen(screen);
                            } else if (screen instanceof TileInventory var3x
                                    && !var3x.isVirtual()
                                    && WorldUtils.areWorldEquals(
                                            MinecraftClient.getInstance().world, var3x.getWorld())) {
                                ContainerPosition var4x = var3x.getContainerPosition();
                                RenderTasks.i(new KalamaHelperHelperCX(
                                        120,
                                        new KalamaHelperHelperDX(var4x.vG(), Color.GREEN),
                                        new KalamaHelperHelperSX(var4x.vF(), Color.RED)));
                                this.close();
                            }
                        }))
                        .aO(TooltipHandler.ap(var2)));
    }

    @Override
    protected void init() {
        super.init();
        int var1 = this.backgroundHeight - 32;
        this.grid.dE(var1);
        new ContentDelegateWidget<KalamaHelperHelperA<InvSubHelperV>>(this.x, this.y, 0, 0)
                .setContentDelegate(this.grid)
                .addTo(this);
    }

    @Override
    protected void runClickTitle(boolean isLeft) {
        this.filterVirtual = !this.filterVirtual;
        this.grid.dI();
    }

    public InventorySelectScreen(Supplier<List<InvSubHelperV>> handledScreens) {
        super(bz, 240, 320);
        this.grid = new KalamaHelperHelperA<>(
                0,
                20,
                this.backgroundWidth,
                12,
                0,
                this.backgroundHeight - 32,
                -4,
                16,
                16,
                16,
                handledScreens,
                this::filterInventory,
                ValueAccessor.of(() -> o, s -> o = s),
                this::makeIcon);
    }

    protected void openInventoryViewScreen(InvSubHelperV screen) {
        if (screen.f() != null) {
            ScreenAccess.of(new InventoryViewScreen(screen.f())).openFromCurrent();
        } else {
            ScreenAccess.of(new InventoryViewScreen(
                            screen.d(),
                            screen.g().orElse(Text.empty()),
                            screen.h().<ItemStack>map(ItemStack::new).orElse(InvTasks.d)))
                    .openFromCurrent();
        }
    }

    protected boolean filterInventory(String value, InvSubHelperV screen) {
        return (!this.filterVirtual || screen.i().isPresent())
                && (FilterService.nameMatch(
                                screen.g().orElse(Text.empty()).getString().replace("§.", ""), value)
                        || InventoryUtils.streamInventory(screen.d())
                                .filter(i -> !i.isEmpty())
                                .map(i -> i.getName().getString().replace("§.", ""))
                                .anyMatch(i -> FilterService.nameMatch(i, value)));
    }

    @Override
    protected List<Text> provideTitleTooltips(DrawableWidget widget) {
        return this.filterVirtual ? bD : bC;
    }
}
