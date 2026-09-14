package me.matl114.events.annotations;

import java.util.Map;
import org.jetbrains.annotations.NotNull;

public interface Dispatch {
   @NotNull
   Map<String, Object> serialize();

   default Map<String, Object> serialize() { return null; }

}
