package me.matl114.bukkit;

import java.util.LinkedHashMap;
import java.util.Map;
import me.matl114.events.annotations.Dispatch;

public class BukkitOfflineplayer implements Dispatch {
    protected String QD;
    protected String name;

    public Map<String, Object> serialize() {
        LinkedHashMap var1 = new LinkedHashMap();
        var1.put("UUID", this.QD);
        var1.put("name", this.name);
        return var1;
    }

    public static BukkitOfflineplayer deserialize(Map<String, Object> args) {
        BukkitOfflineplayer offline = new BukkitOfflineplayer();
        offline.name = (String) args.get("name");
        offline.QD = (String) args.get("uuid");
        return offline;
    }
}
