package me.matl114.utils.world;

public record CachedShapeData(
   int sizeX,
   int sizeY,
   int sizeZ,
   long[] voxelSet,
   int minFullX,
   int minFullY,
   int minFullZ,
   int maxFullX,
   int maxFullY,
   int maxFullZ,
   boolean isEmpty,
   boolean hasSingleAABB
) {
   public long[] voxelSet() {
      return this.voxelSet;
   }

   public int sizeZ() {
      return this.sizeZ;
   }

   public int minFullX() {
      return this.minFullX;
   }

   public int minFullY() {
      return this.minFullY;
   }

   public int minFullZ() {
      return this.minFullZ;
   }

   public boolean hasSingleAABB() {
      return this.hasSingleAABB;
   }

   public int sizeY() {
      return this.sizeY;
   }

   public boolean isEmpty() {
      return this.isEmpty;
   }

   public int maxFullX() {
      return this.maxFullX;
   }

   public int maxFullZ() {
      return this.maxFullZ;
   }

   public int maxFullY() {
      return this.maxFullY;
   }

   public int sizeX() {
      return this.sizeX;
   }
}
