package me.matl114.utils;

import com.mojang.datafixers.util.Pair;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class ReflectUtils {
   public static final Map<String, Integer> objectInvocationIndex = Map.of(
      "getClass", -1, "hashCode", -2, "equals", -3, "clone", -4, "toString", -5, "notify", -6, "notifyAll", -7, "wait", -8, "wait0", -9, "finalize", -10
   );

   public static <T extends Enum> Map<String, T> S(Class<T> clazz) {
      LinkedHashMap var1 = new LinkedHashMap();

      for (Enum var5 : (Enum[])clazz.getEnumConstants()) {
         var1.put(var5.name(), var5);
      }

      return var1;
   }

   public static boolean setFieldRecursively(Object target, String declared, Object value) {
      return b(target, target.getClass(), declared, value);
   }

   public static boolean n(String val) {
      return switch (val) {
         case "int", "void", "boolean", "long", "double", "float", "short", "byte", "char" -> true;
         default -> false;
      };
   }

   public static Map<Field, Object> l(Object value) {
      List<Field> var1 = d(value.getClass());
      HashMap var2 = new HashMap();

      for (Field var4 : var1) {
         if (!Modifier.isStatic(var4.getModifiers())) {
            try {
               var4.setAccessible(true);
               var2.put(var4, var4.get(value));
            } catch (Throwable var6) {
               var2.put(var4, "Error while dumping: " + var6.getMessage());
            }
         }
      }

      return var2;
   }

   public static Class<?> E(String name) {
      try {
         return Class.forName(name);
      } catch (Throwable var2) {
         return null;
      }
   }

   public static Field getFirstFitField(Class<?> clazz, Class<?> fieldType, boolean isStatic) {
      try {
         Field[] var3 = clazz.getDeclaredFields();

         for (Field var7 : var3) {
            if (Modifier.isStatic(var7.getModifiers()) == isStatic && fieldType.isAssignableFrom(var7.getType())) {
               var7.setAccessible(true);
               return var7;
            }
         }
      } catch (Exception var8) {
      }

      return clazz.getSuperclass().getSuperclass() != null ? w(clazz.getSuperclass(), fieldType) : null;
   }

   public static List<Class> g(Class clazz) {
      ArrayList var1 = new ArrayList();
      if (clazz == null) {
         return var1;
      } else {
         Class[] var2 = clazz.getInterfaces();

         for (Class var6 : var2) {
            var1.add(var6);
         }

         var1.addAll(g(clazz.getSuperclass()));
         return var1;
      }
   }

   @Nullable
   public static Field G(Class<?> clazz, String name) {
      try {
         Field var2 = clazz.getDeclaredField(name);
         var2.setAccessible(true);
         return var2;
      } catch (Throwable var3) {
         return null;
      }
   }

   public static Pair<Field, Class> getFieldsRecursively(Class clazz, String fieldName) {
      try {
         Field var2 = clazz.getDeclaredField(fieldName);
         var2.setAccessible(true);
         return Pair.of(var2, clazz);
      } catch (Throwable var3) {
         clazz = clazz.getSuperclass();
         return clazz == null ? null : getFieldsRecursively(clazz, fieldName);
      }
   }

   public static MethodHandle K(Class<?> clazz, String name, Class<?>... argments) {
      try {
         Method var3 = clazz.getDeclaredMethod(name, argments);
         return MethodHandles.privateLookupIn(clazz, MethodHandles.lookup()).unreflect(var3);
      } catch (Throwable var4) {
         return null;
      }
   }

   public static boolean D(Object to, Object from, Class<?> clazz, Class<?> fieldType) {
      return B(to, y(from, clazz, fieldType), clazz, fieldType);
   }

   public static Field w(Class<?> clazz, Class<?> fieldType) {
      try {
         Field[] var2 = clazz.getDeclaredFields();

         for (Field var6 : var2) {
            if (fieldType.isAssignableFrom(var6.getType())) {
               var6.setAccessible(true);
               return var6;
            }
         }
      } catch (Exception var7) {
      }

      return clazz.getSuperclass().getSuperclass() != null ? w(clazz.getSuperclass(), fieldType) : null;
   }

   public static Field[] getAllFitFields(Class<?> clazz, Class<?> fieldType) {
      if (clazz == null) {
         return new Field[0];
      } else {
         ArrayList<Field> var2 = new ArrayList<>();

         for (Field var6 : clazz.getDeclaredFields()) {
            if (fieldType.isAssignableFrom(var6.getType())) {
               var6.setAccessible(true);
               var2.add(var6);
            }
         }

         var2.addAll(Arrays.stream(getAllFitFields(clazz.getSuperclass(), fieldType)).toList());
         return var2.toArray(Field[]::new);
      }
   }

   public static List<Method> e(Class clazz) {
      ArrayList var1 = new ArrayList();
      if (clazz == null) {
         return var1;
      } else {
         Method[] var2 = clazz.getDeclaredMethods();

         for (Method var6 : var2) {
            var1.add(var6);

            try {
               var6.setAccessible(true);
            } catch (Throwable var8) {
            }
         }

         for (Class var12 : clazz.getInterfaces()) {
            var1.addAll(e(var12));
         }

         var1.addAll(e(clazz.getSuperclass()));
         return var1;
      }
   }

   public static Class<?> q(String boxedClassName) {
      return switch (boxedClassName) {
         case "java/lang/Integer" -> int.class;
         case "java/lang/Boolean" -> boolean.class;
         case "java/lang/Long" -> long.class;
         case "java/lang/Double" -> double.class;
         case "java/lang/Float" -> float.class;
         case "java/lang/Short" -> short.class;
         case "java/lang/Byte" -> byte.class;
         case "java/lang/Character" -> char.class;
         case "java/lang/Void" -> void.class;
         default -> throw new IllegalArgumentException("Not a boxed primitive class: " + boxedClassName);
      };
   }

   public static VarHandle L(Class<?> clazz, String name) {
      try {
         Field var2 = clazz.getField(name);
         return MethodHandles.lookup().unreflectVarHandle(var2);
      } catch (Throwable var3) {
         return null;
      }
   }

   public static Constructor getConstructorByParams(Class clazz, Class... parameterTypes) {
      Constructor[] var2 = clazz.getDeclaredConstructors();

      for (Constructor var6 : var2) {
         Class[] var7 = var6.getParameterTypes();
         boolean var8 = true;
         if (var7.length == parameterTypes.length) {
            int var9 = var7.length;

            for (int var10 = 0; var10 < var9; var10++) {
               if (var7[var10] != parameterTypes[var10] && !var7[var10].isAssignableFrom(parameterTypes[var10])) {
                  var8 = false;
               }
            }
         } else {
            var8 = false;
         }

         if (var8) {
            var6.setAccessible(true);
            return var6;
         }
      }

      return null;
   }

   public static String r(String primitive) {
      switch (primitive) {
         case "int":
            return "java/lang/Integer";
         case "boolean":
            return "java/lang/Boolean";
         case "long":
            return "java/lang/Long";
         case "double":
            return "java/lang/Double";
         case "float":
            return "java/lang/Float";
         case "short":
            return "java/lang/Short";
         case "byte":
            return "java/lang/Byte";
         case "char":
            return "java/lang/Character";
         case "void":
            return "java/lang/Void";
         default:
            throw new IllegalArgumentException("Unsupported primitive type: " + primitive);
      }
   }

   public static boolean isExtendedFrom(Class clazz, String s) {
      if (clazz == null) {
         return false;
      } else {
         return clazz.getName().endsWith(s) ? true : isExtendedFrom(clazz.getSuperclass(), s);
      }
   }

   public static Method I(Class<?> clazz, String name, Class<?>... clazzes) {
      try {
         Method var3 = clazz.getDeclaredMethod(name, clazzes);
         var3.setAccessible(true);
         return var3;
      } catch (Throwable var4) {
         return null;
      }
   }

   public static boolean o(String className) {
      return switch (className) {
         case "java/lang/Integer", "java/lang/Boolean", "java/lang/Long", "java/lang/Double", "java/lang/Float", "java/lang/Short", "java/lang/Byte", "java/lang/Character", "java/lang/Void" -> true;
         default -> false;
      };
   }

   public static VarHandle M(Field field) {
      field.setAccessible(true);

      try {
         return MethodHandles.privateLookupIn(field.getDeclaringClass(), MethodHandles.lookup()).unreflectVarHandle(field);
      } catch (Throwable var2) {
         throw new RuntimeException(var2);
      }
   }

   public static Map<Field, Object> m(Object value) {
      return l(value);
   }

   public static Class<?> s(String primitive) {
      switch (primitive) {
         case "int":
            return Integer.class;
         case "boolean":
            return Boolean.class;
         case "long":
            return Long.class;
         case "double":
            return Double.class;
         case "float":
            return Float.class;
         case "short":
            return Short.class;
         case "byte":
            return Byte.class;
         case "char":
            return Character.class;
         case "void":
            return Void.class;
         default:
            throw new IllegalArgumentException("Unsupported primitive type: " + primitive);
      }
   }

   public static boolean B(Object object, Object tar, Class<?> clazz, Class<?> fieldType) {
      try {
         if (object != null && !clazz.isInstance(object)) {
            return false;
         } else {
            Field var4 = w(clazz, fieldType);
            var4.setAccessible(true);
            var4.set(object, tar);
            return true;
         }
      } catch (Throwable var5) {
         var5.printStackTrace();
         return false;
      }
   }

   @Nullable
   public static Field F(Class<?> clazz, String name) {
      try {
         return clazz.getField(name);
      } catch (Throwable var3) {
         return null;
      }
   }

   public static List<Method> f(Class iface) {
      ArrayList var1 = new ArrayList();

      for (Method var5 : iface.getMethods()) {
         int var6 = var5.getModifiers();
         if (var5.isDefault() || Modifier.isPrivate(var6) || Modifier.isStatic(var6)) {
            try {
               var1.add(var5);
            } catch (Throwable var8) {
            }
         }
      }

      for (Class var12 : iface.getInterfaces()) {
         var1.addAll(f(var12));
      }

      return var1;
   }

   public static Object getFieldValue(Object object, Class<?> clazz, Class<?> fieldType, boolean isStatic) {
      try {
         if (object != null && !clazz.isInstance(object)) {
            return false;
         } else {
            Field var4 = getFirstFitField(clazz, fieldType, isStatic);
            var4.setAccessible(true);
            return var4.get(object);
         }
      } catch (Throwable var5) {
         var5.printStackTrace();
         return null;
      }
   }

   public static boolean P(Method method) {
      return objectInvocationIndex.containsKey(method.getName());
   }

   public static MethodHandle J(Class<?> clazz, String name, Class<?>... argments) {
      try {
         Method var3 = clazz.getMethod(name, argments);
         return MethodHandles.lookup().unreflect(var3);
      } catch (Throwable var4) {
         return null;
      }
   }

   public static List<Field> d(Class clazz) {
      ArrayList var1 = new ArrayList();
      if (clazz == null) {
         return var1;
      } else {
         Field[] var2 = clazz.getDeclaredFields();

         for (Field var6 : var2) {
            var1.add(var6);

            try {
               var6.setAccessible(true);
            } catch (Throwable var8) {
            }
         }

         for (Class var12 : clazz.getInterfaces()) {
            var1.addAll(d(var12));
         }

         var1.addAll(d(clazz.getSuperclass()));
         return var1;
      }
   }

   public static Pair<Method, Class> k(Class clazz, String fieldName) {
      Method[] var2 = clazz.getDeclaredMethods();

      for (Method var6 : var2) {
         try {
            if (var6.getName().equals(fieldName)) {
               var6.setAccessible(true);
               return Pair.of(var6, clazz);
            }
         } catch (Throwable var8) {
         }
      }

      clazz = clazz.getSuperclass();
      return clazz == null ? null : k(clazz, fieldName);
   }

   public static List<Class> i(Class clazz) {
      ArrayList var1 = new ArrayList();

      while (clazz != null) {
         var1.add(clazz);
         clazz = clazz.getSuperclass();
      }

      return var1;
   }

   public static Object invokeBaseMethod(Object target, int index, Object[] args) {
      switch (index) {
         case -10:
            throw new IllegalArgumentException("finalize method not supported");
         case -9:
            throw new IllegalArgumentException("wait0 method not supported");
         case -8:
            try {
               switch (args.length) {
                  case 0:
                     target.wait();
                     return null;
                  case 1:
                     target.wait((Long)args[0]);
                     return null;
                  case 2:
                     target.wait();
                     return null;
                  default:
                     throw new IllegalArgumentException("Wrong argument count for wait!");
               }
            } catch (InterruptedException var4) {
               return null;
            }
         case -7:
            target.notifyAll();
            return null;
         case -6:
            target.notify();
            return null;
         case -5:
            return target.toString();
         case -4:
            throw new IllegalStateException("clone method not supported");
         case -3:
            return target.equals(args[0]);
         case -2:
            return target.hashCode();
         case -1:
            return target.getClass();
         default:
            return null;
      }
   }

   public static MethodHandle O(Class<?> clazz, String name, Class<?>... args) {
      try {
         Method var3 = clazz.getDeclaredMethod(name, args);
         return MethodHandles.privateLookupIn(clazz, MethodHandles.lookup()).unreflect(var3);
      } catch (Throwable var4) {
         return null;
      }
   }

   public static Method H(Class<?> clazz, String name, Class<?>... clazzs) {
      try {
         return clazz.getMethod(name, clazzs);
      } catch (Throwable var4) {
         return null;
      }
   }

   public static int getBaseMethodIndex(Method method) {
      String var1 = method.getName();
      if (objectInvocationIndex.containsKey(var1)) {
         return objectInvocationIndex.get(var1);
      } else {
         throw new IllegalArgumentException("Not a base Method!");
      }
   }

   public static Object y(Object object, Class<?> clazz, Class<?> fieldType) {
      try {
         if (object != null && !clazz.isInstance(object)) {
            return false;
         } else {
            Field var3 = w(clazz, fieldType);
            var3.setAccessible(true);
            return var3.get(object);
         }
      } catch (Throwable var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public static Pair<Method, Class> getMethodsRecursively(Class clazz, String methodName, Class[] parameterTypes) {
      try {
         Method[] var3 = clazz.getDeclaredMethods();

         for (Method var7 : var3) {
            Class[] var8 = var7.getParameterTypes();
            if (methodName.equals(var7.getName())) {
               boolean var9 = true;
               if (var8.length == parameterTypes.length) {
                  int var10 = var8.length;

                  for (int var11 = 0; var11 < var10; var11++) {
                     if (var8[var11] != parameterTypes[var11] && !var8[var11].isAssignableFrom(parameterTypes[var11])) {
                        var9 = false;
                     }
                  }
               } else {
                  var9 = false;
               }

               if (var9) {
                  var7.setAccessible(true);
                  return Pair.of(var7, clazz);
               }
            }
         }
      } catch (Throwable var12) {
      }

      clazz = clazz.getSuperclass();
      return clazz == null ? null : getMethodsRecursively(clazz, methodName, parameterTypes);
   }

   public static List<Class> h(Class clazz) {
      HashSet var1 = new HashSet();
      if (clazz == null) {
         return var1.stream().toList();
      } else {
         Class[] var2 = clazz.getInterfaces();

         for (Class var6 : var2) {
            var1.addAll(h(var6));
         }

         var1.addAll(h(clazz.getSuperclass()));
         return var1.stream().toList();
      }
   }

   public static boolean b(Object target, Class clazz, String decleared, Object value) {
      try {
         Field var4 = clazz.getDeclaredField(decleared);
         var4.setAccessible(true);
         var4.set(target, value);
         return true;
      } catch (Throwable var5) {
         clazz = clazz.getSuperclass();
         return clazz == null ? false : b(target, clazz, decleared, value);
      }
   }

   public static VarHandle N(Class<?> clazz, String name) {
      try {
         Field var2 = clazz.getDeclaredField(name);
         return MethodHandles.privateLookupIn(clazz, MethodHandles.lookup()).unreflectVarHandle(var2);
      } catch (Throwable var3) {
         return null;
      }
   }

   public static Pair<Method, Class> j(Class clazz, String fieldName, Class... parameterTypes) {
      try {
         Method var3 = clazz.getDeclaredMethod(fieldName, parameterTypes);
         var3.setAccessible(true);
         return Pair.of(var3, clazz);
      } catch (Throwable var9) {
         for (Class var7 : clazz.getInterfaces()) {
            Pair var8 = j(var7, fieldName, parameterTypes);
            if (var8 != null) {
               return var8;
            }
         }

         clazz = clazz.getSuperclass();
         return clazz == null ? null : j(clazz, fieldName, parameterTypes);
      }
   }

   public static String p(String boxedClassName) {
      return switch (boxedClassName) {
         case "java/lang/Integer" -> "int";
         case "java/lang/Boolean" -> "boolean";
         case "java/lang/Long" -> "long";
         case "java/lang/Double" -> "double";
         case "java/lang/Float" -> "float";
         case "java/lang/Short" -> "short";
         case "java/lang/Byte" -> "byte";
         case "java/lang/Character" -> "char";
         case "java/lang/Void" -> "void";
         default -> throw new IllegalArgumentException("Not a boxed primitive class: " + boxedClassName);
      };
   }

   public static boolean setFirstFitField(Object object, Object tar, Class<?> clazz, Class<?> fieldType, boolean isStatic) {
      try {
         if (object != null && !clazz.isInstance(object)) {
            return false;
         } else {
            Field var5 = getFirstFitField(clazz, fieldType, isStatic);
            var5.setAccessible(true);
            var5.set(object, tar);
            return true;
         }
      } catch (Throwable var6) {
         var6.printStackTrace();
         return false;
      }
   }
}
