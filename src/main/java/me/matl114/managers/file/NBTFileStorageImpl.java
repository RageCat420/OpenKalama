package me.matl114.managers.file;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.io.File;
import java.io.IOException;
import me.matl114.utils.FileUtils;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;

public class NBTFileStorageImpl extends FileStorageImpl {
   NbtCompound nbtCompound;

   @Override
   public void i() {
      this.nbtCompound = new NbtCompound();
      this.file.delete();
      this.f = true;
   }

   public void write() {
      this.ensureParentDir();
      File var1 = new File(this.file.getParentFile(), this.file.getName() + ".tmp");
      if (var1.exists()) {
         var1.delete();
      }

      try {
         NbtIo.write(this.nbtCompound, var1.toPath());
      } catch (IOException var4) {
         throw new RuntimeException("Failed to save " + this.file, var4);
      }

      try {
         FileUtils.b(var1, this.file);
      } catch (IOException var3) {
         throw new RuntimeException("Failed to save " + this.file, var3);
      }

      this.g = false;
   }

   public NBTFileStorageImpl(File file) {
      super(file);
      this.h();
   }

   public <W> DataResult<W> read(Codec<W> codec) {
      return codec.parse(NbtOps.INSTANCE, this.nbtCompound);
   }

   @Override
   public <W> DataResult<?> f(Codec<W> codec, W value) {
      DataResult var3 = codec.encodeStart(NbtOps.INSTANCE, value);
      var3.result().ifPresent(result -> this.write(result, NbtOps.INSTANCE));
      return var3;
   }

   @Override
   public <T> void write(T value, DynamicOps<T> ops) {
      this.nbtCompound = (NbtCompound)ops.convertTo(NbtOps.INSTANCE, value);
      this.g = true;
   }

   public <T, W extends T> W asReadOnly(DynamicOps<T> ops) {
      return (W)(ops == NbtOps.INSTANCE ? this.nbtCompound : NbtOps.INSTANCE.convertTo(ops, this.nbtCompound));
   }

   @Override
   public void h() {
      if (!this.file.exists()) {
         this.nbtCompound = new NbtCompound();
         this.g = false;
      } else {
         try {
            this.nbtCompound = NbtIo.read(this.file.toPath());
         } catch (Throwable var2) {
            throw new RuntimeException(var2);
         }

         this.g = false;
      }
   }

   public <T, W extends T> W as(DynamicOps<T> ops) {
      return (W)NbtOps.INSTANCE.convertTo(ops, this.nbtCompound);
   }



   @Override
   public void g() { }

}
