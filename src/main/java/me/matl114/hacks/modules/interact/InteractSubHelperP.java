package me.matl114.hacks.modules.interact;

import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.commands.params.api.InputArgument;
import me.matl114.utils.commands.params.impl.KalamaHelperHelperG;
import me.matl114.utils.commands.params.impl.OptionalArgumentResult;
import me.matl114.utils.commands.params.impl.PosArgumentResult;
import me.matl114.utils.commands.params.types.EntitySelector;
import me.matl114.utils.commands.params.types.ExecutePos;
import me.matl114.utils.commands.params.types.ExecuteRotation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2f;
import org.joml.Vector3d;

public interface InteractSubHelperP {
    static InteractSubHelperP b(InputArgument<?> inputArgument) {
        if (inputArgument instanceof PosArgumentResult var1) {
            ExecutePos var6 = var1.y();
            return pl -> {
                CommandExecution var2x = PlayerStateManager.oq();
                Vector3d var3x = var6.wu(var2x);
                var3x = var3x.sub(var2x.getExecuteEyePos());
                return EntityUtils.q(new Vec3d(var3x.x(), var3x.y(), var3x.z()));
            };
        } else if (inputArgument instanceof KalamaHelperHelperG var3) {
            ExecuteRotation var5 = var3.y();
            return pl -> {
                CommandExecution var2x = PlayerStateManager.oq();
                Vector2f var3x = var5.Af(var2x);
                return new Vec2f(var3x.x(), var3x.y());
            };
        } else if (inputArgument instanceof OptionalArgumentResult var4) {
            EntitySelector var2 = var4.y();
            return pl -> {
                CommandExecution var2x = PlayerStateManager.oq();
                Entity var3x = var2.amM(var2x);
                if (var3x != null) {
                    Vector3d var5x = var2x.getExecuteEyePos();
                    return EntityUtils.q(var3x.getBoundingBox().getCenter().subtract(var5x.x, var5x.y, var5x.z));
                } else {
                    Vector2f var4x = var2x.so();
                    return new Vec2f(var4x.x, var4x.y);
                }
            };
        } else {
            return null;
        }
    }

    Vec2f a(PlayerEntity var1);
}
