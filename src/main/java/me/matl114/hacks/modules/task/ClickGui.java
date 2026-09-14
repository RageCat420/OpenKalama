package me.matl114.hacks.modules.task;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import me.matl114.accessors.gui.ScreenAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.BlockUpdate;
import me.matl114.gui.FilterService;
import me.matl114.gui.KalamaHelperHelperB;
import me.matl114.gui.KalamaHelperHelperQ;
import me.matl114.gui.McWidgetHelpers;
import me.matl114.gui.WidgetUtils;
import me.matl114.gui.basic.AbstractElement;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.DynamicListWidget;
import me.matl114.gui.basic.DynamicSubScreenWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.KalamaHelperHelperJ;
import me.matl114.gui.basic.KalamaHelperHelperP;
import me.matl114.gui.basic.RenderHandler;
import me.matl114.gui.basic.ScrollableListWidget;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.complex.clickGui.ClickGuiMainScreen;
import me.matl114.gui.complex.config.ConfigurateNewStyleScreen;
import me.matl114.gui.complex.config.DefaultedKeyValueInputWidget;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.elements.ColorBoxElement;
import me.matl114.gui.elements.ColorLabelTextElement;
import me.matl114.gui.elements.ColorSplitterElement;
import me.matl114.gui.presets.single.CenterScreen;
import me.matl114.hacks.MainTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModuleGroup;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.WrapperConfigRef;
import me.matl114.hacks.modules.HackModules;
import me.matl114.hacks.modules.combat.TargetSelector;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.hacks.utils.config.Vec2;
import me.matl114.hacks.utils.config.WrapColor;
import me.matl114.hooks.BaritoneHooks;
import me.matl114.managers.Configs;
import me.matl114.managers.FileManager;
import me.matl114.managers.config.Config;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.config.Ref;
import me.matl114.managers.file.FileStorage;
import me.matl114.managers.input.IHotKey;
import me.matl114.managers.input.IInputManager;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.algorithms.SerialExecutor;
import me.matl114.utils.config.ValueAccessor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.NotNull;

public class ClickGui extends BaseModule {
    private static final List<Text> ks = List.of(Text.literal("左键切换模块是否启用"), Text.literal("右键打开模块配置界面"));
    public KeyBindRef hotkey;
    public final ModulePath kc;
    private static final String kp = "Search";
    private static final int kv = 18;
    public NBTRef<Vec2> widgetSize;
    private static final int METADATA_MARGIN = 10;
    public FileStorage ki;
    public NBTRef<WrapColor> guiTextStyle;
    private static final List<Text> kt = List.of(Text.literal("点击打开模块配置界面"));
    public NBTRef<WrapColor> guiFrameStyle;
    public static final int kr = 40;
    public ModulePath kd;
    public static final int kq = 5;
    public static ClickGui INSTANCE;
    private WeakReference<ContentDelegateWidget<ExecutableWidget>> ko;
    public NBTRef<WrapColor> guiConfigStyle;
    private static final int ku = 180;
    public final FlagRef enableConfigSubgroup;
    private final SerialExecutor kx;
    private static final int METADATA_WIDTH = 140;
    public final KeyBindRef openMenu;
    private static final int kw = 2;
    public final ModulePath kb = makePath(Configs.r, "hotkeys");
    public NBTRef<WrapColor> guiBackgroundStyle;
    public final KeyBindRef openOptionsMenu;
    public static final BlockUpdate ky = new BlockUpdate(
            () -> INSTANCE.guiTextStyle.get().withAlpha(255),
            () -> INSTANCE.guiFrameStyle.get().withAlpha(255),
            () -> INSTANCE.guiTextStyle.get().withAlpha(255),
            () -> INSTANCE.guiConfigStyle.get().withAlpha(255));

