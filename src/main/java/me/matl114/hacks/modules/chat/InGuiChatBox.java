package me.matl114.hacks.modules.chat;

import java.util.Objects;
import me.matl114.accessors.access.HandledScreenAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.gui.complex.other.ChatLikeInputSubScreen;
import me.matl114.hacks.ChatTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

public class InGuiChatBox extends BaseModule {
    public final FlagRef chatBoxInGui;
    public final ModulePath ev = makePath(Configs.h, "chat-helper");

    public InGuiChatBox() {
        super("InGuiChatBox");
        this.chatBoxInGui = this.flagBuilder(this.ev.add("chat-box-in-gui")).build();
        this.bindFlag(this.chatBoxInGui);
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.ai().c(HandledScreen.class), this::oM);
    }

    public void oM(Event<HandledScreen<?>> event) {
        if (event.b instanceof HandledScreenAccess var3 && this.chatBoxInGui.get()) {
            ChatLikeInputSubScreen var4 = new ChatLikeInputSubScreen(
                    var3.getScreenX() + 2,
                    var3.getScreenY()
                            + var3.getScreenBackgroundY()
                            + (var3 instanceof CreativeInventoryScreen ? 40 : 10),
                    var3.getScreenBackgroundX() - 4,
                    12,
                    str -> {
                        if (str != null && !str.isEmpty() && !Objects.equals(str, "/")) {
                            ChatTasks.sayMessage(str, true);
                        }
                    });
            var3.addDrawableChildTo(var4);
        }
    }
}
