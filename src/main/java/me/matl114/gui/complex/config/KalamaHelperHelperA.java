package me.matl114.gui.complex.config;

import java.util.Objects;
import java.util.Optional;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.managers.config.Ref;
import me.matl114.utils.config.AttrKeyValue;

public class KalamaHelperHelperA<W> extends KalamaHelperHelperE<W> {
    Optional<W> O;

    public KalamaHelperHelperA(
            int x, int y, int dx, int dy, int dKey, int dblank, int dvalue, Ref<W> kv, AttrKeyValue<W> attr) {
        this(
                x,
                y,
                dx,
                dy,
                dKey,
                dblank,
                dvalue,
                kv.hasDefaultValue() ? Optional.of((W) kv.getDefaultValue()) : Optional.empty(),
                attr);
    }

    public KalamaHelperHelperA(
            int x, int y, int dx, int dy, int dKey, int dblank, int dvalue, Optional<W> kv, AttrKeyValue<W> attr) {
        super(x, y, dx, dy, dKey, dblank, dvalue - dy, attr);
        this.O = kv;
    }

    public KalamaHelperHelperA(int x, int y, int dx, int dy, int dKey, int dblank, int dvalue, Ref<W> kv, String key) {
        super(x, y, dx, dy, dKey, dblank, dvalue - dy, kv.createKeyValue(key));
        this.O = kv.hasDefaultValue() ? Optional.of((W) kv.getDefaultValue()) : Optional.empty();
    }

    @Override
    protected void af() {
        super.af();
        ExecutableWidget.instance(this.dr + this.ds + this.dt + 1, 1, this.dy - 2, this.dy - 2)
                .<ExecutableWidget>eV(new me.matl114.gui.elements.KalamaHelperHelperD(
                        () -> this.O.isPresent() && !Objects.equals(this.dq.getOriginValue(), this.O.get()),
                        () -> this.O.ifPresent(w -> this.dq.valueChangeInternal(null, (W) w))))
                .addToSub(this);
    }
}
