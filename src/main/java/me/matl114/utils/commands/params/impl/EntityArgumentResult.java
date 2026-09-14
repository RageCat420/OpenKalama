package me.matl114.utils.commands.params.impl;

import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.ArgumentType;

public class EntityArgumentResult<T> extends AbstractArgumentResult<T> {
   private final String rawString;

   public String resultAsString() {
      return this.rawString;
   }

   public EntityArgumentResult(T result, ArgumentType<T> type, ArgumentReader reader, int startIndex, String rawString, boolean parseSuccess) {
      super((T)result, type, reader, startIndex);
      this.rawString = rawString;
      this.h = parseSuccess;
   }
   @Override
   public boolean k() {
      return this.resultAsString();
   }




   @Override
   public String a() { return null; }

}
