package me.matl114.utils.commands.params.api;

import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import me.matl114.utils.commands.params.ArgumentReader;

public interface ArgumentType<T> {
    @Nullable
    InputArgument<T> consume(CommandExecution var1, List<InputArgument<?>> var2, ArgumentReader var3);

    String getArgsName();

    Stream<String> getTab(CommandExecution var1, List<InputArgument<?>> var2);
}
