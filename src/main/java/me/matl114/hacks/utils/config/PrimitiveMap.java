package me.matl114.hacks.utils.config;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import me.matl114.managers.config.NBTParsable;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.config.NBTType;
import me.matl114.managers.config.Ref;
import me.matl114.utils.CodecUtils;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.WrapperFactory;
import me.matl114.utils.config.kv.TypeConvertAttrKeyValue;

public class PrimitiveMap<T, W> implements NBTParsable<PrimitiveMap<T, W>> {
   final NBTType<T> keyType;
   final NBTType<W> valueType;
   final Optional<Primitive<T>> defaultKeyPrimitive;
   final Optional<Primitive<W>> defaultValuePrimitive;
   final Map<T, W> map;
   Map<Primitive<T>, Primitive<W>> originValue;
   NBTType<Map<Primitive<T>, Primitive<W>>> cachedEntryType;
   public static final NBTType<PrimitiveMap<Object, Object>> TYPE = create();

   public PrimitiveMap(NBTType<T> keyType, NBTType<W> valueType, Map<T, W> map) {
      this(keyType, valueType, map, Optional.empty(), Optional.empty());
   }

   public PrimitiveMap(NBTType<T> keyType, NBTType<W> valueType, Map<T, W> map, T defaultKeyPrimitive, W defaultValuePrimitive) {
      this(keyType, valueType, map, Optional.of(Primitive.of(keyType, defaultKeyPrimitive)), Optional.of(Primitive.of(valueType, defaultValuePrimitive)));
   }

   protected PrimitiveMap(
      NBTType<T> keyType, NBTType<W> valueType, Map<T, W> map, Optional<Primitive<T>> defaultKeyPrimitive, Optional<Primitive<W>> defaultValuePrimitive
   ) {
      this.keyType = keyType;
      this.valueType = valueType;
      this.map = new LinkedHashMap<>(map);
      this.defaultKeyPrimitive = defaultKeyPrimitive.map(entry -> {
         Preconditions.checkArgument(entry.valueType() == keyType);
         return entry;
      });
      this.defaultValuePrimitive = defaultValuePrimitive.map(entry -> {
         Preconditions.checkArgument(entry.valueType() == valueType);
         return entry;
      });
   }

   public PrimitiveMap(Map<Primitive<T>, Primitive<W>> map, NBTType<T> keyType, NBTType<W> valueType) {
      this(map, keyType, valueType, Optional.empty(), Optional.empty());
   }

   public PrimitiveMap(
      Map<Primitive<T>, Primitive<W>> map,
      NBTType<T> keyType,
      NBTType<W> valueType,
      Optional<Primitive<T>> defaultKeyPrimitive,
      Optional<Primitive<W>> defaultValuePrimitive
   ) {
      this.keyType = keyType;
      this.valueType = valueType;
      this.originValue = map;
      this.map = new LinkedHashMap<>(map.size());

      for (Entry<Primitive<T>, Primitive<W>> entry : map.entrySet()) {
         Primitive<T> key = entry.getKey();
         Primitive<W> value = entry.getValue();
         Preconditions.checkArgument(key.valueType() == keyType);
         Preconditions.checkArgument(value.valueType() == valueType);
         this.map.put(key.value(), value.value());
      }

      this.defaultKeyPrimitive = defaultKeyPrimitive.map(entryx -> {
         Preconditions.checkArgument(entryx.valueType() == keyType);
         return entryx;
      });
      this.defaultValuePrimitive = defaultValuePrimitive.map(entryx -> {
         Preconditions.checkArgument(entryx.valueType() == valueType);
         return entryx;
      });
   }

   public Map<Primitive<T>, Primitive<W>> toPrimitiveMap() {
      if (this.originValue == null) {
         Map<Primitive<T>, Primitive<W>> cached = new LinkedHashMap<>();

         for (Entry<T, W> entry : this.map.entrySet()) {
            cached.put(Primitive.of(this.keyType, entry.getKey()), Primitive.of(this.valueType, entry.getValue()));
         }

         this.originValue = cached;
      }

      return this.originValue;
   }

