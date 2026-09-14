package me.matl114.bukkit;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import me.matl114.utils.ItemStackUtils;
import me.matl114.versioned.api.VItem;
import me.matl114.versioned.api.VNbt;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public final class CraftItemStack extends BukkitItemStack {
   int AJ;
   int AK;
   int qm;
   ItemStack AL;
   Map<String, String> AH;
   public static final Codec<ComponentChanges> CODEC_DISPLAY_CHANGES = Codec.of(
      ComponentChanges.CODEC, Codec.unboundedMap(Codec.STRING, Codec.PASSTHROUGH).map(s -> {
         if (s.isEmpty()) {
            return ComponentChanges.EMPTY;
         } else {
            Reference2ObjectArrayMap var1 = new Reference2ObjectArrayMap(s.size());

            for (Entry var3 : s.entrySet()) {
               String var4 = (String)var3.getKey();
               boolean var5 = false;
               String var6;
               if (var4.startsWith("!")) {
                  var6 = var4.substring(1);
                  var5 = true;
               } else {
                  var6 = var4;
               }

               ComponentType var7 = (ComponentType)Registries.DATA_COMPONENT_TYPE.get(Identifier.tryParse(var6));
               if (var7 != null) {
                  if (var5) {
                     var1.put(var7, Optional.empty());
                  } else {
                     Codec var8 = var7.getCodecOrThrow();
                     var8 = VItem.w().q().getOrDefault(var7, var8);
                     DataResult<? extends Pair<?, ?>> var9 = var8.decode((Dynamic<Object>)var3.getValue());
                     if (var9.isSuccess()) {
                        var1.put(var7, var9.result().map(Pair::getFirst));
                     }
                  }
               }
            }

            return new ComponentChanges(var1);
         }
      })
   );
   String AI;

   public static CraftItemStack Pa(Map<String, Object> args) {
      int var3 = args.getOrDefault("schema_version", 1) instanceof Number var2 ? var2.intValue() : -1;
      String var13 = "minecraft:air";
      int var12 = 0;
      Map var4 = Map.of();
      int var5 = 0;

      for (Entry var7 : args.entrySet()) {
         String var8 = (String)var7.getKey();
         switch (var8) {
            case "id":
               var13 = (String)var7.getValue();
               break;
            case "count":
               var12 = ((Number)var7.getValue()).intValue();
               break;
            case "components":
               if (!(var7.getValue() instanceof Map var11)) {
                  throw new IllegalArgumentException("components must be a Map");
               }

               var4 = var11;
               break;
            case "DataVersion":
               var5 = ((Number)var7.getValue()).intValue();
         }
      }

      return new CraftItemStack(var13, var12, var4, var5, var3);
   }

   @Override
   public Map<String, Object> serialize() {
      LinkedHashMap var1 = new LinkedHashMap();
      var1.put("id", this.AI);
      var1.put("count", this.qm);
      var1.put("components", this.AH);
      var1.put("DataVersion", this.AK);
      var1.put("schema_version", this.AJ);
      return var1;
   }

   public ItemStack buildDisplay() {
      return this.AL.copy();
   }

   public CraftItemStack(String item, int count, Map<String, String> compoundTag, int dataVersion, int version) {
      this.AI = item;
      this.qm = count;
      this.AK = dataVersion;
      this.AH = compoundTag;
      this.AL = this.buildDisplay0(compoundTag);
   }

   private ItemStack buildDisplay0(Map<String, String> tag) {
      if (!Objects.equals(this.AI, "minecraft:air")) {
         try {
            Item var2 = (Item)Registries.ITEM.get(Identifier.tryParse(this.AI));
            var2 = var2 == Items.AIR ? Items.BARRIER : var2;
            ItemStack var3 = new ItemStack(var2, this.qm);
            NbtCompound var4 = new NbtCompound();

            for (Entry var6 : tag.entrySet()) {
               try {
                  NbtElement var7 = VNbt.getInstance().d((String)var6.getValue());
                  var4.put((String)var6.getKey(), var7);
               } catch (Throwable var8) {
               }
            }

            ComponentChanges var11 = CODEC_DISPLAY_CHANGES.decode(ItemStackUtils.registry().getOps(NbtOps.INSTANCE), var4)
               .result()
               .<ComponentChanges>map(Pair::getFirst)
               .orElse(ComponentChanges.EMPTY);
            var3.applyChanges(var11);
            return var3;
         } catch (Throwable var9) {
            return new ItemStack(Items.BARRIER);
         }
      } else {
         return ItemStack.EMPTY;
      }
   }
}
