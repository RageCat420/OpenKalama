package me.matl114.hacks.modules.render;

import me.matl114.accessors.access.ChatScreenAccess;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.elements.LabelElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;

class RenderSubHelperT extends ChatScreen implements RenderSubHelperZX {
   Text i;

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (keyCode != 257 && keyCode != 335) {
         return super.keyPressed(keyCode, scanCode, modifiers);
      } else {
         this.sendMessage(this.chatField.getText(), true);
         this.chatField.setText("");
         ChatScreenAccess.of(this).resetMessageHistoryIndex();
         return true;
      }
   }

   public void close() {
   }

   protected void init() {
      super.init();
      this.j.Ri = this;
      DisplayWidget.instance(this.width - 80, 0, 80, 40).<DrawableWidget>setRenderHandler(LabelElement.instance(this.i)).addTo(this);
      this.j.Rk = true;
   }

   public RenderSubHelperT(final SleepMode param1, String originalChatText, Text displayMessage) {
      super(originalChatText);
      this.j = var1;
      this.i = displayMessage;
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      super.render(context, mouseX, mouseY, delta);
      this.j.Rk = true;
   }
   SleepMode j;
}
