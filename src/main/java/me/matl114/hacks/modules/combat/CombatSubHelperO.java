package me.matl114.hacks.modules.combat;

import me.matl114.hacks.CombatTasks;
import me.matl114.hacks.utils.config.OptionalPrimitive;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class CombatSubHelperO extends CombatSubHelperSX {
    public synchronized void onUpdate() {
        super.h();
        if (this.t.uI != null) {
            this.u = this.t
                    .uI
                    .getPos()
                    .subtract(MinecraftClient.getInstance().player.getPos());
            if (this.t.uS) {
                OptionalPrimitive var1 = this.t.followOnGroundHeightExtra.get();
                if (var1.isPresent()) {
                    this.u = this.u.add(0.0, (Double) var1.getValue(), 0.0);
                }
            }
        } else {
            this.u = Vec3d.ZERO;
        }
    }

    @Override
    public void i() {}

    public boolean canBeAttack(Entity entity) {
        return entity instanceof PlayerEntity var2
                        && var2 != MinecraftClient.getInstance().player
                        && this.t.followerFollowFriend.get()
                        && !TargetSelector.INSTANCE.isNotFriend(var2)
                ? true
                : TargetSelector.INSTANCE.canAttack(entity)
                        && (!this.t.playerOnly.get() || entity instanceof PlayerEntity);
    }

    @Override
    public void j() {}

    @Override
    public Entity searchTarget() {
        return CombatTasks.l().searchAttack(this.t.range.get(), true, 0, this::canBeAttack);
    }
}
