package me.matl114.accessors.interfaces;

import me.matl114.utils.world.ContainerPosition;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface TileInventory {
   @Nullable
   BlockPos getPos();

   @Nullable
   ClientWorld getWorld();

   @Nullable
   Block getBlockType();

   @Nullable
   ContainerPosition getContainerPosition();

   @Nullable
   default boolean isVirtual() {
      return this.getContainerPosition() == null;
   }

   static TileInventory of(HandledScreen<?> handledScreen) {
      return (TileInventory)handledScreen;
   }

   HandledScreen<?> castHandled();

   default ScreenHandler castHandler() {
      return this.castHandled().getScreenHandler();
   }

   public interface Handler extends TileInventory {
      @Override
      default HandledScreen<?> castHandled() {
         throw new UnsupportedOperationException();
      }

      @Override
      default ScreenHandler castHandler() {
         return (ScreenHandler)(Object)this;
      }

      void sync(TileInventory var1);
   }
}
