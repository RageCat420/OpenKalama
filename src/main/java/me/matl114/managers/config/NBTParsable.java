package me.matl114.managers.config;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import me.matl114.utils.Debug;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;

public interface NBTParsable<T extends NBTParsable<T>> extends AutoRegisterType {
    Map<String, NBTType<?>> registeredParsableTypes = new HashMap<>();

    default void registerNBTType() {
        registerNBTType(this.type());
    }

    static void onLoad(Class<?> c) {
        if (NBTParsable.class.isAssignableFrom(c)) {
            try {
                Field fieldLookup = c.getField("TYPE");
                Preconditions.checkArgument(NBTType.class.isAssignableFrom(fieldLookup.getType()));
                NBTType<?> type = (NBTType<?>) fieldLookup.get(null);
                if (!registeredParsableTypes.containsKey(type.typeName)) {
                    registerNBTType(type);
                }
            } catch (Throwable var3) {
                Debug.a("Auto register fail for type " + c.getName() + ", because static NBTType TYPE field not found");
            }
        }
    }

    static void registerNBTType(NBTType<?> type) {
        if (!registeredParsableTypes.containsKey(type.typeName)) {
            registeredParsableTypes.put(type.typeName, type);
        }
    }

    default Codec<T> codec() {
        return this.type().typeCodec();
    }

    NBTType<T> type();

    default T cast() {
        return (T) (Object) this;
    }

    default NbtElement toNbt() {
        return (NbtElement)
                (Object) this.codec().encodeStart(NbtOps.INSTANCE, this.cast()).getOrThrow();
    }

    default String getTypeName() {
        return this.type().typeName;
    }

    default boolean isSameType(NBTParsable<?> type) {
        return type.getClass() == this.getClass();
    }

    default <W> Optional<T> tryTypeConvert(Ref<W> ref) {
        return Optional.empty();
    }
}
