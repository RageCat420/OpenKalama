package me.matl114.hacks.utils.entity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import me.matl114.events.Event;
import me.matl114.managers.Tasks;
import me.matl114.utils.KalamaHelperHelperAs;
import me.matl114.utils.KalamaHelperHelperUX;
import me.matl114.utils.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.util.math.Vec3d;

public class PredictorImpl implements Predictor {
    private final Deque<HackUtilHelperE> dm = new ArrayDeque<>();
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final int MAX_HISTORY = 30;
    private final Entity dl;

    public List<HackUtilHelperE> getLastKnownPositions(int lastNumber) {
        if (lastNumber <= 0) {
            return Collections.emptyList();
        } else {
            List<HackUtilHelperE> var2 = new ArrayList<>(this.dm);
            if (var2.size() > lastNumber) {
                var2 = var2.subList(var2.size() - lastNumber, var2.size());
            }

            int var3 = lastNumber - var2.size();
            if (var3 > 0) {
                Vec3d var4 = this.dl.getPos();
                int var5 = Tasks.b();

                for (int var6 = 0; var6 < var3; var6++) {
                    var2.add(new HackUtilHelperE(var4, var5));
                }
            }

            return (List<HackUtilHelperE>) var2;
        }
    }

    public PredictorImpl(Entity owner) {
        this.dl = owner;
    }

    @Override
    public Vec3d getKnownDeltaMovement() {
        if (this.dm.size() < 2) {
            return Vec3d.ZERO;
        } else {
            Iterator var1 = this.dm.descendingIterator();
            HackUtilHelperE var2 = (HackUtilHelperE) var1.next();
            HackUtilHelperE var3 = (HackUtilHelperE) var1.next();
            int var4 = var2.tick() - var3.tick();
            if (var4 == 0) {
                return var2.vec3d().subtract(var3.vec3d());
            } else {
                Vec3d var5 = var2.vec3d().subtract(var3.vec3d());
                return var5.multiply(1.0 / var4);
            }
        }
    }

    @Override
    public Vec3d predict(int ticksLater, int method, int useTicksBefore) {
        if (ticksLater == 0) {
            return this.dl.getPos();
        } else {
            int var4 = Tasks.b();
            Vec3d var5 = this.dl.getPos();
            ArrayList<HackUtilHelperE> var6 = new ArrayList<>();
            HackUtilHelperE var7 = null;
            boolean var8 = false;

            for (HackUtilHelperE var10 : this.dm) {
                if (var10.tick() >= var4 - useTicksBefore) {
                    var8 = true;
                    if (var7 != null) {
                        var6.add(var7);
                    }
                }

                var7 = var10;
            }

            if (var7 != null && var8) {
                var6.add(var7);
            }

            if (var6.isEmpty()) {
                return var5;
            } else {
                int var21 = ((HackUtilHelperE) var6.get(var6.size() - 1)).tick();
                int var22 = ((HackUtilHelperE) var6.get(0)).tick();
                int var11 = var4 - useTicksBefore;
                if (var4 + ticksLater < var22) {
                    return ((HackUtilHelperE) var6.get(0)).vec3d();
                } else if (var11 >= var21) {
                    return var5;
                } else {
                    if (var22 > var11) {
                        HackUtilHelperE var12 = (HackUtilHelperE) var6.get(0);
                        var6.add(0, new HackUtilHelperE(var12.vec3d(), var11));
                    }

                    int var23 = var21 - var11 + 1;
                    int var13 = var4 - var21;
                    if (var23 < 2) {
                        return var5;
                    } else {
                        Vec3d[] var14 = new Vec3d[var23];
                        int var15 = 0;
                        HackUtilHelperE var16 = (HackUtilHelperE) var6.get(var15);
                        HackUtilHelperE var17 = null;

                        label81:
                        for (int var18 = 0; var18 < var14.length; var18++) {
                            for (int var19 = var11 + var18;
                                    var16.tick() != var19;
                                    var16 = (HackUtilHelperE) var6.get(var15)) {
                                if (var17 != null && var17.tick() < var19 && var16.tick() > var19) {
                                    var14[var18] = var16.vec3d()
                                            .multiply(var16.tick() - var19)
                                            .add(var17.vec3d().multiply(var19 - var17.tick()))
                                            .multiply(1.0 / (var16.tick() - var17.tick()));
                                    continue label81;
                                }

                                var17 = var16;
                                if (++var15 >= var6.size()) {
                                    throw new RuntimeException("?   WTF");
                                }
                            }

                            var14[var18] = var16.vec3d();
                        }

                        int var24 = var13 + ticksLater;
                        if (var24 <= 0) {
                            return var14[var14.length - 1 + var24];
                        } else {
                            switch (method) {
                                case 1:
                                    return MathUtils.M(var14, var24);
                                case 2:
                                    return MathUtils.N(var14, var24);
                                case 3:
                                    Vec3d[] var26 = Arrays.copyOf(var14, var14.length);
                                    int var27 = var14.length - 1;
                                    return new KalamaHelperHelperUX(var26, () -> var27).compute(var24);
                                case 4:
                                    Vec3d[] var25 = Arrays.copyOf(var14, var14.length);
                                    int var20 = var14.length - 1;
                                    return new KalamaHelperHelperAs(var25, () -> var20).compute(var24);
                                default:
                                    return var5;
                            }
                        }
                    }
                }
            }
        }
    }

    public void onEntityPositionMove(Event<EntityS2CPacket> event) {
        EntityS2CPacket var2 = (EntityS2CPacket) event.e();
        if (var2.getEntity(mc.world) == this.dl) {
            this.hu(new HackUtilHelperE(this.dl.getPos(), Tasks.b()));
        }
    }

    public void tick() {
        while (this.dm.size() > 30) {
            this.dm.removeFirst();
        }

        if (mc.player == this.dl) {
            this.hu(new HackUtilHelperE(this.dl.getPos(), Tasks.b()));
        }
    }

    private void hu(HackUtilHelperE record) {
        HackUtilHelperE var2 = this.dm.peekLast();
        if (!Objects.equals(var2, record)) {
            this.dm.add(record);
        }
    }

    public void onEntityPositionPost(Event<EntityPositionS2CPacket> event) {
        EntityPositionS2CPacket var2 = (EntityPositionS2CPacket) event.e();
        if (var2.getEntityId() == this.dl.getId()) {
            this.hu(new HackUtilHelperE(this.dl.getPos(), Tasks.b()));
        }
    }
}