    public DrawableWidget pc(BaseModule baseModule, TaskSubHelperS metaData) {
        short var3 = 330;
        DynamicListWidget var4 = new DynamicListWidget(0, 0, var3);
        var4.ga(ExecutableWidget.instance(0, 0, var3, 18)
                .eV(new ColorLabelTextElement(
                                TextProvider.c(this.getModuleName(baseModule)),
                                () -> this.guiTextStyle.get().withAlpha(255),
                                () -> this.guiFrameStyle.get().withAlpha(255))
                        .aO(TooltipHandler.ap(this.pa(baseModule)))));
        List<WrapperConfigRef<?>> var5 = baseModule.getEditableConfig();
        Map<String, List<WrapperConfigRef<?>>> var6 = this.pd(var5);
        if (this.pe(var6)) {
            for (Entry<String, List<WrapperConfigRef<?>>> var8 : var6.entrySet()) {
                String var9 = (String) var8.getKey();
                if (this.pf(var9)) {
                    var4.ga(this.pi(baseModule, var9, metaData));
                    BooleanSupplier var15 = this.ph(baseModule, var9, metaData);

                    for (WrapperConfigRef<?> var12 : ((List<WrapperConfigRef<?>>) var8.getValue())) {
                        var4.ga(this.pm(var12, var15));
                    }
                } else {
                    for (WrapperConfigRef<?> var11 : ((List<WrapperConfigRef<?>>) var8.getValue())) {
                        var4.ga(this.pl(var11));
                    }
                }
            }
        } else {
            for (WrapperConfigRef var14 : var5) {
                var4.ga(this.pl(var14));
            }
        }

        baseModule.addCustomWidgets(var4::ga, var3, 18, 2);
        return var4;
    }

    public void openConfigurateScreen(BaseModule baseModule, TaskSubHelperS metaData) {
        DrawableWidget var3 = this.pc(baseModule, metaData);
        CenterScreen var4 = new CenterScreen(var3);
        ScreenAccess.of(var4).addCloseFuture(() -> this.setClickGuiMeta(metaData));
        ScreenAccess.of(var4).openFromCurrent();
    }

    private DrawableWidget createCmdMacrosSettings(Screen screen, TaskSubHelperS meta) {
        List var3 = HackModules.getModuleGroups().stream()
                .flatMap(s -> s.getModules().stream())
                .flatMap(s -> s.getEditableConfig().stream())
                .filter(s -> s.h() instanceof KeyBindRef)
                .toList();
        DynamicListWidget var4 = createConfigScreen(
                Text.translatable("widget.click-gui.selection.Hotkeys"), List::of, var3, WidgetUtils.a, ky);
        return WidgetUtils.createCenterScreenWidget(var4, screen.width, screen.height - 24);
    }

    private DrawableWidget createModuleGroup(
            String module, ModuleGroup moduleGroup, TaskSubHelperR slideMeta, TaskSubHelperS metaData) {
        KalamaHelperHelperCX var5 = this.oU(slideMeta);
        DrawableWidget var6 = this.createDragExpandableHead(module, slideMeta);
        var5.Q(var6);
        KalamaHelperHelperCX var7 = this.oW(moduleGroup, metaData);
        var5.Q(new KalamaHelperHelperJ<>(() -> slideMeta.slidingDown ? var7 : null, 0, var6.getHeight()));
        return var5;
    }

    private DrawableWidget createConfig(TaskSubHelperS meta) {
        ConfigurateNewStyleScreen var2 =
                new ConfigurateNewStyleScreen(Config.getConfigs().stream().toList());
        var2.init(mc, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight());
        return new ContentDelegateWidget(0, 0, 0, 0).setContentDelegate(var2);
    }

    private Map<String, List<WrapperConfigRef<?>>> pd(List<WrapperConfigRef<?>> editableConfigs) {
        Map<String, List<WrapperConfigRef<?>>> var2 = new LinkedHashMap<>();

        for (WrapperConfigRef<?> var4 : editableConfigs) {
            String var5 = this.pg(var4);
            var2.computeIfAbsent(var5, ignored -> new ArrayList<>()).add(var4);
        }

        return var2;
    }

