package me.matl114.hacks;

import java.awt.Color;
import me.matl114.utils.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class KalamaHelperHelperT implements KalamaHelperHelperRX {
   Color c;
   Vec3d[] m;

   public Color color() {
      return this.c;
   }

   public KalamaHelperHelperT abcd(Vec3d[] abcd) {
      this.m = abcd;
      return this;
   }

   public Vec3d[] H() {
      return this.m;
   }

   public KalamaHelperHelperT J(Color color) {
      this.c = color;
      return this;
   }

   public KalamaHelperHelperT(Vec3d abcd, Vec3d b, Vec3d c, Vec3d d, Color color) {
      this.m = new Vec3d[]{abcd, b, c, d};
      this.c = color;
   }

   @Override
   public void render(MatrixStack stack, float partialTicks) {
      RenderUtils.drawQuad(stack, this.m[0], this.m[1], this.m[2], this.m[3], new Color(this.c.getRed(), this.c.getGreen(), this.c.getBlue(), 64));
   }
}
