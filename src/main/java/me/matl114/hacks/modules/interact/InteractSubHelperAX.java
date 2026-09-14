package me.matl114.hacks.modules.interact;


import net.minecraft.client.MinecraftClient;
import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.utils.InteractUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.versioned.api.VItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec2f;

public record InteractSubHelperAX(InteractSubHelperC hand, InteractSubHelperP target) implements InteractSubHelperT {

   public InteractSubHelperP d() {
      return this.target;
   }

   @Override
   public InteractSubHelperC c() {
      return this.hand;
   }

   @Override
   public String a() {
      return "useitem";
   }

   @Override
   public void execute(InteractManager manager, PlayerEntity player) {
      Vec2f var3 = this.target.a(player);
      if (var3 != null) {
         KalamaHelperHelperK var4 = this.hand.Uv();
         if (var4 != null) {
            boolean var5 = InteractManager.abq() || var4.index() == 40;
            Runnable var6 = var5 ? InvExtra.INSTANCE.uh(var4.index()) : InvExtra.INSTANCE.swapInventoryIndexToHand(var4.index());
            if (var6 != null) {
               Vec2f var7 = new Vec2f(player.getPitch(), player.getYaw());
               player.setPitch(var3.x);
               player.setYaw(var3.y);
               Hand var8 = var5 ? Hand.OFF_HAND : Hand.MAIN_HAND;
               ActionResult var9 = MinecraftClient.getInstance().interactionManager.interactItem(player, var8);
               if (InteractManager.INSTANCE.logAction.get()) {
                  MutableText var10 = VItem.w().l((ItemStack)var4.val());
                  manager.logI18NSub("Interact", "message.module.interact-manager.interact.use", new Object[]{var10});
               }

               if (InteractManager.abr()) {
                  InteractUtils.swingHandIfSuccess(var9, var8);
               }

               player.setPitch(var7.x);
               player.setYaw(var7.y);
               var6.run();
            }
         } else if (InteractManager.INSTANCE.logAction.get()) {
            manager.logI18NSub("Interact", "message.module.interact-manager.interact.no-item", new Object[]{this.hand.toString()});
         }
      }
   }
}
