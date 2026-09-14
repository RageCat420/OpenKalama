package me.matl114.hacks;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import me.matl114.events.Event;
import me.matl114.events.RenderListener;
import me.matl114.hacks.api.ModuleGroup;
import me.matl114.hacks.api.ModuleManager;
import me.matl114.hacks.modules.HackModules;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.modules.render.ChestESP;
import me.matl114.hacks.modules.render.CustomOverlay;
import me.matl114.hacks.modules.render.EnchantmentDisplay;
import me.matl114.hacks.modules.render.EntityESP;
import me.matl114.hacks.modules.render.EntityLog;
import me.matl114.hacks.modules.render.EquipmentHud;
import me.matl114.hacks.modules.render.ExplosiveESP;
import me.matl114.hacks.modules.render.Freecam;
import me.matl114.hacks.modules.render.Hud;
import me.matl114.hacks.modules.render.InvHud;
import me.matl114.hacks.modules.render.ItemESP;
import me.matl114.hacks.modules.render.ItemList;
import me.matl114.hacks.modules.render.MineESP;
import me.matl114.hacks.modules.render.ModuleListHud;
import me.matl114.hacks.modules.render.NameList;
import me.matl114.hacks.modules.render.NameTag;
import me.matl114.hacks.modules.render.NoRender;
import me.matl114.hacks.modules.render.NoSound;
import me.matl114.hacks.modules.render.PlayerLog;
import me.matl114.hacks.modules.render.PlayerQueue;
import me.matl114.hacks.modules.render.PlayerStatistic;
import me.matl114.hacks.modules.render.ProjectileESP;
import me.matl114.hacks.modules.render.RenderExtra;
import me.matl114.hacks.modules.render.RenderOptimize;
import me.matl114.hacks.modules.render.SleepMode;
import me.matl114.hacks.modules.render.StorageDisplay;
import me.matl114.hacks.modules.render.WorldScanner;
import me.matl114.hacks.modules.render.Zoom;
import me.matl114.utils.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class RenderTasks {
    private static NameList x;
    private static NoRender r;
    public static boolean i = false;
    public static boolean d = false;
    private static PlayerStatistic O;
    public static Color b = null;
    private static StorageDisplay Q;
    private static ItemList z;
    public static final Vec3d m = new Vec3d(-0.2, -0.2, -0.2);
    private static WorldScanner B;
    private static Hud L;
    private static InvHud N;
    private static ProjectileESP F;
    private static EnchantmentDisplay R;
    private static EquipmentHud P;
    private static ModuleListHud M;
    private static ItemESP y;
    private static RenderOptimize K;
    private static ChestESP A;
    public static boolean g = false;
    public static boolean f = false;
    private static Zoom G;
    public static boolean h = false;
    private static ExplosiveESP v;
    private static RenderExtra q;
    public static boolean j = false;
    public static final ModuleGroup p = new ModuleGroup("Render");
    private static PlayerLog D;
    private static EntityLog t;
    private static MinecraftClient a = MinecraftClient.getInstance();
    private static PlayerQueue E;
    private static final Set<KalamaHelperHelperG> k = new LinkedHashSet<>();
    private static SleepMode H;
    private static MineESP C;
    public static final Vec3d l = new Vec3d(-0.5, -0.5, -0.5);
    public static int DEBUG_TICK = 16;
    private static NoSound s;
    public static final Vec3d o = new Vec3d(0.2, 0.2, 0.2);
    public static boolean e = false;
    private static CustomOverlay I;
    private static Freecam J;
    private static NameTag w;
    private static EntityESP u;
    public static final Vec3d n = new Vec3d(0.5, 0.5, 0.5);

    public static EntityESP r() {
        return u;
    }

    public static void init() {}

    public static PlayerStatistic L() {
        return O;
    }

    public static void g(Box box, Vec3d move, int tick, Color color) {
        i(new KalamaHelperHelperCX(tick, new KalamaHelperHelperS(box, move, color, color)));
    }

    public static RenderExtra n() {
        return q;
    }

    public static EquipmentHud M() {
        return P;
    }

    private static void j(Event<MatrixStack> stackE) {
        if (!k.isEmpty()) {
            synchronized (k) {
                MatrixStack var2 = (MatrixStack) stackE.b;
                float var3 = stackE.<Float>getArgs(0);
                RenderUtils.startDrawVirtual(var2);

                try {
                    Iterator var4 = k.iterator();

                    while (var4.hasNext()) {
                        KalamaHelperHelperG var5 = (KalamaHelperHelperG) var4.next();
                        if (var5.stillRender()) {
                            var5.renderVirtual(var2, var3);
                        } else {
                            var5.b();
                            var4.remove();
                        }
                    }
                } finally {
                    RenderUtils.stopDrawVirtual(var2);
                }
            }
        }
    }

    public static NameTag t() {
        return w;
    }

    public static NoRender o() {
        return r;
    }

    public static EnchantmentDisplay O() {
        return R;
    }

    public static NameList u() {
        return x;
    }

    public static Hud I() {
        return L;
    }

    public static void onDebugRenderTick(Event<MatrixStack> event) {
        if (d && a.player != null) {
            try {
                RenderUtils.startDrawVirtual((MatrixStack) event.e());
                BlockPos var1 = PlayerStateManager.INSTANCE.jL;
                RenderUtils.drawOutlinedBox(
                        (MatrixStack) event.e(),
                        var1.toCenterPos().add(l),
                        var1.toCenterPos().add(n),
                        Color.GREEN);
                Vec3d var2 = a.player.getPos();
                Vec3d var3 = var2.subtract(0.0, 0.500001F, 0.0);
                BlockPos var4 = BlockPos.ofFloored(var3);
                RenderUtils.drawOutlinedBox(
                        (MatrixStack) event.e(),
                        var4.toCenterPos().add(l),
                        var4.toCenterPos().add(n),
                        Color.MAGENTA);
            } finally {
                RenderUtils.stopDrawVirtual((MatrixStack) event.e());
            }
        }
    }

    public static void debugBlockHitResult(BlockHitResult packetHitResult) {
        if (j) {
            i(new KalamaHelperHelperCX(
                    DEBUG_TICK,
                    new KalamaHelperHelperDX(
                            Vec3d.of(packetHitResult.getBlockPos()),
                            Vec3d.of(packetHitResult.getBlockPos()).add(1.0, 1.0, 1.0),
                            Color.WHITE)));
            i(new KalamaHelperHelperCX(
                    DEBUG_TICK,
                    new KalamaHelperHelperDX(
                            packetHitResult.getPos().add(-0.1, -0.1, -0.1),
                            packetHitResult.getPos().add(0.1, 0.1, 0.1),
                            Color.RED)));
        }
    }

    public static ExplosiveESP s() {
        return v;
    }

    public static void debugBox(Box box) {
        if (g && e) {
            i(new KalamaHelperHelperCX(DEBUG_TICK, new KalamaHelperHelperDX(box.getMinPos(), box.getMaxPos(), b)));
        }
    }

    public static NoSound p() {
        return s;
    }

    public static CustomOverlay F() {
        return I;
    }

    public static ItemList w() {
        return z;
    }

    public static void h(Vec3d from, Vec3d deltaMove, int timeTick, Color color) {
        i(new KalamaHelperHelperCX(timeTick, new KalamaHelperHelperBX(from, deltaMove).g(color)));
    }

    public static InvHud K() {
        return N;
    }

    public static void i(KalamaHelperHelperG task) {
        synchronized (k) {
            task.h();
            k.add(task);
        }
    }

    public static ChestESP x() {
        return A;
    }

    public static PlayerLog A() {
        return D;
    }

    public static Zoom D() {
        return G;
    }

    public static ModuleListHud J() {
        return M;
    }

    public static MineESP z() {
        return C;
    }

    public static RenderOptimize H() {
        return K;
    }

    private static void l(ModuleManager m) {
        q = new RenderExtra().register(m);
        r = new NoRender().register(m);
        s = new NoSound().register(m);
        t = new EntityLog().register(m);
        u = new EntityESP().register(m);
        v = new ExplosiveESP().register(m);
        w = new NameTag().register(m);
        x = new NameList().register(m);
        A = new ChestESP().register(m);
        y = new ItemESP().register(m);
        z = new ItemList().register(m);
        B = new WorldScanner().register(m);
        C = new MineESP().register(m);
        D = new PlayerLog().register(m);
        E = new PlayerQueue().register(m);
        F = new ProjectileESP().register(m);
        G = new Zoom().register(m);
        H = new SleepMode().register(m);
        I = new CustomOverlay().register(m);
        J = new Freecam().register(m);
        K = new RenderOptimize().register(m);
        L = new Hud().register(m);
        M = new ModuleListHud().register(m);
        N = new InvHud().register(m);
        O = new PlayerStatistic().register(m);
        P = new EquipmentHud().register(m);
        Q = new StorageDisplay().register(m);
        R = new EnchantmentDisplay().register(m);
    }

    public static KalamaHelperHelperGX k() {
        return new KalamaHelperHelperGX();
    }

    public static PlayerQueue B() {
        return E;
    }

    public static ProjectileESP C() {
        return F;
    }

    public static void debugBoxMov(Box box, Vec3d move) {
        if (g && e) {
            i(new KalamaHelperHelperCX(DEBUG_TICK, new KalamaHelperHelperS(box, move, b, Color.RED)));
        }
    }

    public static void drawBox(Box box, int timeTick, Color color) {
        i(new KalamaHelperHelperCX(timeTick, new KalamaHelperHelperDX(box.getMinPos(), box.getMaxPos(), color)));
    }

    public static ItemESP v() {
        return y;
    }

    public static Freecam G() {
        return J;
    }

    public static ModuleGroup m() {
        return p;
    }

    static {
        RenderListener.q().k(RenderTasks::j);
        RenderListener.q().k(RenderTasks::onDebugRenderTick);
        p.registerFactories(RenderTasks::l);
        HackModules.registerModuleGroup(p);
    }

    public static StorageDisplay N() {
        return Q;
    }

    public static WorldScanner y() {
        return B;
    }

    public static EntityLog q() {
        return t;
    }

    public static SleepMode E() {
        return H;
    }
}
