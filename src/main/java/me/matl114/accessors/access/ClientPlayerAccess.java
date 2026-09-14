package me.matl114.accessors.access;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.matl114.accessors.events.ClientPlayerEntityAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.screen.ScreenHandler;

public interface ClientPlayerAccess extends ClientPlayerEntityAccess {
    @Nullable
    HandledScreen getKeepedInv();

    @Nullable
    ScreenHandler getKeepedInvHandler();

    void clearKeepedInventory(boolean var1);

    @Nonnull
    static ClientPlayerAccess of(@Nonnull ClientPlayerEntity player) {
        return (ClientPlayerAccess) player;
    }

    default HandledScreen getServerOpeningScreen() {
        if (this.getKeepedInv() != null) {
            return this.getKeepedInv();
        } else {
            return MinecraftClient.getInstance().currentScreen instanceof HandledScreen<?> han ? han : null;
        }
    }

    @Nonnull
    default ScreenHandler getServerScreenHandler() {
        return this.getKeepedInvHandler() != null
                ? this.getKeepedInvHandler()
                : ((ClientPlayerEntity) (Object) this).currentScreenHandler;
    }

    boolean isForceNoFall();

    void setForceNoFall(boolean var1);
}
