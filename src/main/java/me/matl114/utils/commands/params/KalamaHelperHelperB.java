package me.matl114.utils.commands.params;

import java.util.List;
import me.matl114.bukkit.ConfigurationSerializable;
import me.matl114.utils.commands.params.api.ArgumentType;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.commands.params.api.InputArgument;
import me.matl114.utils.commands.params.impl.AbstractArgumentType;

public class KalamaHelperHelperB extends AbstractArgumentType<String> implements ArgumentType<String> {
   public InputArgument<String> consume(CommandExecution execution, List<InputArgument<?>> args, ArgumentReader reader) {
      if (reader.hasNext()) {
         String var4 = reader.f();
         return new ConfigurationSerializable(var4, this, reader, reader.b() - 1);
      } else {
         return new ConfigurationSerializable(this.m, this, reader, reader.b());
      }
   }

   public KalamaHelperHelperB(String argsName) {
      super(argsName);
   }
}
