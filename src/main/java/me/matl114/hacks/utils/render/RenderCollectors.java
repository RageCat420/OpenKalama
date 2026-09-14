package me.matl114.hacks.utils.render;

import java.util.List;
import me.matl114.utils.render.RenderCollector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class RenderCollectors {
    public static double HEIGHT = 9.0;
    public static final MinecraftClient mc = MinecraftClient.getInstance();

    public static RenderCollector<HackUtilHelperB> f() {
        return new HackUtilHelperH();
    }

    public static RenderCollector<List<Vec3d>> e() {
        return new HackUtilHelperA();
    }

    public static RenderCollector<Box> createBoxCollector(
            boolean drawOutline, boolean drawSolid, boolean drawTraceLine) {
        return new HackUtilHelperC(drawSolid, drawOutline, drawTraceLine);
    }

    public static RenderCollector<Box> b() {
        return new HackUtilHelperJ();
    }

    public static RenderCollector<Vec3d> d() {
        return new HackUtilHelperG();
    }

    public static RenderCollector<Box> c() {
        return new HackUtilHelperE();
    }
}
