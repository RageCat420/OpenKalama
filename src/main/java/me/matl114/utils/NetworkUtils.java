package me.matl114.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import me.matl114.events.annotations.Modifiable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PendingUpdateManager;

@Modifiable
public class NetworkUtils {
   private static final MinecraftClient mc = MinecraftClient.getInstance();

   public static void restoreSequence(int sequenceRestore) {
      PendingUpdateManager var1 = mc.world.getPendingUpdateManager();
      if (var1.sequence == sequenceRestore) {
         var1.sequence--;
      }
   }

   public static ByteBuf createBytebuf() {
      return Unpooled.buffer();
   }

   public static int generateNextSequence() {
      PendingUpdateManager var0 = mc.world.getPendingUpdateManager().incrementSequence();
      int var1 = var0.sequence;
      var0.close();
      return var1;
   }
}
