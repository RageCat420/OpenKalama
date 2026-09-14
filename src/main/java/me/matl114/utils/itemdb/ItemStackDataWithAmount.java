package me.matl114.utils.itemdb;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.matl114.utils.codecs.NullCodec;
import net.minecraft.item.ItemStack;

public record ItemStackDataWithAmount(ItemStackData stackReference, int count) {
    public static final ItemStackDataWithAmount EMPTY = new ItemStackDataWithAmount(ItemStackData.Iz, 0);

    public ItemStack getAsItemStack() {
        return this.stackReference.gf().copyWithCount(this.count);
    }

    public int yQ() {
        return this.count;
    }

    public ItemStackDataWithAmount asCount(int v) {
        return new ItemStackDataWithAmount(this.stackReference, v);
    }

    public ItemStackData yP() {
        return this.stackReference;
    }

    public ItemStack yN() {
        return this.stackReference.gf();
    }

    public static Codec<ItemStackDataWithAmount> createCodecOf(Codec<ItemStackData> itemStackData) {
        Codec<ItemStackDataWithAmount> var1 = RecordCodecBuilder.create(instance -> instance.group(
                        itemStackData.fieldOf("typeid").forGetter(ItemStackDataWithAmount::yP),
                        Codec.INT.fieldOf("amount").forGetter(ItemStackDataWithAmount::yQ))
                .apply(instance, ItemStackDataWithAmount::new));
        return new NullCodec<>(var1, s -> s.stackReference == ItemStackData.Iz, EMPTY);
    }

    public static ItemStackDataWithAmount of(ItemStack itemStack) {
        return itemStack.isEmpty()
                ? EMPTY
                : new ItemStackDataWithAmount(ItemStackData.Xa(itemStack), itemStack.getCount());
    }
}
