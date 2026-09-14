package me.matl114.hacks;

import java.util.ArrayList;
import net.minecraft.network.packet.Packet;

public final class KalamaHelperHelperV {
    public ArrayList<Runnable> c;
    public ArrayList<Packet<?>> a = new ArrayList<>(4);
    public boolean success;
    public ArrayList<Runnable> b = new ArrayList<>(1);

    public void c(Runnable task) {
        this.b.add(task);
    }

    public void d(Runnable task) {
        this.c.add(task);
    }

    public void b(Packet<?> packet) {
        this.a.add(packet);
    }

    public void run() {
        this.b.forEach(Runnable::run);

        for (Packet var2 : this.a) {
            MovTasks.a.getNetworkHandler().sendPacket(var2);
        }

        this.c.forEach(Runnable::run);
    }

    public void a() {
        this.success = false;
    }

    public KalamaHelperHelperV() {
        this.c = new ArrayList<>(1);
        this.success = true;
    }
}
