package me.matl114.utils.commands.params.impl;

import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.ArgumentType;
import me.matl114.utils.commands.params.types.EntitySelector;

public class OptionalArgumentResult extends AbstractArgumentResult<EntitySelector> {
   private final String rawString;

   public String k() {
      return this.rawString;
   }

   public OptionalArgumentResult(EntitySelector selector, ArgumentType<EntitySelector> type, ArgumentReader reader, int startIndex, boolean parseSuccess) {
      super(selector, type, reader, startIndex);
      this.h = parseSuccess;
      this.rawString = selector == null ? null : String.join(" ", reader.getArgsInRange(startIndex, this.e));
   }

   @Override
   public boolean k() { return false; }

}
