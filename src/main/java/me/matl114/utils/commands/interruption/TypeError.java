package me.matl114.utils.commands.interruption;

import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.ArgumentType;
import me.matl114.utils.commands.params.api.CommandExecution;

public class TypeError extends ArgumentException {
    KalamaHelperHelperH lo;
    ArgumentReader lm;
    String lp;
    String ln;

    public ArgumentReader sJ() {
        return this.lm;
    }

    public KalamaHelperHelperH sL() {
        return this.lo;
    }

    public TypeError(ArgumentReader reader, String argument, KalamaHelperHelperH typeName, String input) {
        this.lm = reader;
        this.ln = argument;
        this.lo = typeName;
        this.lp = input;
    }

    public TypeError(ArgumentType<?> argument, KalamaHelperHelperH typeName, String input) {
        this(null, argument == null ? null : argument.getArgsName(), typeName, input);
    }

    public String sK() {
        return this.ln;
    }

    public String sM() {
        return this.lp;
    }

    public TypeError(String argument, KalamaHelperHelperH typeName, String input) {
        this(null, argument, typeName, input);
    }

    @Override
    public void handleAbort(CommandExecution sender, InterruptionHandler command) {
        command.handleTypeError(sender, this.lm, this.ln, this.lo, this.lp);
    }
}
