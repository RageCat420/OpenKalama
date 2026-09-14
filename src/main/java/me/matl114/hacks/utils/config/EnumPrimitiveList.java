package me.matl114.hacks.utils.config;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import me.matl114.managers.config.ConfigEnum;
import me.matl114.managers.config.NBTParsable;
import me.matl114.managers.config.NBTType;
import me.matl114.utils.CodecUtils;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.PairLikeFactory;
import me.matl114.utils.config.WrapperFactory;
import me.matl114.utils.config.kv.TypeConvertAttrKeyValue;

public class EnumPrimitiveList<T extends ConfigEnum, W> implements NBTParsable<EnumPrimitiveList<T, W>> {
   String type;
   boolean resolve = false;
   NBTType<W> primitiveType;
   List<EnumPrimitiveList<T, W>.LazyEntry> lazyEntryList = new ArrayList<>();
   @SuppressWarnings({"unchecked","rawtypes"})
   public static final NBTType<EnumPrimitiveList<?, ?>> TYPE = (NBTType<EnumPrimitiveList<?, ?>>) (NBTType) create();
   NBTType<List<Pair<T, Primitive<W>>>> cachedEntryType;

   public EnumPrimitiveList(List<Pair<String, Primitive<W>>> lazyEntryList, String type, NBTType<W> primitiveType) {
      this.type = type;
      this.primitiveType = primitiveType;
      this.lazyEntryList = lazyEntryList.stream().<EnumPrimitiveList<T, W>.LazyEntry>map(p -> this.new LazyEntry((String)p.getFirst(), ((Primitive<W>)p.getSecond()).value())).toList();
   }

   public EnumPrimitiveList(Class<T> type, NBTType<W> primitiveType, List<Pair<T, W>> lazyEntryList) {
      this(ConfigEnum.getConfigEnumType(type), primitiveType, lazyEntryList);
      ConfigEnum.ensureRegistered(type);
   }

   public EnumPrimitiveList(String type, NBTType<W> primitiveType, List<Pair<T, W>> lazyEntryList) {
      this.type = type;
      this.tryResolve();
      this.primitiveType = primitiveType;
      this.lazyEntryList = lazyEntryList.stream().<EnumPrimitiveList<T, W>.LazyEntry>map(s -> this.new LazyEntry((T)s.getFirst(), s.getSecond())).toList();
   }

   public static <T extends ConfigEnum, W> EnumPrimitiveList<T, W> of(List<Pair<T, Primitive<W>>> lazyEntryList, String type, NBTType<W> primitiveType) {
      return new EnumPrimitiveList<>(
         type, primitiveType, lazyEntryList.stream().map(s -> Pair.of((T)((ConfigEnum)s.getFirst()), ((Primitive<W>)s.getSecond()).value())).toList()
      );
   }

   public void forEach(BiConsumer<T, W> consumer) {
      if (this.resolve) {
         this.lazyEntryList.forEach(entry -> {
            if (entry.tryResolve()) {
               consumer.accept(entry.lazyValue, entry.value);
            }
         });
      }
   }

   private void tryResolve() {
      if (ConfigEnum.registeredConfigs.containsKey(this.type)) {
         this.resolve = true;
      }
   }

   private List<Pair<String, Primitive<W>>> toPrimitive() {
      return this.lazyEntryList.stream().map(s -> Pair.of(s.name, Primitive.of(this.primitiveType, s.value))).toList();
   }

   private List<Pair<T, Primitive<W>>> toList() {
      return this.lazyEntryList.stream().map(s -> Pair.of(s.lazyValue(), Primitive.of(this.primitiveType, s.value))).toList();
   }

   @Override
   public NBTType<EnumPrimitiveList<T, W>> type() {
      return TYPE.cast();
   }

