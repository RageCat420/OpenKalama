package me.matl114.utils;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.tree.CommandNode;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import me.matl114.events.annotations.Modifiable;
import net.minecraft.client.MinecraftClient;

@Modifiable
public class ClientUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static CompletableFuture<List<String>> getServerCommandTabResult(String command) {
        StringReader var1 = new StringReader(command);
        var1.skip();
        CommandDispatcher<net.minecraft.command.CommandSource> var2 =
                mc.getNetworkHandler().getCommandDispatcher();
        ParseResults<net.minecraft.command.CommandSource> var3 =
                var2.parse(var1, mc.getNetworkHandler().getCommandSource());
        return mc.getNetworkHandler()
                .getCommandDispatcher()
                .getCompletionSuggestions(var3)
                .thenApply(suggestions -> suggestions.getList().stream()
                        .<String>map(Suggestion::getText)
                        .sorted()
                        .toList());
    }

    public static boolean isPlayerOnline() {
        return mc.player != null && !mc.disconnecting;
    }

    public static boolean isNetworkConnecting() {
        return mc.getServer() != null;
    }

    public static List<String> getServerCommands() {
        return mc.getNetworkHandler().getCommandDispatcher().getRoot().getChildren().stream()
                .<String>map(CommandNode::getName)
                .toList();
    }

    public static CompletableFuture<List<String>> c() {
        String var0 = "/version ";
        return getServerCommandTabResult(var0);
    }
}
