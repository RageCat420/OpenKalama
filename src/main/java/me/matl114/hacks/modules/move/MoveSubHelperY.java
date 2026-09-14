package me.matl114.hacks.modules.move;

import me.matl114.events.Event;
import me.matl114.hacks.MovTasks$MovInfo;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import net.minecraft.util.math.Vec3d;

public class MoveSubHelperY implements HackUtilHelperJ {
   boolean Mc;
   NoFall module;
   boolean Md;
   int counter = 0;

   public MoveSubHelperY(NoFall module) {
      this.Mc = false;
      this.Md = false;
      this.module = module;
   }

   public void jy(Event<Vec3d> vec3d) {
   }

   public void ji(Event<MovTasks$MovInfo> setBack) {
      this.Mc = false;
   }

   public void AH(Event<Integer> jumpCooldown) {
   }
}
