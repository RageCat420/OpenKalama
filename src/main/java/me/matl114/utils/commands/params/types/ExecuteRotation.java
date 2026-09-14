package me.matl114.utils.commands.params.types;

import me.matl114.utils.commands.params.api.CommandExecution;
import net.minecraft.util.math.Vec2f;
import org.joml.Vector2f;

public interface ExecuteRotation {
    static ExecuteRotation Be(CommandExecution execution) {
        Vector2f var1 = execution.so();
        return new KalamaHelperHelperF(new Vec2f(var1.x, var1.y));
    }

    private static String formatAbsolute(float value) {
        return value == (float) ((long) value) ? String.valueOf((long) value) : "%.1f".formatted(value);
    }

    static ExecuteRotation fixed(float pitch, float yaw) {
        return new KalamaHelperHelperF(new Vec2f(pitch, yaw));
    }

    Vector2f Af(CommandExecution var1);

    static ExecuteRotation Bd(Vec2f rotation) {
        return new KalamaHelperHelperF(rotation == null ? new Vec2f(0.0F, 0.0F) : rotation);
    }

    static ExecuteRotation relative(int flag, float pitch, float yaw) {
        return new KalamaHelperHelperB(flag, new Vec2f(pitch, yaw));
    }

    static ExecuteRotation Bb() {
        return new KalamaHelperHelperB(3, new Vec2f(0.0F, 0.0F));
    }

    default String aog() {
        Vector2f var1 = this.Af(null);
        return "%s %s".formatted(formatAbsolute(var1.x()), formatAbsolute(var1.y()));
    }

    public static String formatPart(boolean relative, float value) {
        if (relative) {
            return value == 0.0F ? "~" : "~" + formatAbsolute(value);
        } else {
            return formatAbsolute(value);
        }
    }
}
