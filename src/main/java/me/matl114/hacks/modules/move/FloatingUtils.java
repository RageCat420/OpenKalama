package me.matl114.hacks.modules.move;

import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.entity.HackUtilHelperD;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.versioned.api.VPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class FloatingUtils extends BaseModule implements HackUtilHelperJ {
    public final ModulePath DX;
    boolean Eh;
    public final FlagRef DY;
    public final ModulePath fu = makePath(Configs.m, "velocity-management");
    boolean Ec;
    boolean Ee;
    PlayerMoveC2SPacket Ef;
    boolean Eg;
    boolean Ei;
    static HackUtilHelperD cy;
    public static FloatingUtils INSTANCE;
    public final ModulePath sm = this.fu.add("floating-utils");
    boolean Ed;
    public final FlagRef useSnapPacket;
    public final FlagRef onGroundFloat;
    public final KeyBindRef DZ;

    public void SD(boolean t) {
        this.Eh = t;
    }

    public boolean SG() {
        return this.DY.get() || this.Ec;
    }

    @Override
    public int priority() {
        return 2147483646;
    }

    @Override
    public void iC(Event<LegalMovementManager> movementManagerEvent) {
        if (this.SG()) {
            movementManagerEvent.cancel();
            ((LegalMovementManager) movementManagerEvent.b).c.restorePos();
            boolean var2 = this.onGroundFloat.get() || this.Ed;
            if (var2) {
                this.Ef = LegacySnapRotManager.INSTANCE.ahx(mc.player.getPitch(), mc.player.getYaw(), true);
                mc.player.setOnGround(true);
                this.Ei = true;
            } else if (this.Ei) {
                this.Ei = false;
                this.Ef = LegacySnapRotManager.INSTANCE.ahx(
                        mc.player.getPitch(), mc.player.getYaw(), PlayerStateManager.INSTANCE.jI);
            } else {
                this.Ef = VPacket.h(
                        mc.player.getYaw(),
                        mc.player.getPitch(),
                        mc.player.isOnGround(),
                        mc.player.horizontalCollision);
                if (this.Ee) {
                    this.Ef = LegacySnapRotManager.INSTANCE.createAsSnap(this.Ef);
                }
            }
        } else {
            this.Ei = false;
        }
    }

    public void SC(boolean t) {
        this.Ed = t;
    }

    @Override
    public void bb(Event<LegalMovementManager> movementManagerEvent) {}

    public FloatingUtils() {
        super("FloatingUtils");
        this.DX = this.sm.add("grim-floating");
        this.DY = this.flagBuilder(this.DX.addEnable()).build();
        this.DZ = this.toggleHotkey(this.DX.addHotkey(), new MultiKeyBind(), this.DX.addEnable())
                .build();
        this.onGroundFloat = this.flagBuilder(this.DX.add("on-ground-float")).build();
        this.useSnapPacket = this.flagBuilder(this.DX.add("use-snap-packet")).build();
        this.Ec = false;
        this.Ed = false;
        this.Ee = false;
        this.Eg = false;
        this.Eh = false;
        if (cy == null) {
            cy = new HackUtilHelperD(this::cast);
            MovTasks.j.SJ(() -> cy);
        }

        cy.mN(this::cast);
        this.bindFlag(this.DY);
        INSTANCE = this;
    }

    public void SF(Event<PlayerMoveC2SPacket> eventMove) {
        if (!eventMove.d() && !((PlayerMoveC2SPacket) eventMove.b).changesPosition()) {
            this.Eg = true;
        }
    }

    @Override
    public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
        this.Ec = false;
        this.Ed = false;
        this.Ee = this.useSnapPacket.get();
        if (this.Ef != null) {
            if (!this.Eh
                    && (!this.Eg
                            || PlayerStateManager.INSTANCE.nQ(
                                    this.Ef.getPitch(mc.player.getPitch()), this.Ef.getYaw(mc.player.getYaw())))) {
                mc.getNetworkHandler().sendPacket(this.Ef);
            }

            this.Ef = null;
        }

        this.Eh = false;
        this.Eg = false;
        return true;
    }

    public void SE(boolean t) {
        this.Ee = t;
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.ap().getChannel(PlayerMoveC2SPacket.class), this::SF);
    }

    public void SB(boolean t) {
        this.Ec = t;
    }
}
