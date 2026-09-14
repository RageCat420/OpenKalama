package me.matl114.hacks.utils.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.KalamaHelperHelperJ;
import me.matl114.managers.config.ConfigEnum;
import me.matl114.managers.config.NBTParsable;
import me.matl114.managers.config.NBTType;
import me.matl114.managers.config.Ref;
import me.matl114.utils.config.PairLikeFactory;
import me.matl114.utils.config.kv.TypeConvertAttrKeyValue;

public class OptionalPrimitive<T> implements NBTParsable<OptionalPrimitive<T>> {
   @SuppressWarnings("unchecked")
   public static final Class<OptionalPrimitive<Double>> DOUBLE_TYPE = (Class<OptionalPrimitive<Double>>)(Class<?>)OptionalPrimitive.class;
   public static final Class<OptionalPrimitive<Integer>> INT_TYPE = (Class<OptionalPrimitive<Integer>>)(Class<?>)OptionalPrimitive.class;
   boolean present;
   NBTType<T> type;
   T value;
   public static final NBTType<OptionalPrimitive> TYPE = new NBTType<>(
      "optionalprimitive",
      RecordCodecBuilder.create(
         oInstance -> oInstance.group(
               Codec.BOOL.fieldOf("present").forGetter(OptionalPrimitive::isPresent),
               Primitive.TYPE.typeCodec().fieldOf("value").forGetter(OptionalPrimitive::getPrimitive)
            )
            .apply(oInstance, OptionalPrimitive::new)
      ),
      (s, x, y, dx, dy) -> {
         NBTType type = ((OptionalPrimitive)s.getOriginValue()).getType();
         PairLikeFactory<Boolean, ?, OptionalPrimitive> factory = PairLikeFactory.of(
            (bool, val) -> new OptionalPrimitive<>(bool, type, val), OptionalPrimitive::isPresent, OptionalPrimitive::getValue
         );
         KalamaHelperHelperCX subScreenWidget = new KalamaHelperHelperCX(x, y, dx, dy);
         subScreenWidget.Q(new TypeConvertAttrKeyValue<>(s, factory.asFirstWrapper(s::getOriginValue), NBTTypes.f).generateValueWidget(0, 0, dy, dy));
         TypeConvertAttrKeyValue<OptionalPrimitive, ?> valueWidget = new TypeConvertAttrKeyValue<>(s, factory.asSecondWrapper(s::getOriginValue), type);
         DrawableWidget widget = valueWidget.generateValueWidget(dy + 2, 0, dx - dy - 2, dy);
         KalamaHelperHelperJ<?> widget2 = new KalamaHelperHelperJ<>(() -> ((OptionalPrimitive)s.getOriginValue()).isPresent() ? widget : null, 0, 0);
         subScreenWidget.Q(widget2);
         return subScreenWidget;
      },
      new OptionalPrimitive<>(false, Primitive.TYPE.empty())
   );

   public static <T extends ConfigEnum> Class<OptionalPrimitive<WrapEnum<T>>> configEnum(Class<T> clazz) {
      ConfigEnum.ensureRegistered(clazz);
      return (Class)OptionalPrimitive.class;
   }

   public static <T> Class<OptionalPrimitive<T>> type(Class<T> clazz) {
      return (Class)OptionalPrimitive.class;
   }

   public OptionalPrimitive(boolean present, NBTType<T> primitive, T value) {
      this.present = present;
      this.type = primitive;
      this.value = value;
   }

   public OptionalPrimitive(boolean present, Primitive<T> primitive) {
      this(present, primitive.valueType(), primitive.value());
   }

   public Primitive<T> getPrimitive() {
      return Primitive.of(this.type, this.value);
   }

   public boolean test(Predicate<T> predicate) {
      return this.isPresent() && predicate.test(this.value);
   }

   public boolean positive() {
      if (this.isPresent()) {
         if (this.value instanceof Double doubleValue) {
            return doubleValue > 1.0E-7;
         }

         if (this.value instanceof Integer integerValue) {
            return integerValue > 0;
         }
      }

      return false;
   }

   public T orElse(T val) {
      return this.isPresent() ? this.value : val;
   }

   @Override
   public NBTType<OptionalPrimitive<T>> type() {
      return TYPE.cast();
   }

   @Override
   public boolean isSameType(NBTParsable<?> type) {
      return type instanceof OptionalPrimitive<?> optional && this.type == optional.type;
   }

   @Override
   public <W> Optional<OptionalPrimitive<T>> tryTypeConvert(Ref<W> ref) {
      Optional<Primitive<?>> optional = Primitive.convertPrimitives(ref);
      return optional.isPresent() && optional.get().valueType() == this.type
         ? Optional.ofNullable(this.withValue((T)optional.get().value()))
         : Optional.empty();
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof OptionalPrimitive<?> pmt && pmt.type == this.type && pmt.present == this.present && Objects.equals(pmt.value, this.value);
   }

   public boolean isPresent() {
      return this.present;
   }

   public NBTType<T> getType() {
      return this.type;
   }

   public T getValue() {
      return this.value;
   }

   public OptionalPrimitive<T> withPresent(boolean present) {
      return this.present == present ? this : new OptionalPrimitive<>(present, this.type, this.value);
   }

   public OptionalPrimitive<T> withType(NBTType<T> type) {
      return this.type == type ? this : new OptionalPrimitive<>(this.present, type, this.value);
   }

   public OptionalPrimitive<T> withValue(T value) {
      return this.value == value ? this : new OptionalPrimitive<>(this.present, this.type, value);
   }
}
