package me.matl114.hacks.modules.slimefun;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.matl114.hacks.InvTasks;
import me.matl114.utils.itemdb.ItemStackData;
import me.matl114.utils.itemdb.ItemStackDataWithAmount;
import net.minecraft.item.ItemStack;

public record SlimefunSubHelperQ(String id, ItemStackDataWithAmount icon) {
    public static final SlimefunSubHelperQ Wl =
            new SlimefunSubHelperQ("NULL", new ItemStackDataWithAmount(ItemStackData.Xa(RecipeDatabase.Ip), 1));
    public static Codec<SlimefunSubHelperQ> dt = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.optionalFieldOf("rid", "").forGetter(SlimefunSubHelperQ::id),
                    InvTasks.D
                            .optionalFieldOf("icon", ItemStackDataWithAmount.EMPTY)
                            .forGetter(SlimefunSubHelperQ::icon))
            .apply(instance, SlimefunSubHelperQ::new));

    public SlimefunSubHelperQ(String id, ItemStackDataWithAmount icon) {
        this.id = id;
        this.icon = icon;
    }

    public ItemStackDataWithAmount icon() {
        return this.icon;
    }

    public ItemStack amY() {
        return this.icon.getAsItemStack();
    }
}
