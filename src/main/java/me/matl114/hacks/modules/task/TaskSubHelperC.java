package me.matl114.hacks.modules.task;

import java.util.List;
import java.util.Locale;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperH;
import me.matl114.utils.commands.params.ArgumentInputStream;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.CommandExecution;

class TaskSubHelperC implements KalamaHelperHelperH {
    private final ConfigManager a;

    TaskSubHelperC(final ConfigManager this$0) {
        this.a = this$0;
    }

    @Override
    public boolean a(CommandExecution sender, ArgumentInputStream streamArgs, ArgumentReader argsReader) {
        this.a.LB(streamArgs, argsReader);
        return true;
    }

    @Override
    public List<String> b(CommandExecution sender, ArgumentInputStream streamArgs, ArgumentReader argsReader) {
        String[] var4 = argsReader.k();
        String var5 = var4.length > 0 ? var4[var4.length - 1] : "";
        return this.a
                .LI()
                .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(var5.toLowerCase(Locale.ROOT)))
                .toList();
    }
}
