package me.matl114.bukkit;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BukkitPlayerTextures {
    private KalamaHelperHelperM i;
    private BukkitPlayerProfile g;
    private boolean k;
    private URL j;
    static final String a = "textures";
    private JsonObject e;
    private long timestamp;
    private boolean d = false;
    private static final String b = "textures.minecraft.net";
    private URL h;
    private static final String c = "/TEXTURE/";

    @Nullable
    private static KalamaHelperHelperM c(@Nullable String skinModelName) {
        if (skinModelName == null) {
            return null;
        } else {
            try {
                return KalamaHelperHelperM.valueOf(skinModelName.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException var2) {
                return null;
            }
        }
    }

    public boolean isEmpty() {
        return this.h == null && this.j == null;
    }

    @Nullable
    private static URL b(@Nullable String urlString) {
        if (urlString == null) {
            return null;
        } else {
            try {
                return new URL(urlString);
            } catch (MalformedURLException var2) {
                return null;
            }
        }
    }

    public static String encodePropertyValue(
            @Nonnull JsonObject propertyValue, @Nonnull KalamaHelperHelperZ formatter) {
        String var2 = formatter.format(propertyValue);
        return Base64.getEncoder().encodeToString(var2.getBytes(StandardCharsets.UTF_8));
    }

    public BukkitPlayerTextures() {
        this.i = KalamaHelperHelperM.yr;
        this.k = false;
    }

    private static void validateTextureUrl(@Nullable URL url) {
        if (url != null) {
            Preconditions.checkArgument(
                    url.getHost().equals("textures.minecraft.net"),
                    "Expected host '%s' but got '%s'",
                    "textures.minecraft.net",
                    url.getHost());
            Preconditions.checkArgument(
                    url.getPath().startsWith("/TEXTURE/"),
                    "Expected path starting with '%s' but got '%s",
                    "/TEXTURE/",
                    url.getPath());
        }
    }
}
