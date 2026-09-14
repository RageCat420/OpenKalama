package me.matl114.utils.commands.params.types;

import me.matl114.utils.commands.params.api.CommandExecution;
import org.joml.Vector3d;

public record KalamaHelperHelperG(Vector3d vector3d) implements ExecutePos {
    public Vector3d ww() {
        return this.vector3d;
    }

    public Vector3d wu(CommandExecution executor) {
        return this.vector3d;
    }

    @Override
    public String asString() {
        return "%.2f %.2f %.2f".formatted(this.vector3d.x, this.vector3d.y, this.vector3d.z);
    }
}
