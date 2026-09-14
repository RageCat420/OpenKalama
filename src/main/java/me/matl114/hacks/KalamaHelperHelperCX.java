package me.matl114.hacks;

import java.util.function.BooleanSupplier;
import me.matl114.managers.Tasks;
import net.minecraft.client.util.math.MatrixStack;

public class KalamaHelperHelperCX implements KalamaHelperHelperG {
   Runnable e;
   int endTick;
   BooleanSupplier d;
   KalamaHelperHelperRX[] renderObjects;
   boolean registered = false;

   @Override
   public boolean stillRender() {
      return this.registered && Tasks.b() <= this.endTick && (this.d == null || !this.d.getAsBoolean());
   }

   public KalamaHelperHelperCX e(Runnable stopFuture) {
      this.e = stopFuture;
      return this;
   }

   @Override
   public void h() {
      if (!this.registered) {
         this.registered = true;
         RenderTasks.i(this);
      }
   }

   public KalamaHelperHelperCX(int tick, KalamaHelperHelperRX... renderObjects) {
      this.d = null;
      this.e = null;
      this.endTick = tick + Tasks.b();
      this.renderObjects = renderObjects;
   }

   public KalamaHelperHelperCX cancelTimer() {
      this.endTick = Integer.MAX_VALUE;
      return this;
   }

   public KalamaHelperHelperCX d(BooleanSupplier autoStopPredicate) {
      this.d = autoStopPredicate;
      return this;
   }

   @Override
   public void b() {
      this.registered = false;
      this.endTick = -1;
      if (this.e != null) {
         this.e.run();
      }
   }

   public KalamaHelperHelperCX(KalamaHelperHelperRX... renderObjects) {
      this.d = null;
      this.e = null;
      this.endTick = Integer.MAX_VALUE;
      this.renderObjects = renderObjects;
   }

   @Override
   public void renderVirtual(MatrixStack stack, float partialTicks) {
      for (KalamaHelperHelperRX var6 : this.renderObjects) {
         var6.render(stack, partialTicks);
      }
   }

   public KalamaHelperHelperCX refreshTimer(int val) {
      this.endTick = val + Tasks.b();
      return this;
   }
}
