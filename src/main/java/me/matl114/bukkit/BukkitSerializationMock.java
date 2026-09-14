package me.matl114.bukkit;

import com.google.common.base.Preconditions;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import me.matl114.events.annotations.Dispatch;
import me.matl114.utils.Debug;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitSerializationMock {
    public static final String e = "unknown-serialization-type";
    public static BukkitItemFactory a = new BukkitItemFactory();
    private static Map<String, Class<? extends Dispatch>> d = new HashMap<>();
    public static final String b = "==";
    private final Class<? extends Dispatch> clazz;

    @Nullable
    public Dispatch deserialize(@NotNull Map<String, ?> args) {
        Preconditions.checkArgument(args != null, "Args must not be null");
        Dispatch result = null;
        Method method = null;
        if (result == null) {
            method = this.getMethod("deserialize", true);
            if (method != null) {
                result = this.e(method, args);
            }
        }

        if (result == null) {
            method = this.getMethod("valueOf", true);
            if (method != null) {
                result = this.e(method, args);
            }
        }

        if (result == null) {
            Constructor<? extends Dispatch> constructor = this.d();
            if (constructor != null) {
                result = this.f(constructor, args);
            }
        }

        return result;
    }

    public static void l(@NotNull Class<? extends Dispatch> clazz) {
        while (d.values().remove(clazz)) {}
    }

    @Nullable
    protected Method getMethod(@NotNull String name, boolean isStatic) {
        try {
            Method var3 = this.clazz.getDeclaredMethod(name, Map.class);
            if (!Dispatch.class.isAssignableFrom(var3.getReturnType())) {
                return null;
            } else {
                return Modifier.isStatic(var3.getModifiers()) != isStatic ? null : var3;
            }
        } catch (NoSuchMethodException var4) {
            return null;
        } catch (SecurityException var5) {
            return null;
        }
    }

    @Nullable
    protected Dispatch f(@NotNull Constructor<? extends Dispatch> ctor, @NotNull Map<String, ?> args) {
        try {
            return (Dispatch) ctor.newInstance(args);
        } catch (Throwable var5) {
            Debug.e(
                    "Could not call constructor '" + ctor.toString() + "' of " + this.clazz + " for deserialization",
                    var5 instanceof InvocationTargetException ? var5.getCause() : var5);
            return null;
        }
    }

    public static void initTest() {}

    public static BukkitItemFactory b() {
        return a;
    }

    public static void k(@NotNull String alias) {
        d.remove(alias);
    }

    @Nullable
    public static Dispatch g(@NotNull Map<String, ?> args, @NotNull Class<? extends Dispatch> clazz) {
        return new BukkitSerializationMock(clazz).deserialize(args);
    }

    protected BukkitSerializationMock(@NotNull Class<? extends Dispatch> clazz) {
        this.clazz = clazz;
    }

    @NotNull
    public static String n(@NotNull Class<? extends Dispatch> clazz) {
        return clazz.getName();
    }

    public static void registerClass(@NotNull Class<? extends Dispatch> clazz, @NotNull String alias) {
        d.put(alias, clazz);
    }

    @Nullable
    public static Class<? extends Dispatch> m(@NotNull String alias) {
        return d.get(alias);
    }

    @Nullable
    protected Constructor<? extends Dispatch> d() {
        try {
            return this.clazz.getConstructor(Map.class);
        } catch (NoSuchMethodException var2) {
            return null;
        } catch (SecurityException var3) {
            return null;
        }
    }

    @Nullable
    public static Object h(@NotNull Map<String, ?> args) {
        Object var1 = null;
        if (args.containsKey("==")) {
            try {
                String var2 = (String) args.get("==");
                if (var2 == null) {
                    throw new IllegalArgumentException("Cannot have null alias");
                }

                var1 = m(var2);
                if (var1 == null) {
                    return new KalamaHelperHelperA(args);
                }
            } catch (ClassCastException var4) {
                var4.fillInStackTrace();
                throw var4;
            }

            return new BukkitSerializationMock((Class<? extends Dispatch>) var1).deserialize(args);
        } else {
            throw new IllegalArgumentException("Args doesn't contain type key ('==')");
        }
    }

    static {
        Debug.a("loading bukkitMock!");
        registerClass(BukkitMetaItem.class, "ItemMeta");
        registerClass(BukkitMetaItem.class, "org.bukkit.craftbukkit.inventory.CraftMetaItem");
        registerClass(BukkitItemStack.class, "ItemStack");
        registerClass(BukkitItemStack.class, "org.bukkit.inventory.ItemStack");
        registerClass(BukkitOfflineplayer.class, "OfflinePlayer");
        registerClass(BukkitPlayerProfile.class, "PlayerProfile");
    }

    @Nullable
    protected Dispatch e(@NotNull Method method, @NotNull Map<String, ?> args) {
        try {
            Dispatch var3 = (Dispatch) method.invoke(null, args);
            if (var3 != null) {
                return var3;
            }

            Debug.a("Could not call method '" + method.toString() + "' of " + this.clazz
                    + " for deserialization: method returned null");
        } catch (Throwable var5) {
            Debug.e(
                    "Could not call method '" + method.toString() + "' of " + this.clazz + " for deserialization",
                    var5 instanceof InvocationTargetException ? var5.getCause() : var5);
        }

        return null;
    }

    public static void i(@NotNull Class<? extends Dispatch> clazz) {
        registerClass(clazz, n(clazz));
        registerClass(clazz, clazz.getName());
    }
}
