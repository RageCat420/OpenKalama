package me.matl114.utils.commands.params.impl;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.ArgumentType;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.commands.params.api.InputArgument;
import org.jetbrains.annotations.Nullable;

public class KalamaHelperHelperN<T> extends AbstractArgumentType<T> implements ArgumentType<T> {
   private final BiPredicate<CommandExecution, InputArgument<? extends T>> d;
   private final ArgumentType<? extends T> c;

   private boolean u(CommandExecution sender, InputArgument<? extends T> parsed) {
      try {
         return this.d.test(sender, parsed);
      } catch (Throwable var4) {
         return false;
      }
   }

   @Override
   public Stream<String> getTab(CommandExecution sender, List<InputArgument<?>> args) {
      return Stream.concat(super.getTab(sender, args), this.c.getTab(sender, args));
   }

   public KalamaHelperHelperN(String argsName, ArgumentType<? extends T> delegate, T defaultValue) {
      this(argsName, delegate, (T)defaultValue, (execution, argument) -> true);
   }

   @Nullable
   @Override
   public InputArgument<T> consume(CommandExecution sender, List<InputArgument<?>> args, ArgumentReader reader) {
      int var4 = reader.b();
      if (!reader.hasNext()) {
         return this.v(reader, var4);
      } else {
         InputArgument var5 = this.c.consume(sender, args, reader);
         if (var5 != null && var5.k() && this.u(sender, var5)) {
            try {
               return new EntityArgumentResult<>((T)var5.g(), this, reader, var4, var5.a(), true);
            } catch (Throwable var7) {
            }
         }

         reader.c(var4);
         return this.v(reader, var4);
      }
   }

   private EntityArgumentResult<T> v(ArgumentReader reader, int startIndex) {
      return new EntityArgumentResult<>(this.m, this, reader, startIndex, this.m == null ? null : String.valueOf(this.m), true);
   }

   public KalamaHelperHelperN(
      String argsName, ArgumentType<? extends T> delegate, T defaultValue, BiPredicate<CommandExecution, InputArgument<? extends T>> predicate
   ) {
      super(argsName);
      this.c = delegate;
      this.m = (T)defaultValue;
      this.d = predicate;
   }

   public KalamaHelperHelperN(String argsName, ArgumentType<? extends T> delegate, T defaultValue, Predicate<InputArgument<? extends T>> predicate) {
      this(argsName, delegate, (T)defaultValue, (execution, argument) -> predicate.test(argument));
   }
}
