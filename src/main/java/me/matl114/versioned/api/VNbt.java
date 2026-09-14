package me.matl114.versioned.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import me.matl114.versioned.impl.Nbt_v1_21_1;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;

public interface VNbt {
   Codec<NbtElement> a = Codec.PASSTHROUGH.comapFlatMap(dynamic -> {
      NbtElement var1 = (NbtElement)dynamic.convert(NbtOps.INSTANCE).getValue();
      return DataResult.success(var1 == dynamic.getValue() ? var1.copy() : var1);
   }, nbt -> new Dynamic(NbtOps.INSTANCE, nbt.copy()));
   VNbt INSTANCE = new Nbt_v1_21_1();

   NbtElement d(String var1);

   NbtElement c(String var1);

   static VNbt getInstance() {
      return INSTANCE;
   }

   String b(NbtElement var1);

   default String b(NbtElement var1) { return null; }

}
