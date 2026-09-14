package me.matl114.utils.commands.commandGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import me.matl114.utils.commands.params.KalamaHelperHelperA;
import me.matl114.utils.commands.params.api.ArgumentType;
import org.apache.commons.lang3.function.TriFunction;

public class KalamaHelperHelperE<T extends SubCommand> {
    String d;
    List<Consumer<T>> e;
    String b;
    List<ArgumentType<?>> a = new ArrayList<>();
    List<String> c = new ArrayList<>();
    TriFunction<String, KalamaHelperHelperA, String[], T> f;

    public <W extends SubCommand> W l(TriFunction<String, KalamaHelperHelperA, String[], W> builder) {
        SubCommand var2 = (SubCommand) builder.apply(
                this.b, new KalamaHelperHelperA(this.a.toArray(ArgumentType[]::new)), this.c.toArray(String[]::new));
        this.f((T) var2);
        return (W) var2;
    }

    public KalamaHelperHelperE<T> d(String helper) {
        this.c.add(helper);
        return this;
    }

    public KalamaHelperHelperE(KalamaHelperHelperE<T> builder) {
        this.e = new ArrayList<>();
        this.a.addAll(builder.a);
        this.c.addAll(builder.c);
        this.d = builder.d;
        this.f = builder.f;
        this.b = builder.b;
    }

    public KalamaHelperHelperE<T> g(Consumer<T> postTask) {
        this.e.add(postTask);
        return this;
    }

    public KalamaHelperHelperE(TriFunction<String, KalamaHelperHelperA, String[], T> builder) {
        this.e = new ArrayList<>();
        this.f = builder;
    }

    public KalamaHelperHelperE<T> h(ArgumentType<?> arg) {
        this.a.add(arg);
        return this;
    }

    public T k() {
        SubCommand var1 = (SubCommand) (Object) this.f.apply(
                this.b, new KalamaHelperHelperA(this.a.toArray(ArgumentType[]::new)), this.c.toArray(String[]::new));
        this.f((T) var1);
        return (T) var1;
    }

    public KalamaHelperHelperE<T> b(List<String> helpers) {
        this.c.addAll(helpers);
        return this;
    }

    public KalamaHelperHelperE<T> a(String name) {
        this.b = name;
        return this;
    }

    private void f(T command) {
        if (this.d != null) {
            command.setPermission(this.d);
        }

        this.e.forEach(s -> s.accept((T) command));
    }

    public KalamaHelperHelperE<T> e(String permission) {
        this.d = permission;
        return this;
    }

    public KalamaHelperHelperE<T> c(String... helpers) {
        this.c.addAll(Arrays.asList(helpers));
        return this;
    }

    public KalamaHelperHelperE<T> j(KalamaHelperHelperA args) {
        for (ArgumentType var5 : args.d()) {
            this.h(var5);
        }

        return this;
    }

    public KalamaHelperHelperE<T> i(UnaryOperator<me.matl114.utils.commands.params.KalamaHelperHelperD<?, ?>> arg) {
        me.matl114.utils.commands.params.KalamaHelperHelperD var2 = KalamaHelperHelperA.a();
        arg.apply(var2);
        this.a.add(var2.v());
        return this;
    }
}
