package me.matl114.hacks.modules.render;

import java.util.List;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.StringFormat;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.utils.Debug;
import me.matl114.versioned.api.VRecord;
import net.minecraft.client.network.PlayerListEntry;

public class PlayerLog extends BaseModule {
   public final NBTRef<StringFormat> logPlayerInFormat;
   public final ModulePath Ca = makePath(Configs.i, "player-io");
   public final NBTRef<StringFormat> logPlayerOutFormat;
   public final FlagRef ae = this.flagBuilder(this.Ca.add("log-player-io")).build();

   public PlayerLog() {
      super("PlayerLog");
      this.logPlayerInFormat = this.builder(this.Ca.add("log-player-in-format"), StringFormat.class)
         .defaultValue(new StringFormat(List.of("name"), "&7&l[&a&l+&7&l] &f{name}", true))
         .build();
      this.logPlayerOutFormat = this.builder(this.Ca.add("log-player-out-format"), StringFormat.class)
         .defaultValue(new StringFormat(List.of("name"), "&7&l[&c&l-&7&l] &f{name}", true))
         .build();
      this.bindFlag(this.ae);
   }

   public void onPlayerJoin(Event<PlayerListEntry> entry) {
      if (this.ae.get()) {
         Debug.b(this.logPlayerOutFormat.get().formatText(VRecord.getName(((PlayerListEntry)entry.e()).getProfile())));
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.aK(), this::Qn);
      this.registerListener(Listener.aL(), this::onPlayerJoin);
   }

   public void Qn(Event<PlayerListEntry> entry) {
      if (this.ae.get()) {
         Debug.b(this.logPlayerInFormat.get().formatText(VRecord.getName(((PlayerListEntry)entry.e()).getProfile())));
      }
   }
}
