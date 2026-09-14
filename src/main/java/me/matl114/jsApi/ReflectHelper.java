package me.matl114.jsApi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import me.matl114.events.annotations.Modifiable;
import me.matl114.utils.Debug;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Modifiable
public class ReflectHelper {
   public static List<Method> z(Object a, String name, Object... b) {
      return Arrays.stream(a.getClass().getDeclaredMethods())
         .filter(m -> !Modifier.isStatic(m.getModifiers()))
         .filter(m -> m.getName().equals(name))
         .filter(m -> m.getParameterCount() == b.length)
         .filter(m -> {
            for (int var2 = 0; var2 < b.length; var2++) {
               if (!r(b[var2], m.getParameterTypes()[var2])) {
                  return false;
               }
            }

            return true;
         })
         .toList();
   }

   public static Object A(Object a, String name, Object... b) throws Throwable {
      Method var3 = y(a, name, b).get(0);
      return var3.invoke(a, b);
   }

   public static void logMethodsInfo(Object what) {
      Class var1 = what instanceof Class ? (Class)what : what.getClass();
      Debug.b(Text.literal("=== " + j(var1) + " 的方法信息 ===").formatted(Formatting.GREEN));

      for (Method var5 : var1.getMethods()) {
         String var6 = o(var5);
         Debug.b(var6);
         Debug.b(Text.literal("=========").formatted(Formatting.GREEN));
      }
   }

   public static List<String> l(Object what) {
      Class var1 = what instanceof Class ? (Class)what : what.getClass();
      return Arrays.stream(var1.getMethods()).map(ReflectHelper::o).toList();
   }

   public static void m(Object what) {
      Class var1 = what instanceof Class ? (Class)what : what.getClass();
      Debug.b(Text.literal("=== " + j(var1) + " 的私有方法信息 ===").formatted(Formatting.GREEN));

      for (Method var5 : var1.getDeclaredMethods()) {
         if (!Modifier.isPublic(var5.getModifiers())) {
            String var6 = o(var5);
            Debug.b(var6);
            Debug.b(Text.literal("=========").formatted(Formatting.GREEN));
         }
      }
   }

   public static Class<?> getParameterType(Class<?> clazz, int pos, int constructorId) {
      return clazz.getConstructors()[constructorId].getParameterTypes()[pos];
   }

   public static boolean u(String className) {
      return switch (className) {
         case "java/lang/Integer", "java/lang/Boolean", "java/lang/Long", "java/lang/Double", "java/lang/Float", "java/lang/Short", "java/lang/Byte", "java/lang/Character", "java/lang/Void" -> true;
         default -> false;
      };
   }

   public static <T extends Enum<T>> T getEnumParameter(Class<?> clazz, int pos, String value, int constructorId) {
      Constructor var4 = clazz.getConstructors()[constructorId];
      return Enum.valueOf((Class<T>)var4.getParameterTypes()[pos], value);
   }

   private static String q(Executable executable) {
      TypeVariable[] var1 = executable.getTypeParameters();
      if (var1.length == 0) {
         return "";
      } else {
         StringBuilder var2 = new StringBuilder();
         var2.append("<");

         for (int var3 = 0; var3 < var1.length; var3++) {
            var2.append(var1[var3].getName());
            if (var3 < var1.length - 1) {
               var2.append(", ");
            }
         }

         var2.append("> ");
         return var2.toString();
      }
   }

   private static boolean r(Object a, Class<?> b) {
      if (t(b.getName())) {
         return a != null && (a.getClass() == b || w(b.getName()).isAssignableFrom(a.getClass()));
      } else {
         return u(b.getName())
            ? a == null || b.isAssignableFrom(a.getClass()) || b.isAssignableFrom(w(a.getClass().getName()))
            : a == null || b.isAssignableFrom(a.getClass());
      }
   }

   public static String p(Field field) {
      StringBuilder var1 = new StringBuilder();
      var1.append(Modifier.toString(field.getModifiers())).append(" ");
      var1.append(field.getAnnotatedType().getType().getTypeName()).append(" ");
      var1.append(field.getName());
      return var1.toString();
   }

   public static List<String> f(Object what) {
      Class var1 = what instanceof Class ? (Class)what : what.getClass();
      return Arrays.stream(var1.getEnumConstants()).map(s -> ((Enum)s).name()).toList();
   }

   private static boolean s(Class a, Class<?> b) {
      if (t(b.getName())) {
         return a == null || a == b || w(b.getName()).isAssignableFrom(a);
      } else {
         return u(b.getName()) ? a == null || b.isAssignableFrom(a) || b.isAssignableFrom(w(a.getName())) : a == null || b.isAssignableFrom(a);
      }
   }

