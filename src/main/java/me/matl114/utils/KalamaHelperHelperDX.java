package me.matl114.utils;

import net.minecraft.util.math.Vec3d;

public record KalamaHelperHelperDX(Vec3d pos) implements KalamaHelperHelperAd {
   public Vec3d jk() {
      return this.pos;
   }

   @Override
   public String type() {
      return "Pos";
   }
}
