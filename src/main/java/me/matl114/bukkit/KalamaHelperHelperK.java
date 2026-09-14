package me.matl114.bukkit;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class KalamaHelperHelperK {
   BukkitPlayerProfile profile;

   public KalamaHelperHelperK(BukkitPlayerProfile profile) {
      this.profile = profile;
   }

   public BukkitPlayerProfile getProfile() {
      return this.profile;
   }

   @Nullable
   private static String a(@Nonnull String encoded) {
      try {
         return new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
      } catch (IllegalArgumentException var2) {
         return null;
      }
   }

   public KalamaHelperHelperK(UUID uniqueId, String name, URL url) {
      this.profile = new BukkitPlayerProfile(uniqueId, name);
      this.profile.setSkinUrl(url, KalamaHelperHelperM.yr, null);
   }

   @Nullable
   public static JsonObject decodePropertyValue(@Nonnull String encodedPropertyValue) {
      String var1 = a(encodedPropertyValue);
      if (var1 == null) {
         return null;
      } else {
         try {
            JsonElement var2 = JsonParser.parseString(var1);
            return !var2.isJsonObject() ? null : var2.getAsJsonObject();
         } catch (JsonParseException var3) {
            return null;
         }
      }
   }
}
