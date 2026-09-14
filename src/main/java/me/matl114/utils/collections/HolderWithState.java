package me.matl114.utils.collections;

public class HolderWithState<T> {
    public T val;
    public static HolderWithState EMPTY_COMPLETE = new HolderWithState(null, true);
    public boolean state;

    public boolean c() {
        return !this.state;
    }

    public HolderWithState(T val, boolean state) {
        this.val = (T) val;
        this.state = state;
    }

    public boolean a() {
        return this.val == null;
    }

    public HolderWithState(T val) {
        this((T) val, false);
    }

    public boolean b() {
        return this.state;
    }
}
