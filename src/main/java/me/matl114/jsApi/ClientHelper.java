package me.matl114.jsApi;

import java.util.concurrent.locks.LockSupport;
import me.matl114.events.annotations.Modifiable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.world.ClientWorld;

@Modifiable
public class ClientHelper {
   static MinecraftClient mc = MinecraftClient.getInstance();

   public static void runTask(Runnable runnable) {
      mc.execute(runnable);
   }

   public static void sleep(long ms, long ns) throws Throwable {
      if (ms > 0L) {
         Thread.sleep(ms);
      }

      if (ns > 0L) {
         LockSupport.parkNanos(ns);
      }
   }

   public static boolean isOnThread() {
      return mc.isOnThread();
   }

   public static ClientPlayerInteractionManager getInteractions() {
      return mc.interactionManager;
   }

   public static void i(long ms) throws Throwable {
      sleep(ms, 0L);
   }

   public static ClientPlayerEntity getPlayer() {
      return mc.player;
   }

   public static void h(long ns) throws Throwable {
      long var2 = ns / 1000L;
      sleep(var2, ns % 1000L);
   }

   public static ClientWorld getWorld() {
      return mc.world;
   }

   public static MinecraftClient a() {
      return mc;
   }

   public static GameOptions getGameOptions() {
      return mc.options;
   }
}
