package me.matl114.jsApi;

import me.matl114.events.annotations.Modifiable;

@Modifiable
public class EnumHelper {
    public static boolean isEnum(Object what) {
        Class var1 = what instanceof Class ? (Class) what : what.getClass();
        return Enum.class.isAssignableFrom(var1);
    }

    public static <T extends Enum<T>> T getEnum(Class<T> enumClass, String name) {
        return Enum.valueOf(enumClass, name);
    }

    public static <T extends Enum<T>> T b(Class<T> enumClass, int idx) {
        return (T) enumClass.getEnumConstants()[idx];
    }
}
