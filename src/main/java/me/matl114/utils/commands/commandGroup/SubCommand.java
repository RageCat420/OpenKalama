package me.matl114.utils.commands.commandGroup;

import me.matl114.utils.commands.params.KalamaHelperHelperA;
import org.apache.commons.lang3.function.TriFunction;

public interface SubCommand extends CustomTabExecutor {
    default SubCommand br(KalamaHelperHelperD caller) {
        caller.registerSub(this);
        return this;
    }

    static KalamaHelperHelperE<TaskSubCommand> bo() {
        return new KalamaHelperHelperE<>(TaskSubCommand::new);
    }

    static KalamaHelperHelperE<TreeSubCommand> bp() {
        return new KalamaHelperHelperE<>((a, b, c) -> new TreeSubCommand(a, c));
    }

    static KalamaHelperHelperE<SubCommand> bn() {
        return new KalamaHelperHelperE<>(TaskSubCommand::new);
    }

    void setPermission(String var1);

    static <W extends SubCommand> KalamaHelperHelperE<W> bq(
            TriFunction<String, KalamaHelperHelperA, String[], W> factory) {
        return new KalamaHelperHelperE<>(factory);
    }
}
