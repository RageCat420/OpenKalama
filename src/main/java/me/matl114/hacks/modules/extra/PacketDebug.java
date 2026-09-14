package me.matl114.hacks.modules.extra;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import me.matl114.SlimefunHelper;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.StringRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.Debug;
import me.matl114.utils.entity.PlayerInputUtils;
import me.matl114.utils.entity.PlayerInputUtils$Input;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class PacketDebug extends BaseModule {
   public final FlagRef debugPacketOut;
   public final StringRef debugPacketType;
   public final FlagRef interceptPacket;
   public final FlagRef ae;
   public final FlagRef stopDebugInChat;
   private Set<PacketType<?>> AW;
   public final FlagRef debugTime;
   public final StringRef interceptPacketType;
   public final KeyBindRef AX;
   public final ModulePath AU = makePath(Configs.j, "packet-debugger");
   private Set<PacketType<?>> AV = new HashSet<>();
   public final FlagRef debugPacketIn;

   public void onPacket(Event<Packet<?>> packetEvent) {
      if (!packetEvent.d()) {
         if (this.ae.get() && this.interceptPacket.get()) {
            Packet var2 = (Packet)packetEvent.e();
            if (this.AW.contains(var2.getPacketId())) {
               packetEvent.cancel();
            }
         }
      }
   }

   public void debug(Object... val) {
      if (!this.stopDebugInChat.get()) {
         Debug.chat(val);
      } else {
         Debug.e(val);
      }
   }

   public static String simplifyId(Identifier id) {
      return Objects.equals("minecraft", id.getNamespace()) ? "mc:" + id.getPath() : id.toString();
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.aq(), this::onPacketHandle, Integer.MIN_VALUE);
      this.registerListener(Listener.an(), this::onPacketSend, Integer.MIN_VALUE);
      this.registerListener(Listener.ap(), this::onPacket);
   }

   public void onPacketHandle(Event<Packet<?>> packetEvent) {
      if (!packetEvent.d()) {
         if (this.ae.get() && this.debugPacketIn.get()) {
            Packet var2 = (Packet)packetEvent.e();
            if (this.AV.contains(var2.getPacketId())) {
               String var3 = this.debugTime.get() ? ", Tick: " + Tasks.b() : "";
               if (var2 instanceof PlayerPositionLookS2CPacket var4) {
                  Vec3d var5 = new Vec3d(var4.getX(), var4.getY(), var4.getZ());
                  this.debug("Accept", simplifyId(var2.getPacketId().id()), var5.x, var5.y, var5.z, ", Pitch:", var4.getPitch(), ", Yaw:", var4.getYaw(), var3);
               } else {
                  this.debug("Accept", simplifyId(var2.getPacketId().id()), var3);
               }
            }
         }
      }
   }

   public PacketDebug() {
      super("PacketDebug");
      this.AW = new HashSet<>();
      this.ae = this.flagBuilder(this.AU.addEnable()).build();
      this.AX = this.toggleHotkey(this.AU.addHotkey(), new MultiKeyBind(), this.AU.addEnable()).build();
      this.debugPacketIn = this.flagBuilder(this.AU.add("debug-packet-in")).build();
      this.debugPacketOut = this.flagBuilder(this.AU.add("debug-packet-out")).build();
      this.debugPacketType = this.builder(this.AU.add("debug-packet-type"), StringRef.TYPE)
         .defaultValue("^(move_player_.*)$")
         .validator(Configs.a)
         .updateListener(v -> this.AV = this.getDebugTypes(v))
         .build();
      this.debugTime = this.flagBuilder(this.AU.add("debug-time")).build();
      this.stopDebugInChat = this.flagBuilder(this.AU.add("stop-debug-in-chat")).show(() -> SlimefunHelper.DEV_ENV).build();
      this.interceptPacket = this.flagBuilder(this.AU.add("intercept-packet")).build();
      this.interceptPacketType = this.builder(this.AU.add("intercept-packet-type"), StringRef.TYPE)
         .defaultValue("^()$")
         .validator(Configs.a)
         .updateListener(v -> this.AW = this.getDebugTypes(v))
         .build();
      this.bindFlag(this.ae);
   }

   private Set<PacketType<?>> getDebugTypes(String regex) {
      HashSet var2 = new HashSet();

      for (PacketType var4 : Listener.K().keySet()) {
         if (Pattern.matches(regex, var4.id().getPath())) {
            var2.add(var4);
         }
      }

      return var2;
   }

   public void onPacketSend(Event<Packet<?>> packetEvent) {
      if (!packetEvent.d()) {
         if (this.ae.get() && this.debugPacketOut.get()) {
            Packet var2 = (Packet)packetEvent.e();
            if (this.AV.contains(var2.getPacketId())) {
               String var3 = this.debugTime.get() ? ", Tick: " + Tasks.b() : "";
               if (var2 instanceof PlayerMoveC2SPacket var4) {
                  this.debug(
                     "Send",
                     simplifyId(var2.getPacketId().id()),
                     var4.getX(0.0),
                     var4.getY(0.0),
                     var4.getZ(0.0),
                     ", Pitch:",
                     var4.getPitch(0.0F),
                     ", Yaw:",
                     var4.getYaw(0.0F),
                     ", onGround:",
                     var4.isOnGround(),
                     var3
                  );
               } else if (var2 instanceof PlayerInputC2SPacket var5) {
                  PlayerInputUtils$Input var6 = PlayerInputUtils.d(var5);
                  this.debug("Send", simplifyId(var2.getPacketId().id()), var6, var3);
               } else if (var2 instanceof PlayerActionC2SPacket var7) {
                  this.debug("Send", simplifyId(var2.getPacketId().id()), var7.getAction().name(), var7.getPos(), var7.getSequence(), var3);
               } else if (var2 instanceof PlayerInteractEntityC2SPacket var8) {
                  this.debug("Send", simplifyId(var2.getPacketId().id()), ((Enum<?>)var8.type.getType()).name(), var8.entityId, var3);
               } else if (var2 instanceof ClientCommandC2SPacket var9) {
                  this.debug("Send", simplifyId(var2.getPacketId().id()), var9.getMode().name(), var3);
               } else {
                  this.debug("Send", simplifyId(var2.getPacketId().id()), var3);
               }
            }
         }
      }
   }
}
