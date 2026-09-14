package me.matl114.utils.a;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import me.matl114.utils.commands.interruption.KalamaHelperHelperH;
import me.matl114.utils.commands.interruption.TypeError;
import me.matl114.utils.commands.interruption.ValueOutOfRangeError;
import me.matl114.utils.commands.params.api.ArgumentType;
import me.matl114.utils.commands.params.api.KalamaHelperHelperF;
import org.jetbrains.annotations.Nullable;

public class KalamaHelperHelperA {
   public static final List<String> b = List.of("0", "1", "16", "64", "114514", "2147483647");
   public static final List<String> a = List.of("true", "false");
   public static final List<String> c = List.of("0.0", "1.0", "2.0", "3.0", "3.14159", "1.57079", "6.283185");

   public static void o(String arg, float input, int from, int to) {
      if (!(input >= from) || !(input < to)) {
         throw new ValueOutOfRangeError(null, arg, (float)from, (float)to, input);
      }
   }

   public static Supplier<List<String>> w() {
      return () -> c;
   }

   public static int e(int value, int min, int max) {
      return Math.max(Math.min(max, value), min);
   }

   public static List<String> t() {
      return b;
   }

   public static Supplier<Stream<String>> s() {
      return a::stream;
   }

   public static Supplier<Stream<String>> y() {
      return c::stream;
   }

   public static float g(String val, @Nullable ArgumentType<?> arg) {
      try {
         return Float.parseFloat(val);
      } catch (Throwable var3) {
         throw new TypeError(arg, KalamaHelperHelperH.Qw, val);
      }
   }

   public static double l(String val, @Nullable String arg) {
      try {
         return Double.parseDouble(val);
      } catch (Throwable var3) {
         throw new TypeError(arg, KalamaHelperHelperH.Qw, val);
      }
   }

   public static int b(String value, int defaultValue) {
      try {
         return Integer.parseInt(value);
      } catch (NumberFormatException var3) {
         return defaultValue;
      }
   }

   public static KalamaHelperHelperF B() {
      return KalamaHelperHelperF.e(p -> Stream.of("%.2f".formatted(p.sp().y), "~ ~ ~", "^ ^ ^"))
         .c(KalamaHelperHelperF.h((p, str) -> str.startsWith("^") ? Stream.of("^") : Stream.of("~")));
   }

   public static float k(String val, @Nullable String arg) {
      try {
         return Float.parseFloat(val);
      } catch (Throwable var3) {
         throw new TypeError(arg, KalamaHelperHelperH.Qw, val);
      }
   }

   public static KalamaHelperHelperF C() {
      return KalamaHelperHelperF.e(p -> Stream.of("%.2f".formatted(p.sp().z), "~ ~ ~", "^ ^ ^"))
         .c(KalamaHelperHelperF.h((p, str) -> str.startsWith("^") ? Stream.of("^") : Stream.of("~")));
   }

   public static int f(String val, @Nullable ArgumentType<?> arg) {
      try {
         return Integer.parseInt(val);
      } catch (Throwable var3) {
         throw new TypeError(arg, KalamaHelperHelperH.Qv, val);
      }
   }

   public static int j(String val, @Nullable String arg) {
      try {
         return Integer.parseInt(val);
      } catch (Throwable var3) {
         throw new TypeError(arg, KalamaHelperHelperH.Qv, val);
      }
   }

   public static Integer c(String value, Integer defaultValue) {
      try {
         return Integer.parseInt(value);
      } catch (NumberFormatException var3) {
         return defaultValue;
      }
   }

   public static KalamaHelperHelperF A() {
      return KalamaHelperHelperF.e(p -> Stream.of("%.2f".formatted(p.sp().x), "~ ~ ~", "^ ^ ^"));
   }

   public static Supplier<List<String>> v() {
      return () -> b;
   }

   public static boolean m(String val, @Nullable String arg) {
      switch (val) {
         case "true":
            return true;
         case "false":
            return false;
         default:
            throw new TypeError(arg, KalamaHelperHelperH.Qx, val);
      }
   }

   public static String a(String[] args, int index, String defaultValue) {
      return args.length > index ? args[index] : defaultValue;
   }

   public static double h(String val, @Nullable ArgumentType<?> arg) {
      try {
         return Double.parseDouble(val);
      } catch (Throwable var3) {
         throw new TypeError(arg, KalamaHelperHelperH.Qw, val);
      }
   }

   public static List<String> u() {
      return c;
   }

   public static void n(String arg, int input, int from, int to) {
      if (input < from || input >= to) {
         throw new ValueOutOfRangeError(null, arg, from, to, input);
      }
   }

   public static Supplier<List<String>> q() {
      return () -> a;
   }

   public static boolean i(String val, @Nullable ArgumentType<?> arg) {
      switch (val) {
         case "true":
            return true;
         case "false":
            return false;
         default:
            throw new TypeError(arg, KalamaHelperHelperH.Qx, val);
      }
   }

   public static List<String> r() {
      return a;
   }

   public static double d(String value, double defaultValue) {
      try {
         return Double.parseDouble(value);
      } catch (Throwable var4) {
         return defaultValue;
      }
   }

   public static Supplier<Stream<String>> z(File parent, Predicate<String> suffix) {
      return () -> Arrays.stream(parent.listFiles()).filter(File::isFile).map(File::getName).filter(suffix);
   }

   public static void p(String arg, double input, double from, double to) {
      if (!(input >= from) || !(input < to)) {
         throw new ValueOutOfRangeError(null, arg, String.valueOf(from), String.valueOf(to), String.valueOf(input), KalamaHelperHelperH.Qw);
      }
   }

   public static Supplier<Stream<String>> x() {
      return b::stream;
   }
}
