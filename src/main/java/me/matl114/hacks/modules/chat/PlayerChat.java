package me.matl114.hacks.modules.chat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.ListRef;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.ChatUtils$TextBuilder;
import me.matl114.versioned.api.VRecord;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class PlayerChat extends BaseModule {
   public final FlagRef appendChatHead;
   private volatile boolean kN;
   public final FlagRef detectAllMessageWithPlayerNames;
   public final ModulePath sC = makePath(Configs.h, "player-chat");
   public final FlagRef appendTimeStamp;
   private UUID sI;
   public final ListRef sE = this.builder(this.sC.add("game-message-as-player-message"), ListRef.TYPE)
      .defaultValue(
         List.of(
            "^.*\\[([^\\]\\[\\s]+)\\]\\s*[:➟→»》]\\s*(.*)$",
            "^.*\\[[^\\]\\[]+\\].* ([^\\]\\[\\s]+)\\s*[:➟→»》]\\s*(.*)$",
            "^.*<([^><\\s]+)>\\s*[:➟→»》]\\s*(.*)$",
            "^.*《([^》《\\s]+)》\\s*[:➟→»》]\\s*(.*)$",
            "^.*«([^»«\\s]+)»\\s+(.*)$"
         )
      )
      .listValidator(Configs.a)
      .updateListener(list -> this.sD = list.stream().map(Pattern::compile).toList())
      .build();
   public List<Pattern> sD;
   public final Pattern pattern;
   private volatile boolean sJ;

   public boolean hasAnyFunctionEnable() {
      return this.appendChatHead.get() || this.appendTimeStamp.get();
   }

   public Consumer<ChatUtils$TextBuilder> Cj(MutableBoolean shouldModify, String capturedName) {
      if (!this.appendTimeStamp.get() || capturedName == null && this.sI == null) {
         return null;
      } else {
         shouldModify.setTrue();
         String var3 = new SimpleDateFormat("[HH:mm:ss]").format(new Date());
         return builder -> builder.withFormat(Formatting.GRAY).with(var3).withStyle(Style.EMPTY);
      }
   }

   public PlayerChat() {
      super("PlayerChat");
      this.detectAllMessageWithPlayerNames = this.flagBuilder(this.sC.add("detect-all-message-with-player-names")).build();
      this.appendTimeStamp = this.flagBuilder(this.sC.add("append-time-stamp")).build();
      this.appendChatHead = this.flagBuilder(this.sC.add("append-chat-head")).build();
      this.sJ = false;
      this.kN = false;
      this.pattern = Pattern.compile("^(?!_)(?![0-9]+$)[a-zA-Z0-9_]{3,16}$");
   }

   public Consumer<ChatUtils$TextBuilder> Ci(String playerName, MutableBoolean mutableBoolean) {
      return null;
   }

   public void onChatAdd(Event<Text> chatAdd) {
      if (!chatAdd.d() && !this.kN && this.sJ && this.hasAnyFunctionEnable()) {
         this.kN = true;

         try {
            MessageIndicator var2 = chatAdd.getArgs(1);
            String var3 = ChatUtils.l((Text)chatAdd.e());
            Matcher var4 = this.matcher(var3);
            if (var4 != null && var4.groupCount() >= 2) {
               this.handleParsedChatMessage(chatAdd, var3, var4.group(var4.groupCount() - 1), var2 == this.systemIndicator());
            } else {
               String var5 = null;
               if (this.sI != null) {
                  PlayerListEntry var6 = mc.getNetworkHandler().getPlayerListEntry(this.sI);
                  if (var6 != null) {
                     var5 = VRecord.getName(var6.getProfile());
                  }
               }

               if (var5 == null && this.detectAllMessageWithPlayerNames.get()) {
                  String var17 = var3;

                  for (PlayerListEntry var8 : mc.getNetworkHandler().getPlayerList()) {
                     String var9 = VRecord.getName(var8.getProfile());
                     int var10 = var17.indexOf(var9);
                     if (var10 != -1) {
                        var17 = var17.substring(0, var10);
                        var5 = var9;
                     }

                     Text var11 = var8.getDisplayName();
                     if (var11 != null) {
                        String var12 = ChatUtils.l(var11);
                        int var13 = var17.indexOf(var12);
                        if (var13 != -1) {
                           var17 = var17.substring(0, var13);
                           var5 = VRecord.getName(var8.getProfile());
                        }
                     }

                     if (var17.isEmpty()) {
                        break;
                     }
                  }
               }

               this.handleParsedChatMessage(chatAdd, var3, var5, var2 == this.systemIndicator());
            }
         } finally {
            this.kN = false;
         }
      }
   }

   public void handleParsedChatMessage(Event<Text> event, String message, @Nullable String capturedName, boolean isSystem) {
      Text var5 = (Text)event.e();
      MutableBoolean var6 = new MutableBoolean(false);
      ArrayList<Consumer> var7 = new ArrayList();
      var7.add(this.Ci(capturedName, var6));
      var7.add(this.Cj(var6, capturedName));
      if (var6.booleanValue()) {
         ChatUtils$TextBuilder var8 = ChatUtils.builder();

         for (Consumer var10 : var7) {
            if (var10 != null) {
               var10.accept(var8);
            }
         }

         var8.withStyle(Style.EMPTY);
         var5.visit((style, asString) -> {
            var8.accept(style, asString);
            return Optional.empty();
         }, Style.EMPTY);
         event.context(var8.end().build());
      }
   }

   public void onPacketIn(Event<? extends Packet<?>> packetEvent) {
      this.sJ = true;
      if (packetEvent.e() instanceof ChatMessageS2CPacket var3) {
         this.sI = var3.sender();
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      java.util.function.Consumer<Event<Text>> onChatAddH = this::onChatAdd;
      this.registerListener(Listener.aa(), onChatAddH);
      java.util.function.Consumer<Event<ChatMessageS2CPacket>> onPacketInH1 = this::onPacketIn;
      this.registerListener(Listener.aq().getChannel(ChatMessageS2CPacket.class), onPacketInH1);
      java.util.function.Consumer<Event<GameMessageS2CPacket>> onPacketInH2 = this::onPacketIn;
      this.registerListener(Listener.aq().getChannel(GameMessageS2CPacket.class), onPacketInH2);
      java.util.function.Consumer<Event<ChatMessageS2CPacket>> cfH1 = this::Cf;
      this.registerListener(Listener.ar().getChannel(ChatMessageS2CPacket.class), cfH1);
      java.util.function.Consumer<Event<GameMessageS2CPacket>> cfH2 = this::Cf;
      this.registerListener(Listener.ar().getChannel(GameMessageS2CPacket.class), cfH2);
   }

   public void Cf(Event<? extends Packet<?>> packetEvent) {
      this.sJ = false;
      this.sI = null;
   }

   public Matcher matcher(String message) {
      if (this.sD != null) {
         for (Pattern var3 : this.sD) {
            Matcher var4 = var3.matcher(message);
            if (var4.matches() && var4.groupCount() >= 1) {
               return var4;
            }
         }
      }

      return null;
   }

   public MessageIndicator systemIndicator() {
      return mc.isConnectedToLocalServer() ? MessageIndicator.singlePlayer() : MessageIndicator.system();
   }
}
