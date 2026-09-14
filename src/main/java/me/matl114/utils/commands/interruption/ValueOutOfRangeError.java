package me.matl114.utils.commands.interruption;

import java.util.Collection;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.CommandExecution;

public class ValueOutOfRangeError extends ArgumentException {
    KalamaHelperHelperH lA;
    ArgumentReader lm;
    String WA;
    String name;
    String lz;

    public ValueOutOfRangeError(
            ArgumentReader reader, String name, Collection<String> options, String input, KalamaHelperHelperH type) {
        this.lm = reader;
        this.name = name;
        this.lz = "[" + String.join(", ", options) + "]";
        this.WA = input;
        this.lA = type;
    }

    @Override
    public void handleAbort(CommandExecution sender, InterruptionHandler command) {
        command.handleValueOutOfRange(sender, this.lm, this.name, this.lA, this.lz, this.WA);
    }

    public ValueOutOfRangeError(ArgumentReader reader, String name, float from, float to, float input) {
        this.lm = reader;
        this.name = name;
        this.lz = from + " ~ " + to + "(exclusive)";
        this.WA = String.valueOf(input);
        this.lA = KalamaHelperHelperH.Qw;
    }

    public ValueOutOfRangeError(ArgumentReader reader, String name, int from, int to, int input) {
        this.lm = reader;
        this.name = name;
        this.lz = from + " ~ " + to + "(exclusive)";
        this.WA = String.valueOf(input);
        this.lA = KalamaHelperHelperH.Qv;
    }

    public ValueOutOfRangeError(
            ArgumentReader reader, String name, String from, String to, String input, KalamaHelperHelperH type) {
        this.lm = reader;
        this.name = name;
        this.lz = from + " ~ " + to + "(exclusive)";
        this.WA = input;
        this.lA = type;
    }
}
