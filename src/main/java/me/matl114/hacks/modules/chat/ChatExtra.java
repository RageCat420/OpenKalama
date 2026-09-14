package me.matl114.hacks.modules.chat;

import com.google.common.hash.Hashing;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.matl114.accessors.access.ChatScreenAccess;
import me.matl114.commands.MainCommand;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.hacks.utils.config.StringFormat;
import me.matl114.hooks.BaritoneHooks;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.config.StringRef;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.ScreenUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus.Experimental;

public class ChatExtra extends BaseModule {
   public final IntRef passwordEncryptLength;
   public final FlagRef checkChatLen;
   public final ModulePath ew;
   public final FlagRef enableTabFix;
   public final NBTRef<Regex> chatMessageEscapeFormat;
   public final IntRef chatLenLimit;
   public final FlagRef enableChatMessageFormat;
   public final FlagRef checkCommandLen;
   public final IntRef chatHistoryLen;
   public final StringRef passwordEncryptSalt;
   public final IntRef commandLenLimit;
   public final FlagRef passwordEncrypt;
   public final FlagRef ignoreChatLenLimit;
   private final NBTRef<Regex> loginCommandPattern;
   public static ChatExtra INSTANCE;
   public final FlagRef dontSendEmptyMessage;
   public final FlagRef overrideChatHistoryLen;
   public final NBTRef<StringFormat> chatMessageFormatStr;
   @Experimental
   public final FlagRef addToHistoryWhenClose;
   private final String eT;
   private TextFieldWidget eO;
   public final FlagRef escapeTrimChat;
   private final Pattern eS;
   public final ModulePath ev = makePath(Configs.h, "chat-helper");
   public final StringRef limitWarnFormat;
   public final FlagRef escapeNormalizeSpaceChat;
   private final Random eU;

   private boolean iM(String command) {
      if (this.checkCommandLen.get() && command.length() > this.commandLenLimit.get()) {
         String var2 = this.limitWarnFormat.get();
         if (var2 != null && !var2.isEmpty()) {
            Debug.b(ChatUtils.textFromLegacyString(var2.formatted(command.length(), this.commandLenLimit.get())));
         }

         return true;
      } else {
         return false;
      }
   }

   public String iS(String string) {
      StringFormat var2 = this.chatMessageFormatStr.get();
      String var3 = var2.formatString();
      Matcher var4 = this.eS.matcher(var3);
      StringBuffer var5 = new StringBuffer();

      while (var4.find()) {
         int var6 = Integer.parseInt(var4.group(1));
         String var7 = this.generateRandomString(var6);
         var4.appendReplacement(var5, Matcher.quoteReplacement(var7));
      }

      var4.appendTail(var5);
      String var8 = var5.toString();
      return var2.withFormatString(var8).format(string);
   }

   public void iI(Event<String> stringEvent) {
      if (this.dontSendEmptyMessage.get()) {
         String var2 = (String)stringEvent.e();
         if (var2.isEmpty() || Objects.equals(var2, "/") || Objects.equals(var2, MainCommand.l) || Objects.equals(var2, "/" + MainCommand.l)) {
            stringEvent.cancel();
         }
      }

      String var3 = (String)stringEvent.e();
      if (var3.startsWith("/")) {
         if (this.iM(var3)) {
            stringEvent.cancel();
            return;
         }
      } else if (this.iN(var3)) {
         stringEvent.cancel();
         return;
      }
   }

   public void onChatScreenClose(Event<ChatScreen> chatScreenSave) {
      ChatScreen var2 = (ChatScreen)chatScreenSave.e();
      if (this.addToHistoryWhenClose.get()) {
         String var3 = ChatScreenAccess.of(var2).getInputWidget().getText();
         if (!var3.isEmpty() && !Objects.equals("/", var3) && mc.inGameHud != null) {
            mc.inGameHud.getChatHud().addToMessageHistory(var3);
         }
      }
   }

