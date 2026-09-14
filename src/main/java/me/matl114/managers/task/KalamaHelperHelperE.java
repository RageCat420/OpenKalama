package me.matl114.managers.task;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class KalamaHelperHelperE implements TaskManager {
    private final Map<String, Runnable> a;

    @Override
    public Runnable getOrRegister(String value, Runnable task) {
        return this.a.computeIfAbsent(value, r -> task);
    }

    public KalamaHelperHelperE() {
        this.a = new LinkedHashMap<>();
    }

    @Override
    public Runnable a(String value) {
        return this.a.get(value);
    }

    public KalamaHelperHelperE(Map<String, Runnable> tasks) {
        this.a = tasks;
    }

    @Override
    public void register(String value, Runnable task) {
        this.a.put(value, task);
    }

    @Override
    public Map<String, Runnable> b() {
        return Collections.unmodifiableMap(this.a);
    }
}
