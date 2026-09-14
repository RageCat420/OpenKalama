package me.matl114.hooks.impl.xaeroplus.wrapper;

public class TextWrapper<T> extends ElementWrapper<T> {
    public String value;
    public int x;
    public int z;
    public int color;
    public float scale;

    public TextWrapper(String value, int x, int z, int color, float scale) {
        this.value = value;
        this.x = x;
        this.z = z;
        this.color = color;
        this.scale = scale;
    }
}
