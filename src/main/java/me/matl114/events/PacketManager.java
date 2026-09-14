package me.matl114.events;

import com.google.common.collect.Queues;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import me.matl114.events.catchers.PacketCatcher;
import me.matl114.events.channels.EventChannel;
import me.matl114.events.channels.KalamaHelperHelperC;
import me.matl114.events.packets.PacketStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.AcknowledgeReconfigurationC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatCommandSignedC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.EnterReconfigurationS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.world.World;

public class PacketManager {
    private static final ReferenceSet<PacketType<?>> i = new ReferenceArraySet();
    private static final MinecraftClient g = MinecraftClient.getInstance();
    public static EventChannel<Void> k = new EventChannel<>();
    public static KalamaHelperHelperC<PacketStorage> j = new KalamaHelperHelperC<>(PacketStorage::side);
    public static final ConcurrentLinkedQueue<PacketStorage> a = Queues.newConcurrentLinkedQueue();
    public static boolean e = false;
    public static boolean f = false;
    public static final WeakHashMap<Packet<?>, List<Consumer<Event<Packet<?>>>>> d = new WeakHashMap<>();
    public static final ConcurrentLinkedQueue<PacketStorage> b = Queues.newConcurrentLinkedQueue();
    private static final ReferenceSet<PacketType<?>> h = new ReferenceArraySet();
    public static final WeakHashMap<Packet<?>, List<Consumer<Event<Packet<?>>>>> c = new WeakHashMap<>();

    public static void flushOutBound() {
        try {
            if (g.getNetworkHandler() != null
                    && g.getNetworkHandler().getConnection().isOpen()) {
                f = true;

                try {
                    for (PacketStorage var1 : b) {
                        var1.send();
                    }
                } finally {
                    f = false;
                }
            }
        } finally {
            b.clear();
        }
    }

    public static void w(Event<Void> disconnect) {
        i();
    }

    public static void u(PacketStorage packet) {
        a.add(packet);
    }

    public static void f(Event<Packet<?>> packet) {
        List<Consumer<Event<Packet<?>>>> var1 = c.remove(packet.b);
        if (var1 != null && !var1.isEmpty()) {
            for (Consumer var3 : var1) {
                var3.accept(packet);
            }
        }
    }

    protected static <W> void y(PacketCatcher<W> listener, Consumer<W> handler) {
        listener.k(handler);
    }

    public static KalamaHelperHelperC<PacketStorage> z() {
        return j;
    }

    public static boolean isInventoryPacket(PacketType<?> pkt) {
        return pkt == PlayPackets.CONTAINER_CLICK || pkt == PlayPackets.CONTAINER_CLOSE_C2S;
    }

    public static void k() {
        try {
            if (g.getNetworkHandler() != null
                    && g.getNetworkHandler().getConnection().isOpen()) {
                e = true;

                try {
                    for (PacketStorage var1 : a) {
                        var1.handle();
                    }
                } finally {
                    e = false;
                }
            }
        } finally {
            a.clear();
        }
    }

    public static <T extends Packet<?>> void e(T post, Consumer<Event<T>> packet) {
        if (packet != null) {
            d.computeIfAbsent(post, kv -> new ArrayList<>()).add((Consumer) packet);
        }
    }

    public static EventChannel<Void> A() {
        return k;
    }

    public static <T extends Packet<?>> void d(T post, Runnable packet) {
        if (packet != null) {
            e((T) post, ev -> packet.run());
        }
    }

    public static void g(Event<Packet<?>> packet) {
        List<Consumer<Event<Packet<?>>>> var1 = d.remove(packet.b);
        if (var1 != null && !var1.isEmpty()) {
            for (Consumer var3 : var1) {
                var3.accept(packet);
            }
        }
    }

    public static boolean s(Packet<?> pkt) {
        return pkt instanceof KeepAliveS2CPacket
                || pkt instanceof ChatMessageS2CPacket
                || pkt instanceof GameMessageS2CPacket
                || pkt instanceof CloseScreenS2CPacket
                || pkt instanceof ChunkDataS2CPacket;
    }

    public static boolean o(Packet<?> pkt) {
        return pkt instanceof KeepAliveC2SPacket
                || pkt instanceof ChatCommandSignedC2SPacket
                || pkt instanceof ChatMessageC2SPacket
                || pkt instanceof CommandExecutionC2SPacket
                || pkt instanceof RequestCommandCompletionsC2SPacket;
    }

    public static boolean isAsyncOrNotTransactionC2SPacket(PacketType<?> pkt) {
        return pkt != null && h.contains(pkt);
    }

    public static <T extends Packet<?>> void c(T post, Consumer<Event<T>> packet) {
        if (packet != null) {
            c.computeIfAbsent(post, kv -> new ArrayList<>()).add((Consumer) packet);
        }
    }

