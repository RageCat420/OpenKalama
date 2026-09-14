package me.matl114.jsApi;

import me.matl114.events.annotations.Modifiable;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Vector3d;
import org.joml.Vector3f;

@Modifiable
public class DataHelper {
   public static String getIdNamespace(Identifier id) {
      return id.getNamespace();
   }

   public static String getIdKey(Identifier id) {
      return id.getPath();
   }

   public static double getVecZ(Vec3d vec3d) {
      return vec3d.z;
   }

   public static Identifier namespacedKey(String id) {
      return Identifier.tryParse(id);
   }

   public static Object d(Object pos) {
      return JsMacrosBridge.i().b(createVec(pos));
   }

   public static Vec3d normalize(Object vec1) {
      return createVec(vec1).normalize();
   }

   public static Vec3d a(double x, double y, double z) {
      return new Vec3d(x, y, z);
   }

   public static double horizontalDistance(Object vec3d) {
      return createVec(vec3d).horizontalLength();
   }

   public static Object b(double x, double y, double z) {
      return JsMacrosBridge.i().b(new Vec3d(x, y, z));
   }

   public static double length(Object vec3d) {
      return createVec(vec3d).length();
   }

   public static BlockPos createBlockPos(Vec3d vec3d) {
      return BlockPos.ofFloored(vec3d);
   }

   public static double lengthSquared(Object vec3d) {
      return createVec(vec3d).lengthSquared();
   }

   public static double squaredDistance(Object vec1, Object vec2) {
      Vec3d var2 = createVec(vec1);
      Vec3d var3 = createVec(vec2);
      return var2.squaredDistanceTo(var3);
   }

   public static Vec3d createVec(Object pos3d) {
      if (pos3d instanceof Vec3d) {
         return (Vec3d)pos3d;
      } else if (pos3d instanceof Vec3i var1) {
         return new Vec3d(var1.getX(), var1.getY(), var1.getZ());
      } else if (pos3d instanceof Vector3d var2) {
         return new Vec3d(var2.x, var2.y, var2.z);
      } else if (pos3d instanceof Vector3f var3) {
         return new Vec3d(var3.x, var3.y, var3.z);
      } else {
         Object var4 = JsMacrosBridge.i().a(pos3d, Object.class);
         if (var4 instanceof Vec3d var5) {
            return var5;
         } else if (var4 instanceof Vec3i var6) {
            return Vec3d.of(var6);
         } else {
            throw new IllegalArgumentException("Unsupported vec3 type: " + pos3d.getClass());
         }
      }
   }

   public static BlockPos f(Object pos3d) {
      return pos3d instanceof BlockPos var1 ? var1 : BlockPos.ofFloored(createVec(pos3d));
   }

   public static double horizontalDistanceSquared(Object vec3d) {
      return createVec(vec3d).horizontalLengthSquared();
   }

   public static BlockPos e(double x, double y, double z) {
      return new BlockPos((int)x, (int)y, (int)z);
   }

   public static double getVecX(Vec3d vec3d) {
      return vec3d.x;
   }

   public static double getVecY(Vec3d vec3d) {
      return vec3d.y;
   }
}
