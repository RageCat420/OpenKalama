package me.matl114.hacks.modules.survival;

import java.util.HashSet;
import java.util.Set;
import me.matl114.accessors.hacks.PlayerInteractionAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.annotations.Modifiable;
import me.matl114.hacks.MineTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.interact.InteractExtra;
import me.matl114.hacks.modules.move.LegacySnapRotManager;
import me.matl114.hacks.utils.tasks.TimerExecutor;
import me.matl114.hooks.ViaFabricPlusHooks;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class AntiAXray extends BaseModule {
   public final IntRef packetLimit;
   public final FlagRef rotate;
   TimerExecutor Wc;
   public final ModulePath VY = makePath(Configs.o, "survival-mine-utils.aaxray-simple");
   public final FlagRef legal;
   private static final Set<BlockPos> simpleDetection = new HashSet<>();
   public final FlagRef enable;
   public final KeyBindRef hotkey;
   TimerExecutor Wd;

   public void amQ() {
      simpleDetection.clear();
   }

   public void aO(Event<ClientPlayerEntity> player) {
      if (this.enable.get()) {
         this.Wd.b(1200, this::amQ);
         this.Wc.b(20, this::doSimpleDetection);
      }
   }

   public AntiAXray() {
      super("AntiAXray");
      portConfigs(makePath(Configs.g, "aaxray.simple"), this.VY);
      this.enable = this.flagBuilder(this.VY.add("enable")).defaultValue(false).build();
      this.hotkey = this.toggleHotkey(this.VY.add("hotkey"), new MultiKeyBind(), this.VY.add("enable")).build();
      this.packetLimit = this.builder(this.VY.add("packet-limit"), IntRef.TYPE).defaultValue(30).validator(Configs.e).build();
      this.legal = this.flagBuilder(this.VY.add("legal")).build();
      this.rotate = this.flagBuilder(this.VY.add("rotate")).build();
      this.Wc = new TimerExecutor();
      this.Wd = new TimerExecutor();
      this.bindFlag(this.enable);
   }

   public void y(Event<World> event) {
      this.amQ();
   }

   @Modifiable
   public void doSimpleDetection() {
      BlockPos var1 = mc.player.getSteppingPos().add(0, 1, 0);
      int var2 = 0;

      for (Vec3i var4 : InteractExtra.INSTANCE.fw()) {
         BlockPos var5 = var1.add(var4);
         if (!MineTasks.distanceOutOfReach(var5, mc.player.getEyePos()) && !simpleDetection.contains(var5)) {
            simpleDetection.add(var5);
            if (this.rotate.get() && ViaFabricPlusHooks.isSupportDupRot()) {
               Vec3d var6 = var5.toCenterPos().subtract(mc.player.getEyePos());
               LegacySnapRotManager.INSTANCE.ahs(var6.normalize(), false);
            }

            Vec3d var8 = var5.toCenterPos().subtract(mc.player.getEyePos());
            Direction var7 = Direction.getFacing(var8).getOpposite();
            if (this.legal.get()) {
               mc.interactionManager.sendSequencedPacket(mc.world, sequence -> new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, var5, var7, sequence));
               PlayerInteractionAccess.of(mc.interactionManager).sendAbortBreakPacket();
            } else {
               mc.interactionManager.sendSequencedPacket(mc.world, sequence -> new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, var5, var7, sequence));
            }

            if (++var2 >= this.packetLimit.get()) {
               break;
            }
         }
      }
   }

   @Override
   public void onEnableModule() {
      super.onEnableModule();
      this.amQ();
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.N(), this::y);
      this.registerListener(Listener.U(), this::aO);
   }
}
