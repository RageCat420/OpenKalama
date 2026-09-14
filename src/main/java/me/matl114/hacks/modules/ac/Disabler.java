package me.matl114.hacks.modules.ac;

import java.util.Objects;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.PacketManager;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.modules.move.LegacySnapRotManager;
import me.matl114.hooks.ViaFabricPlusHooks;
import me.matl114.managers.Configs;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.NetworkUtils;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Disabler extends BaseModule {
   public final FlagRef autoFlushMultiPlaceQueue;
   Direction RJ;
   public static Disabler INSTANCE;
   boolean RM;
   BlockPos RL;
   boolean RI;
   Vec3d RK;
   public final FlagRef grimMultiPlace;
   public final FlagRef autoFlushPlaceBreakQueue;
   public final EnumRef<DisablerManager$SupportAC> currentAc;
   boolean RN;
   public final ModulePath RC = makePath(Configs.j, "disablers");
   public final FlagRef grimSelfCheck;
   public final KeyBindRef J;
   public final FlagRef ae = this.builder(this.RC.addEnable(), Boolean.class).defaultValue(true).build();

   public void aO(Event<Void> event) {
      this.RM = false;
   }

   public void IU(Event<KalamaHelperHelperI<ModulePreset>> event) {
      switch ((ModulePreset)((KalamaHelperHelperI)event.b).b()) {
         case fg:
         case fh:
            this.currentAc.set(DisablerManager$SupportAC.GRIM);
            break;
         case fi:
            this.currentAc.set(DisablerManager$SupportAC.MATRIX);
            break;
         default:
            this.currentAc.set(DisablerManager$SupportAC.NONE);
      }
   }

   public boolean ajA() {
      return this.autoFlushMultiPlaceQueue.get() ? this.flushACPlaceQueue0() : false;
   }

   public void onPingPong(Event<CommonPongC2SPacket> eventTransaction) {
      int var2 = ((CommonPongC2SPacket)eventTransaction.b).getParameter();
      if (var2 == (short)var2) {
         this.RN = false;
      }
   }

   public boolean ajw() {
      if (this.ae.get()) {
         return switch ((DisablerManager$SupportAC)this.currentAc.get()) {
            case GRIM -> this.ajz();
            case MATRIX -> false;
            default -> true;
         };
      } else {
         return false;
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.O(), this::z);
      this.registerListener(Listener.ap().getChannel(PlayerRespawnS2CPacket.class), this::A);
      this.registerListener(Listener.bx().c(ModulePreset.class), this::IU);
      this.registerListener(Listener.T(), this::aO);
      this.registerListener(Listener.ap().getChannel(PlayerInteractBlockC2SPacket.class), this::onPlace, 2147483646);
      this.registerListener(Listener.ap().getChannel(PlayerActionC2SPacket.class), this::onBreakAction, 2147483646);
      this.registerListener(Listener.ap().getChannel(PlayerMoveC2SPacket.class), this::ajF);
      this.registerListener(Listener.ap().getChannel(CommonPongC2SPacket.class), this::onPingPong);
   }

   public Disabler() {
      super("Disabler");
      this.J = this.moduleEntry(this.RC.addHotkey(), new MultiKeyBind(), this.RC.addEnable(), moduleMeta(() -> this.currentAc)).build();
      this.currentAc = this.builder(this.RC.add("current-ac"), DisablerManager$SupportAC.class).defaultValue(DisablerManager$SupportAC.NONE).build();
      this.grimSelfCheck = this.builder(this.RC.add("grim-self-check"), Boolean.class).defaultValue(false).build();
      this.grimMultiPlace = this.builder(this.RC.add("grim-multi-place"), Boolean.class).defaultValue(true).build();
      this.autoFlushMultiPlaceQueue = this.builder(this.RC.add("auto-flush-multi-place-queue"), Boolean.class).defaultValue(true).build();
      this.autoFlushPlaceBreakQueue = this.builder(this.RC.add("auto-flush-place-break-queue"), Boolean.class).defaultValue(true).build();
      this.RN = false;
      INSTANCE = this;
      this.bindFlag(this.ae);
   }

   public void onPlace(Event<PlayerInteractBlockC2SPacket> blockPlace) {
      if (!blockPlace.d()) {
         BlockHitResult var2 = ((PlayerInteractBlockC2SPacket)blockPlace.b).getBlockHitResult();
         Direction var3 = var2.getSide();
         Vec3d var4 = var2.getPos();
         BlockPos var5 = var2.getBlockPos();
         if (this.ae.get() && this.RN && this.autoFlushMultiPlaceQueue.get()) {
            this.flushACPlaceQueue0();
         }

         this.RN = true;
         if (this.RI) {
            this.RM = false;
         }

         if (this.RM
            && this.ae.get()
            && this.currentAc.get() == DisablerManager$SupportAC.GRIM
            && this.grimMultiPlace.get()
            && (var3 != this.RJ || !Objects.equals(var4, this.RK) || !Objects.equals(var5, this.RL))) {
            PlayerInteractBlockC2SPacket var6 = (PlayerInteractBlockC2SPacket)blockPlace.b;
            PacketManager.b(
               var6,
               () -> Listener.sendPacketNoEvents(
                  new PlayerInteractBlockC2SPacket(var6.getHand(), var6.getBlockHitResult(), NetworkUtils.generateNextSequence())
               )
            );
         }

         this.RJ = var3;
         this.RK = var4;
         this.RL = var5;
      }
   }

   public void A(Event<PlayerRespawnS2CPacket> respawn) {
      if (!this.RI) {
         this.RI = true;
      }
   }

   private boolean flushACPlaceQueue0() {
      switch ((DisablerManager$SupportAC)this.currentAc.get()) {
         case GRIM:
            if (this.RN) {
               if (ViaFabricPlusHooks.isSupportDupRot()) {
                  LegacySnapRotManager.INSTANCE.snapAt(mc.player.getPitch(), mc.player.getYaw(), true);
               } else {
                  int var1 = InventoryUtils.getSelectedSlot();
                  int var2 = var1 == 8 ? 7 : 8;
                  Listener.sendPacketNoEvents(new UpdateSelectedSlotC2SPacket(var2));
                  Listener.sendPacketNoEvents(new UpdateSelectedSlotC2SPacket(var1));
               }
            }

            this.RN = false;
            return true;
         default:
            return false;
      }
   }

   public void ajF(Event<PlayerMoveC2SPacket> playerMoveC2SPacket) {
      this.RN = false;
   }

   public boolean ajC() {
      return this.autoFlushPlaceBreakQueue.get() ? this.flushACPlaceQueue0() : false;
   }

   public boolean ajv() {
      return this.ae.get() && this.currentAc.get() == DisablerManager$SupportAC.GRIM && this.grimSelfCheck.get() && this.RI;
   }

   public boolean ajy() {
      if (this.ae.get()) {
         return switch ((DisablerManager$SupportAC)this.currentAc.get()) {
            case GRIM -> this.ajv();
            case MATRIX -> false;
            default -> true;
         };
      } else {
         return false;
      }
   }

   public void onBreakAction(Event<PlayerActionC2SPacket> eventBreak) {
      if (!eventBreak.d()) {
         switch (((PlayerActionC2SPacket)eventBreak.b).getAction()) {
            case START_DESTROY_BLOCK:
            case STOP_DESTROY_BLOCK:
               if (this.ae.get() && this.RN && this.autoFlushPlaceBreakQueue.get()) {
                  this.flushACPlaceQueue0();
               }

               this.RN = false;
               return;
         }
      }
   }

   public boolean isMultiRotPlaceCheckDisabled(boolean methodCanMultiRot) {
      return this.ajw() && (methodCanMultiRot || this.ajy());
   }

   public boolean ajz() {
      return this.ae.get() && this.currentAc.get() == DisablerManager$SupportAC.GRIM && this.grimMultiPlace.get();
   }

   public void z(Event<Void> eventDisconnect) {
      this.RI = false;
   }
}
