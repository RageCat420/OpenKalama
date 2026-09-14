package me.matl114.hacks.modules.chat;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import javax.annotation.Nonnull;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.channels.EventChannel;
import me.matl114.gui.KalamaHelperHelperB;
import me.matl114.gui.WidgetUtils;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.complex.config.KalamaHelperHelperD;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.elements.IconElement;
import me.matl114.gui.elements.LabelElement;
import me.matl114.gui.elements.MultiLineTextElement;
import me.matl114.gui.presets.lists.ListEntryWidgetController;
import me.matl114.gui.presets.single.CenterScreen;
import me.matl114.gui.presets.single.ConfirmingWidgetScreen;
import me.matl114.hacks.ChatTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.task.ClickGui;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.managers.Configs;
import me.matl114.managers.FileManager;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.config.StringRef;
import me.matl114.managers.file.FileStorage;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.ChatUtils$TextBuilder;
import me.matl114.utils.Debug;
import me.matl114.utils.ScreenUtils;
import me.matl114.utils.config.ValueAccessor;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;

public class EncryptChat extends BaseModule {
   public final NBTRef<Regex> encryptCommandMessagePattern;
   private volatile boolean kN;
   public final FlagRef decryptMessageIn;
   public final StringRef decryptIgnoreSuffix;
   private final ModulePath kA = makePath(Configs.h, "encrypt-chat");
   private static final EventChannel<String> kB = new EventChannel<>();
   private ChatSubHelperH kM;
   private final FileStorage kI;
   public final FlagRef kC = this.flagBuilder(this.kA.add("encrypt-message-out")).build();
   public static final EncryptChat$AESEncryption kP = new ChatSubHelperG();
   private EncryptChat$Encryptor kL;
   public static final EncryptChat$AESEncryption kO = new ChatSubHelperS();
   public final StringRef encryptPrefix;
   private ChatSubHelperK kJ;
   public static final EncryptChat$AESEncryption kQ = new EncryptChat$AESEncryption("ECB", "PKCS5Padding", false);
   private CharSet kF;
   private boolean kK;

   private void reloadIgnoredSuffix(String value) {
      CharArraySet var2 = new CharArraySet();

      for (int var3 = 0; var3 < value.length(); var3++) {
         var2.add(value.charAt(var3));
      }

      this.kF = var2;
   }

   public static String tryEncrypt(String encrypt, EncryptChat$Encryptor encryptor, int maxLength) {
      while (!encrypt.isEmpty()) {
         String var3 = encryptor.a("#%" + encrypt);
         if (var3.length() <= maxLength) {
            return var3;
         }

         encrypt = encrypt.substring(0, encrypt.length() - 1);
      }

      return "";
   }

   public void qI(ChatSubHelperK keyList) {
      this.kJ = keyList;
      this.kI.f(ChatSubHelperK.CODEC, keyList);
      this.kK = true;
   }

   public String qU() {
      ChatSubHelperH var1 = this.qT();
      if (var1 == null) {
         return "";
      } else if (!var1.i().isEmpty()) {
         return var1.i();
      } else if (!var1.g().isEmpty() && var1.e() != EncryptChat$EncryptAlgorithm.NONE) {
         String var2 = this.qV(var1.g(), var1.e());
         var1.j(var2);
         this.qI(this.kJ);
         return var2;
      } else {
         return "";
      }
   }

   private int trimIgnoredSuffixEnd(String message) {
      int var2;
      for (var2 = message.length() - 1; var2 >= 0; var2--) {
         char var3 = message.charAt(var2);
         if (!Character.isWhitespace(var3) && !this.kF.contains(var3)) {
            break;
         }
      }

      return var2 + 1;
   }

