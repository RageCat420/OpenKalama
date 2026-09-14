package me.matl114.managers;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.File;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import me.matl114.managers.config.Config;
import me.matl114.managers.config.ConfigLoader;
import net.fabricmc.loader.api.FabricLoader;

public class Configs {
    public static final Config n = ConfigLoader.loadExternalConfig("sfhelper-configs/interact.yml", "interact settings")
            .markForSave();
    public static final Predicate<String> b = x -> {
        try {
            JsonParser.parseString(x);
            return true;
        } catch (NullPointerException | JsonParseException var2x) {
            return false;
        }
    };
    public static final Config l = ConfigLoader.loadExternalConfig("sfhelper-configs/inv.yml", "inv settings")
            .markForSave();
    public static final Predicate<Integer> d = c(0);
    public static final Predicate<String> a = x -> {
        try {
            Pattern.compile(x);
            return true;
        } catch (NullPointerException | PatternSyntaxException var2x) {
            return false;
        }
    };
    public static final Config m = ConfigLoader.loadExternalConfig("sfhelper-configs/mov.yml", "mov settings")
            .markForSave();
    public static final Predicate<String> c = x -> {
        try {
            return true;
        } catch (Throwable var2x) {
            return false;
        }
    };
    public static final Config p = ConfigLoader.loadExternalConfig("sfhelper-configs/slimefun.yml", "slimefun settings")
            .markForSave();
    public static final Config k = ConfigLoader.loadExternalConfig("sfhelper-configs/combat.yml", "combat settings")
            .markForSave();
    public static final Config g = ConfigLoader.loadExternalConfig("sfhelper-configs/mine.yml", "mine settings")
            .markForSave();
    public static final Config i = ConfigLoader.loadExternalConfig("sfhelper-configs/render.yml", "render settings")
            .markForSave();
    public static final Config s;
    public static final Config q = ConfigLoader.loadExternalConfig("sfhelper-configs/models.yml", "model settings")
            .markForSave();
    public static final Predicate<Integer> e = c(1);
    public static final Config o = ConfigLoader.loadExternalConfig("sfhelper-configs/survival.yml", "survival settings")
            .markForSave();
    public static final Config r;
    private static boolean init = false;
    public static final Config h = ConfigLoader.loadExternalConfig("sfhelper-configs/chat.yml", "chat settings")
            .markForSave();
    public static final Config j = ConfigLoader.loadExternalConfig("sfhelper-configs/test.yml", "test settings")
            .markForSave();

    public static Predicate<Integer> c(int min) {
        return x -> x >= min;
    }

    public static Predicate<Integer> intRange(int min, int max) {
        return x -> x >= min && x <= max;
    }

    static {
        File var0 = FabricLoader.getInstance()
                .getConfigDir()
                .resolve("sfhelper-configs/hotkeys.yml")
                .toFile();
        File var1 = FabricLoader.getInstance()
                .getConfigDir()
                .resolve("sfhelper-configs/misc.yml")
                .toFile();
        if (var0.exists() && !var1.exists()) {
            var0.renameTo(var1);
        }

        File var2 = FabricLoader.getInstance()
                .getConfigDir()
                .resolve("sfhelper-configs/internal.yml")
                .toFile();
        if (var2.exists()) {
            var2.delete();
        }

        var2 = FabricLoader.getInstance()
                .getConfigDir()
                .resolve("sfhelper-configs/http.yml")
                .toFile();
        if (var2.exists()) {
            var2.delete();
        }

        r = ConfigLoader.loadExternalConfig("sfhelper-configs/misc.yml", "misc settings")
                .markForSave();
        s = ConfigLoader.loadExternalConfig("sfhelper-configs/toggles.yml", "toggle settings")
                .markForSave();
    }

    public static Predicate<Integer> d(int mAX) {
        return x -> x <= mAX;
    }

    public static Predicate<Double> doubleRange(double min, double max) {
        return x -> x >= min && x <= max;
    }

    public static void loadConfigs() {
        g.registerGlobal();
        h.registerGlobal();
        i.registerGlobal();
        j.registerGlobal();
        k.registerGlobal();
        l.registerGlobal();
        m.registerGlobal();
        n.registerGlobal();
        o.registerGlobal();
        p.registerGlobal();
        q.registerGlobal();
        r.registerGlobal();
        s.registerGlobal();
        if (init) {
            Config.reloadAll();
        } else {
            init = true;
        }
    }
}
