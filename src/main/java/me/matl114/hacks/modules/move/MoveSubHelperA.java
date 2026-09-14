package me.matl114.hacks.modules.move;

import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.events.Event;
import me.matl114.hacks.MovTasks$MovInfo;
import me.matl114.hacks.utils.entity.EntityMovementStatus;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.managers.Tasks;
import me.matl114.utils.entity.PlayerInputUtils;
import me.matl114.utils.entity.PlayerInputUtils$Input;
import me.matl114.versioned.api.VPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.Vec3d;

public class MoveSubHelperA extends MoveSubHelperY {
    Vec3d vr;
    boolean vC;
    int vt;
    int vp;
    int vx;
    int vz;
    private static final int vA = 3;
    MoveSubHelperH vB;
    Packet<?> vD;
    int vo = -1;
    Boolean vs;
    boolean vq;
    Vec3d vy;
    int vw;
    boolean vv;
    boolean vu;

    @Override
    public void gz(Event<LegalMovementManager> movementManagerEvent) {
        if (this.module.isActive()
                && (this.vB == MoveSubHelperH.AQ || this.vB == MoveSubHelperH.AR || this.vB == MoveSubHelperH.AS)) {
            ClientPlayerEntity var2 = ((LegalMovementManager) movementManagerEvent.b).c.a;
            PlayerInputUtils$Input var3 = PlayerInputUtils.a(var2);
            boolean var4 = true;
            boolean var5 = true;
            if (this.vB == MoveSubHelperH.AQ) {
                if (var5) {
                    var3 = var3.rx(this.vC).ry(false).rz(false).rA(false);
                }

                if (var4) {
                    var3 = var3.rB(false);
                }
            } else if (this.vB == MoveSubHelperH.AR) {
                if (var5) {
                    var3 = var3.rx(this.vC).ry(false).rz(false).rA(false);
                }

                if (var4) {
                    var3 = var3.rB(true);
                }
            } else {
                if (var5) {
                    var3 = var3.rx(false).ry(false).rz(false).rA(false);
                }

                if (var4) {
                    var3 = var3.rB(true);
                }
            }

            if (!var2.isFallFlying()) {
                var3.applyInput(var2);
            }
        }
    }

    @Override
    public void iC(Event<LegalMovementManager> movementManagerEvent) {
        if (this.module.isActive()) {
            EntityMovementStatus var2 = ((LegalMovementManager) movementManagerEvent.b).c;
            if (this.vB == MoveSubHelperH.AQ) {
                ((LegalMovementManager) movementManagerEvent.b).c.restorePos();
                ClientPlayerAccess.of((ClientPlayerEntity) var2.a).resyncPos();
                this.vB = MoveSubHelperH.AR;
            } else {
                boolean var3;
                if (this.vB == MoveSubHelperH.AR) {
                    var3 = true;
                    if (this.vp + 6 <= Tasks.b()) {
                        this.vB = MoveSubHelperH.AP;
                    }
                } else if (this.vB == MoveSubHelperH.AS) {
                    var3 = true;
                    if (this.vp + 3 <= Tasks.b()) {
                        this.vB = MoveSubHelperH.AP;
                    }
                } else {
                    var3 = this.vp + 3 >= Tasks.b()
                            || this.vq && this.vp + 30 >= Tasks.b()
                            || ((ClientPlayerEntity) var2.a).getY() <= this.module.Uw - this.module.UF;
                }

                boolean var4 = this.vB == MoveSubHelperH.AS || this.vp + 3 >= Tasks.b();
                if (var3 && !var4) {
                    this.vt = 0;
                    this.vq = false;
                }

                if (var3 && !this.Md && !var2.b && ((ClientPlayerEntity) var2.a).isOnGround()) {
                    if (this.vB == MoveSubHelperH.AS) {
                        this.vt++;
                    } else {
                        this.vt = 1;
                    }

                    if (this.vt > 20) {
                        this.vp = -1;
                        this.vr = null;
                        this.vt = 0;
                        this.vB = MoveSubHelperH.AP;
                        return;
                    }

                    this.vq = false;
                    this.Md = true;
                    this.vp = Tasks.b();
                    this.counter = 0;
                    this.vr = ((ClientPlayerEntity) var2.a).getPos();
                    this.module.Uw = var2.g.getY();
                    this.vD = VPacket.g(var2.g.getX(), var2.g.getY() + 9.0E-8, var2.g.getZ(), false, var2.c);
                    movementManagerEvent.cancel();
                    PlayerInputUtils$Input var5 = PlayerInputUtils.a((ClientPlayerEntity) var2.a);
                    if (this.vB == MoveSubHelperH.AP) {
                        this.vC = !var5.rE() && !var5.rF() && !var5.rG() && !var5.rH();
                        var5.rB(false).applyInput((ClientPlayerEntity) var2.a);
                        this.vB = MoveSubHelperH.AQ;
                    }

                    this.Mc = true;
                    return;
                }
            }
        }
    }

    @Override
    public void bb(Event<LegalMovementManager> movementManagerEvent) {
        ClientPlayerEntity var2 = ((LegalMovementManager) movementManagerEvent.b).c.a;
        boolean var3 = ClientPlayerAccess.of(var2).isForceNoFall();
        if (var3) {
            this.Md = true;
            this.counter = 0;
            this.module.Uw = this.module.Uz;
            MinecraftClient.getInstance()
                    .getNetworkHandler()
                    .sendPacket(VPacket.g(
                            var2.getX(), this.module.Uz + 9.0E-8, var2.getZ(), false, var2.horizontalCollision));
            this.Mc = true;
            ClientPlayerAccess.of(var2).setForceNoFall(false);
        } else if (this.module.isActive()) {
            this.counter++;
        }

        if (this.counter > 100) {
            this.Mc = false;
        }
    }

    @Override
    public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
        if (this.vD != null) {
            MinecraftClient.getInstance().getNetworkHandler().sendPacket(this.vD);
        }

        this.vD = null;
        if (this.vs != null) {
            if (!this.Md) {
                ((LegalMovementManager) movementManagerEvent.b).c.a.setOnGround(this.vs);
            }

            this.vs = null;
        }

        if (this.Md) {}

        return true;
    }

    public MoveSubHelperA(NoFall module) {
        super(module);
        this.vp = -1;
        this.vr = null;
        this.vt = 0;
        this.vz = 0;
        this.vB = MoveSubHelperH.AP;
        this.vC = false;
        this.vD = null;
        this.vq = true;
        this.vt = 0;
    }

    @Override
    public void ji(Event<MovTasks$MovInfo> setBack) {
        this.vq = true;
        this.vo = Tasks.b();
        Vec3d var2 = ((MovTasks$MovInfo) setBack.b).vec3d();
        boolean var3 = this.vy != null && this.vy.squaredDistanceTo(var2) < 1.0E-12;
        if (var3) {
            this.vz++;
            if (this.vz > 3) {
                this.vz = 0;
                this.vt = 100;
            }
        } else {
            this.vz = 0;
        }

        this.vy = var2;
        if (this.vB == MoveSubHelperH.AR) {
            this.vB = MoveSubHelperH.AS;
        }

        super.ji(setBack);
    }
}
