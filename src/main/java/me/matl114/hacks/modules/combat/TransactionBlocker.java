package me.matl114.hacks.modules.combat;

import me.matl114.events.Event;
import me.matl114.events.KalamaHelperHelperG;
import me.matl114.events.Listener;
import me.matl114.events.PacketManager;
import me.matl114.events.packets.PacketStorage;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.Debug;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;

public class TransactionBlocker extends BaseModule {
   public final KeyBindRef J;
   public final FlagRef bwTest1;
   public final ModulePath pe;
   public final ModulePath pd = makePath(Configs.k, "lag-utils");
   public final FlagRef enable;
   int rideId;

   public TransactionBlocker() {
      super("TransactionBlocker");
      this.pe = this.pd.add("transaction-blocker");
      this.enable = this.flagBuilder(this.pe.add("enable")).build();
      this.J = this.toggleHotkey(this.pe.addHotkey(), new MultiKeyBind(), this.pe.addEnable()).build();
      this.bwTest1 = this.flagBuilder(this.pe.add("bw-test-1")).build();
      this.bindFlag(this.enable);
   }

   public void xz(Event<PlayerRespawnS2CPacket> event) {
      if (this.bwTest1.get()) {
         this.enable.set(true);
      }
   }

   public void xu() {
      PacketManager.n(packet -> this.isTransactionRelated(packet.packetType()) ? KalamaHelperHelperG.NT : KalamaHelperHelperG.NV);
   }

   @Override
   public void onDisableModule() {
      super.onDisableModule();
      this.xu();
   }

   public void onPlayerRespawnLook(Event<PlayerPositionLookS2CPacket> event) {
      if (this.bwTest1.get() && mc.player != null && mc.player.getAbilities().flying) {
         Debug.b("Start");
         this.enable.set(true);
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(PacketManager.z().c(NetworkSide.SERVERBOUND), this::xx);
      this.registerListener(Listener.ap().getChannel(PlayerPositionLookS2CPacket.class), this::onPlayerRespawnLook);
      this.registerListener(Listener.ap().getChannel(EntityPassengersSetS2CPacket.class), this::onDismount);
   }

   public void onDismount(Event<EntityPassengersSetS2CPacket> event) {
      EntityPassengersSetS2CPacket var2 = (EntityPassengersSetS2CPacket)event.b;

      for (int var6 : var2.getPassengerIds()) {
         if (var6 == mc.player.getId()) {
            this.rideId = ((EntityPassengersSetS2CPacket)event.b).getEntityId();
            return;
         }
      }

      if (this.rideId == ((EntityPassengersSetS2CPacket)event.b).getEntityId()) {
         this.enable.set(true);
      }
   }

   public boolean xv(Packet<?> packet) {
      return packet instanceof CommonPongC2SPacket || packet instanceof CommonPingS2CPacket;
   }

   public boolean isTransactionRelated(PacketType<?> packet) {
      return packet == CommonPackets.PING || packet == CommonPackets.PONG;
   }

   public void xx(Event<PacketStorage> packetEvent) {
      if (this.enable.get() && this.isTransactionRelated(((PacketStorage)packetEvent.b).packetType())) {
         packetEvent.cancel();
         Listener.sendPacketNoEvents(new CommonPongC2SPacket(0));
      }
   }

   @Override
   public void onEnableModule() {
      super.onEnableModule();
   }
}
