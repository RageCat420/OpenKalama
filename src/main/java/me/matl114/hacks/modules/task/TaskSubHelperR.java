package me.matl114.hacks.modules.task;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class TaskSubHelperR {
    boolean slidingDown;
    private int a;
    public static Codec<TaskSubHelperR> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("x_coord").forGetter(TaskSubHelperR::c),
                    Codec.INT.fieldOf("y_coord").forGetter(TaskSubHelperR::d),
                    Codec.BOOL.fieldOf("sliding_down").forGetter(TaskSubHelperR::isSlidingDown))
            .apply(instance, TaskSubHelperR::new));
    private int b;

    public TaskSubHelperR(int x, int y, boolean slidingDown) {
        this.a = x;
        this.b = y;
        this.slidingDown = slidingDown;
    }

    public void b(int y) {
        this.b = y;
    }

    public int c() {
        return this.a;
    }

    public void a(int x) {
        this.a = x;
    }

    public boolean isSlidingDown() {
        return this.slidingDown;
    }

    public int d() {
        return this.b;
    }
}
