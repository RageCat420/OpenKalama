package me.matl114.hacks;

import me.matl114.hacks.api.ModuleGroup;
import me.matl114.hacks.api.ModuleManager;
import me.matl114.hacks.modules.HackModules;
import me.matl114.hacks.modules.extra.AutoLogout;
import me.matl114.hacks.modules.extra.AutoReconnect;
import me.matl114.hacks.modules.extra.BadPackets;
import me.matl114.hacks.modules.extra.BeaconPlus;
import me.matl114.hacks.modules.extra.BoatVClip;
import me.matl114.hacks.modules.extra.ClientExtra;
import me.matl114.hacks.modules.extra.EnderEyeLog;
import me.matl114.hacks.modules.extra.EventNotify;
import me.matl114.hacks.modules.extra.GuiFix;
import me.matl114.hacks.modules.extra.PacketDebug;
import me.matl114.hacks.modules.extra.ServerScanner;
import me.matl114.hacks.modules.extra.SkinBlink;
import me.matl114.hacks.modules.extra.Warps;

public class ExtraTasks {
    public static BadPackets d;
    public static AutoReconnect i;
    public static GuiFix g;
    public static SkinBlink m;
    public static Warps k;
    public static PacketDebug c;
    private static AutoLogout j;
    public static final ModuleGroup a = new ModuleGroup("Extra");
    public static ServerScanner h;
    public static BoatVClip l;
    public static EventNotify n;
    public static BeaconPlus e;
    public static EnderEyeLog f;
    public static ClientExtra b;

    public static BoatVClip n() {
        return l;
    }

    public static BeaconPlus g() {
        return e;
    }

    public static Warps m() {
        return k;
    }

    public static EnderEyeLog h() {
        return f;
    }

    public static BadPackets f() {
        return d;
    }

    public static SkinBlink o() {
        return m;
    }

    public static PacketDebug e() {
        return c;
    }

    public static void a() {}

    public static AutoReconnect k() {
        return i;
    }

    public static EventNotify p() {
        return n;
    }

    private static void b(ModuleManager m) {
        b = new ClientExtra().register(m);
        c = new PacketDebug().register(m);
        d = new BadPackets().register(m);
        e = new BeaconPlus().register(m);
        f = new EnderEyeLog().register(m);
        g = new GuiFix().register(m);
        h = new ServerScanner().register(m);
        i = new AutoReconnect().register(m);
        j = new AutoLogout().register(m);
        k = new Warps().register(m);
        l = new BoatVClip().register(m);
        ExtraTasks.m = new SkinBlink().register(m);
        n = new EventNotify().register(m);
    }

    public static ClientExtra d() {
        return b;
    }

    public static AutoLogout l() {
        return j;
    }

    public static ServerScanner j() {
        return h;
    }

    public static ModuleGroup c() {
        return a;
    }

    public static GuiFix i() {
        return g;
    }

    static {
        a.registerFactories(ExtraTasks::b);
        HackModules.registerModuleGroup(a);
    }
}
