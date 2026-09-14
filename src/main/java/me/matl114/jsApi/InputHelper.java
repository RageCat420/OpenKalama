package me.matl114.jsApi;

import me.matl114.events.annotations.Modifiable;
import me.matl114.managers.input.KeyCode;
import me.matl114.managers.input.SimpleInputManager;
import me.matl114.utils.ScreenUtils;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;

@Modifiable
public class InputHelper {
    public static Class<?> b = GLFW.class;
    static final MinecraftClient a = MinecraftClient.getInstance();

    public static boolean o() {
        return SimpleInputManager.h().isKeyPressed(342)
                || SimpleInputManager.h().isKeyPressed(346);
    }

    public String getKeyName(int keyCode) {
        return KeyCode.getNameForKey(keyCode);
    }

    public int i(String keyName) {
        return KeyCode.getKeyCodeFromName("KEY_" + keyName.toUpperCase());
    }

    public static boolean n() {
        return SimpleInputManager.h().isKeyPressed(341)
                || SimpleInputManager.h().isKeyPressed(345);
    }

    public static void charAction(int codePoint, int modifiers) {
        a.execute(() -> {
            Screen var2 = a.currentScreen;
            if (var2 != null && a.getOverlay() == null) {
                if (Character.charCount(codePoint) == 1) {
                    Screen.wrapScreenError(
                            () -> var2.charTyped((char) codePoint, modifiers),
                            "charTyped event handler",
                            var2.getClass().getCanonicalName());
                } else {
                    for (char var6 : Character.toChars(codePoint)) {
                        Screen.wrapScreenError(
                                () -> var2.charTyped(var6, modifiers),
                                "charTyped event handler",
                                var2.getClass().getCanonicalName());
                    }
                }
            }
        });
    }

    public static void f(int str) {
        charAction(str, ScreenUtils.getCurrentModifiers());
    }

    public static boolean hasKeyPressed(int keyCode) {
        return SimpleInputManager.h().isKeyPressed(keyCode);
    }

    public int j(String mouseButtonName) {
        return KeyCode.getKeyCodeFromName("MOUSE_BUTTON_" + mouseButtonName.toUpperCase());
    }

    public static void g(String chr, int modifiers) {
        charAction(chr.charAt(0), modifiers);
    }

    public int k(String name) {
        return KeyCode.getKeyCodeFromName(name);
    }

    public static void b(int key, int action) {
        c(key, action, ScreenUtils.getCurrentModifiers());
    }

    public static boolean m() {
        return SimpleInputManager.h().isKeyPressed(340)
                || SimpleInputManager.h().isKeyPressed(344);
    }

    public static boolean p() {
        return SimpleInputManager.h().isKeyPressed(257)
                || SimpleInputManager.h().isKeyPressed(355);
    }

    public static void keyAction(int key, int scancode, int action, int modifiers) {
        a.execute(() -> a.keyboard.onKey(a.getWindow().getHandle(), key, scancode, action, modifiers));
    }

    public static void e(String str) {
        e(str);
    }

    public static Keyboard getKeyboard() {
        return a.keyboard;
    }

    public static void c(int key, int action, int modifiers) {
        keyAction(key, GLFW.glfwGetKeyScancode(key), action, modifiers);
    }
}
