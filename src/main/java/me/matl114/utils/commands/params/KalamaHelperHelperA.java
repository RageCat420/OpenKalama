package me.matl114.utils.commands.params;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import me.matl114.utils.commands.interruption.ValueAbsentError;
import me.matl114.utils.commands.params.api.ArgumentType;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.commands.params.api.InputArgument;
import me.matl114.utils.commands.params.impl.AbstractArgumentType;

public class KalamaHelperHelperA {
   ArgumentType<?>[] a;

   public ArgumentType<?>[] d() {
      return this.a;
   }

   public static <T extends AbstractArgumentType<W>, W> KalamaHelperHelperD<T, W> b(Function<String, T> builder) {
      return new KalamaHelperHelperD<>(builder);
   }

   public KalamaHelperHelperA(String... args) {
      this.a = Arrays.stream(args).map(KalamaHelperHelperB::new).toArray(ArgumentType[]::new);
   }

   public static KalamaHelperHelperD<KalamaHelperHelperB, String> a() {
      return new KalamaHelperHelperD<>(KalamaHelperHelperB::new);
   }

   public ArgumentInputStream c(CommandExecution execution, ArgumentReader reader) {
      ArrayList var3 = new ArrayList();
      List<ArgumentType> var4 = Arrays.stream(this.a).collect(Collectors.toCollection(ArrayList::new));

      for (ArgumentType var6 : var4) {
         if (!reader.hasNext()) {
            break;
         }

         InputArgument var7 = var6.consume(execution, var3, reader);
         if (var7 == null) {
            throw new ValueAbsentError(var6.getArgsName(), new ArgumentReader(reader));
         }

         var3.add(var7);
      }

      return new ArgumentInputStream(execution, reader, var4, var3);
   }

   public KalamaHelperHelperA(ArgumentType<?>... args) {
      this.a = args;
   }
}
