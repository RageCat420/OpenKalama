package me.matl114.utils.commands.params.impl;

import java.util.Optional;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.ArgumentType;
import me.matl114.utils.commands.params.types.ExecuteRotation;

public class KalamaHelperHelperG extends AbstractArgumentResult<ExecuteRotation> {
   String blockEntities;

   @Override
   public String a() {
      return this.blockEntities;
   }

   public KalamaHelperHelperG(Optional<ExecuteRotation> rotation, ArgumentType<ExecuteRotation> type, ArgumentReader reader, int startIndex) {
      super(rotation == null ? null : (ExecuteRotation)rotation.orElse(null), type, reader, startIndex);
      if (rotation == null) {
         this.blockEntities = null;
         this.h = false;
      } else if (this.f && rotation.isPresent()) {
         this.blockEntities = ((ExecuteRotation)rotation.get()).aog();
      } else {
         this.blockEntities = String.join(" ", this.c.getArgsInRange(startIndex, this.e));
      }
   }

}
