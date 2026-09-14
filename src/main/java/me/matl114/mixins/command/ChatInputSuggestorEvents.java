package me.matl114.mixins.command;

import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import me.matl114.commands.MainCommand;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({ChatInputSuggestor.class})
public class ChatInputSuggestorEvents {
   @Shadow
   @Final
   TextFieldWidget field_21599;
   @Shadow
   private CompletableFuture<Suggestions> field_21611;
   @Shadow
   private boolean field_21614;

   @Shadow
   protected abstract void method_23937();

   @Shadow
   public void method_23920(boolean var1) { }

   @Inject(
      method = {"refresh"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;getCursor()I",
         shift = Shift.BEFORE
      )},
      cancellable = true
   )
   private void parseClientCommandsTabComplete(CallbackInfo ci) {
      if (MainCommand.isClientCommand(this.field_21599.getText())) {
         if (!this.field_21614) {
            CompletableFuture<Suggestions> suggestionCompletableFuture = MainCommand.tabCompleteClientCommand(
               this.field_21599.getText(), this.field_21599.getCursor()
            );
            if (suggestionCompletableFuture != null) {
               this.field_21611 = suggestionCompletableFuture;
               this.field_21611.thenRun(() -> {
                  if (this.field_21611.isDone()) {
                     this.method_23920(true);
                  }
               });
            }
         }

         ci.cancel();
      }
   }
}
