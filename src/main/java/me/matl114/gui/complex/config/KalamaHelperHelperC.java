package me.matl114.gui.complex.config;

import me.matl114.managers.config.Ref;
import me.matl114.utils.config.AttrKeyValue;

public record KalamaHelperHelperC<T>(Ref<T> ref, AttrKeyValue<T> keyValue) {
    public AttrKeyValue<T> keyValue() {
        return this.keyValue;
    }

    public Ref<T> ref() {
        return this.ref;
    }

    public void save() {
        this.ref.setValue(this.keyValue.getOriginValue());
    }
}
