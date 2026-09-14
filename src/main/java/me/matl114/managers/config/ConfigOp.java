package me.matl114.managers.config;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.DataResult.Error;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class ConfigOp implements DynamicOps<Ref<?>> {
   public static final ConfigOp INSTANCE = new ConfigOp();
   private static final Ref<?> EMPTY = new ObjectRef.JustOnlyObjectRef(null);

   private static Ref<?> wrapConfigValue(Object value) {
      Ref<?> wrapped = Refs.wrapInstance(value);
      return (Ref<?>)(wrapped == null ? new ObjectRef.JustOnlyObjectRef(value) : wrapped);
   }

   private static DataResult<String> requireStringKey(Ref<?> input) {
      return INSTANCE.getStringValue(input).mapError(message -> "Map key must be string: " + message);
   }

   public Ref<?> empty() {
      return EMPTY;
   }

   public <U> U convertTo(DynamicOps<U> outOps, Ref<?> input) {
      return (U)JavaOps.INSTANCE.convertTo(outOps, input.getAsPrimitive());
   }

   public DataResult<Number> getNumberValue(Ref<?> input) {
      Object primitive = input.getAsPrimitive();
      return primitive instanceof Number number ? DataResult.success(number) : DataResult.error(() -> "Not a numeric config value: " + primitive);
   }

   public Ref<?> createNumeric(Number i) {
      if (i instanceof Byte || i instanceof Short || i instanceof Integer) {
         return wrapConfigValue(i.intValue());
      } else if (i instanceof Long) {
         return wrapConfigValue(i.longValue());
      } else if (!(i instanceof Float) && !(i instanceof Double)) {
         double doubleValue = i.doubleValue();
         if (Double.isFinite(doubleValue) && Math.rint(doubleValue) == doubleValue) {
            long longValue = i.longValue();
            return longValue >= -2147483648L && longValue <= 2147483647L ? wrapConfigValue((int)longValue) : wrapConfigValue(longValue);
         } else {
            return wrapConfigValue(doubleValue);
         }
      } else {
         return wrapConfigValue(i.doubleValue());
      }
   }

   public DataResult<Boolean> getBooleanValue(Ref<?> input) {
      if (input instanceof FlagRef flagRef) {
         return DataResult.success(flagRef.get());
      } else {
         Object primitive = input.getAsPrimitive();
         return primitive instanceof Boolean bool ? DataResult.success(bool) : DataResult.error(() -> "Not a boolean config value: " + primitive);
      }
   }

   public Ref<?> createBoolean(boolean value) {
      return new FlagRef(value);
   }

   public DataResult<String> getStringValue(Ref<?> input) {
      Object primitive = input.getAsPrimitive();
      return primitive instanceof String string ? DataResult.success(string) : DataResult.error(() -> "Not a string config value: " + primitive);
   }

   public Ref<?> createString(String value) {
      return wrapConfigValue(value);
   }

   public DataResult<Ref<?>> mergeToList(Ref<?> list, Ref<?> value) {
      if (list == this.empty()) {
         List<String> values = new ArrayList<>();
         values.add(Objects.toString(value.getAsPrimitive(), null));
         return DataResult.success(new ListRef(values));
      } else if (list instanceof ListRef listRef) {
         List<String> values = new ArrayList<>(listRef.get());
         values.add(Objects.toString(value.getAsPrimitive(), null));
         return DataResult.success(new ListRef(values));
      } else {
         return DataResult.error(() -> "Not a list config value: " + list.getAsPrimitive());
      }
   }

   public DataResult<Ref<?>> mergeToMap(Ref<?> map, Ref<?> key, Ref<?> value) {
      DataResult<String> keyResult = requireStringKey(key);
      if (keyResult.isError()) {
         String message = keyResult.error().<String>map(Error::message).orElse("Unknown map key error");
         return DataResult.error(() -> message);
      } else {
         String resolvedKey = (String)keyResult.result().get();
         MapRef mapRef;
         if (map == this.empty()) {
            mapRef = new MapRef();
         } else {
            if (!(map instanceof MapRef existing)) {
               return DataResult.error(() -> "Not a map config value: " + map.getAsPrimitive());
            }

            mapRef = new MapRef();
            mapRef.setValueNoCopy(new LinkedHashMap<>(existing.getValue()));
         }

         mapRef.putRaw(resolvedKey, value);
         return DataResult.success(mapRef);
      }
   }

   public DataResult<Stream<Pair<Ref<?>, Ref<?>>>> getMapValues(Ref<?> input) {
      return input instanceof MapRef mapRef
         ? DataResult.success(mapRef.getValue().entrySet().stream().map(entry -> Pair.of(new StringRef(entry.getKey()), entry.getValue())))
         : DataResult.error(() -> "Not a map config value: " + input.getAsPrimitive());
   }

   public Ref<?> createMap(Stream<Pair<Ref<?>, Ref<?>>> map) {
      Map<String, Object> values = new LinkedHashMap<>();
      map.forEach(entry -> {
         String key = requireStringKey((Ref<?>)entry.getFirst()).result().orElseGet(() -> Objects.toString(((Ref)entry.getFirst()).getAsPrimitive()));
         values.put(key, ((Ref)entry.getSecond()).getAsPrimitive());
      });
      return wrapConfigValue(values);
   }

   public DataResult<Stream<Ref<?>>> getStream(Ref<?> input) {
      return input instanceof ListRef listRef
         ? DataResult.success(listRef.get().stream().map(ConfigOp::wrapConfigValue))
         : DataResult.error(() -> "Not a list config value: " + input.getAsPrimitive());
   }

   public Ref<?> createList(Stream<Ref<?>> input) {
      return new ListRef(input.map(value -> Objects.toString(value.getAsPrimitive(), null)).toList());
   }

   public Ref<?> remove(Ref<?> input, String key) {
      if (input instanceof MapRef mapRef) {
         Map<String, Ref<?>> values = new LinkedHashMap<>(mapRef.getValue());
         values.remove(key);
         MapRef copy = new MapRef();
         copy.setValueNoCopy(values);
         return copy;
      } else {
         return input;
      }
   }
}
