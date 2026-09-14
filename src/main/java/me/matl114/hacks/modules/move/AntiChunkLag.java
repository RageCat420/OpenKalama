package me.matl114.hacks.modules.move;

import me.matl114.events.Event;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.entity.HackUtilHelperD;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.MathUtils;
import me.matl114.utils.WorldUtils;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction.Axis;

public class AntiChunkLag extends BaseModule implements HackUtilHelperJ {
   public final FlagRef ae;
   public final IntRef predictVelocity;
   public final FlagRef logToPlayer;
   public static AntiChunkLag INSTANCE;
   public final KeyBindRef J;
   public final FlagRef freezeWhenLag;
   public static HackUtilHelperD cy;
   public final ModulePath aD = makePath(Configs.m, "move-safety.anti-chunk-lag");
   public boolean currentMayFaceLagChunk;

   public void applyBeforeMovementPacketModify(Event<LegalMovementManager> movementManagerEvent) {
      int var2 = 1 + this.predictVelocity.get() / 16;
      boolean var3 = false;
      ChunkPos var4 = mc.player.getChunkPos();
      Vec3d var5 = mc.player.getPos().withAxis(Axis.Y, 0.0);
      double var6 = MathUtils.b(this.predictVelocity.get());

      label92:
      for (int var8 = -var2; var8 <= var2; var8++) {
         for (int var9 = -var2; var9 <= var2; var9++) {
            ChunkPos var10 = new ChunkPos(var8 + var4.x, var9 + var4.z);
            if (!WorldUtils.isChunkLoaded(var10.x, var10.z)) {
               Vec3d var11 = new Vec3d(var10.getStartX(), 0.0, var10.getStartZ());
               Box var12 = new Box(var11, var11.add(16.0, 0.0, 16.0));
               double var13 = Math.max(MathUtils.getBoxDistance(var5.x, var12.minX, var12.maxX), MathUtils.getBoxDistance(var5.z, var12.minZ, var12.maxZ));
               if (var13 < var6) {
                  var3 = true;
                  break label92;
               }
            }
         }
      }

      if (var3 && !this.currentMayFaceLagChunk) {
         this.currentMayFaceLagChunk = true;
         if (this.ae.get() && this.logToPlayer.get()) {
            this.logI18N("message.module.anti-chunk-lag.facing-lag", new Object[0]);
         }
      } else if (!var3 && this.currentMayFaceLagChunk) {
         this.currentMayFaceLagChunk = false;
      }

      if (this.currentMayFaceLagChunk && this.ae.get() && this.freezeWhenLag.get()) {
         Vec3d var24 = mc.player.getPos();
         Vec3d var25 = ((LegalMovementManager)movementManagerEvent.b).c.g;
         Vec3d var26 = var24.subtract(var25);
         int var27 = (int)MathUtils.j(var26.x);
         int var15 = (int)MathUtils.j(var26.z);
         if (var15 != 0 || var27 != 0) {
            boolean var16 = false;

            label66:
            for (int var17 = 0; var17 <= var2; var17++) {
               for (int var18 = 0; var18 <= var2; var18++) {
                  ChunkPos var19 = new ChunkPos(var17 * var27 + var4.x, var18 * var15 + var4.z);
                  if (!WorldUtils.isChunkLoaded(var19.x, var19.z)) {
                     Vec3d var20 = new Vec3d(var19.getStartX(), 0.0, var19.getStartZ());
                     Box var21 = new Box(var20, var20.add(16.0, 0.0, 16.0));
                     double var22 = Math.max(MathUtils.getBoxDistance(var5.x, var21.minX, var21.maxX), MathUtils.getBoxDistance(var5.z, var21.minZ, var21.maxZ));
                     if (var22 < var6) {
                        var16 = true;
                        break label66;
                     }
                  }
               }
            }

            if (var16) {
               ((LegalMovementManager)movementManagerEvent.b).c.restorePos();
               FloatingUtils.INSTANCE.SB(true);
               if (mc.player.isFallFlying()) {
                  if (ElytraExtra.INSTANCE.afr()) {
                     if (ElytraExtra.INSTANCE.agb()) {
                        FloatingUtils.INSTANCE.SD(false);
                     } else {
                        FloatingUtils.INSTANCE.SD(true);
                     }
                  } else {
                     FloatingUtils.INSTANCE.SD(false);
                  }
               } else {
                  FloatingUtils.INSTANCE.SD(true);
               }
            }
         }
      }
   }

   @Override
   public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
      return true;
   }

   @Override
   public void iB(Event<LegalMovementManager> movementManagerEvent) {
      this.applyBeforeMovementPacketModify(movementManagerEvent);
   }

   public AntiChunkLag() {
      super("AntiChunkLag");
      this.ae = this.flagBuilder(this.aD.addEnable()).build();
      this.J = this.toggleHotkey(this.aD.addHotkey(), new MultiKeyBind(), this.aD.addEnable()).build();
      this.predictVelocity = this.intBuilder(this.aD.add("predict-velocity")).defaultValue(48).validator(Configs.e).build();
      this.freezeWhenLag = this.flagBuilder(this.aD.add("freeze-when-lag")).build();
      this.logToPlayer = this.flagBuilder(this.aD.add("log-to-player")).build();
      INSTANCE = this;
      if (cy == null) {
         cy = new HackUtilHelperD(this::cast);
         MovTasks.j.SJ(() -> cy);
      }

      cy.mN(this::cast);
      this.bindFlag(this.ae);
   }

   @Override
   public void bb(Event<LegalMovementManager> movementManagerEvent) {
   }
}
