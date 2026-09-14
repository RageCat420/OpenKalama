package me.matl114.hacks;

import me.matl114.events.Event;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;

class KalamaHelperHelperX implements HackUtilHelperJ {
   boolean resetThisTick;

   @Override
   public void iC(Event<LegalMovementManager> movementManagerEvent) {
      if (this.resetThisTick) {
         movementManagerEvent.cancel();
      }
   }

   @Override
   public void iB(Event<LegalMovementManager> movementManagerEvent) {
      if (this.resetThisTick) {
         movementManagerEvent.cancel();
      }
   }

   @Override
   public void bb(Event<LegalMovementManager> movementManagerEvent) {
      if (MovTasks.doingTp) {
         this.resetThisTick = true;
      }
   }

   @Override
   public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
      if (this.resetThisTick) {
         this.resetThisTick = false;
         ((LegalMovementManager)movementManagerEvent.e()).c.restorePos();
         ((LegalMovementManager)movementManagerEvent.e()).c.restoreOnGround();
      }

      return true;
   }

   @Override
   public int priority() {
      return 0;
   }
}
