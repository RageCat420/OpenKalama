package me.matl114.utils.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import me.matl114.gui.McWidgetHelpers;
import me.matl114.gui.basic.DrawableWidget;
import org.jetbrains.annotations.Nullable;

public class BaseAttrKeyValue<T> implements AttrKeyValue<T>, Cloneable {
    public static AttrKeyValue.CustomWidgetFactory<?> WIDGET_FACTORY = BaseAttrKeyValue::generateTextInputValueWidget;
    protected AttrKeyValue.CustomWidgetFactory<T> widgetFactory;
    protected final WrapperFactory<String, T> stringifyFactory;
    final String keyName;
    private String WA;

    @Nullable
    private T originValue;

    @Nullable
    private Supplier<T> updater;

    List<Predicate<T>> validators = new ArrayList<>();
    List<Consumer<T>> listeners = new ArrayList<>();
    protected boolean validate = true;

    public static <T> AttrKeyValue.CustomWidgetFactory<T> getWidgetFactory() {
        return (AttrKeyValue.CustomWidgetFactory<T>) WIDGET_FACTORY;
    }

    public BaseAttrKeyValue(String key, @Nonnull T value, WrapperFactory<String, T> stringifyFactory) {
        this(key, value, getWidgetFactory(), stringifyFactory);
    }

    public BaseAttrKeyValue(
            String key,
            @Nonnull T value,
            AttrKeyValue.CustomWidgetFactory<T> widgetFactory,
            WrapperFactory<String, T> stringifyFactory) {
        this.keyName = key;
        this.originValue = value;
        this.stringifyFactory = stringifyFactory;
        this.WA = this.updateValue(value);
        this.widgetFactory = widgetFactory;
        this.validate = true;
    }

    public <R extends BaseAttrKeyValue<T>> BaseAttrKeyValue(
            String key,
            @Nonnull T value,
            AttrKeyValue.CustomWidgetFactory<T> widgetFactory,
            Function<R, WrapperFactory<String, T>> lateInitialization) {
        this.keyName = key;
        this.originValue = value;
        this.stringifyFactory = lateInitialization.apply((R) (Object) this);
        this.WA = this.updateValue(value);
        this.widgetFactory = widgetFactory;
        this.validate = true;
    }

    public <R extends BaseAttrKeyValue<T>> BaseAttrKeyValue(
            String key,
            Optional<T> value,
            AttrKeyValue.CustomWidgetFactory<T> widgetFactory,
            WrapperFactory<String, T> lateInitialization) {
        this.keyName = key;
        this.originValue = value.orElse(null);
        this.stringifyFactory = lateInitialization;
        this.WA = this.originValue == null ? null : this.updateValue(this.originValue);
        this.widgetFactory = widgetFactory;
        this.validate = value.isPresent();
    }

    @Override
    public final WrapperFactory<String, T> getStringifyFactory() {
        return this.stringifyFactory;
    }

    @Override
    public final String getValue() {
        this.checkUpdate();
        return this.WA;
    }

    @Nullable
    @Override
    public final T getOriginValue() {
        this.checkUpdate();
        return this.originValue;
    }

    private void checkUpdate() {
        if (this.updater != null) {
            T updated = this.updater.get();
            if (!Objects.equals(updated, this.originValue)) {
                this.originValue = updated;
                this.WA = updated == null ? null : this.updateValue(updated);
                this.validate = true;
            }
        }
    }

    @Override
    public final boolean isValueValid(T value) {
        try {
            for (Predicate<T> validator : this.validators) {
                if (!validator.test(value)) {
                    return false;
                }
            }

            return true;
        } catch (Throwable var4) {
            return false;
        }
    }

    @Override
    public final boolean setOriginValue(T value) {
        this.valueChange(null, this.updateValue(value));
        return this.isValidate();
    }

    protected final boolean setOriginValue0(T value) {
        if (this.isValueValid(value)) {
            this.originValue = value;
            this.callListeners(this.originValue);
            return true;
        } else {
            return false;
        }
    }

    private final void callListeners(T val) {
        try {
            for (Consumer<T> validator : this.listeners) {
                validator.accept(val);
            }
        } catch (Throwable var4) {
        }
    }

    @Override
    public final boolean validateAndUpdate() {
        try {
            T re = this.getStringifyFactory().create(this.getValue());
            return this.validate = this.setOriginValue0(re);
        } catch (Throwable var2) {
            return this.validate = false;
        }
    }

    public final void valueChange(Object selectable, String string) {
        if (!Objects.equals(string, this.WA)) {
            this.WA = string;
            this.validate = this.validateAndUpdate();
        }
    }

    @Override
    public final String updateValue(T val) {
        return this.stringifyFactory.get(val);
    }

    @Override
    public final void valueChangeInternal(Object selectable, T val) {
        this.valueChange(selectable, this.updateValue(val));
    }

    @Override
    public final void addListener(Consumer<T> li) {
        this.listeners.add(li);
    }

    @Override
    public final void addValidator(Predicate<T> validator) {
        this.validators.add(validator);
    }

    public final void setUpdater(Supplier<T> updater) {
        this.updater = updater;
        this.checkUpdate();
    }

    @Override
    public final AttrKeyValue.CustomWidgetFactory<T> getCustomWidgetFactory() {
        return this.widgetFactory;
    }

    @Override
    public <W extends AttrKeyValue<T>> W copy() {
        BaseAttrKeyValue<T> attrKeyValue = (BaseAttrKeyValue<T>) (Object) this.clone();
        attrKeyValue.validators = new ArrayList<>(attrKeyValue.validators);
        attrKeyValue.listeners = new ArrayList<>(attrKeyValue.listeners);
        return (W) attrKeyValue;
    }

    @Override
    protected final Object clone() {
        try {
            return super.clone();
        } catch (Throwable var2) {
            throw new RuntimeException(var2);
        }
    }

    public static <T> DrawableWidget generateTextInputValueWidget(
            AttrKeyValue<T> attrKeyValue, int x, int y, int inputDx, int dy) {
        return McWidgetHelpers.createAttrValueEditBox(attrKeyValue, x, y, inputDx, dy);
    }

    @Override
    public String getKeyName() {
        return this.keyName;
    }

    public List<Predicate<T>> getValidators() {
        return this.validators;
    }

    public List<Consumer<T>> getListeners() {
        return this.listeners;
    }

    @Override
    public boolean isValidate() {
        return this.validate;
    }
}
