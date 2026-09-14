package me.matl114.utils.config;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import me.matl114.utils.KalamaHelperHelperJ;

public interface WrapperFactory<T, W> {
    RuntimeException PARSE_FAILURE = new KalamaHelperHelperJ();
    WrapperFactory<Object, Object> IDENTITY = of(Function.identity(), Function.identity());

    WrapperFactory<List<Pair>, Map<?, ?>> LIST_MAP_WRAPPER_FACTORY = of(
            lst -> {
                LinkedHashMap<Object, Object> map = new LinkedHashMap<>(lst.size());
                lst.forEach(pair -> map.put(pair.getFirst(), pair.getSecond()));
                return map;
            },
            map -> map.entrySet().stream()
                    .map(s -> Pair.of(s.getKey(), s.getValue()))
                    .collect(Collectors.toCollection(ArrayList::new)));

    static <T> WrapperFactory<T, T> identity() {
        return (WrapperFactory<T, T>) IDENTITY;
    }

    W create(T var1);

    T get(W var1);

    static <T, W> WrapperFactory<T, W> of(Function<T, W> function, Function<W, T> function2) {
        return new WrapperFactory.BaseWrapperFactory<>(function, function2);
    }

    default WrapperFactory<W, T> inverse() {
        return of(this::get, this::create);
    }

    default <R> WrapperFactory<T, R> concat(WrapperFactory<W, R> other) {
        return (WrapperFactory<T, R>)
                WrapperFactory.<T, R>of(s -> other.create(this.create(s)), t -> this.get(other.get((R) t)));
    }

    default Codec<W> wrapCodecComapFlatMap(Codec<T> codec) {
        return codec.comapFlatMap(
                s -> {
                    try {
                        return DataResult.success(this.create((T) s));
                    } catch (Throwable var3) {
                        return DataResult.error(() -> "Error create");
                    }
                },
                this::get);
    }

    default Codec<W> wrapCodecXmap(Codec<T> codec) {
        return codec.xmap(this::create, this::get);
    }

    static <T, W> WrapperFactory<List<T>, List<W>> list(WrapperFactory<T, W> factory) {
        return new WrapperFactory.ListWrapperFactory<>(factory);
    }

    static <S, T, U, V> WrapperFactory<Map<U, V>, Map<S, T>> map(
            WrapperFactory<U, S> keyMapper, WrapperFactory<V, T> valueMapper) {
        return of(
                map1 -> {
                    Map<S, T> map2 = (Map<S, T>) (new LinkedHashMap<>());

                    for (Entry<U, V> re : map1.entrySet()) {
                        map2.put(keyMapper.create(re.getKey()), valueMapper.create(re.getValue()));
                    }

                    return map2;
                },
                map2 -> {
                    Map<U, V> map1 = new LinkedHashMap<>();

                    for (Entry<S, T> re : map2.entrySet()) {
                        map1.put(keyMapper.get(re.getKey()), valueMapper.get(re.getValue()));
                    }

                    return map1;
                });
    }

    static <R, S extends R, T> WrapperFactory<S, T> fromCodec(Codec<T> codec, DynamicOps<R> ops) {
        WrapperFactory raw = WrapperFactory.<S, T>of(
                s -> (T) ((Pair) codec.decode((DynamicOps) ops, (R) s).getOrThrow()).getFirst(),
                t -> (S) ((Codec) codec)
                        .encodeStart((DynamicOps) ops, (Object) t)
                        .getOrThrow());
        return (WrapperFactory<S, T>) raw;
    }

    static <K1, K2> WrapperFactory<List<Pair<K1, K2>>, Map<K1, K2>> getListMapWrapper() {
        return (WrapperFactory) LIST_MAP_WRAPPER_FACTORY;
    }

    public static class BaseWrapperFactory<T, W> implements WrapperFactory<T, W> {
        Function<T, W> function;
        Function<W, T> function2;

        @Override
        public W create(T va) {
            return this.function.apply(va);
        }

        @Override
        public T get(W va) {
            return this.function2.apply(va);
        }

        @Override
        public WrapperFactory<W, T> inverse() {
            return new WrapperFactory.BaseWrapperFactory<>(this.function2, this.function);
        }

        @Override
        public <R> WrapperFactory<T, R> concat(WrapperFactory<W, R> other) {
            return other instanceof WrapperFactory.BaseWrapperFactory<W, R> base
                    ? new WrapperFactory.BaseWrapperFactory<>(
                            this.function.andThen(base.function), base.function2.andThen(this.function2))
                    : (WrapperFactory<T, R>) new WrapperFactory.BaseWrapperFactory(
                            this.function.andThen(s -> (R) other.create(s)),
                            (Object t) -> this.function2.apply((W) ((WrapperFactory) other).get(t)));
        }

        public BaseWrapperFactory(Function<T, W> function, Function<W, T> function2) {
            this.function = function;
            this.function2 = function2;
        }
    }

    public static class ListWrapperFactory<T, W> implements WrapperFactory<List<T>, List<W>> {
        WrapperFactory<T, W> wrapped;

        public List<W> create(List<T> va) {
            return va.stream().map(this.wrapped::create).toList();
        }

        public List<T> get(List<W> va) {
            return va.stream().map(this.wrapped::get).toList();
        }

        public ListWrapperFactory(WrapperFactory<T, W> wrapped) {
            this.wrapped = wrapped;
        }
    }
}