   public ChatExtra() {
      super("ChatExtra");
      this.ew = makePath(Configs.h, "chat-screen-tools");
      this.ignoreChatLenLimit = this.flagBuilder(this.ev.add("ignore-chat-len-limit")).build();
      this.escapeTrimChat = this.flagBuilder(this.ev.add("escape-trim-chat")).build();
      this.escapeNormalizeSpaceChat = this.flagBuilder(this.ev.add("escape-normalize-space-chat")).build();
      this.checkChatLen = this.flagBuilder(this.ev.add("check-chat-len")).build();
      this.chatLenLimit = this.intBuilder(this.ev.add("chat-len-limit")).defaultValue(256).build();
      this.checkCommandLen = this.flagBuilder(this.ev.add("check-command-len")).build();
      this.commandLenLimit = this.intBuilder(this.ev.add("command-len-limit")).defaultValue(32760).build();
      this.limitWarnFormat = this.builder(this.ev.add("limit-warn-format"), String.class).defaultValue("&c你的输入内容太长了! %d / %d").build();
      this.overrideChatHistoryLen = this.flagBuilder(this.ev.add("override-chat-history-len")).build();
      this.chatHistoryLen = this.intBuilder(this.ev.add("chat-history-len")).defaultValue(100).validator(Configs.d).build();
      this.addToHistoryWhenClose = this.flagBuilder(this.ev.add("add-to-history-when-close")).build();
      this.dontSendEmptyMessage = this.flagBuilder(this.ev.add("dont-send-empty-message")).build();
      this.enableTabFix = this.flagBuilder(this.ev.add("enable-tab-fix")).build();
      this.loginCommandPattern = this.builder(this.ew.add("login-command-pattern"), Regex.class)
         .defaultValue(new Regex("^(/login|/l|/reg|/register|/changepass|/changepassword) (.+)$"))
         .build();
      this.passwordEncrypt = this.flagBuilder(this.ew.add("password-encrypt")).build();
      this.passwordEncryptLength = this.intBuilder(this.ew.add("password-encrypt-length")).defaultValue(20).build();
      this.passwordEncryptSalt = this.builder(this.ew.add("password-encrypt-salt"), String.class).defaultValue("").build();
      this.enableChatMessageFormat = this.flagBuilder(this.ev.add("enable-chat-message-format")).build();
      this.chatMessageFormatStr = this.builder(this.ev.add("chat-message-format-str"), StringFormat.class)
         .defaultValue(new StringFormat(List.of("message", "random+数字"), "{message}喵 | WurstV91 client | {random6}"))
         .build();
      this.chatMessageEscapeFormat = this.builder(this.ev.add("chat-message-escape-format"), Regex.class).defaultValue(new Regex("^()$")).build();
      this.eS = Pattern.compile("\\{random(\\d+)\\}");
      this.eT = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
      this.eU = new Random();
      INSTANCE = this;
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.Z(), this::iI, 2147483646);
      this.registerListener(Listener.ai().c(ChatScreen.class), this::onChatScreenInitialized);
      this.registerListener(Listener.ai().c(HandledScreen.class), this::onChatScreenInitialized);
      this.registerListener(Listener.ad().c(ChatScreen.class), this::onChatScreenClose);
      this.registerListener(Listener.Z(), this::onChatPasswordEncrypt, -999);
      this.registerListener(Listener.Z(), this::iU, 2147483637);
   }

   private String generateRandomString(int length) {
      StringBuilder var2 = new StringBuilder(length);

      for (int var3 = 0; var3 < length; var3++) {
         var2.append(
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
               .charAt(this.eU.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".length()))
         );
      }

      return var2.toString();
   }

   private boolean iN(String command) {
      if (this.checkChatLen.get() && command.length() > this.chatLenLimit.get()) {
         String var2 = this.limitWarnFormat.get();
         if (var2 != null && !var2.isEmpty()) {
            Debug.b(ChatUtils.textFromLegacyString(var2.formatted(command.length(), this.chatLenLimit.get())));
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void onDisableModule() {
      super.onDisableModule();
   }

   public void iU(Event<String> chatEvent) {
      if (!chatEvent.d()) {
         if (this.enableChatMessageFormat.get()) {
            String var2 = (String)chatEvent.e();
            if (!this.iT(var2)) {
               chatEvent.context(this.iS(var2));
            }
         }
      }
   }

   private String encryptWithPlayerName(String str, String playerName) {
      String var3 = playerName + ":" + this.passwordEncryptSalt.get() + str;
      byte[] var4 = Hashing.sha256().hashString(var3, StandardCharsets.UTF_8).asBytes();
      BigInteger var5 = new BigInteger(1, var4);
      String var6 = var5.toString(36);
      StringBuilder var7 = new StringBuilder();
      int var8 = this.passwordEncryptLength.get();
      if (var6.length() > var8) {
         var7.append(var6, 0, var8);
      } else {
         int var9 = var6.length();
         var7.append(var6);

         while (var9 < var8 / 2 - 2) {
            var9 = 2 * var9 + 1;
            var7.append("@").append(var6);
         }
      }

      return var7.toString();
   }

   public boolean iT(String originString) {
      boolean var2 = false;
      var2 = var2 || this.chatMessageEscapeFormat.get().test(originString);
      var2 = var2 || originString.startsWith("/") || originString.startsWith(".");
      if (BaritoneHooks.getInstance().isEnabled()) {
         var2 = var2 || originString.startsWith(BaritoneHooks.getInstance().getCommandPrefix());
      } else {
         var2 = var2 || originString.startsWith("#");
      }

      return var2;
   }

   public String normalizeSendText(String sent) {
      if (!this.escapeTrimChat.get()) {
         sent = sent.trim();
      }

      if (!this.escapeNormalizeSpaceChat.get()) {
         sent = StringUtils.normalizeSpace(sent);
      }

      if (!this.ignoreChatLenLimit.get()) {
         sent = StringHelper.truncateChat(sent);
      }

      return sent;
   }

   public boolean onChatObfRender(TextFieldWidget widget, DrawContext context, int x, int y, float partialTicks) {
      String var6 = widget.getText();
      Matcher var7 = this.loginCommandPattern.get().pattern().matcher(var6);
      if (var7.find() && var7.groupCount() > 0) {
         String var8 = var7.group(1) + " <password-hidden>";
         if (this.eO == null) {
            this.eO = new TextFieldWidget(mc.textRenderer, 0, 0, 0, 0, Text.empty());
            this.eO.setDrawsBackground(false);
            this.eO.setFocusUnlocked(false);
         }

         TextFieldWidget var9 = this.eO;
         var9.setX(widget.getX());
         var9.setY(widget.getY());
         var9.setWidth(widget.getWidth());
         var9.setHeight(widget.getHeight());
         var9.setText(var8);
         var9.setCursorToEnd(false);
         var9.render(context, x, y, partialTicks);
         return true;
      } else {
         return false;
      }
   }

   public void onChatScreenInitialized(Event<Screen> screenEvent) {
      if (this.ignoreChatLenLimit.get()) {
         if (screenEvent.e() instanceof ChatScreen var3) {
            ChatScreenAccess var6 = ChatScreenAccess.of(var3);
            TextFieldWidget var4 = var6.getInputWidget();
            var4.setMaxLength(32768);
         } else if (screenEvent.e() instanceof AnvilScreen var5 && var5.getFocused() instanceof TextFieldWidget var8) {
            var8.setMaxLength(32768);
         }
      }
   }

   public void onChatPasswordEncrypt(Event<String> commandChat) {
      if (mc.player != null && this.passwordEncrypt.get() && !ScreenUtils.hasCtrlDown()) {
         String var2 = (String)commandChat.e();
         if (this.loginCommandPattern.get().test(var2)) {
            String var3 = mc.player.getNameForScoreboard();
            String[] var4 = var2.split(" ");

            for (int var5 = 1; var5 < var4.length; var5++) {
               if (!var4[var5].startsWith("plain:")) {
                  var4[var5] = this.encryptWithPlayerName(var4[var5], var3);
               } else {
                  var4[var5] = var4[var5].substring("plain:".length());
               }
            }

            var2 = String.join(" ", var4);
            commandChat.context(var2);
         }
      }
   }
}
