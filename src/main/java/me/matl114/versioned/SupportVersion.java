package me.matl114.versioned;

public class SupportVersion {
    int b;
    public static final SupportVersion CURRENT = e();
    int c;

    public int f() {
        return this.b;
    }

    @Override
    public String toString() {
        return this.b > 25 ? this.b + "." + this.c : "1." + this.b + "." + this.c;
    }

    public int g() {
        return this.c;
    }

    public static SupportVersion parse(String version) {
        String[] var1 = version.split("\\.");

        try {
            return var1.length == 2 && var1[0].equals("1")
                    ? new SupportVersion(Integer.parseInt(var1[1]), 1)
                    : new SupportVersion(
                            Integer.parseInt(var1[var1.length - 2]), Integer.parseInt(var1[var1.length - 1]));
        } catch (Throwable var3) {
            return new SupportVersion(21, 1);
        }
    }

    public boolean d(int major, int minor) {
        return this.b == major && this.c == minor;
    }

    public static SupportVersion e() {
        String var0 = "1.21.1";
        return parse(var0);
    }

    public boolean c(int major, int minor) {
        if (major < this.b) {
            return false;
        } else {
            return major > this.b ? true : minor >= this.c;
        }
    }

    public SupportVersion(int major, int minor) {
        this.b = major;
        this.c = minor;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SupportVersion var2 && var2.f() == this.b && var2.g() == this.c;
    }

    public boolean b(int major, int minor) {
        if (major < this.b) {
            return true;
        } else {
            return major > this.b ? false : minor <= this.c;
        }
    }
}