   private Primitive<T> copyKeyPrimitive(Primitive<T> value) {
      return Primitive.of(value.valueType(), value.valueType().parse(value.valueType().toNbt(value.value())));
   }

   private Primitive<W> copyValuePrimitive(Primitive<W> value) {
      return Primitive.of(value.valueType(), value.valueType().parse(value.valueType().toNbt(value.value())));
   }

   public Primitive<T> createNewKeyPrimitive() {
      return this.defaultKeyPrimitive.map(this::copyKeyPrimitive).orElseGet(() -> Primitive.of(this.keyType, this.keyType.createEmpty()));
   }

   public Primitive<W> createNewValuePrimitive() {
      return this.defaultValuePrimitive.map(this::copyValuePrimitive).orElseGet(() -> Primitive.of(this.valueType, this.valueType.createEmpty()));
   }

   public static <T, W> NBTType<PrimitiveMap<T, W>> create() {
      Codec<Primitive<T>> keyCodec = Primitive.TYPE.<Primitive<T>>cast().typeCodec();
      Codec<Primitive<W>> valueCodec = Primitive.TYPE.<Primitive<W>>cast().typeCodec();
      Function<PrimitiveMap<T, W>, NBTType<Map<Primitive<T>, Primitive<W>>>> typeGenerator = w -> {
         if (w.cachedEntryType == null) {
            w.cachedEntryType = NBTTypes.createArrayMapLike(
               "primitive_map",
               Primitive.TYPE.cast(),
               w::createNewKeyPrimitive,
               "key",
               Primitive.TYPE.cast(),
               w::createNewValuePrimitive,
               "value",
               WrapperFactory.identity(),
               AttrKeyValue.CustomWidgetFactory.cutSizeXLeft(0.5),
               AttrKeyValue.CustomWidgetFactory.cutSizeXRight(0.5),
               300,
               20
            );
         }

         return w.cachedEntryType;
      };
      return new NBTType<>(
         "primitivemap",
         RecordCodecBuilder.create(
            instance -> instance.group(
                  CodecUtils.arrayMapCodec(keyCodec, valueCodec).fieldOf("data").forGetter(PrimitiveMap::toPrimitiveMap),
                  NBTTypes.codec().fieldOf("key_type").forGetter(PrimitiveMap::keyType),
                  NBTTypes.codec().fieldOf("value_type").forGetter(PrimitiveMap::valueType),
                  keyCodec.optionalFieldOf("default_key_primitive").forGetter(PrimitiveMap::defaultKeyPrimitive),
                  valueCodec.optionalFieldOf("default_value_primitive").forGetter(PrimitiveMap::defaultValuePrimitive)
               )
               .apply(instance, PrimitiveMap::new)
         ),
         (attr, x, y, dx, dy) -> {
            PrimitiveMap<T, W> map = (PrimitiveMap<T, W>)attr.getOriginValue();
            WrapperFactory<Map<Primitive<T>, Primitive<W>>, PrimitiveMap<T, W>> wrapperFactory = WrapperFactory.of(
               mp -> new PrimitiveMap<>(mp, map.keyType, map.valueType, map.defaultKeyPrimitive, map.defaultValuePrimitive), PrimitiveMap::toPrimitiveMap
            );
            return new TypeConvertAttrKeyValue<>(attr, wrapperFactory, typeGenerator.apply(map)).generateValueWidget(x, y, dx, dy);
         },
         new PrimitiveMap<>((NBTType<T>)NBTTypes.g, (NBTType<W>)NBTTypes.g, Map.of())
      );
   }

   public static <T, W> Class<PrimitiveMap<T, W>> classType() {
      return (Class<PrimitiveMap<T, W>>)(Class<?>)PrimitiveMap.class;
   }

