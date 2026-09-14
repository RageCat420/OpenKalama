package me.matl114.hooks;

import io.github.reserveword.imblocker.common.gui.FocusableObject;

public class IMBlockerHooks implements IHooks {
    boolean enabled = false;
    public static IMBlockerHooks instance;

    public IMBlockerHooks() {
        try {
            Class<?> clazz = FocusableObject.class;
            this.enabled = true;
        } catch (Throwable var2) {
            this.enabled = false;
        }
    }

    public static IMBlockerHooks getInstance() {
        if (instance == null) {
            instance = new IMBlockerHooks();
        }

        return instance;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
