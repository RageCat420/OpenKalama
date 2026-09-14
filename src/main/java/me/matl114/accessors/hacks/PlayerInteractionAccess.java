package me.matl114.accessors.hacks;

import javax.annotation.Nullable;
import me.matl114.utils.WorldUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public interface PlayerInteractionAccess {
   void startMiningBlock(BlockPos var1, Direction var2);

   default void sendBreakPacket() {
      BlockPos currentPos = this.getCurrentMiningPos();
      this.sendBreakPacket(currentPos);
   }

   default void sendBreakPacket(BlockPos currentPos) {
      Vec3d shouldFacing = currentPos.toCenterPos().subtract(MinecraftClient.getInstance().player.getEyePos());
      Direction dir = Direction.getFacing(shouldFacing).getOpposite();
      this.sendBreakPacket(currentPos, dir);
   }

   void sendBreakPacket(BlockPos var1, Direction var2);

   public boolean breakIfComplete();

   void abortBreak(Direction var1);

   void syncSelectedHotbar(int var1);

   public BlockPos getCurrentMiningPos();

   public void resetCurrentMiningPos();

   public BlockPos getCurrentFailBreakPos();

   public boolean isFailBreakEmpty();

   public int getCurrentMiningTicks();

   default float predictCurrentMiningProgressWithTool(ItemStack tool) {
      BlockPos currentBreakingPos = this.getCurrentMiningPos();
      BlockState block = MinecraftClient.getInstance().world.getBlockState(currentBreakingPos);
      if (block.isAir()) {
         return -1.0F;
      } else {
         float miningSpeed = WorldUtils.getPlayerBlockBreakingSpeedWithCanMineMultiply(MinecraftClient.getInstance().player, block, tool);
         float speed = WorldUtils.calcBlockBreakingDelta(block, MinecraftClient.getInstance().world, currentBreakingPos, miningSpeed);
         int ticksSinceLastStart = this.getCurrentMiningTicks();
         return speed * ticksSinceLastStart;
      }
   }

   public int getFailBreakMiningTicks();

   default float getFailBreakMiningProgress() {
      BlockPos currentFailBreakPos = this.getCurrentFailBreakPos();
      return currentFailBreakPos == null ? -1.0F : this.predictFailMiningProgressWithTool(MinecraftClient.getInstance().player.getMainHandStack(), 0);
   }

   default float predictFailMiningProgressWithTool(ItemStack tool, int extraTick) {
      BlockPos currentBreakingPos = this.getCurrentFailBreakPos();
      BlockState block = MinecraftClient.getInstance().world.getBlockState(currentBreakingPos);
      if (block.isAir()) {
         return -1.0F;
      } else {
         float miningSpeed = WorldUtils.getPlayerBlockBreakingSpeedWithCanMineMultiply(MinecraftClient.getInstance().player, block, tool);
         float speed = WorldUtils.calcBlockBreakingDelta(block, MinecraftClient.getInstance().world, currentBreakingPos, miningSpeed);
         int ticksSinceLastStart = this.getFailBreakMiningTicks() + extraTick;
         return speed * ticksSinceLastStart;
      }
   }

   float getCurrentMiningProgress(@Nullable ItemStack var1);

   default void sendStartBreakPacket(BlockPos pos, Direction direction) {
      this.startMiningBlock(pos, direction);
   }

   default void sendStartBreakPacket(BlockPos pos) {
      Vec3d shouldFacing = pos.toCenterPos().subtract(MinecraftClient.getInstance().player.getEyePos());
      Direction direction = Direction.getFacing(shouldFacing).getOpposite();
      this.sendStartBreakPacket(pos, direction);
   }

   default void sendAbortBreakPacket() {
      this.abortBreak(Direction.DOWN);
   }

   boolean sendFailBreakCurrentPos(@Nullable Direction var1);

   public int getMiningCooldown();

   void setMiningCooldown(int var1);

   ActionResult simulateInteractBlock(Hand var1, BlockHitResult var2);

   ActionResult simulateInteractItem(Hand var1);

   public static PlayerInteractionAccess of(ClientPlayerInteractionManager manager) {
      return (PlayerInteractionAccess)manager;
   }
}
