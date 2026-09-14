package me.matl114.hacks;

import java.util.List;
import me.matl114.commands.MainCommand;
import me.matl114.managers.Tasks;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperH;
import me.matl114.utils.commands.params.ArgumentInputStream;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.CommandExecution;

class KalamaHelperHelperWX implements KalamaHelperHelperH {
    private final KalamaHelperHelperNX b;

    KalamaHelperHelperWX(final KalamaHelperHelperNX this$0) {
        this.b = this$0;
    }

    @Override
    public boolean a(CommandExecution var1, ArgumentInputStream streamArgs, ArgumentReader argsReader) {
        int var4 = streamArgs.nextInt();
        String[] var5 = argsReader.k();
        Tasks.l(() -> MainCommand.dispatchCommand(var5), var4);
        return true;
    }

    @Override
    public List<String> b(CommandExecution var1, ArgumentInputStream streamArgs, ArgumentReader argsReader) {
        ArgumentReader var4 = new ArgumentReader(argsReader.k());
        var4.d();
        return this.b.e(var1, argsReader);
    }
}
