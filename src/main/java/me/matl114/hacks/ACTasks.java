package me.matl114.hacks;

import java.util.function.Consumer;
import me.matl114.hacks.api.ModuleManager;
import me.matl114.hacks.modules.ac.Disabler;
import me.matl114.hacks.modules.ac.PacketOrderManager;
import me.matl114.hacks.modules.ac.PostManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class ACTasks {
   private static Disabler c;
   private static final MinecraftClient a = MinecraftClient.getInstance();
   private static PacketOrderManager d;
   private static PostManager b;

   public static void c(Consumer<ClientPlayNetworkHandler> packet) {
      b.addNextPreTickAction(packet);
   }

   public static PostManager e() {
      return b;
   }

   public static PacketOrderManager g() {
      return d;
   }

   private static void d(ModuleManager moduleManager) {
      b = new PostManager().register(moduleManager);
      c = new Disabler().register(moduleManager);
      d = new PacketOrderManager().register(moduleManager);
   }

   public static Disabler f() {
      return c;
   }

   public static void init() {
   }

   public static void b(Consumer<ClientPlayNetworkHandler> handler) {
      b.KQ(handler);
   }

   static {
      ExtraTasks.c().registerFactories(ACTasks::d);
   }
}
