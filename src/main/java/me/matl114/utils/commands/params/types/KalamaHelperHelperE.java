package me.matl114.utils.commands.params.types;

import me.matl114.utils.commands.params.api.CommandExecution;
import org.joml.Vector2f;
import org.joml.Vector3d;

public record KalamaHelperHelperE(Vector3d vector3d) implements ExecutePos {
    public static Vector3d lookCoordTooPos(float pitch, float yaw, double x, double y, double z) {
        Vector2f var8 = new Vector2f(pitch, yaw);
        double var9 = Math.cos((var8.y + 90.0F) * (float) (Math.PI / 180.0));
        double var11 = Math.sin((var8.y + 90.0F) * (float) (Math.PI / 180.0));
        double var13 = Math.cos(-var8.x * (float) (Math.PI / 180.0));
        double var15 = Math.sin(-var8.x * (float) (Math.PI / 180.0));
        double var17 = Math.cos((-var8.x + 90.0F) * (float) (Math.PI / 180.0));
        double var19 = Math.sin((-var8.x + 90.0F) * (float) (Math.PI / 180.0));
        Vector3d var21 = new Vector3d(var9 * var13, var15, var11 * var13);
        Vector3d var22 = new Vector3d(var9 * var17, var19, var11 * var17);
        Vector3d var23 = new Vector3d(var21).cross(new Vector3d(var22)).mul(-1.0);
        double var24 = var21.x * z + var22.x * y + var23.x * x;
        double var26 = var21.y * z + var22.y * y + var23.y * x;
        double var28 = var21.z * z + var22.z * y + var23.z * x;
        return new Vector3d(var24, var26, var28);
    }

    public Vector3d ww() {
        return this.vector3d;
    }

    public Vector3d wu(CommandExecution executor) {
        Vector3d var2 = executor.sp();
        Vector2f var3 = executor.so();
        return var2.add(lookCoordTooPos(var3.x, var3.y, this.vector3d.x, this.vector3d.y, this.vector3d.z));
    }

    @Override
    public String asString() {
        StringBuilder var1 = new StringBuilder();
        var1.append("^");
        if (this.vector3d.x() != 0.0) {
            var1.append("%.1f".formatted(this.vector3d.x()));
        }

        var1.append(" ");
        var1.append("^");
        if (this.vector3d.y() != 0.0) {
            var1.append("%.1f".formatted(this.vector3d.y()));
        }

        var1.append(" ");
        var1.append("^");
        if (this.vector3d.z() != 0.0) {
            var1.append("%.1f".formatted(this.vector3d.z()));
        }

        return var1.toString();
    }
}
