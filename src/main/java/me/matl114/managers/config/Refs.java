package me.matl114.managers.config;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import me.matl114.managers.input.MultiKeyBind;

public class Refs {
   private static final List<Refs.TypedReferenceBuilder<?>> referenceBuilders = ImmutableList.builder()
      .add(new Refs.TypedReferenceBuilder<>(Boolean.class, List.of(FlagRef::new)))
      .add(new Refs.TypedReferenceBuilder<>(Integer.class, List.of(IntRef::new)))
      .add(new Refs.TypedReferenceBuilder<>(Long.class, List.of(LongRef::new)))
      .add(new Refs.TypedReferenceBuilder<>(Float.class, List.of(FloatRef::of)))
      .add(new Refs.TypedReferenceBuilder<>(Double.class, List.of(DoubleRef::of)))
      .add(new Refs.TypedReferenceBuilder<>(ConfigEnum.class, List.of(EnumRef::new)))
      .add(new Refs.TypedReferenceBuilder<>(MultiKeyBind.class, List.of(KeyBindRef::new)))
      .add(new Refs.TypedReferenceBuilder<>(List.class, List.of(ListRef::new)))
      .add(new Refs.TypedReferenceBuilder<>(NBTParsable.class, List.of(NBTRef::new)))
      .add(
         new Refs.TypedReferenceBuilder<>(
            String.class,
            ImmutableList.builder()
               .add(EnumRef::fromString)
               .add(KeyBindRef::fromString)
               .add(NBTRef::fromString)
               .add(FlagRef::fromString)
               .add(IntRef::fromString)
               .add(LongRef::fromString)
               .add(StringRef::new)
               .build()
         )
      )
      .add(new Refs.TypedReferenceBuilder<>(Object.class, List.of(ObjectRef.JustOnlyObjectRef::new)))
      .build();
   public static final Codec<Ref<?>> CODEC = Codec.PASSTHROUGH.comapFlatMap(dynamic -> {
      Ref<?> nbtElement = (Ref<?>)dynamic.convert(ConfigOp.INSTANCE).getValue();
      return DataResult.success(nbtElement);
   }, nbt -> new Dynamic(ConfigOp.INSTANCE, nbt));

   public static List<Refs.TypedReferenceBuilder<?>> getReferenceBuilders() {
      return Collections.unmodifiableList(referenceBuilders);
   }

   public static Ref<?> wrapInstance(Object value) {
      if (value == null) {
         return null;
      } else if (value instanceof Ref<?> ref) {
         return ref;
      } else if (value instanceof Map map) {
         return transferConfig(map);
      } else {
         for (Refs.TypedReferenceBuilder<?> typedBuilder : getReferenceBuilders()) {
            Ref<?> ref = typedBuilder.tryBuild(value);
            if (ref != null) {
               return ref;
            }
         }

         return null;
      }
   }

   public static MapRef transferConfig(Map<String, Object> config) {
      MapRef newConfig = new MapRef();

      for (Entry<String, Object> entry : config.entrySet()) {
         if (entry.getValue() != null) {
            Ref<?> wrapped = wrapInstance(entry.getValue());
            if (wrapped != null) {
               newConfig.putRaw(entry.getKey(), wrapped);
            }
         }
      }

      return newConfig;
   }

   public static class TypedReferenceBuilder<T> {
      public Class<T> baseClass;
      public List<Function<T, Ref<?>>> builders;

      public Ref<?> tryBuild(Object value) {
         if (!this.baseClass.isAssignableFrom(value.getClass())) {
            return null;
         } else {
            T val = (T)value;
            Ref<?> ref = null;

            for (Function<T, Ref<?>> builder : this.builders) {
               if ((ref = builder.apply(val)) != null) {
                  break;
               }
            }

            return ref;
         }
      }

      public TypedReferenceBuilder(Class<T> baseClass, List<Function<T, Ref<?>>> builders) {
         this.baseClass = baseClass;
         this.builders = builders;
      }
   }
}
