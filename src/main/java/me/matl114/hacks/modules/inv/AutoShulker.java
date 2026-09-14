package me.matl114.hacks.modules.inv;

import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.ACTasks;
import me.matl114.hacks.InteractionTasks;
import me.matl114.hacks.InvTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.RaycastUtils;
import me.matl114.utils.ScreenUtils;
import me.matl114.utils.entity.PlayerInputUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public class AutoShulker extends BaseModule {
   public final FlagRef s0tickSteal;
   public final ModulePath wP = makePath(Configs.l, "auto-inv");
   public final KeyBindRef toggleKey;
   public final ModulePath yj = this.wP.add("auto-shulker");
   public final FlagRef yk = this.flagBuilder(this.yj.add("enable")).build();

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.ao().getChannel(PlayerInteractBlockC2SPacket.class), this::onClickShulkerBoxOrPlaceShulkerBox);
   }

   public void onClickShulkerBoxOrPlaceShulkerBox(Event<PlayerInteractBlockC2SPacket> event) {
      if (!event.d()) {
         if (this.yk.get()) {
            PlayerInteractBlockC2SPacket var2 = (PlayerInteractBlockC2SPacket)event.b;
            BlockHitResult var3 = var2.getBlockHitResult();
            boolean var4 = mc.player.isSneaking();
            BlockState var5 = mc.world.getBlockState(var3.getBlockPos());
            if (var5.getBlock() instanceof ShulkerBoxBlock && !var4 && mc.world.getBlockEntity(var3.getBlockPos()) instanceof ShulkerBoxBlockEntity var7) {
               int var11 = InvTasks.predictOpenVanillaContainerSize(var3.getBlockPos());
               if (var11 > 0) {
                  if (this.s0tickSteal.get()) {
                     InvTasks.executePredictInventoryAction(var7, handler -> {
                        for (int var2x = 0; var2x < var11; var2x++) {
                           mc.interactionManager.clickSlot(handler.syncId, var2x, 0, SlotActionType.QUICK_MOVE, mc.player);
                        }
                     });
                     int var12 = Tasks.b();
                     ScreenUtils.getOpenScreenFuture().thenRunAsync(() -> {
                        if (var12 + 4 > Tasks.b()) {
                           mc.player.closeHandledScreen();
                        }
                     }, mc);
                  } else {
                     int var13 = Tasks.b();
                     ScreenUtils.getOpenScreenFuture().thenRun(() -> {
                        if (mc.player.currentScreenHandler != mc.player.playerScreenHandler) {
                           if (var13 + 4 <= Tasks.b()) {
                              return;
                           }

                           for (int var2x = 0; var2x < var11; var2x++) {
                              mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, var2x, 0, SlotActionType.QUICK_MOVE, mc.player);
                           }
                        }

                        mc.player.closeHandledScreen();
                     });
                  }
               }
            } else {
               if (var4) {
                  mc.player.setSneaking(false);
                  PlayerInputUtils.a(mc.player).rC(false).sendPlayerSneakUpdatePacket();
                  ClientPlayerAccess.of(mc.player).resyncSneak();
               }

               BlockPos var10 = var3.getBlockPos().offset(var3.getSide());
               BlockState var8 = mc.world.getBlockState(var10);
               if (var8.getBlock() instanceof ShulkerBoxBlock) {
                  BlockHitResult var9 = RaycastUtils.f(var10);
                  ACTasks.c(ch -> InteractionTasks.b(Hand.MAIN_HAND, var9, true));
               }
            }
         }
      }
   }

   public AutoShulker() {
      super("AutoShulker");
      this.toggleKey = this.toggleHotkey(this.yj.add("toggle-key"), new MultiKeyBind(), this.yj.add("enable")).build();
      this.s0tickSteal = this.flagBuilder(this.yj.add("0tick-steal")).build();
      this.bindFlag(this.yk);
   }
}
