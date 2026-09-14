package me.matl114.utils;

import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup.RegistryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper.Impl;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;

public class KalamaHelperHelperT implements WrapperLookup {
    protected static final KalamaHelperHelperT INSTANCE = new KalamaHelperHelperT();

    public <T> Impl<T> getWrapperOrThrow(RegistryKey<? extends Registry<? extends T>> registryRef) {
        return ItemStackUtils.registry().getWrapperOrThrow(registryRef);
    }

    public <V> RegistryOps<V> getOps(DynamicOps<V> delegate) {
        return ItemStackUtils.registry().getOps(delegate);
    }

    public <T> Optional<Impl<T>> getOptionalWrapper(RegistryKey<? extends Registry<? extends T>> registryRef) {
        return ItemStackUtils.registry().getOptionalWrapper(registryRef);
    }

    public RegistryLookup createRegistryLookup() {
        return ItemStackUtils.registry().createRegistryLookup();
    }

    public Stream<RegistryKey<? extends Registry<?>>> streamAllRegistryKeys() {
        return ItemStackUtils.registry().streamAllRegistryKeys();
    }
}
