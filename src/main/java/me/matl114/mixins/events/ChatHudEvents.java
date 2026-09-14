package me.matl114.mixins.events;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import me.matl114.accessors.events.ChatHudAccess;
import me.matl114.accessors.events.ChatHudLineAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.hud.ChatHudLine.Visible;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({ChatHud.class})
public class ChatHudEvents implements ChatHudAccess {
   @Shadow
   @Final
   private List<Visible> field_2064;
   @Shadow
   @Final
   private List<ChatHudLine> field_2061;
   @Unique
   public String uniqueId;

   @Unique
   @Override
   public void setUniqueMessageId(String id) {
      this.uniqueId = id;
   }

   @Unique
   @Override
   public ArrayList<Visible> getVisibleLines() {
      return (ArrayList<Visible>)(Object)this.field_2064;
   }

   @Inject(
      method = {"addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onMessageAdd(
      Text message, MessageSignatureData signatureData, MessageIndicator indicator, CallbackInfo ci, @Local(argsOnly = true) LocalRef<Text> textLocalRef
   ) {
      if (!Listener.aa().d()) {
         Event<Text> addMessageEvent = new Event<>(message, true, true, signatureData, indicator);
         Listener.aa().catchEvent(addMessageEvent);
         if (addMessageEvent.d()) {
            ci.cancel();
         }

         textLocalRef.set(addMessageEvent.e());
      }
   }

   @Inject(
      method = {"addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/hud/ChatHud;logChatMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V",
         shift = Shift.AFTER
      )}
   )
   private void onChatHudLineCreate(Text message, MessageSignatureData signatureData, MessageIndicator indicator, CallbackInfo ci, @Local ChatHudLine line) {
      ChatHudLineAccess.of(line).setUniqueMessageId(this.uniqueId);
   }

   @Unique
   @Override
   public void clearUniqueMessages(String id) {
      this.field_2064.removeIf(s -> Objects.equals(ChatHudLineAccess.of(s).getUniqueMessageId(), id));
      this.field_2061.removeIf(s -> Objects.equals(ChatHudLineAccess.of(s).getUniqueMessageId(), id));
   }

   @Inject(
      method = {"addVisibleMessage"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onVisibleMessageAdd(ChatHudLine message, CallbackInfo ci, @Local(argsOnly = true) LocalRef<ChatHudLine> lineLocalRef) {
      if (!Listener.ab().d()) {
         Event<ChatHudLine> addMessageEvent = new Event<>(message, true, true);
         Listener.ab().catchEvent(addMessageEvent);
         if (addMessageEvent.d()) {
            ci.cancel();
            return;
         }

         lineLocalRef.set(addMessageEvent.e());
      }
   }

   @ModifyExpressionValue(
      method = {"addVisibleMessage"},
      at = {@At(
         value = "NEW",
         target = "(ILnet/minecraft/text/OrderedText;Lnet/minecraft/client/gui/hud/MessageIndicator;Z)Lnet/minecraft/client/gui/hud/ChatHudLine$Visible;"
      )}
   )
   private Visible onVisibleLineCreate(Visible original, @Local(argsOnly = true) ChatHudLine line) {
      String unique = ChatHudLineAccess.of(line).getUniqueMessageId();
      if (unique != null) {
         ChatHudLineAccess.of(original).setUniqueMessageId(unique);
      }

      return original;
   }
}
