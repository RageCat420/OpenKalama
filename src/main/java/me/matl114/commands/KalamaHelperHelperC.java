package me.matl114.commands;

import java.util.List;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperH;
import me.matl114.utils.commands.params.ArgumentInputStream;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.CommandExecution;

class KalamaHelperHelperC implements KalamaHelperHelperH {
   private final MainCommand this$0;

   @Override
   public List<String> b(CommandExecution var1, ArgumentInputStream streamArgs, ArgumentReader argsReader) {
      ArgumentReader var4 = new ArgumentReader(argsReader.k());
      var4.d();
      return this.this$0.e(var1, argsReader);
   }

   KalamaHelperHelperC(final MainCommand this$0) {
      this.this$0 = this$0;
   }

   @Override
   public boolean a(CommandExecution var1, ArgumentInputStream streamArgs, ArgumentReader argsReader) {
      ArgumentReader var4 = new ArgumentReader(argsReader.k());
      var4.d();
      this.this$0.bM(var1, var4);
      return true;
   }

}