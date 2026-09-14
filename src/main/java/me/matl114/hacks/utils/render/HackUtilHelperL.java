package me.matl114.hacks.utils.render;

import me.matl114.utils.render.Quad;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public record HackUtilHelperL(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
   public double maxY() {
      return this.maxY;
   }

   public HackUtilHelperL(Vec3d min, Vec3d max) {
      this(min.x, min.y, min.z, max.x, max.y, max.z);
   }

   public double maxZ() {
      return this.maxZ;
   }

   public double minY() {
      return this.minY;
   }

   public double minZ() {
      return this.minZ;
   }

   public double minX() {
      return this.minX;
   }

   public double maxX() {
      return this.maxX;
   }

   public Quad acm() {
      if (RenderElements.isFlat(this.maxX, this.minX)) {
         double var4 = this.maxX;
         return new Quad(
            new Vec3d(var4, this.minZ, this.maxY), new Vec3d(var4, this.minY, this.maxY), new Vec3d(var4, this.minY, this.maxZ), new Vec3d(var4, this.minZ, this.maxZ)
         );
      } else if (RenderElements.isFlat(this.minZ, this.minY)) {
         double var3 = this.minZ;
         return new Quad(
            new Vec3d(this.maxX, var3, this.maxY), new Vec3d(this.minX, var3, this.maxY), new Vec3d(this.minX, var3, this.maxZ), new Vec3d(this.maxX, var3, this.maxZ)
         );
      } else if (RenderElements.isFlat(this.maxY, this.maxZ)) {
         double var1 = this.maxY;
         return new Quad(
            new Vec3d(this.maxX, this.minZ, var1), new Vec3d(this.minX, this.minZ, var1), new Vec3d(this.minX, this.minY, var1), new Vec3d(this.maxX, this.minY, var1)
         );
      } else {
         throw new IllegalStateException("Quad is not a face");
      }
   }

   public HackUtilHelperL(Box box, Direction direction) {
      double var10001 = switch (direction) {
         case WEST -> box.getMinPos().x;
         case EAST -> box.getMaxPos().x;
         default -> box.getMinPos().x;
      };

      double var10002 = switch (direction) {
         case DOWN -> box.getMinPos().y;
         case UP -> box.getMaxPos().y;
         default -> box.getMinPos().y;
      };

      double var10003 = switch (direction) {
         case NORTH -> box.getMinPos().z;
         case SOUTH -> box.getMaxPos().z;
         default -> box.getMinPos().z;
      };

      double var10004 = switch (direction) {
         case WEST -> box.getMinPos().x;
         case EAST -> box.getMaxPos().x;
         default -> box.getMaxPos().x;
      };

      double var10005 = switch (direction) {
         case DOWN -> box.getMinPos().y;
         case UP -> box.getMaxPos().y;
         default -> box.getMaxPos().y;
      };

      this(var10001, var10002, var10003, var10004, var10005, switch (direction) {
         case NORTH -> box.getMinPos().z;
         case SOUTH -> box.getMaxPos().z;
         default -> box.getMaxPos().z;
      });
   }

   public HackUtilHelperL offset(Vec3d delta) {
      return new HackUtilHelperL(this.maxX + delta.x, this.minZ + delta.y, this.maxY + delta.z, this.minX + delta.x, this.minY + delta.y, this.maxZ + delta.z);
   }

   public HackUtilHelperL(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      super();
      minX = RenderElements.normalizeZero(Math.min(minX, maxX));
      minY = RenderElements.normalizeZero(Math.min(minY, maxY));
      minZ = RenderElements.normalizeZero(Math.min(minZ, maxZ));
      maxX = RenderElements.normalizeZero(Math.max(minX, maxX));
      maxY = RenderElements.normalizeZero(Math.max(minY, maxY));
      maxZ = RenderElements.normalizeZero(Math.max(minZ, maxZ));
      this.maxX = minX;
      this.minZ = minY;
      this.maxY = minZ;
      this.minX = maxX;
      this.minY = maxY;
      this.maxZ = maxZ;
   }
}