   public static final <T extends ConfigEnum, W> NBTType<EnumPrimitiveList<T, W>> create() {
      Function<EnumPrimitiveList<T, W>, NBTType<List<Pair<T, Primitive<W>>>>> typeGenerator = w -> {
         if (w.cachedEntryType == null) {
            w.tryResolve();
            Map<String, T> finiteMap = (Map<String, T>)ConfigEnum.registeredConfigs.get(w.type);
            NBTType<Pair<T, Primitive<W>>> pairLikeNbtType = NBTTypes.createPairLike(
               "pair",
               NBTTypes.n("config_enum_lookup", finiteMap, s -> s.cast().name()),
               "key",
               Primitive.TYPE.cast(),
               "value",
               PairLikeFactory.pair(),
               AttrKeyValue.CustomWidgetFactory.cutSizeXLeft(0.5),
               AttrKeyValue.CustomWidgetFactory.cutSizeXRight(0.5)
            );
            w.cachedEntryType = NBTTypes.createListLke("parametered_map", pairLikeNbtType, WrapperFactory.identity(), 300, 20);
         }

         return w.cachedEntryType;
      };
      Codec<EnumPrimitiveList<T, W>> codec = RecordCodecBuilder.create(
         oInstance -> oInstance.group(
               CodecUtils.pairListCodec(Codec.STRING, Primitive.TYPE.cast().typeCodec()).fieldOf("data").forGetter(el -> (List) el.toPrimitive()),
               Codec.STRING.fieldOf("key_type").forGetter(EnumPrimitiveList::getType),
               NBTTypes.<W>codec().fieldOf("value_type").forGetter(EnumPrimitiveList::getPrimitiveType)
            )
            .apply(oInstance, (data, type, primitiveType) -> {
               EnumPrimitiveList<T, W> list = new EnumPrimitiveList<T, W>(java.util.Collections.<Pair<String, Primitive<W>>>emptyList(), type, primitiveType);
               List<Pair<String, Primitive<W>>> dataPairs = (List<Pair<String, Primitive<W>>>) (List<?>) data;
               list.lazyEntryList = dataPairs.stream().map(s -> list.new LazyEntry(s.getFirst(), s.getSecond().value())).collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
               return list;
            })
      );
      return new NBTType<>(
         "enumprimitivelist",
         codec,
         (w, x, y, dx, dy) -> {
            EnumPrimitiveList<T, W> map = (EnumPrimitiveList<T, W>)w.getOriginValue();
            map.tryResolve();
            WrapperFactory<List<Pair<T, Primitive<W>>>, EnumPrimitiveList<T, W>> wrapperFactory = WrapperFactory.of(
               mp -> of(mp, map.getType(), map.getPrimitiveType()), EnumPrimitiveList::toList
            );
            return new TypeConvertAttrKeyValue<>(w, wrapperFactory, typeGenerator.apply(map)).generateValueWidget(x, y, dx, dy);
         },
         new EnumPrimitiveList<>(List.of(), "config_enum", (NBTType<W>)NBTTypes.g)
      );
   }

   @Override
   public boolean isSameType(NBTParsable<?> type) {
      return NBTParsable.super.isSameType(type)
         && type instanceof EnumPrimitiveList enumList
         && Objects.equals(enumList.type, this.type)
         && this.primitiveType == enumList.primitiveType;
   }

   public String getType() {
      return this.type;
   }

   public NBTType<W> getPrimitiveType() {
      return this.primitiveType;
   }

   private class LazyEntry {
      String name;
      T lazyValue;
      W value;

      public LazyEntry(String name, W value) {
         this.name = name;
         this.value = value;
         this.tryResolve();
      }

      public LazyEntry(T key, W value) {
         this.name = key.cast().name();
         this.value = value;
         this.lazyValue = key;
      }

      public boolean tryResolve() {
         if (!EnumPrimitiveList.this.resolve) {
            EnumPrimitiveList.this.tryResolve();
         }

         if (EnumPrimitiveList.this.resolve) {
            if (this.lazyValue == null) {
               Map<String, ConfigEnum> re = ConfigEnum.registeredConfigs.get(EnumPrimitiveList.this.type);
               this.lazyValue = (T)re.get(this.name);
            }

            return this.lazyValue != null;
         } else {
            return false;
         }
      }

      public T lazyValue() {
         this.tryResolve();
         return this.lazyValue;
      }
   }
}
