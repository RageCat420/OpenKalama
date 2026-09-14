package me.matl114.events;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.crash.CrashReport;

public class GlobalEventVars {
   public static Event<MinecraftClient> c = null;
   public static CrashReport a = null;
   public static AtomicInteger d = new AtomicInteger(0);
   public static boolean lastRenderNeedDisableGuiLight;

   public int getModCnt() {
      return d.incrementAndGet();
   }

   public static boolean fetchThisTimeGuiLightStatus() {
      if (lastRenderNeedDisableGuiLight) {
         lastRenderNeedDisableGuiLight = false;
         return true;
      } else {
         return false;
      }
   }
}
