package me.matl114.accessors.events;

import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.ChatHudLine.Visible;

public interface ChatHudLineAccess {
   void setUniqueMessageId(String var1);

   String getUniqueMessageId();

   static ChatHudLineAccess of(ChatHudLine chatHudLine) {
      return (ChatHudLineAccess)(Object)chatHudLine;
   }

   static ChatHudLineAccess of(Visible uniqueMessageId) {
      return (ChatHudLineAccess)(Object)uniqueMessageId;
   }
}
