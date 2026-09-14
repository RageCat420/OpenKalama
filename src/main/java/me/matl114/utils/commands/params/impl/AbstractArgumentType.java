package me.matl114.utils.commands.params.impl;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import me.matl114.utils.commands.interruption.ArgumentException;
import me.matl114.utils.commands.params.api.ArgumentType;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.commands.params.api.InputArgument;

public class AbstractArgumentType<T> implements ArgumentType<T> {
   public me.matl114.utils.commands.params.api.KalamaHelperHelperF l = me.matl114.utils.commands.params.api.KalamaHelperHelperF.a;
   private final String k;
   protected T m = (T)null;

   public void aE(me.matl114.utils.commands.params.api.KalamaHelperHelperF tabCompletor) {
      this.l = tabCompletor;
   }

   @Override
   public String getArgsName() {
      return this.k;
   }

   public AbstractArgumentType(String argsName) {
      this.k = argsName;
   }

   public me.matl114.utils.commands.params.api.KalamaHelperHelperF aD() {
      return this.l;
   }

   public T aF() {
      return this.m;
   }

   protected Stream<String> aC(Stream<String> stream, List<InputArgument<?>> args) {
      if (!args.isEmpty()) {
         InputArgument var3 = (InputArgument)args.get(args.size() - 1);
         String var4 = (var3 != null && var3.h() != null ? var3.h() : "").toLowerCase(Locale.ROOT);
         stream = stream.filter(s -> s.toLowerCase(Locale.ROOT).contains(var4));
      }

      return stream;
   }

   @Override
   public Stream<String> getTab(CommandExecution sender, List<InputArgument<?>> args) {
      try {
         Stream var3 = this.l.b(sender, args);
         return this.aC(var3, args);
      } catch (ArgumentException var4) {
         return Stream.empty();
      }
   }

   public void aG(T defaultValue) {
      this.m = (T)defaultValue;
   }

   public InputArgument<T> consume(Object arg0, Object arg1, Object arg2) { return null; }

}
