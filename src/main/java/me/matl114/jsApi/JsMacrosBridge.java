package me.matl114.jsApi;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface JsMacrosBridge {
    boolean isItemEmpty(Object var1);

    ItemStack e(Object var1);

    Object h(Object var1) throws Throwable;

    static JsMacrosBridge i() {
        return KalamaHelperHelperP.INSTANCE;
    }

    Object newBlockData(BlockState var1, BlockEntity var2, BlockPos var3);

    Object d();

    Object b(Object var1);

    default <T> T j(Object what, Class<T> type) {
        return (T) (type.isInstance(what) ? type.cast(what) : this.a(what, type));
    }

    Object f(ItemStack var1);

    <T> T a(Object var1, Class<T> var2);
}
