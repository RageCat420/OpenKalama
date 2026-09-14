package me.matl114.hacks.utils.config;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import me.matl114.managers.config.NBTParsable;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.config.NBTType;
import me.matl114.managers.config.Ref;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.PairLikeFactory;
import me.matl114.utils.config.WrapperFactory;
import me.matl114.utils.config.kv.AttrKeyValues;
import me.matl114.utils.config.kv.TypeConvertAttrKeyValue;
import net.minecraft.text.Text;

public class PrimitivePairList<T, W> implements NBTParsable<PrimitivePairList<T, W>> {
   final NBTType<T> firstType;
   final NBTType<W> secondType;
   final String firstName;
   final String secondName;
   final List<Pair<T, W>> list;
   final Optional<Primitive<T>> defaultFirstPrimitive;
   final Optional<Primitive<W>> defaultSecondPrimitive;
   List<Pair<Primitive<T>, Primitive<W>>> originValue;
   NBTType<List<Pair<Primitive<T>, Primitive<W>>>> cachedEntryType;
   public static final NBTType<PrimitivePairList<Object, Object>> TYPE = create();

   public PrimitivePairList(String firstName, String secondName, NBTType<T> firstType, NBTType<W> secondType, List<Pair<T, W>> list) {
      this(firstName, secondName, firstType, secondType, list, Optional.empty(), Optional.empty());
   }

   public PrimitivePairList(
      String firstName,
      String secondName,
      NBTType<T> firstType,
      NBTType<W> secondType,
      List<Pair<T, W>> list,
      Optional<Primitive<T>> defaultFirstPrimitive,
      Optional<Primitive<W>> defaultSecondPrimitive
   ) {
      this.firstName = firstName;
      this.secondName = secondName;
      this.firstType = firstType;
      this.secondType = secondType;
      this.list = new ArrayList<>(list);
      this.defaultFirstPrimitive = defaultFirstPrimitive.map(entry -> {
         Preconditions.checkArgument(entry.valueType() == firstType);
         return entry;
      });
      this.defaultSecondPrimitive = defaultSecondPrimitive.map(entry -> {
         Preconditions.checkArgument(entry.valueType() == secondType);
         return entry;
      });
   }

   public PrimitivePairList(
      String firstName, String secondName, List<Pair<Primitive<T>, Primitive<W>>> primitiveList, NBTType<T> firstType, NBTType<W> secondType
   ) {
      this(firstName, secondName, primitiveList, firstType, secondType, Optional.empty(), Optional.empty());
   }

   public PrimitivePairList(
      String firstName,
      String secondName,
      List<Pair<Primitive<T>, Primitive<W>>> primitiveList,
      NBTType<T> firstType,
      NBTType<W> secondType,
      Optional<Primitive<T>> defaultFirstPrimitive,
      Optional<Primitive<W>> defaultSecondPrimitive
   ) {
      this.firstName = firstName;
      this.secondName = secondName;
      this.firstType = firstType;
      this.secondType = secondType;
      this.originValue = primitiveList;
      this.list = new ArrayList<>(primitiveList.size());

      for (Pair<Primitive<T>, Primitive<W>> entry : primitiveList) {
         Primitive<T> first = (Primitive<T>)entry.getFirst();
         Primitive<W> second = (Primitive<W>)entry.getSecond();
         Preconditions.checkArgument(first.valueType() == firstType);
         Preconditions.checkArgument(second.valueType() == secondType);
         this.list.add(Pair.of(first.value(), second.value()));
      }

      this.defaultFirstPrimitive = defaultFirstPrimitive.map(entryx -> {
         Preconditions.checkArgument(entryx.valueType() == firstType);
         return entryx;
      });
      this.defaultSecondPrimitive = defaultSecondPrimitive.map(entryx -> {
         Preconditions.checkArgument(entryx.valueType() == secondType);
         return entryx;
      });
   }

