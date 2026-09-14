package me.matl114.hacks.modules.render;

import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.events.impl.MouseScrollAction;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.NBTTypes;
import me.matl114.hacks.utils.config.OptionalPrimitive;
import me.matl114.managers.Configs;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;

public class Zoom extends BaseModule {
   static boolean currentHasScrollListener = false;
   public DoubleRef minZoomScale;
   private Double IR;
   Double IQ;
   public final ModulePath ow = makePath(Configs.i, "render");
   public FlagRef scrollScale;
   public DoubleRef defaultZoom;
   public KeyBindRef IK;
   public final ModulePath IJ = makePath(Configs.i, "zoom");
   public NBTRef<OptionalPrimitive<Double>> overrideCommonFov;
   public FlagRef ae = this.builder(this.IJ.addEnable(), Boolean.class).defaultValue(true).build();

   private void Xo(Event<MouseScrollAction> eventMouseScroll) {
      if (this.IQ == null) {
         this.IQ = this.defaultZoom.get();
      }

      double var2 = ((MouseScrollAction)eventMouseScroll.b).horizontal();
      if (var2 > 0.0) {
         this.IQ = this.IQ * 1.1;
      } else if (var2 < 0.0) {
         this.IQ = this.IQ * 0.9;
      }

      this.IQ = Math.max(this.minZoomScale.get(), this.IQ);
      eventMouseScroll.cancel();
   }

   public void tickFov(Event<Float> eventFov) {
      if (this.ae.get()) {
         if (this.IK.get().d() && mc.currentScreen == null) {
            if (this.IQ == null) {
               this.IQ = this.defaultZoom.get();
            }

            if (this.IR == null) {
               this.IR = (Double)mc.options.getMouseSensitivity().getValue();
            }

            mc.options.getMouseSensitivity().setValue(this.IR / this.IQ);
            this.Xn();
            eventFov.context((float)(((Float)eventFov.e()).floatValue() / this.IQ));
         } else {
            this.IQ = null;
            if (this.IR != null) {
               mc.options.getMouseSensitivity().setValue(this.IR);
               this.IR = null;
            }

            if (this.overrideCommonFov.get().isPresent()) {
               float var2 = (float)(Object)this.overrideCommonFov.get().getValue().doubleValue();
               eventFov.context((Float)eventFov.e() / var2);
            }
         }
      } else if (this.IR != null) {
         mc.options.getMouseSensitivity().setValue(this.IR);
         this.IR = null;
      }
   }

   public void Xn() {
      if (!currentHasScrollListener && this.scrollScale.get()) {
         currentHasScrollListener = true;
         Listener.br().j(mouseEvent -> {
            if (this.ae.get() && this.scrollScale.get() && this.IK.get().d()) {
               this.Xo(mouseEvent);
               return true;
            } else {
               this.IQ = null;
               currentHasScrollListener = false;
               return false;
            }
         });
      }
   }

   public Zoom() {
      super("Zoom");
      this.IK = this.hotkey(this.IJ.addHotkey(), new MultiKeyBind()).build();
      this.scrollScale = this.builder(this.IJ.add("scroll-scale"), Boolean.class).defaultValue(true).build();
      this.defaultZoom = this.doubleBuilder(this.IJ.add("default-zoom")).defaultValue(3.0).validator(Configs.doubleRange(0.0, 114514.0)).build();
      this.minZoomScale = this.doubleBuilder(this.IJ.add("min-zoom-scale")).defaultValue(1.0).validator(Configs.doubleRange(0.0, 114514.0)).build();
      this.overrideCommonFov = this.builder(this.IJ.add("override-common-fov"), OptionalPrimitive.DOUBLE_TYPE)
         .defaultValue(new OptionalPrimitive<>(false, NBTTypes.e, 1.0))
         .build();
      this.IQ = null;
      this.IR = null;
      this.bindFlag(this.ae);
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(RenderListener.H(), this::tickFov);
   }
}
