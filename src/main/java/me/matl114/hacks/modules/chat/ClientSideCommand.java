package me.matl114.hacks.modules.chat;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.InvTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.utils.Debug;
import me.matl114.utils.ItemStackUtils;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;

public class ClientSideCommand extends BaseModule {
    private static final Predicate<CommandSource> vG = val -> true;
    private static final Command<CommandSource> vH = val -> 1;
    public final FlagRef clientSideGive;
    public List<String> vI;
    public final ModulePath vE = makePath(Configs.h, "client-side-command");
    private static ResultConsumer<ClientCommandSource> vJ = (c, s, r) -> {};
    public final FlagRef ae =
            this.flagBuilder(this.vE.add("client-side-command-override")).build();

    private boolean handleClientSideGiveCommand(
            Map<String, ParsedArgument<ClientCommandSource, ?>> argsMap, String command) throws CommandSyntaxException {
        if (this.clientSideGive.get()) {
            if (mc.interactionManager.getCurrentGameMode().isCreative()) {
                Debug.b(Text.literal("尝试在客户端执行give指令").formatted(Formatting.GREEN));
                ParsedArgument var3 = (ParsedArgument) argsMap.get("targets");
                EntitySelector var4 = (EntitySelector) var3.getResult();
                StringRange var5 = var3.getRange();
                if (!var4.isSenderOnly()
                        && !Objects.equals(
                                mc.player.getNameForScoreboard(), command.substring(var5.getStart(), var5.getEnd()))) {
                    Debug.b(Text.literal("你选中了其他生物,指令转向服务端执行!").formatted(Formatting.YELLOW));
                    return false;
                } else {
                    ItemStackArgument var6 = (ItemStackArgument) ((ParsedArgument) argsMap.get("item")).getResult();
                    int var7 = argsMap.containsKey("count")
                            ? (Integer) ((ParsedArgument) argsMap.get("count")).getResult()
                            : 1;
                    ItemStack var8 = var6.createStack(var7, false);
                    InvTasks.creativeGive(var8, var7);
                    Debug.b(Text.literal("命令执行成功！").formatted(Formatting.GREEN));
                    return true;
                }
            } else {
                Debug.b(Text.literal("你启用了客户端指令的功能,但是你并不是创造模式!").formatted(Formatting.YELLOW));
                return false;
            }
        } else {
            Debug.b(Text.literal("尝试在客户端执行give指令,但是你没有启用客户端give指令").formatted(Formatting.RED));
            return false;
        }
    }

    private static Text getErrorMessage(CommandSyntaxException e) {
        Text var1 = Texts.toText(e.getRawMessage());
        String var2 = e.getContext();
        return (Text)
                (var2 != null
                        ? Text.translatable("command.context.parse_error", new Object[] {var1, e.getCursor(), var2})
                        : var1);
    }

    private <T extends CommandSource> void onClientCommandReload(Event<CommandTreeS2CPacket> reload) {
        CommandDispatcher var2 = mc.getNetworkHandler().getCommandDispatcher();
        RootCommandNode var3 = var2.getRoot();
        if (var3 != null && this.ae.get()) {
            this.addOurCommandNodesInRoot(var3);
        }
    }

    @Override
    public void registerAll() {
        super.registerAll();
        java.util.function.Consumer<Event<CommandTreeS2CPacket>> reloadH = this::onClientCommandReload;
        this.registerListener(Listener.ar().getChannel(CommandTreeS2CPacket.class), reloadH);
        this.registerListener(Listener.Z(), this::GL, 1);
    }

    public void GL(Event<String> commandEvent) {
        String var2 = (String) commandEvent.e();
        if (this.ae.get() && var2.startsWith("/")) {
            var2 = var2.substring(1);
            if (this.vI.stream().anyMatch(var2::startsWith) && this.dispatchVanillaCommand(var2)) {
                commandEvent.cancel();
                return;
            }
        }
    }

    public ClientSideCommand() {
        super("ClientSideCommand");
        this.clientSideGive = this.flagBuilder(this.vE.add("client-side-give")).build();
        this.vI = List.of("give", "minecraft:give");
        this.bindFlag(this.ae);
    }

    private <T extends CommandSource> void addOurCommandNodesInRoot(RootCommandNode<T> node) {
        if (this.clientSideGive.get()) {
            CommandNode var2 = node.getChild("minecraft:give");
            LiteralCommandNode var3;
            if (var2 == null) {
                var3 = new LiteralCommandNode("minecraft:give", null, vG, null, null, false);
                node.addChild(var3);
            } else {
                var3 = (LiteralCommandNode) var2;
            }

            if (node.getChild("give") == null) {
                LiteralCommandNode var4 = new LiteralCommandNode("give", null, vG, var3, null, false);
                node.addChild(var4);
            }

            if (var2 == null) {
                CommandRegistryAccess var8 =
                        CommandRegistryAccess.of(ItemStackUtils.l(), FeatureFlags.DEFAULT_ENABLED_FEATURES);
                ArgumentCommandNode var5 = new ArgumentCommandNode(
                        "targets", EntityArgumentType.players(), null, vG, null, null, false, null);
                var3.addChild(var5);
                ArgumentCommandNode var6 = new ArgumentCommandNode(
                        "item", ItemStackArgumentType.itemStack(var8), vH, vG, null, null, false, null);
                var5.addChild(var6);
                ArgumentCommandNode var7 = new ArgumentCommandNode(
                        "count", IntegerArgumentType.integer(1), vH, vG, null, null, false, null);
                var6.addChild(var7);
            }
        }
    }

    private boolean dispatchVanillaCommand(String command) {
        if (mc.player == null) {
            return false;
        } else {
            mc.player.setClientPermissionLevel(4);

            try {
                ParseResults var2 = mc.getNetworkHandler()
                        .getCommandDispatcher()
                        .parse(command, mc.getNetworkHandler().getCommandSource());
                if (var2.getReader().canRead()) {
                    if (var2.getExceptions().size() == 1) {
                        throw (CommandSyntaxException)
                                var2.getExceptions().values().iterator().next();
                    }

                    if (var2.getContext().getRange().isEmpty()) {
                        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS
                                .dispatcherUnknownCommand()
                                .createWithContext(var2.getReader());
                    }

                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS
                            .dispatcherUnknownArgument()
                            .createWithContext(var2.getReader());
                }

                String var3 = var2.getReader().getString();
                CommandContextBuilder var4 = var2.getContext();
                ArrayList var5 = new ArrayList();
                CommandContextBuilder var6 = var4;

                while (true) {
                    CommandContextBuilder var7 = var6.getChild();
                    if (var7 == null) {
                        if (var6.getCommand() == null) {
                            vJ.onCommandComplete(var4.build(var3), false, 0);
                            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS
                                    .dispatcherUnknownCommand()
                                    .createWithContext(var2.getReader());
                        }

                        Map var10 = var6.getArguments();
                        if (var3.startsWith("give") || var3.startsWith("minecraft:give")) {
                            return this.handleClientSideGiveCommand(var10, command);
                        }
                        break;
                    }

                    var5.add(var6);
                    var6 = var7;
                }
            } catch (CommandSyntaxException var8) {
                Debug.b(getErrorMessage(var8));
            } catch (Throwable var9) {
                Debug.chat(Text.literal("Internal Error!").formatted(Formatting.RED), var9);
            }

            return false;
        }
    }
}
