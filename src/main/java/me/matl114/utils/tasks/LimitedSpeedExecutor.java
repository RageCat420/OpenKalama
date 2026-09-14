package me.matl114.utils.tasks;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import me.matl114.managers.config.IntRef;
import org.jetbrains.annotations.NotNull;

public class LimitedSpeedExecutor implements Executor {
    private AtomicInteger b;
    private Deque<Runnable> a;
    private IntRef c;

    private void b(Runnable runnable) {
        runnable.run();
    }

    public void reset() {
        this.b.set(0);

        while (!this.a.isEmpty() && this.b.getAndIncrement() < this.c.get()) {
            Runnable var1 = this.a.poll();
            this.b(var1);
        }
    }

    public void a(Runnable runnable) {
        this.a.add(runnable);
    }

    public LimitedSpeedExecutor(IntRef count) {
        this.c = count;
        this.b = new AtomicInteger(0);
        this.a = new ArrayDeque<>();
    }

    @Override
    public void execute(@NotNull Runnable runnable) {
        if (this.b.get() > this.c.get()) {
            this.a.add(runnable);
        } else {
            while (!this.a.isEmpty() && this.b.getAndIncrement() <= this.c.get()) {
                Runnable var2 = this.a.poll();
                this.b(var2);
            }

            this.b.incrementAndGet();
            this.b(runnable);
        }
    }
}
