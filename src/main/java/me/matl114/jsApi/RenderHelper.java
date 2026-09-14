package me.matl114.jsApi;

import java.awt.Color;
import java.util.List;
import me.matl114.events.annotations.Modifiable;
import me.matl114.hacks.KalamaHelperHelperAa;
import me.matl114.hacks.KalamaHelperHelperBX;
import me.matl114.hacks.KalamaHelperHelperDX;
import me.matl114.hacks.KalamaHelperHelperGX;
import me.matl114.hacks.KalamaHelperHelperHX;
import me.matl114.hacks.KalamaHelperHelperI;
import me.matl114.hacks.KalamaHelperHelperK;
import me.matl114.hacks.KalamaHelperHelperRX;
import me.matl114.hacks.KalamaHelperHelperSX;
import me.matl114.hacks.KalamaHelperHelperW;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

@Modifiable
public class RenderHelper {
   public static KalamaHelperHelperRX createBox(double x, double y, double z, double x1, double y1, double z1, Color color) {
      return d(new Vec3d(x, y, z), new Vec3d(x1, y1, z1), color);
   }

   public static KalamaHelperHelperRX h(Object vec3d1, Color color) {
      return new KalamaHelperHelperW(JsHelper.a(vec3d1, Entity.class), color);
   }

   public static KalamaHelperHelperRX f(Object vec3d1, Color color) {
      return new KalamaHelperHelperSX(DataHelper.createVec(vec3d1), color);
   }

   public static KalamaHelperHelperRX createQuad(Object vec3d1, Object vec3d2, Object vec3d3, Object vec3d4, Color color) {
      return new me.matl114.hacks.KalamaHelperHelperT(
         DataHelper.createVec(vec3d1), DataHelper.createVec(vec3d2), DataHelper.createVec(vec3d3), DataHelper.createVec(vec3d4), color
      );
   }

   public static KalamaHelperHelperRX j(Object vec3d1, Object vec3d2, Color color) {
      return new KalamaHelperHelperBX(DataHelper.createVec(vec3d1), DataHelper.createVec(vec3d2)).g(color);
   }

   public static KalamaHelperHelperGX builder() {
      return new KalamaHelperHelperGX();
   }

   public static KalamaHelperHelperRX i(Object vec3d1, Color color) {
      return new KalamaHelperHelperK(JsHelper.a(vec3d1, Entity.class), color);
   }

   public static KalamaHelperHelperRX g(Object vec3d1, Color color) {
      return new KalamaHelperHelperI(JsHelper.a(vec3d1, Entity.class), color);
   }

   public static KalamaHelperHelperRX k(List vec3d1, Color color) {
      return new KalamaHelperHelperAa(vec3d1.stream().map(DataHelper::createVec).toList(), color);
   }

   public static KalamaHelperHelperRX d(Object vec3d1, Object vec3d2, Color color) {
      return new KalamaHelperHelperDX(DataHelper.createVec(vec3d1), DataHelper.createVec(vec3d2), color);
   }

   public static KalamaHelperHelperRX e(Object vec3d1, Object vec3d2, Color color) {
      return new KalamaHelperHelperHX(DataHelper.createVec(vec3d1), DataHelper.createVec(vec3d2), color);
   }

   public static Color color(int x, int y, int z) {
      return new Color(x, y, z);
   }
}
