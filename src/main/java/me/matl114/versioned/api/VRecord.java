package me.matl114.versioned.api;

import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.component.type.ProfileComponent;

public interface VRecord {
    static ProfileComponent staticProfile(UUID uuid, String name, PropertyMap properties) {
        GameProfile var3 = new GameProfile(uuid, name);
        var3.getProperties().putAll(properties);
        return new ProfileComponent(var3);
    }

    static ProfileComponent h(String name) {
        return new ProfileComponent(Optional.of(name), Optional.empty(), k());
    }

    static PropertyMap k() {
        return new PropertyMap();
    }

    static UUID getGameProfileId(ProfileComponent profileComponent) {
        return profileComponent.gameProfile().getId();
    }

    static PropertyMap createProperty(Multimap<String, Property> ppt) {
        PropertyMap var1 = new PropertyMap();
        var1.putAll(ppt);
        return var1;
    }

    static PropertyMap getProperties(GameProfile profile) {
        return profile.getProperties();
    }

    static ProfileComponent withProperty(ProfileComponent component, PropertyMap properties) {
        GameProfile var2 = new GameProfile(
                component.gameProfile().getId(), component.gameProfile().getName());
        var2.getProperties().putAll(properties);
        return new ProfileComponent(var2);
    }

    static String getGameProfileName(ProfileComponent profileComponent) {
        return profileComponent.gameProfile().getName();
    }

    static UUID getId(GameProfile profile) {
        return profile.getId();
    }

    static String getName(GameProfile profile) {
        return profile.getName();
    }

    static PropertyMap getGameProfileProperties(ProfileComponent profileComponent) {
        return profileComponent.gameProfile().getProperties();
    }
}
