package me.matl114.hacks.modules.move;

import java.util.ArrayDeque;
import java.util.Deque;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.MovTasks$MovInfo;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.utils.entity.HackUtilHelperD;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.utils.Debug;
import me.matl114.versioned.api.VPacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.math.Vec3d;

public class MovTest extends BaseModule implements HackUtilHelperJ {
    int zZ;
    boolean runOnGroundThisTick;
    Packet<?> Ad;
    public static HackUtilHelperD instance;
    Vec3d Aa;
    int Ac;
    Deque<EntityVelocityUpdateS2CPacket> zX = new ArrayDeque<>();
    MoveSubHelperRX zY;
    Deque<Vec3d> Ae;

    public MovTest() {
        super("MovTest");
        this.Ac = 0;
        this.Ad = null;
        this.Ae = new ArrayDeque<>();
        if (instance == null) {
            instance = new HackUtilHelperD(this::cast);
            MovTasks.j.SJ(() -> instance);
        }

        instance.mN(this::cast);
    }

    public void applyBeforeMovementPacketModify(Event<LegalMovementManager> movementManagerEvent) {
        if (this.enable()) {
            movementManagerEvent.cancel();
            ((LegalMovementManager) movementManagerEvent.b).c.restorePos();
            this.Ad = VPacket.j(
                    mc.player.getX(),
                    mc.player.getY(),
                    mc.player.getZ(),
                    mc.player.getYaw(),
                    mc.player.getPitch(),
                    mc.player.isOnGround(),
                    mc.player.horizontalCollision);
        }
    }

    public void Ot(Event<CommonPingS2CPacket> event) {
        if (this.enable()) {}
    }

    @Override
    public int priority() {
        return 10000000;
    }

    @Override
    public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
        if (this.Ad != null) {
            Listener.sendPacketNoEvents(this.Ad);
            this.Ad = null;
        }

        if (this.enable()) {
            if (this.Ae == null) {
                this.Ae = new ArrayDeque<>();
            }

            this.Ae.add(mc.player.getPos());
            Vec3d var3 = null;

            while (this.Ae.size() > 20) {
                var3 = this.Ae.removeFirst();
            }

            if (var3 != null) {
                Debug.chat(
                        "Speed last one sec :",
                        mc.player.getPos().subtract(var3).length());
            }
        } else if (this.Ae != null) {
            this.Ae.clear();
            this.Ae = null;
        }

        return true;
    }

    public boolean enable() {
        return false;
    }

    @Override
    public void gz(Event<LegalMovementManager> movementManagerEvent) {
        if (!this.enable()) {}
    }

    public void onVelocityPacket(Event<EntityVelocityUpdateS2CPacket> event) {
        if (this.enable()
                && !checkNull()
                && ((EntityVelocityUpdateS2CPacket) event.b).getEntityId() == mc.player.getId()) {}
    }

    @Override
    public void bb(Event<LegalMovementManager> movementManagerEvent) {}

    public void ji(Event<MovTasks$MovInfo> setBack) {}

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.aA(), this::ji);
        this.registerListener(Listener.ap().getChannel(CommonPingS2CPacket.class), this::Ot);
        this.registerListener(Listener.ap().getChannel(EntityVelocityUpdateS2CPacket.class), this::onVelocityPacket);
    }
}
