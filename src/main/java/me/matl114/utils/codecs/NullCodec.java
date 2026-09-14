package me.matl114.utils.codecs;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.function.Predicate;

public class NullCodec<A> implements Codec<A> {
    Predicate<A> predicate;
    Codec<A> delegate;
    A empty;

    public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
        return ops.empty() != input && input != null
                ? this.delegate.decode(ops, input)
                : DataResult.success(Pair.of(this.empty, input));
    }

    public NullCodec(Codec<A> delegate, Predicate<A> predicate, A empty) {
        this.delegate = delegate;
        this.predicate = predicate;
        this.empty = empty;
    }

    public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
        return this.predicate.test(input) ? DataResult.success(ops.empty()) : this.delegate.encode(input, ops, prefix);
    }
}
