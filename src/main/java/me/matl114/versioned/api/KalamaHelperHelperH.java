package me.matl114.versioned.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import javax.annotation.Nonnull;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record KalamaHelperHelperH(@Nullable ComponentType<?> type, boolean removed) {
    public static final Codec<KalamaHelperHelperH> CODEC = Codec.STRING.flatXmap(
            id -> {
                boolean var1 = id.startsWith("!");
                if (var1) {
                    id = id.substring("!".length());
                }

                Identifier var2 = Identifier.tryParse(id);
                ComponentType var3 = (ComponentType) Registries.DATA_COMPONENT_TYPE.get(var2);
                if (var3 == null) {
                    return DataResult.success(new KalamaHelperHelperH(null, false));
                } else {
                    return var3.shouldSkipSerialization()
                            ? DataResult.error(() -> "'" + var2 + "' is not a persistent component")
                            : DataResult.success(new KalamaHelperHelperH(var3, var1));
                }
            },
            type -> {
                ComponentType var1 = type.AR();
                if (var1 == null) {
                    return DataResult.error(() -> "Null component type");
                } else {
                    Identifier var2 = Registries.DATA_COMPONENT_TYPE.getId(var1);
                    return var2 == null
                            ? DataResult.error(() -> "Unregistered component: " + var1)
                            : DataResult.success(type.removed() ? "!" + var2 : var2.toString());
                }
            });

    public KalamaHelperHelperH(@Nullable ComponentType<?> type, boolean removed) {
        this.type = type;
        this.removed = removed;
    }

    @Nonnull
    public Codec<?> getValueCodec() {
        if (this.type == null) {
            return Codec.EMPTY.codec();
        } else if (this.removed) {
            return Codec.EMPTY.codec();
        } else {
            Codec var1 = VItem.w().q().get(this.type);
            return var1 == null ? this.type.getCodecOrThrow() : var1;
        }
    }

    @Nullable
    public ComponentType<?> AR() {
        return this.type;
    }

    public boolean removed() {
        return this.removed;
    }
}
