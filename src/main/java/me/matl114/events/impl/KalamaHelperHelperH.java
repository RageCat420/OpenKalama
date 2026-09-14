package me.matl114.events.impl;

import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

public class KalamaHelperHelperH {
   ActionResult b;
   BlockHitResult a;
   final Hand c;

   public KalamaHelperHelperH e(BlockHitResult hitResult) {
      this.a = hitResult;
      return this;
   }

   public ActionResult c() {
      return this.b;
   }

   public KalamaHelperHelperH f(ActionResult actionResult) {
      this.b = actionResult;
      return this;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof KalamaHelperHelperH var2)) {
         return false;
      } else if (!var2.a(this)) {
         return false;
      } else {
         BlockHitResult var3 = this.b();
         BlockHitResult var4 = var2.b();
         if (var3 == null ? var4 == null : var3.equals(var4)) {
            ActionResult var5 = this.c();
            ActionResult var6 = var2.c();
            if (var5 == null ? var6 == null : var5.equals(var6)) {
               Hand var7 = this.d();
               Hand var8 = var2.d();
               return var7 == null ? var8 == null : var7.equals(var8);
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public KalamaHelperHelperH(BlockHitResult hitResult, ActionResult actionResult, Hand hand) {
      this.a = hitResult;
      this.b = actionResult;
      this.c = hand;
   }

   public BlockHitResult b() {
      return this.a;
   }

   @Override
   public String toString() {
      return "UseItemOnBlock(hitResult=" + this.b() + ", actionResult=" + this.c() + ", hand=" + this.d() + ")";
   }

   protected boolean a(Object other) {
      return other instanceof KalamaHelperHelperH;
   }

   public Hand d() {
      return this.c;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      BlockHitResult var3 = this.b();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      ActionResult var4 = this.c();
      var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      Hand var5 = this.d();
      return var2 * 59 + (var5 == null ? 43 : var5.hashCode());
   }
}
