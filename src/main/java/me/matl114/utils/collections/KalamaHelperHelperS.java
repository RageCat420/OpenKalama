package me.matl114.utils.collections;

import java.util.Optional;
import net.minecraft.component.ComponentType;

public class KalamaHelperHelperS<T> {
    public Optional<T> b;
    public ComponentType<T> a;

    public KalamaHelperHelperS(ComponentType<T> type, Optional<T> value) {
        this.a = type;
        this.b = value;
    }
}
