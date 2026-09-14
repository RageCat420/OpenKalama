package me.matl114.utils.commands.interruption;

import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.CommandExecution;

public class DispatchFailureError extends ArgumentException {
   boolean condition = true;
   ArgumentReader argumentReader;

   public DispatchFailureError(ArgumentReader reader) {
      this.argumentReader = reader;
   }

   public void setCondition(boolean condition) {
      this.condition = condition;
   }

   @Override
   public void handleAbort(CommandExecution sender, InterruptionHandler command) {
      command.bB(sender, this.argumentReader);
   }

   @Override
   public boolean isConditionError() {
      return this.condition;
   }
}
