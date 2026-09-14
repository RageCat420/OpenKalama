package me.matl114.utils.commands.params;

import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.commands.params.api.InputArgument;
import me.matl114.utils.commands.params.api.KalamaHelperHelperF;
import me.matl114.utils.commands.params.impl.AbstractArgumentType;

public class KalamaHelperHelperD<W extends AbstractArgumentType<T>, T> {
    List<KalamaHelperHelperF> e = new ArrayList<>();
    public static final List<String> a = List.of("true", "false");
    T d;
    Function<String, W> b;
    String c;

    public <R extends Enum<R>> KalamaHelperHelperD<W, T> q(R value) {
        Class var2 = value.getClass();
        this.d(() -> Arrays.stream((Enum[]) var2.getEnumConstants())
                        .map(s -> s.name().toLowerCase(Locale.ROOT)))
                .b(value.name().toLowerCase(Locale.ROOT));
        return this;
    }

    public T y() {
        return this.d;
    }

    public KalamaHelperHelperD<W, T> s(Function<String, Stream<String>> f) {
        this.r((p, str) -> (Stream<String>) f.apply(str));
        return this;
    }

    public KalamaHelperHelperD<W, T> e(Function<CommandExecution, Stream<String>> list) {
        this.e.add(KalamaHelperHelperF.e(list));
        return this;
    }

    public <R extends Enum<R>> KalamaHelperHelperD<W, T> p(Class<R> type) {
        this.d(() -> Arrays.stream((Enum[]) type.getEnumConstants())
                .map(s -> s.name().toLowerCase(Locale.ROOT)));
        return this;
    }

    public KalamaHelperHelperD<W, T> j() {
        this.d(me.matl114.utils.a.KalamaHelperHelperA.y());
        return this;
    }

    public KalamaHelperHelperD<W, T> o() {
        this.d(a::stream);
        return this;
    }

    public W v() {
        AbstractArgumentType var1 = this.b.apply(this.c);
        var1.aG(this.d);
        List var2 = List.copyOf(this.e);
        var1.l = KalamaHelperHelperF.l(var2);
        return (W) var1;
    }

    public KalamaHelperHelperD<W, T> c(KalamaHelperHelperF result) {
        this.e.add(result);
        return this;
    }

    public KalamaHelperHelperD<W, T> h(IntList list) {
        this.d(() -> list.stream().map(String::valueOf));
        return this;
    }

    public KalamaHelperHelperD<W, T> n(boolean def) {
        this.d(a::stream).b(String.valueOf(def));
        return this;
    }

    public Function<String, W> w() {
        return this.b;
    }

    public KalamaHelperHelperD<W, T> l(String... list) {
        this.d(() -> Arrays.stream(list));
        return this;
    }

    public List<KalamaHelperHelperF> z() {
        return this.e;
    }

    public KalamaHelperHelperD<W, T> A(Function<String, W> factory) {
        this.b = factory;
        return this;
    }

    public KalamaHelperHelperD<W, T> t(Function<InputArgument<?>, Stream<String>> f) {
        this.u((p, str) -> (Stream<String>) f.apply(str));
        return this;
    }

    public KalamaHelperHelperD(Function<String, W> factory) {
        this.b = factory;
    }

    public KalamaHelperHelperD<W, T> k(List<String> list) {
        this.d(list::stream);
        return this;
    }

    public KalamaHelperHelperD<W, T> u(BiFunction<CommandExecution, InputArgument<?>, Stream<String>> f) {
        this.c(KalamaHelperHelperF.i(f));
        return this;
    }

    public KalamaHelperHelperD<W, T> g(int def) {
        this.d(me.matl114.utils.a.KalamaHelperHelperA.x()).b(String.valueOf(def));
        return this;
    }

    public KalamaHelperHelperD<W, T> r(BiFunction<CommandExecution, String, Stream<String>> f) {
        this.c(KalamaHelperHelperF.h(f));
        return this;
    }

    public KalamaHelperHelperD<W, T> m(List<String> list, String def) {
        this.d(list::stream).b(def);
        return this;
    }

    public KalamaHelperHelperD<W, T> b(String defaultValue) {
        this.d = (T) defaultValue;
        return this;
    }

    public String x() {
        return this.c;
    }

    public KalamaHelperHelperD<W, T> a(T name) {
        this.d = (T) name;
        return this;
    }

    public KalamaHelperHelperD<W, T> d(Supplier<Stream<String>> list) {
        this.e.add(KalamaHelperHelperF.g(list));
        return this;
    }

    public KalamaHelperHelperD<W, T> f() {
        this.d(me.matl114.utils.a.KalamaHelperHelperA.x());
        return this;
    }

    public KalamaHelperHelperD<W, T> B(String name) {
        this.c = name;
        return this;
    }

    public KalamaHelperHelperD<W, T> i(float fl) {
        this.d(me.matl114.utils.a.KalamaHelperHelperA.y()).b(String.valueOf(fl));
        return this;
    }
}
