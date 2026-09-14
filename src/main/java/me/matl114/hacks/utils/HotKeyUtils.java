package me.matl114.hacks.utils;

import java.util.function.BooleanSupplier;
import me.matl114.hacks.modules.task.Modules;
import me.matl114.managers.config.Config;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.input.SimpleHotKey$InputHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class HotKeyUtils {
   private static final MinecraftClient mc = MinecraftClient.getInstance();

   public static Runnable wrapFlagAsToggle(String path, FlagRef flagRef) {
      return () -> {
         boolean var2 = !flagRef.get();
         flagRef.set(var2);
         Modules.INSTANCE.sendToggleMessage(path, var2);
      };
   }

   public static SimpleHotKey$InputHandler c(BooleanSupplier task) {
      return (ih, in) -> isValidState() ? task.getAsBoolean() : false;
   }

   public static Runnable f(String[] path, FlagRef flagRef) {
      return wrapFlagAsToggle(String.join(".", path), flagRef);
   }

   public static boolean isValidState() {
      return mc.currentScreen == null ? true : !Modules.INSTANCE.shouldNotExecuteConditionHotkey();
   }

   public static Runnable h(Config config, String... path) {
      String var2 = String.join(".", path);
      FlagRef var3 = config.getBoolean(path);
      return wrapFlagAsToggle(var2, var3);
   }

   public static SimpleHotKey$InputHandler b(Runnable task) {
      return (ih, in) -> {
         if (isValidState()) {
            task.run();
            return true;
         } else {
            return false;
         }
      };
   }

   public static SimpleHotKey$InputHandler d(Runnable task) {
      return (ih, in) -> {
         task.run();
         return true;
      };
   }

   public static SimpleHotKey$InputHandler i(Config config, String... path) {
      String var2 = String.join(".", path);
      FlagRef var3 = config.getBoolean(path);
      if (var3 != null) {
         Runnable var4 = wrapFlagAsToggle(var2, var3);
         return (ih, m) -> {
            ClientPlayerEntity var3x = m.getClient().player;
            if (var3x != null && isValidState()) {
               var4.run();
               return Modules.INSTANCE.toggleKeysStopVanilla.get();
            } else {
               return false;
            }
         };
      } else {
         throw new IllegalArgumentException("No Flag for " + var2);
      }
   }

   public static SimpleHotKey$InputHandler e(BooleanSupplier task) {
      return (ih, in) -> task.getAsBoolean();
   }
}
