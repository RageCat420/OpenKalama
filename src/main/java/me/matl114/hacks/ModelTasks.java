package me.matl114.hacks;

import me.matl114.hacks.api.ModuleGroup;
import me.matl114.hacks.api.ModuleManager;
import me.matl114.hacks.modules.HackModules;
import me.matl114.hacks.modules.models.CustomTextures;
import me.matl114.hacks.modules.models.ModelExtra;
import me.matl114.hacks.modules.models.NewStyleModel;
import me.matl114.hacks.modules.models.SlimefunModels;

public class ModelTasks {
    private static SlimefunModels e;
    public static final ModuleGroup a = new ModuleGroup("Model");
    private static NewStyleModel d;
    private static ModelExtra b;
    private static CustomTextures c;

    public static SlimefunModels f() {
        return e;
    }

    public static NewStyleModel e() {
        return d;
    }

    public static void init() {}

    private static void initModule(ModuleManager m) {
        b = new ModelExtra().register(m);
        c = new CustomTextures().register(m);
        d = new NewStyleModel().register(m);
        e = new SlimefunModels().register(m);
    }

    public static CustomTextures d() {
        return c;
    }

    static {
        a.registerFactories(ModelTasks::initModule);
        HackModules.registerModuleGroup(a);
    }

    public static ModelExtra c() {
        return b;
    }
}
