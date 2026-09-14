package me.matl114.hacks.api;

import me.matl114.managers.config.Config;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.MapRef;
import me.matl114.managers.config.Ref;
import me.matl114.managers.input.MultiKeyBind;

public class WrapperModuleSettingBuilder extends WrapperSettingBuilder<MultiKeyBind> {
    boolean registered = false;
    ModuleEntry moduleEntry;

    @Override
    public <W2 extends Ref<MultiKeyBind>> W2 build() {
        Ref var1 = super.build();
        if (this.registered) {
            this.module.registeredModuleEntry.add(this.moduleEntry);
        }

        return (W2) var1;
    }

    public WrapperModuleSettingBuilder(MapRef ref, Config rootConfig, BaseModule module, ModuleEntry moduleEntry) {
        super(ref, rootConfig, KeyBindRef.TYPE, module);
        this.moduleEntry = moduleEntry;
        this.path(moduleEntry.hotkeyPath);
    }

    @Override
    public WrapperSettingBuilder<MultiKeyBind> registerModuleEntry() {
        this.registered = true;
        return this;
    }
}
