package me.matl114.utils.commands.params.impl;

import java.util.Optional;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.ArgumentType;
import me.matl114.utils.commands.params.types.ExecutePos;

public class PosArgumentResult extends AbstractArgumentResult<ExecutePos> {
   String rawString;

   @Override
   public String a() {
      return this.rawString;
   }

   public PosArgumentResult(Optional<ExecutePos> vector3d, ArgumentType<ExecutePos> type, ArgumentReader reader, int startIndex) {
      super(vector3d == null ? null : (ExecutePos)vector3d.orElse(null), type, reader, startIndex);
      if (vector3d == null) {
         this.rawString = null;
         this.h = false;
      } else if (this.f && vector3d.isPresent()) {
         this.rawString = ((ExecutePos)vector3d.get()).asString();
      } else {
         this.rawString = String.join(" ", this.c.getArgsInRange(startIndex, this.e));
      }
   }

}
