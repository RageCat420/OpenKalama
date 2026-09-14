package me.matl114.utils.commands.commandGroup;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import me.matl114.utils.commands.params.ArgumentInputStream;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.CommandExecution;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.commons.lang3.function.TriFunction;

public interface KalamaHelperHelperH {
    static KalamaHelperHelperH m(BiConsumer<CommandExecution, ArgumentInputStream> var) {
        return (var1, streamArgs, argsReader) -> {
            var.accept(var1, streamArgs);
            return true;
        };
    }

    static KalamaHelperHelperH h(BooleanSupplier task) {
        return (var1, streamArgs, argsReader) -> task.getAsBoolean();
    }

    default List<String> b(CommandExecution var1, ArgumentInputStream streamArgs, ArgumentReader argsReader) {
        return List.of();
    }

    boolean a(CommandExecution var1, ArgumentInputStream var2, ArgumentReader var3);

    static KalamaHelperHelperH f(TriFunction<PlayerEntity, ArgumentInputStream, ArgumentReader, Boolean> delegate) {
        return (var1, streamArgs, argsReader) ->
                var1.isPlayer() ? (Boolean) delegate.apply(var1.si(), streamArgs, argsReader) : false;
    }

    static KalamaHelperHelperH n(BiPredicate<CommandExecution, ArgumentInputStream> var) {
        return (var1, streamArgs, argsReader) -> var.test(var1, streamArgs);
    }

    static KalamaHelperHelperH g(Runnable task) {
        return (var1, streamArgs, argsReader) -> {
            task.run();
            return true;
        };
    }

    static KalamaHelperHelperH j(BiConsumer<PlayerEntity, ArgumentInputStream> var) {
        return (var1, streamArgs, argsReader) -> {
            if (var1.isPlayer()) {
                var.accept(var1.si(), streamArgs);
                return true;
            } else {
                return false;
            }
        };
    }

    static KalamaHelperHelperH i(Consumer<ArgumentInputStream> var) {
        return (var1, streamArgs, argsReader) -> {
            var.accept(streamArgs);
            return true;
        };
    }

    static KalamaHelperHelperH k(BiPredicate<PlayerEntity, ArgumentInputStream> var) {
        return (var1, streamArgs, argsReader) -> var1.isPlayer() ? var.test(var1.si(), streamArgs) : false;
    }

    static KalamaHelperHelperH l(Consumer<CommandExecution> var) {
        return (var1, streamArgs, argsReader) -> {
            var.accept(var1);
            return true;
        };
    }
}
