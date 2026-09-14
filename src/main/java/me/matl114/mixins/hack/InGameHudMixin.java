package me.matl114.mixins.hack;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.matl114.hacks.modules.render.NoRender;
import me.matl114.hacks.modules.render.RenderExtra;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(
   value = {InGameHud.class},
   priority = 10
)
public class InGameHudMixin {
   @Shadow
   @Final
   private DebugHud field_2026;
   @Shadow
   @Final
   private MinecraftClient field_2035;
   @Unique
   private boolean tmpValue3;
   @Unique
   private boolean tmpValue;
   @Unique
   private boolean tmpValue2;

   @Inject(
      at = {@At("HEAD")},
      method = {"renderPlayerList(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V"}
   )
   private void rejectWurstHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      if (RenderExtra.INSTANCE.disableWurstHud.get()) {
         this.tmpValue3 = true;
         this.tmpValue2 = MinecraftClient.getInstance().options.hudHidden;
         this.field_2035.options.hudHidden = false;
         if (!this.field_2026.showDebugHud) {
            this.tmpValue = true;
            this.field_2026.showDebugHud = true;
         } else {
            this.tmpValue = false;
         }
      }
   }

   @Inject(
      method = {"renderPlayerList"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/world/ClientWorld;getScoreboard()Lnet/minecraft/scoreboard/Scoreboard;",
         shift = Shift.AFTER
      )}
   )
   private void resetHudData(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      if (this.tmpValue3) {
         this.tmpValue3 = false;
         this.field_2035.options.hudHidden = this.tmpValue2;
         if (this.tmpValue) {
            this.field_2026.showDebugHud = false;
         }
      }
   }

   @WrapWithCondition(
      method = {"renderMiscOverlays"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/hud/InGameHud;renderSpyglassOverlay(Lnet/minecraft/client/gui/DrawContext;F)V"
      )}
   )
   private boolean onRenderSpyGlass(InGameHud instance, DrawContext context, float scale) {
      return !NoRender.INSTANCE.CL();
   }

   @WrapWithCondition(
      method = {"renderMiscOverlays"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/util/Identifier;F)V",
         ordinal = 0
      )}
   )
   private boolean onRenderHeadItem(InGameHud instance, DrawContext context, Identifier texture, float opacity) {
      return !NoRender.INSTANCE.CL();
   }

   @WrapWithCondition(
      method = {"renderMiscOverlays"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/util/Identifier;F)V",
         ordinal = 1
      )}
   )
   private boolean onRenderFreeze(InGameHud instance, DrawContext context, Identifier texture, float opacity) {
      return !NoRender.INSTANCE.CK();
   }

   @WrapWithCondition(
      method = {"renderMiscOverlays"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/hud/InGameHud;renderPortalOverlay(Lnet/minecraft/client/gui/DrawContext;F)V"
      )}
   )
   private boolean onRenderPortal(InGameHud instance, DrawContext context, float scale) {
      return !NoRender.INSTANCE.CM();
   }

   @WrapWithCondition(
      method = {"renderMiscOverlays"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/hud/InGameHud;renderVignetteOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/Entity;)V"
      )}
   )
   private boolean onRenderVignette(InGameHud instance, DrawContext context, Entity entity) {
      return !NoRender.INSTANCE.CO();
   }
}
