package me.matl114.hacks.utils.config;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import me.matl114.managers.config.NBTType;
import me.matl114.utils.CodecUtils;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.WrapperFactory;
import me.matl114.utils.config.kv.TypeConvertAttrKeyValue;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class EntryPrimitiveMap<T, W> extends PrimitiveMap<Holder<T>, W> {
   public static final NBTType<EntryPrimitiveMap<Object, Object>> TYPE = createEntry();

   public W getEntryValueOr(T value, W fallback) {
      Object var3 = this.uJ((T)value);
      return (W)(var3 == null ? fallback : var3);
   }

   public EntryPrimitiveMap(Registry<T> registry, NBTType<W> type, Map<T, W> map) {
      this(registry, type, map, Optional.empty());
   }

   @Override
   public NBTType<PrimitiveMap<Holder<T>, W>> type() {
      return TYPE.cast();
   }

   private static <T, W> Map<T, W> holderMapToValueMap(Map<Holder<T>, Primitive<W>> map, Registry<T> registry, NBTType<W> type) {
      LinkedHashMap var3 = new LinkedHashMap();

      for (Entry var5 : map.entrySet()) {
         Holder var6 = (Holder)var5.getKey();
         Primitive var7 = (Primitive)var5.getValue();
         Preconditions.checkArgument(registry.getKey() == var6.registry().getKey());
         Preconditions.checkArgument(var7.valueType() == type);
         if (var6.entry() != null) {
            var3.put(var6.entry(), var7.value());
         }
      }

      return var3;
   }

   private static <T> Optional<Primitive<Holder<T>>> uB(Registry<T> registry) {
      return Optional.of(Primitive.of(Holder.TYPE.cast(), Holder.of(registry, null)));
   }

   private static <T, W> WrapperFactory<PrimitiveMap<Holder<T>, W>, EntryPrimitiveMap<T, W>> uI() {
      return WrapperFactory.of(EntryPrimitiveMap::uH, map -> map);
   }

   public EntryPrimitiveMap(Registry<T> registry, NBTType<W> type, Map<T, W> map, Optional<Primitive<W>> defaultPrimitive) {
      this(registry, type, defaultPrimitive, uC(registry, map));
   }

   @Override
   public boolean equals(Object object) {
      return this == object || object instanceof EntryPrimitiveMap var2 && super.equals(var2);
   }

   public EntryPrimitiveMap(Registry<T> registry, NBTType<W> type, Map<T, W> map, W defaultValue) {
      this(registry, type, map, Optional.ofNullable(defaultValue).map(value -> Primitive.of(type, value)));
   }

   private static <T, W> Registry<T> uF(PrimitiveMap<Holder<T>, W> map) {
      return map.defaultKeyPrimitive()
         .map(Primitive::value)
         .map(Holder::registry)
         .or(() -> map.map().keySet().stream().findFirst().map(Holder::registry))
         .orElseThrow(() -> new IllegalArgumentException("Can not resolve registry from PrimitiveMap"));
   }

   @Override
   protected PrimitiveMap<Holder<T>, W> withDefault(
      Map<Holder<T>, W> map, Optional<Primitive<Holder<T>>> defaultKeyPrimitive, Optional<Primitive<W>> defaultValuePrimitive
   ) {
      Registry var4 = this.registry();
      return new EntryPrimitiveMap<>(var4, this.valueType(), defaultValuePrimitive, map);
   }

   private static <T, W> EntryPrimitiveMap<T, W> uH(PrimitiveMap<Holder<T>, W> map) {
      return new EntryPrimitiveMap<>(uG(map), uF(map), map.valueType(), map.defaultValuePrimitive());
   }

   public static <T, W> Class<EntryPrimitiveMap<T, W>> uA() {
      return (Class<EntryPrimitiveMap<T, W>>)(Class<?>)EntryPrimitiveMap.class;
   }

   private static <T, W> Map<Holder<T>, Primitive<W>> uG(PrimitiveMap<Holder<T>, W> map) {
      LinkedHashMap<Holder<T>, Primitive<W>> var1 = new LinkedHashMap<>();

      for (Entry<Holder<T>, W> var3 : map.map().entrySet()) {
         var1.put(var3.getKey(), Primitive.of(map.valueType(), var3.getValue()));
      }

      return var1;
   }

   public Registry<T> registry() {
      return this.defaultKeyPrimitive().map(Primitive::value).map(Holder::registry).orElse(null);
   }

   private static <T, W> Codec<EntryPrimitiveMap<T, W>> legacyCodec() {
      return RecordCodecBuilder.create(
         instance -> instance.group(
               CodecUtils.arrayMapCodec(Holder.TYPE.<T>cast().typeCodec(), Primitive.TYPE.cast().typeCodec()).fieldOf("data").forGetter(EntryPrimitiveMap::uG),
               Registries.REGISTRIES.getCodec().fieldOf("key_type").forGetter(EntryPrimitiveMap::registry),
               NBTTypes.codec().fieldOf("value_type").forGetter(PrimitiveMap::valueType),
               Primitive.TYPE.cast().typeCodec().optionalFieldOf("default_primitive").forGetter(PrimitiveMap::defaultValuePrimitive)
            )
            .apply(instance, EntryPrimitiveMap::new)
      );
   }

   public EntryPrimitiveMap(Map<Holder<T>, Primitive<W>> map, Registry<T> registry, NBTType<W> type, Optional<Primitive<W>> defaultPrimitive) {
      this(registry, type, holderMapToValueMap(map, registry, type), resolveDefaultPrimitive(map, registry, type, defaultPrimitive));
   }

   protected EntryPrimitiveMap(Registry<T> registry, NBTType<W> type, Optional<Primitive<W>> defaultPrimitive, Map<Holder<T>, W> map) {
      super(Holder.TYPE.cast(), type, map, uB(registry), defaultPrimitive);
   }

   private static <T, W> Optional<Primitive<W>> resolveDefaultPrimitive(
      Map<Holder<T>, Primitive<W>> map, Registry<T> registry, NBTType<W> type, Optional<Primitive<W>> defaultPrimitive
   ) {
      Primitive var4 = null;

      for (Entry var6 : map.entrySet()) {
         Holder var7 = (Holder)var6.getKey();
         Primitive var8 = (Primitive)var6.getValue();
         Preconditions.checkArgument(registry.getKey() == var7.registry().getKey());
         Preconditions.checkArgument(var8.valueType() == type);
         if (var7.entry() == null) {
            var4 = var8;
         }
      }

      Primitive var9 = var4;
      return defaultPrimitive.<Primitive<W>>map(entry -> {
         Preconditions.checkArgument(entry.valueType() == type);
         return (Primitive<W>)entry;
      }).or(() -> Optional.ofNullable((Primitive<W>)var9));
   }

   @Nullable
   public W uJ(T value) {
      Map<T, W> var2 = this.map();
      W var3 = var2.get(Holder.of(this.registry(), value));
      return var3 != null ? var3 : var2.get(Holder.of(this.registry(), null));
   }

   private static <T, W> Map<Holder<T>, W> uC(Registry<T> registry, Map<T, W> map) {
      LinkedHashMap<Holder<T>, W> var2 = new LinkedHashMap<>(map.size());

      for (Entry<T, W> var4 : map.entrySet()) {
         var2.put(Holder.of(registry, var4.getKey()), var4.getValue());
      }

      return var2;
   }

   public static <T, W> NBTType<EntryPrimitiveMap<T, W>> createEntry() {
      WrapperFactory var0 = uI();
      NBTType var1 = PrimitiveMap.TYPE.cast();
      AttrKeyValue.CustomWidgetFactory var2 = (attr, x, y, dx, dy) -> var1.customWidgetFactory()
         .generateWidget(new TypeConvertAttrKeyValue<>(attr, var0, var1), x, y, dx, dy);
      return new NBTType<>(
         "entryprimitivemap",
         Codec.withAlternative(var0.wrapCodecXmap(var1.typeCodec()), legacyCodec()),
         var2,
         new EntryPrimitiveMap<>(Registries.BLOCK, (NBTType<W>)NBTTypes.g, Map.of())
      );
   }
}
