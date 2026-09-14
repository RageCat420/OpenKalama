package me.matl114.jsApi;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Locale;
import me.matl114.events.annotations.Modifiable;
import me.matl114.utils.Debug;
import net.minecraft.client.MinecraftClient;

@Modifiable
public class JsHelper {
   public static void runSingleRepeat(Object object, String flag, boolean debug) {
      throw new UnsupportedOperationException();
   }

   public static void runOnMainThread(Runnable runnable) {
      MinecraftClient.getInstance().execute(runnable);
   }

   public static <T> T a(Object what, Class<T> type) {
      return JsMacrosBridge.i().j(what, type);
   }

   private static KalamaHelperHelperV c(String clazzName, String methodName) {
      try {
         Class<?> var2 = Class.forName(clazzName);
         return Arrays.stream(var2.getMethods())
            .filter(m -> Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers()))
            .filter(m -> m.getParameterCount() == 1)
            .filter(m -> m.getName().equals(methodName))
            .findFirst()
            .<KalamaHelperHelperV>map(m -> s -> m.invoke(null, s))
            .orElseGet(() -> m -> {
               throw new UnsupportedOperationException("Failed to find wrapper for :" + var2.getSimpleName() + ", use JavaUtils.getHelperFromRaw instead");
            });
      } catch (Throwable var3) {
         Debug.a("Fail to look up wrapping method : " + clazzName + "." + methodName);
         return m -> {
            throw new UnsupportedOperationException("Failed to find wrapper for :" + clazzName + ", use JavaUtils.getHelperFromRaw instead");
         };
      }
   }

   public static <T> T wrap(Object object) throws Throwable {
      return (T)JsMacrosBridge.i().b(object);
   }

   public static void g(Object object, String flag) {
      runSingleRepeat(object, flag, true);
   }

   public static <T extends Enum<T>> T b(Object what, Class<T> type) {
      if (type.isInstance(what)) {
         return (T)type.cast(what);
      } else {
         return what instanceof String var2 ? Enum.valueOf(type, var2.toUpperCase(Locale.ROOT)) : a(what, type);
      }
   }

   private static KalamaHelperHelperV d(String clazzName, Class<?> clazz2) {
      try {
         Class<?> var2 = Class.forName(clazzName);
         return Arrays.stream(var2.getConstructors())
            .filter(m -> Modifier.isPublic(m.getModifiers()))
            .filter(m -> m.getParameterCount() == 1)
            .filter(m -> clazz2.isAssignableFrom(m.getParameterTypes()[0]))
            .findFirst()
            .<KalamaHelperHelperV>map(m -> xva$0 -> m.newInstance(xva$0))
            .orElseGet(() -> m -> {
               throw new UnsupportedOperationException("Failed to find wrapper for :" + var2.getSimpleName() + ", use JavaUtils.getHelperFromRaw instead");
            });
      } catch (Throwable var3) {
         Debug.a("Fail to look up wrapping constructor : " + clazzName);
         return m -> {
            throw new UnsupportedOperationException("Failed to find wrapper for :" + clazzName + ", use JavaUtils.getHelperFromRaw instead");
         };
      }
   }
}
