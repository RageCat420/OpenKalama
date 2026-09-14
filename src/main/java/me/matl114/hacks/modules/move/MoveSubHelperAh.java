package me.matl114.hacks.modules.move;

import me.matl114.events.Event;

public interface MoveSubHelperAh {
   default void onStart(MoveSubHelperSX state) {
   }

   default TravellingControl$Type getType() {
      return TravellingControl$Type.TEST;
   }

   boolean onTick(Event<Void> var1);

   default void onStop() {
   }
}
