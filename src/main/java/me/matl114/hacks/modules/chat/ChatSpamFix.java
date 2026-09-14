package me.matl114.hacks.modules.chat;

import java.util.List;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.RegexList;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.Debug;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.Text;
import net.minecraft.util.Nullables;

public class ChatSpamFix extends BaseModule {
    public final NBTRef<RegexList> regexList;
    public final ModulePath ro = makePath(Configs.h, "chat-spam-fix");
    public final FlagRef logHiddenMessages;
    public final FlagRef ae = this.flagBuilder(this.ro.add("enable")).build();

    public void onMessageAdd(Event<Text> event) {
        if (this.ae.get()) {
            String var2 = ChatUtils.l((Text) event.e());
            if (this.regexList.get().test(var2)) {
                event.cancel();
                if (this.logHiddenMessages.get()) {
                    String var3 = ((Text) event.e())
                            .getString()
                            .replaceAll("\r", "\\\\r")
                            .replaceAll("\n", "\\\\n");
                    String var4 =
                            (String) Nullables.map((MessageIndicator) event.getArgs(1), MessageIndicator::loggedName);
                    if (var4 != null) {
                        Debug.e("[ChatSpamFix]", var4, var3);
                    } else {
                        Debug.e("[ChatSpamFix]", var3);
                    }
                }
            }
        }
    }

    public ChatSpamFix() {
        super("ChatSpamFix");
        this.regexList = this.builder(this.ro.add("regex-list"), RegexList.class)
                .defaultValue(new RegexList(List.of()))
                .build();
        this.logHiddenMessages =
                this.flagBuilder(this.ro.add("log-hidden-messages")).build();
        this.bindFlag(this.ae);
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.aa(), this::onMessageAdd);
    }
}
