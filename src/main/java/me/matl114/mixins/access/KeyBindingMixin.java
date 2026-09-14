package me.matl114.mixins.access;

import me.matl114.accessors.hacks.KeyBindAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.client.util.InputUtil.Type;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin({KeyBinding.class})
public class KeyBindingMixin implements KeyBindAccess {
    @Shadow
    private Key field_1655;

    @Unique
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    @Override
    public void resetKeyState() {
        long handle = mc.getWindow().getHandle();
        int code = this.field_1655.getCode();
        if (this.field_1655.getCategory() == Type.MOUSE) {
            this.method_23481(GLFW.glfwGetMouseButton(handle, code) == 1);
        } else {
            this.method_23481(InputUtil.isKeyPressed(handle, code));
        }
    }

    @Shadow
    public void method_23481(boolean var1) {}
}
