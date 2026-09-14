package me.matl114.hacks.modules.chat;

public interface EncryptChat$Encryption {
   default String d(String pass) {
      return pass;
   }

   EncryptChat$Encryptor c(String var1) throws Throwable;
}
