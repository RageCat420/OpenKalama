package me.matl114.hacks.modules;

import java.util.LinkedHashMap;
import java.util.Map;
import me.matl114.hacks.api.AbstractManager;
import me.matl114.hacks.api.ModuleGroup;
import me.matl114.utils.Debug;

public class ModuleMain extends AbstractManager<ModuleGroup> {
    Map<String, ModuleGroup> moduleGroups = new LinkedHashMap<>();

    @Override
    public void unloadModules() {}

    public Map<String, ModuleGroup> getModuleGroups() {
        return this.moduleGroups;
    }

    @Override
    public void reloadModules() {
        this.registered.forEach(AbstractManager::reloadModules);
    }

    public void o(ModuleGroup module) {
        super.registerModule(module);
        this.moduleGroups.put(module.getName(), module);
    }

    public void p(ModuleGroup module) {
        Debug.a("Unexpected unregister in a moduleGroup! " + module.getName());
        super.unregisterModule(module);
        this.moduleGroups.remove(module.getName());
    }
}
