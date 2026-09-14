package me.matl114.utils.commands.params.impl;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import me.matl114.api.Displayable;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.ArgumentType;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.commands.params.api.InputArgument;
import org.jetbrains.annotations.Nullable;

public class DispatchArgumentType<T> implements ArgumentType<T> {
    List<Pair<BiPredicate<CommandExecution, List<InputArgument<?>>>, ArgumentType<? extends T>>> b = new ArrayList<>();
    String a;

    @Nullable
    @Override
    public InputArgument<T> consume(CommandExecution sender, List<InputArgument<?>> args, ArgumentReader reader) {
        for (Pair var5 : this.b) {
            if (((BiPredicate) var5.getFirst()).test(sender, args)) {
                return ((ArgumentType) var5.getSecond()).consume(sender, args, reader);
            }
        }

        return new Displayable<>(this, reader);
    }

    public DispatchArgumentType<T> registerDispatcher(
            BiPredicate<CommandExecution, List<InputArgument<?>>> predicate, ArgumentType<? extends T> type) {
        this.b.add(Pair.of(predicate, type));
        return this;
    }

    public DispatchArgumentType<T> registerArgumentDispatcher(
            int argumentIndex, String string, ArgumentType<? extends T> type) {
        this.registerDispatcher(
                (execution, arguments) -> {
                    try {
                        int var4 = argumentIndex < 0 ? arguments.size() + argumentIndex : argumentIndex;
                        if (var4 >= 0 && var4 < arguments.size()) {
                            InputArgument var5 = arguments.get(var4);
                            return var5 != null && var5.k() && var5.z().equalsIgnoreCase(string);
                        } else {
                            return false;
                        }
                    } catch (Throwable var6) {
                        return false;
                    }
                },
                type);
        return this;
    }

    public DispatchArgumentType(String name) {
        this.a = name;
    }

    @Override
    public Stream<String> getTab(CommandExecution sender, List<InputArgument<?>> args) {
        for (Pair var4 : this.b) {
            if (((BiPredicate) var4.getFirst()).test(sender, args)) {
                return ((ArgumentType) var4.getSecond()).getTab(sender, args);
            }
        }

        return Stream.empty();
    }

    @Override
    public String getArgsName() {
        return this.a;
    }
}