    private DrawableWidget pi(BaseModule baseModule, String prefix, TaskSubHelperS metaData) {
        short var4 = 330;
        int var5 = (int) this.widgetSize.get().y();
        int var6 = var5 + 2;
        String var7 = this.pk(baseModule, prefix);
        metaData.b(var7);
        KalamaHelperHelperCX var8 = new KalamaHelperHelperCX(0, 0, var4, var6);
        ExecutableWidget.instance(0, 0, var4, var6)
                .<ExecutableWidget>eV(new AbstractElement()
                        .cF(KalamaHelperHelperP.az(
                                () -> metaData.setSubGroupExpanded(var7, !metaData.isSubGroupExpanded(var7)))))
                .addToSub(var8);
        DisplayWidget.instance(0, 2, var4 - var5, var5)
                .<DrawableWidget>setRenderHandler(new ColorSplitterElement(
                        TextProvider.c(this.getConfigSubGroupTitle(prefix)),
                        this.guiTextStyle.get().withAlpha(255),
                        () -> this.guiBackgroundStyle.get().withAlpha(192)))
                .addToSub(var8);
        ExecutableWidget.instance(var4 - var5, 2, var5, var5)
                .<DrawableWidget>setRenderHandler(new AbstractElement()
                        .cD(RenderHandler.ofColorQuad(
                                this.guiBackgroundStyle.get().withAlpha(192)))
                        .cD((element, context, mouseX, mouseY, delta, alpha, shouldHighlight) -> {
                            context.r(this.guiTextStyle.get().withAlpha(255));
                            context.u(
                                    metaData.isSubGroupExpanded(var7) ? KalamaHelperHelperB.j : KalamaHelperHelperB.k,
                                    element.getTextureWidth() - element.getTextureHeight() + 2,
                                    2,
                                    0,
                                    element.getTextureHeight() - 4,
                                    element.getTextureHeight() - 4);
                            context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                        }))
                .addToSub(var8);
        return var8;
    }

    public ClickGui() {
        super("ClickGui");
        this.kc = makePath(Configs.r, "hotkeys");
        this.kd = makePath(Configs.r, "click-gui");
        this.openMenu = this.hotkey(this.kb.add("open-menu"))
                .defaultValue(new MultiKeyBind(341, 71))
                .registerHotkey(HotKeyUtils.b(this::oL))
                .build();
        this.openOptionsMenu = this.hotkey(this.kb.add("open-options-menu"))
                .defaultValue(new MultiKeyBind())
                .registerHotkey(HotKeyUtils.d(this::openGameOptionsMenu))
                .build();
        this.hotkey = this.hotkey(this.kd.add("hotkey"))
                .defaultValue(new MultiKeyBind(346))
                .registerHotkey(this::onHotkey)
                .build();
        this.widgetSize = this.builder(this.kd.add("widget-size"), Vec2.class)
                .defaultValue(new Vec2(55.0, 12.0))
                .build();
        this.ki = FileManager.getInstance().o("click-gui-data.nbt");
        this.guiFrameStyle = this.builder(this.kd.add("gui-frame-style"), WrapColor.class)
                .defaultValue(new WrapColor("#984FDB"))
                .build();
        this.guiBackgroundStyle = this.builder(this.kd.add("gui-background-style"), WrapColor.class)
                .defaultValue(new WrapColor("#323232"))
                .build();
        this.guiConfigStyle = this.builder(this.kd.add("gui-config-style"), WrapColor.class)
                .defaultValue(new WrapColor("#323232"))
                .build();
        this.guiTextStyle = this.builder(this.kd.add("gui-text-style"), WrapColor.class)
                .defaultValue(new WrapColor(Formatting.WHITE))
                .build();
        this.enableConfigSubgroup = this.builder(this.kd.add("enable-config-subgroup"), Boolean.class)
                .defaultValue(true)
                .build();
        this.ko = null;
        this.kx = new SerialExecutor(CompletableFuture::runAsync);
        INSTANCE = this;
    }

    private DrawableWidget pm(WrapperConfigRef<?> configWidget, BooleanSupplier extraShowCondition) {
        short var3 = 330;
        KalamaHelperHelperCX var4 = new KalamaHelperHelperCX(0, 0, var3, 20);
        var4.Q(DisplayWidget.instance(0, 0, var3, 20));
        DrawableWidget var5 = this.pp(configWidget.h(), configWidget.getKeyName());
        var4.Q(var5);
        BooleanSupplier var6 = configWidget.showPredicate();
        return new KalamaHelperHelperJ<>(
                () -> var6.getAsBoolean() && extraShowCondition.getAsBoolean() ? var4 : null, 0, 0);
    }

