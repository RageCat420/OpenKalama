package me.matl114.utils.commands.params.api;

import com.google.common.collect.Streams;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import me.matl114.utils.commands.interruption.ArgumentException;

public interface KalamaHelperHelperF {
    KalamaHelperHelperF a = (s, arg) -> Stream.empty();

    default KalamaHelperHelperF j(Predicate<List<InputArgument<?>>> predicate) {
        return (p, args) -> predicate.test(args) ? this.b(p, args) : Stream.empty();
    }

    static KalamaHelperHelperF g(Supplier<Stream<String>> supplier) {
        return (s, arg) -> {
            Stream var3 = (Stream) supplier.get();
            return var3 == null ? Stream.empty() : var3;
        };
    }

    @Nonnull
    Stream<String> a(CommandExecution var1, List<InputArgument<?>> var2) throws ArgumentException;

    static KalamaHelperHelperF h(BiFunction<CommandExecution, String, Stream<String>> f) {
        return (p, args) -> {
            if (args.size() < 2) {
                return Stream.empty();
            } else {
                InputArgument var3 = args.get(args.size() - 2);
                String var4 = var3.a();
                return var4 == null ? Stream.empty() : (Stream) f.apply(p, var4);
            }
        };
    }

    default KalamaHelperHelperF c(KalamaHelperHelperF result) {
        return (s, arg) -> Stream.concat(this.b(s, arg), result.b(s, arg));
    }

    static KalamaHelperHelperF l(List<KalamaHelperHelperF> tabSupplier) {
        return (s, arg) -> {
            ArrayList<Stream<String>> var3 = new ArrayList<>(tabSupplier.size());

            for (KalamaHelperHelperF var5 : tabSupplier) {
                var3.add(var5.b(s, arg));
            }

            return Streams.concat(var3.toArray(Stream[]::new));
        };
    }

    static KalamaHelperHelperF i(BiFunction<CommandExecution, InputArgument<?>, Stream<String>> f) {
        return (p, args) -> {
            if (args.size() < 2) {
                return Stream.empty();
            } else {
                InputArgument var3 = args.get(args.size() - 2);
                return var3 == null ? Stream.empty() : (Stream) f.apply(p, var3);
            }
        };
    }

    static KalamaHelperHelperF d(Function<CommandExecution, List<String>> f) {
        return (s, arg) -> {
            List var3 = (List) f.apply(s);
            return var3 == null ? Stream.empty() : var3.stream();
        };
    }

    static KalamaHelperHelperF e(Function<CommandExecution, Stream<String>> f) {
        return (s, arg) -> {
            Stream var3 = (Stream) f.apply(s);
            return var3 == null ? Stream.empty() : var3;
        };
    }

    static KalamaHelperHelperF f(Supplier<List<String>> supplier) {
        return (s, arg) -> {
            List var3 = (List) supplier.get();
            return var3 == null ? Stream.empty() : var3.stream();
        };
    }

    @Nonnull
    default Stream<String> b(CommandExecution sender, List<InputArgument<?>> args) {
        try {
            return this.a(sender, args);
        } catch (ArgumentException var4) {
            return Stream.empty();
        }
    }

    default KalamaHelperHelperF k(Predicate<List<InputArgument<?>>> predicate, KalamaHelperHelperF result) {
        return (p, args) -> predicate.test(args) ? this.b(p, args) : result.b(p, args);
    }
}