   public static Class<?> v(String boxedClassName) {
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

   public static List<String> n(Object what) {
      Class var1 = what instanceof Class ? (Class)what : what.getClass();
      return Arrays.stream(var1.getDeclaredMethods()).filter(s -> !Modifier.isPublic(s.getModifiers())).map(ReflectHelper::o).toList();
   }

   public static String o(Executable constructor) {
      StringBuilder var1 = new StringBuilder();
      var1.append(Modifier.toString(constructor.getModifiers())).append(" ");
      var1.append(q(constructor));
      var1.append(constructor.getAnnotatedReturnType().getType().getTypeName()).append(" ");
      var1.append(constructor.getName());
      var1.append("(");
      Type[] var2 = constructor.getGenericParameterTypes();
      Parameter[] var3 = constructor.getParameters();

      for (int var4 = 0; var4 < var2.length; var4++) {
         var1.append(j(var2[var4])).append(" ");
         if (var4 < var3.length && var3[var4].isNamePresent()) {
            var1.append(var3[var4].getName());
         } else {
            var1.append("p").append(var4);
         }

         if (var4 < var2.length - 1) {
            var1.append(", ");
         }
      }

      var1.append(")");
      Class[] var6 = constructor.getExceptionTypes();
      if (var6.length > 0) {
         var1.append(" throws ");

         for (int var5 = 0; var5 < var6.length; var5++) {
            var1.append(j(var6[var5]));
            if (var5 < var6.length - 1) {
               var1.append(", ");
            }
         }
      }

      return var1.toString();
   }

   public static List<String> i(Object what) {
      Class var1 = what instanceof Class ? (Class)what : what.getClass();
      return Arrays.stream(var1.getDeclaredConstructors()).map(ReflectHelper::o).toList();
   }

   public static List<Method> x(Object a, String name, Class... b) {
      return Arrays.stream(a.getClass().getMethods())
         .filter(m -> !Modifier.isStatic(m.getModifiers()))
         .filter(m -> m.getName().equals(name))
         .filter(m -> m.getParameterCount() == b.length)
         .filter(m -> {
            for (int var2 = 0; var2 < b.length; var2++) {
               if (!s(b[var2], m.getParameterTypes()[var2])) {
                  return false;
               }
            }

            return true;
         })
         .toList();
   }

   public static boolean g(Object what) {
      Class var1 = what instanceof Class ? (Class)what : what.getClass();
      return Enum.class.isAssignableFrom(var1);
   }

   public static Class<?> w(String primitive) {
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

   public static <T extends Enum<T>> T e(Class<?> clazz, int pos, String value) {
      return Arrays.stream(clazz.getConstructors())
         .filter(con -> con.getParameterCount() > pos && Enum.class.isAssignableFrom(con.getParameterTypes()[pos]))
         .findAny()
         .map(cls -> Enum.valueOf((Class<? extends T>)cls.getParameterTypes()[pos], value))
         .get();
   }

   public static List<Method> y(Object a, String name, Object... b) {
      return Arrays.stream(a.getClass().getMethods())
         .filter(m -> !Modifier.isStatic(m.getModifiers()))
         .filter(m -> m.getName().equals(name))
         .filter(m -> m.getParameterCount() == b.length)
         .filter(m -> {
            for (int var2 = 0; var2 < b.length; var2++) {
               if (!r(b[var2], m.getParameterTypes()[var2])) {
                  return false;
               }
            }

            return true;
         })
         .toList();
   }

   public static void logClassInfo(Object what) {
      Class var1 = what instanceof Class ? (Class)what : what.getClass();
      Debug.b(Text.literal("=== " + var1.getSimpleName() + "的信息 ===").formatted(Formatting.YELLOW));
      Debug.b(Text.literal("类型: " + Modifier.toString(var1.getModifiers())));
      Debug.b(Text.literal("父类: " + var1.getSuperclass()));
      Debug.b(Text.literal("接口: " + Arrays.<Class<?>>asList(var1.getInterfaces())));
      Debug.b(Text.literal("=== " + j(var1) + " 的构造器信息 ===").formatted(Formatting.GREEN));

      for (Constructor var5 : var1.getDeclaredConstructors()) {
         String var6 = o(var5);
         Debug.b(var6);
         Debug.b(Text.literal("=========").formatted(Formatting.GREEN));
      }
   }

   public static List<Method> d(Class<?> clazz, String methodName) {
      return Arrays.stream(clazz.getMethods()).filter(s -> Objects.equals(s.getName(), methodName)).toList();
   }

   public static <T extends Enum<T>> T a(Class<T> enumClass, String name) {
      return (T)Arrays.stream((Enum[])enumClass.getEnumConstants()).filter(e -> e.name().equals(name)).findFirst().orElse(null);
   }

   private static String j(Type type) {
      if (type instanceof Class var1) {
         String var12 = var1.getSimpleName();
         if (var1.isEnum()) {
            var12 = var12 + "(Enum)";
         }

         return var12;
      } else if (type instanceof ParameterizedType var11) {
         StringBuilder var13 = new StringBuilder();
         Type var14 = var11.getRawType();
         var13.append(j(var14));
         Type[] var15 = var11.getActualTypeArguments();
         if (var15.length > 0) {
            var13.append("<");

            for (int var17 = 0; var17 < var15.length; var17++) {
               var13.append(j(var15[var17]));
               if (var17 < var15.length - 1) {
                  var13.append(", ");
               }
            }

            var13.append(">");
         }

         return var13.toString();
      } else if (type instanceof TypeVariable) {
         return ((TypeVariable)type).getName();
      } else if (!(type instanceof WildcardType var2)) {
         return type instanceof GenericArrayType var10 ? j(var10.getGenericComponentType()) + "[]" : type.getTypeName();
      } else {
         StringBuilder var3 = new StringBuilder("?");
         Type[] var4 = var2.getUpperBounds();
         Type[] var5 = var2.getLowerBounds();
         if (var5.length > 0) {
            var3.append(" super ");

            for (Type var9 : var5) {
               var3.append(j(var9));
            }
         } else if (var4.length > 0 && (var4.length != 1 || var4[0] != Object.class)) {
            var3.append(" extends ");

            for (int var16 = 0; var16 < var4.length; var16++) {
               var3.append(j(var4[var16]));
               if (var16 < var4.length - 1) {
                  var3.append(" & ");
               }
            }
         }

         return var3.toString();
      }
   }

   public static boolean t(String val) {
      return switch (val) {
         case "int", "void", "boolean", "long", "double", "float", "short", "byte", "char" -> true;
         default -> false;
      };
   }
}