    private Text getConfigSubGroupTitle(String prefix) {
        return Text.translatable("config.index." + prefix);
    }

    private DrawableWidget createDragExpandableHead(String module, TaskSubHelperR slideMeta) {
        return ExecutableWidget.instance(0, 0, (int) this.widgetSize.get().x(), (int)
                        this.widgetSize.get().y())
                .eV(new AbstractElement()
                        .cF(new TaskSubHelperI(this, slideMeta))
                        .cD(new ColorLabelTextElement(
                                TextProvider.c(Text.translatableWithFallback(
                                        "widget.click-gui.module-group-name." + module, module)),
                                () -> this.guiTextStyle.get().withAlpha(255),
                                () -> this.guiFrameStyle.get().withAlpha(255)))
                        .cD((element, context, mouseX, mouseY, delta, alpha, shouldHighlight) -> {
                            context.r(this.guiBackgroundStyle.get().withAlpha(255));
                            context.u(
                                    slideMeta.slidingDown ? KalamaHelperHelperB.j : KalamaHelperHelperB.k,
                                    element.getTextureWidth() - element.getTextureHeight() + 2,
                                    2,
                                    0,
                                    element.getTextureHeight() - 4,
                                    element.getTextureHeight() - 4);
                            context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                        })
                        .aO(TooltipHandler.ap(List.of(Text.literal("拖动或鼠标滚轮以修改位置")))));
    }

    public Text getModuleName(BaseModule baseModule) {
        return Text.translatableWithFallback(
                "widget.click-gui.module-name." + baseModule.getModuleManager().getName() + "." + baseModule.getName(),
                baseModule.getName());
    }

    public void pn(BaseModule baseModule) {
        this.openConfigurateScreen(baseModule, this.getClickGuiMetadata());
    }

    public TaskSubHelperS getClickGuiMetadata() {
        NbtCompound var1 = this.ki.b(NbtOps.INSTANCE);
        DataResult var2 = TaskSubHelperS.CODEC.decode(NbtOps.INSTANCE, var1);
        TaskSubHelperS var3;
        if (var2.isSuccess()) {
            var3 = (TaskSubHelperS) ((Pair) var2.getOrThrow()).getFirst();
        } else {
            var3 = new TaskSubHelperS();
        }

        List var4 = this.oO();
        var4.add("Search");
        var3.checkDefault(var4, (int) this.widgetSize.get().x(), (int)
                this.widgetSize.get().y());
        this.setClickGuiMeta(var3);
        return var3;
    }

    private boolean pe(Map<String, List<WrapperConfigRef<?>>> groupedConfigs) {
        return this.enableConfigSubgroup.get() && groupedConfigs.size() > 1;
    }

    public static DynamicListWidget createConfigScreen(
            Text title,
            Supplier<List<Text>> titleTooltips,
            List<WrapperConfigRef<?>> configs,
            KalamaHelperHelperQ layout,
            BlockUpdate palette) {
        int var5 = layout.Nj();
        DynamicListWidget var6 = new DynamicListWidget(0, 0, var5);
        var6.ga(ExecutableWidget.instance(0, 0, var5, layout.buttonHeight())
                .eV(new ColorLabelTextElement(
                                TextProvider.c(title),
                                () -> palette.titleBackgroundColor().getColorInt(),
                                () -> palette.titleTextColor().getColorInt())
                        .aO(TooltipHandler.ar(titleTooltips))));

        for (WrapperConfigRef var8 : configs) {
            KalamaHelperHelperCX var9 =
                    new KalamaHelperHelperCX(0, 0, var5, layout.buttonHeight() + layout.buttonBlank());
            var9.Q(DisplayWidget.instance(0, 0, var5, layout.buttonBlank() + layout.buttonHeight()));
            var9.Q(pD(var8, layout, palette));
            KalamaHelperHelperJ var10 =
                    new KalamaHelperHelperJ<>(() -> var8.showPredicate().getAsBoolean() ? var9 : null, 0, 0);
            var6.ga(var10);
        }

        return var6;
    }