    public static void l(Function<PacketStorage, KalamaHelperHelperG> pdd) {
        if (g.getNetworkHandler() != null
                && g.getNetworkHandler().getConnection().isOpen()) {
            e = true;
            Iterator var1 = a.iterator();

            try {
                while (var1.hasNext()) {
                    PacketStorage var2 = (PacketStorage) var1.next();
                    switch ((KalamaHelperHelperG) pdd.apply(var2)) {
                        case NV:
                            var1.remove();
                            break;
                        case NT:
                            var2.handle();
                            var1.remove();
                    }
                }
            } finally {
                e = false;
            }
        } else {
            a.removeIf(v -> pdd.apply(v) != KalamaHelperHelperG.NV);
        }
    }

    public static boolean handleQueueInPacket(Packet<?> packet, ClientConnection connection) {
        if (e) {
            return false;
        } else {
            if (connection.getPacketListener() instanceof ClientPlayPacketListener var3) {
                if (!(packet instanceof DisconnectS2CPacket)
                        && !(packet instanceof HealthUpdateS2CPacket var5 && var5.getHealth() <= 0.0)
                        && !(packet instanceof PlayerRespawnS2CPacket)
                        && !(packet instanceof EnterReconfigurationS2CPacket)) {
                    Event var4 = new Event<>(
                            new KalamaHelperHelperH(packet, System.currentTimeMillis(), connection),
                            true,
                            false,
                            connection);
                    j.b(var4);
                    if (var4.d()) {
                        u((PacketStorage) var4.e());
                        return true;
                    }
                } else {
                    i();
                }
            }

            return false;
        }
    }

    public static <T extends Packet<?>> void b(T post, Runnable packet) {
        if (packet != null) {
            c((T) post, ev -> packet.run());
        }
    }

    public static void x(Event<World> event) {
        i();
    }

    public static boolean handleQueueOutPacket(Packet<?> packet, ClientConnection connection) {
        if (f) {
            return false;
        } else {
            if (connection.getPacketListener() instanceof ClientPlayPacketListener var3) {
                if (packet instanceof AcknowledgeReconfigurationC2SPacket) {
                    i();
                } else {
                    Event var4 = new Event<>(
                            new KalamaHelperHelperH(packet, System.currentTimeMillis(), connection),
                            true,
                            false,
                            connection);
                    j.b(var4);
                    if (var4.d()) {
                        v((PacketStorage) var4.e());
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public static void i() {
        k.broadcast(null);
        k();
        flushOutBound();
    }

    public static boolean t(PacketType<?> pkt) {
        return pkt != null && i.contains(pkt);
    }

    public static void n(Function<PacketStorage, KalamaHelperHelperG> pdd) {
        if (g.getNetworkHandler() != null
                && g.getNetworkHandler().getConnection().isOpen()) {
            f = true;
            Iterator var1 = b.iterator();

            try {
                while (var1.hasNext()) {
                    PacketStorage var2 = (PacketStorage) var1.next();
                    switch ((KalamaHelperHelperG) pdd.apply(var2)) {
                        case NV:
                            var1.remove();
                            break;
                        case NT:
                            var2.send();
                            var1.remove();
                    }
                }
            } finally {
                f = false;
            }
        } else {
            b.removeIf(v -> pdd.apply(v) != KalamaHelperHelperG.NV);
        }
    }

    public static void a(Packet<?> post, Packet<?> packet) {
        if (packet != null) {
            c(post, ev -> {
                ClientConnection var2 = ev.getArgs(0);
                var2.send(packet);
            });
        }
    }

    public static void v(PacketStorage packet) {
        b.add(packet);
    }

    static {
        Listener.ao().k(PacketManager::f);
        Listener.an().k(PacketManager::g);
        h.add(CommonPackets.KEEP_ALIVE_C2S);
        h.add(PlayPackets.CHAT_COMMAND_SIGNED);
        h.add(PlayPackets.CHAT_COMMAND);
        h.add(PlayPackets.CHAT);
        h.add(PlayPackets.COMMAND_SUGGESTION);
        i.add(CommonPackets.KEEP_ALIVE_S2C);
        i.add(PlayPackets.PLAYER_CHAT);
        i.add(PlayPackets.SYSTEM_CHAT);
        i.add(PlayPackets.CONTAINER_CLOSE_S2C);
        i.add(PlayPackets.LEVEL_CHUNK_WITH_LIGHT);
        y(Listener.O(), PacketManager::w);
        y(Listener.N(), PacketManager::x);
    }

    public static boolean q(Packet<?> pkt) {
        return pkt instanceof ClickSlotC2SPacket || pkt instanceof CloseHandledScreenC2SPacket;
    }
}
