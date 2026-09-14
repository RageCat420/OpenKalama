package me.matl114.utils.commands.interruption;

import me.matl114.utils.KalamaHelperHelperJ;
import me.matl114.utils.commands.params.api.CommandExecution;

public class ArgumentException extends KalamaHelperHelperJ {
    public boolean isConditionError() {
        return false;
    }

    public void handleAbort(CommandExecution var1, InterruptionHandler var2) {}
}
