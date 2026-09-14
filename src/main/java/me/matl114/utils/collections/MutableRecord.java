package me.matl114.utils.collections;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JavaOps;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public class MutableRecord {
   public final Map<String, Object> b;
   public final List<String> a;

   public <T> T c(String key, T defaultValue) {
      return (T)(Object)this.b.getOrDefault(key, defaultValue);
   }

   public <T> void e(String key, T value) {
      this.b.put(key, value);
   }

   public List<Pair<String, Object>> a() {
      return this.a.stream().map(s -> Pair.of(s, this.b.get(s))).toList();
   }

   public static <T extends Record> MutableRecord of(List<String> keys, T value) {
      HashMap var2 = new HashMap();
      Class var3 = value.getClass();
      RecordComponent[] var4 = var3.getRecordComponents();
      Preconditions.checkArgument(var4.length == keys.size());

      for (int var5 = 0; var5 < var4.length; var5++) {
         RecordComponent var6 = var4[var5];
         String var7 = (String)keys.get(var5);
         Method var8 = var6.getAccessor();

         try {
            Object var9 = var8.invoke(value);
            var2.put(var7, var9);
         } catch (InvocationTargetException | IllegalAccessException var10) {
            throw new RuntimeException("Failed to get value for " + var7, var10);
         }
      }

      return new MutableRecord(keys, var2);
   }

   public static <T> MutableRecord k(List<String> keys, T value, Codec<T> codec) {
      DataResult var3 = codec.encodeStart(JavaOps.INSTANCE, value);
      Object var4 = var3.getOrThrow();
      if (!(var4 instanceof Map var5)) {
         throw new IllegalArgumentException("Must be a record like: " + var4);
      } else {
         return new MutableRecord(keys, var5);
      }
   }

   public <T> T toRecord(Codec<T> codec) {
      DataResult var2 = codec.parse(JavaOps.INSTANCE, this.b);
      return (T)var2.getOrThrow();
   }

   public MutableRecord(List<String> fields, Map<String, Object> values) {
      this.a = fields;
      this.b = new HashMap<>(values);
   }

   public <T> T d(String key, T defaultValue) {
      Object var3 = this.b(key);
      if (var3 != null) {
         return (T)var3;
      } else {
         this.e(key, defaultValue);
         return (T)defaultValue;
      }
   }

   public <T> T b(String key) {
      return (T)(Object)this.b.get(key);
   }

   public <T extends Record> T j(Class<T> clazz) {
      RecordComponent[] var2 = clazz.getRecordComponents();
      Class[] var3 = Arrays.stream(var2).map(RecordComponent::getType).toArray(Class[]::new);

      Constructor var4;
      try {
         var4 = clazz.getDeclaredConstructor(var3);
         var4.setAccessible(true);
      } catch (NoSuchMethodException var8) {
         throw new RuntimeException("Record constructor not found", var8);
      }

      Object[] var5 = this.a.stream().map(this.b::get).toArray();

      try {
         return (T)var4.newInstance(var5);
      } catch (IllegalAccessException | InvocationTargetException | InstantiationException var7) {
         throw new RuntimeException("Failed to instantiate record", var7);
      }
   }

   public static <T extends Record> MutableRecord h(T value) {
      HashMap var1 = new HashMap();
      Class var2 = value.getClass();
      RecordComponent[] var3 = var2.getRecordComponents();
      ArrayList var4 = new ArrayList(var3.length);

      for (RecordComponent var8 : var3) {
         String var9 = var8.getName();
         var4.add(var9);

         Method var10;
         try {
            var10 = var2.getMethod(var9);
         } catch (NoSuchMethodException var13) {
            throw new RuntimeException("Record component accessor not found: " + var9, var13);
         }

         try {
            Object var11 = var10.invoke(value);
            var1.put(var9, var11);
         } catch (InvocationTargetException | IllegalAccessException var12) {
            throw new RuntimeException("Failed to get value for " + var9, var12);
         }
      }

      return new MutableRecord(var4, var1);
   }

   public void replaceMap(MutableRecord record) {
      this.b.clear();
      this.b.putAll(record.b);
   }

   public <T> void f(String key, UnaryOperator<T> valueUpdate) {
      this.b.put(key, valueUpdate.apply(this.b(key)));
   }
}
