package me.matl114.utils.commands.interruption;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.CommandExecution;

public interface InterruptionHandler {
    void handleValueOutOfRange(
            CommandExecution var1,
            @Nullable ArgumentReader var2,
            @Nullable String var3,
            KalamaHelperHelperH var4,
            String var5,
            @Nonnull String var6);

    void bz(CommandExecution var1, String var2, ArgumentReader var3);

    void bB(CommandExecution var1, ArgumentReader var2);

    void handleTypeError(
            CommandExecution var1,
            @Nullable ArgumentReader var2,
            @Nullable String var3,
            KalamaHelperHelperH var4,
            String var5);

    void handleExecutorInvalid(CommandExecution var1, boolean var2);

    void bA(CommandExecution var1, String var2);

    void bv(CommandExecution var1, @Nullable ArgumentReader var2, @Nonnull String var3);

    void bw(CommandExecution var1, @Nullable ArgumentReader var2, @Nonnull String var3);
}