    private String pk(BaseModule baseModule, String prefix) {
        return baseModule.getModuleManager().getName() + "." + baseModule.getName() + ":" + prefix;
    }

    private boolean onHotkey(IHotKey iHotKey, IInputManager manager) {
        if (HotKeyUtils.isValidState()) {
            this.oR();
            return true;
        } else if (mc.currentScreen instanceof ClickGuiMainScreen var4) {
            var4.close();
            return true;
        } else {
            return false;
        }
    }

    public Stream<BaseModule> oS(ModuleGroup group) {
        return group.getModules().stream().filter(BaseModule::shouldShowInGui);
    }

    @NotNull
    private DrawableWidget pp(Ref<?> wrapper, String keyName) {
        short var3 = 330;
        return this.createRefEditor(keyName, wrapper, 0, 2, var3, 18);
    }

    private DrawableWidget createBaseSettings(Screen screen, TaskSubHelperS metaData) {
        DrawableWidget var3 = this.pc(INSTANCE, metaData);
        return WidgetUtils.createCenterScreenWidget(var3, screen.width, screen.height - 24);
    }

    private DrawableWidget pu(Screen screen, TaskSubHelperS meta) {
        DrawableWidget var3 = this.pc(Modules.INSTANCE, meta);
        return WidgetUtils.createCenterScreenWidget(var3, screen.width, screen.height - 24);
    }

    public void openGameOptionsMenu() {
        GameOptions var1 = mc.options;
        ArrayList<SimpleOption> var2 = new ArrayList();

        for (Field var6 : GameOptions.class.getDeclaredFields()) {
            var6.setAccessible(true);
            if (var6.getType().isAssignableFrom(SimpleOption.class)) {
                try {
                    SimpleOption var7 = (SimpleOption) var6.get(var1);
                    if (var7 != null) {
                        var2.add(var7);
                    }
                } catch (Throwable var8) {
                }
            }
        }

        ScrollableListWidget var9 = new ScrollableListWidget(20, 20, 360, 280);
        int var10 = 0;

        for (SimpleOption var13 : var2) {
            ClickableWidget var14 = var13.createWidget(mc.options);
            var9.addScrollingWidget(new ContentDelegateWidget(20, var10, 320, 40).setContentDelegate(var14));
            var10 += var14.getHeight();
        }

        DefaultedKeyValueInputWidget var12 =
                new DefaultedKeyValueInputWidget(Text.literal("Options Screen"), 400, 320, var9);
        ScreenAccess.of(var12).openFromCurrent();
    }

    private DrawableWidget pr(TaskSubHelperS metaData) {
        KalamaHelperHelperCX var2 = new KalamaHelperHelperCX(0, 0, 0, 0);
        int var3 = (int) this.widgetSize.get().x();
        int var4 = (int) this.widgetSize.get().y();
        DynamicListWidget var5 = new DynamicListWidget(0, var4, var3);
        var2.Q(var5);
        Runnable var6 = () -> {
            String var3x = metaData.c;
            ArrayList var4x = new ArrayList();
            ArrayList var5x = new ArrayList();
            if (var3x != null && !var3x.isEmpty()) {
                for (ModuleGroup var7x : HackModules.getModuleGroups()) {
                    this.oS(var7x).forEach(module -> {
                        String var5xx = module.getName();
                        String var6x = ChatUtils.l(this.getModuleName(module));
                        if (FilterService.nameMatch(var5xx, var3x)
                                || !Objects.equals(var5xx, var6x) && FilterService.nameMatch(var6x, var3x)) {
                            var4x.add(module);
                        }

                        if (module.getEditableConfig().stream().anyMatch(editable -> {
                            String var2x = ChatUtils.H(editable.getKeyName());
                            return FilterService.nameMatch(var2x, var3x);
                        })) {
                            var5x.add(module);
                        }
                    });
                }

                mc.execute(() -> {
                    var5.clearChildren();
                    this.createSearchResultGroupSubList(var5::ga, "Name", var4x, metaData);
                    this.createSearchResultGroupSubList(var5::ga, "Setting", var5x, metaData);
                });
            } else {
                mc.execute(var5::clearChildren);
            }
        };
        ContentDelegateWidget var7 = McWidgetHelpers.c(
                0,
                0,
                var3,
                var4,
                (v, t) -> {
                    if (!Objects.equals(t, metaData.c)) {
                        metaData.j(t);
                        this.kx.d(var6);
                    }
                },
                metaData.c);
        var6.run();
        var2.Q(var7);
        return var2;
    }

