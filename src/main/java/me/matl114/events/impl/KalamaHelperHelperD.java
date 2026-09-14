package me.matl114.events.impl;

import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

public record KalamaHelperHelperD(ClientWorld world, BlockPos pos, BlockState oldState, BlockState newState) {
   public BlockPos pos() {
      return this.pos;
   }

   public BlockState oldState() {
      return this.oldState;
   }

   public KalamaHelperHelperD(ClientWorld world, BlockPos pos, BlockState oldState, BlockState newState) {
      this.world = world;
      this.pos = pos;
      this.oldState = oldState;
      this.newState = newState;
   }

   public ClientWorld world() {
      return this.world;
   }
}
