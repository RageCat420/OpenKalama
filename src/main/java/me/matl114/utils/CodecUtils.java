package me.matl114.utils;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

public class CodecUtils {
   public static <T, W> Codec<Map<T, W>> arrayMapCodec(Codec<T> keyCodec, Codec<W> valueCodec) {
      return Codec.list(pairCodec(keyCodec, "key", valueCodec, "value")).xmap(lst -> {
         LinkedHashMap var1 = new LinkedHashMap();
         lst.forEach(p -> var1.put(p.getFirst(), p.getSecond()));
         return var1;
      }, mp -> mp.entrySet().stream().map(v -> Pair.of(v.getKey(), v.getValue())).toList());
   }

   public static <T, W> Codec<List<Pair<T, W>>> pairListCodec(Codec<T> keyCodec, Codec<W> valueCodec) {
      return Codec.list(pairCodec(keyCodec, "key", valueCodec, "value"));
   }

   public static <T extends Enum<T>> Codec<T> enumCodec(Class<T> clazz) {
      HashMap var1 = new HashMap();

      for (Enum var5 : (Enum[])clazz.getEnumConstants()) {
         var1.put(var5.name().toLowerCase(Locale.ROOT), var5);
      }

      return finiteMapCodec(var1, Enum::name);
   }

   public static <T> Codec<T> finiteMapCodec(Map<String, T> map, Function<T, String> stringFunction) {
      LinkedHashMap var2 = new LinkedHashMap(map.size());
      Object var3 = null;

      for (Entry var5 : map.entrySet()) {
         if (var3 == null) {
            var3 = var5.getValue();
         }

         var2.put(((String)var5.getKey()).toLowerCase(Locale.ROOT), var5.getValue());
      }

      return Codec.STRING.comapFlatMap(str -> {
         String var2x = str.toLowerCase(Locale.ROOT);
         return var2.containsKey(var2x) ? DataResult.success(var2.get(var2x)) : DataResult.error(() -> "Not in enum directory");
      }, stringFunction);
   }

   public static <T, W> Codec<Pair<T, W>> pairCodec(Codec<T> firstCodec, String firstName, Codec<W> secondCodec, String secondName) {
      return RecordCodecBuilder.create(
         instance -> instance.group(firstCodec.fieldOf(firstName).forGetter(Pair::getFirst), secondCodec.fieldOf(secondName).forGetter(Pair::getSecond))
            .apply(instance, Pair::of)
      );
   }
}
