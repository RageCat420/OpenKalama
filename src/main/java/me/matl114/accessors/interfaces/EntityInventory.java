package me.matl114.accessors.interfaces;

import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;

public interface EntityInventory<T> {
    @Nullable
    T getOwner();

    HandledScreen<?> castHandled();

    default ScreenHandler castHandler() {
        return this.castHandled().getScreenHandler();
    }

    public interface Handler<T> extends EntityInventory<T> {
        @Override
        default HandledScreen<?> castHandled() {
            throw new UnsupportedOperationException();
        }

        @Override
        default ScreenHandler castHandler() {
            return (ScreenHandler) (Object) this;
        }

        void sync(EntityInventory<T> var1);
    }
}
