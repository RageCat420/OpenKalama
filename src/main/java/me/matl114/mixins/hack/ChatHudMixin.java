package me.matl114.mixins.hack;

import java.util.List;
import me.matl114.hacks.ChatTasks;
import me.matl114.hacks.modules.chat.ChatExtra;
import me.matl114.hacks.modules.render.SleepMode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.ChatHudLine.Visible;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin({ChatHud.class})
public class ChatHudMixin {
   @Shadow
   @Final
   private List<Visible> field_2064;

   @Inject(
      method = {"addVisibleMessage"},
      at = {@At(
         value = "INVOKE",
         target = "Ljava/util/List;remove(I)Ljava/lang/Object;",
         shift = Shift.BEFORE
      )},
      cancellable = true
   )
   private void resizeChatHistoryMaxLength(ChatHudLine message, CallbackInfo ci) {
      if (ChatExtra.INSTANCE.overrideChatHistoryLen.get()) {
         int chat = ChatTasks.f().chatHistoryLen.get();
         if (chat > 0 && this.field_2064.size() <= chat) {
            ci.cancel();
         }
      }
   }

   @Inject(
      method = {"isChatFocused"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onSleepingChatScreenUseChatHud(CallbackInfoReturnable<Boolean> cir) {
      if (SleepMode.INSTANCE.aim() && SleepMode.INSTANCE.aiq() instanceof ChatScreen) {
         cir.setReturnValue(true);
      }
   }
}
