package me.matl114.hacks.modules.interact;

import com.mojang.datafixers.util.Pair;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;
import me.matl114.accessors.access.PlayerMoveC2SPacketAccess;
import me.matl114.accessors.hacks.KeyBindAccess;
import me.matl114.commands.MainCommand;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.RecipeBookToggle;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.hacks.KalamaHelperHelperUX;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hooks.ViaFabricPlusHooks;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperD;
import me.matl114.utils.commands.commandGroup.SubCommand;
import me.matl114.utils.commands.commandGroup.TaskSubCommand;
import me.matl114.utils.commands.commandGroup.TreeSubCommand;
import me.matl114.utils.commands.params.ArgumentInputStream;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.KalamaHelperHelperA;
import me.matl114.utils.commands.params.api.ArgumentType;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.commands.params.api.InputArgument;
import me.matl114.utils.commands.params.impl.DispatchArgumentType;
import me.matl114.utils.commands.params.impl.EntityArgumentType;
import me.matl114.utils.commands.params.impl.KalamaHelperHelperN;
import me.matl114.utils.commands.params.impl.RotationArgumentType;
import me.matl114.utils.commands.params.types.EntitySelector;
import me.matl114.utils.commands.params.types.ExecutePos;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Style;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class InteractManager extends BaseModule {
    private static final List<String> KS = List.of("mainhand", "offhand");
    private static final String Lc = "<id|all> 取消指定或全部循环交互请求";
    int holdUseTick;
    public final FlagRef swingHand;
    public final FlagRef offhand;
    public final FlagRef disableLowVersionSpeedResetWhenCommand;
    private static final String KW =
            "[hand|item_id] [once|inf|interval] [delay] <look|pos|entity> [pitch yaw|pos参数|@entity] 提交使用物品请求";
    public final FlagRef offhandHoldUse;
    private static final String Lb = "显示当前循环交互请求";
    Runnable Lt;
    public boolean Lq;
    public final FlagRef logAction;
    private static final String Le = "[useitem|attack|holduse] 显示交互指令帮助";
    public static InteractManager INSTANCE;
    private static final String KY = "[hand|item_id] [once|inf|interval] [delay] <@entity> 提交实体攻击请求";
    public final FlagRef keepTaskWhenExit;
    public final FlagRef ignorePotionLevel;
    private static final String La = "[hand|item_id] [once|inf|interval] [delay] [release_ticks] 提交持续使用物品请求";
    private static final List<String> KU = List.of("look", "pos", "entity");
    private final Map<String, RecipeBookToggle> Lo;
    private long requestCounter;
    boolean Lr;
    private static final String KX = "entity|block 提交攻击请求";
    private static final List<String> KT = List.of("once", "inf", "1", "4", "10", "20");
    public final ModulePath Lf = makePath(Configs.n, "interaction-tweaks.interact-manager");
    private static final List<String> KV = List.of("useitem", "attack", "holduse");
    public final FlagRef disableLowVersionSpeedReset;
    private static final String KZ = "[hand|item_id] [once|inf|interval] [delay] <pos参数> 提交挖掘请求";
    private static final String Ld = "取消全部循环交互请求";
    public final FlagRef logCommand;

    private void aaz(MainCommand mainCommand, String name) {
        TreeSubCommand var3 = mainCommand.bD().a(name).k();
        var3.subBuilder(SubCommand.bo())
                .u("list")
                .x("message.command." + name + ".list.help")
                .z(cmd -> cmd.executor(this::aaN))
                .r();
        var3.subBuilder(SubCommand.bo())
                .u("cancel")
                .x("message.command." + name + ".cancel.help")
                .A(KalamaHelperHelperA.a()
                        .B("id")
                        .d(() -> Stream.concat(Stream.of("all"), this.Lo.keySet().stream()))
                        .v())
                .z(cmd -> cmd.executor(this::aaO))
                .r();
        var3.subBuilder(SubCommand.bo())
                .u("clear")
                .x("message.command." + name + ".clear.help")
                .z(cmd -> cmd.executor(this::aaP))
                .r();
        var3.subBuilder(SubCommand.bo())
                .u("help")
                .x("message.command." + name + ".help.help")
                .A(new KalamaHelperHelperN<>(
                        "dispatch", KalamaHelperHelperA.a().B("dispatch").k(KV).v(), "all"))
                .z(cmd -> cmd.executor(this::aaQ))
                .r();
    }

    private void aay(MainCommand mainCommand) {
        TreeSubCommand var2 = mainCommand.bC().a("interact").k();
        var2.subBuilder(SubCommand.bo())
                .u("useitem")
                .x("message.command.interact.useitem.help")
                .A(this.aaA("hand"))
                .A(this.aaC("task"))
                .A(this.aaD("delay"))
                .A(this.aaF("target_type", KU))
                .A(this.targetDispatchArgument(3, "target", true))
                .z(cmd -> cmd.executor(this::aaJ))
                .r();
        var2.subBuilder(SubCommand.bp())
                .u("attack")
                .x("message.command.interact.attack.help")
                .z(tree -> tree.<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                        .u("entity")
                        .x("message.command.interact.attack.entity.help")
                        .A(this.aaA("hand"))
                        .A(this.aaC("task"))
                        .A(this.aaD("delay"))
                        .A(new EntityArgumentType("target"))
                        .z(cmd -> cmd.executor(this::aaK))
                        .r()
                        .subBuilder(SubCommand.bo())
                        .u("block")
                        .x("message.command.interact.attack.block.help")
                        .A(this.aaA("hand"))
                        .A(this.aaC("task"))
                        .A(this.aaD("delay"))
                        .A(new me.matl114.utils.commands.params.impl.KalamaHelperHelperA("target"))
                        .z(cmd -> cmd.executor(this::aaL))
                        .r())
                .r();
        var2.subBuilder(SubCommand.bo())
                .u("holduseitem")
                .x("message.command.interact.holduseitem.help")
                .A(this.aaA("hand"))
                .A(this.aaC("task"))
                .A(this.aaD("delay"))
                .A(this.aaE("release_ticks"))
                .z(cmd -> cmd.executor(this::onUseItem))
                .r();
        this.aaz(mainCommand, "interactman");
        this.aaz(mainCommand, "iman");
    }

    private static Integer abm(String raw) {
        try {
            int var1 = Integer.parseInt(raw);
            return var1 >= 0 ? var1 : null;
        } catch (Throwable var2) {
            return null;
        }
    }

    private boolean submitRequest(CommandExecution context, RecipeBookToggle request) {
        this.Lo.put(request.id(), request);
        if (this.logCommand.get()) {
            context.sm("&c[Interact] &f已提交请求 " + request.id());
        }

        return true;
    }

    private static Stream<String> itemStackTabs(String token) {
        String var1 = token == null ? "" : token;
        int var2 = var1.indexOf(91);
        if (var2 >= 0) {
            return potionMetaTabs(var1, var2);
        } else {
            Stream var3 = itemIdTabs();
            Item var4 = (Item) Registries.ITEM.get(Identifier.tryParse(token));
            return var4 != Items.AIR && hasPotionComponent(var4)
                    ? Stream.concat(var3, Stream.of(var1 + "[")).distinct()
                    : var3;
        }
    }

    private boolean aaI(ArgumentReader reader, CommandExecution context, String usage) {
        if (reader.hasNext()) {
            context.sm("&c[Interact] &e参数多余:" + reader.j());
            this.aaX(context, usage);
            return false;
        } else {
            return true;
        }
    }

    private static InteractSubHelperFX parseItemStackSelector(String raw) {
        if (raw != null && !raw.isBlank()) {
            String var1 = raw;
            String var2 = null;
            int var3 = raw.indexOf(91);
            if (var3 >= 0) {
                if (!raw.endsWith("]") || var3 == 0) {
                    return null;
                }

                var1 = raw.substring(0, var3);
                var2 = raw.substring(var3 + 1, raw.length() - 1);
                if (var2.isBlank() || var2.indexOf(91) >= 0 || var2.indexOf(93) >= 0) {
                    return null;
                }
            }

            Item var4 =
                    (Item) Registries.ITEM.getOrEmpty(Identifier.tryParse(var1)).orElse(null);
            if (var4 == null) {
                return null;
            } else {
                if (var2 != null) {
                    Identifier var5 = Identifier.tryParse(var2);
                    if (Registries.POTION.containsId(var5)) {
                        RegistryEntry var6 = Registries.POTION.getEntry((Potion) Registries.POTION.get(var5));
                        return new InteractSubHelperFX(var4, var6);
                    }
                }

                return new InteractSubHelperFX(var4, null);
            }
        } else {
            return null;
        }
    }

    private void sendHoldUseHelp(CommandExecution context) {
        context.sm(
                "&c[Interact] &fholduseitem 用法: !!holduseitem [hand|item_id] [once|inf|interval] [delay] [release_ticks]");
        context.sm("&c[Interact] &fhand 可填 mainhand / offhand，也可直接填物品 id");
        context.sm("&c[Interact] &finterval 可填 once(执行一次)、inf(一直执行)，或具体循环次数");
        context.sm("&c[Interact] &fdelay 填非负整数,代表执行的间隔.若为0,则一次性执行至多9次");
        context.sm("&c[Interact] &frelease_ticks 表示按住使用后多少 tick 松开，默认 20");
        context.sm("&c[Interact] &f注:该模式中,使用物品将直接把物品长时间换到主手或副手,直到使用完毕");
        context.sm("&c[Interact] &f使用示例&e(可以点击直接拷贝):");
        context.sn(ChatUtils.builder()
                .withColorString("1. 使用下界合金矛3次,一次30gt,40gt使用一次: &e" + MainCommand.getMainCommandPrefix()
                        + "holduseitem netherite_spear 3 40 30")
                .withGlobal(Style.EMPTY.withClickEvent(ChatUtils.getClickCopyText(
                        MainCommand.getMainCommandPrefix() + "holduseitem netherite_spear 3 40 30")))
                .end()
                .build());
        context.sn(ChatUtils.builder()
                .withColorString("2. 使用下界合金矛3次,一次3gt,40gt使用一次: &e" + MainCommand.getMainCommandPrefix()
                        + "holduseitem netherite_spear 3 40 3")
                .withGlobal(Style.EMPTY.withClickEvent(ChatUtils.getClickCopyText(
                        MainCommand.getMainCommandPrefix() + "holduseitem netherite_spear 3 40 32")))
                .end()
                .build());
        context.sn(ChatUtils.builder()
                .withColorString("3. 喝延时神龟药水3次,40gt喝一次: &e" + MainCommand.getMainCommandPrefix()
                        + "holduseitem potion[long_turtle_master] 3 40 32")
                .withGlobal(Style.EMPTY.withClickEvent(ChatUtils.getClickCopyText(
                        MainCommand.getMainCommandPrefix() + "holduseitem potion[long_turtle_master] 3 40 32")))
                .end()
                .build());
    }

    private KalamaHelperHelperN<String> aaE(String name) {
        return new KalamaHelperHelperN<>(
                name,
                KalamaHelperHelperA.a().B(name).f().v(),
                "20",
                (execution, inputArgument) -> abm(inputArgument.a()) != null);
    }

    private String aaZ(CommandExecution context, String raw) {
        if (this.Lo.containsKey(raw)) {
            return raw;
        } else {
            List var3 =
                    this.Lo.keySet().stream().filter(id -> id.startsWith(raw)).toList();
            if (var3.isEmpty()) {
                context.sm("&c[Interact] 找不到请求: " + raw);
                return null;
            } else if (var3.size() > 1) {
                context.sm("&c[Interact] 请求 ID 前缀不唯一: " + raw);
                return null;
            } else {
                return (String) var3.get(0);
            }
        }
    }

    public void aax(Event<PlayerMoveC2SPacket> eventPacket) {
        if ((this.disableLowVersionSpeedReset.get() && this.Lq
                        || this.disableLowVersionSpeedResetWhenCommand.get() && this.Lr)
                && ViaFabricPlusHooks.isSupportDupRot()
                && eventPacket.b instanceof Full var3
                && var3 instanceof PlayerMoveC2SPacketAccess var4
                && var4.getCause() == PlayerMoveC2SPacketAccess.Cause.LEGACY_SNAP) {
            eventPacket.cancel();
        }
    }

    public void y(Event<World> event) {
        this.aaV(null);
    }

    private static String aba(List<InputArgument<?>> args) {
        if (args.isEmpty()) {
            return "";
        } else {
            InputArgument var1 = (InputArgument) args.get(args.size() - 1);
            return var1 != null && var1.h() != null ? var1.h() : "";
        }
    }

    private ArgumentType<String> aaF(String name, List<String> allowedHeads) {
        return KalamaHelperHelperA.a().B(name).k(allowedHeads).v();
    }

    public void onInputEvent(Event<Void> event) {
        if (!checkNull()) {
            this.Lr = true;

            try {
                Iterator var2 = this.Lo.entrySet().iterator();

                while (var2.hasNext()) {
                    Entry var3 = (Entry) var2.next();
                    RecipeBookToggle var4 = (RecipeBookToggle) var3.getValue();
                    InteractSubHelperX var5 = var4.countdown();
                    if (var5.canRun()) {
                        int var6 = var5.countDown();

                        for (int var7 = 0; var7 < var6; var7++) {
                            var4.context().execute(this, mc.player);
                        }
                    } else {
                        var2.remove();
                    }
                }

                if (this.holdUseTick == 0 || this.holdUseTick > 0 && !mc.player.isUsingItem()) {
                    this.holdUseTick = -1;
                    KeyBindAccess.of(mc.options.useKey).resetKeyState();
                    if (this.Lt != null) {
                        this.Lt.run();
                        this.Lt = null;
                    }
                } else if (this.holdUseTick > 0) {
                    this.holdUseTick--;
                    mc.options.useKey.setPressed(true);
                }
            } finally {
                this.Lr = false;
            }

            this.Lq = true;
        }
    }

    public static boolean abr() {
        return INSTANCE != null && INSTANCE.swingHand.get();
    }

    public void tI(Event<Void> event) {
        this.aaV(null);
    }

    @Override
    public void onDisableModule() {
        super.onDisableModule();
        this.aaV(null);
    }

    private void aaX(CommandExecution context, String usage) {
        if (usage != null && !usage.isBlank()) {
            context.sm("&7用法: " + usage);
        }
    }

    private KalamaHelperHelperN<String> aaC(String name) {
        return new KalamaHelperHelperN<>(
                name,
                KalamaHelperHelperA.a().B(name).d(KT::stream).v(),
                "once",
                (execution, argument) -> abj(argument.a()));
    }

    private ArgumentType<String> aaB(String name) {
        return KalamaHelperHelperA.a().B(name).c((sender, args) -> abb(args)).v();
    }

    private static InteractSubHelperX abk(String raw, int delay) {
        if (raw != null && !raw.isBlank()) {
            String var2 = raw.toLowerCase(Locale.ROOT);

            return (InteractSubHelperX)
                    (switch (var2) {
                        case "once" -> new InteractSubHelperV(delay);
                        case "inf", "infinite", "forever" -> new InteractSubHelperL(delay);
                        default -> {
                            Integer var5 = abm(raw);
                            yield var5 == null ? null : new InteractSubHelperL(var5, delay);
                        }
                    });
        } else {
            return new InteractSubHelperV(delay);
        }
    }

    private static boolean abh(String raw) {
        return raw != null && !raw.isBlank() ? parseUseContextSelector(raw) != null : false;
    }

    private ArgumentType<Object> targetDispatchArgument(int index, String name, boolean allowTarget) {
        DispatchArgumentType<Object> var4 = new DispatchArgumentType<Object>(name)
                .registerArgumentDispatcher(index, "look", new RotationArgumentType(name + "_look"))
                .registerArgumentDispatcher(index, "pos", new KalamaHelperHelperUX(name + "_pos"));
        if (allowTarget) {
            var4.registerArgumentDispatcher(index, "entity", new EntityArgumentType(name + "_entity"));
        }

        return var4;
    }

    private void sendUseItemHelp(CommandExecution context) {
        context.sm("&c[Interact] &fattack 用法分两支: entity / block");
        context.sm("&c[Interact] &fentity: !!attack entity [hand|item_id] [once|inf|interval] [delay] <@entity>");
        context.sm("&c[Interact] &fblock: !!attack block [hand|item_id] [once|inf|interval] [delay] <pos参数>");
        context.sm("&c[Interact] &fhand 可填 mainhand / offhand，也可直接填物品 id");
        context.sm("&c[Interact] &finterval 可填 once(执行一次)、inf(一直执行)，或具体循环次数");
        context.sm("&c[Interact] &fdelay 填非负整数,代表执行的间隔.若为0,则一次性执行至多9次");
        context.sm("&c[Interact] &f使用示例&e(可以点击直接拷贝):");
        context.sn(ChatUtils.builder()
                .withColorString("1. 攻击距离玩家超过0.01的最近实体一次: &e" + MainCommand.getMainCommandPrefix()
                        + "attack entity mainhand once 1 @e[distance=0.1..,limit=1,sort=nearest]")
                .withGlobal(Style.EMPTY.withClickEvent(ChatUtils.getClickCopyText(MainCommand.getMainCommandPrefix()
                        + "attack entity mainhand once 1 @e[distance=0.1..,limit=1,sort=nearest]")))
                .end()
                .build());
        context.sn(ChatUtils.builder()
                .withColorString("2. 攻击距离玩家超过0.01的最近实体16次,间隔10gt: &e" + MainCommand.getMainCommandPrefix()
                        + "attack entity mainhand 16 10 @e[distance=1..,limit=1,sort=nearest]")
                .withGlobal(Style.EMPTY.withClickEvent(ChatUtils.getClickCopyText(MainCommand.getMainCommandPrefix()
                        + "attack entity mainhand 16 10 @e[distance=1..,limit=1,sort=nearest]")))
                .end()
                .build());
        context.sn(ChatUtils.builder()
                .withColorString(
                        "3. 攻击脚下方块30次,1gt攻击一次: &e" + MainCommand.getMainCommandPrefix() + "attack block 30 1 ~ ~-1 ~")
                .withGlobal(Style.EMPTY.withClickEvent(
                        ChatUtils.getClickCopyText(MainCommand.getMainCommandPrefix() + "attack block 30 1 ~ ~-1 ~")))
                .end()
                .build());
        context.sn(ChatUtils.builder()
                .withColorString("2. 攻击脚下方块2次,100gt攻击一次(?): &e" + MainCommand.getMainCommandPrefix()
                        + "attack block 2 200 ~ ~-1 ~")
                .withGlobal(Style.EMPTY.withClickEvent(
                        ChatUtils.getClickCopyText(MainCommand.getMainCommandPrefix() + "attack block 2 200 ~ ~-1 ~")))
                .end()
                .build());
        context.sm("&c[Interact] &f指令建议配合BindCmd模块一起使用,通过快捷键自动发送");
    }

    public void hO(Event<Void> event) {
        this.Lq = false;
    }

    private static Stream<String> potionIdTabs() {
        return Registries.POTION.stream()
                .<Identifier>map(Registries.POTION::getId)
                .filter(Objects::nonNull)
                .map(Identifier::getPath);
    }

    public static KalamaHelperHelperK<ItemStack> currentHandContext(Hand hand) {
        if (mc.player == null) {
            return null;
        } else {
            Hand var1 = normalizedHand(hand);
            int var2 = var1 == Hand.OFF_HAND ? 40 : InventoryUtils.getSelectedSlot();
            return new KalamaHelperHelperK<>(var2, mc.player.getStackInHand(var1));
        }
    }

    public static Hand preferredHand() {
        return abq() ? Hand.OFF_HAND : Hand.MAIN_HAND;
    }

    private static boolean hasPotionComponent(Item itemId) {
        return itemId != null && itemId.getComponents().contains(DataComponentTypes.POTION_CONTENTS);
    }

    private KalamaHelperHelperN<String> aaD(String name) {
        return new KalamaHelperHelperN<>(
                name,
                KalamaHelperHelperA.a().B(name).f().v(),
                "1",
                (execution, inputArgument) -> abm(inputArgument.a()) != null);
    }

    public static boolean abq() {
        return INSTANCE != null && INSTANCE.offhand.get();
    }

    private static Stream<String> abb(List<InputArgument<?>> args) {
        Stream var1 = KS.stream();
        return Stream.concat(var1, itemStackTabs(aba(args))).distinct();
    }

    private boolean canSubmit(CommandExecution context) {
        if (mc.player != null && mc.world != null) {
            return true;
        } else {
            context.sm("&c[Interact] 当前没有可用玩家或世界");
            return false;
        }
    }

    public static Hand normalizedHand(Hand hand) {
        return hand == null ? Hand.MAIN_HAND : hand;
    }

    private static boolean abj(String raw) {
        return raw != null && !raw.isBlank() ? abk(raw, 0) != null : false;
    }

    private void aaV(CommandExecution reporter) {
        this.Lt = null;
        this.holdUseTick = -1;
        if (!this.Lo.isEmpty() && (reporter != null || !this.keepTaskWhenExit.get())) {
            List var2 = List.copyOf(this.Lo.keySet());
            this.Lo.clear();
            if (reporter != null) {
                reporter.sm("&c[Interact] &f已清理 " + var2.size() + " 个循环请求");
            }
        }
    }

    private boolean onUseItem(CommandExecution context, ArgumentInputStream streamArgs, ArgumentReader reader) {
        if (!this.canSubmit(context)) {
            return true;
        } else {
            Pair var4 = this.parseHandTaskContext(streamArgs);
            int var5 = streamArgs.nextInt();
            return !this.aaI(
                            reader,
                            context,
                            "[hand|item_id] [once|inf|interval] [delay] <look|pos|entity> [pitch yaw|pos参数|@entity] 提交使用物品请求")
                    ? true
                    : this.submitRequest(
                            context,
                            new RecipeBookToggle(
                                    this.aaY("holduseitem"),
                                    (InteractSubHelperX) var4.getSecond(),
                                    new InteractSubHelperR(this, (InteractSubHelperC) var4.getFirst(), var5)));
        }
    }

    private String aaY(String type) {
        String var2 = type != null && !type.isBlank() ? type : "task";

        String var3;
        do {
            var3 = var2 + "_" + ++this.requestCounter;
        } while (this.Lo.containsKey(var3));

        return var3;
    }

    public InteractManager() {
        super("InteractManager");
        this.offhand = this.flagBuilder(this.Lf.add("offhand")).build();
        this.swingHand = this.flagBuilder(this.Lf.add("swing-hand")).build();
        this.logCommand = this.flagBuilder(this.Lf.add("log-command")).build();
        this.logAction = this.flagBuilder(this.Lf.add("log-action")).build();
        this.offhandHoldUse = this.flagBuilder(this.Lf.add("offhand-hold-use")).build();
        this.keepTaskWhenExit =
                this.flagBuilder(this.Lf.add("keep-task-when-exit")).build();
        this.ignorePotionLevel =
                this.flagBuilder(this.Lf.add("ignore-potion-level")).build();
        this.disableLowVersionSpeedReset =
                this.flagBuilder(this.Lf.add("disable-low-version-speed-reset")).build();
        this.disableLowVersionSpeedResetWhenCommand = this.flagBuilder(
                        this.Lf.add("disable-low-version-speed-reset-when-command"))
                .build();
        this.Lo = new LinkedHashMap<>();
        this.requestCounter = 0L;
        this.Lq = false;
        this.Lr = false;
        this.holdUseTick = -1;
        this.Lt = null;
        INSTANCE = this;
    }

    @Override
    public void addCustomWidgets(Consumer<DrawableWidget> acceptor, int dx, int dy, int dblank) {
        super.addCustomWidgets(acceptor, dx, dy, dblank);
        acceptor.accept(this.createTitleLabel("widget.interact-manager.command", 0, dblank, dx, dy));
        acceptor.accept(this.createTitleLabel("widget.attack.attack.use-argument", 0, dblank, dx, dy));
    }

    private boolean aaJ(CommandExecution context, ArgumentInputStream streamArgs, ArgumentReader reader) {
        if (!this.canSubmit(context)) {
            return true;
        } else {
            Pair var4 = this.parseHandTaskContext(streamArgs);
            String var5 = streamArgs.o();
            if (!KU.contains(var5)) {
                context.sm("&c[Interact] &e不存在的目标类型: " + var5);
                this.aaX(
                        context,
                        "[hand|item_id] [once|inf|interval] [delay] <look|pos|entity> [pitch yaw|pos参数|@entity] 提交使用物品请求");
                return true;
            } else {
                InputArgument var6 = streamArgs.e();
                InteractSubHelperP var7 = InteractSubHelperP.b(var6);
                if (var7 == null) {
                    context.sm("&c[Interact] &e缺少或无效使用目标");
                    this.aaX(
                            context,
                            "[hand|item_id] [once|inf|interval] [delay] <look|pos|entity> [pitch yaw|pos参数|@entity] 提交使用物品请求");
                    return true;
                } else {
                    return !this.aaI(
                                    reader,
                                    context,
                                    "[hand|item_id] [once|inf|interval] [delay] <look|pos|entity> [pitch yaw|pos参数|@entity] 提交使用物品请求")
                            ? true
                            : this.submitRequest(
                                    context,
                                    new RecipeBookToggle(
                                            this.aaY("useitem"),
                                            (InteractSubHelperX) var4.getSecond(),
                                            new InteractSubHelperAX((InteractSubHelperC) var4.getFirst(), var7)));
                }
            }
        }
    }

    private boolean aaQ(CommandExecution context, ArgumentInputStream streamArgs, ArgumentReader reader) {
        String var4 = streamArgs.o().toLowerCase(Locale.ROOT);
        if (!this.aaI(reader, context, "[useitem|attack|holduse] 显示交互指令帮助")) {
            return true;
        } else {
            switch (var4) {
                case "useitem":
                    this.aaR(context);
                    break;
                case "attack":
                    this.sendUseItemHelp(context);
                    break;
                case "holduse":
                    this.sendHoldUseHelp(context);
                    break;
                default:
                    context.sm("&c[Interact] &e不存在的帮助: " + var4);
                    this.aaX(context, "[useitem|attack|holduse] 显示交互指令帮助");
            }

            return true;
        }
    }

    private KalamaHelperHelperN<String> aaA(String name) {
        return new KalamaHelperHelperN<>(name, this.aaB(name), "mainhand", (execution, argument) -> abh(argument.a()));
    }

    private static Stream<String> itemIdTabs() {
        return Registries.ITEM.stream().map(Registries.ITEM::getId).map(Identifier::getPath);
    }

    private Pair<InteractSubHelperC, InteractSubHelperX> parseHandTaskContext(ArgumentInputStream streamArgs) {
        String var2 = streamArgs.o();
        String var3 = streamArgs.o();
        int var4 = streamArgs.nextInt();
        InteractSubHelperC var5 = parseUseContextSelector(var2);
        InteractSubHelperX var6 = abk(var3, var4);
        return Pair.of(var5, var6);
    }

    private boolean aaK(CommandExecution context, ArgumentInputStream streamArgs, ArgumentReader reader) {
        if (!this.canSubmit(context)) {
            return true;
        } else {
            Pair var4 = this.parseHandTaskContext(streamArgs);
            InputArgument var5 = streamArgs.e();
            EntitySelector var6 = (EntitySelector) var5.g();
            if (var6 == null) {
                context.sm("&c[Interact] &e缺少或无效使用目标");
                this.aaX(
                        context,
                        "[hand|item_id] [once|inf|interval] [delay] <look|pos|entity> [pitch yaw|pos参数|@entity] 提交使用物品请求");
                return true;
            } else {
                return !this.aaI(
                                reader,
                                context,
                                "[hand|item_id] [once|inf|interval] [delay] <look|pos|entity> [pitch yaw|pos参数|@entity] 提交使用物品请求")
                        ? true
                        : this.submitRequest(
                                context,
                                new RecipeBookToggle(
                                        this.aaY("attack"),
                                        (InteractSubHelperX) var4.getSecond(),
                                        new InteractSubHelperOX((InteractSubHelperC) var4.getFirst(), var6)));
            }
        }
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerCommandBootstrap(this::aay);
        this.registerListener(Listener.bd(), this::onInputEvent, 2147483646);
        this.registerListener(Listener.O(), this::tI);
        this.registerListener(Listener.N(), this::y);
        this.registerListener(Listener.be(), this::hO, Integer.MIN_VALUE);
        this.registerListener(Listener.ap().getChannel(PlayerMoveC2SPacket.class), this::aax);
    }

    private boolean aaL(CommandExecution context, ArgumentInputStream streamArgs, ArgumentReader reader) {
        if (!this.canSubmit(context)) {
            return true;
        } else {
            Pair var4 = this.parseHandTaskContext(streamArgs);
            InputArgument var5 = streamArgs.e();
            ExecutePos var6 = (ExecutePos) var5.g();
            if (var6 == null) {
                context.sm("&c[Interact] &e缺少或无效使用目标");
                this.aaX(
                        context,
                        "[hand|item_id] [once|inf|interval] [delay] <look|pos|entity> [pitch yaw|pos参数|@entity] 提交使用物品请求");
                return true;
            } else {
                return !this.aaI(
                                reader,
                                context,
                                "[hand|item_id] [once|inf|interval] [delay] <look|pos|entity> [pitch yaw|pos参数|@entity] 提交使用物品请求")
                        ? true
                        : this.submitRequest(
                                context,
                                new RecipeBookToggle(
                                        this.aaY("mine"),
                                        (InteractSubHelperX) var4.getSecond(),
                                        new InteractSubHelperJ((InteractSubHelperC) var4.getFirst(), var6)));
            }
        }
    }

    private boolean aaP(CommandExecution context, ArgumentInputStream streamArgs, ArgumentReader reader) {
        int var4 = this.Lo.size();
        this.aaV(context);
        context.sm("&c[Interact] &f已清空 " + var4 + " 个循环请求");
        return true;
    }

    private void aaR(CommandExecution context) {
        context.sm(
                "&c[Interact] &fuseitem 用法: !!useitem [hand|item_id] [once|inf|interval] [delay] <look|pos|entity> [pitch yaw|pos参数|@entity]");
        context.sm("&c[Interact] &fhand 可填 mainhand / offhand，也可直接填物品 id");
        context.sm("&c[Interact] &finterval 可填 once(执行一次)、inf(一直执行)，或具体循环次数");
        context.sm("&c[Interact] &fdelay 填非负整数,代表执行的间隔.若为0,则一次性执行至多9次");
        context.sm("&c[Interact] &ftarget 可填 look(转头视角)、pos(看向的位置)、entity(目标实体)");
        context.sm("&c[Interact] &f使用示例&e(可以点击直接拷贝):");
        context.sn(ChatUtils.builder()
                .withColorString("1. 向上90度使用二级神龟喷溅药水: &e" + MainCommand.getMainCommandPrefix()
                        + "useitem splash_potion[strong_turtle_master] look -90 ~")
                .withGlobal(Style.EMPTY.withClickEvent(ChatUtils.getClickCopyText(
                        MainCommand.getMainCommandPrefix() + "useitem splash_potion[strong_turtle_master] look -90 ~")))
                .end()
                .build());
        context.sn(ChatUtils.builder()
                .withColorString("2. 向上90度使用延时神龟喷溅药水: &e" + MainCommand.getMainCommandPrefix()
                        + "useitem splash_potion[long_turtle_master] look -90 ~")
                .withGlobal(Style.EMPTY.withClickEvent(ChatUtils.getClickCopyText(
                        MainCommand.getMainCommandPrefix() + "useitem splash_potion[long_turtle_master] look -90 ~")))
                .end()
                .build());
        context.sn(ChatUtils.builder()
                .withColorString("3. 向下90度使用9个经验瓶,一次使用行完: &e" + MainCommand.getMainCommandPrefix()
                        + "useitem experience_bottle 9 0 look 90 ~")
                .withGlobal(Style.EMPTY.withClickEvent(ChatUtils.getClickCopyText(
                        MainCommand.getMainCommandPrefix() + "useitem experience_bottle 9 0 look 90 ~")))
                .end()
                .build());
        context.sn(ChatUtils.builder()
                .withColorString("4. 向下90度使用9个经验瓶,一gt使用一次: &e" + MainCommand.getMainCommandPrefix()
                        + "useitem experience_bottle 9 1 look 90 ~")
                .withGlobal(Style.EMPTY.withClickEvent(ChatUtils.getClickCopyText(
                        MainCommand.getMainCommandPrefix() + "useitem experience_bottle 9 1 look 90 ~")))
                .end()
                .build());
        context.sm("&c[Interact] &f指令建议配合BindCmd模块一起使用,通过快捷键自动发送");
    }

    private boolean aaO(CommandExecution context, ArgumentInputStream streamArgs, ArgumentReader reader) {
        String var4 = streamArgs.o();
        if ("all".equalsIgnoreCase(var4)) {
            int var7 = this.Lo.size();
            this.aaV(context);
            context.sm("&c[Interact] &f已取消全部 " + var7 + " 个循环请求");
            return true;
        } else {
            String var5 = this.aaZ(context, var4);
            if (var5 == null) {
                return true;
            } else {
                RecipeBookToggle var6 = this.Lo.remove(var5);
                if (var6 == null) {
                    context.sm("&c[Interact] &e找不到请求: " + var4);
                    return true;
                } else {
                    context.sm("&c[Interact] &f已取消请求 " + var6.id());
                    return true;
                }
            }
        }
    }

    private boolean aaN(CommandExecution context, ArgumentInputStream streamArgs, ArgumentReader reader) {
        if (this.Lo.isEmpty()) {
            context.sm("&c[Interact] &e当前没有循环请求");
            return true;
        } else {
            context.sm("&c[Interact] &f当前循环请求:");
            this.Lo.values().forEach(request -> context.sm("&7- " + request.id()));
            return true;
        }
    }

    private static InteractSubHelperC parseUseContextSelector(String raw) {
        if (raw != null && !raw.isBlank()) {
            return switch (raw) {
                case "mainhand" -> InteractSubHelperC.Ux(Hand.MAIN_HAND);
                case "offhand" -> InteractSubHelperC.Ux(Hand.OFF_HAND);
                default -> InteractSubHelperC.Uy(parseItemStackSelector(raw));
            };
        } else {
            return null;
        }
    }

    private static Stream<String> potionMetaTabs(String raw, int metaStart) {
        if (raw.indexOf(91, metaStart + 1) < 0 && raw.indexOf(93, metaStart + 1) < 0) {
            String var2 = raw.substring(0, metaStart);
            Item var3 = (Item) Registries.ITEM.get(Identifier.tryParse(var2));
            if (var3 != Items.AIR && hasPotionComponent(var3)) {
                String var4 = raw.substring(0, metaStart + 1);
                return potionIdTabs().map(id -> var4 + id + "]");
            } else {
                return Stream.empty();
            }
        } else {
            return Stream.empty();
        }
    }
}
