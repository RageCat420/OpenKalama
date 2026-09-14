package me.matl114.hacks.modules.render;

public class RenderOptimize$RenderController {
   int e;
   boolean a = false;
   boolean d;
   boolean b = false;
   boolean c = false;

   public RenderOptimize$RenderController g(boolean hideLabelBack) {
      this.b = hideLabelBack;
      return this;
   }

   public RenderOptimize$RenderController h(boolean hideAll) {
      this.c = hideAll;
      return this;
   }

   public RenderOptimize$RenderController() {
      this.d = false;
      this.e = 0;
   }

   public boolean d() {
      return this.d;
   }

   public RenderOptimize$RenderController lastUpdateRaycastTick(int lastUpdateRaycastTick) {
      this.e = lastUpdateRaycastTick;
      return this;
   }

   public boolean c() {
      return this.c;
   }

   public RenderOptimize$RenderController i(boolean raycastResult) {
      this.d = raycastResult;
      return this;
   }

   public int e() {
      return this.e;
   }

   public boolean a() {
      return this.a;
   }

   public boolean b() {
      return this.b;
   }

   public RenderOptimize$RenderController f(boolean hideLabelFront) {
      this.a = hideLabelFront;
      return this;
   }
}
