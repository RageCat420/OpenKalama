package me.matl114.hacks.modules.interact;

import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.utils.InteractUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.versioned.api.VItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class InteractSubHelperR implements InteractSubHelperT {
    int releaseTicks;
    InteractSubHelperC hand;

    @Override
    public String a() {
        return "holduse";
    }

    public InteractSubHelperR(final InteractManager this$0, final InteractSubHelperC hand, final int releaseTicks) {
        this.hand = hand;
        this.releaseTicks = releaseTicks;
    }

    @Override
    public InteractSubHelperC c() {
        return this.hand;
    }

    @Override
    public void execute(InteractManager manager, PlayerEntity player) {
        KalamaHelperHelperK var3 = this.hand.Uv();
        if (var3 != null) {
            boolean var4 = InteractManager.INSTANCE.offhandHoldUse.get() || var3.index() == 40;
            Runnable var5 = var4
                    ? InvExtra.INSTANCE.uh(var3.index())
                    : InvExtra.INSTANCE.swapInventoryIndexToHand(var3.index());
            if (var5 != null) {
                Hand var6 = var4 ? Hand.OFF_HAND : Hand.MAIN_HAND;
                ActionResult var7 =
                        MinecraftClient.getInstance().interactionManager.interactItem(player, var6);
                if (InteractManager.INSTANCE.logAction.get()) {
                    MutableText var8 = VItem.w().l((ItemStack) var3.val());
                    manager.logI18NSub("Interact", "message.module.interact-manager.interact.use", new Object[] {var8});
                }

                manager.holdUseTick = this.releaseTicks;
                if (InteractManager.abr()) {
                    InteractUtils.swingHandIfSuccess(var7, var6);
                }

                InteractManager.INSTANCE.Lt = var5;
            }
        } else if (InteractManager.INSTANCE.logAction.get()) {
            manager.logI18NSub(
                    "Interact", "message.module.interact-manager.interact.no-item", new Object[] {this.hand.toString()
                    });
        }
    }
}
