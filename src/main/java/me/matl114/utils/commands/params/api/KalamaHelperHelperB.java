package me.matl114.utils.commands.params.api;

import me.matl114.utils.ChatUtils;
import me.matl114.utils.Debug;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector3d;

public record KalamaHelperHelperB(PlayerEntity sender) implements CommandExecution {
    @Override
    public void sn(Text message) {
        if (this.sender != null) {
            Debug.sendPlayer(message);
        }
    }

    public PlayerEntity Iz() {
        return this.sender;
    }

    @Override
    public void sm(@NotNull String message) {
        if (this.sender != null) {
            Debug.sendPlayer(ChatUtils.textFromLegacyString(message));
        }
    }

    @Nullable
    @Override
    public PlayerEntity si() {
        return this.sender;
    }

    public Vector2f getExecuteRot() {
        PlayerEntity var1 = this.sender;
        return var1 instanceof PlayerEntity ? new Vector2f(var1.getPitch(), var1.getYaw()) : new Vector2f(0.0F, 0.0F);
    }

    public Vector3d getExecutePos() {
        PlayerEntity var1 = this.sender;
        return var1 instanceof PlayerEntity
                ? new Vector3d(var1.getX(), var1.getY(), var1.getZ())
                : new Vector3d(0.0, 0.0, 0.0);
    }

    public World getExecuteWorld() {
        PlayerEntity var1 = this.sender;
        return (World) (var1 instanceof PlayerEntity ? var1.getEntityWorld() : MinecraftClient.getInstance().world);
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public World sr() {
        return null;
    }

    public Vector3d sp() {
        return null;
    }

    public Vector2f so() {
        return null;
    }
}
