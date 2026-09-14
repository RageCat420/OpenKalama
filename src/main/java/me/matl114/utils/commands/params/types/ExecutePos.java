package me.matl114.utils.commands.params.types;

import me.matl114.utils.commands.params.api.CommandExecution;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;

public interface ExecutePos {
   Vector3d wu(CommandExecution var1);

   static ExecutePos Dc(CommandExecution executor) {
      return new KalamaHelperHelperG(executor.sp());
   }

   String asString();

   static ExecutePos of(Vec3d vec3d) {
      return new KalamaHelperHelperG(new Vector3d(vec3d.x, vec3d.y, vec3d.z));
   }
}
