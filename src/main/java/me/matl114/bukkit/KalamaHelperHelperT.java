package me.matl114.bukkit;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import me.matl114.utils.Debug;
import me.matl114.versioned.api.VNbt;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.StringNbtReader;

public class KalamaHelperHelperT {
   private static final Pattern b = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)(?:b|s|i|l)|[-+]?(?:[0-9]+[.]?[0-9]*|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?(?:f|d)", 2);
   private static final Pattern a = Pattern.compile("^\\[.*]");
   public static final String c = "item:\n  ==: org.bukkit.inventory.ItemStack\n  v: 3465\n  type: DIRT\n  meta:\n    ==: ItemMeta\n    meta-type: UNSPECIFIC\n    PublicBukkitValues:\n      infinityexpansion:display: 351372703i\n";

   public static BukkitItemStack c(String string) {
      return d(string);
   }

   public static BukkitItemStack d(String string) {
      BukkitYaml var1 = new BukkitYaml();

      try {
         return var1.getItemStackFromString(string);
      } catch (KalamaHelperHelperS var3) {
         Debug.f(var3);
         return new BukkitItemStack(Items.STONE, 1);
      }
   }

   public static NbtElement a(Object object) {
      if (object instanceof String var1) {
         try {
            return StringNbtReader.parse(var1);
         } catch (CommandSyntaxException var3) {
            throw new RuntimeException("Failed to deserialise nbt", var3);
         }
      } else {
         return b(object);
      }
   }

   public static NbtElement b(Object object) {
      if (object instanceof Map var1) {
         NbtCompound var17 = new NbtCompound();

         for (Entry<?, ?> var19 : ((Map<?, ?>)var1).entrySet()) {
            String var5 = String.valueOf(var19.getKey());
            var17.put(var5, b(var19.getValue()));
         }

         return var17;
      } else if (object instanceof List var6) {
         if (var6.isEmpty()) {
            return new NbtList();
         } else {
            NbtList var16 = new NbtList();

            for (Object var4 : var6) {
               var16.add(b(var4));
            }

            return var16;
         }
      } else if (object instanceof Boolean var7) {
         return NbtByte.of(var7);
      } else if (object instanceof Byte var8) {
         return NbtByte.of(var8);
      } else if (object instanceof Short var9) {
         return NbtShort.of(var9);
      } else if (object instanceof Integer var10) {
         return NbtInt.of(var10);
      } else if (object instanceof Long var11) {
         return NbtLong.of(var11);
      } else if (object instanceof Float var12) {
         return NbtFloat.of(var12);
      } else if (object instanceof Double var13) {
         return NbtDouble.of(var13);
      } else if (object instanceof BigDecimal var14) {
         return NbtDouble.of(var14.doubleValue());
      } else if (!(object instanceof String var2)) {
         throw new RuntimeException("Could not deserialize NBT value of type " + object.getClass().getName());
      } else {
         if (a.matcher(var2).matches() || var2.startsWith("{") || b.matcher(var2).matches()) {
            try {
               return VNbt.getInstance().c(var2);
            } catch (RuntimeException var15) {
            }
         }

         return NbtString.of(var2.replace("\\n", "\n"));
      }
   }
}
