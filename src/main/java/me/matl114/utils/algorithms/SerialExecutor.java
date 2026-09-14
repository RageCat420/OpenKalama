package me.matl114.utils.algorithms;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public class SerialExecutor implements Runnable, Executor {
   Consumer<Runnable> e;
   final Queue<Runnable> d = new ConcurrentLinkedQueue<>();
   final AtomicBoolean f = new AtomicBoolean(false);

   public SerialExecutor(Consumer<Runnable> asyncRunner) {
      this.e = asyncRunner;
   }

   @Override
   public void execute(@NotNull Runnable runnable) {
      this.d(runnable);
   }

   public void d(Runnable task) {
      this.d.add(task);
      this.e.accept(this);
   }

   @Override
   public void run() {
      if (this.f.compareAndSet(false, true)) {
         try {
            while (!this.d.isEmpty()) {
               Runnable var1 = this.d.poll();
               var1.run();
            }
         } finally {
            this.f.set(false);
         }
      }
   }
}
