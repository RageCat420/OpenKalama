package me.matl114.commands;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.function.Supplier;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.utils.Debug;
import me.matl114.utils.commands.commandGroup.AbstractMainCommand;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperK;
import me.matl114.utils.commands.commandGroup.SubCommand;
import net.minecraft.client.MinecraftClient;

public class MainCommand extends AbstractMainCommand {
    private static MainCommand m;
    public static final MinecraftClient k = MinecraftClient.getInstance();
    private static final List<KalamaHelperHelperB> n = new ArrayList<>();
    public static String l = "!!";

    public void aM(AbstractMainCommand main) {
        this.ay(main);
    }

    public static List<String> callTabCompletion(String[] command) {
        if (k.player != null) {
            List var1 = m.bY(k.player, "", command);
            if (var1 != null && !var1.isEmpty()) {
                return var1;
            }
        }

        return List.of();
    }

    public static void registerCommandBootstrap(KalamaHelperHelperB bootStrap) {
        n.add(bootStrap);
        if (m != null) {
            bootStrap.onCommandReload(m);
        }
    }

    public static void aE() {
        reloadCommand();
    }

    public static void unregisterCommandBootstrap(Predicate<KalamaHelperHelperB> bootstrap) {
        n.removeIf(bootstrap);
    }

    public static void reloadCommand() {
        new MainCommand();
        if (k.player != null) {
            Debug.b("Command Successfully reloaded");
        }
    }

    static {
        Listener.Z().k(MainCommand::aN);
    }

    public static void dispatchClientCommand(String command) {
        if (k.player != null) {
            String[] var1 = command.split(" ");
            if (var1.length == 0) {
                return;
            }

            try {
                if (m.bX(k.player, "", var1)) {
                    return;
                }
            } catch (Throwable var3) {
                Debug.chat("Unexpected Error occurred :", var3.getMessage());
                Debug.f(var3);
            }
        }
    }

    public void aL(String dispatchName, AbstractMainCommand main) {
        this.ay(new KalamaHelperHelperK(dispatchName, main));
    }

    public MainCommand() {
        this.ay(new KalamaHelperHelperK(
                "help",
                SubCommand.bo()
                        .a("help")
                        .g(s -> s.executor(new KalamaHelperHelperC(this)))
                        .k()));
        m = this;
        if (n != null) {
            n.forEach(s -> s.onCommandReload(this));
        }
    }

    public static CompletableFuture<Suggestions> dispatchTabComplete(String command, int cursorAt, boolean withPrefix) {
        if (cursorAt < 0) {
            return null;
        } else {
            String var3 = command.substring(0, cursorAt);
            int var4 = -1;
            int var5 = l.length() + (withPrefix ? 1 : 0);
            ArrayList<String> var6 = new ArrayList();

            while (true) {
                int var7 = var3.indexOf(" ", var4 + 1);
                if (var7 == -1) {
                    var6.add(var3.substring(var4 + 1));
                    StringRange var8 = new StringRange(var5 + var4 + 1, var5 + var3.length());
                    List<String> var11 = callTabCompletion(var6.toArray(new String[0]));
                    List var9 = var11.stream().map(i -> new Suggestion(var8, i)).toList();
                    Suggestions var10 = new Suggestions(var8, var9);
                    return CompletableFuture.completedFuture(var10);
                }

                var6.add(var3.substring(var4 + 1, var7));
                var4 = var7;
            }
        }
    }

    public static void aH(String name, Supplier<AbstractMainCommand> commandSupplier) {
        registerCommandBootstrap(main -> main.aL(name, (AbstractMainCommand) commandSupplier.get()));
    }

    public static CompletableFuture<Suggestions> tabCompleteClientCommand(String command, int cursorAt) {
        if (command.startsWith(l)) {
            return dispatchTabComplete(command.substring(l.length()), cursorAt - l.length(), false);
        } else {
            return command.startsWith("/" + l)
                    ? dispatchTabComplete(command.substring(l.length() + 1), cursorAt - l.length() - 1, true)
                    : null;
        }
    }

    public static boolean isClientCommand(String command) {
        return command.startsWith(l) || command.startsWith("/" + l);
    }

    public static void aI(Supplier<AbstractMainCommand> commandSupplier) {
        registerCommandBootstrap(main -> main.aM((AbstractMainCommand) commandSupplier.get()));
    }

    public static String getMainCommandPrefix() {
        return "/" + l;
    }

    public static void aN(Event<String> commandEvent) {
        String var1 = (String) commandEvent.e();
        if (var1.startsWith(l)) {
            dispatchClientCommand(var1.substring(l.length()));
            commandEvent.cancel();
        } else if (var1.startsWith("/" + l)) {
            dispatchClientCommand(var1.substring(l.length() + 1));
            commandEvent.cancel();
        }
    }

    public static void dispatchCommand(String[] args) {
        if (k.player != null) {
            if (args.length == 0) {
                return;
            }

            try {
                if (m.bX(k.player, "", args)) {
                    return;
                }
            } catch (Throwable var2) {
                Debug.chat("Unexpected Error occurred :", var2.getMessage());
                Debug.f(var2);
            }
        }
    }
}
