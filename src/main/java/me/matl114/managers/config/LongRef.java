package me.matl114.managers.config;

import me.matl114.utils.config.BaseAttrKeyValue;
import me.matl114.utils.config.kv.AttrKeyValues;

public class LongRef extends Ref<Long> {
    public static final Class<Long> TYPE = Long.class;
    long value;

    public LongRef(Object ref) {
        this(((Number) ref).longValue());
    }

    public static LongRef fromString(String value) {
        try {
            long val = Long.parseLong(value);
            if (val > 2147483647L || val < -2147483648L) {
                return new LongRef(val);
            }
        } catch (NumberFormatException var4) {
        }

        return null;
    }

    public Long getValue() {
        return this.get();
    }

    public long get() {
        return this.value;
    }

    public void setValue(Long value) {
        this.set(value);
    }

    @Override
    public Object getAsPrimitive() {
        return this.value;
    }

    @Override
    public <W> boolean isSameTypeWith(Ref<W> ref) {
        return ref instanceof LongRef;
    }

    @Override
    public <W> boolean copyValueFrom(Ref<W> otherRef) {
        if (otherRef instanceof LongRef lg) {
            this.set(lg.get());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public BaseAttrKeyValue<Long> _createKeyValue0(String key) {
        return new BaseAttrKeyValue<>(key, this.value, AttrKeyValues.LONG_FACTORY);
    }

    public void set(long value) {
        if (this.validateUpdateValue(value)) {
            this.value = value;
            this.callUpdate();
        }
    }

    public LongRef(long value) {
        this.value = value;
    }
}
