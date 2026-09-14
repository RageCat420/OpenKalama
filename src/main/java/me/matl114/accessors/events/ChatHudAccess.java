package me.matl114.accessors.events;

import java.util.ArrayList;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine.Visible;

public interface ChatHudAccess {
   void setUniqueMessageId(String var1);

   ArrayList<Visible> getVisibleLines();

   void clearUniqueMessages(String var1);

   static ChatHudAccess of(ChatHud chatHud) {
      return (ChatHudAccess)chatHud;
   }
}
