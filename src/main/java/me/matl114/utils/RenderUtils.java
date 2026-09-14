package me.matl114.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.List;
import java.util.function.Function;
import me.matl114.events.annotations.Modifiable;
import me.matl114.utils.render.ColorQuad;
import me.matl114.utils.render.Quad;
import me.matl114.utils.world.RegionPos;
import me.matl114.versioned.api.VRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity.BillboardMode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

public class RenderUtils {
   private static float storedLineWidth;
   private static final MinecraftClient mc = MinecraftClient.getInstance();
   private static boolean startVirtual = false;

   public static Vector2d getScreenSize() {
      int var0 = mc.getWindow().getScaledWidth();
      int var1 = mc.getWindow().getScaledHeight();
      return new Vector2d(var0, var1);
   }

   public static void q(MatrixStack matrix, Vec3d from, Vec3d to, Color color) {
      VRender.getInstance().r(matrix, from, to, color);
   }

   @Modifiable
   public static Quaternionf getBillboardRotation(BillboardMode renderState, float pitch, float yaw) {
      Quaternionf var3 = new Quaternionf();
      Camera var4 = mc.gameRenderer.getCamera();

      return switch (renderState) {
         case FIXED -> var3.rotationYXZ((float) (-Math.PI / 180.0) * yaw, (float) (Math.PI / 180.0) * pitch, 0.0F);
         case HORIZONTAL -> var3.rotationYXZ((float) (-Math.PI / 180.0) * yaw, (float) (Math.PI / 180.0) * A(var4.getPitch()), 0.0F);
         case VERTICAL -> var3.rotationYXZ((float) (-Math.PI / 180.0) * z(var4.getYaw()), (float) (Math.PI / 180.0) * pitch, 0.0F);
         case CENTER -> var3.rotationYXZ((float) (-Math.PI / 180.0) * z(var4.getYaw()), (float) (Math.PI / 180.0) * A(var4.getPitch()), 0.0F);
         default -> throw new MatchException((String)null, (Throwable)null);
      };
   }

   public static void s(MatrixStack matrix4f, Vec3d a, Vec3d b, Vec3d c, Vec3d d, Color color) {
      VRender.getInstance().t(matrix4f, new Quad(a, b, c, d), ColorQuad.of(color.getRGB()));
   }

   @Modifiable
   public static Vec3d getTracerOrigin(float partialTicks) {
      return getCameraLookVec(partialTicks).multiply(10.0);
   }

   public static void drawOutlinedBox(MatrixStack matrix, Vec3d from, Vec3d to, Color color) {
      Vec3d var4 = getCameraPos();
      q(matrix, from.subtract(var4), to.subtract(var4), color);
   }

   public static void j(MatrixStack matrixStack, List<Vec3d> path, Color color) {
      if (path.size() >= 2) {
         Vec3d var3 = getCameraPos();
         VRender.getInstance().p(matrixStack, path.stream().map(v -> v.subtract(var3)).toList(), color);
      }
   }

   public static Vector2d translate2D(Vec3d pos, float tickProgress) {
      Quaternionf var2 = mc.gameRenderer.getCamera().getRotation().conjugate(new Quaternionf());
      Matrix4f var3 = new Matrix4f().rotation(var2);
      float var4 = (float)mc.gameRenderer.getFov(mc.gameRenderer.getCamera(), tickProgress, true);
      Matrix4f var5 = mc.gameRenderer.getBasicProjectionMatrix(var4);
      Vec3d var6 = getCameraPos();
      return translate3DTo2D(var3, var5, var6, pos, true);
   }

   public static void o(MatrixStack matrixStack, List<Vec3d> pairs, Color color) {
      VRender.getInstance().q(matrixStack, pairs, color);
   }

   @Modifiable
   public static Box getLerpedBox(Entity e, float partialTicks) {
      if (e.isRemoved()) {
         return e.getBoundingBox();
      } else {
         Vec3d var2 = getLerpedPos(e, partialTicks).subtract(e.getPos());
         return e.getBoundingBox().offset(var2);
      }
   }

   @Modifiable
   public static BlockPos getCameraBlockPos() {
      Camera var0 = mc.getBlockEntityRenderDispatcher().camera;
      return var0 == null ? BlockPos.ORIGIN : var0.getBlockPos();
   }

   public static Vector2d translate3DTo2D(Matrix4f cameraMatrix, Matrix4f projectionMatrix, Vec3d camera, Vec3d pos, boolean checkInScreen) {
      Vector4f var5 = new Vector4f((float)(pos.x - camera.x), (float)(pos.y - camera.y), (float)(pos.z - camera.z), 1.0F);
      var5.mul(cameraMatrix);
      var5.mul(projectionMatrix);
      if (checkInScreen && var5.w <= 0.0F) {
         return null;
      } else {
         if (var5.w < 0.0F) {
            var5.w = -var5.w;
         }

         float var6 = var5.x / var5.w;
         float var7 = var5.y / var5.w;
         double var8 = mc.getWindow().getWidth();
         double var10 = mc.getWindow().getHeight();
         double var12 = (var6 * 0.5 + 0.5) * var8;
         double var14 = (1.0 - (var7 * 0.5 + 0.5)) * var10;
         double var16 = mc.getWindow().getScaleFactor();
         double var18 = var12 / var16;
         double var20 = var14 / var16;
         return !Double.isInfinite(var18) && !Double.isInfinite(var20) ? new Vector2d(var18, var20) : null;
      }
   }

