package me.matl114.utils;

import java.util.Arrays;
import me.matl114.SlimefunHelper;
import me.matl114.events.annotations.Modifiable;
import me.matl114.hacks.ChatTasks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Modifiable
public class Debug {
    private static Logger logger = LoggerFactory.getLogger("Kalama");

    public static void sendPlayer(Text text) {
        if (MinecraftClient.getInstance().player != null) {
            if (MinecraftClient.getInstance().isOnThread()) {
                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(text);
            } else {
                MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance()
                        .inGameHud
                        .getChatHud()
                        .addMessage(text));
            }
        } else {
            ChatTasks.d(text);
        }
    }

    public static void e(Object... objs) {
        a(String.join(
                " ",
                Arrays.stream(objs).map(o -> o == null ? "null" : o.toString()).toArray(String[]::new)));
    }

    public static void a(String string) {
        logger.info(string);
    }

    public static void stackTrace() {
        StackTraceElement[] var0 = Thread.currentThread().getStackTrace();

        for (StackTraceElement var4 : var0) {
            a(var4.toString());
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void chat(Object... values) {
        MutableText var1 = Text.literal("");
        boolean var2 = true;

        for (Object var6 : values) {
            if (var2) {
                var2 = false;
            } else {
                var1.append(Text.of(" "));
            }

            if (var6 instanceof Text) {
                var1.append((Text) var6);
            } else {
                var1.append(Text.literal(var6 == null ? "null" : var6.toString()));
            }
        }

        sendPlayer(var1);
    }

    public static void b(Object string) {
        if (string instanceof Text var1) {
            sendPlayer(var1);
        } else {
            sendPlayer(Text.literal(string == null ? "null" : string.toString()));
        }
    }

    public static boolean test(Object obj) {
        f(obj);
        return false;
    }

    public static void h(Object object) {
        if (SlimefunHelper.DEV_ENV) {
            f(object);
        }
    }

    public static void g(Object... objs) {
        if (SlimefunHelper.DEV_ENV) {
            e(objs);
        }
    }

    public static void f(Object object) {
        if (object instanceof Throwable var1) {
            logger.info("Printing stack trace:", var1);
        } else {
            logger.info(object != null ? object.toString() : "null");
        }
    }
}
