package me.matl114.hacks.modules;

import java.util.Collection;
import me.matl114.events.annotations.Modifiable;
import me.matl114.hacks.api.ModuleGroup;

@Modifiable
public class HackModules {
    public static final ModuleMain main = new ModuleMain();

    public static Collection<ModuleGroup> getModuleGroups() {
        return main.moduleGroups.values();
    }

    public static void d() {
        main.reloadModules();
    }

    public static ModuleGroup getModuleGroup(String name) {
        return main.moduleGroups.get(name);
    }

    public static void e() {}

    public static void registerModuleGroup(ModuleGroup group) {
        main.o(group);
    }
}
