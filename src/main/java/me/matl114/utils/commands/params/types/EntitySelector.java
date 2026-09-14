package me.matl114.utils.commands.params.types;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import me.matl114.utils.commands.params.api.CommandExecution;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public interface EntitySelector {
    String asString();

    default Entity amL(CommandExecution execution) {
        List var2 = this.wo(execution).stream()
                .filter(entity -> entity != null && !entity.isRemoved())
                .toList();
        return var2.isEmpty()
                ? null
                : (Entity) var2.get(ThreadLocalRandom.current().nextInt(var2.size()));
    }

    List<Entity> wo(CommandExecution var1);

    default Vec3d pos(CommandExecution execution) {
        Entity var2 = this.amL(execution);
        return var2 == null ? null : var2.getPos();
    }

    default Entity amM(CommandExecution execution) {
        return this.wo(execution).stream()
                .filter(entity -> entity != null && !entity.isRemoved())
                .findFirst()
                .orElse(null);
    }
}
