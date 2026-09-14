package me.matl114.mixins.events;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import me.matl114.accessors.gui.ScreenAccess;
import me.matl114.accessors.interfaces.MetadataHolder;
import me.matl114.events.Listener;
import me.matl114.gui.basic.DisplayWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Environment(EnvType.CLIENT)
@Mixin({Screen.class})
public abstract class ScreenEvents extends AbstractParentElement implements MetadataHolder, ScreenAccess {
   @Unique
   List<Consumer<Screen>> initializeTasks;
   @Unique
   List<Runnable> screenCloseFuture;
   @Unique
   Screen parent = null;

   @Override
   public void addInitTask(Consumer<Screen> runnable) {
      if (this.initializeTasks == null) {
         this.initializeTasks = new ArrayList<>();
      }

      this.initializeTasks.add(runnable);
   }

   @Override
   public void addCloseFuture(Runnable runnable) {
      if (this.screenCloseFuture == null) {
         this.screenCloseFuture = new ArrayList<>();
      }

      this.screenCloseFuture.add(runnable);
   }

   @Inject(
      method = {"close"},
      at = {@At("RETURN")}
   )
   private void onScreenClsoe(CallbackInfo ci) {
      Listener.ad().broadcast((Screen)(Object)this);
      if (this.screenCloseFuture != null) {
         for (Runnable runnable : this.screenCloseFuture) {
            runnable.run();
         }
      }
   }

   @Inject(
      method = {"init(Lnet/minecraft/client/MinecraftClient;II)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screen/Screen;setInitialFocus()V",
         shift = Shift.BEFORE
      )}
   )
   public void onPostInitialization(MinecraftClient client, int width, int height, CallbackInfo ci) {
      Listener.ai().broadcast((Screen)(Object)this);
      if (this.initializeTasks != null) {
         for (Consumer<Screen> runnable : this.initializeTasks) {
            runnable.accept((Screen)(Object)this);
         }
      }
   }

   @Inject(
      method = {"init(Lnet/minecraft/client/MinecraftClient;II)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screen/Screen;initTabNavigation()V",
         shift = Shift.AFTER
      )}
   )
   public void onClearAndInit(CallbackInfo ci) {
      Listener.ai().broadcast((Screen)(Object)this);
      if (this.initializeTasks != null) {
         for (Consumer<Screen> runnable : this.initializeTasks) {
            runnable.accept((Screen)(Object)this);
         }
      }
   }

   @Inject(
      method = {"resize"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screen/Screen;initTabNavigation()V",
         shift = Shift.AFTER
      )}
   )
   public void onResize(MinecraftClient client, int width, int height, CallbackInfo ci) {
      Listener.ai().broadcast((Screen)(Object)this);
      if (this.initializeTasks != null) {
         for (Consumer<Screen> runnable : this.initializeTasks) {
            runnable.accept((Screen)(Object)this);
         }
      }
   }

   @Shadow
   protected abstract <T extends Element & Drawable & Selectable> T method_37063(T var1);

   @Shadow
   protected void method_37066(Element child) {
   }

   @Shadow
   protected abstract <T extends Drawable> T method_37060(T var1);

   @Unique
   @Override
   public <T extends Element & Drawable & Selectable> T addDrawableChildTo(T drawable) {
      if (drawable instanceof DisplayWidget display) {
         this.method_37060(display);
         return drawable;
      } else {
         return this.method_37063(drawable);
      }
   }

   @Unique
   @Override
   public void removeChildFrom(Element val) {
      this.method_37066(val);
   }

   @Unique
   @Override
   public void open() {
      MinecraftClient.getInstance().setScreen((Screen)(Object)this);
   }

   @Unique
   @Override
   public void openFromCurrent() {
      this.parent = MinecraftClient.getInstance().currentScreen;
      this.open();
   }

   @Unique
   @Override
   public void openFrom(Screen parent) {
      this.parent = parent;
      this.open();
   }

   @Unique
   @Override
   public void switchToScreen(Screen anotherScreen) {
      Screen p = this.parent;
      this.parent = null;
      ScreenAccess.of(anotherScreen).setParent(p);
      MinecraftClient.getInstance().setScreen(anotherScreen);
   }

   @Override
   public void switchFromCurrent() {
      Screen current = MinecraftClient.getInstance().currentScreen;
      if (current == null) {
         this.parent = null;
      } else {
         this.parent = ((ScreenEvents)(Object)current).parent;
         ((ScreenEvents)(Object)current).parent = null;
      }

      MinecraftClient.getInstance().setScreen((Screen)(Object)this);
   }

   @ModifyArgs(
      method = {"close"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"
      )
   )
   public void onRedirectReturnScreen(Args args) {
      if (this.parent != null) {
         args.set(0, this.parent);
         this.parent = null;
      }
   }

   @Override
   public Screen getParent() {
      return this.parent;
   }

   @Override
   public void setParent(Screen parent) {
      this.parent = parent;
   }
}
