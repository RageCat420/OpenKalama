package me.matl114.hacks.modules.ac;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.function.Consumer;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.managers.Tasks;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;

public class PostManager extends BaseModule {
    private final Deque<Consumer<ClientPlayNetworkHandler>> yg = new ArrayDeque<>(33);
    private final Deque<Consumer<ClientPlayNetworkHandler>> yh = new ArrayDeque<>(33);
    private int ye;
    public static PostManager INSTANCE;
    private int yf;
    private Deque<CommonPongC2SPacket> yi = new ArrayDeque<>(33);

    public void KU(Event<CommonPingS2CPacket> event) {
        this.ye--;
        if (this.ye < 0) {
            this.ye = 0;
        }
    }

    private void KX(ClientPlayNetworkHandler handler) {
        this.runQueue(handler, this.yh);
    }

    public PostManager() {
        super("PostManager");
        INSTANCE = this;
    }

    public void z(Event<Void> v) {}

    private void onPreTick(Event<ClientPlayerEntity> v) {
        if (this.ye > 0 && this.yf + 20 < Tasks.b()) {
            this.ye = 0;
            this.yf = Tasks.b();
            this.KX(mc.getNetworkHandler());
        }
    }

    private void runQueue(
            ClientPlayNetworkHandler handler, Deque<Consumer<ClientPlayNetworkHandler>> postTickHandlers) {
        if (!postTickHandlers.isEmpty()) {
            if (handler != null) {
                Iterator var3 = postTickHandlers.iterator();

                while (var3.hasNext()) {
                    ((Consumer) var3.next()).accept(handler);
                    var3.remove();
                }
            } else {
                postTickHandlers.clear();
            }
        }
    }

    private void KY(Event<Void> v) {
        this.ye = 0;
    }

    public void KT(Event<ClientPlayerEntity> event) {
        this.KV(mc.getNetworkHandler());
    }

    private void KV(ClientPlayNetworkHandler handler) {
        this.runQueue(handler, this.yg);
    }

    public void x(Event<Void> tick) {
        this.KX(mc.getNetworkHandler());
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.ap().getChannel(CommonPingS2CPacket.class), this::KS);
        this.registerListener(Listener.ar().getChannel(CommonPingS2CPacket.class), this::KU);
        this.registerListener(Listener.V(), this::KT);
        this.registerListener(Listener.O(), this::KY);
        this.registerListener(Listener.V(), this::onPreTick);
        this.registerListener(Listener.S(), this::x);
    }

    public void KS(Event<CommonPingS2CPacket> packetPing) {
        this.ye++;
        this.yf = Tasks.b();
    }

    public void addNextPreTickAction(Consumer<ClientPlayNetworkHandler> packet) {
        if (this.yf < Tasks.b() - 10) {
            if (mc.getNetworkHandler() != null) {
                packet.accept(mc.getNetworkHandler());
            }
        } else {
            this.yh.addLast(packet);
        }
    }

    public void KQ(Consumer<ClientPlayNetworkHandler> handler) {
        this.yg.add(handler);
    }
}
