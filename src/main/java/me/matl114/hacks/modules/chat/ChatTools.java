package me.matl114.hacks.modules.chat;

import io.github.reserveword.imblocker.common.gui.FocusableObject;
import java.util.List;
import me.matl114.accessors.access.ChatScreenAccess;
import me.matl114.accessors.gui.ScreenAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.gui.McWidgetHelpers;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.elements.IconElement;
import me.matl114.hacks.ChatTasks;
import me.matl114.hacks.MainTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.hooks.IMBlockerHooks;
import me.matl114.managers.Configs;
import me.matl114.managers.KalamaHelperHelperG;
import me.matl114.managers.TaskManagers;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.StringRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.ScreenUtils;
import me.matl114.utils.config.PropertyTracker;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ChatTools extends BaseModule {
   private static final List<Text> Hm = List.of(Text.literal("自动发送缓存聊天框中的东西"), Text.literal("查看配置界面以调整参数"));
   public final FlagRef enableTools;
   private static final List<Text> Hp = List.of(Text.literal("可以将旁边的小输入框中的数字和字符进行ascii转换"));
   private static final Text Hz = Text.literal("-").setStyle(Style.EMPTY.withBold(true));
   public final FlagRef Hf;
   public final IntRef autoChatPeriod;
   private static final Identifier Hj = Identifier.tryParse("kalama:gui/lock_disable");
   private static final Text HA = Text.literal("+").setStyle(Style.EMPTY.withBold(true));
   public final StringRef quickChars;
   private static final List<Text> Hr = List.of(Text.literal("左击切换是否进行聊天格式化"), Text.literal("右击以打开配置文件"));
   public final FlagRef enableQuickChars;
   public final KeyBindRef Hh;
   private String Ht;
   private static final List<Text> Hq = List.of(Text.literal("左击切换是否进行消息加密"), Text.literal("右击以打开配置文件"), Text.literal("按住ctrl发送可以禁用加密"));
   KalamaHelperHelperCX Hu;
   private static final Identifier Hi = Identifier.tryParse("kalama:gui/lock_enable");
   ContentDelegateWidget<KalamaHelperHelperCX> Hv;
   private static final List<Text> Hs = List.of(Text.literal("点击展开/关闭特殊字符快捷键"), Text.literal("可以在配置界面中配置特殊字符列表"));
   public final ModulePath ew = makePath(Configs.h, "chat-screen-tools");
   TextFieldWidget Hy;
   public int counter;
   private static final List<Text> Hk = List.of(Text.literal("点击展开/关闭聊天框小工具栏"));
   private static final List<Text> Hn = List.of(Text.literal("切换是否keepChatInv"), Text.literal("若启用,回车发送文字后将仍保持在聊天界面"));
   TextFieldWidget Hx;
   public final FlagRef Hc;
   public final StringRef cached;
   ContentDelegateWidget<KalamaHelperHelperCX> Hw;
   public final FlagRef obfLoginMessage;
   private static final List<Text> Hl = List.of(Text.literal("发送缓存聊天框中的东西"));
   public final IntRef autoChatMultiple;
   private static final List<Text> Ho = List.of(Text.literal("点击将当前正在输入的输入框中"), Text.literal("输入的字符转为unicode字符"));

   @Override
   public void onDisableModule() {
      super.onDisableModule();
      this.counter = 0;
   }

   private void initToolWidget() {
      short var1 = 250;
      byte var2 = 68;
      KalamaHelperHelperCX var3 = new KalamaHelperHelperCX(0, 0, 250, 68);
      ContentDelegateWidget var4 = McWidgetHelpers.c(0, 48, 120, 20, PropertyTracker.event(this.cached::set), this.cached.get());
      this.Hx = (TextFieldWidget)var4.ef();
      ExecutableWidget.instance(120, 48, 60, 20)
         .<ExecutableWidget>eV(
            new ButtonElement(TextProvider.c(Text.literal("send cache")), ButtonAction.a(() -> ChatTasks.sayMessage(this.cached.get(), true)))
               .aO(TooltipHandler.ap(Hl))
         )
         .addToSub(var3);
      Runnable var5 = HotKeyUtils.h(Configs.h, this.ew.add("auto-chat").toPath());
      ExecutableWidget.instance(180, 48, 50, 20)
         .<ExecutableWidget>eV(
            new ButtonElement(TextProvider.c(Text.literal("auto-send")), ButtonAction.a(var5)).cw(el -> this.Hc.get()).aO(TooltipHandler.ap(Hm))
         )
         .addToSub(var3);
      Runnable var6 = HotKeyUtils.h(Configs.h, this.ew.add("keep-chat-inv").toPath());
      ExecutableWidget.instance(180, 24, 70, 20)
         .<ExecutableWidget>eV(
            new ButtonElement(TextProvider.c(Text.literal("keep-chat-inv")), ButtonAction.a(var6)).cw(el -> this.Hf.get()).aO(TooltipHandler.ap(Hn))
         )
         .addToSub(var3);
      ExecutableWidget.instance(120, 24, 60, 20).<ExecutableWidget>eV(new ButtonElement(TextProvider.c(Text.literal("to-unicode")), ButtonAction.a(() -> {
         TextFieldWidget var1x = this.findCurrentFocusing();
         if (var1x != null) {
            var1x.setText(ChatUtils.d(var1x.getText()));
         }
      })).aO(TooltipHandler.ap(Ho))).addToSub(var3);
      ExecutableWidget.instance(0, 24, 20, 20).<ExecutableWidget>eV(IconElement.co(Hi, Hj, ButtonAction.b(i -> {
         if (i) {
            ChatTasks.k().kC.toggle();
         } else {
            MainTasks.p(ChatTasks.k());
         }
      }), el -> ChatTasks.k().shouldEncryptSendMessage()).aO(TooltipHandler.ap(Hq))).addToSub(var3);
      ExecutableWidget.instance(20, 24, 20, 20)
         .<ExecutableWidget>eV(new ButtonElement(TextProvider.c(Text.literal("F").formatted(Formatting.BOLD)), ButtonAction.b(i -> {
            if (i) {
               ChatTasks.f().enableChatMessageFormat.toggle();
            } else {
               MainTasks.p(ChatTasks.f());
            }
         })).cw(el -> ChatTasks.f().enableChatMessageFormat.get()).aO(TooltipHandler.ap(Hr)))
         .addToSub(var3);
      ContentDelegateWidget var7 = McWidgetHelpers.c(40, 24, 40, 20, PropertyTracker.event(s -> this.Ht = s), this.Ht);
      this.Hy = (TextFieldWidget)var7.ef();
      ExecutableWidget.instance(80, 24, 40, 20)
         .<ExecutableWidget>eV(
            new ButtonElement(TextProvider.c(Text.literal("int<->char")), ButtonAction.a(() -> this.tranlateInt2char(this.Hy))).aO(TooltipHandler.ap(Hp))
         )
         .addToSub(var3);
      ContentDelegateWidget var8 = new ContentDelegateWidget(250, 0, 0, 0).addToSub(var3);
      ExecutableWidget.instance(225, 0, 22, 20)
         .<ExecutableWidget>eV(
            new ButtonElement(el -> this.enableQuickChars.get() ? Hz : HA, ButtonAction.a(this.enableQuickChars::toggle)).aO(TooltipHandler.ap(Hs))
         )
         .addToSub(var3);
      this.Hw = var8;
      this.Hu = var3;
      this.toggleSpecialCharWidget(this.enableQuickChars.get());
      this.Hv = new ContentDelegateWidget<>(-250, -68, 0, 0);
      this.toggleBasicToolScreen(this.enableTools.get());
   }

   private void tranlateInt2char(TextFieldWidget int2CharInputField) {
      String var2 = int2CharInputField.getText();
      if (!var2.isEmpty()) {
         try {
            int var3 = Integer.parseInt(var2);

            try {
               char var7 = (char)var3;
               int2CharInputField.setText(String.valueOf(var7));
            } catch (Throwable var5) {
               int2CharInputField.setText("Error");
            }
         } catch (Throwable var6) {
            char var4 = var2.charAt(0);
            int2CharInputField.setText(String.valueOf((int)var4));
         }
      }
   }

   public void onCloseChatScreen(Event<ChatScreen> event) {
      if (this.Hf.get()
         && mc.player != null
         && mc.world != null
         && mc.currentScreen != null
         && mc.currentScreen.getClass() == ChatScreen.class
         && ScreenUtils.hasEnterDown()) {
         ChatScreenAccess var2 = ChatScreenAccess.of((ChatScreen)mc.currentScreen);
         var2.resetMessageHistoryIndex();
         event.cancel();
      }
   }

   public void onRemoveCommandPrefix() {
      if (mc.currentScreen instanceof ChatScreen var2 && var2.getFocused() instanceof TextFieldWidget var4 && var4.getText().startsWith("/")) {
         var4.setText(var4.getText().substring(1));
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.V(), this::onTick);
      this.registerListener(Listener.ai().c(ChatScreen.class), this::onChatScreenInitialize);
      this.registerListener(Listener.ae().c(ChatScreen.class), this::onCloseChatScreen);
      this.registerListener(Listener.ai().c(ChatScreen.class), this::fixIMBlockerStateError);
      TaskManagers.c().register("button-toggle.auto-chat", this.Hc);
      TaskManagers.c().register("button-toggle.keep-chat-inv", this.Hf);
   }

   private void VQ(String specialChars) {
      this.toggleSpecialCharWidget(this.enableQuickChars.get());
   }

   public void onChatScreenInitialize(Event<ChatScreen> event) {
      ChatScreen var2 = (ChatScreen)event.b;
      if (this.Hu == null) {
         this.initToolWidget();
      }

      ExecutableWidget.instance(var2.width - 20, var2.height - 56, 20, 20)
         .<ExecutableWidget>eV(new ButtonElement(el -> this.enableTools.get() ? Hz : HA, ButtonAction.a(this.enableTools::toggle)).aO(TooltipHandler.ap(Hk)))
         .addTo(var2);
      ContentDelegateWidget var3 = new ContentDelegateWidget(var2.width, var2.height - 36, 0, 0);
      var3.setContentDelegate(this.Hv);
      var3.addTo(var2);
      ScreenAccess var4 = ScreenAccess.of(var2);
      this.Hx.setX(var2.width - 250);
      this.Hx.setY(var2.height - 56);
      this.Hy.setX(var2.width - 210);
      this.Hy.setY(var2.height - 104 + 24);
      if (this.enableTools.get()) {
         var4.addDrawableChildTo(this.Hx);
         var4.addDrawableChildTo(this.Hy);
      }
   }

   public ChatTools() {
      super("ChatTools");
      this.enableTools = this.flagBuilder(this.ew.add("enable-tools")).updateListener(this::toggleBasicToolScreen).build();
      this.enableQuickChars = this.flagBuilder(this.ew.add("enable-quick-chars")).updateListener(this::toggleSpecialCharWidget).build();
      this.quickChars = this.builder(this.ew.add("quick-chars"), String.class)
         .defaultValue(
            "\ud83d\ude21\ud83e\udd13\ud83e\udd75\ud83d\ude2d\ud83e\udd21\ud83d\ude0b\ud83e\udd24\ud83d\ude0a\ud83d\ude04\ud83e\udd72\ud83d\ude01\ud83d\udc49\ud83d\udc46\ud83e\udd14\ud83d\ude0e\ud83d\udc0d\ud83d\ude05♂♀"
         )
         .updateListener(this::VQ)
         .build();
      this.cached = this.builder(this.ew.add("cached"), String.class).defaultValue("").build();
      this.Hc = this.flagBuilder(Configs.h, this.ew.add("auto-chat").toPath()).build();
      this.autoChatPeriod = this.intBuilder(this.ew.add("auto-chat-period")).defaultValue(21).build();
      this.autoChatMultiple = this.intBuilder(this.ew.add("auto-chat-multiple")).defaultValue(1).build();
      this.Hf = this.flagBuilder(Configs.h, this.ew.add("keep-chat-inv").toPath()).build();
      this.obfLoginMessage = this.flagBuilder(this.ew.add("obf-login-message")).build();
      this.Hh = this.hotkey(Configs.h, this.ew.add("remove-command-prefix-hotkey").toPath(), new MultiKeyBind())
         .registerHotkey(HotKeyUtils.d(this::onRemoveCommandPrefix))
         .build();
      this.counter = 0;
      this.Ht = "";
      this.bindFlag(this.enableTools);
   }

   private void toggleBasicToolScreen(boolean bl) {
      if (this.Hv != null) {
         if (bl) {
            this.Hv.setContentDelegate(this.Hu);
         } else {
            this.Hv.setContentDelegate(null);
         }

         if (mc.currentScreen instanceof ChatScreen var3) {
            ScreenAccess var4 = ScreenAccess.of(var3);
            Tasks.l(() -> {
               var4.removeChildFrom(this.Hx);
               var4.removeChildFrom(this.Hy);
               if (bl) {
                  var4.addDrawableChildTo(this.Hx);
                  var4.addDrawableChildTo(this.Hy);
               }
            }, 0);
         }
      }
   }

   public void fixIMBlockerStateError(Event<ChatScreen> event) {
      if (IMBlockerHooks.getInstance().isEnabled() && ((ChatScreen)event.b).getFocused() instanceof FocusableObject var3) {
         ChatScreen var4 = (ChatScreen)event.b;
         KalamaHelperHelperG.c(() -> {
            if (var4.getFocused() == var3) {
               var3.updateEnglishState();
            }
         }, 100L);
      }
   }

   public void VL() {
      String var1 = this.cached.get();
      if (var1 != null) {
         ChatTasks.sayMessage(var1, false);
      }
   }

   public void onTick(Event<ClientPlayerEntity> gt) {
      if (mc.getNetworkHandler() != null && this.Hc.get()) {
         this.counter++;
         if (this.counter >= this.autoChatPeriod.getValue()) {
            this.counter = 0;

            for (int var2 = 0; var2 < this.autoChatMultiple.get(); var2++) {
               this.VL();
            }
         }
      }
   }

   private void toggleSpecialCharWidget(boolean bl) {
      if (this.Hw != null) {
         if (bl) {
            String var2 = this.quickChars.getValue();
            int var3 = var2.length();
            int var4 = (var3 + 1 - 1) / 4 + 1;
            int var5 = 1;
            byte var6 = 4;
            int var7 = 0;
            KalamaHelperHelperCX var8 = new KalamaHelperHelperCX(-100, -24 * var4, 100, 24 * var4);

            for (int var9 = 0; var9 < var3; var9++) {
               var5++;
               char var10 = var2.charAt(var9);
               String var11 = String.valueOf(var10);
               if (!ChatUtils.c(var10)) {
                  if (++var9 < var3) {
                     char var12 = var2.charAt(var9);
                     var11 = new String(new char[]{var10, var12});
                  }
               }

               String var13 = var11;
               ExecutableWidget.instance(100 - 25 * var5, (var4 - var7) * 24, 22, 20)
                  .<ExecutableWidget>eV(new ButtonElement(TextProvider.c(Text.literal(var13)), ButtonAction.a(() -> {
                     TextFieldWidget var2x = this.findCurrentFocusing();
                     if (var2x != null) {
                        var2x.write(var13);
                     }
                  })))
                  .addToSub(var8);
               if (var5 >= var6) {
                  var5 = 0;
                  var7++;
               }
            }

            this.Hw.setContentDelegate(var8);
         } else {
            this.Hw.setContentDelegate(null);
         }
      }
   }

   @Nullable
   private TextFieldWidget findCurrentFocusing() {
      if (mc.currentScreen instanceof ChatScreen var2 && var2.getFocused() instanceof TextFieldWidget var3) {
         return var3;
      } else {
         return this.Hu != null
               && this.Hu.isFocused()
               && this.Hu.getSelected() instanceof ContentDelegateWidget var4
               && var4.ef() instanceof TextFieldWidget var5
            ? var5
            : null;
      }
   }
}
