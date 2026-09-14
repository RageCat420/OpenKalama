package me.matl114.utils.config;

import com.mojang.datafixers.util.Pair;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface PairLikeFactory<A, B, P> {
   PairLikeFactory<?, ?, Pair<?, ?>> PAIR_FACTORY = new PairLikeFactory.BasePairLikeFactory<>(Pair::of, Pair::getFirst, Pair::getSecond);

   P create(A var1, B var2);

   A getFirst(P var1);

   B getSecond(P var1);

   default P withFirst(P val, A value2) {
      return this.create(value2, this.getSecond(val));
   }

   default P withSecond(P val, B value2) {
      return this.create(this.getFirst(val), value2);
   }

   default WrapperFactory<A, P> asFirstWrapper(Supplier<P> source) {
      return WrapperFactory.of(s -> this.withFirst(source.get(), s), this::getFirst);
   }

   default WrapperFactory<B, P> asSecondWrapper(Supplier<P> source) {
      return WrapperFactory.of(s -> this.withSecond(source.get(), s), this::getSecond);
   }

   static <A, B, P> PairLikeFactory<A, B, P> of(BiFunction<A, B, P> creator, Function<P, A> firstGetter, Function<P, B> secondGetter) {
      return new PairLikeFactory.BasePairLikeFactory<>(creator, firstGetter, secondGetter);
   }

   default PairLikeFactory<B, A, P> swap() {
      return of((b, a) -> this.create(a, b), this::getSecond, this::getFirst);
   }

   static <A, B> PairLikeFactory<A, B, Pair<A, B>> pair() {
      return (PairLikeFactory)PAIR_FACTORY;
   }

   default <D> PairLikeFactory<A, B, D> concat(WrapperFactory<P, D> mapper) {
      return new PairLikeFactory.BasePairLikeFactory<>(
         (a, b) -> mapper.create(this.create(a, b)), s -> this.getFirst(mapper.get(s)), s -> this.getSecond(mapper.get(s))
      );
   }

   public static class BasePairLikeFactory<A, B, P> implements PairLikeFactory<A, B, P> {
      private final BiFunction<A, B, P> creator;
      private final Function<P, A> firstGetter;
      private final Function<P, B> secondGetter;

      @Override
      public P create(A first, B second) {
         return this.creator.apply(first, second);
      }

      @Override
      public A getFirst(P pair) {
         return this.firstGetter.apply(pair);
      }

      @Override
      public B getSecond(P pair) {
         return this.secondGetter.apply(pair);
      }

      @Override
      public PairLikeFactory<B, A, P> swap() {
         return new PairLikeFactory.BasePairLikeFactory<>((b, a) -> this.creator.apply(a, b), this.secondGetter, this.firstGetter);
      }

      public BasePairLikeFactory(BiFunction<A, B, P> creator, Function<P, A> firstGetter, Function<P, B> secondGetter) {
         this.creator = creator;
         this.firstGetter = firstGetter;
         this.secondGetter = secondGetter;
      }
   }
}
