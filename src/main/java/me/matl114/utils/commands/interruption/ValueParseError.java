package me.matl114.utils.commands.interruption;

import javax.annotation.Nullable;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.CommandExecution;

public class ValueParseError extends ArgumentException {
    String ln;

    @Nullable
    ArgumentReader lm;

    @Override
    public void handleAbort(CommandExecution sender, InterruptionHandler command) {
        command.bv(sender, this.lm, this.ln);
    }

    public ValueParseError(ArgumentReader reader, String argument) {
        this.lm = reader;
        this.ln = argument;
    }
}
