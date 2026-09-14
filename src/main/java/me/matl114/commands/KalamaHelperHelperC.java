package me.matl114.commands;

import java.util.List;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperH;
import me.matl114.utils.commands.params.ArgumentInputStream;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.CommandExecution;

class KalamaHelperHelperC implements KalamaHelperHelperH {
   private final MainCommand this$0;

   public List<String> supplyTab(CommandExecution var1, ArgumentInputStream streamArgs, ArgumentReader argsReader) {
      ArgumentReader var4 = new ArgumentReader(argsReader.k());
      var4.d();
      return this.this$0.e(var1, argsReader);
   }

   KalamaHelperHelperC(final MainCommand this$0) {
      this.this$0 = this$0;
   }

   public boolean execute(CommandExecution var1, ArgumentInputStream streamArgs, ArgumentReader argsReader) {
      ArgumentReader var4 = new ArgumentReader(argsReader.k());
      var4.d();
      MainCommand.ba(this.this$0, var1, var4);
      return true;
   }



   @Override
   public boolean a(Object arg0, Object arg1, Object arg2) { return null; }

}
