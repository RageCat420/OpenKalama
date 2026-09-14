package me.matl114.hacks.utils.config;

import com.google.common.base.Preconditions;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import me.matl114.managers.config.NBTType;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.WrapperFactory;
import me.matl114.utils.config.kv.TypeConvertAttrKeyValue;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class WeakEntryPrimitiveMap<T, W> extends PrimitiveMap<WeakHolder<T>, W> {
   public static final NBTType<WeakEntryPrimitiveMap<Object, Object>> TYPE = createEntry();
   public static final Identifier ru = new Identifier("minecraft", "default");

   public Map<Identifier, W> Au() {
      return this.map().entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().location(), entry -> entry.getValue()));
   }

   @Override
   protected PrimitiveMap<WeakHolder<T>, W> withDefault(
      Map<WeakHolder<T>, W> map, Optional<Primitive<WeakHolder<T>>> defaultKeyPrimitive, Optional<Primitive<W>> defaultValuePrimitive
   ) {
      Identifier var4 = this.registry();
      return new WeakEntryPrimitiveMap<>(var4, this.valueType(), defaultValuePrimitive, map);
   }

   @Nullable
   public W getOrDefault(RegistryKey<T> value) {
      return this.Av(value.getValue());
   }

   public WeakEntryPrimitiveMap(Map<WeakHolder<T>, Primitive<W>> map, Identifier registry, NBTType<W> type) {
      this(map, registry, type, Optional.empty());
   }

   public Map<RegistryKey<T>, W> keyMap() {
      RegistryKey var1 = RegistryKey.ofRegistry(this.registry());
      return this.map().entrySet().stream().collect(Collectors.toMap(entry -> RegistryKey.of(var1, entry.getKey().location()), Entry::getValue));
   }

   private static <T, W> Optional<Primitive<W>> resolveDefaultPrimitive(
      Map<WeakHolder<T>, Primitive<W>> map, Identifier registry, NBTType<W> type, Optional<Primitive<W>> defaultPrimitive
   ) {
      Primitive var4 = null;

      for (Entry var6 : map.entrySet()) {
         WeakHolder var7 = (WeakHolder)var6.getKey();
         Primitive var8 = (Primitive)var6.getValue();
         Preconditions.checkArgument(Objects.equals(registry, var7.registry()));
         Preconditions.checkArgument(var8.valueType() == type);
         if (Objects.equals(var7.location(), ru)) {
            var4 = var8;
         }
      }

      Primitive var9 = var4;
      return defaultPrimitive.<Primitive<W>>map(entry -> {
         Preconditions.checkArgument(entry.valueType() == type);
         return (Primitive<W>)entry;
      }).or(() -> Optional.ofNullable((Primitive<W>)var9));
   }

   public Identifier registry() {
      return this.defaultKeyPrimitive().map(Primitive::value).map(WeakHolder::registry).orElse(null);
   }

   public W getOrWithDefault(RegistryKey<T> value, W fallback) {
      return this.Ay(value.getValue(), (W)fallback);
   }

   public WeakEntryPrimitiveMap(Identifier registry, NBTType<W> type, Map<Identifier, W> map, Optional<Primitive<W>> defaultPrimitive) {
      this(registry, type, defaultPrimitive, Ao(registry, map));
   }

   private static <T, W> Map<WeakHolder<T>, W> Ao(Identifier registry, Map<Identifier, W> map) {
      LinkedHashMap var2 = new LinkedHashMap(map.size());

      for (Entry var4 : map.entrySet()) {
         var2.put(new WeakHolder(registry, (Identifier)var4.getKey()), var4.getValue());
      }

      return var2;
   }

   private static <T, W> WrapperFactory<PrimitiveMap<WeakHolder<T>, W>, WeakEntryPrimitiveMap<T, W>> uI() {
      return WrapperFactory.of(WeakEntryPrimitiveMap::As, map -> map);
   }

   public static <T, W> NBTType<WeakEntryPrimitiveMap<T, W>> createEntry() {
      WrapperFactory var0 = uI();
      NBTType var1 = PrimitiveMap.TYPE.cast();
      AttrKeyValue.CustomWidgetFactory var2 = (attr, x, y, dx, dy) -> var1.customWidgetFactory()
         .generateWidget(new TypeConvertAttrKeyValue<>(attr, var0, var1), x, y, dx, dy);
      return new NBTType<>(
         "weakentryprimitivemap",
         var0.wrapCodecXmap(var1.typeCodec()),
         var2,
         new WeakEntryPrimitiveMap<>(Map.of(), RegistryKeys.BLOCK.getValue(), (NBTType<W>)NBTTypes.g)
      );
   }

   private static <T, W> Map<WeakHolder<T>, Primitive<W>> uG(PrimitiveMap<WeakHolder<T>, W> map) {
      LinkedHashMap var1 = new LinkedHashMap();

      for (Entry var3 : map.map().entrySet()) {
         var1.put((WeakHolder)var3.getKey(), Primitive.of(map.valueType(), var3.getValue()));
      }

      return var1;
   }

   @Override
   public boolean equals(Object object) {
      return this == object || object instanceof WeakEntryPrimitiveMap var2 && super.equals(var2);
   }

   @Nullable
   public W Ax(RegistryEntry<T> value) {
      return value.getKey().map(this::getOrDefault).orElse(null);
   }

   public static <T, W> Class<WeakEntryPrimitiveMap<T, W>> uA() {
      return (Class<WeakEntryPrimitiveMap<T, W>>)(Class<?>)WeakEntryPrimitiveMap.class;
   }

   private static <T, W> Identifier Ar(PrimitiveMap<WeakHolder<T>, W> map) {
      return map.defaultKeyPrimitive()
         .map(Primitive::value)
         .map(WeakHolder::registry)
         .or(() -> map.map().keySet().stream().findFirst().map(WeakHolder::registry))
         .orElseThrow(() -> new IllegalArgumentException("Can not resolve registry from PrimitiveMap"));
   }

   public W Ay(Identifier value, W fallback) {
      Object var3 = this.Av(value);
      return (W)(var3 == null ? fallback : var3);
   }

   private static <T> Optional<Primitive<WeakHolder<T>>> An(Identifier registry) {
      return Optional.of(Primitive.of(WeakHolder.TYPE.cast(), new WeakHolder<>(registry, ru)));
   }

   @Nullable
   public W Av(Identifier value) {
      Map var2 = this.map();
      Object var3 = var2.get(new WeakHolder(this.registry(), value));
      return (W)(var3 != null ? var3 : var2.get(new WeakHolder(this.registry(), WeakHolder.ru)));
   }

   private static <T, W> WeakEntryPrimitiveMap<T, W> As(PrimitiveMap<WeakHolder<T>, W> map) {
      return new WeakEntryPrimitiveMap<>(uG(map), Ar(map), map.valueType(), map.defaultValuePrimitive());
   }

   public WeakEntryPrimitiveMap(RegistryKey<? extends Registry<T>> registry, NBTType<W> type, Map<Identifier, W> map, W defaultValue) {
      this(registry.getValue(), type, map, Optional.ofNullable(defaultValue).map(value -> Primitive.of(type, value)));
   }

   @Override
   public NBTType<PrimitiveMap<WeakHolder<T>, W>> type() {
      return TYPE.cast();
   }

   public WeakEntryPrimitiveMap(RegistryKey<? extends Registry<T>> registry, NBTType<W> type, Map<Identifier, W> map) {
      this(registry.getValue(), type, map, Optional.empty());
   }

   private WeakEntryPrimitiveMap(Identifier registry, NBTType<W> type, Optional<Primitive<W>> defaultPrimitive, Map<WeakHolder<T>, W> map) {
      super(WeakHolder.TYPE.cast(), type, map, An(registry), defaultPrimitive);
   }

   public WeakEntryPrimitiveMap(Map<WeakHolder<T>, Primitive<W>> map, Identifier registry, NBTType<W> type, Optional<Primitive<W>> defaultPrimitive) {
      this(registry, type, weakMapToValueMap(map, registry, type), resolveDefaultPrimitive(map, registry, type, defaultPrimitive));
   }

   private static <T, W> Map<Identifier, W> weakMapToValueMap(Map<WeakHolder<T>, Primitive<W>> map, Identifier registry, NBTType<W> type) {
      LinkedHashMap var3 = new LinkedHashMap();

      for (Entry var5 : map.entrySet()) {
         WeakHolder var6 = (WeakHolder)var5.getKey();
         Primitive var7 = (Primitive)var5.getValue();
         Preconditions.checkArgument(Objects.equals(registry, var6.registry()));
         Preconditions.checkArgument(var7.valueType() == type);
         if (!Objects.equals(var6.location(), ru)) {
            var3.put(var6.location(), var7.value());
         }
      }

      return var3;
   }
}
