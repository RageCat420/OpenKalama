package me.matl114.hacks.modules.ac;

import java.util.Objects;
import me.matl114.accessors.access.PlayerMoveC2SPacketAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class PacketOrderManager extends BaseModule {
   public boolean bM;
   public boolean bN;
   public static PacketOrderManager INSTANCE;
   public boolean bJ;
   public boolean bP;
   public boolean bI;
   public boolean bO;
   boolean bQ;
   public boolean bK;
   public boolean bG;
   public boolean bH;
   public boolean bL;

   public void fr(Event<PlayerInteractBlockC2SPacket> event) {
      this.bN = true;
   }

   public void onEntityAction(Event<ClientCommandC2SPacket> event) {
      switch (((ClientCommandC2SPacket)event.b).getMode()) {
         case START_SPRINTING:
         case STOP_SPRINTING:
            if (!mc.player.hasVehicle()) {
               this.bM = true;
            }
            break;
         case START_FALL_FLYING:
            this.bP = true;
      }
   }

   public void fv(Event<PlayerMoveC2SPacket> event) {
      this.bQ = true;
      PlayerMoveC2SPacketAccess.Cause var2 = PlayerMoveC2SPacketAccess.of((PlayerMoveC2SPacket)event.b).getCause();
      if (var2 != PlayerMoveC2SPacketAccess.Cause.SET_BACK && var2 != PlayerMoveC2SPacketAccess.Cause.LEGACY_SNAP) {
         this.fu();
      }
   }

   public PacketOrderManager() {
      super("PacketOrderManager");
      INSTANCE = this;
   }

   public void onPlayerAction(Event<PlayerActionC2SPacket> event) {
      switch (((PlayerActionC2SPacket)event.b).getAction()) {
         case SWAP_ITEM_WITH_OFFHAND:
            this.bG = true;
            break;
         case DROP_ITEM:
         case DROP_ALL_ITEMS:
            this.bH = true;
            break;
         case RELEASE_USE_ITEM:
            this.bK = true;
            break;
         case STOP_DESTROY_BLOCK:
         case ABORT_DESTROY_BLOCK:
         case START_DESTROY_BLOCK:
            this.bL = true;
      }
   }

   public void fu() {
      this.bG = false;
      this.bH = false;
      this.bJ = false;
      this.bI = false;
      this.bK = false;
      this.bL = false;
      this.bN = false;
      this.bO = false;
      this.bM = false;
      this.bP = false;
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.ap().getChannel(PlayerInteractEntityC2SPacket.class), this::onInteract, Integer.MAX_VALUE);
      this.registerListener(Listener.ap().getChannel(PlayerInteractBlockC2SPacket.class), this::fr, Integer.MAX_VALUE);
      this.registerListener(Listener.ap().getChannel(PlayerActionC2SPacket.class), this::onPlayerAction, Integer.MAX_VALUE);
      this.registerListener(Listener.ap().getChannel(ClientCommandC2SPacket.class), this::onEntityAction, Integer.MAX_VALUE);
      this.registerListener(Listener.ap().getChannel(PlayerMoveC2SPacket.class), this::fv, Integer.MAX_VALUE);
   }

   public void onInteract(Event<PlayerInteractEntityC2SPacket> event) {
      String var2 = ((Enum<?>)((PlayerInteractEntityC2SPacket)event.b).type.getType()).name();
      if (Objects.equals(var2, "ATTACK")) {
         this.bJ = true;
      } else {
         this.bI = true;
      }
   }
}