   private List<Pair<String, ValueAccessor<?>>> createChatKeyEntryAccessors(ChatSubHelperH entry) {
      ArrayList var2 = new ArrayList();
      var2.add(Pair.of("widget.encrypt-chat.name", ValueAccessor.of(entry::c, entry::d)));
      var2.add(Pair.of("widget.encrypt-chat.algorithm", ValueAccessor.of(entry::e, algorithm -> entry.f(algorithm))));
      var2.add(Pair.of("widget.encrypt-chat.phase", ValueAccessor.of(entry::g, entry::h)));
      var2.add(Pair.of("widget.encrypt-chat.key", ValueAccessor.of(entry::i, entry::j)));
      return var2;
   }

   private DrawableWidget createEditRenderHandler(ChatSubHelperH entry, MutableObject<ChatSubHelperH> index) {
      KalamaHelperHelperCX var3 = new KalamaHelperHelperCX(0, 0, 220, 20);
      ExecutableWidget.instance(2, 2, 16, 16).<ExecutableWidget>eV(IconElement.co(ButtonElement.bH, ButtonElement.bJ, ButtonAction.a(() -> {
         if (entry == index.getValue()) {
            index.setValue(null);
         } else {
            index.setValue(entry);
         }
      }), bl -> index.getValue() == entry)).addToSub(var3);
      DisplayWidget.instance(45, 0, 100, 20)
         .<DrawableWidget>setRenderHandler(new LabelElement(ClickGui.INSTANCE.guiBackgroundStyle.get().withAlpha(64)))
         .addToSub(var3);
      ExecutableWidget.instance(45, 0, 100, 20)
         .<ExecutableWidget>eV(
            new MultiLineTextElement(
               el -> Text.literal("%s\n(%s)".formatted(entry.c(), entry.e().name())), ClickGui.INSTANCE.guiConfigStyle.get().withAlpha(255), 0
            )
         )
         .addToSub(var3);
      ExecutableWidget.instance(155, 0, 60, 20)
         .<ExecutableWidget>eV(
            new ButtonElement(
               TextProvider.c(Text.translatable("widget.encrypt-chat.open-editor")),
               ButtonAction.a(
                  () -> {
                     DrawableWidget var2 = WidgetUtils.g(
                        Text.translatable("widget.encrypt-chat.open-editor.title"),
                        List::of,
                        this.createChatKeyEntryAccessors(entry),
                        WidgetUtils.a,
                        ClickGui.ky
                     );
                     new CenterScreen(var2).access().openFromCurrent();
                  }
               )
            )
         )
         .addToSub(var3);
      return var3;
   }

   public boolean shouldEncryptSendMessage() {
      return this.kC.get() && !ScreenUtils.hasCtrlDown() && this.qT() != null;
   }

   public String qV(String phrase, EncryptChat$EncryptAlgorithm algorithm) {
      return algorithm.getEncryption().d(phrase);
   }

   public String qR(String message) {
      EncryptChat$Encryptor var2 = this.qS();
      return var2 == EncryptChat$Encryptor.EMPTY ? null : qW(message, var2).orElse(null);
   }

   @Nonnull
   public EncryptChat$Encryptor qS() {
      ChatSubHelperH var1 = this.qT();
      if (var1 != null && var1.e() != EncryptChat$EncryptAlgorithm.NONE) {
         if (this.kK || this.kM == null || this.kM != var1) {
            try {
               String var2 = this.qU();
               if (var2.isEmpty()) {
                  this.kL = EncryptChat$Encryptor.EMPTY;
               } else {
                  this.kL = Objects.requireNonNull(var1.e().getEncryption().c(var2));
               }
            } catch (Throwable var3) {
               Debug.b("[ChatEncrypt] 当前密钥格式不正确, 已跳过聊天加密/解密。");
               Debug.f(var3);
               this.kL = EncryptChat$Encryptor.EMPTY;
            }

            this.kM = var1;
            this.kK = false;
         }

         return Objects.requireNonNull(this.kL);
      } else {
         return EncryptChat$Encryptor.EMPTY;
      }
   }

