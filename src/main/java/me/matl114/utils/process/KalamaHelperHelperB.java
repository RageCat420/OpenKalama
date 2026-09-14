package me.matl114.utils.process;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KalamaHelperHelperB {
    private static String b;
    private static final Logger a = LoggerFactory.getLogger("QueueNotice-Notification");

    static {
        a.info("[NotificationHelper] 通知系统初始化（子进程方案）...");
        String var0 = System.getProperty("java.home");
        b = var0 + "/bin/javaw.exe";

        try {
            ProcessBuilder var1 = new ProcessBuilder(b, "-version");
            var1.redirectErrorStream(true);
            Process var13 = var1.start();
            var13.waitFor();
            a.info("[NotificationHelper] Java 子进程可用: {}", b);
        } catch (Exception var12) {
            b = var0 + "/bin/java.exe";

            try {
                ProcessBuilder var2 = new ProcessBuilder(b, "-version");
                var2.redirectErrorStream(true);
                Process var3 = var2.start();
                var3.waitFor();
                a.info("[NotificationHelper] Java 子进程可用（回退）: {}", b);
            } catch (Exception var11) {
                a.error("[NotificationHelper] 找不到 Java 可执行文件，通知功能不可用");
                b = null;
            }
        }
    }

    public static boolean a(String title, String message) {
        if (b == null) {
            return false;
        } else {
            Thread var2 = new Thread(() -> b(title, message), "QueueNotice-Send");
            var2.setDaemon(true);
            var2.start();
            return true;
        }
    }

    private static void b(String title, String message) {
        try {
            String var2 = NotificationServerProcess.getModClasspath();
            if (var2 == null || var2.isEmpty()) {
                return;
            }

            String var3 = NotificationServerProcess.class.getName();
            ArrayList var4 = new ArrayList();
            var4.add(b);
            var4.add("-cp");
            var4.add(var2);
            var4.add(var3);
            var4.add(title);
            var4.add(message);
            ProcessBuilder var5 = new ProcessBuilder(var4);
            var5.redirectErrorStream(true);
            Process var6 = var5.start();
            boolean var7 = var6.waitFor(15L, TimeUnit.SECONDS);
            if (var7) {
                int var8 = var6.exitValue();
                if (var8 == 0) {
                    a.info("[NotificationHelper] 通知子进程正常退出");
                } else {
                    a.warn("[NotificationHelper] 通知子进程退出码: {}", var8);
                }
            } else {
                a.warn("[NotificationHelper] 通知子进程超时，强制终止");
                var6.destroyForcibly();
            }
        } catch (Exception var9) {
            a.error("[NotificationHelper] 通知子进程启动失败: {}", var9.getMessage(), var9);
        }
    }
}
