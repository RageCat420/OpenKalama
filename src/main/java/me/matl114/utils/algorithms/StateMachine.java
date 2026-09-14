package me.matl114.utils.algorithms;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import me.matl114.utils.collections.KalamaHelperHelperK;

public class StateMachine {
   final KalamaHelperHelperA[] actions;
   final KalamaHelperHelperB e;
   boolean c = false;
   final List<KalamaHelperHelperK<KalamaHelperHelperE>> f = new ArrayList<>();
   int a;
   boolean g = false;
   final int b;

   public void c(int state) {
      if (this.g) {
         throw new IllegalStateException("Set during state running");
      } else {
         this.d(state);
      }
   }

   public void e() {
      this.c = true;
   }

   public int getState() {
      return this.a;
   }

   public StateMachine(int initializeState, KalamaHelperHelperB stateUpdater, KalamaHelperHelperA... actions) {
      Preconditions.checkArgument(initializeState >= 0 && initializeState < actions.length);
      this.b = initializeState;
      this.a = initializeState;
      this.e = stateUpdater;
      this.actions = actions;
   }

   private void d(int state) {
      int var2 = this.a;
      this.a = state;
      this.callUpdate(var2, state);
   }

   public void f() {
      this.g = true;

      try {
         this.c = false;
         this.d(this.e.update(this, this.a));

         for (int var1 = 0; var1 < this.actions.length && !this.c; var1++) {
            KalamaHelperHelperA var2 = this.actions[this.a];
            this.d(var2.step(this));
         }
      } finally {
         this.g = false;
      }
   }

   public void registerListener(int state, KalamaHelperHelperE listener) {
      this.f.add(new KalamaHelperHelperK<>(state, listener));
   }

   private void callUpdate(int from, int to) {
      if (from != to) {
         for (KalamaHelperHelperK var4 : this.f) {
            if (var4.index() == from) {
               ((KalamaHelperHelperE)var4.val()).onUpdate(false);
            }

            if (var4.index() == to) {
               ((KalamaHelperHelperE)var4.val()).onUpdate(true);
            }
         }
      }
   }
}