    private String pg(WrapperConfigRef<?> configWidget) {
        String[] var2 = configWidget.getPath();
        return var2 != null && var2.length > 1 ? String.join(".", Arrays.copyOf(var2, var2.length - 1)) : "";
    }

    private void createSearchResultGroupSubList(
            Consumer<DrawableWidget> childrenAdder, String group, List<BaseModule> list, TaskSubHelperS metaData) {
        MutableBoolean var5 = new MutableBoolean(true);
        int var6 = (int) this.widgetSize.get().x();
        int var7 = (int) this.widgetSize.get().y();
        KalamaHelperHelperCX var8 = new KalamaHelperHelperCX(0, 0, var6, var7);
        ExecutableWidget.instance(0, 0, var6, var7)
                .<ExecutableWidget>eV(
                        new AbstractElement().cF(KalamaHelperHelperP.az(() -> var5.setValue(!var5.booleanValue()))))
                .addToSub(var8);
        DisplayWidget.instance(0, 0, var6 - var7, var7)
                .<DrawableWidget>setRenderHandler(new ColorSplitterElement(
                        TextProvider.c(Text.literal(group)),
                        this.guiTextStyle.get().withAlpha(255),
                        () -> this.guiBackgroundStyle.get().withAlpha(192)))
                .addToSub(var8);
        ExecutableWidget.instance(var6 - var7, 0, var7, var7)
                .<DrawableWidget>setRenderHandler(new AbstractElement()
                        .cD(RenderHandler.ofColorQuad(
                                this.guiBackgroundStyle.get().withAlpha(192)))
                        .cD((element, context, mouseX, mouseY, delta, alpha, shouldHighlight) -> {
                            context.r(this.guiTextStyle.get().withAlpha(255));
                            context.u(
                                    var5.booleanValue() ? KalamaHelperHelperB.j : KalamaHelperHelperB.k,
                                    element.getTextureWidth() - element.getTextureHeight() + 2,
                                    2,
                                    0,
                                    element.getTextureHeight() - 4,
                                    element.getTextureHeight() - 4);
                            context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                        }))
                .addToSub(var8);
        childrenAdder.accept(var8);
        KalamaHelperHelperCX var9 = this.oX(list, metaData);
        var9.S();
        KalamaHelperHelperJ var10 = new KalamaHelperHelperJ<>(() -> var5.booleanValue() ? var9 : null, 0, 0);
        childrenAdder.accept(var10);
    }

    private DrawableWidget pb(BaseModule baseModule, TaskSubHelperS metaData) {
        FlagRef var3 = baseModule.getBindFlag();
        return ExecutableWidget.instance(0, 0, (int) this.widgetSize.get().x(), (int)
                        this.widgetSize.get().y())
                .eV(new ColorBoxElement(
                                var3 != null
                                        ? ButtonAction.b(bl -> {
                                            if (bl) {
                                                var3.toggle();
                                            } else {
                                                this.openConfigurateScreen(baseModule, metaData);
                                            }
                                        })
                                        : ButtonAction.a(() -> this.openConfigurateScreen(baseModule, metaData)),
                                TextProvider.c(this.getModuleName(baseModule)),
                                () -> this.guiBackgroundStyle.get().withAlpha(192),
                                () -> this.guiTextStyle.get().withAlpha(255),
                                (el, bl) -> {
                                    if (var3 != null && var3.get()) {
                                        return this.guiFrameStyle.get().withAlpha(255);
                                    } else {
                                        return bl ? -1 : null;
                                    }
                                })
                        .aO(TooltipHandler.ap(this.getModuleButtonTooltips(baseModule))));
    }

