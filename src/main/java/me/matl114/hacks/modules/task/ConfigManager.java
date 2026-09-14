package me.matl114.hacks.modules.task;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.matl114.commands.MainCommand;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.hacks.MainTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.WrapperConfigRef;
import me.matl114.hacks.modules.HackModules;
import me.matl114.managers.Configs;
import me.matl114.managers.FileManager;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.Config;
import me.matl114.managers.config.ConfigOp;
import me.matl114.managers.config.ListRef;
import me.matl114.managers.config.MapRef;
import me.matl114.managers.config.Ref;
import me.matl114.managers.file.FileStorage;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperD;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperH;
import me.matl114.utils.commands.commandGroup.SubCommand;
import me.matl114.utils.commands.commandGroup.TaskSubCommand;
import me.matl114.utils.commands.commandGroup.TreeSubCommand;
import me.matl114.utils.commands.params.ArgumentInputStream;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.KalamaHelperHelperA;
import me.matl114.utils.commands.params.KalamaHelperHelperB;
import me.matl114.utils.commands.params.api.KalamaHelperHelperF;
import me.matl114.utils.config.BaseAttrKeyValue;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class ConfigManager extends BaseModule {
    private final ListRef privacyProtectionPathKeywords;
    private static final Set<String> yo = Set.of("slimefunhelper", "slime");
    public static final Codec<MapRef> yq = Codec.PASSTHROUGH.comapFlatMap(
            dynamic -> {
                Object var1 = dynamic.convert(ConfigOp.INSTANCE).getValue();
                return var1 instanceof MapRef var2
                        ? DataResult.success(var2)
                        : DataResult.error(() -> "Config payload is not a MapRef: " + var1);
            },
            mapRef -> new Dynamic(ConfigOp.INSTANCE, mapRef));
    private final ModulePath aD = makePath(Configs.r, "config");

    public void LB(ArgumentInputStream args, ArgumentReader argsReader) {
        String var3 = args.o();

        String var4;
        try {
            var4 = LP(var3);
        } catch (IllegalArgumentException var14) {
            Debug.b(Text.literal(var14.getMessage()).formatted(Formatting.RED));
            return;
        }

        if (FileManager.getInstance().s(var4)) {
            Debug.b(Text.literal("当前配置文件已存在: " + var4).formatted(Formatting.RED));
            Debug.b(Text.literal("点击本文本打开文件夹以查看或重命名")
                    .formatted(Formatting.YELLOW)
                    .styled(style -> style.withClickEvent(ChatUtils.getOpenFile(FileManager.f))));
        } else {
            List<BaseModule> var5 = this.LG(argsReader);
            if (!var5.isEmpty()) {
                LinkedHashMap<Identifier, MapRef> var6 = new LinkedHashMap<>();

                for (BaseModule var8 : var5) {
                    for (WrapperConfigRef var10 : var8.getEditableConfig()) {
                        Config var11 = var10.getConfig();
                        String[] var12 = var10.getPath();
                        Ref var13 = var10.h();
                        var6.computeIfAbsent(var11.getRegistryKey().getValue(), k -> new MapRef())
                                .setValue(var13, var12);
                    }
                }

                List var15 = this.privacyProtectionPathKeywords.get();

                for (Identifier var18 : new HashSet<>(var6.keySet())) {
                    if (this.LL(var18, var15)) {
                        Debug.b(Text.literal("保存时跳过配置: " + var18 + " 以避免隐私信息泄露(可在设置中调整关键词)")
                                .formatted(Formatting.YELLOW));
                        var6.remove(var18);
                    }
                }

                TaskSubHelperA var17 = new TaskSubHelperA(var6);
                this.LC(var4, var17);
            }
        }
    }

    private static void LO(List<TaskSubHelperM> result, List<String> path, MapRef mapRef) {
        for (Entry var4 : mapRef.getValue().entrySet()) {
            path.add((String) var4.getKey());
            Ref var5 = (Ref) var4.getValue();
            if (var5 instanceof MapRef var6) {
                LO(result, path, var6);
            } else {
                result.add(new TaskSubHelperM(path.toArray(String[]::new), var5));
            }

            path.remove(path.size() - 1);
        }
    }

    public void Lx(ArgumentInputStream args) {
        String var2 = args.o();
        String var3 = args.o();
        Config var4 = (Config) Config.REGISTRY.get(Identifier.tryParse(var2));
        if (var4 == null) {
            Debug.b(Text.literal("未找到配置文件: " + var2).formatted(Formatting.RED));
        } else {
            Ref var5 = var4.get(var3.split("\\."));
            if (var5 == null) {
                Debug.b(Text.literal("未找到配置项: " + var2 + "." + var3).formatted(Formatting.RED));
            } else if (!var5.hasDefaultValue()) {
                Debug.b(Text.literal("配置项没有默认值: " + var2 + "." + var3).formatted(Formatting.RED));
            } else {
                var5.resetValue();
                Debug.b(Text.literal("成功重置配置项: " + var2 + "." + var3).formatted(Formatting.GREEN));
            }
        }
    }

    private MapRef LK(Config config, String pathPrefix) {
        if (pathPrefix.isEmpty()) {
            return config.asRef();
        } else {
            String var3 = pathPrefix.endsWith(".") ? pathPrefix.substring(0, pathPrefix.length() - 1) : pathPrefix;
            MapRef var4 = new MapRef();

            for (TaskSubHelperM var6 : LN(config.asRef())) {
                String var7 = String.join(".", var6.path());
                if (var7.equals(var3) || var7.startsWith(var3 + ".")) {
                    var4.setValue(var6.Xm(), var6.path());
                }
            }

            return var4;
        }
    }

    public void LD(ArgumentInputStream args) {
        String var2 = args.f();
        if (var2 == null) {
            this.LM();
        } else {
            String var3;
            try {
                var3 = LP(var2);
            } catch (IllegalArgumentException var15) {
                Debug.b(Text.literal(var15.getMessage()).formatted(Formatting.RED));
                this.LM();
                return;
            }

            String var4 = args.o();
            String var5 = args.o();
            Config var6 = null;
            if (!"all".equalsIgnoreCase(var4)) {
                var6 = Lq(Identifier.tryParse(var4));
                if (var6 == null) {
                    Debug.b(Text.literal("未找到配置文件: " + var4).formatted(Formatting.RED));
                    return;
                }
            }

            TaskSubHelperA var7 = this.LE(var3);
            if (var7 != null) {
                if (var6 == null) {
                    for (Entry var9 : var7.jr().entrySet()) {
                        Config var10 = Lq((Identifier) var9.getKey());
                        if (var10 == null) {
                            Debug.b(Text.literal("跳过未注册配置: " + var9.getKey()).formatted(Formatting.YELLOW));
                        } else {
                            for (TaskSubHelperM var12 : LN((MapRef) var9.getValue())) {
                                String var13 = String.join(".", var12.path());
                                if (var13.startsWith(var5)) {
                                    Ref var14 = var10.get(var12.path());
                                    if (var14 != null) {
                                        var14.copyValueFrom(var12.Xm());
                                    }
                                }
                            }
                        }
                    }
                } else {
                    MapRef var16 = Lr(var7, var6.getRegistryKey().getValue());
                    if (var16 != null) {
                        for (TaskSubHelperM var18 : LN(var16)) {
                            String var19 = String.join(".", var18.path());
                            if (var19.startsWith(var5)) {
                                Ref var20 = var6.get(var18.path());
                                if (var20 != null) {
                                    var20.copyValueFrom(var18.Xm());
                                }
                            }
                        }
                    }
                }

                Debug.b(Text.literal("成功加载配置快照" + var3).formatted(Formatting.GREEN));
            }
        }
    }

    private List<BaseModule> LG(ArgumentReader argsReader) {
        Map var2 = HackModules.getModuleGroups().stream()
                .flatMap(s -> s.getModules().stream())
                .collect(Collectors.toMap(s -> s.getName().toLowerCase(Locale.ROOT), b -> (BaseModule) b));
        ArrayList var3 = new ArrayList();

        for (String var7 : argsReader.k()) {
            if (var7 != null && !var7.isBlank()) {
                this.LH(var3, var2, var7);
            }
        }

        return var3;
    }

    public void Lu() {
        Tasks.l(Config::reloadAll, 1);
        Debug.b(Text.literal("成功重载配置文件").formatted(Formatting.GREEN));
    }

    public TaskSubHelperA LE(String fileName) {
        TaskSubHelperA var6;
        try (FileStorage var2 = FileManager.getInstance().v(fileName, true, false)) {
            if (var2 == null) {
                Debug.b(Text.literal("配置快照不存在: " + fileName).formatted(Formatting.RED));
                this.LM();
                return null;
            }

            var2.h();
            Ref var3 = var2.b(ConfigOp.INSTANCE);
            DataResult<TaskSubHelperA> var4 = TaskSubHelperA.CODEC.parse(ConfigOp.INSTANCE, var3);
            if (var4.isError()) {
                String var11 = var4.error().<String>map(err -> err.message()).orElse("未知解码错误");
                Debug.b(Text.literal("加载配置快照失败: " + var11).formatted(Formatting.RED));
                return null;
            }

            TaskSubHelperA var5 = (TaskSubHelperA) var4.result().get();
            var5 = LQ(var5);
            var6 = var5;
        }

        return var6;
    }

    private static String LP(String rawPath) {
        String var1 = rawPath == null ? "" : rawPath.trim();
        if (var1.isEmpty()) {
            throw new IllegalArgumentException("配置快照名称不能为空");
        } else if (!var1.contains("/") && !var1.contains("\\")) {
            int var2 = var1.lastIndexOf(46);
            String var3 = var2 > 0 ? var1.substring(0, var2) : var1;
            if (!var3.isEmpty() && !".".equals(var3) && !"..".equals(var3)) {
                for (int var4 = 0; var4 < var3.length(); var4++) {
                    char var5 = var3.charAt(var4);
                    if (var5 < ' ' || "<>:\"/\\|?*".indexOf(var5) >= 0) {
                        throw new IllegalArgumentException("配置快照名称不是合法文件名: " + rawPath);
                    }
                }

                return var3 + ".nbt";
            } else {
                throw new IllegalArgumentException("配置快照名称不是合法文件名");
            }
        } else {
            throw new IllegalArgumentException("配置快照名称不能包含路径分隔符");
        }
    }

    private boolean LL(Identifier identifier, List<String> privacyKeywords) {
        String var3 = identifier.getPath();
        return privacyKeywords.stream()
                .filter(keyword -> keyword != null && !keyword.isBlank())
                .anyMatch(var3::contains);
    }

    private static List<TaskSubHelperM> LN(MapRef mapRef) {
        ArrayList var1 = new ArrayList();
        LO(var1, new ArrayList<>(), mapRef);
        return var1;
    }

    public void LF(ArgumentInputStream args, ArgumentReader argsReader) {
        String var3 = args.f();
        if (var3 == null) {
            this.LM();
        } else {
            String var4;
            try {
                var4 = LP(var3);
            } catch (IllegalArgumentException var16) {
                Debug.b(Text.literal(var16.getMessage()).formatted(Formatting.RED));
                this.LM();
                return;
            }

            List<BaseModule> var5 = this.LG(argsReader);
            if (!var5.isEmpty()) {
                TaskSubHelperA var6 = this.LE(var4);
                if (var6 != null) {
                    for (BaseModule var8 : var5) {
                        for (WrapperConfigRef var10 : var8.getEditableConfig()) {
                            Config var11 = var10.getConfig();
                            String[] var12 = var10.getPath();
                            Ref var13 = var10.h();
                            MapRef var14 =
                                    var6.snapSnot().get(var11.getRegistryKey().getValue());
                            if (var14 != null) {
                                Ref var15 = var14.get(var12);
                                if (var15 != null) {
                                    var13.copyValueFrom(var15);
                                }
                            }
                        }
                    }

                    Debug.b(Text.literal("成功加载配置快照" + var4).formatted(Formatting.GREEN));
                }
            }
        }
    }

    private static MapRef Lr(TaskSubHelperA snapshot, Identifier configId) {
        MapRef var2 = snapshot.jr().get(configId);
        if (var2 != null) {
            return var2;
        } else {
            for (Entry var4 : snapshot.jr().entrySet()) {
                if (Objects.equals(Lp((Identifier) var4.getKey()), configId)) {
                    return (MapRef) var4.getValue();
                }
            }

            return null;
        }
    }

    private Stream<String> LJ(String configName) {
        if ("all".equalsIgnoreCase(configName)) {
            return Config.REGISTRY.stream()
                    .flatMap(config -> config.getVisiblePaths().stream())
                    .filter(Objects::nonNull)
                    .distinct()
                    .sorted();
        } else {
            Config var2 = (Config) Config.REGISTRY.get(Identifier.tryParse(configName));
            return var2 == null
                    ? Stream.empty()
                    : var2.getVisiblePaths().stream()
                            .filter(Objects::nonNull)
                            .distinct()
                            .sorted();
        }
    }

    public void Ly(ArgumentInputStream args, ArgumentReader reader) {
        List<BaseModule> var3 = this.LG(reader);
        if (!var3.isEmpty()) {
            for (BaseModule var5 : var3) {
                for (WrapperConfigRef var7 : var5.getEditableConfig()) {
                    var7.h().resetValue();
                }

                Debug.b(Text.literal("成功重置模块配置项: " + var5.getName()).formatted(Formatting.GREEN));
            }
        }
    }

    public Stream<String> LI() {
        return HackModules.getModuleGroups().stream()
                .flatMap(s -> s.getModules().stream().map(BaseModule::getName))
                .distinct()
                .sorted();
    }

    public void Lt() {
        Tasks.l(MainTasks::n, 1);
        Debug.b(Text.literal("成功打开配置文件界面").formatted(Formatting.GREEN));
    }

    private static Config Lq(Identifier id) {
        return (Config) Config.REGISTRY.get(Lp(id));
    }

    @Override
    public void addCustomWidgets(Consumer<DrawableWidget> acceptor, int dx, int dy, int dblank) {
        super.addCustomWidgets(acceptor, dx, dy, dblank);
        acceptor.accept(this.createTitleLabel("widget.config-manager.command", 0, dblank, dx, dy));
    }

    public void Ls(MainCommand mainCommand) {
        TreeSubCommand var2 = mainCommand.bD().a("config").k();
        KalamaHelperHelperB var3 = KalamaHelperHelperA.a()
                .B("config_name")
                .c(KalamaHelperHelperF.g(() -> Config.REGISTRY.stream()
                        .map(Config::getRegistryKey)
                        .filter(Objects::nonNull)
                        .map(registryKey -> registryKey.getValue().toString())))
                .v();
        KalamaHelperHelperB var4 = KalamaHelperHelperA.a()
                .B("config_name")
                .b("all")
                .l("all")
                .c(KalamaHelperHelperF.g(() -> Config.REGISTRY.stream()
                        .map(Config::getRegistryKey)
                        .filter(Objects::nonNull)
                        .map(registryKey -> registryKey.getValue().toString())))
                .v();
        KalamaHelperHelperB var5 = KalamaHelperHelperA.a()
                .B("path")
                .c(KalamaHelperHelperF.h((sender, configName) -> {
                    Config var2x = (Config) Config.REGISTRY.get(Identifier.tryParse(configName));
                    return var2x == null
                            ? Stream.empty()
                            : Stream.concat(var2x.getVisiblePaths().stream(), var2x.getPaths().stream())
                                    .distinct()
                                    .sorted();
                }))
                .v();
        KalamaHelperHelperB var6 = KalamaHelperHelperA.a()
                .B("path")
                .c(KalamaHelperHelperF.g(() -> Stream.of("<填写路径>")))
                .v();
        KalamaHelperHelperB var7 = KalamaHelperHelperA.a()
                .B("path_prefix")
                .b("")
                .c(KalamaHelperHelperF.h((sender, configName) -> this.LJ(configName)))
                .v();
        KalamaHelperHelperB var8 = KalamaHelperHelperA.a()
                .B("path")
                .c(KalamaHelperHelperF.g(() -> Stream.of("<填写路径>")))
                .c(KalamaHelperHelperF.g(me.matl114.utils.a.KalamaHelperHelperA.z(
                        FileManager.f, sx -> sx.endsWith(".nbt") || sx.endsWith(".dat"))))
                .v();
        var2.<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                .u("open")
                .x("message.command.config.open.help")
                .z(e -> e.executor(KalamaHelperHelperH.g(this::Lt)))
                .r()
                .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                .u("reload")
                .x("message.command.config.reload.help")
                .z(e -> e.executor(KalamaHelperHelperH.g(this::Lu)))
                .r()
                .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                .u("openfolder")
                .x("message.command.config.openfolder.help")
                .z(s -> s.executor(KalamaHelperHelperH.g(this::Lv)))
                .r()
                .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                .u("save")
                .x("message.command.config.save.help")
                .A(var6)
                .A(var4)
                .A(var7)
                .z(e -> e.executor(KalamaHelperHelperH.i(this::LA)))
                .r()
                .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                .u("savemodule")
                .x("message.command.config.savemodule.help")
                .A(var6)
                .z(e -> e.executor(new TaskSubHelperC(this)))
                .r()
                .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                .u("load")
                .x("message.command.config.load.help")
                .A(var8)
                .A(var4)
                .A(var7)
                .z(e -> e.executor(KalamaHelperHelperH.i(this::LD)))
                .r()
                .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                .u("loadmodule")
                .x("message.command.config.loadmodule.help")
                .A(var8)
                .z(e -> e.executor(new TaskSubHelperT(this)))
                .r()
                .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                .u("set")
                .x("message.command.config.set.help")
                .A(var3)
                .A(var5)
                .A(KalamaHelperHelperA.a().B("string").v())
                .z(e -> e.executor(KalamaHelperHelperH.i(this::Lw)))
                .r()
                .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                .u("reset")
                .x("message.command.config.reset.help")
                .A(var3)
                .A(var5)
                .z(e -> e.executor(KalamaHelperHelperH.i(this::Lx)))
                .r()
                .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                .u("resetall")
                .x("message.command.config.resetall.help")
                .A(var4)
                .A(KalamaHelperHelperA.a().B("confirm").b("").l("--confirm").v())
                .z(e -> e.executor(KalamaHelperHelperH.i(this::Lz)))
                .r()
                .subBuilder(SubCommand.bo())
                .u("resetmodule")
                .x("message.command.config.resetmodule.help")
                .z(e -> e.executor(new TaskSubHelperP(this)))
                .r();
    }

    public void LC(String fileName, TaskSubHelperA snapshot) {
        DataResult<Ref<?>> var3 = TaskSubHelperA.CODEC.encodeStart(ConfigOp.INSTANCE, snapshot);
        if (var3.isError()) {
            String var9 = var3.error().<String>map(err -> err.message()).orElse("未知编码错误");
            Debug.b(Text.literal("保存配置快照失败: " + var9).formatted(Formatting.RED));
        } else {
            try (FileStorage var4 = FileManager.getInstance().t(fileName).t()) {
                var4.write(var3.result().get(), ConfigOp.INSTANCE);
                Debug.b(Text.literal("成功保存配置快照: " + fileName + " ,点击本文本打开文件夹")
                        .formatted(Formatting.GREEN)
                        .styled(style -> style.withClickEvent(
                                ChatUtils.getOpenFile(var4.q().getParentFile()))));
            }
        }
    }

    public void Lv() {
        Util.getOperatingSystem().open(FileManager.f);
        Debug.b(Text.literal("成功打开配置保存与导入文件夹").formatted(Formatting.GREEN));
    }

    public void Lw(ArgumentInputStream args) {
        String var2 = args.o();
        String var3 = args.o();
        String var4 = args.o();
        Config var5 = (Config) Config.REGISTRY.get(Identifier.tryParse(var2));
        if (var5 == null) {
            Debug.b(Text.literal("未找到配置文件: " + var2).formatted(Formatting.RED));
        } else {
            Ref var6 = var5.get(var3.split("\\."));
            if (var6 == null) {
                Debug.b(Text.literal("未找到配置项: " + var2 + "." + var3).formatted(Formatting.RED));
            } else {
                BaseAttrKeyValue var7 = var6.createKeyValue(var3);
                var7.valueChange(this, var4);
                if (!var7.isValidate()) {
                    Debug.b(Text.literal("配置项格式不正确: " + var2 + "." + var3).formatted(Formatting.RED));
                } else {
                    Debug.b(Text.literal("成功设置配置项: " + var2 + "." + var3).formatted(Formatting.GREEN));
                }
            }
        }
    }

    private static TaskSubHelperA LQ(TaskSubHelperA snapshot) {
        if (PORT_CONFIG_MAPS.isEmpty()) {
            return snapshot;
        } else {
            Map<Identifier, MapRef> var1 = new LinkedHashMap<>(snapshot.jr());
            boolean var2 = false;

            for (Entry<ModulePath, ModulePath> var4 : PORT_CONFIG_MAPS.entrySet()) {
                ModulePath var5 = var4.getKey();
                ModulePath var6 = var4.getValue();
                Identifier var7 = var5.getConfig().getRegistryKey().getValue();
                Identifier var8 = var6.getConfig().getRegistryKey().getValue();
                if (var1.containsKey(var7)) {
                    MapRef var9 = var1.get(var7);
                    Ref<?> var10 = var9.get(var5.toPath());
                    if (var10 != null) {
                        var2 = true;
                        MapRef var11 = var1.computeIfAbsent(var8, vvv -> new MapRef());
                        var9.setValue(null, var5.toPath());
                        var11.setValue(var10, var6.toPath());
                    }
                }
            }

            return var2 ? new TaskSubHelperA(var1) : snapshot;
        }
    }

    public ConfigManager() {
        super("ConfigManager");
        this.privacyProtectionPathKeywords = this.builder(this.aD.add("privacy-protection-path-keywords"), ListRef.TYPE)
                .defaultValue(List.of("chat", "http"))
                .build();
    }

    private void LH(List<BaseModule> result, Map<String, BaseModule> moduleMap, String moduleName) {
        BaseModule var4 = (BaseModule) moduleMap.get(moduleName.toLowerCase(Locale.ROOT));
        if (var4 == null) {
            Debug.b(Text.literal("未找到模块: " + moduleName).formatted(Formatting.RED));
        } else {
            if (!result.contains(var4)) {
                result.add(var4);
            }
        }
    }

    public void LA(ArgumentInputStream args) {
        String var2 = args.o();

        String var3;
        try {
            var3 = LP(var2);
        } catch (IllegalArgumentException var12) {
            Debug.b(Text.literal(var12.getMessage()).formatted(Formatting.RED));
            return;
        }

        if (FileManager.getInstance().s(var3)) {
            Debug.b(Text.literal("当前配置文件已存在: " + var3).formatted(Formatting.RED));
            Debug.b(Text.literal("点击本文本打开文件夹以查看或重命名")
                    .formatted(Formatting.YELLOW)
                    .styled(style -> style.withClickEvent(ChatUtils.getOpenFile(FileManager.f))));
        } else {
            String var4 = args.o();
            String var5 = args.o().trim();
            TaskSubHelperA var11;
            if ("all".equalsIgnoreCase(var4)) {
                List var6 = this.privacyProtectionPathKeywords.get();
                LinkedHashMap var7 = new LinkedHashMap();

                for (Config var9 : Config.REGISTRY) {
                    if (var9.getRegistryKey() != null) {
                        Identifier var10 = var9.getRegistryKey().getValue();
                        if (this.LL(var10, var6)) {
                            Debug.b(Text.literal("保存时跳过配置: " + var10 + " 以避免隐私信息泄露(可在设置中调整关键词)")
                                    .formatted(Formatting.YELLOW));
                        } else {
                            var7.put(var10, this.LK(var9, var5));
                        }
                    }
                }

                var11 = new TaskSubHelperA(var7);
            } else {
                Config var13 = (Config) Config.REGISTRY.get(Identifier.tryParse(var4));
                if (var13 == null) {
                    Debug.b(Text.literal("未找到配置文件: " + var4).formatted(Formatting.RED));
                    return;
                }

                LinkedHashMap var15 = new LinkedHashMap();
                Identifier var16 = var13.getRegistryKey().getValue();
                var15.put(var16, this.LK(var13, var5));
                var11 = new TaskSubHelperA(var15);
            }

            DataResult var14 = TaskSubHelperA.CODEC.encodeStart(ConfigOp.INSTANCE, var11);
            this.LC(var3, var11);
        }
    }

    public void Lz(ArgumentInputStream args) {
        String var2 = args.o();
        String var3 = args.o();
        if ("--confirm".equals(var3)) {
            if ("all".equalsIgnoreCase(var2)) {
                for (Config var5 : Config.REGISTRY) {
                    for (String var7 : var5.getVisiblePaths()) {
                        Ref var8 = var5.get(Config.cutToPath(var7));
                        if (var8 != null && var8.hasDefaultValue()) {
                            var8.resetValue();
                        }
                    }
                }
            } else {
                Config var9 = (Config) Config.REGISTRY.get(Identifier.tryParse(var2));
                if (var9 == null) {
                    Debug.b(Text.literal("未找到配置文件: " + var2).formatted(Formatting.RED));
                    return;
                }

                for (String var11 : var9.getVisiblePaths()) {
                    Ref var12 = var9.get(Config.cutToPath(var11));
                    if (var12 != null && var12.hasDefaultValue()) {
                        var12.resetValue();
                    }
                }
            }
        } else {
            Debug.chat(
                    ChatUtils.textFromLegacyString("&c该指令将会重置部分配置文件,是否确认? "),
                    Text.literal("[确认]")
                            .formatted(Formatting.RED)
                            .formatted(Formatting.BOLD)
                            .styled(style -> style.withClickEvent(ChatUtils.getSuggestCommand(
                                    MainCommand.l + "config resetall " + var2 + " --confirm"))));
        }
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerCommandBootstrap(this::Ls);
    }

    private static Identifier Lp(Identifier id) {
        return id != null && yo.contains(id.getNamespace()) ? Identifier.of("kalama", id.getPath()) : id;
    }

    private void LM() {
        Debug.b(Text.literal("请将保存的 config 文件拖到配置快照目录中，点击本文本打开文件夹")
                .formatted(Formatting.YELLOW)
                .styled(style -> style.withClickEvent(ChatUtils.getOpenFile(FileManager.f))));
    }
}
