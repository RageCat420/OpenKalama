package me.matl114.mixins.events;

import me.matl114.events.Event;
import me.matl114.events.GlobalEventVars;
import me.matl114.events.Listener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Main.class})
public class MainEvents {
   @Inject(
      method = {"main"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/MinecraftClient;run()V",
         shift = Shift.AFTER
      )}
   )
   private static void onMain(String[] args, CallbackInfo ci) {
      if (MinecraftClient.getInstance() != null) {
         if (GlobalEventVars.a != null) {
            GlobalEventVars.a = null;
            GlobalEventVars.c = null;
            mainLoop(MinecraftClient.getInstance());
         } else {
            if (GlobalEventVars.c == null) {
               MinecraftClient mc = MinecraftClient.getInstance();
               GlobalEventVars.c = new Event<>(mc, mc.isRunning(), false, null);
               if (!Listener.Y().d()) {
                  Listener.Y().catchEvent(GlobalEventVars.c);
               }
            }

            if (MinecraftClient.getInstance().isRunning()) {
               if (!GlobalEventVars.c.d()) {
                  GlobalEventVars.c = null;
                  GlobalEventVars.a = null;
                  return;
               }

               GlobalEventVars.c = null;
               GlobalEventVars.a = null;
               mainLoop(MinecraftClient.getInstance());
            } else {
               GlobalEventVars.c = null;
            }
         }
      }
   }

   private static void mainLoop(MinecraftClient mc) {
      while (true) {
         GlobalEventVars.c = null;
         GlobalEventVars.a = null;
         mc.run();
         if (GlobalEventVars.a != null) {
            GlobalEventVars.a = null;
         } else if (!Listener.Y().d()) {
            Event<MinecraftClient> event = new Event<>(mc, mc.isRunning(), false, null);
            Listener.Y().catchEvent(event);
            if (!event.d()) {
               return;
            }
         }
      }
   }
}
