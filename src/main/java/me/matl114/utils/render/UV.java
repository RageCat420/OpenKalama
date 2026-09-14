package me.matl114.utils.render;

public class UV {
    public static final UV DEFAULT = new UV(0.0F, 0.0F, 1.0F, 1.0F);
    float a;
    float d;
    float b;
    float c;

    public float d() {
        return this.b;
    }

    public UV(float u0, float v0, float u1, float v1) {
        this.a = u0;
        this.b = v0;
        this.c = u1;
        this.d = v1;
    }

    public float f() {
        return this.d;
    }

    public float b(int idx) {
        return (idx - 1 & 2) == 0 ? this.d : this.b;
    }

    public void j(float v1) {
        this.d = v1;
    }

    protected boolean k(Object other) {
        return other instanceof UV;
    }

    @Override
    public int hashCode() {
        byte var1 = 59;
        int var2 = 1;
        var2 = var2 * 59 + Float.floatToIntBits(this.c());
        var2 = var2 * 59 + Float.floatToIntBits(this.d());
        var2 = var2 * 59 + Float.floatToIntBits(this.e());
        return var2 * 59 + Float.floatToIntBits(this.f());
    }

    @Override
    public String toString() {
        return "UV(u0=" + this.c() + ", v0=" + this.d() + ", u1=" + this.e() + ", v1=" + this.f() + ")";
    }

    public float a(int idx) {
        return (idx & 2) == 0 ? this.a : this.c;
    }

    public float e() {
        return this.c;
    }

    public float c() {
        return this.a;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof UV var2)) {
            return false;
        } else if (!var2.k(this)) {
            return false;
        } else if (Float.compare(this.c(), var2.c()) != 0) {
            return false;
        } else if (Float.compare(this.d(), var2.d()) != 0) {
            return false;
        } else {
            return Float.compare(this.e(), var2.e()) != 0 ? false : Float.compare(this.f(), var2.f()) == 0;
        }
    }

    public void i(float u1) {
        this.c = u1;
    }

    public void h(float v0) {
        this.b = v0;
    }

    public void g(float u0) {
        this.a = u0;
    }
}
