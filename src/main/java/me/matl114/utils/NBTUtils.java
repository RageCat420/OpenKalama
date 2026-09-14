package me.matl114.utils;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import org.slf4j.Logger;

public class NBTUtils {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static NbtElement resolve(@Nonnull NbtCompound tag, String... value) {
        NbtCompound var2 = tag;

        for (int var3 = 0; var3 < value.length - 1; var3++) {
            if (!(var2.get(value[var3]) instanceof NbtCompound var5)) {
                return null;
            }

            var2 = var5;
        }

        return var2.get(value[value.length - 1]);
    }

    public static <W> W toValue(NbtElement nbtElement, Codec<W> codec, WrapperLookup lookup) {
        DataResult var3 = codec.parse(lookup.getOps(NbtOps.INSTANCE), nbtElement);
        return (W) (var3.isSuccess() ? var3.getOrThrow() : null);
    }

    public static NbtElement getOrDefault(@Nonnull NbtCompound element, String key, Supplier<NbtElement> defaultValue) {
        return element.entries.computeIfAbsent(key, k -> (NbtElement) defaultValue.get());
    }

    @Nullable
    public static NbtElement f(@Nonnull NbtCompound tag, String path) {
        String[] var2 = path.split("\\.");
        return resolve(tag, var2);
    }

    public static NbtElement a(@Nonnull NbtCompound element, String key, NbtElement defaultValue) {
        return element.entries.getOrDefault(key, defaultValue);
    }

    public static <W> W l(NbtElement nbtElement, Codec<W> codec) {
        DataResult var2 = codec.parse(NbtOps.INSTANCE, nbtElement);
        return (W) (var2.isSuccess() ? var2.getOrThrow() : null);
    }

    public static <W> void putValue(
            @Nonnull NbtCompound tag, String key, W value, Codec<W> codec, WrapperLookup lookup) {
        tag.put(key, (NbtElement)
                codec.encodeStart(lookup.getOps(NbtOps.INSTANCE), value).getOrThrow());
    }

    public static void putIfAbsent(@Nonnull NbtCompound element, String key, NbtElement value) {
        element.entries.putIfAbsent(key, value);
    }

    public static <W> W getValue(@Nonnull NbtCompound tag, String key, Codec<W> codec, WrapperLookup lookup) {
        NbtElement var4 = tag.get(key);
        return (W)
                (var4 == null
                        ? null
                        : codec.parse(lookup.getOps(NbtOps.INSTANCE), var4)
                                .resultOrPartial()
                                .orElse(null));
    }

    public static NbtElement d(@Nonnull NbtCompound element, String key, Function<String, NbtElement> defaultValue) {
        return (NbtElement) element.entries.computeIfAbsent(key, defaultValue);
    }

    public static <W> void i(@Nonnull NbtCompound tag, String key, W value, Codec<W> codec) {
        tag.put(key, (NbtElement) codec.encodeStart(NbtOps.INSTANCE, value).getOrThrow());
    }

    public static NbtCompound ensurePath(@Nonnull NbtCompound tag, String path) {
        String[] var2 = path.split("\\.");
        NbtCompound var3 = tag;

        for (int var4 = 0; var4 < var2.length; var4++) {
            NbtElement var5 = var3.entries.compute(
                    var2[var4], (k, v) -> v instanceof NbtCompound var2x ? var2x : new NbtCompound());
            var3 = (NbtCompound) var5;
        }

        return var3;
    }

    public static <W> W j(@Nonnull NbtCompound tag, String key, Codec<W> codec) {
        NbtElement var3 = tag.get(key);
        return (W)
                (var3 == null
                        ? null
                        : codec.parse(NbtOps.INSTANCE, var3).resultOrPartial().orElse(null));
    }
}
