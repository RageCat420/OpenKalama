package me.matl114.utils;

import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class RegistryUtils {
    public static final HashMap<Class<?>, RegistryKey<?>> b;
    public static final Map<RegistryKey<?>, Class<?>> a;

    public static <T> Set<RegistryEntry<T>> parseEntryWhiteList(Registry<T> registry, String regex) {
        LinkedHashSet var2 = new LinkedHashSet();

        try {
            Predicate var3 = Pattern.compile(regex).asMatchPredicate();

            for (Identifier var5 : registry.getIds()) {
                if (var3.test(var5.getPath())) {
                    RegistryEntry var6 = (RegistryEntry) registry.getEntry(var5).orElse(null);
                    if (var6 != null) {
                        var2.add(var6);
                    }
                }
            }
        } catch (Throwable var7) {
        }

        return var2;
    }

    public static <W> RegistryEntry<W> getRegistryEntry(
            DynamicRegistryManager lookup, RegistryKey<? extends Registry<? extends W>> key, W value) {
        return lookup.getOptional(key).map(s -> s.getEntry(value)).orElse(null);
    }

    public static <T> Class<T> f(RegistryKey<Registry<T>> key) {
        return (Class<T>) a.get(key);
    }

    public static <T> Set<T> b(Registry<T> registry, String regex) {
        return parseWhiteList(registry, Pattern.compile(regex));
    }

    public static <T> RegistryKey<? extends Registry<T>> i(T value) {
        for (Class var1 = value.getClass(); var1 != Object.class; var1 = var1.getSuperclass()) {
            RegistryKey var2 = b.get(var1);
            if (var2 != null) {
                b.put(value.getClass(), var2);
                return var2;
            }
        }

        return null;
    }

    public static <W> RegistryEntry<W> g(WrapperLookup lookup, RegistryKey<W> key) {
        return lookup.getOptionalWrapper(key.getRegistryRef())
                .flatMap(registry -> registry.getOptional(key))
                .orElse(null);
    }

    static {
        HashMap var0 = new HashMap();

        try {
            for (Field var4 : RegistryKeys.class.getDeclaredFields()) {
                if (Modifier.isStatic(var4.getModifiers())
                        && Modifier.isFinal(var4.getModifiers())
                        && var4.getGenericType() instanceof ParameterizedType var6
                        && var6.getRawType() == RegistryKey.class) {
                    Type[] var7 = var6.getActualTypeArguments();
                    if (var7.length == 1
                            && var7[0] instanceof ParameterizedType var9
                            && var9.getRawType() == Registry.class) {
                        Type[] var10 = var9.getActualTypeArguments();
                        if (var10.length == 1) {
                            Type var11 = var10[0];
                            Class var12 = d(var11);
                            var0.put((RegistryKey) var4.get(null), var12);
                        }
                    }
                }
            }
        } catch (Throwable var13) {
            throw new RuntimeException(var13);
        }

        var0.put(RegistryKeys.ITEM, Item.class);
        var0.put(RegistryKeys.BLOCK, Block.class);
        var0.put(RegistryKeys.ATTRIBUTE, EntityAttribute.class);
        var0.put(RegistryKeys.ENCHANTMENT, Enchantment.class);
        var0.put(RegistryKeys.BLOCK_ENTITY_TYPE, BlockEntityType.class);
        var0.put(RegistryKeys.ENTITY_TYPE, EntityType.class);
        var0.put(RegistryKeys.STATUS_EFFECT, StatusEffect.class);
        a = ImmutableMap.copyOf(var0);
        b = new HashMap<>(a.entrySet().stream().collect(Collectors.toMap(Entry::getValue, Entry::getKey, (k, v) -> v)));
    }

    private static Class<?> d(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType var1) {
            return (Class<?>) var1.getRawType();
        } else if (type instanceof TypeVariable var2) {
            return d(var2.getBounds()[0]);
        } else if (type instanceof WildcardType var3) {
            return d(var3.getUpperBounds()[0]);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    public static <T> Class<T> getRegistryType(Registry<T> registry) {
        return f((RegistryKey) registry.getKey());
    }

    public static <T> Set<T> parseWhiteList(Registry<T> registry, Pattern regex) {
        LinkedHashSet var2 = new LinkedHashSet();

        try {
            Predicate var3 = regex.asMatchPredicate();

            for (Identifier var5 : registry.getIds()) {
                if (var3.test(var5.getPath())) {
                    var2.add(registry.get(var5));
                }
            }
        } catch (Throwable var6) {
        }

        return var2;
    }
}
