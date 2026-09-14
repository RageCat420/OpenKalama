package me.matl114.hacks.modules.interact;

import me.matl114.hacks.CombatTasks;
import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.commands.params.types.EntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public record InteractSubHelperOX(InteractSubHelperC hand, EntitySelector entity) implements InteractSubHelperT {

   public EntitySelector wZ() {
      return this.entity;
   }

   @Override
   public InteractSubHelperC c() {
      return this.hand;
   }

   @Override
   public String a() {
      return "attack";
   }

   @Override
   public void execute(InteractManager manager, PlayerEntity player) {
      Entity var3 = this.entity.amM(PlayerStateManager.oq());
      if (var3 != null) {
         KalamaHelperHelperK var4 = this.hand.Uv();
         if (var4 != null) {
            Runnable var5 = InvExtra.INSTANCE.swapInventoryIndexToHand(var4.index());
            if (var5 != null) {
               CombatTasks.n().Yh(var3);
               if (InteractManager.INSTANCE.logAction.get()) {
                  Text var6 = EntityUtils.getEntityDisplayable(var3);
                  manager.logI18NSub("Interact", "message.module.interact-manager.interact.attack", new Object[]{var6});
               }

               var5.run();
            }
         } else if (InteractManager.INSTANCE.logAction.get()) {
            manager.logI18NSub("Interact", "message.module.interact-manager.interact.no-item", new Object[]{this.hand.toString()});
         }
      }
   }
}