    private BooleanSupplier ph(BaseModule baseModule, String prefix, TaskSubHelperS metaData) {
        String var4 = this.pk(baseModule, prefix);
        metaData.b(var4);
        return () -> metaData.isSubGroupExpanded(var4);
    }

    private KalamaHelperHelperCX oU(TaskSubHelperR slideMeta) {
        return new DynamicSubScreenWidget(
                ValueAccessor.of(slideMeta::c, slideMeta::a), ValueAccessor.of(slideMeta::d, slideMeta::b));
    }

    private DrawableWidget px(Screen screen, TaskSubHelperS meta) {
        List var3 = Stream.of(BindCommand.INSTANCE, EventCommand.INSTANCE)
                .flatMap(s -> s.getEditableConfig().stream())
                .toList();
        DynamicListWidget var4 = createConfigScreen(
                Text.translatable("widget.click-gui.selection.CmdMacros"), List::of, var3, WidgetUtils.a, ky);
        return WidgetUtils.createCenterScreenWidget(var4, screen.width, screen.height - 24);
    }

    private DrawableWidget oT(List<String> modules, TaskSubHelperS meta) {
        KalamaHelperHelperCX var3 = new KalamaHelperHelperCX(0, 0, 0, 0);

        for (String var5 : modules) {
            ModuleGroup var6 = HackModules.getModuleGroup(var5);
            TaskSubHelperR var7 = meta.getModuleMeta(var5);
            var3.Q(this.createModuleGroup(var5, var6, var7, meta));
        }

        var3.Q(this.pq(meta, meta.getModuleMeta("Search")));
        return var3;
    }

    private DrawableWidget pw(Screen screen, TaskSubHelperS meta) {
        DrawableWidget var3 = this.pc(TargetSelector.INSTANCE, meta);
        return WidgetUtils.createCenterScreenWidget(var3, screen.width, screen.height - 24);
    }

    private DrawableWidget pB(Screen screen, TaskSubHelperS metaData) {
        List var3 = BaritoneHooks.getInstance().getAllSettings().entrySet().stream()
                .map(s -> Pair.of("widget.click-gui.baritone." + s.getKey(), s.getValue()))
                .toList();
        DrawableWidget var4 = WidgetUtils.createValueAccessorsEditScreen(
                Text.translatable("widget.click-gui.selection.Baritone"), List::of, var3, WidgetUtils.a, ky, true);
        return WidgetUtils.createCenterScreenWidget(var4, screen.width, screen.height - 24);
    }

    private static KalamaHelperHelperCX pD(
            WrapperConfigRef<?> wrapper, KalamaHelperHelperQ layout, BlockUpdate palette) {
        return WidgetUtils.d(wrapper.h(), wrapper.getKeyName(), layout, palette);
    }

