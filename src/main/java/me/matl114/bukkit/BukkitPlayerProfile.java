package me.matl114.bukkit;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import me.matl114.events.annotations.Dispatch;
import me.matl114.utils.Debug;
import me.matl114.utils.ItemStackUtils;
import me.matl114.versioned.api.VRecord;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;

public class BukkitPlayerProfile implements Dispatch {
    Multimap<String, Property> lH = LinkedHashMultimap.create();
    static final String lI = "textures";
    URL lG;
    private static final String lJ = "textures.minecraft.net";
    UUID lF;
    private static final String lK = "/TEXTURE/";
    String name;

    @Nonnull
    public static JsonObject getOrCreateObject(@Nonnull JsonObject parent, @Nonnull String key) {
        JsonObject var2 = getObjectOrNull(parent, key);
        if (var2 == null) {
            var2 = new JsonObject();
            parent.add(key, var2);
        }

        return var2;
    }

    public ProfileComponent te() {
        PropertyMap var1 = VRecord.createProperty(this.lH);
        return VRecord.staticProfile(this.lF, this.name == null ? "" : this.name, var1);
    }

    @Deprecated
    @ParametersAreNonnullByDefault
    @Nonnull
    public static KalamaHelperHelperK fromBase64(UUID uuid, String base64skinTexture) {
        String var2 = new String(Base64.getDecoder().decode(base64skinTexture));
        JsonObject var3 = new JsonParser().parse(var2).getAsJsonObject();
        String var4 = var3.getAsJsonObject("textures")
                .getAsJsonObject("SKIN")
                .get("url")
                .getAsString();

        URL var5;
        try {
            var5 = URI.create(var4).toURL();
        } catch (MalformedURLException var8) {
            throw new RuntimeException(var8);
        }

        return new KalamaHelperHelperK(uuid, base64skinTexture, var5);
    }

    public void setSkinUrl(URL skinUrl, KalamaHelperHelperM model, URL cape) {
        this.lG = skinUrl;
        if (skinUrl == null && cape == null) {
            this.lH.removeAll("textures");
        } else {
            Property var4 = encodeUrlToProperty(skinUrl, model, cape);
            this.lH.removeAll("textures");
            this.lH.put("textures", var4);
        }
    }

    @Nullable
    public static JsonObject getObjectOrNull(@Nonnull JsonObject parent, @Nonnull String key) {
        JsonElement var2 = parent.get(key);
        return var2 instanceof JsonObject ? (JsonObject) var2 : null;
    }

    @ParametersAreNonnullByDefault
    @Nonnull
    public static KalamaHelperHelperK tm(String url) {
        UUID var1 = UUID.nameUUIDFromBytes(url.getBytes(StandardCharsets.UTF_8));
        return tk(var1, url);
    }

    public Map<String, Object> serialize() {
        LinkedHashMap var1 = new LinkedHashMap();
        if (this.lF != null) {
            var1.put("uniqueId", this.lF.toString());
        }

        if (this.name != null) {
            var1.put("name", this.name);
        }

        this.rebuildDirtyProperties();
        if (!this.lH.isEmpty()) {
            ArrayList var2 = new ArrayList();
            this.lH.forEach((propertyName, property) -> var2.add(serializeProperty(property)));
            var1.put("properties", var2);
        }

        return var1;
    }

    public void addGameProfile(ItemStack stack) {
        ItemStackUtils.setOrRemoveChange(stack, DataComponentTypes.PROFILE, this.te());
    }

    public Multimap<String, Property> tr() {
        return this.lH;
    }

    public BukkitPlayerProfile(UUID uniqueId, String name) {
        this.lF = uniqueId;
        this.name = name;
    }

    public static Map<String, Object> serializeProperty(@Nonnull Property property) {
        LinkedHashMap var1 = new LinkedHashMap();

        try {
            var1.put("name", property.name());
        } catch (Throwable var3) {
        }

        var1.put("value", property.value());
        if (property.hasSignature()) {
            var1.put("signature", property.signature());
        }

        return var1;
    }

    public PropertyMap td() {
        return VRecord.createProperty(this.lH);
    }

