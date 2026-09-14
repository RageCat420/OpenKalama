package me.matl114.hacks.modules.chat;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.matl114.accessors.events.ChatHudAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.utils.ChatUtils;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.ChatHudLine.Visible;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class ChatCombine extends BaseModule {
   private static final Pattern HS = Pattern.compile("^\\s*?\\[x(\\d*?)\\]$");
   public final ModulePath HQ = makePath(Configs.h, "chat-combine");
   public final FlagRef ae = this.flagBuilder(this.HQ.add("enable")).build();
   private static final String HR = " &r&7&l[x&a%d&7&l]";

   public ChatCombine() {
      super("ChatCombine");
      this.bindFlag(this.ae);
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.ab(), this::onAddVisibleMessage);
   }

   public void onAddVisibleMessage(Event<ChatHudLine> textEvent) {
      if (this.ae.get()) {
         ChatHudLine var2 = (ChatHudLine)textEvent.e();
         Text var3 = var2.content();
         String var4 = ChatUtils.orderedTextToLegacyString(var3.asOrderedText());
         ChatHud var5 = mc.inGameHud.getChatHud();
         int var6 = 0;
         if (var5 != null) {
            ArrayList var7 = ChatHudAccess.of(var5).getVisibleLines();
            ListIterator var8 = var7.listIterator();
            ArrayList var9 = new ArrayList();

            while (var8.hasNext()) {
               Visible var10 = (Visible)var8.next();
               var9.add(0, var10.content());
               String var11 = ChatUtils.orderedTextToLegacyString(var9.toArray(OrderedText[]::new));
               if (var11.length() > var4.length() + 10 + " &r&7&l[x&a%d&7&l]".length()) {
                  break;
               }

               if (var11.startsWith(var4)) {
                  String var12 = var11.substring(var4.length());
                  if (var12.isEmpty()) {
                     var6++;
                     var8.remove();

                     while (var8.hasPrevious()) {
                        var8.previous();
                        var8.remove();
                     }

                     var9.clear();
                  } else {
                     Matcher var13 = HS.matcher(var12);
                     if (var13.find()) {
                        try {
                           var6 += Integer.parseInt(var13.group(1));
                           var8.remove();

                           while (var8.hasPrevious()) {
                              var8.previous();
                              var8.remove();
                           }

                           var9.clear();
                        } catch (Throwable var15) {
                        }
                     }
                  }
               }
            }
         }

         if (var6 > 0) {
            String var16 = " &r&7&l[x&a%d&7&l]".formatted(var6 + 1);
            MutableText var17 = ChatUtils.copyText(var3);
            var17.append(ChatUtils.textFromLegacyString(var16));
            textEvent.context(new ChatHudLine(var2.creationTick(), var17, var2.signature(), var2.indicator()));
         }
      }
   }
}
