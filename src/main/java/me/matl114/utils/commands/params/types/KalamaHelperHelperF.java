package me.matl114.utils.commands.params.types;

import me.matl114.utils.commands.params.api.CommandExecution;
import net.minecraft.util.math.Vec2f;
import org.joml.Vector2f;

public record KalamaHelperHelperF(Vec2f rotation) implements ExecuteRotation {
    public Vec2f Ag() {
        return this.rotation;
    }

    public Vector2f Af(CommandExecution execution) {
        return this.rotation == null ? new Vector2f(0.0F, 0.0F) : new Vector2f(this.rotation.x, this.rotation.y);
    }
}
