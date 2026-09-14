package me.matl114.managers.task;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface TaskManager {
    @Nonnull
    Map<String, Runnable> b();

    void register(String var1, Runnable var2);

    @Nullable
    Runnable a(String var1);

    @Nonnull
    Runnable getOrRegister(String var1, Runnable var2);

    static TaskManager f() {
        LinkedHashMap var0 = new LinkedHashMap();
        return new KalamaHelperHelperE(var0);
    }

    static TaskManager g(HashMap<String, Runnable> map) {
        return new KalamaHelperHelperE(map);
    }
}
