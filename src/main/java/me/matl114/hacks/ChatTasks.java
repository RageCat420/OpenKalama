package me.matl114.hacks;

import me.matl114.commands.MainCommand;
import me.matl114.events.annotations.Modifiable;
import me.matl114.hacks.api.ModuleGroup;
import me.matl114.hacks.api.ModuleManager;
import me.matl114.hacks.modules.HackModules;
import me.matl114.hacks.modules.chat.ChatCombine;
import me.matl114.hacks.modules.chat.ChatExtra;
import me.matl114.hacks.modules.chat.ChatSpamFix;
import me.matl114.hacks.modules.chat.ChatTools;
import me.matl114.hacks.modules.chat.ClientSideCommand;
import me.matl114.hacks.modules.chat.EncryptChat;
import me.matl114.hacks.modules.chat.InGuiChatBox;
import me.matl114.hacks.modules.chat.PlayerChat;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.IntRef;
import me.matl114.utils.tasks.LimitedSpeedExecutor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ChatTasks {
   private static ChatSpamFix i;
   private static EncryptChat g;
   private static final LimitedSpeedExecutor k = new LimitedSpeedExecutor(new IntRef(5));
   public static final MinecraftClient j = MinecraftClient.getInstance();
   @Modifiable
   public static final ModuleGroup a = new ModuleGroup("Chat");
   private static ChatCombine e;
   private static InGuiChatBox f;
   private static ClientSideCommand d;
   private static ChatExtra b;
   private static PlayerChat h;
   private static ChatTools c;

   public static LimitedSpeedExecutor n() {
      return k;
   }

   public static ChatTools g() {
      return c;
   }

   public static ChatSpamFix m() {
      return i;
   }

   public static ClientSideCommand h() {
      return d;
   }

   public static ChatExtra f() {
      return b;
   }

   public static ModuleGroup e() {
      return a;
   }

   public static void init() {
   }

   public static EncryptChat k() {
      return g;
   }

   private static void b(ModuleManager m) {
      b = new ChatExtra().register(m);
      c = new ChatTools().register(m);
      d = new ClientSideCommand().register(m);
      e = new ChatCombine().register(m);
      f = new InGuiChatBox().register(m);
      g = new EncryptChat().register(m);
      h = new PlayerChat().register(m);
      i = new ChatSpamFix().register(m);
   }

   public static void d(Text text) {
      k.a(() -> j.inGameHud.getChatHud().addMessage(text));
   }

   public static PlayerChat l() {
      return h;
   }

   public static InGuiChatBox j() {
      return f;
   }

   public static void sayMessage(String chatText, boolean addToHistory) {
      if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().player.networkHandler != null) {
         chatText = f().normalizeSendText(chatText);
         if (addToHistory) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addToMessageHistory(chatText);
         }

         if (chatText.startsWith("/")) {
            MinecraftClient.getInstance().player.networkHandler.sendChatCommand(chatText.substring(1));
         } else {
            MinecraftClient.getInstance().player.networkHandler.sendChatMessage(chatText);
         }
      }
   }

   public static ChatCombine i() {
      return e;
   }

   static {
      a.registerFactories(ChatTasks::b);
      HackModules.registerModuleGroup(a);
      Tasks.e(player -> k.reset());
      MainCommand.aI(KalamaHelperHelperNX::new);
   }
}
