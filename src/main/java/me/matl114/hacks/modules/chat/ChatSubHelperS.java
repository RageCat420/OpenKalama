package me.matl114.hacks.modules.chat;

import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Random;
import javax.crypto.spec.IvParameterSpec;
import me.matl114.utils.EncryptUtils;

public class ChatSubHelperS extends EncryptChat$AESEncryption implements ChatSubHelperM {
   @Override
   public Pair<AlgorithmParameterSpec, byte[]> splitIV(byte[] message) {
      ByteBuffer var2 = ByteBuffer.wrap(message);
      int var3 = var2.capacity();
      long var4 = var2.getLong();
      byte[] var6 = new byte[var3 - 8];
      var2.get(var6);
      byte[] var7 = new byte[16];
      new Random(var4).nextBytes(var7);
      return new Pair(new IvParameterSpec(var7), var6);
   }

   public ChatSubHelperS() {
      super("CFB8", "NoPadding", true);
   }

   @Override
   public Pair<AlgorithmParameterSpec, byte[]> generateIV() {
      long var1 = EncryptUtils.a.nextLong();
      byte[] var3 = new byte[16];
      new Random(var1).nextBytes(var3);
      return new Pair(new IvParameterSpec(var3), ByteBuffer.allocate(8).putLong(var1).array());
   }
}
