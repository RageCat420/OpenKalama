package me.matl114.utils.config.kv;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import me.matl114.managers.config.NBTType;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.WrapperFactory;

public class TypeConvertAttrKeyValue<W, T> implements AttrKeyValue<T>, Cloneable {
    public AttrKeyValue<W> delegate;
    public final WrapperFactory<T, W> wrapperFactory;
    public final AttrKeyValue.CustomWidgetFactory<T> factoryOverride;
    public final WrapperFactory<String, T> stringifyOverride;
    protected boolean validate = true;
    private String WA = "";
    private W lastUpdate;

    public TypeConvertAttrKeyValue(AttrKeyValue<W> attrKeyValue, WrapperFactory<T, W> wrapperFactory, NBTType<T> type) {
        this(attrKeyValue, wrapperFactory, type.customWidgetFactory(), type.stringifyFactory());
    }

    public TypeConvertAttrKeyValue(
            AttrKeyValue<W> attrKeyValue,
            WrapperFactory<T, W> wrapperFactory,
            AttrKeyValue.CustomWidgetFactory<T> factoryOverride,
            WrapperFactory<String, T> stringifyFactory) {
        this.wrapperFactory = wrapperFactory;
        this.factoryOverride = factoryOverride;
        this.stringifyOverride = stringifyFactory;
        this.delegate = attrKeyValue;
        this.checkUpdate();
    }

    private void checkUpdate() {
        W re = this.delegate.getOriginValue();
        if (!Objects.equals(re, this.lastUpdate)) {
            this.lastUpdate = re;
            this.WA = this.stringifyOverride.get(this.wrapperFactory.get(re));
            this.validate = true;
        }
    }

    @Override
    public String getKeyName() {
        return this.delegate.getKeyName();
    }

    @Override
    public WrapperFactory<String, T> getStringifyFactory() {
        return this.stringifyOverride;
    }

    @Override
    public String getValue() {
        this.checkUpdate();
        return this.WA;
    }

    @Override
    public T getOriginValue() {
        this.checkUpdate();
        return this.wrapperFactory.get(this.lastUpdate);
    }

    @Override
    public boolean isValueValid(T val) {
        W value;
        try {
            value = this.wrapperFactory.create(val);
        } catch (Throwable var4) {
            return false;
        }

        return this.delegate.isValueValid(value);
    }

    @Override
    public boolean setOriginValue(T val) {
        String value0 = this.stringifyOverride.get(val);
        this.valueChange(null, value0);
        return this.isValidate();
    }

    @Override
    public boolean validateAndUpdate() {
        try {
            T val = this.stringifyOverride.create(this.WA);
            W wval = this.wrapperFactory.create(val);
            if (this.delegate.setOriginValue(wval)) {
                this.validate = true;
                this.lastUpdate = wval;
                return true;
            } else {
                this.validate = false;
                return false;
            }
        } catch (Throwable var3) {
            return this.validate = false;
        }
    }

    @Override
    public String updateValue(T val) {
        return this.stringifyOverride.get(val);
    }

    @Override
    public boolean isValidate() {
        return this.validate && this.delegate.isValidate();
    }

    @Override
    public void addListener(Consumer<T> li) {
        this.delegate.addListener(s -> li.accept(this.wrapperFactory.get(s)));
    }

    @Override
    public void addValidator(Predicate<T> validator) {
        this.delegate.addValidator(s -> validator.test(this.wrapperFactory.get(s)));
    }

    @Override
    public AttrKeyValue.CustomWidgetFactory<T> getCustomWidgetFactory() {
        return this.factoryOverride;
    }

    @Override
    public <S extends AttrKeyValue<T>> S copy() {
        TypeConvertAttrKeyValue<W, T> val = this.clone();
        val.delegate = val.delegate.copy();
        return (S) val;
    }

    public void valueChange(Object object, String string) {
        this.checkUpdate();
        if (!Objects.equals(this.WA, string)) {
            this.WA = string;
            this.validate = this.validateAndUpdate();
        }
    }

    public TypeConvertAttrKeyValue<W, T> clone() {
        try {
            return (TypeConvertAttrKeyValue<W, T>) super.clone();
        } catch (CloneNotSupportedException var2) {
            throw new AssertionError();
        }
    }
}
