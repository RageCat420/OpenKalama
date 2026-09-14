package me.matl114.hacks.utils.entity;

import me.matl114.events.Event;
import net.minecraft.util.math.Vec3d;

public interface HackUtilHelperJ extends Comparable<HackUtilHelperJ> {
   int QI = 2147483646;
   int QE = -100000;
   int QH = 10000000;
   int QF = 0;
   int QG = 100000;

   default int priority() {
      return 0;
   }

   default boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
      return true;
   }

   default void gz(Event<LegalMovementManager> movementManagerEvent) {
   }

   default void bb(Event<LegalMovementManager> movementManagerEvent) {
   }

   default int compareTo(HackUtilHelperJ var1) {
      return this.priority() - var1.priority();
   }

   default boolean shouldApply(Event<LegalMovementManager> movementManagerEvent) {
      this.mP(movementManagerEvent);
      LegalMovementManager var2 = (LegalMovementManager)movementManagerEvent.e();
      this.bb(movementManagerEvent);
      return true;
   }

   default void iC(Event<LegalMovementManager> movementManagerEvent) {
   }

   default void jI(Event<LegalMovementManager> movementManagerEvent, Event<Vec3d> moveEvent) {
   }

   default void jJ(Event<LegalMovementManager> movementManagerEvent, Event<Vec3d> moveEvent) {
   }

   default void iB(Event<LegalMovementManager> movementManagerEvent) {
   }

   default void mP(Event<LegalMovementManager> movementManagerEvent) {
   }
}
