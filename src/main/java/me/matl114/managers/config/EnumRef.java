package me.matl114.managers.config;

import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.Objects;
import me.matl114.utils.Debug;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.BaseAttrKeyValue;

public class EnumRef<T extends ConfigEnum> extends LazilyRegisterTypeRef<T, String> {
    public static <T extends ConfigEnum> Class<T> parameter(Class<?> enumClass) {
        return (Class<T>) enumClass;
    }

    public EnumRef(ConfigEnum enumR) {
        super(enumR.getConfigEnumType(), (T) enumR);
    }

    public EnumRef(String value) {
        super(value);
    }

    protected void tryRegisterType(T value) {
        ConfigEnum.ensureRegistered(value.cast().getClass());
    }

    protected String toLazy(T val) {
        return val.cast().name();
    }

    protected String fromStringToLazy(String string) {
        return string;
    }

    protected String fromLazyToString(String val) {
        return val;
    }

    @Override
    protected String prefix() {
        return "enum";
    }

    @Override
    protected void tryResolve() {
        if (!this.resolved) {
            Map<String, ConfigEnum> re = ConfigEnum.registeredConfigs.get(this.enumType);
            if (re == null) {
                this.resolved = false;
            } else {
                ConfigEnum val = re.get(this.enumValue);
                Preconditions.checkNotNull(
                        val,
                        "Unregistered enum value %s in enum type %s with %s"
                                .formatted(this.enumValue, this.enumType, re.toString()));
                this.resolved = true;
                this.set((T) val);
            }
        }
    }

    public void setEnumType(Class<? extends Enum> clazz) {
        if (!Objects.equals(this.enumType, ConfigEnum.getConfigEnumType(clazz))) {
            throw new IllegalArgumentException("Enum type mismatch the class name: " + this.enumType + " and " + clazz);
        } else {
            if (!this.resolved) {
                ConfigEnum.ensureRegistered(clazz);
                this.tryResolve();
            }
        }
    }

    protected T validateAndCast(Object val) {
        if (!this.resolved) {
            this.setEnumType((Class<? extends Enum>) val.getClass());
        }

        T configEnum = (T) val;
        Preconditions.checkArgument(
                Objects.equals(this.enumType, configEnum.getConfigEnumType()), "Enum type mismatch !");
        return configEnum;
    }

    public static EnumRef<ConfigEnum> fromString(String value) {
        if (value.startsWith("enum:")) {
            try {
                return new EnumRef<>(value);
            } catch (Throwable var2) {
                Debug.e("Parse config as Enum Selection failed: ", value, ", Error Message: ", var2.getMessage());
            }
        }

        return null;
    }

    @Override
    public BaseAttrKeyValue<T> _createKeyValue0(String key) {
        if (!this.resolved) {
            this.tryResolve();
        }

        if (this.resolved) {
            return AttrKeyValue.enumMap(
                    key, this.getValue(), (Map<String, T>) this.getValue().getMap());
        } else {
            throw new IllegalStateException("Access to a config enum instance before it is registered");
        }
    }

    public void next() {
        T val = this.get();
        Enum enumValue = val.cast();
        Enum[] values = (Enum[]) enumValue.getClass().getEnumConstants();
        this.set((T) values[(enumValue.ordinal() + 1) % values.length]);
    }
}
