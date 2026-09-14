package me.matl114.utils.commands.params.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;
import me.matl114.utils.commands.interruption.ArgumentException;
import me.matl114.utils.commands.interruption.KalamaHelperHelperH;
import me.matl114.utils.commands.interruption.TypeError;
import me.matl114.utils.commands.interruption.ValueAbsentError;
import me.matl114.utils.commands.interruption.ValueOutOfRangeError;
import me.matl114.utils.commands.interruption.ValueParseError;
import me.matl114.utils.commands.params.ArgumentReader;

public interface InputArgument<W> {
    public int c();

    default <T extends Enum<T>> boolean D(Class<T> type) throws ArgumentException {
        try {
            this.A(type);
            return true;
        } catch (ArgumentException var3) {
            return false;
        }
    }

    default String B(Collection<String> selections) throws ArgumentException {
        String var2 = this.z();

        for (String var4 : selections) {
            if (var4.equalsIgnoreCase(var2)) {
                return var4;
            }
        }

        throw new ValueOutOfRangeError(this.e(), this.i().getArgsName(), selections, var2, KalamaHelperHelperH.Qz);
    }

    default boolean t() {
        try {
            this.getFloat();
            return true;
        } catch (ArgumentException var2) {
            return false;
        }
    }

    public int b();

    default ArgumentReader f() {
        return new ArgumentReader(this.d()).c(this.b());
    }

    default <T extends Enum<T>> boolean E(Collection<String> selections) throws ArgumentException {
        try {
            this.B(selections);
            return true;
        } catch (ArgumentException var3) {
            return false;
        }
    }

    default float getFloat() throws ArgumentException {
        this.m();
        String var1 = this.a();

        try {
            return Float.parseFloat(var1);
        } catch (Throwable var3) {
            throw new TypeError(this.e(), this.i().getArgsName(), KalamaHelperHelperH.Qw, var1);
        }
    }

    default int clampInt(int low, int highEx) throws ArgumentException {
        int var3 = this.n();
        if (var3 >= low && var3 < highEx) {
            return var3;
        } else {
            throw new ValueOutOfRangeError(this.e(), this.i().getArgsName(), low, highEx, var3);
        }
    }

    default double getDouble() throws ArgumentException {
        this.m();
        String var1 = this.a();

        try {
            return Double.parseDouble(var1);
        } catch (Throwable var3) {
            throw new TypeError(this.e(), this.i().getArgsName(), KalamaHelperHelperH.Qw, var1);
        }
    }

    default ArgumentReader e() {
        return new ArgumentReader(this.d()).c(this.c());
    }

    W g() throws ArgumentException;

    default double clampDouble(double low, double highEx) throws ArgumentException {
        double var5 = this.getDouble();
        if (var5 >= low && var5 < highEx) {
            return var5;
        } else {
            throw new ValueOutOfRangeError(
                    this.e(),
                    this.i().getArgsName(),
                    String.valueOf(low),
                    String.valueOf(highEx),
                    String.valueOf(var5),
                    KalamaHelperHelperH.Qw);
        }
    }

    default boolean C() {
        return this.g() != null;
    }

    default boolean o() throws ArgumentException {
        this.m();
        String var1 = this.a();
        switch (var1) {
            case "true":
                return true;
            case "false":
                return false;
            default:
                throw new TypeError(this.e(), this.i().getArgsName(), KalamaHelperHelperH.Qx, var1);
        }
    }

    default int n() throws ArgumentException {
        this.m();
        String var1 = this.a();

        try {
            return Integer.parseInt(var1);
        } catch (Throwable var3) {
            throw new TypeError(this.e(), this.i().getArgsName(), KalamaHelperHelperH.Qv, var1);
        }
    }

    public String a();

    default void l() throws ArgumentException {
        if (!this.k()) {
            throw new ValueAbsentError(this.e(), this.i().getArgsName());
        }
    }

    default boolean j() {
        return null == this.g();
    }

    default W y() throws ArgumentException {
        this.m();
        Object var1 = this.g();
        if (var1 == null) {
            throw new ValueParseError(this.e(), this.i().getArgsName());
        } else {
            return (W) var1;
        }
    }

    public ArgumentType<W> i();

    default float clampFloat(float low, float highEx) throws ArgumentException {
        float var3 = this.getFloat();
        if (var3 >= low && var3 < highEx) {
            return var3;
        } else {
            throw new ValueOutOfRangeError(this.e(), this.i().getArgsName(), low, highEx, var3);
        }
    }

    default boolean r() {
        try {
            this.n();
            return true;
        } catch (ArgumentException var2) {
            return false;
        }
    }

    public String h();

    default <T extends Enum<T>> T A(Class<T> type) throws ArgumentException {
        String var2 = this.z();
        Enum[] var3 = (Enum[]) type.getEnumConstants();

        for (int var4 = 0; var4 < var3.length; var4++) {
            if (var3[var4].name().equalsIgnoreCase(var2)) {
                return (T) var3[var4];
            }
        }

        throw new ValueOutOfRangeError(
                this.e(),
                this.i().getArgsName(),
                Arrays.stream((Enum[]) type.getEnumConstants())
                        .map(Enum::name)
                        .map(s -> s.toLowerCase(Locale.ROOT))
                        .collect(Collectors.toList()),
                var2,
                KalamaHelperHelperH.Qz);
    }

    default boolean s() {
        try {
            this.o();
            return true;
        } catch (ArgumentException var2) {
            return false;
        }
    }

    default void m() throws ArgumentException {
        this.l();
        if (this.j()) {
            throw new ValueParseError(this.e(), this.i().getArgsName());
        }
    }

    default String z() throws ArgumentException {
        this.m();
        String var1 = this.a();
        if (var1 == null) {
            throw new ValueParseError(this.e(), this.i().getArgsName());
        } else {
            return var1;
        }
    }

    default boolean u() {
        try {
            this.getDouble();
            return true;
        } catch (ArgumentException var2) {
            return false;
        }
    }

    public boolean k();

    public ArgumentReader d();
}
