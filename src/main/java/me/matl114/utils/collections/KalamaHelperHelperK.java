package me.matl114.utils.collections;

public record KalamaHelperHelperK<T>(int index, T val) {
    public int index() {
        return this.index;
    }

    public T val() {
        return this.val;
    }
}
