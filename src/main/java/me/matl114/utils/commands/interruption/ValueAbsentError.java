package me.matl114.utils.commands.interruption;

import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.CommandExecution;

public class ValueAbsentError extends ArgumentException {
   public ArgumentReader Ez;
   public String Ey;

   public ValueAbsentError(String argumentName, ArgumentReader argumentReader) {
      this.Ey = argumentName;
      this.Ez = argumentReader;
   }

   public ValueAbsentError(ArgumentReader argumentReader, String argumentName) {
      this.Ey = argumentName;
      this.Ez = argumentReader;
   }

   @Override
   public void handleAbort(CommandExecution sender, InterruptionHandler command) {
      command.bw(sender, this.Ez, this.Ey);
   }
}
