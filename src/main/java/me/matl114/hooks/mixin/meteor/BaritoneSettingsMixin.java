package me.matl114.hooks.mixin.meteor;

import com.llamalad7.mixinextras.sugar.Local;
import meteordevelopment.meteorclient.pathing.BaritoneSettings;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.StringSetting.Builder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Environment(EnvType.CLIENT)
@Mixin({BaritoneSettings.class})
public class BaritoneSettingsMixin {
    @Shadow(remap = false)
    private static String getDescription(String settingName) {
        return null;
    }

    @ModifyArg(
            method = {"createWrappers"},
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lmeteordevelopment/meteorclient/settings/SettingGroup;add(Lmeteordevelopment/meteorclient/settings/Setting;)Lmeteordevelopment/meteorclient/settings/Setting;",
                            ordinal = 4),
            require = 0,
            remap = false)
    private Setting<?> hookCreateWrappers(Setting<?> setting, @Local baritone.api.Settings.Setting baritoneSide) {
        return (Setting<?>)
                (setting instanceof IntSetting intSetting && baritoneSide.value instanceof Long
                        ? ((Builder) ((Builder) ((Builder) ((Builder) ((Builder)
                                                                        new Builder().name(baritoneSide.getName()))
                                                                .description(getDescription(baritoneSide.getName())))
                                                        .defaultValue(String.valueOf((Long) baritoneSide.defaultValue)))
                                                .onChanged(string -> {
                                                    try {
                                                        long longValue2 = Long.parseLong(string);
                                                        baritoneSide.value = longValue2;
                                                    } catch (Throwable var5) {
                                                    }
                                                }))
                                        .onModuleActivated(stringSetting ->
                                                stringSetting.set(String.valueOf((Long) baritoneSide.value))))
                                .build()
                        : setting);
    }
}
