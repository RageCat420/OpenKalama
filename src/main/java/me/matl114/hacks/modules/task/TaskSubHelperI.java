package me.matl114.hacks.modules.task;

import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperM;
import me.matl114.gui.basic.KalamaHelperHelperP;

class TaskSubHelperI implements KalamaHelperHelperP {
   private final TaskSubHelperR val$slideMeta;

   boolean move;
   double ac;
   double ad;

   public boolean onAction(ExecutableWidget element, double mouseX, double mouseY, int button, KalamaHelperHelperM type) {
      if (type == KalamaHelperHelperM.nR) {
         if (element.isMouseOver(mouseX, mouseY)) {
            this.ac = mouseX;
            this.ad = mouseY;
            this.move = false;
            return true;
         } else {
            return false;
         }
      } else if (type == KalamaHelperHelperM.nS) {
         if (Math.abs(mouseX - this.ac) >= 1.0 || Math.abs(mouseY - this.ad) >= 1.0) {
            int var8 = (int)(mouseX - this.ac);
            int var9 = (int)(mouseY - this.ad);
            this.val$slideMeta.a(this.val$slideMeta.c() + var8);
            this.val$slideMeta.b(this.val$slideMeta.d() + var9);
            this.move = true;
         }

         return true;
      } else if (type == KalamaHelperHelperM.nQ && button == 0 || type == KalamaHelperHelperM.nP && button != 0) {
         if (!this.move) {
            if (element.isMouseOver(mouseX, mouseY)) {
               this.val$slideMeta.slidingDown = !this.val$slideMeta.slidingDown;
            }
         } else {
            this.move = false;
         }

         return true;
      } else {
         return true;
      }
   }

   TaskSubHelperI(final ClickGui this$0, final TaskSubHelperR param2) {
      this.val$slideMeta = param2;
      this.move = false;
   }

   public boolean a(ExecutableWidget element, double mouseX, double mouseY, int button) {
      return true;
   }
}