   public static void startDrawVirtual(MatrixStack matrixStack) {
      if (!startVirtual) {
         startVirtual = true;
         matrixStack.push();
         GL11.glEnable(3042);
         GL11.glDisable(2929);
         storedLineWidth = RenderSystem.getShaderLineWidth();
         RenderSystem.lineWidth(2.0F);
         GL11.glDepthMask(false);
      }
   }

   public static void n(MatrixStack matrixStack, List<Vec3d> pairs, Color color) {
      if (pairs.size() >= 2) {
         Vec3d var3 = getCameraPos();
         VRender.getInstance().q(matrixStack, pairs.stream().map(v -> v.subtract(var3)).toList(), color);
      }
   }

   @Modifiable
   public static Vec3d getLerpedPos(Entity e, float partialTicks) {
      if (e.isRemoved()) {
         return e.getPos();
      } else {
         double var2 = MathHelper.lerp(partialTicks, e.lastRenderX, e.getX());
         double var4 = MathHelper.lerp(partialTicks, e.lastRenderY, e.getY());
         double var6 = MathHelper.lerp(partialTicks, e.lastRenderZ, e.getZ());
         return new Vec3d(var2, var4, var6);
      }
   }

   public static void l(MatrixStack matrixStack, Vec3d from, Vec3d to, Color color) {
      n(matrixStack, List.of(from, to), color);
   }

   @Modifiable
   private static float z(float yaw) {
      return yaw - 180.0F;
   }

   @Modifiable
   private static float A(float pitch) {
      return -pitch;
   }

   public static void drawQuad(MatrixStack matrix4f, Vec3d a, Vec3d b, Vec3d c, Vec3d d, Color color) {
      Vec3d var6 = getCameraPos();
      s(matrix4f, a.subtract(var6), b.subtract(var6), c.subtract(var6), d.subtract(var6), color);
   }

   public static Function<Vec3d, Vector2d> createProjector(Matrix4f cam, Matrix4f proj, boolean checkInScreen) {
      Vec3d var3 = getCameraPos();
      return v -> translate3DTo2D(cam, proj, var3, v, checkInScreen);
   }

   public static void k(MatrixStack matrixStack, List<Vec3d> path, Color color) {
      VRender.getInstance().p(matrixStack, path, color);
   }

   @Deprecated
   public static void resetCurrentShaderColor() {
   }

   @Modifiable
   public static void applyRegionalRenderOffset(MatrixStack matrixStack, RegionPos region) {
      Vec3d var2 = region.xs().subtract(getCameraPos());
      matrixStack.translate(var2.x, var2.y, var2.z);
   }

   public static void r(MatrixStack matrix, Vec3d from, Vec3d to, Color color) {
      Vec3d var4 = getCameraPos();
      VRender.getInstance().s(matrix, from.subtract(var4), to.subtract(var4), color);
   }

   public static void m(MatrixStack matrixStack, Vec3d from, Vec3d to, Color color) {
      o(matrixStack, List.of(from, to), color);
   }

   @Modifiable
   public static Vec3d getCameraEntityPos() {
      Camera var0 = mc.gameRenderer.getCamera();
      if (var0 == null) {
         return Vec3d.ZERO;
      } else {
         Entity var1 = var0.getFocusedEntity();
         return var1 == null ? var0.getPos() : var1.getPos();
      }
   }

   @Modifiable
   public static Vec3d getLerpedDelta(Entity e, float partialTicks) {
      return getLerpedPos(e, partialTicks).subtract(e.getPos());
   }

   @Modifiable
   public static Vec3d getCameraPos() {
      Camera var0 = mc.getBlockEntityRenderDispatcher().camera;
      return var0 == null ? Vec3d.ZERO : var0.getPos();
   }

   @Modifiable
   public static Vec3d getCameraLookVec(float partialTicks) {
      Camera var1 = mc.gameRenderer.getCamera();
      Vector3f var2 = var1.getHorizontalPlane();
      return new Vec3d(var2.x(), var2.y(), var2.z());
   }

   @Modifiable
   public static RegionPos f() {
      return RegionPos.xp(getCameraBlockPos());
   }

   public static Function<Vec3d, Vector2d> D(Matrix4f cam, Matrix4f proj) {
      return createProjector(cam, proj, true);
   }

   @Modifiable
   public static VertexConsumer B(VertexConsumer vertexConsumer, Sprite sprite) {
      return new KalamaHelperHelperAp(vertexConsumer, sprite);
   }

   public static boolean stopDrawVirtual(MatrixStack matrixStack) {
      if (startVirtual) {
         startVirtual = false;
         resetCurrentShaderColor();
         GL11.glDisable(3042);
         GL11.glEnable(2929);
         RenderSystem.lineWidth(storedLineWidth);
         GL11.glDepthMask(true);
         matrixStack.pop();
         return true;
      } else {
         return false;
      }
   }
}
