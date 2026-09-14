package me.matl114.utils;

import com.mojang.datafixers.util.Either;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.nbt.NbtCompound;

public class KalamaHelperHelperAk {
   NbtCompound b;
   Either<UUID, String> a;
   KalamaHelperHelperAd c;

   public KalamaHelperHelperAd d() {
      return this.c;
   }

   public KalamaHelperHelperAk(Either<UUID, String> source, NbtCompound config, KalamaHelperHelperAd data) {
      this.a = source;
      this.b = config;
      this.c = data;
   }

   public Either<UUID, String> b() {
      return this.a;
   }

   public String getDisplayName() {
      return (String)(Object)this.b().map(UUID::toString, Function.identity());
   }

   public NbtCompound c() {
      return this.b;
   }
}
