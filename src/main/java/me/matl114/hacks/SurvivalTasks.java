package me.matl114.hacks;

import me.matl114.hacks.api.ModuleGroup;
import me.matl114.hacks.api.ModuleManager;
import me.matl114.hacks.modules.HackModules;
import me.matl114.hacks.modules.survival.AntiAXray;
import me.matl114.hacks.modules.survival.AutoBreed;
import me.matl114.hacks.modules.survival.AutoLibrarian;
import me.matl114.hacks.modules.survival.BaritoneFix;
import me.matl114.hacks.modules.survival.BlockFarm;
import me.matl114.hacks.modules.survival.ElytraFinder;
import me.matl114.hacks.modules.survival.PathManager;
import me.matl114.hacks.modules.survival.SchedularSettings;
import me.matl114.hacks.modules.survival.SearchControl;
import me.matl114.hacks.modules.survival.SeedOre;
import me.matl114.hacks.modules.survival.VillagerEsp;
import me.matl114.hacks.modules.survival.WorldManager;
import me.matl114.hacks.modules.survival.XaeroHelper;
import org.jetbrains.annotations.ApiStatus.Experimental;

public class SurvivalTasks {
    private static WorldManager d;
    private static AutoBreed i;
    private static SeedOre g;
    private static BaritoneFix m;
    private static PathManager k;
    private static VillagerEsp c;
    private static SearchControl j;
    public static final ModuleGroup a = new ModuleGroup("Survival");
    private static AutoLibrarian h;

    @Experimental
    private static ElytraFinder l;

    public static XaeroHelper n;
    private static BlockFarm e;
    private static AntiAXray f;
    private static SchedularSettings b;

    public static ElytraFinder n() {
        return l;
    }

    public static BlockFarm g() {
        return e;
    }

    public static PathManager m() {
        return k;
    }

    public static AntiAXray h() {
        return f;
    }

    public static WorldManager f() {
        return d;
    }

    public static BaritoneFix o() {
        return m;
    }

    public static VillagerEsp e() {
        return c;
    }

    public static void a() {}

    public static AutoBreed k() {
        return i;
    }

    public static XaeroHelper p() {
        return n;
    }

    private static void b(ModuleManager m) {
        b = new SchedularSettings().register(m);
        c = new VillagerEsp().register(m);
        d = new WorldManager().register(m);
        e = new BlockFarm().register(m);
        f = new AntiAXray().register(m);
        g = new SeedOre().register(m);
        h = new AutoLibrarian().register(m);
        i = new AutoBreed().register(m);
        j = new SearchControl().register(m);
        k = new PathManager().register(m);
        SurvivalTasks.m = new BaritoneFix().register(m);
        n = new XaeroHelper().register(m);
    }

    public static SchedularSettings d() {
        return b;
    }

    public static SearchControl l() {
        return j;
    }

    public static AutoLibrarian j() {
        return h;
    }

    public static ModuleGroup c() {
        return a;
    }

    public static SeedOre i() {
        return g;
    }

    static {
        a.registerFactories(SurvivalTasks::b);
        HackModules.registerModuleGroup(a);
    }
}
