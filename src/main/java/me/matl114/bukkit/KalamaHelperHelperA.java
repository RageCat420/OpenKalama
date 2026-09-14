package me.matl114.bukkit;

import java.util.LinkedHashMap;
import java.util.Map;

public class KalamaHelperHelperA {
    public Map<String, ?> b;
    public String a;

    public KalamaHelperHelperA(Map value) {
        this.b = new LinkedHashMap<>(value);
        this.a = (String) value.get("==");
        this.b.remove("==");
    }
}
