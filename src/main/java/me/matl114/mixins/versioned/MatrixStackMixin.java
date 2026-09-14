package me.matl114.mixins.versioned;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({MatrixStack.class})
public abstract class MatrixStackMixin implements me.matl114.versioned.api.MatrixStack {
   @Shadow
   public void method_22903() { }

   @Shadow
   public void method_22909() { }

   @Shadow
public abstract Entry method_23760() ;

   @Shadow
   public void method_46416(float var1, float var2, float var3) { }

   @Shadow
   public void method_22905(float var1, float var2, float var3) { }

   @Shadow
   public void method_22907(Quaternionf var1) { }

   @Override
   public void pushMatrix() {
      this.method_22903();
   }

   @Override
   public void popMatrix() {
      this.method_22909();
   }

   @Override
   public Matrix4f peek3D() {
      return this.method_23760().getPositionMatrix();
   }

   @Override
   public Matrix3f peekNormal() {
      return this.method_23760().getNormalMatrix();
   }

   @Override
   public void translate(float x, float y) {
      this.method_46416(x, y, 0.0F);
   }

   public void translateZ(float z) {
      this.method_46416(0.0F, 0.0F, z);
   }

   @Override
   public void scale(float x, float y) {
      this.method_22905(x, y, 1.0F);
   }

   @Override
   public void multiply3D(Quaternionf quaternion) {
      this.method_22907(quaternion);
   }
}

