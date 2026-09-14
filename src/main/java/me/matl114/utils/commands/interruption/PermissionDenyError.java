package me.matl114.utils.commands.interruption;

import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.CommandExecution;

public class PermissionDenyError extends ArgumentException {
    ArgumentReader HK;
    String HJ;

    public PermissionDenyError(String permission, ArgumentReader currentCommandInput) {
        this.HJ = permission;
        this.HK = currentCommandInput;
    }

    @Override
    public void handleAbort(CommandExecution sender, InterruptionHandler command) {
        command.bz(sender, this.HJ, this.HK);
    }

    @Override
    public boolean isConditionError() {
        return true;
    }
}
