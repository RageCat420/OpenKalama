package me.matl114.versioned.api;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public interface MatrixStack {
   Matrix3f peekNormal();

   void scale(float var1, float var2);

   static MatrixStack of(net.minecraft.client.util.math.MatrixStack matrixStack) {
      return (MatrixStack)matrixStack;
   }

   void pushMatrix();

   void translate(float var1, float var2);

   void multiply3D(Quaternionf var1);

   void popMatrix();

   Matrix4f peek3D();
}