   public List<Pair<Primitive<T>, Primitive<W>>> toPrimitivePairList() {
      if (this.originValue == null) {
         List<Pair<Primitive<T>, Primitive<W>>> cached = new ArrayList<>();

         for (Pair<T, W> entry : this.list) {
            cached.add(Pair.of(Primitive.of(this.firstType, (T)entry.getFirst()), Primitive.of(this.secondType, (W)entry.getSecond())));
         }

         this.originValue = cached;
      }

      return this.originValue;
   }

   private Primitive<T> copyFirstPrimitive(Primitive<T> primitive) {
      return Primitive.of(primitive.valueType(), primitive.valueType().parse(primitive.valueType().toNbt(primitive.value())));
   }

   private Primitive<W> copySecondPrimitive(Primitive<W> primitive) {
      return Primitive.of(primitive.valueType(), primitive.valueType().parse(primitive.valueType().toNbt(primitive.value())));
   }

   public Pair<Primitive<T>, Primitive<W>> createNewPrimitivePair() {
      return Pair.of(
         this.defaultFirstPrimitive.map(this::copyFirstPrimitive).orElseGet(() -> Primitive.of(this.firstType, this.firstType.createEmpty())),
         this.defaultSecondPrimitive.map(this::copySecondPrimitive).orElseGet(() -> Primitive.of(this.secondType, this.secondType.createEmpty()))
      );
   }

   public static final <T, W> NBTType<PrimitivePairList<T, W>> create() {
      NBTType<Pair<Primitive<T>, Primitive<W>>> pairCodecType = NBTTypes.createPairLike(
         "pair",
         Primitive.TYPE.cast(),
         "first",
         Primitive.TYPE.cast(),
         "second",
         PairLikeFactory.of(Pair::of, Pair::getFirst, Pair::getSecond),
         AttrKeyValue.CustomWidgetFactory.cutSizeXLeft(0.5),
         AttrKeyValue.CustomWidgetFactory.cutSizeXRight(0.5)
      );
      Codec<Pair<Primitive<T>, Primitive<W>>> pairCodec = pairCodecType.typeCodec();
      Codec<Primitive<T>> firstPrimitiveCodec = Primitive.TYPE.<Primitive<T>>cast().typeCodec();
      Codec<Primitive<W>> secondPrimitiveCodec = Primitive.TYPE.<Primitive<W>>cast().typeCodec();
      return new NBTType<>(
         "primitivepairlist",
         RecordCodecBuilder.create(
            instance -> instance.group(
                  Codec.STRING.optionalFieldOf("first_name", "").forGetter(PrimitivePairList::firstName),
                  Codec.STRING.optionalFieldOf("second_name", "").forGetter(PrimitivePairList::secondName),
                  Codec.list(pairCodec).fieldOf("data").forGetter(PrimitivePairList::toPrimitivePairList),
                  NBTTypes.codec().fieldOf("first_type").forGetter(PrimitivePairList::firstType),
                  NBTTypes.codec().fieldOf("second_type").forGetter(PrimitivePairList::secondType),
                  firstPrimitiveCodec.optionalFieldOf("default_first_primitive").forGetter(PrimitivePairList::defaultFirstPrimitive),
                  secondPrimitiveCodec.optionalFieldOf("default_second_primitive").forGetter(PrimitivePairList::defaultSecondPrimitive)
               )
               .apply(instance, PrimitivePairList::new)
         ),
         (attr, x, y, dx, dy) -> {
            PrimitivePairList<T, W> pairList = (PrimitivePairList<T, W>)attr.getOriginValue();
            String firstName = pairList.firstName();
            String secondName = pairList.secondName();
            NBTType<Pair<Primitive<T>, Primitive<W>>> pairType = NBTTypes.createPairLike(
               "pair",
               Primitive.TYPE.cast(),
               "first",
               Primitive.TYPE.cast(),
               "second",
               PairLikeFactory.of(Pair::of, Pair::getFirst, Pair::getSecond),
               w -> AttrKeyValue.CustomWidgetFactory.cutSizeXLeft(0.5)
                  .apply(AttrKeyValue.CustomWidgetFactory.withLabel(Text.translatableWithFallback(firstName, firstName)).apply(w)),
               w -> AttrKeyValue.CustomWidgetFactory.cutSizeXRight(0.5)
                  .apply(AttrKeyValue.CustomWidgetFactory.withLabel(Text.translatableWithFallback(secondName, secondName)).apply(w))
            );
            AttrKeyValue.CustomWidgetFactory<List<Pair<Primitive<T>, Primitive<W>>>> widgetFactory = (w1, x1, y1, dx1, dy1) -> NBTTypes.generateListModifyButton(
               w1, pairType, pairList::createNewPrimitivePair, x1, y1, dx1, dy1, 300, 20
            );
            WrapperFactory<String, List<Pair<Primitive<T>, Primitive<W>>>> stringListWrapperFactory = AttrKeyValues.STR_LIST_FACTORY
               .concat(WrapperFactory.list(pairType.stringifyFactory()));
            WrapperFactory<List<Pair<Primitive<T>, Primitive<W>>>, PrimitivePairList<T, W>> wrapperFactory = WrapperFactory.of(
               mp -> new PrimitivePairList<>(
                  firstName, secondName, mp, pairList.firstType, pairList.secondType, pairList.defaultFirstPrimitive, pairList.defaultSecondPrimitive
               ),
               PrimitivePairList::toPrimitivePairList
            );
            return new TypeConvertAttrKeyValue<>(attr, wrapperFactory, widgetFactory, stringListWrapperFactory).generateValueWidget(x, y, dx, dy);
         },
         new PrimitivePairList<>("", "", (NBTType<T>)NBTTypes.g, (NBTType<W>)NBTTypes.g, List.of())
      );
   }

