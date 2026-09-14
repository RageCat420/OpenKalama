package me.matl114.mixins.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.MouseDragAction;
import me.matl114.events.impl.MouseMoveAction;
import me.matl114.managers.input.SimpleInputManager;
import me.matl114.utils.ScreenUtils;
import me.matl114.utils.collections.KalamaHelperHelperM;
import me.matl114.utils.collections.Point;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(
   value = {Mouse.class},
   priority = 1
)
public class MouseEvents {
   @Shadow
   @Final
   private MinecraftClient field_1779;
   @Shadow
   private double field_1789;
   @Shadow
   private double field_1787;
   @Shadow
   private int field_1780;

   @Shadow
   public double method_1603() { }

   @Shadow
   public double method_1604() { }

   @Inject(
      method = {"onMouseScroll"},
      cancellable = true,
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/MinecraftClient;getOverlay()Lnet/minecraft/client/gui/screen/Overlay;"
      )}
   )
   private void onMouseScroll(long handle, double xOffset, double yOffset, CallbackInfo ci) {
      if (MinecraftClient.getInstance().getOverlay() == null && SimpleInputManager.h().onMouseScroll(xOffset, yOffset)) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"onMouseButton"},
      cancellable = true,
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/MinecraftClient;getOverlay()Lnet/minecraft/client/gui/screen/Overlay;",
         ordinal = 0,
         shift = Shift.BEFORE
      )}
   )
   private void onMouseClick(long handle, int button, int action, int mods, CallbackInfo ci, @Local(ordinal = 3) int i) {
      Point coord = ScreenUtils.b(this.field_1779, (Mouse)(Object)this);
      if (SimpleInputManager.h().onMouseClick(coord.a, coord.b, i, action, mods)) {
         ci.cancel();
      }
   }

   @Redirect(
      method = {"tick"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screen/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V",
         ordinal = 0
      )
   )
   private void onMouseMove(Runnable task, String errorTitle, String screenName, @Local(ordinal = 2) double f, @Local(ordinal = 3) double g) {
      Event<MouseMoveAction> event = new Event<>(new MouseMoveAction((Mouse)(Object)this, f, g), true, false);
      Listener.bs().catchEvent(event);
      if (!event.d()) {
         Screen.wrapScreenError(task, errorTitle, screenName);
      }
   }

   @Redirect(
      method = {"tick"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screen/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V",
         ordinal = 1
      )
   )
   private void onMouseDrag(
      Runnable task,
      String errorTitle,
      String screenName,
      @Local(ordinal = 2) double f,
      @Local(ordinal = 3) double g,
      @Local(ordinal = 4) double h,
      @Local(ordinal = 5) double i
   ) {
      Event<MouseDragAction> event = new Event<>(new MouseDragAction((Mouse)(Object)this, f, g, h, i), true, false);
      Listener.bt().catchEvent(event);
      if (!event.d()) {
         Screen.wrapScreenError(task, errorTitle, screenName);
      }
   }

   @Inject(
      method = {"tick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/Mouse;isCursorLocked()Z"
      )},
      cancellable = true
   )
   private void onScreenNull(CallbackInfo ci) {
      if (MinecraftClient.getInstance().currentScreen == null && MinecraftClient.getInstance().getOverlay() == null) {
         double f = this.method_1603() * this.field_1779.getWindow().getScaledWidth() / this.field_1779.getWindow().getWidth();
         double g = this.method_1604() * this.field_1779.getWindow().getScaledHeight() / this.field_1779.getWindow().getHeight();
         Event<MouseMoveAction> event = new Event<>(new MouseMoveAction((Mouse)(Object)this, f, g), true, false);
         Listener.bs().catchEvent(event);
         if (this.field_1780 != -1) {
            double h = this.field_1789 * this.field_1779.getWindow().getScaledWidth() / this.field_1779.getWindow().getWidth();
            double i = this.field_1787 * this.field_1779.getWindow().getScaledHeight() / this.field_1779.getWindow().getHeight();
            Event<MouseDragAction> event2 = new Event<>(new MouseDragAction((Mouse)(Object)this, f, g, h, i), true, false);
            Listener.bt().catchEvent(event2);
         }
      }
   }

   @WrapOperation(
      method = {"updateMouse"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"
      )}
   )
   private void onMouseUpdateLook(ClientPlayerEntity instance, double x, double y, Operation<Void> original) {
      Event<KalamaHelperHelperM> event = new Event<>(new KalamaHelperHelperM(x, y), true, true);
      Listener.aI().catchEvent(event);
      if (!event.d()) {
         original.call(new Object[]{instance, event.e().a, event.e().b});
      }
   }
}
