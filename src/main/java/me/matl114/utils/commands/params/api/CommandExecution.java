package me.matl114.utils.commands.params.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.matl114.utils.commands.interruption.InvalidExecutorError;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.joml.Vector2f;
import org.joml.Vector3d;

public interface CommandExecution {
    CommandExecution EMPTY = new KalamaHelperHelperB(null);

    Vector2f so();

    @Nonnull
    Vector3d sp();

    @Nonnull
    default PlayerEntity ss() {
        if (this.isPlayer()) {
            return this.si();
        } else {
            throw new InvalidExecutorError(false);
        }
    }

    static CommandExecution sender(@Nonnull PlayerEntity sender) {
        return new KalamaHelperHelperB(sender);
    }

    @Nonnull
    World sr();

    default boolean isPlayer() {
        return this.si() instanceof PlayerEntity;
    }

    default Vector3d getExecuteEyePos() {
        PlayerEntity var1 = this.si();
        return var1 instanceof PlayerEntity ? this.sp().add(0.0, var1.getEyeHeight(var1.getPose()), 0.0) : this.sp();
    }

    void sm(@Nonnull String var1);

    @Nullable
    PlayerEntity si();

    boolean hasPermission(String var1);

    void sn(Text var1);
}
