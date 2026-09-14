package me.matl114.managers.input;

import me.matl114.managers.InputState;
import net.minecraft.client.MinecraftClient;

public interface IInputManager {
    MinecraftClient getClient();

    boolean isKeyPressed(int var1);

    void b(IHotKey var1);

    IHotKey getHotkey(String var1);

    void a(IHotKey var1);

    InputState e(int var1);

    InputState d(int var1);
}