   public static <T, W> Class<PrimitivePairList<T, W>> uA() {
      return (Class<PrimitivePairList<T, W>>)(Class<?>)PrimitivePairList.class;
   }

   @Override
   public NBTType<PrimitivePairList<T, W>> type() {
      return TYPE.cast();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else {
         return !(object instanceof PrimitivePairList<?, ?> that)
            ? false
            : Objects.equals(this.firstType, that.firstType)
               && Objects.equals(this.secondType, that.secondType)
               && Objects.equals(this.list, that.list)
               && Objects.equals(this.defaultFirstPrimitive, that.defaultFirstPrimitive)
               && Objects.equals(this.defaultSecondPrimitive, that.defaultSecondPrimitive);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.firstType, this.secondType, this.list, this.defaultFirstPrimitive, this.defaultSecondPrimitive);
   }

   @Override
   public boolean isSameType(NBTParsable<?> type) {
      return NBTParsable.super.isSameType(type)
         && type instanceof PrimitivePairList pairList
         && pairList.firstType == this.firstType
         && pairList.secondType == this.secondType
         && Objects.equals(pairList.firstName, this.firstName)
         && Objects.equals(pairList.secondName, this.secondName);
   }

   @Override
   public <R> Optional<PrimitivePairList<T, W>> tryTypeConvert(Ref<R> ref) {
      return ref instanceof NBTRef nbt
            && nbt.get() instanceof PrimitivePairList<?, ?> pairList
            && pairList.firstType == this.firstType
            && pairList.secondType == this.secondType
         ? Optional.of(
            new PrimitivePairList<>(
               this.firstName,
               this.secondName,
               this.firstType,
               this.secondType,
               (List<Pair<T, W>>)pairList.list(),
               this.defaultFirstPrimitive,
               this.defaultSecondPrimitive
            )
         )
         : Optional.empty();
   }

   public NBTType<T> firstType() {
      return this.firstType;
   }

   public NBTType<W> secondType() {
      return this.secondType;
   }

   public String firstName() {
      return this.firstName;
   }

   public String secondName() {
      return this.secondName;
   }

   public List<Pair<T, W>> list() {
      return this.list;
   }

   public Optional<Primitive<T>> defaultFirstPrimitive() {
      return this.defaultFirstPrimitive;
   }

   public Optional<Primitive<W>> defaultSecondPrimitive() {
      return this.defaultSecondPrimitive;
   }

   public List<Pair<Primitive<T>, Primitive<W>>> originValue() {
      return this.originValue;
   }

   public NBTType<List<Pair<Primitive<T>, Primitive<W>>>> cachedEntryType() {
      return this.cachedEntryType;
   }
}
