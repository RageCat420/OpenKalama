package me.matl114.mixins.hack;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.Objects;
import me.matl114.accessors.access.ChatScreenAccess;
import me.matl114.accessors.gui.CustomFocusBehaviourScreenAccess;
import me.matl114.hacks.ChatTasks;
import me.matl114.hacks.utils.chat.ChatScreenTextFieldWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin({ChatScreen.class})
public abstract class ChatScreenMixin extends Screen implements CustomFocusBehaviourScreenAccess, ChatScreenAccess {
   @Shadow
   protected TextFieldWidget field_2382;
   @Shadow
   private int field_2387;
   @Shadow
   protected String field_18973;

   @Shadow
   public void method_44056(String var1, boolean var2) { }

   @Unique
   @Override
   public void resetMessageHistoryIndex() {
      this.field_2387 = MinecraftClient.getInstance().inGameHud.getChatHud().getMessageHistory().size();
   }

   @Accessor("chatInputSuggestor")
   @Override
public abstract ChatInputSuggestor getSuggestor() ;

   @Override
   public TextFieldWidget getInputWidget() {
      return this.field_2382;
   }

   protected ChatScreenMixin(Text title) {
      super(title);
   }

   @WrapOperation(
      method = {"onChatFieldUpdate"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screen/ChatInputSuggestor;setWindowActive(Z)V"
      )}
   )
   private void fixChatInputSuggestor(ChatInputSuggestor instance, boolean windowActive, Operation<Void> original, @Local(argsOnly = true) String chatText) {
      if (ChatTasks.f().enableTabFix.get()) {
         original.call(new Object[]{instance, true});
      } else {
         original.call(new Object[]{instance, !Objects.equals(chatText, this.field_18973)});
      }
   }

   @Unique
   @Override
   public Element getDefaultElement() {
      return this.field_2382;
   }

   @Unique
   @Override
   public boolean canFocusButtonWhenClicked() {
      return false;
   }

   @WrapOperation(
      method = {"normalize"},
      at = {@At(
         value = "INVOKE",
         target = "Ljava/lang/String;trim()Ljava/lang/String;"
      )}
   )
   private String cancelTrim(String instance, Operation<String> original) {
      return !ChatTasks.f().escapeTrimChat.get() ? (String)original.call(new Object[]{instance}) : instance;
   }

   @WrapOperation(
      method = {"normalize"},
      at = {@At(
         value = "INVOKE",
         target = "Lorg/apache/commons/lang3/StringUtils;normalizeSpace(Ljava/lang/String;)Ljava/lang/String;",
         remap = false
      )}
   )
   private String cancelNormalize(String actualChar, Operation<String> original) {
      return !ChatTasks.f().escapeNormalizeSpaceChat.get() ? (String)original.call(new Object[]{actualChar}) : actualChar;
   }

   @WrapOperation(
      method = {"normalize"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/StringHelper;truncateChat(Ljava/lang/String;)Ljava/lang/String;"
      )}
   )
   private String cancelTruncate(String text, Operation<String> original) {
      return !ChatTasks.f().ignoreChatLenLimit.get() ? (String)original.call(new Object[]{text}) : text;
   }

   @WrapOperation(
      method = {"init"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/client/gui/screen/ChatScreen;chatField:Lnet/minecraft/client/gui/widget/TextFieldWidget;",
         ordinal = 0
      )}
   )
   private void modifyTextFieldWidget(ChatScreen instance, TextFieldWidget value, Operation<Void> original) {
       original.call(new Object[]{instance, new ChatScreenTextFieldWidget((ChatScreen)(Object)this)});
    }
}

