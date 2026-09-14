package me.matl114.utils.render;

public record KalamaHelperHelperF(int val1, int val2, int val3, int val4) implements ColorQuad {
    public int val2() {
        return this.val2;
    }

    public int val3() {
        return this.val3;
    }

    public int val4() {
        return this.val4;
    }

    @Override
    public int get(int idx) {
        return switch (idx & 3) {
            case 0 -> this.val1;
            case 1 -> this.val2;
            case 2 -> this.val3;
            case 3 -> this.val4;
            default -> throw new IndexOutOfBoundsException();
        };
    }

    public int val1() {
        return this.val1;
    }
}