   private void openKeyListEditScreen() {
      ArrayList var1 = new ArrayList<>(this.kJ.entries().stream().map(ChatSubHelperH::b).toList());
      int var2 = this.kJ.selected();
      MutableObject var3 = new MutableObject(var2 >= 0 && var2 < var1.size() ? (ChatSubHelperH)var1.get(var2) : null);
      ListEntryWidgetController var4 = ListEntryWidgetController.mutable(var1, ChatSubHelperH::a, value -> this.createEditRenderHandler(value, var3), 30, 220);
      KalamaHelperHelperD var5 = new KalamaHelperHelperD(var4, 0, 0, 320, 260);
      ConfirmingWidgetScreen var6 = new ConfirmingWidgetScreen(
         Text.translatable("widget.encrypt-chat.key-list-editor.title"),
         var5,
         this::shouldEncryptSendMessage,
         () -> this.qI(new ChatSubHelperK(var3.getValue() == null ? -1 : var1.indexOf(var3.getValue()), List.copyOf(var1)))
      );
      var6.access().openFromCurrent();
   }

   private Text rebuildMessage(Text origin, ChatSubHelperD result, String replacement) {
      MutableInt var4 = new MutableInt(0);
      ChatUtils$TextBuilder var5 = ChatUtils.builder();
      var5.withStyle(Style.EMPTY);
      origin.visit((style, asString) -> {
         int var5x = asString.length();
         if (var4.intValue() + var5x > result.start()) {
            int var6 = result.start() - var4.intValue();
            if (var6 > 0) {
               String var7 = asString.substring(0, var6);
               var5.accept(style, var7);
               var4.add(var7.length());
            }

            return StringVisitable.TERMINATE_VISIT;
         } else {
            var5.accept(style, asString);
            var4.add(var5x);
            return Optional.empty();
         }
      }, Style.EMPTY);
      var5.withHoverEvent(ChatUtils.G(List.of(Text.literal("当前密文:" + result.suffix()), Text.literal("点击拷贝").formatted(Formatting.YELLOW))))
         .withClickEvent(ChatUtils.getClickCopyText(result.suffix()))
         .with(replacement + result.prefix())
         .withHoverEvent(ChatUtils.G(List.of(Text.literal("当前消息由Kalama解密"))))
         .withClickEvent(null)
         .withBold(true)
         .withColor(Formatting.DARK_PURPLE)
         .with(" [!]")
         .withStyle(Style.EMPTY);
      return var5.end().build();
   }

   public EncryptChat() {
      super("EncryptChat");
      this.decryptMessageIn = this.flagBuilder(this.kA.add("decrypt-message-in")).build();
      this.encryptPrefix = this.builder(this.kA.add("encrypt-prefix"), StringRef.TYPE).defaultValue("").validator(s -> s.isEmpty() || s.endsWith(" ")).build();
      this.kF = new CharArraySet();
      this.decryptIgnoreSuffix = this.builder(this.kA.add("decrypt-ignore-suffix"), StringRef.TYPE)
         .defaultValue("喵")
         .updateListener(this::reloadIgnoredSuffix)
         .build();
      this.encryptCommandMessagePattern = this.builder(this.kA.add("encrypt-command-message-pattern"), Regex.class)
         .defaultValue(new Regex("^/(minecraft:)?(msg|say|me) ([^\\s]+) (.*)$"))
         .build();
      this.kI = FileManager.getInstance().o("encrypt-chat-keys.nbt");
      this.kJ = this.kI.read(ChatSubHelperK.CODEC, () -> new ChatSubHelperK(-1, List.of()));
      this.kK = true;
      this.kL = EncryptChat$Encryptor.EMPTY;
      this.kN = false;
   }

