package me.matl114.managers.file;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.io.File;
import java.util.function.Supplier;

public class AutoSaveFileStorage implements FileStorage {
   FileStorage fileStorage;

   @Override
   public void n(boolean deprecated) {
      this.fileStorage.n(deprecated);
   }

   public AutoSaveFileStorage(FileStorage fileStorage) {
      this.fileStorage = fileStorage;
   }

   @Override
   public boolean p() {
      return this.fileStorage.p();
   }

   @Override
   public <T, W extends T> W b(DynamicOps<T> ops) {
      return this.fileStorage.b(ops);
   }

   @Override
   public void close() {
      if (this.fileStorage.p()) {
         this.fileStorage.g();
      }

      this.fileStorage.close();
   }

   @Override
   public boolean m() {
      return this.fileStorage.m();
   }

   @Override
   public File q() {
      return this.fileStorage.q();
   }

   @Override
   public void h() {
      this.fileStorage.h();
   }

   @Override
   public void l(boolean dirty) {
      this.fileStorage.l(dirty);
   }

   @Override
   public <W> DataResult<W> e(Codec<W> codec) {
      return this.fileStorage.e(codec);
   }

   @Override
   public <R, T, W extends T> R readOrThrow(Codec<R> codec) {
      return this.fileStorage.readOrThrow(codec);
   }

   @Override
   public void i() {
      this.fileStorage.i();
   }

   @Override
   public <T, W extends T> W c(DynamicOps<T> ops) {
      return this.fileStorage.c(ops);
   }

   @Override
   public void g() {
      this.fileStorage.g();
   }

   @Override
   public FileStorage t() {
      return this.fileStorage.t();
   }

   @Override
   public <T> void write(T value, DynamicOps<T> ops) {
      this.fileStorage.write(value, ops);
   }

   @Override
   public <R, T, W extends T> R read(Codec<R> codec, Supplier<R> defaultVal) {
      return this.fileStorage.read(codec, defaultVal);
   }

   @Override
   public <W> DataResult<?> f(Codec<W> codec, W value) {
      return this.fileStorage.f(codec, value);
   }
}