    private DrawableWidget pq(TaskSubHelperS metaData, TaskSubHelperR slideMeta) {
        String var3 = "Search";
        KalamaHelperHelperCX var4 = this.oU(slideMeta);
        DrawableWidget var5 = this.createDragExpandableHead(var3, slideMeta);
        var4.Q(var5);
        DrawableWidget var6 = this.pr(metaData);
        var4.Q(new KalamaHelperHelperJ<>(() -> slideMeta.slidingDown ? var6 : null, 0, var5.getHeight()));
        return var4;
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.ai().c(MultiplayerScreen.class), this::onScreenInitialize);
    }

    public void oJ(Config config) {
        MainTasks.o(config);
    }

    public void oL() {
        MainTasks.n();
    }

    public void resetGui() {
        if (mc.currentScreen instanceof ClickGuiMainScreen var2) {
            var2.close();
        }

        this.ki.write(new NbtCompound(), NbtOps.INSTANCE);
    }

    public void oR() {
        List var1 = this.oO();
        TaskSubHelperS var2 = this.getClickGuiMetadata();
        LinkedHashMap<String, Function<Screen, DrawableWidget>> var3 = new LinkedHashMap<>();
        var3.put("Module", s -> this.oT(var1, var2));
        var3.put("Friends", s -> this.pw(s, var2));
        var3.put("CmdMacros", s -> this.px(s, var2));
        var3.put("Hotkeys", s -> this.createCmdMacrosSettings(s, var2));
        var3.put("BaseSettings", s -> this.pu(s, var2));
        var3.put("GuiSettings", s -> this.createBaseSettings(s, var2));
        var3.put("Config", s -> this.createConfig(var2));
        if (BaritoneHooks.getInstance().isBaritoneAPISupported()) {
            var3.put("Baritone", s -> this.pB(s, var2));
        }

        ClickGuiMainScreen var4 = new ClickGuiMainScreen(var3);
        ScreenAccess.of(var4).addCloseFuture(() -> this.setClickGuiMeta(var2));
        ScreenAccess.of(var4).openFromCurrent();
    }

    public void onScreenInitialize(Event<MultiplayerScreen> screenEvent) {
        if (screenEvent.e() instanceof MultiplayerScreen var3) {
            ExecutableWidget var5 = ExecutableWidget.instance(0, 0, 100, 20)
                    .eV(new ButtonElement(TextProvider.c(Text.literal("Phoenix")), ButtonAction.a(this::oR)));
            if (this.ko != null && this.ko.get() != null) {
                ScreenAccess.of(var3).removeChildFrom(this.ko.get());
            }

            this.ko = null;
            ContentDelegateWidget var4 = new ContentDelegateWidget(var3.width - 100, 5, 0, 0);
            var4.setContentDelegate(var5);
            var4.addTo(var3);
            this.ko = new WeakReference<>(var4);
        }
    }

    private DrawableWidget pl(WrapperConfigRef<?> configWidget) {
        return this.pm(configWidget, () -> true);
    }

    private boolean pf(String prefix) {
        return prefix != null && !prefix.isEmpty();
    }

    public List<Text> getModuleButtonTooltips(BaseModule baseModule) {
        ArrayList var2 = new ArrayList<>(ChatUtils.parseTranslation(
                "widget.click-gui.module-name." + baseModule.getModuleManager().getName() + "." + baseModule.getName()
                        + ".tooltips",
                ""));
        if (!var2.isEmpty()) {
            var2.add(Text.empty());
        }

        if (baseModule.getBindFlag() != null) {
            var2.addAll(ks);
        } else {
            var2.addAll(kt);
        }

        return var2;
    }

    public List<String> oO() {
        return new ArrayList<>(HackModules.main.getModuleGroups().keySet());
    }

    public List<Text> pa(BaseModule baseModule) {
        return ChatUtils.parseTranslation(
                "widget.click-gui.module-name." + baseModule.getModuleManager().getName() + "." + baseModule.getName()
                        + ".tooltips",
                "暂无介绍");
    }

    public void setClickGuiMeta(TaskSubHelperS meta) {
        NbtElement var2 = (NbtElement)
                TaskSubHelperS.CODEC.encodeStart(NbtOps.INSTANCE, meta).getOrThrow();
        this.ki.write(var2, NbtOps.INSTANCE);
    }

    private KalamaHelperHelperCX oW(ModuleGroup moduleGroup, TaskSubHelperS metaData) {
        return this.oX(this.oS(moduleGroup).toList(), metaData);
    }

    private DrawableWidget pz(TaskSubHelperS meta) {
        return new KalamaHelperHelperCX(0, 0, 0, 0);
    }

    private KalamaHelperHelperCX oX(Collection<BaseModule> baseModules, TaskSubHelperS metaData) {
        KalamaHelperHelperCX var3 = new KalamaHelperHelperCX(0, 0, 0, 0);
        int var4 = 0;

        for (BaseModule var6 : baseModules) {
            DrawableWidget var7 = this.pb(var6, metaData);
            var3.Q(new ContentDelegateWidget<DrawableWidget>(0, var4, 0, 0).setContentDelegate(var7));
            var4 += var7.getHeight();
        }

        return var3;
    }
}
