package me.matl114.utils.commands.params.types;

import me.matl114.utils.commands.params.api.CommandExecution;
import net.minecraft.util.math.Vec2f;
import org.joml.Vector2f;

public record KalamaHelperHelperB(int flag, Vec2f rotation) implements ExecuteRotation {
   public Vec2f Ag() {
      return this.rotation;
   }

   public int wv() {
      return this.flag;
   }

   public Vector2f Af(CommandExecution execution) {
      Vector2f var2 = execution.so();
      Vec2f var3 = this.rotation == null ? new Vec2f(0.0F, 0.0F) : this.rotation;
      return new Vector2f((this.flag & 1) != 0 ? var2.x + var3.x : var3.x, (this.flag & 2) != 0 ? var2.y + var3.y : var3.y);
   }

   public String asString() {
      Vec2f var1 = this.rotation == null ? new Vec2f(0.0F, 0.0F) : this.rotation;
      return "%s %s".formatted(ExecuteRotation.formatPart((this.flag & 1) != 0, var1.x), ExecuteRotation.formatPart((this.flag & 2) != 0, var1.y));
   }
}
