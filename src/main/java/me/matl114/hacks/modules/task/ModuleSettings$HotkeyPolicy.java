package me.matl114.hacks.modules.task;

import me.matl114.managers.config.ConfigEnum;

public enum ModuleSettings$HotkeyPolicy implements ConfigEnum {
    ONLY_WHEN_NO_SCREEN,
    WHEN_NO_INPUT_SCREEN,
    RUN_IN_ALL_SCREEN;

    @Override
    public String getConfigEnumType() {
        return "module_settings_hotkey_policy";
    }
}
