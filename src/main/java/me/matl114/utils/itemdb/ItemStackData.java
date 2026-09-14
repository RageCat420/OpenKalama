package me.matl114.utils.itemdb;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import me.matl114.utils.CustomItemStackBuilder;
import me.matl114.utils.ItemStackUtils;
import me.matl114.versioned.api.VItem;
import me.matl114.versioned.api.VNbt;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.NbtOps;

public interface ItemStackData {
   ItemStackData Iz = new KalamaHelperHelperE();
   ItemStack Ix = CustomItemStackBuilder.a().type(Items.BARRIER).amount(1).e("&c物品解析失败").g().i("").i("&7详细信息请检查日志").endLore().build();
   Codec<ItemStackData> dt = Codec.PASSTHROUGH.xmap(dynamic -> {
      JsonElement var1 = (JsonElement)dynamic.convert(JsonOps.INSTANCE).getValue();
      return (ItemStackData)(var1.isJsonNull() ? Iz : new KalamaHelperHelperD(var1));
   }, data -> {
      JsonElement var1 = data.gb();
      return new Dynamic(JsonOps.INSTANCE, var1);
   });
   ItemStack Iy = CustomItemStackBuilder.a().type(Items.STRUCTURE_VOID).amount(1).e("&c物品索引缺失").g().i("").i("&7请修复item-database.json").endLore().build();

   JsonElement gb();

   ItemStack gf();

   ItemStack ge();

   void resolveItemStack();

   boolean isValid();

   public static ItemStackData wrapCopy(ItemStack stack) {
      if (stack.isEmpty()) {
         return Iz;
      } else {
         stack = stack.copyWithCount(1);
         JsonElement var1 = serialize(stack);
         return new KalamaHelperHelperD(var1, stack);
      }
   }

   public static ItemStackData Xa(ItemStack stack) {
      return (ItemStackData)(stack.isEmpty() ? Iz : new KalamaHelperHelperC(stack.copyWithCount(1)));
   }

   public static ItemStack deserialize(JsonElement json) {
      if (json.isJsonObject()) {
         JsonObject var6 = json.getAsJsonObject();

         try {
            ItemStack var7 = (ItemStack)((Pair)VItem.w().x().decode(ItemStackUtils.registry().getOps(JsonOps.INSTANCE), var6).getOrThrow()).getFirst();
            return var7.isEmpty() ? ItemStack.EMPTY : var7;
         } catch (Throwable var4) {
            throw new JsonParseException(var4);
         }
      } else {
         String var1 = json.getAsString();

         try {
            NbtCompound var2 = (NbtCompound)VNbt.getInstance().c(var1);
            ItemStack var3 = (ItemStack)((Pair)VItem.w().x().decode(ItemStackUtils.registry().getOps(NbtOps.INSTANCE), var2).getOrThrow()).getFirst();
            return var3.isEmpty() ? ItemStack.EMPTY : var3;
         } catch (Throwable var5) {
            throw new NbtException(var5.getMessage());
         }
      }
   }

   public static JsonElement serialize(ItemStack stack) {
      if (stack.isEmpty()) {
         return JsonNull.INSTANCE;
      } else {
         NbtCompound var1 = VItem.w().k(stack, ItemStackUtils.registry());
         return new JsonPrimitive(VNbt.getInstance().b(var1));
      }
   }

   public static ItemStackData wrapRaw(ItemStack stack) {
      return (ItemStackData)(stack.isEmpty() ? Iz : new KalamaHelperHelperC(stack));
   }

}
