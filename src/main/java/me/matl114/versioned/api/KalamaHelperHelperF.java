package me.matl114.versioned.api;

import java.util.List;
import me.matl114.utils.render.ColorQuad;
import me.matl114.utils.render.Quad;
import me.matl114.utils.render.UV;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.Vec3d;

public interface KalamaHelperHelperF {
    @KalamaHelperHelperB(a = "PositionColorNormalLineWidth")
    void drawLines(net.minecraft.client.util.math.MatrixStack var1, VertexConsumer var2, List<Vec3d> var3, int var4);

    @KalamaHelperHelperB(a = "PositionColorNormalLineWidth")
    void e(net.minecraft.client.util.math.MatrixStack var1, VertexConsumer var2, Vec3d var3, Vec3d var4, int var5);

    @KalamaHelperHelperB(a = "PositionColor")
    void drawQuad(net.minecraft.client.util.math.MatrixStack var1, VertexConsumer var2, Quad var3, ColorQuad var4);

    @KalamaHelperHelperB(a = "PositionTextureColor", b = "TexturedGui")
    void drawTexturedQuad(
            net.minecraft.client.util.math.MatrixStack var1, VertexConsumer var2, Quad var3, UV var4, ColorQuad var5);

    @KalamaHelperHelperB(a = "PositionColorNormalLineWidth", b = "Lines")
    void a(net.minecraft.client.util.math.MatrixStack var1, VertexConsumer var2, Vec3d var3, Vec3d var4, int var5);

    @KalamaHelperHelperB(a = "PositionColor", b = "Quad")
    void b(net.minecraft.client.util.math.MatrixStack var1, VertexConsumer var2, Vec3d var3, Vec3d var4, int var5);
}
