package me.matl114.hacks;

import java.awt.Color;
import me.matl114.utils.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class KalamaHelperHelperI implements KalamaHelperHelperRX {
   Color c;
   Entity h;

   public KalamaHelperHelperI v(Entity entity) {
      this.h = entity;
      return this;
   }

   public KalamaHelperHelperI w(Color color) {
      this.c = color;
      return this;
   }

   public Color d() {
      return this.c;
   }

   public KalamaHelperHelperI(Entity entity, Color color) {
      this.h = entity;
      this.c = color;
   }

   @Override
   public void render(MatrixStack stack, float partialTicks) {
      RenderUtils.r(stack, this.h.getBoundingBox().getMinPos(), this.h.getBoundingBox().getMaxPos(), this.c);
   }

   public Entity u() {
      return this.h;
   }
}
