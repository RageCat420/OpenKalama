package me.matl114.hacks.modules.chat;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import me.matl114.utils.EncryptUtils;

public class EncryptChat$AESEncryption implements EncryptChat$Encryption {
   String b;
   String a;
   boolean initialVector;

   public String f() {
      return this.b;
   }

   public boolean isInitialVector() {
      return this.initialVector;
   }

   public String e() {
      return this.a;
   }

   public EncryptChat$AESEncryption(String mode, String padding, boolean initialVector) {
      this.a = mode;
      this.b = padding;
      this.initialVector = initialVector;
   }

   @Override
   public EncryptChat$Encryptor c(String key) throws Throwable {
      return new ChatSubHelperQ(new SecretKeySpec(EncryptUtils.n(key), "AES"), this);
   }

   public String generateKey(String key) {
      try {
         byte[] var2 = new byte[16];
         new Random(1738389128127L).nextBytes(var2);
         PBEKeySpec var3 = new PBEKeySpec(key.toCharArray(), var2, 65536, 128);
         SecretKeyFactory var4 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
         byte[] var5 = var4.generateSecret(var3).getEncoded();
         return EncryptUtils.d.encodeToString(new SecretKeySpec(var5, "AES").getEncoded());
      } catch (InvalidKeySpecException | NoSuchAlgorithmException var6) {
         throw new RuntimeException(var6);
      }
   }
}
