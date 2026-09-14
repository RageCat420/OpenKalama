package me.matl114.utils.commands.commandGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import me.matl114.utils.commands.interruption.ArgumentException;
import me.matl114.utils.commands.interruption.DispatchFailureError;
import me.matl114.utils.commands.interruption.PermissionDenyError;
import me.matl114.utils.commands.params.ArgumentInputStream;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.CommandExecution;
import org.jetbrains.annotations.Nullable;

public class KalamaHelperHelperF implements SubCommand, KalamaHelperHelperD {
    String c;
    List<SubCommand> o = new ArrayList<>();
    String b;

    @Override
    public String c() {
        return this.b;
    }

    @Override
    public SubCommand aA() {
        return null;
    }

    @Override
    public ArgumentInputStream b(CommandExecution execution, ArgumentReader reader) {
        return new ArgumentInputStream(execution, reader, List.of(), List.of());
    }

    public KalamaHelperHelperF(String name) {
        this.b = name;
    }

    @Override
    public void registerSub(SubCommand command) {
        this.o.add(command);
    }

    @Override
    public void setPermission(String permission) {
        this.c = permission;
    }

    @Override
    public boolean onCustomCommand(CommandExecution var1, ArgumentReader reader) {
        if (!this.be(var1)) {
            throw new PermissionDenyError(this.a(), reader);
        } else {
            ArgumentException var3 = null;

            for (SubCommand var5 : this.o) {
                try {
                    if (var5.onCustomCommand(var1, new ArgumentReader(reader))) {
                        return true;
                    }
                } catch (ArgumentException var8) {
                    if (!var8.isConditionError()) {
                        throw var8;
                    }

                    var3 = var8;
                }
            }

            if (var3 != null) {
                throw var3;
            } else {
                throw new DispatchFailureError(reader);
            }
        }
    }

    @Override
    public Collection<SubCommand> az() {
        return this.o;
    }

    @Override
    public Stream<String> getHelp(String prefix) {
        return this.o.stream().flatMap(s -> s.getHelp(prefix + this.c() + " "));
    }

    @Override
    public void setFallbackCommand(
            SubCommand fallbackCommand, me.matl114.utils.commands.params.api.KalamaHelperHelperF fallbackTabSuggestor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> e(CommandExecution sender, ArgumentReader arguments) {
        return this.be(sender)
                ? this.o.stream()
                        .flatMap(s -> s.e(sender, new ArgumentReader(arguments)).stream())
                        .toList()
                : List.of();
    }

    @Nullable
    @Override
    public String a() {
        return this.c;
    }

    @Override
    public Stream<String> f(CommandExecution sender, ArgumentReader arguments) {
        return this.be(sender)
                ? this.o.stream().flatMap(s -> s.f(sender, new ArgumentReader(arguments)))
                : Stream.empty();
    }
}
