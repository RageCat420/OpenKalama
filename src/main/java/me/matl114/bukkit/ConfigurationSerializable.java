package me.matl114.bukkit;

import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.ArgumentType;
import me.matl114.utils.commands.params.api.InputArgument;
import me.matl114.utils.commands.params.impl.AbstractArgumentResult;

public class ConfigurationSerializable extends AbstractArgumentResult<String> implements InputArgument<String> {
   public String serialize() {
      return this.g;
   }

   public ConfigurationSerializable(String string, ArgumentType<String> type, ArgumentReader reader, int startIndex) {
      super(string, type, reader, startIndex);
   }
   @Override
   public boolean k() {
      return this.h;
   }

   @Override
   public String a() {
      return this.g;
   }

}
