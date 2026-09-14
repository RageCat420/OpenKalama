package me.matl114.versioned.api;

import java.awt.Color;
import java.util.List;
import me.matl114.utils.render.ColorQuad;
import me.matl114.utils.render.Quad;
import me.matl114.utils.render.UV;
import me.matl114.versioned.impl.Render_v1_21_1;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.texture.GuiAtlasManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public interface VRender {
   KalamaHelperHelperA a = new KalamaHelperHelperA(false, TextLayerType.SEE_THROUGH, 0, 0);
   VRender INSTANCE = new Render_v1_21_1();
   KalamaHelperHelperD b = new KalamaHelperHelperD(16711935, OverlayTexture.DEFAULT_UV, 0);

   @KalamaHelperHelperB(
      b = "Lines",
      a = "PositionColorNormalLineWidth"
   )
   void h(KalamaHelperHelperJ var1);

   default void r(net.minecraft.client.util.math.MatrixStack matrix, Vec3d from, Vec3d to, Color color) {
      this.h((op, bf) -> op.a(matrix, bf, from, to, color.getRGB()));
   }

   default void w(Sprite sprite, net.minecraft.client.util.math.MatrixStack stack, Quad quad, UV uv, ColorQuad color) {
      this.n(sprite, (op, bf) -> op.drawTexturedQuad(stack, bf, quad, uv, color));
   }

   default void v(Identifier path, net.minecraft.client.util.math.MatrixStack stack, Quad quad, UV uv, ColorQuad color) {
      this.m(path, (op, bf) -> op.drawTexturedQuad(stack, bf, quad, uv, color));
   }

   @KalamaHelperHelperB(
      b = "Rect",
      a = "PositionColor"
   )
   void l(KalamaHelperHelperJ var1, boolean var2);

   void drawTextCameraCoord(OrderedText var1, net.minecraft.client.util.math.MatrixStack var2, Vec3d var3, int var4, Color var5, KalamaHelperHelperA var6);

   default void q(net.minecraft.client.util.math.MatrixStack matrixStack, List<Vec3d> pairs, Color color) {
      this.h((op, bf) -> {
         int var5 = pairs.size();

         for (byte var6 = 1; var6 < var5; var6 += 2) {
            op.e(matrixStack, bf, (Vec3d)pairs.get(var6 - 1), (Vec3d)pairs.get(var6), color.getRGB());
         }
      });
   }

   default void y(net.minecraft.client.util.math.MatrixStack stack, Quad quad, ColorQuad color) {
      this.o((operation, vertexConsumer) -> operation.drawQuad(stack, vertexConsumer, quad, color));
   }

   default void t(net.minecraft.client.util.math.MatrixStack matrix4f, Quad quad, ColorQuad color) {
      this.j((op, bf) -> op.drawQuad(matrix4f, bf, quad, color), false);
   }

   static VRender getInstance() {
      return INSTANCE;
   }

   @KalamaHelperHelperB(
      b = "TexturedGui",
      a = "PositionTextureColor"
   )
   void n(Sprite var1, KalamaHelperHelperJ var2);

   void A(ItemStack var1, net.minecraft.client.util.math.MatrixStack var2, Vec3d var3, ItemDisplayContext var4, KalamaHelperHelperD var5);

   default void p(net.minecraft.client.util.math.MatrixStack matrixStack, List<Vec3d> path, Color color) {
      this.h((op, bf) -> op.drawLines(matrixStack, bf, path, color.getRGB()));
   }

   @KalamaHelperHelperB(
      b = "Quad",
      a = "PositionColor"
   )
   void j(KalamaHelperHelperJ var1, boolean var2);

   @KalamaHelperHelperB(
      b = "TexturedGui",
      a = "PositionTextureColor"
   )
   void m(Identifier var1, KalamaHelperHelperJ var2);

   @KalamaHelperHelperB(
      b = "LineStrip",
      a = "PositionColorNormalLineWidth"
   )
   void i(KalamaHelperHelperJ var1);

   static int createTextPositionFlag(int xAlign, int yAlign) {
      int var2 = xAlign < 0 ? 0 : (xAlign > 0 ? 2 : 1);
      int var3 = yAlign < 0 ? 0 : (yAlign > 0 ? 2 : 1);
      return 3 * var3 + var2;
   }

   @KalamaHelperHelperB(
      b = "Rect",
      a = "PositionColor"
   )
   void k(KalamaHelperHelperJ var1, boolean var2);

   @KalamaHelperHelperB(
      b = "Gui",
      a = "PositionColor"
   )
   void o(KalamaHelperHelperJ var1);

   default void s(net.minecraft.client.util.math.MatrixStack matrix, Vec3d from, Vec3d to, Color color) {
      this.j((op, bf) -> op.b(matrix, bf, from, to, color.getRGB()), true);
   }

   default void drawGuiSpriteQuadCameraCoord(Identifier path, net.minecraft.client.util.math.MatrixStack stack, Quad quad, UV uv, ColorQuad color) {
      GuiAtlasManager var6 = MinecraftClient.getInstance().getGuiAtlasManager();
      Sprite var7 = var6.getSprite(path);
      this.w(var7, stack, quad, uv, color);
   }

   default void o(KalamaHelperHelperJ var1) {  }

}
