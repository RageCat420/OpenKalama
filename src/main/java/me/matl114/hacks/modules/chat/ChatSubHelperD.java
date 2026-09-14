package me.matl114.hacks.modules.chat;

public record ChatSubHelperD(int start, int end, String prefix, String cipher, String suffix, String decrypted) {
   public String cipher() {
      return this.cipher;
   }

   public String suffix() {
      return this.suffix;
   }

   public String decrypted() {
      return this.decrypted;
   }

   

   public int end() {
      return this.end;
   }

   public int start() {
      return this.start;
   }

   public String prefix() {
      return this.prefix;
   }
}
