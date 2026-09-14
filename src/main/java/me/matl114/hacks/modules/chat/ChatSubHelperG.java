package me.matl114.hacks.modules.chat;

import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.GCMParameterSpec;
import me.matl114.utils.EncryptUtils;

public class ChatSubHelperG extends EncryptChat$AESEncryption implements ChatSubHelperM {
   @Override
   public Pair<AlgorithmParameterSpec, byte[]> splitIV(byte[] message) {
      byte[] var2 = new byte[12];
      byte[] var3 = new byte[message.length - 12];
      ByteBuffer.wrap(message).get(var2).get(var3);
      return new Pair(new GCMParameterSpec(96, var2), var3);
   }

   public ChatSubHelperG() {
      super("GCM", "NoPadding", true);
   }

   @Override
   public Pair<AlgorithmParameterSpec, byte[]> generateIV() {
      byte[] var1 = new byte[12];
      EncryptUtils.a.nextBytes(var1);
      return new Pair(new GCMParameterSpec(96, var1), var1);
   }
}
