package me.matl114.managers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import me.matl114.utils.Debug;

public class KalamaHelperHelperG {
   private static final ConcurrentHashMap<String, ScheduledFuture<?>> b = new ConcurrentHashMap<>();
   private static final ScheduledExecutorService a = Executors.newSingleThreadScheduledExecutor();
   private static final AtomicInteger c = new AtomicInteger(0);

   public static ScheduledExecutorService a() {
      return a;
   }

   public static String b(Runnable task, long initialDelay, long repeat) {
      String var5 = "repeat-" + c.incrementAndGet();
      ScheduledFuture var6 = a.scheduleAtFixedRate(() -> {
         try {
            task.run();
         } catch (Exception var3) {
            Debug.getLogger().warn("重复任务执行异常: {}", var5);
            var3.printStackTrace();
         }
      }, initialDelay, repeat, TimeUnit.MILLISECONDS);
      b.put(var5, var6);
      return var5;
   }

   public static boolean d(String taskId) {
      ScheduledFuture var1 = b.get(taskId);
      if (var1 != null) {
         boolean var2 = var1.cancel(true);
         if (var2) {
            b.remove(taskId);
         }

         return var2;
      } else {
         return false;
      }
   }

   public static String c(Runnable task, long delay) {
      String var3 = "delayed-" + c.incrementAndGet();
      ScheduledFuture var4 = a.schedule(() -> {
         try {
            task.run();
         } catch (Exception var6) {
            Debug.getLogger().warn("任务执行异常: {}", var3);
            var6.printStackTrace();
         } finally {
            b.remove(var3);
         }
      }, delay, TimeUnit.MILLISECONDS);
      b.put(var3, var4);
      return var3;
   }
}
