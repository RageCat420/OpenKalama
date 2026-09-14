package me.matl114.hacks.modules.move;

import java.util.Optional;
import me.matl114.utils.EntityUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;

public class MoveSubHelperSX {
    public boolean g;
    public Travel f;
    public long startingTime;
    public Vec3d c;
    public Optional<Vec3d> a;
    public boolean e = false;
    public boolean h;
    public MoveSubHelperOX d;

    public boolean shouldNotRun() {
        return MinecraftClient.getInstance().player == null || this.h || this.e;
    }

    public void onStart(Travel instance, Vec3d startPos) {
        this.startingTime = System.currentTimeMillis();
        this.e = false;
        this.g = false;
        this.h = false;
        this.f = instance;
        this.c = startPos;
    }

    public MoveSubHelperSX() {
        this.g = false;
    }

    public Vec3d getCurrentFlyingTarget() {
        return this.a.orElseGet(() -> {
            Vec3d var0 = MinecraftClient.getInstance().player.getPos();
            Vec3d var1 = EntityUtils.pitchYawToRotation(
                    0.0F, MinecraftClient.getInstance().player.getYaw());
            return var0.add(var1.multiply(10000.0)).withAxis(Axis.Y, var0.getY());
        });
    }
}
