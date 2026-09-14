package me.matl114.mixins.events;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.util.Objects;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({ChatScreen.class})
public class ChatScreenEvents {
    @Inject(
            method = {"sendMessage"},
            at = {@At("HEAD")},
            cancellable = true)
    private void onSendInput(
            String chatText,
            boolean addToHistory,
            CallbackInfo ci,
            @Local(argsOnly = true) LocalRef<String> chatTextRef) {
        Event<String> stringEvent = new Event<>(chatText, true, true);
        Listener.ac().catchEvent(stringEvent);
        if (stringEvent.d()) {
            ci.cancel();
        }

        if (!Objects.equals(stringEvent.b, chatText)) {
            chatTextRef.set(stringEvent.e());
        }
    }
}
