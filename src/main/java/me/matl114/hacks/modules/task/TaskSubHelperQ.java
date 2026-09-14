package me.matl114.hacks.modules.task;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public record TaskSubHelperQ(int selected, List<TaskSubHelperK> entries) {
    public static Codec<TaskSubHelperQ> CODEC = RecordCodecBuilder.create(oinstance -> oinstance
            .group(
                    Codec.INT.fieldOf("index").forGetter(TaskSubHelperQ::Rj),
                    Codec.list(TaskSubHelperK.dt).fieldOf("entries").forGetter(TaskSubHelperQ::Rk))
            .apply(oinstance, TaskSubHelperQ::new));

    public int Rj() {
        return this.selected;
    }

    public List<TaskSubHelperK> Rk() {
        return this.entries;
    }

    public TaskSubHelperK ahk() {
        return this.entries.size() > this.selected && this.selected >= 0 ? this.entries.get(this.selected) : null;
    }
}