   public static <T, W, R extends PrimitiveMap<T, W>> Codec<R> inheritedCodec(Function<PrimitiveMap<T, W>, R> wrapper) {
      return TYPE.cast().typeCodec().xmap(wrapper, map -> map);
   }

   public static <T, W, R extends PrimitiveMap<T, W>> Codec<R> inheritedAlternativeCodec(Function<PrimitiveMap<T, W>, R> wrapper, Codec<R> legacyCodec) {
      return Codec.withAlternative(inheritedCodec(wrapper), legacyCodec);
   }

   public static <T, W, R extends PrimitiveMap<T, W>> AttrKeyValue.CustomWidgetFactory<R> inheritedWidgetFactory(Function<PrimitiveMap<T, W>, R> wrapper) {
      NBTType<PrimitiveMap<T, W>> delegateType = TYPE.cast();
      WrapperFactory<PrimitiveMap<T, W>, R> wrapperFactory = WrapperFactory.of(wrapper, map -> map);
      return (attr, x, y, dx, dy) -> new TypeConvertAttrKeyValue<>(attr, wrapperFactory, delegateType).generateValueWidget(x, y, dx, dy);
   }

   @Override
   public NBTType<PrimitiveMap<T, W>> type() {
      return TYPE.cast();
   }

   protected PrimitiveMap<T, W> withDefault(Map<T, W> map, Optional<Primitive<T>> defaultKeyPrimitive, Optional<Primitive<W>> defaultValuePrimitive) {
      return new PrimitiveMap<>(this.keyType, this.valueType, map, defaultKeyPrimitive, defaultValuePrimitive);
   }

   @Override
   public <R> Optional<PrimitiveMap<T, W>> tryTypeConvert(Ref<R> ref) {
      return ref instanceof NBTRef<?> nbt
            && nbt.get() instanceof PrimitiveMap<?, ?> primitiveMap
            && primitiveMap.keyType == this.keyType
            && primitiveMap.valueType == this.valueType
         ? Optional.of(this.withDefault((Map<T, W>)primitiveMap.map(), this.defaultKeyPrimitive, this.defaultValuePrimitive))
         : Optional.empty();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else {
         return !(object instanceof PrimitiveMap<?, ?> that)
            ? false
            : Objects.equals(this.keyType, that.keyType)
               && Objects.equals(this.valueType, that.valueType)
               && Objects.equals(this.defaultKeyPrimitive, that.defaultKeyPrimitive)
               && Objects.equals(this.defaultValuePrimitive, that.defaultValuePrimitive)
               && Objects.equals(this.map, that.map);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.keyType, this.valueType, this.defaultKeyPrimitive, this.defaultValuePrimitive, this.map);
   }

   @Override
   public boolean isSameType(NBTParsable<?> type) {
      return NBTParsable.super.isSameType(type)
         && type instanceof PrimitiveMap<?, ?> primitiveMap
         && primitiveMap.keyType == this.keyType
         && primitiveMap.valueType == this.valueType
         && Objects.equals(primitiveMap.defaultKeyPrimitive, this.defaultKeyPrimitive)
         && Objects.equals(primitiveMap.defaultValuePrimitive, this.defaultValuePrimitive);
   }

   public NBTType<T> keyType() {
      return this.keyType;
   }

   public NBTType<W> valueType() {
      return this.valueType;
   }

   public Optional<Primitive<T>> defaultKeyPrimitive() {
      return this.defaultKeyPrimitive;
   }

   public Optional<Primitive<W>> defaultValuePrimitive() {
      return this.defaultValuePrimitive;
   }

   public Map<T, W> map() {
      return this.map;
   }

   public Map<Primitive<T>, Primitive<W>> originValue() {
      return this.originValue;
   }

   public NBTType<Map<Primitive<T>, Primitive<W>>> cachedEntryType() {
      return this.cachedEntryType;
   }
}
