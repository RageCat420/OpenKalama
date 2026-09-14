package me.matl114.managers.file;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.io.File;
import java.util.function.Supplier;

public interface FileStorage extends AutoCloseable {
   void i();

   File q();

   <W> DataResult<?> f(Codec<W> var1, W var2);

   void h();

   void l(boolean var1);

   default <R, T, W extends T> R readOrThrow(Codec<R> codec) {
      return (R)(Object)this.e(codec).getOrThrow();
   }

   @Override
   default void close() {
      this.n(true);
   }

   default <R, T, W extends T> R read(Codec<R> codec, Supplier<R> defaultVal) {
      DataResult var3 = this.e(codec);
      return (R)(var3.isSuccess() ? var3.getOrThrow() : defaultVal.get());
   }

   <T, W extends T> W b(DynamicOps<T> var1);

   void n(boolean var1);

   default FileStorage t() {
      return this instanceof AutoSaveFileStorage var2 ? var2 : new AutoSaveFileStorage(this);
   }

   <T, W extends T> W c(DynamicOps<T> var1);

   <W> DataResult<W> e(Codec<W> var1);

   void g();

   boolean m();

   <T> void write(T var1, DynamicOps<T> var2);

   boolean p();

}
