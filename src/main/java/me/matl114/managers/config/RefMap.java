package me.matl114.managers.config;

public interface RefMap {
    Ref<?> get(String... var1);

    IntRef getInt(String... var1);

    FlagRef getBoolean(String... var1);

    DoubleRef getDouble(String... var1);

    <T extends ConfigEnum> EnumRef<T> getEnum(String... var1);

    StringRef getString(String... var1);

    ListRef getList(String... var1);

    KeyBindRef getKeyBind(String... var1);
}
