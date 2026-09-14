package me.matl114.accessors.access;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public interface PlayerInteractBlockC2SPacketAccess {
    void setHand(Hand var1);

    void setBlockHitResult(BlockHitResult var1);

    void setSequence(int var1);

    PlayerInteractBlockC2SPacketAccess.UseContext getUseContext();

    default boolean hasUseContext() {
        return this.getUseContext() != null;
    }

    void setUseContext(PlayerInteractBlockC2SPacketAccess.UseContext var1);

    static PlayerInteractBlockC2SPacketAccess of(PlayerInteractBlockC2SPacket packet) {
        return (PlayerInteractBlockC2SPacketAccess) packet;
    }

    public record UseContext(ItemStack stack, BlockState oldState, ActionResult actionResult, boolean blockPlace) {
        public boolean isEmpty() {
            return this.stack.isEmpty() || !(this.stack.getItem() instanceof BlockItem);
        }

        public BlockPos getPlaceBlockPos(Hand hand, BlockHitResult blockHitResult) {
            return this.oldState.isAir()
                    ? blockHitResult.getBlockPos()
                    : new ItemPlacementContext(MinecraftClient.getInstance().player, hand, this.stack, blockHitResult)
                            .getBlockPos();
        }

        public boolean isAccepted() {
            return this.actionResult.isAccepted();
        }
    }
}
