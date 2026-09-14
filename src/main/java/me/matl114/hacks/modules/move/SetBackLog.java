package me.matl114.hacks.modules.move;

import java.util.List;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.StringFormat;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.Debug;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;

public class SetBackLog extends BaseModule {
   public final FlagRef checkSetbackPackets;
   public final NBTRef<StringFormat> logAcFormat;
   public final NBTRef<StringFormat> logResyncFormat;
   public int maxTpId;
   public final ModulePath gQ = makePath(Configs.m, "move-safety");
   public Vec3d ri;
   public final FlagRef rd = this.flagBuilder(this.gQ.add("log-resync-packets")).build();

   public void onSetBack(Event<PlayerPositionLookS2CPacket> event) {
      PlayerPositionLookS2CPacket var2 = (PlayerPositionLookS2CPacket)event.b;
      int var3 = var2.getTeleportId();
      this.maxTpId = Math.max(this.maxTpId, var3);
      if (mc.player != null) {
         this.ri = mc.player.getPos();
      }

      if (this.rd.get()) {
         StringFormat var4 = this.logResyncFormat.get();
         Debug.b(var4.formatText(ChatUtils.v(var2.getX(), var2.getY(), var2.getZ())));
      }

      if (this.checkSetbackPackets.get() && var3 < 0 && mc.player != null) {
         StringFormat var7 = this.logAcFormat.get();

         try {
            Debug.b(var7.formatText(var3, ChatUtils.v(var2.getX(), var2.getY(), var2.getZ())));
         } catch (Throwable var6) {
            Debug.b(ChatUtils.textFromLegacyString("&cInvalid format string: " + var6.getMessage()));
         }
      }
   }

   public SetBackLog() {
      super("SetBackLog");
      this.logResyncFormat = this.builder(this.gQ.add("log-resync-format"), StringFormat.class)
         .defaultValue(new StringFormat(List.of("position"), "&fPos Resync {position}", true))
         .build();
      this.checkSetbackPackets = this.flagBuilder(this.gQ.add("check-setback-packets")).build();
      this.logAcFormat = this.builder(this.gQ.add("log-ac-format"), StringFormat.class)
         .defaultValue(new StringFormat(List.of("teleportId", "position"), "&c[AC] 反作弊回弹! tp号:{teleportId}, 位置: {position}", true))
         .build();
      this.maxTpId = -1;
      this.ri = Vec3d.ZERO;
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.e(PlayerPositionLookS2CPacket.class), this::onSetBack);
   }
}
