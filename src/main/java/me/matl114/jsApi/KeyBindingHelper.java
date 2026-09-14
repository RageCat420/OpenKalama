package me.matl114.jsApi;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import me.matl114.events.annotations.Modifiable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;

@Modifiable
public class KeyBindingHelper {
    private static final Map<String, KeyBinding> b = new HashMap<>();
    public static GameOptions a = MinecraftClient.getInstance().options;

    public static KeyBinding m() {
        return getKeyBinding("key.drop");
    }

    public static KeyBinding getKeyBinding(String key) {
        return b.get(key);
    }

    public static KeyBinding k() {
        return getKeyBinding("key.use");
    }

    public static KeyBinding c() {
        return getKeyBinding("key.back");
    }

    public static KeyBinding l() {
        return getKeyBinding("key.attack");
    }

    public static KeyBinding b() {
        return getKeyBinding("key.forward");
    }

    public static boolean isPressed(KeyBinding keyBinding) {
        return keyBinding.isPressed();
    }

    public static KeyBinding j() {
        return getKeyBinding("key.inventory");
    }

    public static KeyBinding f() {
        return getKeyBinding("key.jump");
    }

    public static KeyBinding h() {
        return getKeyBinding("key.sprint");
    }

    static {
        try {
            Class<GameOptions> var0 = GameOptions.class;

            for (Field var4 : var0.getDeclaredFields()) {
                if (var4.getType() == KeyBinding.class) {
                    var4.setAccessible(true);
                    KeyBinding var5 = (KeyBinding) var4.get(a);
                    b.put(var5.getTranslationKey(), var5);
                }
            }
        } catch (Throwable var13) {
            throw new RuntimeException(var13);
        }
    }

    public static boolean wasPressed(KeyBinding keyBinding) {
        return keyBinding.wasPressed();
    }

    public static KeyBinding d() {
        return getKeyBinding("key.left");
    }

    public static void setPress(KeyBinding keyBinding, boolean pressed) {
        keyBinding.setPressed(pressed);
    }

    public static void reset(KeyBinding keyBinding) {
        while (keyBinding.wasPressed()) {}

        keyBinding.setPressed(false);
    }

    public static KeyBinding e() {
        return getKeyBinding("key.right");
    }

    public static KeyBinding g() {
        return getKeyBinding("key.sneak");
    }

    public static KeyBinding i() {
        return getKeyBinding("key.swapOffhand");
    }
}
