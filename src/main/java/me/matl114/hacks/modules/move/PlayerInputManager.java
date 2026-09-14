package me.matl114.hacks.modules.move;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.matl114.events.Event;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.utils.entity.HackUtilHelperD;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.utils.entity.PlayerInputUtils;
import me.matl114.utils.entity.PlayerInputUtils$Input;

public class PlayerInputManager extends BaseModule implements HackUtilHelperJ {
   public static HackUtilHelperD cy;
   public static PlayerInputManager INSTANCE;
   private final List<MoveSubHelperAg> priorityQueue = new ArrayList<>(16);

   public void applyPreTickModify(Event<LegalMovementManager> movementManagerEvent) {
      HackUtilHelperJ.super.bb(movementManagerEvent);
      if (!this.priorityQueue.isEmpty() && mc.player != null) {
         boolean var2 = false;

         for (MoveSubHelperAg var4 : this.priorityQueue) {
            boolean var5 = var4.ahB(mc.player);
            var2 |= var5;
         }

         if (var2) {
            ((LegalMovementManager)movementManagerEvent.b).c();
         }
      }
   }

   public void ZZ(int priority, boolean sneak, int startTicks, int ticks) {
      this.ZN(MoveSubHelperBX.gI(priority).gO(sneak), startTicks, ticks);
   }

   public void ZW(int priority, boolean jump, int ticks) {
      this.ZX(priority, jump, 0, ticks);
   }

   public void ZT(int priority, boolean left, int startTicks, int ticks) {
      this.ZN(MoveSubHelperBX.gI(priority).gL(left), startTicks, ticks);
   }

   public void ZY(int priority, boolean sneak, int ticks) {
      this.ZZ(priority, sneak, 0, ticks);
   }

   public PlayerInputManager() {
      super("PlayerInputManager");
      INSTANCE = this;
      if (cy == null) {
         cy = new HackUtilHelperD(this::cast);
         MovTasks.j.SJ(() -> cy);
      }

      cy.mN(this::cast);
   }

   public void ZS(int priority, boolean left, int ticks) {
      this.ZT(priority, left, 0, ticks);
   }

   public void ZQ(int priority, boolean backward, int ticks) {
      this.ZR(priority, backward, 0, ticks);
   }

   public void aab(int priority, boolean sprint, int startTicks, int ticks) {
      this.ZN(MoveSubHelperBX.gI(priority).gP(sprint), startTicks, ticks);
   }

   public void ZU(int priority, boolean right, int ticks) {
      this.ZV(priority, right, 0, ticks);
   }

   public void ZR(int priority, boolean backward, int startTicks, int ticks) {
      this.ZN(MoveSubHelperBX.gI(priority).gK(backward), startTicks, ticks);
   }

   public void ZV(int priority, boolean right, int startTicks, int ticks) {
      this.ZN(MoveSubHelperBX.gI(priority).gM(right), startTicks, ticks);
   }

   private void aac(MoveSubHelperAg timedModifier) {
      int var2 = 0;

      while (var2 < this.priorityQueue.size() && this.priorityQueue.get(var2).compareTo(timedModifier) <= 0) {
         var2++;
      }

      this.priorityQueue.add(var2, timedModifier);
   }

   public void addInputModifier(MoveSubHelperBX modifier, int ticks) {
      this.ZN(modifier, 0, ticks);
   }

   public void ZX(int priority, boolean jump, int startTicks, int ticks) {
      this.ZN(MoveSubHelperBX.gI(priority).gN(jump), startTicks, ticks);
   }

   public void applyAfterInputTick(Event<LegalMovementManager> movementManagerEvent) {
      if (!this.priorityQueue.isEmpty() && mc.player != null) {
         PlayerInputUtils$Input var2 = PlayerInputUtils.a(mc.player);
         Iterator var3 = this.priorityQueue.iterator();

         while (var3.hasNext()) {
            MoveSubHelperAg var4 = (MoveSubHelperAg)var3.next();
            if (var4.ahA(var2)) {
               var3.remove();
            }
         }

         var2.applyInput(mc.player);
      }
   }

   public void aaa(int priority, boolean sprint, int ticks) {
      this.aab(priority, sprint, 0, ticks);
   }

   public void ZO(int priority, boolean forward, int ticks) {
      this.ZP(priority, forward, 0, ticks);
   }

   public void ZP(int priority, boolean forward, int startTicks, int ticks) {
      this.ZN(MoveSubHelperBX.gI(priority).gJ(forward), startTicks, ticks);
   }

   public void ZL(MoveSubHelperBX modifier) {
      this.addInputModifier(modifier, 1);
   }

   public void ZN(MoveSubHelperBX modifier, int startTicks, int ticks) {
      if (modifier != null && !modifier.isEmpty() && ticks >= 0) {
         this.aac(new MoveSubHelperAg(startTicks, ticks, modifier));
      }
   }
}
