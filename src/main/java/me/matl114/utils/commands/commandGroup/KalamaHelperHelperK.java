package me.matl114.utils.commands.commandGroup;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.CommandExecution;

public class KalamaHelperHelperK extends DelegateSubCommand {
   @Override
   public Stream<String> f(CommandExecution sender, ArgumentReader arguments) {
      if (arguments.hasNext()) {
         String var3 = arguments.g();
         if (this.c().equalsIgnoreCase(var3)) {
            arguments.f();
            return super.f(sender, arguments);
         } else {
            return Stream.empty();
         }
      } else {
         return super.f(sender, arguments).map(s -> this.c() + " " + s);
      }
   }

   @Override
   public boolean onCustomCommand(CommandExecution sender, ArgumentReader arguments) {
      return arguments.hasNext() && this.c().equalsIgnoreCase(arguments.f()) ? super.onCustomCommand(sender, arguments) : false;
   }

   public KalamaHelperHelperK(String name, CustomTabExecutor delegate) {
      super(name, delegate);
   }

   @Override
   public List<String> e(CommandExecution sender, ArgumentReader arguments) {
      if (arguments.hasNext()) {
         String var3 = arguments.f();
         if (this.c().equalsIgnoreCase(var3)) {
            return super.e(sender, arguments);
         } else if (!arguments.hasNext()) {
            String var4 = var3.toLowerCase(Locale.ROOT);
            return this.b.toLowerCase(Locale.ROOT).contains(var4) ? List.of(this.b) : List.of();
         } else {
            return List.of();
         }
      } else {
         return List.of();
      }
   }
}
