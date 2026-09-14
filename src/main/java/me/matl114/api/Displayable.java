package me.matl114.api;

import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.ArgumentType;
import me.matl114.utils.commands.params.impl.AbstractArgumentResult;

public class Displayable<T> extends AbstractArgumentResult<T> {
    public String getDisplay() {
        return null;
    }

    public Displayable(ArgumentType<T> type, ArgumentReader reader) {
        super(null, type, reader, reader.b());
        this.f = false;
    }

    @Override
    public boolean k() {
        return this.getDisplay() != null;
    }

    @Override
    public String a() {
        return null;
    }
}
