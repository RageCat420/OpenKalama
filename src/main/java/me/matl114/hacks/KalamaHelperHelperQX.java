package me.matl114.hacks;

import java.util.List;
import me.matl114.commands.MainCommand;
import me.matl114.managers.Tasks;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperH;
import me.matl114.utils.commands.params.ArgumentInputStream;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.CommandExecution;
import org.apache.commons.lang3.mutable.MutableInt;

class KalamaHelperHelperQX implements KalamaHelperHelperH {
   private final KalamaHelperHelperNX this$0;

   KalamaHelperHelperQX(final KalamaHelperHelperNX this$0) {
      this.this$0 = this$0;
   }

   @Override
   public boolean a(CommandExecution var1, ArgumentInputStream streamArgs, ArgumentReader argsReader) {
      int var4 = streamArgs.nextInt();
      int var5 = streamArgs.nextInt();
      String[] var6 = argsReader.k();
      MutableInt var7 = new MutableInt(0);
      Tasks.q(() -> {
         if (ChatTasks.j.world != null && ChatTasks.j.player != null) {
            MainCommand.dispatchCommand(var6);
            return var7.incrementAndGet() >= var5;
         } else {
            return true;
         }
      }, 0, var4);
      return true;
   }

   @Override
   public List<String> b(CommandExecution var1, ArgumentInputStream streamArgs, ArgumentReader argsReader) {
      ArgumentReader var4 = new ArgumentReader(argsReader.k());
      var4.d();
      return this.this$0.e(var1, argsReader);
   }

}
