package me.matl114.utils;

import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import me.matl114.events.annotations.Modifiable;

@Modifiable
public class CollectionUtils {
   private static final Pattern b = Pattern.compile("\\[(\\-?[\\d]+)\\]");
   private static final Pattern a = Pattern.compile("^([^\\[\\]]*?)((?:\\[\\-?\\d+\\])*)$");

   public static <A, B> Pair<A, B> entryToPair(Entry<A, B> entry) {
      return Pair.of(entry.getKey(), entry.getValue());
   }

   public static <T, V> Map<T, V> d() {
      return new LinkedHashMap<>();
   }

   public static <A, B> Map<A, B> pairListToMap(List<Pair<A, B>> map) {
      LinkedHashMap var1 = new LinkedHashMap();

      for (Pair var3 : map) {
         var1.put(var3.getFirst(), var3.getSecond());
      }

      return var1;
   }

   public static <T> List<T> c(T[] list) {
      return Arrays.stream(list).collect(Collectors.toCollection(ArrayList::new));
   }

   public static <A, B> List<Pair<A, B>> j(Map<A, B> map) {
      ArrayList var1 = new ArrayList();

      for (Entry var3 : map.entrySet()) {
         var1.add(Pair.of(var3.getKey(), var3.getValue()));
      }

      return var1;
   }

   public static <T> List<T> a() {
      return new ArrayList<>();
   }

   private static Object h(@Nullable Object tree, String path) {
      if (tree == null) {
         return null;
      } else {
         Matcher var2 = a.matcher(path);
         if (!var2.matches()) {
            throw new IllegalArgumentException("Invalid path: " + path);
         } else {
            String var3 = var2.group(1);
            String var4 = var2.group(2);
            if (!var3.isEmpty()) {
               if (!(tree instanceof Map var5)) {
                  return null;
               }

               tree = var5.get(var3);
            }

            if (tree == null) {
               return null;
            } else {
               if (!var4.isEmpty()) {
                  Matcher var10 = b.matcher(var4);

                  while (var10.find()) {
                     String var6 = var10.group(1);
                     int var7 = Integer.parseInt(var6);
                     if (tree instanceof List var8) {
                        if (var7 >= 0) {
                           tree = var8.get(var7);
                        } else {
                           tree = var8.get(var8.size() + var7);
                        }
                     } else if (tree.getClass().isArray()) {
                        int var9 = Array.getLength(tree);
                        if (var7 < 0) {
                           tree = Array.get(tree, var9 + var7);
                        } else {
                           tree = Array.get(tree, var7);
                        }
                     }

                     if (tree == null) {
                        return null;
                     }
                  }
               }

               return tree;
            }
         }
      }
   }

   public static <T, V> Map<T, V> e(Map<T, V> map) {
      return new LinkedHashMap<>(map);
   }

   public static <T, V> Map<T, V> newHashMap(T[] array, V[] arr) {
      int var2 = Math.min(array.length, arr.length);
      LinkedHashMap var3 = new LinkedHashMap(var2);

      for (int var4 = 0; var4 < var2; var4++) {
         var3.put(array[var4], arr[var4]);
      }

      return var3;
   }

   public static <T> T g(@Nullable Object tree, String path) {
      if (tree == null) {
         return null;
      } else if (path != null && !path.isEmpty()) {
         int var2 = path.indexOf(46);
         String var3 = var2 == -1 ? path : path.substring(0, var2);
         String var4 = var2 == -1 ? null : path.substring(var2 + 1);
         return g(h(tree, var3), var4);
      } else {
         return (T)tree;
      }
   }

   public static <T> List<T> b(List<T> list) {
      return new ArrayList<>(list);
   }
}
