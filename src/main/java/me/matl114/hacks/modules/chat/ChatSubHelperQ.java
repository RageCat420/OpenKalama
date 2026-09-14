package me.matl114.hacks.modules.chat;

import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.AEADBadTagException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import me.matl114.utils.EncryptUtils;

public class ChatSubHelperQ implements EncryptChat$Encryptor {
   Cipher d;
   SecretKey b;
   Cipher e;
   EncryptChat$AESEncryption c;

   public String encrypt(String message) {
      try {
         if (this.c.initialVector) {
            Pair var2 = this.generateIV();
            this.d.init(1, this.b, (AlgorithmParameterSpec)var2.getFirst());
            byte[] var3 = this.d.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return EncryptUtils.i(ByteBuffer.allocate(var3.length + ((byte[])var2.getSecond()).length).put((byte[])var2.getSecond()).put(var3).array());
         } else {
            return EncryptUtils.i(this.d.doFinal(EncryptUtils.c(message)));
         }
      } catch (BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException var4) {
         throw new RuntimeException(var4);
      }
   }

   public String decrypt(String message) {
      try {
         if (this.c.initialVector) {
            Pair var2 = this.splitIV(EncryptUtils.l(message));
            this.e.init(2, this.b, (AlgorithmParameterSpec)var2.getFirst());
            return EncryptUtils.d(this.e.doFinal((byte[])var2.getSecond()));
         } else {
            return EncryptUtils.d(this.e.doFinal(EncryptUtils.l(message)));
         }
      } catch (AEADBadTagException var3) {
         return "???";
      } catch (BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException var4) {
         throw new RuntimeException(var4);
      }
   }

   public Pair<AlgorithmParameterSpec, byte[]> generateIV() {
      return ((ChatSubHelperM)(Object)this.c).generateIV();
   }

   public ChatSubHelperQ(SecretKey key, EncryptChat$AESEncryption algorithm) {
      this.b = key;
      this.c = algorithm;

      try {
         Cipher var3 = Cipher.getInstance(this.b.getAlgorithm() + "/" + this.c.e() + "/" + this.c.f());
         if (this.c.initialVector) {
            var3.init(1, this.b, (AlgorithmParameterSpec)(Object)this.generateIV().getFirst());
         } else {
            var3.init(1, this.b);
         }

         this.d = var3;
         Cipher var4 = Cipher.getInstance(this.b.getAlgorithm() + "/" + this.c.e() + "/" + this.c.f());
         if (this.c.initialVector) {
            var4.init(2, this.b, (AlgorithmParameterSpec)(Object)this.generateIV().getFirst());
         } else {
            var4.init(2, this.b);
         }

         this.e = var4;
      } catch (InvalidAlgorithmParameterException var5) {
         throw new RuntimeException(var5);
      } catch (Throwable var6) {
         throw new RuntimeException(var6);
      }
   }

   public Pair<AlgorithmParameterSpec, byte[]> splitIV(byte[] message) {
      return ((ChatSubHelperM)(Object)this.c).splitIV(message);
   }



   @Override
   public void a(Object arg0) { }

}
