package me.matl114.hacks.modules.inv;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.versioned.api.VItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public record InvSubHelperB(String name, List<KalamaHelperHelperK<NbtCompound>> itemNBT, int maxSize, InvSubHelperL rule) {
   public static final InvSubHelperB dr = new InvSubHelperB("", List.of(), 0, new InvSubHelperL());
   public static List<String> ds = List.of("name", "item-nbt", "max-size", "rule");
   public static Codec<InvSubHelperB> dt = RecordCodecBuilder.create(
      oinstance -> oinstance.group(
            Codec.STRING.fieldOf("name").forGetter(InvSubHelperB::name),
            Codec.list(InventoryUtils.b).fieldOf("items").forGetter(InvSubHelperB::hz),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("maxSize").forGetter(InvSubHelperB::hA),
            InvSubHelperL.wJ.forGetter(InvSubHelperB::hB)
         )
         .apply(oinstance, InvSubHelperB::new)
   );

   public List<KalamaHelperHelperK<NbtCompound>> hz() {
      return this.itemNBT;
   }

   public InvSubHelperL hB() {
      return this.rule;
   }

   public int hA() {
      return this.maxSize;
   }

   public static InvSubHelperB fromItem(String name, List<KalamaHelperHelperK<ItemStack>> itemNBT, int maxSize, InvSubHelperL rule) {
      return new InvSubHelperB(
         name,
         itemNBT.stream()
            .filter(s -> !((ItemStack)s.val()).isEmpty())
            .map(s -> new KalamaHelperHelperK<>(s.index(), VItem.w().k((ItemStack)s.val(), ItemStackUtils.registry())))
            .toList(),
         maxSize,
         rule
      );
   }

   public List<KalamaHelperHelperK<ItemStack>> hy() {
      return this.itemNBT.stream().map(s -> new KalamaHelperHelperK<>(s.index(), VItem.w().j(s.val(), ItemStackUtils.registry()))).toList();
   }
}
