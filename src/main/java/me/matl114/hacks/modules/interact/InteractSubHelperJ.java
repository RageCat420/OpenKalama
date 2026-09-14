package me.matl114.hacks.modules.interact;

import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.commands.params.types.ExecutePos;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;

public record InteractSubHelperJ(InteractSubHelperC hand, ExecutePos target) implements InteractSubHelperT {

    public ExecutePos ahl() {
        return this.target;
    }

    @Override
    public InteractSubHelperC c() {
        return this.hand;
    }

    @Override
    public String a() {
        return "mine";
    }

    @Override
    public void execute(InteractManager manager, PlayerEntity player) {
        Vector3d var3 = this.target.wu(PlayerStateManager.oq());
        BlockPos var4 = new BlockPos((int) var3.x, (int) var3.y, (int) var3.z);
        KalamaHelperHelperK var5 = this.hand.Uv();
        if (var5 != null) {
            Runnable var6 = InvExtra.INSTANCE.swapInventoryIndexToHand(var5.index());
            if (var6 != null) {
                MinecraftClient.getInstance()
                        .interactionManager
                        .attackBlock(
                                var4,
                                Direction.getFacing(MinecraftClient.getInstance()
                                        .player
                                        .getEyePos()
                                        .subtract(var4.toCenterPos())));
                if (InteractManager.INSTANCE.logAction.get()) {
                    manager.logI18NSub("Interact", "message.module.interact-manager.interact.mine", new Object[] {
                        ChatUtils.t(Vec3d.of(var4))
                    });
                }

                var6.run();
            }
        } else if (InteractManager.INSTANCE.logAction.get()) {
            manager.logI18NSub(
                    "Interact", "message.module.interact-manager.interact.no-item", new Object[] {this.hand.toString()
                    });
        }
    }
}
