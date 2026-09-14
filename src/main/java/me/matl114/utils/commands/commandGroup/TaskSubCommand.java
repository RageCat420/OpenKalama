package me.matl114.utils.commands.commandGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import me.matl114.utils.commands.params.ArgumentInputStream;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.KalamaHelperHelperA;
import me.matl114.utils.commands.params.api.CommandExecution;

public class TaskSubCommand extends SubCommandImpl {
   KalamaHelperHelperH u;

   @Override
   public Stream<String> f(CommandExecution sender, ArgumentReader arguments) {
      return this.be(sender) ? this.getHelp(arguments.m()) : Stream.empty();
   }

   public TaskSubCommand executor(KalamaHelperHelperH executor) {
      this.u = executor;
      return this;
   }

   @Override
   public boolean onCustomCommand(CommandExecution sender, ArgumentReader arguments) {
      return this.u != null && this.u.a(sender, this.b(sender, arguments), arguments);
   }

   public KalamaHelperHelperH cj() {
      return this.u;
   }

   public TaskSubCommand(String name, KalamaHelperHelperA argsTemplate, String... help) {
      super(name, argsTemplate, help);
   }

   @Override
   public List<String> e(CommandExecution sender, ArgumentReader arguments) {
      ArrayList var3 = new ArrayList();
      if (this.be(sender)) {
         ArgumentInputStream var4 = this.b(sender, arguments);
         var4.getTabComplete(sender).forEach(var3::add);
         if (arguments.hasNext() && this.u != null) {
            var3.addAll(this.u.b(sender, var4, arguments));
         }
      }

      return var3;
   }

}
