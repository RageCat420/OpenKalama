package me.matl114.hacks.api;

import java.util.ArrayList;
import java.util.List;

public class AbstractManager<T> {
    public List<T> registered = new ArrayList<>();

    public void unloadModules() {
        ArrayList<T> var1 = new ArrayList<>(this.registered);
        this.registered.clear();
        var1.forEach(this::unregisterModule);
    }

    public void loadModules() {}

    public void unregisterModule(T module) {
        this.registered.remove(module);
    }

    public void reloadModules() {
        this.unloadModules();
        this.loadModules();
    }

    public void registerModule(T module) {
        this.registered.add((T) module);
    }
}
