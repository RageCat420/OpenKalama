package me.matl114.utils.commands.params.types;

import me.matl114.utils.commands.params.api.CommandExecution;
import org.joml.Vector3d;

public record KalamaHelperHelperA(int flag, Vector3d vector3d) implements ExecutePos {
    public Vector3d ww() {
        return this.vector3d;
    }

    public int wv() {
        return this.flag;
    }

    public Vector3d wu(CommandExecution executor) {
        Vector3d var2 = executor.sp();
        double var3;
        if ((this.flag & 1) != 0) {
            var3 = var2.x() + this.vector3d.x();
        } else {
            var3 = this.vector3d.x();
        }

        double var5;
        if ((this.flag & 2) != 0) {
            var5 = var2.y() + this.vector3d.y();
        } else {
            var5 = this.vector3d.y();
        }

        double var7;
        if ((this.flag & 4) != 0) {
            var7 = var2.z() + this.vector3d.z();
        } else {
            var7 = this.vector3d.z();
        }

        return new Vector3d(var3, var5, var7);
    }

    @Override
    public String asString() {
        StringBuilder var1 = new StringBuilder();
        if ((this.flag & 1) != 0) {
            var1.append("~");
        }

        if (this.vector3d.x() != 0.0) {
            var1.append("%.1f".formatted(this.vector3d.x()));
        }

        var1.append(" ");
        if ((this.flag & 2) != 0) {
            var1.append("~");
        }

        if (this.vector3d.y() != 0.0) {
            var1.append("%.1f".formatted(this.vector3d.y()));
        }

        var1.append(" ");
        if ((this.flag & 4) != 0) {
            var1.append("~");
        }

        if (this.vector3d.z() != 0.0) {
            var1.append("%.1f".formatted(this.vector3d.z()));
        }

        return var1.toString();
    }
}
