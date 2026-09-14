package me.matl114.hacks.modules.combat;

import java.util.Objects;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.entity.EntityMovementStatus;
import me.matl114.managers.Configs;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.entity.PlayerInputUtils;
import me.matl114.utils.entity.PlayerInputUtils$Input;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityPose;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class PearlFly extends BaseModule {
   public final FlagRef enableStand;
   public final FlagRef autoCrawl;
   public final FlagRef enableJumpUp;
   public final FlagRef rY;
   public final FlagRef useWasdControl;
   public final ModulePath rX;
   public final DoubleRef autoActivateRange;
   public final FlagRef offhand;
   public final KeyBindRef J;
   public final ModulePath ea = makePath(Configs.k, "combat-utils");
   public final ModulePath rW = this.ea.add("pearl-fly");
   public final FlagRef enableCrawl;

   public boolean doPearlUse(BlockPos originPos, BlockPos pos) {
      EntityPose var3 = mc.player.getPose();
      Vec3d var4;
      if (var3 == EntityPose.SWIMMING) {
         var4 = pos.toCenterPos().subtract(mc.player.getEyePos());
      } else {
         var4 = pos.toCenterPos().add(originPos.toCenterPos()).multiply(0.5).subtract(mc.player.getEyePos());
      }

      return this.usePearl(var4);
   }

   public boolean usePearl(Vec3d look) {
      look = look.normalize();
      KalamaHelperHelperK var2 = InventoryUtils.p(ss -> ss.getItem() == Items.ENDER_PEARL, true, false);
      if (var2 == null) {
         this.logI18NSub("Pearl", "message.module.pearl-fly.no-pearl", new Object[0]);
         return true;
      } else {
         boolean var3 = this.offhand.get() || var2.index() == 40;
         Runnable var4 = var3 ? InvExtra.INSTANCE.uh(var2.index()) : InvExtra.INSTANCE.swapInventoryIndexToHand(var2.index());
         if (var4 == null) {
            return false;
         } else {
            EntityMovementStatus var5 = new EntityMovementStatus(mc.player);
            PlayerStateManager.setPlayerRotationSafe(mc.player, look);
            Hand var6 = var3 ? Hand.OFF_HAND : Hand.MAIN_HAND;
            mc.interactionManager.interactItem(mc.player, var6);
            var5.d();
            var4.run();
            return true;
         }
      }
   }

   public void onInputEvent(Event<Void> event) {
      if (!checkNull()) {
         if (this.rY.get()) {
            EntityPose var2 = mc.player.getPose();
            if (var2 == EntityPose.SWIMMING) {
               if (!this.enableCrawl.get()) {
                  return;
               }
            } else if (!this.enableStand.get()) {
               return;
            }

            if (mc.player.getItemCooldownManager().isCoolingDown(Items.ENDER_PEARL)) {
               return;
            }

            Direction var3 = mc.player.getHorizontalFacing();
            Vec3d var4 = mc.player.getBlockPos().toCenterPos();
            Vec3d var5 = mc.player.getPos();
            BlockPos var6 = mc.player.getBlockPos();
            if (this.useWasdControl.get()) {
               PlayerInputUtils$Input var7 = PlayerInputUtils.of(mc.options);
               Direction var8 = var3.rotateYCounterclockwise();
               BlockPos var9 = var6.add(var3.getVector().multiply(var7.ro())).add(var8.getVector().multiply(var7.rp()));
               if (this.enableJumpUp.get() && var7.rq() > 0) {
                  var9 = var9.offset(Direction.UP, var7.rq());
               }

               if (!Objects.equals(var6, var9) && this.doPearlUse(var6, var9)) {
                  this.rY.set(false);
                  return;
               }
            } else {
               if (MathUtils.o(var4, var5, 0.5 - this.autoActivateRange.get())) {
                  return;
               }

               Direction var16 = var3;
               Direction var17 = var3;
               double var10 = Double.MAX_VALUE;

               do {
                  BlockPos var12 = var6.offset(var16);
                  BlockState var13 = mc.world.getBlockState(var12);
                  if (MathUtils.o(var5, var12.toCenterPos(), 0.5 + this.autoActivateRange.get())) {
                     double var14 = var12.getSquaredDistance(var5);
                     if (var14 < var10) {
                        var17 = var16;
                        var10 = var14;
                     }
                  }

                  var16 = var16.rotateYClockwise();
               } while (var16 != var3);

               if (var10 == Double.MAX_VALUE) {
                  return;
               }

               BlockPos var18 = var6.offset(var17, 1);
               BlockState var19 = mc.world.getBlockState(var18);
               if (!var19.isAir() && !var19.isLiquid() && this.doPearlUse(var6, var18)) {
                  this.rY.set(false);
                  return;
               }

               if (this.autoCrawl.get() && var2 != EntityPose.SWIMMING) {
                  BlockState var20 = mc.world.getBlockState(var18.offset(Direction.UP));
                  if (!var20.isAir() && !var20.isLiquid() && this.doPearlUse(var6, var18)) {
                     this.rY.set(false);
                     return;
                  }
               }
            }
         }
      }
   }

   public PearlFly() {
      super("PearlFly");
      this.rX = this.ea.add("pearl-phase");
      this.rY = this.flagBuilder(this.rX.addEnable()).build();
      this.J = this.toggleHotkey(this.rX.addHotkey(), new MultiKeyBind(), this.rX.addEnable()).build();
      this.enableCrawl = this.flagBuilder(this.rX.add("enable-crawl")).build();
      this.enableStand = this.flagBuilder(this.rX.add("enable-stand")).build();
      this.autoCrawl = this.flagBuilder(this.rX.add("auto-crawl")).build();
      this.autoActivateRange = this.doubleBuilder(this.rX.add("auto-activate-range")).defaultValue(0.35).build();
      this.useWasdControl = this.flagBuilder(this.rX.add("use-wasd-control")).build();
      this.enableJumpUp = this.builder(this.rX.add("enable-jump-up"), Boolean.class).defaultValue(true).build();
      this.offhand = this.flagBuilder(this.rW.add("offhand")).build();
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.bd(), this::onInputEvent);
   }
}
