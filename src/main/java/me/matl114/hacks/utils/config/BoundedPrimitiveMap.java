package me.matl114.hacks.utils.config;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JavaOps;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import me.matl114.managers.config.NBTType;
import me.matl114.utils.CodecUtils;
import me.matl114.utils.config.WidgetFactory;
import me.matl114.utils.config.WrapperFactory;
import me.matl114.utils.config.kv.AttrKeyValues;
import me.matl114.utils.config.kv.WrapperAttrKeyValue;
import org.apache.commons.lang3.function.TriFunction;

public class BoundedPrimitiveMap<W, T> {
   protected final Map<W, T> map;

   public BoundedPrimitiveMap(List<W> keys, Map<W, T> map, NBTType<T> type) {
      this.map = new LinkedHashMap<>(map.size());

      for (Object var5 : keys) {
         Object var6 = map.get(var5);
         if (var6 == null) {
            var6 = type.createEmpty();
         }

         this.map.put((W)var5, (T)var6);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(this.map);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else {
         return object instanceof BoundedPrimitiveMap var2 ? Objects.equals(this.map, var2.map) : false;
      }
   }

   public Map<W, T> toMap() {
      return this.map;
   }

   public static <S, T, W extends BoundedPrimitiveMap<S, T>> NBTType<W> create(
      String what,
      TriFunction<List<S>, Map<S, T>, NBTType<T>, W> creator,
      List<S> baseLookup,
      Codec<S> keyCodec,
      WidgetFactory<S> keyWidget,
      NBTType<T> ptype,
      int keyLabelWidth,
      int listWidth,
      int listHeight
   ) {
      WrapperFactory var9 = WrapperFactory.of(map -> (BoundedPrimitiveMap)creator.apply(baseLookup, map, ptype), BoundedPrimitiveMap::toMap);
      WrapperFactory var10 = WrapperFactory.fromCodec(keyCodec, JavaOps.INSTANCE);
      return (NBTType<W>)(new NBTType<>(
         what,
         CodecUtils.arrayMapCodec(Codec.STRING, ptype.typeCodec()).xmap(map -> {
            LinkedHashMap var2 = new LinkedHashMap(map.size());

            for (Entry var4 : map.entrySet()) {
               DataResult var5 = keyCodec.decode(JavaOps.INSTANCE, var4.getKey());
               if (var5.isSuccess()) {
                  var2.put(((Pair)var5.getOrThrow()).getFirst(), var4.getValue());
               }
            }

            return var2;
         }, map -> {
            LinkedHashMap var2 = new LinkedHashMap(map.size());

            for (Entry var4 : ((java.util.Set<Entry>)(map).entrySet())) {
               DataResult var5 = keyCodec.encodeStart(JavaOps.INSTANCE, var4.getKey());
               if (var5.isSuccess()) {
                  var2.put((String)var5.getOrThrow(), var4.getValue());
               }
            }

            return var2;
         }).xmap(map -> (BoundedPrimitiveMap)creator.apply(baseLookup, map, ptype), BoundedPrimitiveMap::toMap),
         (w, x, y, dx, dy) -> NBTTypes.generateBoundedListModifyButton(
            new WrapperAttrKeyValue<>(w, var9), baseLookup, ptype, keyWidget, x, y, dx, dy, keyLabelWidth, listWidth, listHeight
         ),
         AttrKeyValues.STR_MAP_FACTORY.concat(WrapperFactory.map(var10, ptype.stringifyFactory())).concat(var9),
         (BoundedPrimitiveMap)creator.apply(baseLookup, Map.of(), ptype)
      ));
   }
}
