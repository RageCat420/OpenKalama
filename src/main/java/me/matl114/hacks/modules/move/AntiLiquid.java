package me.matl114.hacks.modules.move;

import java.util.List;
import me.matl114.events.Event;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.entity.HackUtilHelperD;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.MathUtils;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class AntiLiquid extends BaseModule implements HackUtilHelperJ {
   public final FlagRef enableNotFly;
   public final IntRef autoArmorFlySwitchGt;
   static HackUtilHelperD instance;
   int gZ;
   boolean currentArmorGlidingSaveState;
   public final FlagRef autoArmorFlyControl;
   public final DoubleRef leaveWaterExpandCheck;
   public final DoubleRef waterExpandCheck;
   public final FlagRef enableFly;
   public final KeyBindRef J;
   int ha;
   public final ModulePath gQ = makePath(Configs.m, "move-safety");
   public final EnumRef<AntiLiquid$Mode> mode;
   public final FlagRef ae;
   public final ModulePath gR = this.gQ.add("anti-liquid");

   @Override
   public void registerAll() {
      super.registerAll();
   }

   public void applyBeforeMovementPacketModify(Event<LegalMovementManager> movementManagerEvent) {
      if (this.currentArmorGlidingSaveState) {
         if (!ElytraExtra.INSTANCE.PD.isEmpty()) {
            if (Tasks.b() < this.autoArmorFlySwitchGt.get() + this.gZ) {
               movementManagerEvent.cancel();
               ((LegalMovementManager)movementManagerEvent.b).b();
               FloatingUtils.INSTANCE.SB(true);
               return;
            }

            ElytraExtra.INSTANCE.PD.clear();
         } else {
            this.gZ = 0;
         }
      }

      if (this.ae.get() && this.mode.get() != AntiLiquid$Mode.NONE) {
         Box var2 = mc.player.getBoundingBox().expand(this.waterExpandCheck.get(), this.waterExpandCheck.get(), this.waterExpandCheck.get());

         for (BlockPos var5 : MathUtils.getOccupiedBlockPositions(var2)) {
            BlockState var6 = mc.world.getBlockState(var5);
            Fluid var7 = var6.getFluidState().getFluid();
            boolean var8 = var7 == Fluids.WATER || var7 == Fluids.FLOWING_WATER;
            boolean var9 = var7 == Fluids.LAVA || var7 == Fluids.FLOWING_LAVA;
            AntiLiquid$Mode var10 = this.mode.get();
            boolean var11 = var10.isAntiLava() && var9;
            boolean var12 = var10.isAntiWater() && var8;
            if (var11 || var12) {
               this.handleMayFlyIntoFluid(movementManagerEvent, var11, var12);
               return;
            }
         }

         this.handleOutOfWater();
      }
   }

   public AntiLiquid() {
      super("AntiLiquid");
      this.ae = this.flagBuilder(this.gR.addEnable()).build();
      this.J = this.moduleEntry(this.gR.addHotkey(), new MultiKeyBind(), this.gR.addEnable(), moduleMeta(() -> this.mode)).build();
      this.mode = this.builder(this.gR.add("mode"), AntiLiquid$Mode.class).defaultValue(AntiLiquid$Mode.NONE).build();
      this.waterExpandCheck = this.doubleBuilder(this.gR.add("water-expand-check")).defaultValue(0.2).build();
      this.enableNotFly = this.flagBuilder(this.gR.add("enable-not-fly")).build();
      this.enableFly = this.flagBuilder(this.gR.add("enable-fly")).build();
      this.autoArmorFlyControl = this.flagBuilder(this.gR.add("auto-armor-fly-control")).build();
      this.autoArmorFlySwitchGt = this.intBuilder(this.gR.add("auto-armor-fly-switch-gt")).defaultValue(5).build();
      this.leaveWaterExpandCheck = this.doubleBuilder(this.gR.add("leave-water-expand-check")).defaultValue(2.0).build();
      this.gZ = 0;
      this.ha = 0;
      this.bindFlag(this.ae);
      if (instance == null) {
         instance = new HackUtilHelperD(this::cast);
         MovTasks.j.SJ(() -> instance);
      }

      instance.mN(this::cast);
   }

   private void handleOutOfWater() {
      if (this.currentArmorGlidingSaveState && Tasks.b() > this.ha + this.autoArmorFlySwitchGt.get()) {
         Box var1 = mc.player.getBoundingBox().expand(this.leaveWaterExpandCheck.get(), this.leaveWaterExpandCheck.get(), this.leaveWaterExpandCheck.get());
         List<BlockPos> var2 = MathUtils.getOccupiedBlockPositions(var1);
         boolean var3 = false;

         for (BlockPos var5 : var2) {
            BlockState var6 = mc.world.getBlockState(var5);
            Fluid var7 = var6.getFluidState().getFluid();
            boolean var8 = var7 == Fluids.WATER || var7 == Fluids.FLOWING_WATER;
            if (var8) {
               var3 = true;
               break;
            }
         }

         if (!var3) {
            this.currentArmorGlidingSaveState = false;
            if (mc.player.isFallFlying() && ElytraExtra.INSTANCE.enable2.get() && !ElytraExtra.INSTANCE.afr()) {
               ElytraExtra.INSTANCE.afY(-1);
            }
         }
      }
   }

   private void handleMayFlyIntoFluid(Event<LegalMovementManager> eventMove, boolean isLava, boolean isWater) {
      boolean var4 = !PlayerStateManager.INSTANCE.jz;
      boolean var5 = !PlayerStateManager.INSTANCE.jy;
      if (isWater && var4 || isLava && var5) {
         if (mc.player.isFallFlying()) {
            if (PlayerStateManager.INSTANCE.jO > 20
               && !MovTasks.az().enable.get()
               && isWater
               && this.autoArmorFlyControl.get()
               && ElytraExtra.INSTANCE.enable2.get()
               && ElytraExtra.INSTANCE.afr()) {
               eventMove.cancel();
               ((LegalMovementManager)eventMove.b).c.restorePos();
               ElytraExtra.INSTANCE.afW(true);
               this.currentArmorGlidingSaveState = true;
               this.ha = Tasks.b();
               if (!ElytraExtra.INSTANCE.PD.isEmpty()) {
                  this.gZ = Tasks.b();
               }

               FloatingUtils.INSTANCE.SB(true);
               return;
            }

            if (this.enableFly.get()) {
               eventMove.cancel();
               ((LegalMovementManager)eventMove.b).b();
               FloatingUtils.INSTANCE.SB(true);
            }
         } else if (this.enableNotFly.get()) {
            eventMove.cancel();
            ((LegalMovementManager)eventMove.b).b();
            FloatingUtils.INSTANCE.SB(true);
         }
      }
   }
}