    public static BukkitPlayerProfile deserialize(Map<String, Object> map) {
        String uuidString = (String) map.get("uniqueId");
        UUID uniqueId;
        if (uuidString == null) {
            uniqueId = null;
        } else {
            uniqueId = UUID.fromString(uuidString);
        }

        String name = (String) map.get("name");
        BukkitPlayerProfile profile = new BukkitPlayerProfile(uniqueId, name);

        try {
            if (map.containsKey("properties")) {
                for (Object propertyData : (List) map.get("properties")) {
                    Preconditions.checkArgument(
                            propertyData instanceof Map, "Propertu data (%s) is not a valid Map", propertyData);
                    Property property = tf((Map<?, ?>) propertyData);
                    profile.lH.put((String) ((Map) propertyData).get("name"), property);
                }
            }
        } catch (Throwable var8) {
            Debug.a("error in properties deserialization");
        }

        return profile;
    }

    public static URL tl(String hash) {
        String var1 = "http://textures.minecraft.net/texture/" + hash;

        try {
            return URI.create(var1).toURL();
        } catch (MalformedURLException var5) {
            throw new RuntimeException(var5);
        }
    }

    @Override
    public String toString() {
        return "{uid: " + this.lF + ",name: " + this.name + ",properties: "
                + (this.lH.isEmpty() ? "empty" : this.lH.toString()) + "}";
    }

    @ParametersAreNonnullByDefault
    @Nonnull
    public static KalamaHelperHelperK tn(UUID uuid, String hashCode) {
        return tk(uuid, "http://textures.minecraft.net/texture/" + hashCode);
    }

    @ParametersAreNonnullByDefault
    @Nonnull
    public static KalamaHelperHelperK tk(UUID uuid, String url) {
        String var2 = "{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}";
        String var3 = Base64.getEncoder().encodeToString(var2.getBytes(StandardCharsets.UTF_8));

        URL var4;
        try {
            var4 = URI.create(url).toURL();
        } catch (MalformedURLException var7) {
            throw new RuntimeException(var7);
        }

        return th(uuid, var3, var4);
    }

    @ParametersAreNonnullByDefault
    @Nonnull
    public static KalamaHelperHelperK tj(String base64skinTexture) {
        UUID var1 = UUID.nameUUIDFromBytes(base64skinTexture.getBytes(StandardCharsets.UTF_8));
        return fromBase64(var1, base64skinTexture);
    }

    public static Property encodeUrlToProperty(URL skinUrl, KalamaHelperHelperM model, URL cape) {
        JsonObject var3 = new JsonObject();
        if (skinUrl != null) {
            JsonObject var4 = getOrCreateObject(var3, "textures");
            JsonObject var5 = getOrCreateObject(var4, Type.SKIN.name());
            var5.addProperty("url", skinUrl.toExternalForm());
            if (model != KalamaHelperHelperM.yr) {
                JsonObject var6 = getOrCreateObject(var5, "metadata");
                var6.addProperty("model", model.name().toLowerCase(Locale.ROOT));
            }
        }

        if (cape != null) {
            JsonObject var7 = getOrCreateObject(var3, "textures");
            JsonObject var9 = getOrCreateObject(var7, Type.CAPE.name());
            var9.addProperty("url", cape.toExternalForm());
        }

        String var8 = BukkitPlayerTextures.encodePropertyValue(var3, KalamaHelperHelperZ.COMPACT);
        return new Property("textures", var8);
    }

    public void rebuildDirtyProperties() {}

    public static Property tf(@Nonnull Map<?, ?> map) {
        String var1 = (String) map.get("name");
        String var2 = (String) map.get("value");
        String var3 = (String) map.get("signature");
        return new Property(var1, var2, var3);
    }

    @ParametersAreNonnullByDefault
    @Nonnull
    public static KalamaHelperHelperK to(String hashCode) {
        UUID var1 = UUID.nameUUIDFromBytes(hashCode.getBytes(StandardCharsets.UTF_8));
        return tn(var1, hashCode);
    }

    @ParametersAreNonnullByDefault
    @Nonnull
    public static KalamaHelperHelperK th(UUID uuid, String base64skinTexture, URL url) {
        return new KalamaHelperHelperK(uuid, base64skinTexture, url);
    }
}
