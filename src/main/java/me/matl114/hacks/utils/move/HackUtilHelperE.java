package me.matl114.hacks.utils.move;

import me.matl114.hacks.utils.move.goal.IPathGoal;

public class HackUtilHelperE implements HackUtilHelperD {
   boolean canMine;
   IPathGoal currentGoal = null;

   @Override
   public IPathGoal getCurrentGoal() {
      return this.currentGoal;
   }

   @Override
   public void e() {
   }

   @Override
   public void setCanMine(boolean canMine) {
      this.canMine = canMine;
   }

   @Override
   public void d() {
   }

   @Override
   public boolean isPathing() {
      return false;
   }

   @Override
   public void a() {
   }

   @Override
   public void sumitGoal(IPathGoal pos) {
   }
}
