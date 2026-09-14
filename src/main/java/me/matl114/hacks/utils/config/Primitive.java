package me.matl114.hacks.utils.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import javax.annotation.Nonnull;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.LongRef;
import me.matl114.managers.config.NBTParsable;
import me.matl114.managers.config.NBTType;
import me.matl114.managers.config.Ref;
import me.matl114.managers.config.StringRef;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.WrapperFactory;
import me.matl114.utils.config.kv.TypeConvertAttrKeyValue;

public record Primitive<T>(NBTType<T> valueType, @Nonnull T value, String valueString) implements NBTParsable<Primitive<T>> {
   public static final String su = "|";
   public static final NBTType<Primitive<Object>> TYPE = create();

   public static <T> Primitive<T> of(NBTType<T> valueType, T value) {
      return new Primitive<>(valueType, value, valueType.stringifyFactory().get(value));
   }

   public static <T> DataResult<Primitive<T>> parse(String va) {
      int idx = va.indexOf("|");
      String type = va.substring(0, idx);
      NBTType<T> lookup = NBTTypes.primitiveTypes(type);
      if (lookup == null) {
         return DataResult.error(() -> "No such primitive type: " + type);
      } else {
         String value = va.substring(idx + 1);

         try {
            T val = lookup.stringifyFactory().create(value);
            return DataResult.success(new Primitive<>(lookup, val, value));
         } catch (Throwable var6) {
            return DataResult.error(() -> "Error parsing primitive value: " + va);
         }
      }
   }

   public String aog() {
      return this.valueType.typeName() + "|" + this.valueString;
   }

   private static <T> NBTType<Primitive<T>> create() {
      return new NBTType<Primitive<T>>(
         "primitive",
         (Codec)Codec.STRING.comapFlatMap(Primitive::parse, Primitive::aog),
         (AttrKeyValue.CustomWidgetFactory<Primitive<T>>)(w, x, y, dx, dy) -> {
            Primitive<T> primitive = w.getOriginValue();
            NBTType<T> typeT = primitive.valueType;
            return new TypeConvertAttrKeyValue<>(w, WrapperFactory.of(s -> of(typeT, (T)s), Primitive::value), typeT).generateValueWidget(x, y, dx, dy);
         },
         WrapperFactory.of(s -> (Primitive<T>)Primitive.parse(s).getOrThrow(), Primitive::aog),
         of((NBTType<T>)NBTTypes.g, (T)"")
      );
   }

   @Override
   public NBTType<Primitive<T>> type() {
      return TYPE.cast();
   }

   @Override
   public boolean isSameType(NBTParsable<?> type) {
      return NBTParsable.super.isSameType(type) && type instanceof Primitive<?> primitive && this.valueType == primitive.valueType;
   }

   @Override
   public <W> Optional<Primitive<T>> tryTypeConvert(Ref<W> ref) {
      return (Optional)convertPrimitives(ref);
   }

   public static Optional<Primitive<?>> convertPrimitives(Ref<?> ref) {
      if (ref instanceof FlagRef flag) {
         return Optional.of(of(NBTTypes.f, flag.get()));
      } else if (ref instanceof IntRef intRef) {
         return Optional.of(of(NBTTypes.c, intRef.get()));
      } else if (ref instanceof LongRef longRef) {
         return Optional.of(of(NBTTypes.d, longRef.get()));
      } else if (ref instanceof DoubleRef doubleRef) {
         return Optional.of(of(NBTTypes.e, doubleRef.get()));
      } else if (ref instanceof StringRef strRef) {
         return Optional.of(of(NBTTypes.g, strRef.get()));
      } else if (ref instanceof KeyBindRef keyBindRef) {
         return Optional.of(of(NBTTypes.i, keyBindRef.get()));
      } else {
         return ref instanceof EnumRef enumRef ? Optional.of(of(NBTTypes.o, new WrapEnum(enumRef))) : Optional.empty();
      }
   }
}
