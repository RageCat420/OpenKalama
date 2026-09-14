package me.matl114.utils.commands.params.impl;

import java.util.List;
import java.util.function.Function;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.commands.params.types.EntitySelector;
import net.minecraft.entity.Entity;

public record KalamaHelperHelperM(String raw, Function<CommandExecution, List<Entity>> resolver)
        implements EntitySelector {

    public String raw() {
        return this.raw;
    }

    public List<Entity> wo(CommandExecution execution) {
        return this.resolver.apply(execution).stream()
                .filter(entity -> entity != null && !entity.isRemoved())
                .toList();
    }

    public Function<CommandExecution, List<Entity>> resolver() {
        return this.resolver;
    }

    @Override
    public String asString() {
        return null;
    }
}
