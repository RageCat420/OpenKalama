package me.matl114.hacks.modules.interact;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.accessors.hacks.PlayerInteractionAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.CombatTasks;
import me.matl114.hacks.InteractionTasks;
import me.matl114.hacks.InvTasks;
import me.matl114.hacks.KalamaHelperHelperCX;
import me.matl114.hacks.KalamaHelperHelperDX;
import me.matl114.hacks.KalamaHelperHelperIX;
import me.matl114.hacks.KalamaHelperHelperV;
import me.matl114.hacks.MineTasks;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.RenderTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ColorUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RenderUtils;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class TpInteract extends BaseModule {
   public final KeyBindRef enableHotkey;
   public final FlagRef mineInteractUseFailMine;
   public final FlagRef enable;
   public final KeyBindRef os;
   public final ModulePath op = makePath(Configs.n, "tp-interact");
   private final float ENABLE_NO_TP_DISTANCE;
   public static TpInteract INSTANCE;

   public boolean wL(Vec3d pos, Packet<?>... packetToSend) {
      return this.executeTp(pos, () -> {
         for (Packet var4 : packetToSend) {
            Listener.sendPacketNoEvents(var4);
         }
      });
   }

   public TpInteract() {
      super("TpInteract");
      this.enable = this.flagBuilder(this.op.add("enable")).build();
      this.enableHotkey = this.toggleHotkey(this.op.add("enable-hotkey"), new MultiKeyBind(), this.op.add("enable")).build();
      this.mineInteractUseFailMine = this.flagBuilder(this.op.add("mine-interact-use-fail-mine")).build();
      this.os = this.hotkey(Configs.n, this.op.add("try-tp-steal-chest-key").toPath()).defaultValue(new MultiKeyBind()).build();
      this.ENABLE_NO_TP_DISTANCE = 1.14F;
      this.bindFlag(this.enable);
      INSTANCE = this;
   }

   public boolean executeTp(Vec3d pos, Runnable callback) {
      Vec3d var3 = mc.player.getPos();
      KalamaHelperHelperIX var4 = MovTasks.createPlayerMovContext();
      List var5 = MovTasks.z(var3, pos, false, 200.0, true);
      List var6 = MovTasks.z(pos, var3, false, 200.0, true);
      if (!var5.isEmpty() && !var6.isEmpty()) {
         if (RenderTasks.j) {
            RenderTasks.i(
               new KalamaHelperHelperCX(
                  RenderTasks.DEBUG_TICK, new KalamaHelperHelperDX(mc.player.dimensions.getBoxAt(pos), ColorUtils.k(Color.MAGENTA, 0.25F))
               )
            );
         }

         ArrayList var7 = new ArrayList();
         var7.addAll(MovTasks.l(var5));
         var7.addAll(MovTasks.l(var6));
         List var8 = MovTasks.createMovingPacketsForMovSequence(var4, var7, false, true);

         for (int var9 = 0; var9 < var5.size(); var9++) {
            ((KalamaHelperHelperV)var8.get(var9)).run();
         }

         callback.run();

         for (int var11 = var5.size(); var11 < var8.size(); var11++) {
            if (!((KalamaHelperHelperV)var8.get(var11)).success) {
               List var10 = var7.subList(var11, var8.size());
               Tasks.l(() -> MovTasks.scheduleFarawayMoveInternal(var10, false, var4.mS(), true), 1);
               break;
            }

            ((KalamaHelperHelperV)var8.get(var11)).run();
         }

         MovTasks.Y();
         ClientPlayerAccess.of(mc.player).setForceNoFall(true);
         return true;
      } else {
         Debug.b("[TpAct] Can not reach the target");
         return false;
      }
   }

   public boolean tpToBlock(BlockPos pos, Predicate<Vec3d> callBack) {
      Vec3d var3 = RenderUtils.getCameraEntityPos();
      double var4 = mc.player.getEyeHeight(mc.player.getPose());
      if (MineTasks.distanceOutOfReach(pos, var3.add(0.0, var4, 0.0)) || MovTasks.h.checkEnvironmentCollision(mc.player, var3, true)) {
         var3 = null;

         for (Vec3i var7 : InteractExtra.INSTANCE.fw()) {
            Vec3d var8 = pos.add(var7).toBottomCenterPos().add(0.0, 1.0E-4, 0.0);
            if (!MineTasks.distanceOutOfReach(pos, var8.add(0.0, var4, 0.0)) && !MovTasks.h.checkEnvironmentCollision(mc.player, var8, true)) {
               var3 = var8;
               break;
            }
         }
      }

      if (var3 == null) {
         this.logI18NSub("TpAct", "message.module.tp-interact.cannot-reach", new Object[0]);
         return false;
      } else {
         return callBack.test(var3);
      }
   }

   public void gA(Event<KalamaHelperHelperI<ModulePreset>> event) {
      switch ((ModulePreset)((KalamaHelperHelperI)event.e()).b()) {
         case fd:
         case fe:
            this.enable.set(true);
            break;
         default:
            this.enable.set(false);
      }
   }

   public boolean tpToEntity(Entity pos, Packet<?> packetToSend) {
      Vec3d var3 = RenderUtils.getCameraEntityPos();
      Box var4 = pos.getBoundingBox();
      double var5 = CombatTasks.j().getAttackRange() + 1.0;
      double var7 = mc.player.getEyeHeight(mc.player.getPose());
      if (var4.squaredMagnitude(var3.add(0.0, var7, 0.0)) > MathUtils.a(var5)) {
         var3 = null;
         BlockPos var9 = pos.getBlockPos();

         for (Vec3i var11 : InteractExtra.INSTANCE.fw()) {
            Vec3d var12 = var9.add(var11).toBottomCenterPos().add(0.0, 1.0E-4, 0.0);
            if (var4.squaredMagnitude(var12.add(0.0, var7, 0.0)) < MathUtils.a(var5) && !MovTasks.h.checkEnvironmentCollision(mc.player, var12, true)) {
               var3 = var12;
               break;
            }
         }
      }

      if (var3 == null) {
         Debug.b("[TpAct] Can not reach the target");
         return false;
      } else {
         return this.wL(var3, packetToSend);
      }
   }

   public void onInteractBlock(Event<PlayerInteractBlockC2SPacket> event) {
      if (!event.d()) {
         if (this.enable.get()) {
            PlayerInteractBlockC2SPacket var2 = (PlayerInteractBlockC2SPacket)event.b;
            BlockHitResult var3 = ((PlayerInteractBlockC2SPacket)event.b).getBlockHitResult();
            BlockPos var4 = var3.getBlockPos();
            double var5 = InteractExtra.INSTANCE.getBlockReachDistance() + 1.14F;
            if (new Box(var4).squaredMagnitude(mc.player.getEyePos()) > MathUtils.a(var5)) {
               if (!mc.player.isSneaking() && this.os.get().d()) {
                  int var7 = InvTasks.predictOpenVanillaContainerSize(var4);
                  if (var7 != 0) {
                     if (this.tpToBlock(var4, sel -> this.executeTp(sel, () -> {
                        Debug.b("[TpInteract] 尝试和物品栏交互");
                        Listener.sendPacketNoEvents(var2);
                        InvTasks.executePredictInventoryAction(InventoryUtils.d(Collections.nCopies(var7, ItemStack.EMPTY)), handler -> {
                           for (int var2x = 0; var2x < var7; var2x++) {
                              mc.interactionManager.clickSlot(handler.syncId, var2x, 0, SlotActionType.QUICK_MOVE, mc.player);
                           }
                        });
                     }))) {
                        event.cancel();
                     }

                     return;
                  }
               }

               if (this.tpToBlock(var4, sel -> this.wL(sel, var2))) {
                  event.cancel();
               }
            }
         }
      }
   }

   public void onBlockMine(Event<PlayerActionC2SPacket> event) {
      if (!event.d()) {
         if (this.enable.get()) {
            PlayerActionC2SPacket var2 = (PlayerActionC2SPacket)event.b;
            switch (var2.getAction()) {
               case START_DESTROY_BLOCK:
               case STOP_DESTROY_BLOCK:
                  BlockPos var3 = var2.getPos();
                  if (var3 == null) {
                     return;
                  }

                  if (var3.getY() < mc.world.getBottomY() - 1 || var3.getY() > mc.world.getBottomY() + mc.world.getHeight() + 1) {
                     return;
                  }

                  var3 = var2.getPos();
                  double var4 = InteractExtra.INSTANCE.getBlockReachDistance() + 1.14F;
                  if (new Box(var3).squaredMagnitude(mc.player.getEyePos()) > MathUtils.a(var4)) {
                     PlayerActionC2SPacket var6 = (PlayerActionC2SPacket)event.e();
                     if (this.tpToBlock(
                        var3,
                        this.mineInteractUseFailMine.get()
                           ? selectedPos -> this.executeTp(
                              selectedPos,
                              () -> {
                                 mc.getNetworkHandler().sendPacket(var6);
                                 PlayerInteractionAccess var1 = PlayerInteractionAccess.of(mc.interactionManager);
                                 if (var6.getAction() == Action.START_DESTROY_BLOCK
                                    && Objects.equals(var1.getCurrentMiningPos(), var6.getPos())
                                    && var1.getCurrentFailBreakPos() == null) {
                                    var1.sendFailBreakCurrentPos(null);
                                 }
                              }
                           )
                           : sel -> this.wL(sel, var6)
                     )) {
                        event.cancel();
                     }
                  }
                  break;
               default:
                  return;
            }
         }
      }
   }

   public boolean tpAndInteractBlock(BlockHitResult hitResult, Hand hand, boolean swing) {
      return this.tpToBlock(hitResult.getBlockPos(), sel -> this.executeTp(sel, () -> InteractionTasks.b(hand, hitResult, swing)));
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.ap().getChannel(PlayerInteractBlockC2SPacket.class), this::onInteractBlock);
      this.registerListener(Listener.ap().getChannel(PlayerInteractEntityC2SPacket.class), this::onInteractEntity);
      this.registerListener(Listener.ap().getChannel(PlayerActionC2SPacket.class), this::onBlockMine);
      this.registerListener(Listener.bx().c(ModulePreset.class), this::gA);
   }

   public void onInteractEntity(Event<PlayerInteractEntityC2SPacket> event) {
      if (!event.d()) {
         if (this.enable.get()) {
            PlayerInteractEntityC2SPacket var2 = (PlayerInteractEntityC2SPacket)event.b;
            int var3 = var2.entityId;
            Entity var4 = mc.world.getEntityById(var3);
            if (var4 != null
               && var4.getBoundingBox().squaredMagnitude(mc.player.getEyePos()) > MathUtils.a(CombatTasks.j().getAttackRange() + 1.14F)
               && this.tpToEntity(var4, var2)) {
               event.cancel();
            }
         }
      }
   }
}
