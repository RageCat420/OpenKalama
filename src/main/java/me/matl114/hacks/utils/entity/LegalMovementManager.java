package me.matl114.hacks.utils.entity;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import me.matl114.events.Event;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.entity.PlayerInputUtils;
import me.matl114.utils.entity.PlayerInputUtils$Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class LegalMovementManager {
    boolean g;
    boolean f;
    List<HackUtilHelperJ> b;
    public EntityMovementStatus<ClientPlayerEntity> c;
    final List<HackUtilHelperJ> a = new ArrayList<>();
    public Deque<Pair<Float, Float>> e;
    boolean h;
    public EntityMovementStatus<ClientPlayerEntity> d;

    protected void popImportantRotation(boolean apply) {
        if (this.e != null) {
            Pair var2 = this.e.removeLast();
            if (apply && var2 != null) {
                if (var2.getFirst() != null) {
                    EntityUtils.setEntityPitchSafe(this.c.a, (Float) var2.getFirst());
                }

                if (var2.getSecond() != null) {
                    PlayerStateManager.nT((PlayerEntity) (Object) this.c.a, (Float) var2.getSecond());
                }
            }
        }
    }

    public boolean t() {
        return this.f;
    }

    public boolean e() {
        return this.d() ? this.e.stream().anyMatch(pair -> pair.getFirst() != null) : false;
    }

    public void postInputTick(ClientPlayerEntity player) {
        if (this.c != null) {
            Event var2 = new Event<>(this, false, false);

            for (HackUtilHelperJ var4 : this.b) {
                var4.gz(var2);
            }

            if (this.f) {
                PlayerInputUtils$Input var5 = PlayerInputUtils.a(player);
                var5 = PlayerInputUtils.tryCorrectMovementInput(
                        var5, ((LegalMovementManager) var2.b).c.i, player.getYaw());
                if (var5.ro() < 0.01 && (var5.rK() || player.isSprinting())) {
                    var5.rD(false);
                    player.setSprinting(false);
                }

                var5.applyInput(player);
            }
        }
    }

    public boolean preTravelTick(ClientPlayerEntity args, Event<Vec3d> movementInput) {
        if (this.c == null) {
            return true;
        } else {
            Event var3 = new Event<>(this, true, false);

            for (HackUtilHelperJ var5 : this.b) {
                var5.jI(var3, movementInput);
            }

            return !var3.d();
        }
    }

    public void r(ClientPlayerEntity args) {
        if (this.c != null) {
            Event var2 = new Event<>(this, true, false);
            Iterator var3 = new ArrayList<>(this.a).iterator();
            int var4 = 0;

            while (var3.hasNext()) {
                HackUtilHelperJ var5 = (HackUtilHelperJ) var3.next();
                boolean var6 = var4 < this.b.size() && this.b.get(var4) == var5;
                if (var6) {
                    var4++;
                }

                if (!var5.postModify(var2, var6)) {
                    var3.remove();
                    this.a.remove(var5);
                }
            }

            if (this.g) {
                this.c.restorePos();
            }

            if (this.h) {
                this.c.d();
            }
        }
    }

    public void a() {
        this.f = true;
    }

    public boolean p(ClientPlayerEntity args) {
        if (this.c == null) {
            return true;
        } else {
            Event var2 = new Event<>(this, true, false);

            for (HackUtilHelperJ var4 : this.b) {
                var4.iB(var2);
            }

            return !var2.d();
        }
    }

    public boolean pitchModified() {
        return Math.abs(EntityUtils.k(this.c.h) - EntityUtils.k(this.c.a.getPitch())) > 2.0F;
    }

    public void c() {
        this.h = true;
    }

    public LegalMovementManager() {
        this.b = new ArrayList<>();
        this.f = false;
        this.g = false;
        this.h = false;
    }

    public boolean q(ClientPlayerEntity args) {
        if (this.c == null) {
            return true;
        } else {
            Event var2 = new Event<>(this, true, false);

            for (HackUtilHelperJ var4 : this.b) {
                var4.iC(var2);
            }

            return !var2.d();
        }
    }

    public void i(HackUtilHelperJ movementModifier) {
        int var2 = movementModifier.priority();
        int var3 = 0;

        while (var3 < this.a.size() && this.a.get(var3).priority() <= var2) {
            var3++;
        }

        this.a.add(var3, movementModifier);
    }

    public void s() {
        if (this.yawModified()) {
            this.a();
        }
    }

    public boolean f() {
        return this.d() ? this.e.stream().anyMatch(pair -> pair.getSecond() != null) : false;
    }

    public boolean u() {
        return this.g;
    }

    public void pushImportantRotation(boolean hasPitch, boolean hasYaw) {
        if (hasPitch || hasYaw) {
            if (this.e == null) {
                this.e = new ArrayDeque<>();
            }

            this.e.addLast(Pair.of(hasPitch ? this.c.a.getPitch() : null, hasYaw ? this.c.a.getYaw() : null));
        }
    }

    public boolean yawModified() {
        ClientPlayerEntity var1 = this.c.a;
        float var2 = EntityUtils.i(var1.getYaw(), this.c.i);
        float var3 = EntityUtils.i(var1.getYaw(), var1.getYaw());
        float var4 = Math.abs(var2 - var3);
        float var5 = Math.min(var4, 360.0F - var4);
        return var5 > 2.0F;
    }

    public boolean v() {
        return this.h;
    }

    public void b() {
        this.g = true;
    }

    public boolean d() {
        return this.e != null && !this.e.isEmpty();
    }

    public void l(ClientPlayerEntity args) {
        this.c = new EntityMovementStatus(args);
        this.b = new ArrayList<>();
        this.e = null;
        this.g = false;
        this.h = false;
        this.f = false;
        Event var2 = new Event<>(this, false, false);

        for (HackUtilHelperJ var4 : this.a) {
            if (var4.shouldApply(var2)) {
                this.b.add(var4);
            }
        }

        this.d = new EntityMovementStatus(args);
    }

    public void postTravelTick(ClientPlayerEntity args, Event<Vec3d> movementInput) {
        if (this.c != null) {
            Event var3 = new Event<>(this, false, false);

            for (HackUtilHelperJ var5 : this.b) {
                var5.jJ(var3, movementInput);
            }
        }
    }
}
