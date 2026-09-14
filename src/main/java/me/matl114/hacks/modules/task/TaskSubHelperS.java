package me.matl114.hacks.modules.task;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;

public class TaskSubHelperS {
    String c;
    Map<String, TaskSubHelperR> a;
    public static Codec<TaskSubHelperS> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.unboundedMap(Codec.STRING, TaskSubHelperR.CODEC)
                            .fieldOf("module_list_metas")
                            .forGetter(TaskSubHelperS::g),
                    Codec.unboundedMap(Codec.STRING, Codec.BOOL)
                            .optionalFieldOf("sub_group_metas", Map.of())
                            .forGetter(TaskSubHelperS::h),
                    Codec.STRING.optionalFieldOf("searching", "").forGetter(TaskSubHelperS::i))
            .apply(instance, TaskSubHelperS::new));
    Map<String, Boolean> b;

    public void b(String key) {
        this.b.putIfAbsent(key, true);
    }

    public Map<String, TaskSubHelperR> g() {
        return this.a;
    }

    public void checkDefault(List<String> moduleNames, int wX, int wY) {
        int var4 = moduleNames.size();
        int var5 = 0;
        int var6 = 0;

        for (int var7 = 0; var7 < var4; var5++) {
            int var8 = var5 * (wX + 5);
            if (var8 + wX > MinecraftClient.getInstance().getWindow().getScaledWidth()) {
                var5 = 0;
                var8 = 0;
                var6++;
            }

            this.a((String) moduleNames.get(var7), var8, 40 + var6 * wY * 2);
            var7++;
        }
    }

    public String i() {
        return this.c;
    }

    public void a(String moduleName, int x, int y) {
        if (!this.a.containsKey(moduleName)) {
            TaskSubHelperR var4 = new TaskSubHelperR(x, y, false);
            this.a.put(moduleName, var4);
        }
    }

    public Map<String, Boolean> h() {
        return this.b;
    }

    public TaskSubHelperS() {
        this.a = new LinkedHashMap<>();
        this.b = new LinkedHashMap<>();
        this.c = "";
    }

    public TaskSubHelperR getModuleMeta(String moduleName) {
        return Objects.requireNonNull(this.a.get(moduleName));
    }

    public void j(String searching) {
        this.c = searching;
    }

    public boolean isSubGroupExpanded(String key) {
        this.b(key);
        return this.b.get(key);
    }

    public void setSubGroupExpanded(String key, boolean expanded) {
        this.b.put(key, expanded);
    }

    public TaskSubHelperS(
            Map<String, TaskSubHelperR> moduleCoordinates, Map<String, Boolean> subGroupMetaMap, String searching) {
        this.a = new LinkedHashMap<>(moduleCoordinates);
        this.b = new LinkedHashMap<>(subGroupMetaMap);
        this.c = searching;
    }
}
