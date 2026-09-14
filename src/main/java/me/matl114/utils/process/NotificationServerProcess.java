package me.matl114.utils.process;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;

public class NotificationServerProcess {
   public static String getModClasspath() {
      try {
         URI var0 = KalamaHelperHelperB.class.getProtectionDomain().getCodeSource().getLocation().toURI();
         File var1 = new File(var0);
         return var1.getAbsolutePath();
      } catch (Exception var2) {
         return null;
      }
   }

   public static void main(String[] args) {
      if (args.length < 2) {
         System.err.println("用法: NotificationServer <标题> <消息>");
         System.exit(1);
      } else {
         String var1 = args[0];
         String var2 = args[1];
         System.out.println("[NotificationServer] 发送通知: title=" + var1 + ", message=" + var2);
         if (SystemTray.isSupported() && SystemTray.getSystemTray() != null) {
            TrayIcon var3 = null;

            try {
               BufferedImage var4 = new BufferedImage(16, 16, 2);
               Graphics2D var5 = var4.createGraphics();
               var5.setColor(Color.GREEN);
               var5.fillOval(0, 0, 16, 16);
               var5.dispose();
               var3 = new TrayIcon(var4, "Queue Notice Mod");
               var3.setImageAutoSize(true);
               SystemTray.getSystemTray().add(var3);
               var3.displayMessage(var1, var2, MessageType.INFO);
               System.out.println("[NotificationServer] 通知已发送");
               Thread.sleep(6000L);
            } catch (Exception var14) {
               System.err.println("[NotificationServer] 错误: " + var14.getMessage());
               var14.printStackTrace();
               System.exit(1);
            } finally {
               if (var3 != null) {
                  try {
                     SystemTray.getSystemTray().remove(var3);
                  } catch (Exception var13) {
                  }
               }
            }

            System.out.println("[NotificationServer] 完成，退出");
            System.exit(0);
         } else {
            System.err.println("[NotificationServer] SystemTray 不支持");
            System.exit(1);
         }
      }
   }
}
