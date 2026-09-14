package me.matl114.managers.config;

import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.BaseAttrKeyValue;

public class DoubleRef extends Ref<Double> {
    public static final Class<Double> TYPE = Double.class;
    double value;

    public static DoubleRef of(Object va) {
        return new DoubleRef(((Number) va).doubleValue());
    }

    public DoubleRef(Double doubleValue) {
        this(doubleValue.doubleValue());
    }

    public Double getValue() {
        return this.get();
    }

    public double get() {
        return this.value;
    }

    public void setValue(Double value) {
        this.set(value);
    }

    @Override
    public Object getAsPrimitive() {
        return this.value;
    }

    @Override
    public <W> boolean isSameTypeWith(Ref<W> ref) {
        return ref instanceof DoubleRef;
    }

    @Override
    public <W> boolean copyValueFrom(Ref<W> otherRef) {
        if (otherRef instanceof DoubleRef db) {
            this.set(db.get());
            return true;
        } else if (otherRef instanceof FloatRef floatRef) {
            this.set(floatRef.get());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public BaseAttrKeyValue<Double> _createKeyValue0(String key) {
        return AttrKeyValue.doubleVal(key, this.value);
    }

    public void set(double va) {
        if (this.validateUpdateValue(va)) {
            this.value = va;
            this.callUpdate();
        }
    }

    public DoubleRef(double value) {
        this.value = value;
    }
}