   public void qK(Event<Text> chatAdd) {
      if (!chatAdd.d() && !this.kN && this.decryptMessageIn.get()) {
         this.kN = true;

         try {
            String var2 = ChatUtils.l((Text)chatAdd.e());
            ChatSubHelperD var3 = this.qL(var2);
            if (var3 == null) {
               return;
            }

            Event var4 = new Event<>(var3.cipher(), true, true, var3.prefix(), var3.suffix());
            qY().catchEvent(var4);
            if (!var4.d()) {
               String var5 = Objects.requireNonNullElse((String)var4.e(), "");
               chatAdd.context(this.rebuildMessage((Text)chatAdd.e(), var3, var5));
               return;
            }

            chatAdd.cancel();
         } finally {
            this.kN = false;
         }
      }
   }

   @Override
   public void addCustomWidgets(Consumer<DrawableWidget> acceptor, int dx, int dy, int dblank) {
      acceptor.accept(ExecutableWidget.instance(0, dblank, dx, dy).eV(new ButtonElement(el -> {
         ChatSubHelperH var2 = this.kJ.Ri();
         if (var2 == null) {
            return Text.translatable("widget.encrypt-chat.key-list-editor").append("None");
         } else {
            String var3 = "%s(%s%s)".formatted(var2.c(), var2.e().name(), var2.g().isEmpty() ? "" : "/phrase");
            return Text.translatable("widget.encrypt-chat.key-list-editor").append(var3);
         }
      }, ButtonAction.a(this::openKeyListEditScreen)).aO(TooltipHandler.ap(KalamaHelperHelperB.b()))));
   }

   private boolean isEncryptedMessageChar(char ch) {
      return "!\"#$%¼'(),-.:;<=>?@[\\]^_`{|}~¡¢£¤¥¦¨©ª«¬®¯°±²³µ¶·×¹º0123456789+»¿".indexOf(ch) >= 0;
   }

   private ChatSubHelperD qL(String message) {
      int var2 = this.trimIgnoredSuffixEnd(message);
      if (var2 <= 0) {
         return null;
      } else {
         int var3 = var2;

         while (var3 > 0 && this.isEncryptedMessageChar(message.charAt(var3 - 1))) {
            var3--;
         }

         if (var3 == var2) {
            return null;
         } else {
            String var4 = message.substring(var3, var2);
            String var5 = this.qR(var4);
            return var5 == null ? null : new ChatSubHelperD(var3, var2, message.substring(0, var3), var4, message.substring(var2), var5);
         }
      }
   }

   public static Optional<String> qW(String message, EncryptChat$Encryptor encryptor) {
      try {
         String var2 = encryptor.b(message);
         return var2.startsWith("#%") ? Optional.of(var2.substring(2)) : Optional.empty();
      } catch (Exception var3) {
         return Optional.empty();
      }
   }

   public void qQ(Event<String> event) {
      if (!event.d() && this.shouldEncryptSendMessage()) {
         EncryptChat$Encryptor var2 = this.qS();
         if (var2 != EncryptChat$Encryptor.EMPTY) {
            Matcher var3 = this.encryptCommandMessagePattern.get().pattern().matcher((CharSequence)event.e());
            if (var3.matches() && var3.groupCount() > 0) {
               int var8 = var3.groupCount();
               String var5 = var3.group(var8);
               String var6 = tryEncrypt(var5, var2, 32000);
               StringBuilder var7 = new StringBuilder((String)event.e());
               var7.replace(var3.start(var8), var3.end(var8), var6);
               event.context(var7.toString());
            } else {
               String var4 = (String)event.e();
               if (!ChatTasks.f().iT(var4)) {
                  event.context(this.encryptPrefix.get() + tryEncrypt(var4, var2, 256));
               }
            }
         }
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.aa(), this::qK, -10);
      this.registerListener(Listener.Z(), this::qQ, 2147483645);
      this.reloadIgnoredSuffix(this.decryptIgnoreSuffix.get());
   }

   public static EventChannel<String> qY() {
      return kB;
   }

   public ChatSubHelperH qT() {
      return this.kJ.Ri();
   }
}
