package me.matl114.hacks.modules.task;

import java.util.List;
import java.util.function.Consumer;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.ChatTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.EnumPrimitiveList;
import me.matl114.hacks.utils.config.NBTTypes;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.config.NBTType;
import me.matl114.managers.input.MultiKeyBind;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.world.World;

public class EventCommand extends BaseModule {
   public final NBTRef<EnumPrimitiveList<EventCommand$EventType, String>> eventMap;
   public static EventCommand INSTANCE;
   public final KeyBindRef J;
   public final ModulePath ze = makePath(Configs.r, "event-command");
   public final FlagRef ae = this.flagBuilder(this.ze.addEnable()).build();

   public void lh(Event<World> event) {
      if (this.ae.get()) {
         this.Nf(EventCommand$EventType.WORLD_CHANGE, this::Ng);
      }
   }

   private void Ng(String s) {
      ChatTasks.sayMessage(s, false);
   }

   public EventCommand() {
      super("EventCommand");
      this.J = this.moduleEntry(this.ze.addHotkey(), new MultiKeyBind(), this.ze.addEnable()).build();
      this.eventMap = this.builder(this.ze.add("event-map"), NBTType.parameter(EnumPrimitiveList.class))
         .defaultValue(new EnumPrimitiveList<>(EventCommand$EventType.class, NBTTypes.g, List.of()))
         .build();
      this.bindFlag(this.ae);
      INSTANCE = this;
   }

   public void A(Event<PlayerRespawnS2CPacket> eventRespawn) {
      if (this.ae.get()) {
         PlayerRespawnS2CPacket var2 = (PlayerRespawnS2CPacket)eventRespawn.e();
         if (var2.flag() == 0 || var2.flag() == 1) {
            this.Nf(EventCommand$EventType.RESPAWN, this::Ng);
         }
      }
   }

   public void Jr(Event<ClientPlayerEntity> event) {
      if (this.ae.get()) {
         this.Nf(EventCommand$EventType.JOIN_GAME, this::Ng);
      }
   }

   public void Nf(EventCommand$EventType type, Consumer<String> commandSender) {
      this.eventMap.get().forEach((eventType, s) -> {
         if (eventType == type) {
            commandSender.accept(s);
         }
      });
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.ar().getChannel(PlayerRespawnS2CPacket.class), this::A);
      this.registerListener(Listener.N(), this::lh);
      this.registerListener(Listener.M(), this::Jr);
      this.registerListener(Listener.ar().getChannel(EntityStatusS2CPacket.class), this::Nh);
   }

   public void Nh(Event<EntityStatusS2CPacket> event) {
      if (!checkNull()) {
         if (this.ae.get() && ((EntityStatusS2CPacket)event.b).getStatus() == 35 && ((EntityStatusS2CPacket)event.b).getEntity(mc.world) == mc.player) {
            this.Nf(EventCommand$EventType.TRIGGER_TOTEM, this::Ng);
         }
      }
   }
}
