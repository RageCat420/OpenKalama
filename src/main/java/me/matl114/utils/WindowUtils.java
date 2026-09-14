package me.matl114.utils;

import java.io.IOException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;
import net.minecraft.util.Util.OperatingSystem;

public class WindowUtils {
   private static final String c = "Kalama";
   public static final MinecraftClient a = MinecraftClient.getInstance();
   private static final String d = "Add-Type -AssemblyName PresentationFramework; [System.Windows.MessageBox]::Show($env:KALAMA_MESSAGE, $env:KALAMA_TITLE) | Out-Null";
   public static final OperatingSystem b = Util.getOperatingSystem();

   private static String f(String message) {
      return message == null ? "" : message;
   }

   private static void showWindowsMessageBox(String title, String message) {
      startWindowsProcess(
         "Add-Type -AssemblyName PresentationFramework; [System.Windows.MessageBox]::Show($env:KALAMA_MESSAGE, $env:KALAMA_TITLE) | Out-Null", title, message
      );
   }

   public static boolean c(String title, String message) {
      return me.matl114.utils.process.KalamaHelperHelperB.a(title, message);
   }

   private static void startWindowsProcess(String script, String title, String message) {
      ProcessBuilder var3 = new ProcessBuilder("powershell.exe", "-NoProfile", "-NonInteractive", "-WindowStyle", "Hidden", "-Command", script);
      var3.environment().put("KALAMA_TITLE", title);
      var3.environment().put("KALAMA_MESSAGE", message);

      try {
         var3.start();
      } catch (IOException var5) {
      }
   }

   public static boolean b(String title, String message) {
      if (!isWindowsSystem()) {
         return false;
      } else {
         String var2 = e(title);
         String var3 = f(message);
         return d("sfh-window-notification", () -> showWindowsMessageBox(var2, var3));
      }
   }

   private static String h(String s) {
      return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
   }

   private static boolean d(String threadName, Runnable task) {
      Thread var2 = new Thread(task, threadName);
      var2.setDaemon(true);
      var2.start();
      return true;
   }

   public static boolean isWindowsSystem() {
      return b == OperatingSystem.WINDOWS;
   }

   private static String e(String title) {
      return title != null && !title.isBlank() ? title : "Kalama";
   }
}
