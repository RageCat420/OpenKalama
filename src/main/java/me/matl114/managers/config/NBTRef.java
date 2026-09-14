package me.matl114.managers.config;

import com.google.common.base.Preconditions;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import me.matl114.utils.Debug;
import me.matl114.utils.config.BaseAttrKeyValue;
import me.matl114.versioned.api.VNbt;
import net.minecraft.nbt.NbtElement;

public class NBTRef<T extends NBTParsable<?>> extends LazilyRegisterTypeRef<T, NbtElement> {
    private NBTType<T> type;

    public NBTRef(T nbtR) {
        super(nbtR.type().typeName(), nbtR);
        this.type = (NBTType<T>) nbtR.type();
    }

    public NBTRef(String value) {
        super(value);
    }

    protected void tryRegisterType(T value) {
        value.registerNBTType();
    }

    protected NbtElement toLazy(T val) {
        return val.toNbt();
    }

    protected NbtElement fromStringToLazy(String string) {
        return VNbt.getInstance().d(string);
    }

    protected String fromLazyToString(NbtElement val) {
        return VNbt.getInstance().b(val);
    }

    @Override
    protected String prefix() {
        return "nbt";
    }

    @Override
    protected void tryResolve() {
        if (!this.resolved) {
            NBTType<?> re = NBTParsable.registeredParsableTypes.get(this.enumType);
            if (re == null) {
                this.resolved = false;
            } else {
                this.type = (NBTType<T>) re;

                try {
                    T val = this.type.parse(this.enumValue);
                    this.resolved = true;
                    this.set(val);
                } catch (Throwable var3) {
                    throw new RuntimeException("Raw NBT value could not be parsed into type "
                            + this.type.typeName
                            + ", which may be caused by a corrupted config file: "
                            + (this.configReference == null ? "Unknown" : this.configReference.getConfigName()));
                }
            }
        }
    }

    private void initializeAndCheckNbtType(NBTParsable obj) {
        NBTType type1 = obj.type();
        if (!Objects.equals(this.enumType, type1.typeName().toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("NBT type mismatch : " + this.enumType + " and " + type1.typeName());
        } else {
            if (!this.resolved) {
                obj.registerNBTType();
                this.tryResolve();
            }
        }
    }

    protected T validateAndCast(Object val) {
        NBTParsable parsable = (NBTParsable) val;
        if (!this.resolved) {
            this.initializeAndCheckNbtType(parsable);
        }

        T configEnum = (T) val;
        Preconditions.checkArgument(
                Objects.equals(this.enumType, configEnum.type().typeName().toLowerCase(Locale.ROOT)),
                "Nbt type mismatch !");
        return configEnum;
    }

    public static <T extends NBTParsable<T>> NBTRef<T> fromString(String value) {
        if (value.startsWith("nbt:")) {
            try {
                return new NBTRef<>(value);
            } catch (Throwable var2) {
                Debug.e("Parse config as nbt Selection failed: ", value, ", Error Message: ", var2.getMessage());
            }
        }

        return null;
    }

    @Override
    public final <W> boolean isSameTypeWith(Ref<W> ref) {
        if (super.isSameTypeWith(ref) && ref instanceof NBTRef nbtRef) {
            this.tryResolve();
            nbtRef.tryResolve();
            if (this.resolved && ref instanceof NBTRef<?> nbtref && nbtref.resolved) {
                NBTParsable parsable = nbtref.get();
                if (!this.get().isSameType(parsable)) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public BaseAttrKeyValue<T> _createKeyValue0(String key) {
        if (!this.resolved) {
            this.tryResolve();
        }

        if (this.resolved) {
            return this.type.createAttrKeyValue(key, this.get());
        } else {
            throw new IllegalStateException("Access to a nbt type before it is registered");
        }
    }

    @Override
    public <R> boolean tryConvert(Ref<R> ref) {
        if (!this.resolved) {
            this.tryResolve();
        }

        if (this.resolved) {
            Optional<T> re = (Optional<T>) (Object) this.get().tryTypeConvert(ref);
            re.ifPresent(this::set);
            return re.isPresent();
        } else {
            return false;
        }
    }
}
