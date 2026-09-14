package me.matl114.managers;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.annotations.Cancelable;
import me.matl114.events.annotations.Modifiable;
import me.matl114.managers.task.KalamaHelperHelperC;
import me.matl114.managers.task.KalamaHelperHelperH;
import me.matl114.managers.task.KalamaHelperHelperI;
import me.matl114.utils.Debug;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.crash.CrashException;

public class Tasks {
    private static final Set<Consumer<ClientPlayerEntity>> d = new LinkedHashSet<>();
    private static final Deque<Cancelable> e = new ConcurrentLinkedDeque<>();
    private static volatile int b;
    private static volatile int a;
    private static final MinecraftClient c = MinecraftClient.getInstance();
    private static final Deque<Cancelable> f = new ConcurrentLinkedDeque<>();

    @Modifiable
    public static void p(Runnable task, int delay) {
        f.addLast(new KalamaHelperHelperC(task, delay));
    }

    @Modifiable
    public static void q(BooleanSupplier task, int delay, int period) {
        f.addLast(new KalamaHelperHelperH(task, delay, period));
    }

    public static void i(Event<Void> v) {
        a++;
        if (a < 0) {
            a = 0;
        } else if (a % 20 == 0) {
            b++;
        }

        g();
    }

    @Modifiable
    public static void o(Cancelable task) {
        f.addLast(task);
    }

    static {
        Listener.T().k(Tasks::onPostTick);
        Listener.S().k(Tasks::i);
    }

    @Modifiable
    public static int d() {
        return b;
    }

    private Tasks() {}

    public static void a() {}

    @Modifiable
    public static void l(Runnable task, int delay) {
        e.addLast(new KalamaHelperHelperC(task, delay));
    }

    @Modifiable
    public static int b() {
        return a;
    }

    public static void onPostTick(Event<Void> v) {
        if (c.player != null) {
            h(c.player);
        }

        f();
    }

    public static void n(BooleanSupplier task, int delay, int period, int time) {
        e.addLast(new KalamaHelperHelperI(task, delay, period, time));
    }

    @Modifiable
    public static void k(Cancelable task) {
        e.addLast(task);
    }

    public static void r(BooleanSupplier task, int delay, int period, int time) {
        f.addLast(new KalamaHelperHelperI(task, delay, period, time));
    }

    public static boolean isPeriod(int period) {
        return a % period == 0;
    }

    public static void f() {
        Iterator var0 = e.iterator();

        while (var0.hasNext()) {
            try {
                Cancelable var1 = (Cancelable) var0.next();
                if (var1.optional()) {
                    var0.remove();
                }
            } catch (StackOverflowError | CrashException var2) {
                var0.remove();
                throw var2;
            } catch (Throwable var3) {
                Debug.a("unexpected error while executing TimedTask:");
                Debug.f(var3);
                var0.remove();
            }
        }
    }

    @Modifiable
    public static void m(BooleanSupplier task, int delay, int period) {
        e.addLast(new KalamaHelperHelperH(task, delay, period));
    }

    public static void e(Consumer<ClientPlayerEntity> r) {
        d.add(r);
    }

    public static void g() {
        Iterator var0 = f.iterator();

        while (var0.hasNext()) {
            try {
                Cancelable var1 = (Cancelable) var0.next();
                if (var1.optional()) {
                    var0.remove();
                }
            } catch (StackOverflowError | CrashException var2) {
                var0.remove();
                throw var2;
            } catch (Throwable var3) {
                Debug.a("unexpected error while executing TimedTask:");
                Debug.f(var3);
                var0.remove();
            }
        }
    }

    public static void h(ClientPlayerEntity player) {
        d.forEach(i -> i.accept(player));
    }
}
