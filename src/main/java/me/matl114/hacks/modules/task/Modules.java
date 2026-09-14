package me.matl114.hacks.modules.task;

import java.util.List;
import me.matl114.accessors.events.ChatHudAccess;
import me.matl114.accessors.gui.TextFieldAccess;
import me.matl114.commands.MainCommand;
import me.matl114.gui.WidgetUtils;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.presets.single.KeyBindConfigurateWidget;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.StringFormat;
import me.matl114.managers.Configs;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.config.StringRef;
import me.matl114.utils.Debug;
import net.minecraft.client.gui.Element;
import net.minecraft.text.Text;

public class Modules extends BaseModule {
   public final NBTRef<StringFormat> moduleLogMessageFormat;
   public final NBTRef<StringFormat> moduleOffNotifyFormat;
   public static final String Jn = "kalama:module_toggle/";
   public final StringRef moduleCommandPrefix;
   public static Modules INSTANCE;
   public final FlagRef moduleToggleNotify;
   public final NBTRef<StringFormat> moduleOnNotifyFormat;
   public final FlagRef toggleKeysStopVanilla;
   ModulePath Je = makePath(Configs.r, "module-settings");
   public final FlagRef compressModuleToggleMessage;
   public final EnumRef<ModuleSettings$HotkeyPolicy> Jf = this.builder(this.Je.add("hotkey-work-policy"), ModuleSettings$HotkeyPolicy.class)
      .defaultValue(ModuleSettings$HotkeyPolicy.ONLY_WHEN_NO_SCREEN)
      .build();

   public void sendToggleMessage(String message, boolean result) {
      if (!checkNull()) {
         if (this.moduleToggleNotify.get()) {
            StringFormat var3 = result ? this.moduleOnNotifyFormat.get() : this.moduleOffNotifyFormat.get();
            ChatHudAccess var4 = ChatHudAccess.of(mc.inGameHud.getChatHud());
            String var5 = "kalama:module_toggle/" + message;
            if (this.compressModuleToggleMessage.get()) {
               var4.clearUniqueMessages(var5);
            }

            var4.setUniqueMessageId(var5);
            Debug.b(var3.formatText(Text.translatableWithFallback(message, message)));
            var4.setUniqueMessageId(null);
         }
      }
   }

   public Modules() {
      super("Modules");
      this.toggleKeysStopVanilla = this.builder(this.Je.add("toggle-keys-stop-vanilla"), FlagRef.TYPE).defaultValue(true).build();
      this.moduleToggleNotify = this.builder(this.Je.add("module-toggle-notify"), FlagRef.TYPE).defaultValue(true).build();
      this.moduleOnNotifyFormat = this.builder(this.Je.add("module-on-notify-format"), StringFormat.class)
         .defaultValue(new StringFormat(List.of("name"), "&a&l[+] &f{name}", true))
         .build();
      this.moduleOffNotifyFormat = this.builder(this.Je.add("module-off-notify-format"), StringFormat.class)
         .defaultValue(new StringFormat(List.of("name"), "&c&l[-] &f{name}", true))
         .build();
      this.compressModuleToggleMessage = this.flagBuilder(this.Je.add("compress-module-toggle-message")).build();
      this.moduleLogMessageFormat = this.builder(this.Je.add("module-log-message-format"), StringFormat.class)
         .defaultValue(new StringFormat(List.of("module_name", "message"), "&c[{module_name}] &f{message}", true))
         .updateListener(s -> BaseModule.moduleMessageFormat = s)
         .build();
      this.moduleCommandPrefix = this.builder(this.Je.add("module-command-prefix"), StringRef.TYPE)
         .defaultValue("!!")
         .updateListener(s -> MainCommand.l = s)
         .build();
      INSTANCE = this;
   }

   public boolean shouldNotExecuteConditionHotkey() {
      if (mc.currentScreen != null) {
         if (this.Jf.getValue() == ModuleSettings$HotkeyPolicy.RUN_IN_ALL_SCREEN) {
            return false;
         } else if (checkNull()) {
            return true;
         } else {
            switch ((ModuleSettings$HotkeyPolicy)this.Jf.getValue()) {
               case ONLY_WHEN_NO_SCREEN:
                  return true;
               case WHEN_NO_INPUT_SCREEN:
                  Element var1 = mc.currentScreen.getFocused();
                  if (var1 instanceof TextFieldAccess) {
                     return true;
                  } else {
                     if (var1 instanceof DrawableWidget var2) {
                        DrawableWidget var3 = WidgetUtils.a(var2);
                        if (WidgetUtils.isInputWidget(WidgetUtils.a(var3))) {
                           return true;
                        }

                        List var4 = WidgetUtils.b(var2);
                        if (var4.stream().anyMatch(s -> s instanceof KeyBindConfigurateWidget)) {
                           return true;
                        }
                     }

                     return false;
                  }
               default:
                  return false;
            }
         }
      } else {
         return false;
      }
   }
}
