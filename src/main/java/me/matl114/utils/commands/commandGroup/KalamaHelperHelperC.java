package me.matl114.utils.commands.commandGroup;

import com.google.common.collect.Streams;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import me.matl114.utils.commands.interruption.ArgumentException;
import me.matl114.utils.commands.interruption.DispatchFailureError;
import me.matl114.utils.commands.interruption.PermissionDenyError;
import me.matl114.utils.commands.params.ArgumentInputStream;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.CommandExecution;
import org.jetbrains.annotations.NotNull;

public interface KalamaHelperHelperC extends CustomTabExecutor, KalamaHelperHelperD {
    @Override
    default Stream<String> f(CommandExecution sender, ArgumentReader reader) {
        if (this.be(sender)) {
            if (reader.hasNext()) {
                String var3 = reader.g();
                SubCommand var4 = this.at(var3);
                if (var4 != null) {
                    reader.f();
                    return var4.f(sender, reader);
                } else {
                    return Stream.empty();
                }
            } else {
                return this.getHelp(reader.m());
            }
        } else {
            return Stream.empty();
        }
    }

    @Override
    default boolean onCustomCommand(@NotNull CommandExecution var1, ArgumentReader reader) throws ArgumentException {
        if (this.be(var1)) {
            if (reader.hasNext()) {
                String var3 = reader.f();
                SubCommand var4 = this.at(var3);
                if (var4 != null) {
                    return var4.onCustomCommand(var1, reader);
                } else {
                    reader.i();
                    return this.av(var1, reader);
                }
            } else {
                throw new DispatchFailureError(reader);
            }
        } else {
            throw new PermissionDenyError(this.a(), reader);
        }
    }

    SubCommand at(String var1);

    default boolean av(@NotNull CommandExecution var1, ArgumentReader reader) throws ArgumentException {
        SubCommand var3 = this.aA();
        if (var3 == null) {
            throw new DispatchFailureError(reader);
        } else {
            return var3.onCustomCommand(var1, reader);
        }
    }

    @Override
    default Stream<String> getHelp(String prefix) {
        return Stream.concat(
                Streams.concat(this.az().stream()
                        .map(cmd -> cmd.getHelp(prefix + cmd.c() + " "))
                        .toArray(Stream[]::new)),
                this.aA() == null ? Stream.empty() : this.aA().getHelp(prefix));
    }

    default List<String> au(CommandExecution sender, ArgumentReader arguments) {
        SubCommand var3 = this.aA();
        return var3 == null ? List.of() : var3.e(sender, arguments);
    }

    @Override
    default List<String> e(CommandExecution sender, ArgumentReader arguments) {
        ArrayList var3 = new ArrayList();
        if (this.be(sender)) {
            ArgumentInputStream var4 = this.b(sender, arguments);
            var4.getTabComplete(sender).forEach(var3::add);
            if (arguments.hasNext()) {
                String var5 = var4.d().a();
                SubCommand var6 = this.at(var5);
                if (var6 != null) {
                    var4.e();
                    List var7 = var6.e(sender, arguments);
                    if (var7 != null) {
                        var3.addAll(var7);
                        return var3;
                    }
                }

                List var8 = this.au(sender, arguments);
                if (var8 != null) {
                    var3.addAll(var8);
                    return var3;
                }
            }
        }

        return var3;
    }
}
