package me.matl114.utils.collections;

public class FlatBitsetUtil {
    private static final long ALL_SET = -1L;
    private static final int a = 6;
    private static final int c = 64;

    public static boolean isRangeSet(long[] bitset, int from, int to) {
        return firstClear(bitset, from, to) == -1;
    }

    public static int firstClear(long[] bitset, int from, int to) {
        if ((from | to | to - from) < 0) {
            throw new IndexOutOfBoundsException();
        } else {
            int var3 = from >>> 6;
            int var4 = from & -64;

            long var5;
            for (var5 = ~bitset[var3] & -1L << from; var5 == 0L; var5 = ~bitset[++var3]) {
                var4 += 64;
                if (var4 >= to) {
                    return -1;
                }
            }

            int var7 = var4 | Long.numberOfTrailingZeros(var5);
            return var7 >= to ? -1 : var7;
        }
    }
}
