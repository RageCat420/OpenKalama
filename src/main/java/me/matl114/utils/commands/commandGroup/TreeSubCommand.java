package me.matl114.utils.commands.commandGroup;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import me.matl114.utils.commands.interruption.ArgumentException;
import me.matl114.utils.commands.interruption.DispatchFailureError;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.KalamaHelperHelperA;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.commands.params.api.InputArgument;
import net.minecraft.util.Language;
import org.jetbrains.annotations.NotNull;

public class TreeSubCommand extends SubCommandImpl implements KalamaHelperHelperC, KalamaHelperHelperD {
    private final Map<String, SubCommand> s;
    private SubCommand q = null;
    boolean t;
    private me.matl114.utils.commands.params.api.KalamaHelperHelperF r =
            me.matl114.utils.commands.params.api.KalamaHelperHelperF.a;

    public TreeSubCommand(String name, String... helpContent) {
        super(name, null, helpContent);
        this.t = false;
        this.e = new KalamaHelperHelperA(
                KalamaHelperHelperA.a().B("dispatch_" + name).c(this::cf).v());
        this.s = new LinkedHashMap<>();
    }

    public boolean onDefaultCommand(@NotNull CommandExecution var1, ArgumentReader reader) throws ArgumentException {
        SubCommand var3 = this.aA();
        if (var3 == null) {
            DispatchFailureError var4 = new DispatchFailureError(reader);
            var4.setCondition(this.t);
            throw var4;
        } else {
            return var3.onCustomCommand(var1, reader);
        }
    }

    @Override
    public void setFallbackCommand(
            SubCommand fallbackCommand, me.matl114.utils.commands.params.api.KalamaHelperHelperF fallbackTabSuggestor) {
        this.q = fallbackCommand;
        this.r = fallbackTabSuggestor;
    }

    @Override
    public void registerSub(SubCommand command) {
        this.s.put(command.c(), command);
    }

    public TreeSubCommand conditional(boolean conditional) {
        this.t = conditional;
        return this;
    }

    public Stream<String> cf(CommandExecution CommandExecution, List<InputArgument<?>> argumentReader) {
        return Stream.concat(this.s.keySet().stream(), this.r.b(CommandExecution, argumentReader));
    }

    @Override
    public Stream<String> f(CommandExecution sender, ArgumentReader arguments) {
        return KalamaHelperHelperC.super.f(sender, arguments);
    }

    @Override
    public SubCommand at(String name) {
        return this.s.get(name);
    }

    @Override
    public Collection<SubCommand> az() {
        return this.s.values();
    }

    @Override
    public SubCommand aA() {
        return this.q;
    }

    @Override
    public Stream<String> getHelp(String prefix) {
        return Stream.concat(
                Stream.of(this.help).map(s -> prefix + Language.getInstance().get(s, s)),
                KalamaHelperHelperC.super.getHelp(prefix));
    }
}
