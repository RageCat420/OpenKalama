package me.matl114.hacks.modules.extra;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.concurrent.CompletableFuture;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.Debug;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EyeOfEnderEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.Vec3d;

public class EnderEyeLog extends BaseModule {
    private static final IntList STRONGHOLD_DISTANCE_RANGES = new IntArrayList();
    public final ModulePath ad = makePath(Configs.j, "other");
    private static final int MAX_STRONGHOLD_COORD = 40000;
    public final FlagRef ae =
            this.flagBuilder(this.ad.add("enable-ender-eye-log")).build();
    private final Int2ObjectMap<Vec3d> af = new Int2ObjectOpenHashMap();

    public void onEntityRemove(Event<Entity> event) {
        if (this.ae.get() && event.e() instanceof EyeOfEnderEntity) {
            Vec3d var2 = (Vec3d) (Object) this.af.remove(((Entity) event.e()).getId());
            if (var2 != null) {
                Vec3d var3 = ((Entity) event.e()).getPos();
                Vec3d var4 = var3.subtract(var2);
                var4 = var4.normalize();
                int var5 = (int) var2.getX();
                Debug.b("Start Calculating EyeOfEnder...");
                if (var4.x == 0.0) {
                    if (var4.z > 0.0) {
                        Debug.b("[EnderEye] Pointing at Z+");
                    } else {
                        Debug.b("[EnderEye] Pointing at Z-");
                    }
                } else if (var4.z == 0.0) {
                    if (var4.x > 0.0) {
                        Debug.b("[EnderEye] Pointing at X+");
                    } else {
                        Debug.b("[EnderEye] Pointing at X-");
                    }
                } else {
                    int var6 = var4.x > 0.0 ? 1 : -1;
                    double var7 = var4.getZ() / var4.getX();
                    double var9 = var2.getZ() - var7 * var2.getX();
                    CompletableFuture.<IntList>supplyAsync(() -> {
                                IntArrayList var6x = new IntArrayList();
                                int var7x = (int) ((long) (40000.0 - var9 - var7 * var5) / (var6 * var7));
                                int var8 = (int) ((long) (-40000.0 - var9 - var7 * var5) / (var6 * var7));
                                int var9x = (40000 - var5) / var6;
                                int var10 = (-40000 - var5) / var6;
                                int var11x = Math.max(0, Math.min(Math.min(var7x, var8), Math.min(var9x, var10)));
                                int var12 = Math.max(0, Math.min(Math.max(var7x, var8), Math.max(var9x, var10)));

                                for (int var13 = var11x; var13 < var12; var13++) {
                                    int var14 = var5 + var6 * var13;
                                    if (var14 % 16 == 0) {
                                        var6x.add(var14);
                                    }
                                }

                                double var15 = 1.0;
                                IntArrayList var17 = new IntArrayList();
                                IntListIterator var18 = var6x.iterator();

                                while (var18.hasNext()) {
                                    int var19 = (Integer) var18.next();
                                    double var20 = var7 * var19 + var9;
                                    long var22 = Math.round(var20);
                                    if (var22 % 16L == 0L) {
                                        double var24 = Math.abs(var22 - var20);
                                        if (var24 < 0.1) {
                                            var15 = Math.min(var15, var24);
                                            var17.add(var19);
                                        }
                                    }
                                }

                                return var17;
                            })
                            .thenAcceptAsync(
                                    i -> {
                                        if (!i.isEmpty()) {
                                            Debug.b("[EnderEye] Potentials Positions:");
                                            IntListIterator var5x = i.iterator();

                                            while (var5x.hasNext()) {
                                                int var6x = (Integer) var5x.next();
                                                int var7x = (int) Math.round(var7 * var6x + var9);
                                                Debug.chat("[EnderEye] ", ChatUtils.s(var6x, var7x));
                                            }

                                            byte var13 = -1;
                                            IntListIterator var14 = i.iterator();

                                            while (var14.hasNext()) {
                                                int var15 = (Integer) var14.next();
                                                int var8 = (int) Math.round(var7 * var15 + var9);
                                                int var9x = (int) Math.sqrt(var15 * var15 + var8 * var8);

                                                for (byte var10 = 0;
                                                        var10 < STRONGHOLD_DISTANCE_RANGES.size() - 1;
                                                        var10 += 2) {
                                                    int var11x = STRONGHOLD_DISTANCE_RANGES.getInt(var10);
                                                    int var12 = STRONGHOLD_DISTANCE_RANGES.getInt(var10 + 1);
                                                    if (var9x >= var11x && var9x <= var12) {
                                                        if (var13 != -1 && var13 != var10) {
                                                            return;
                                                        }

                                                        var13 = var10;
                                                        Debug.chat(
                                                                "[EnderEye] Most probably at:",
                                                                ChatUtils.s(var15, var8),
                                                                ",In ring",
                                                                var10 / 2 + 1);
                                                    }
                                                }
                                            }
                                        } else {
                                            Debug.b("[EnderEye] Calculation failure");
                                        }
                                    },
                                    mc);
                }
            }
        }
    }

    public void onEnderEye(Event<EntitySpawnS2CPacket> event) {
        EntitySpawnS2CPacket var2 = (EntitySpawnS2CPacket) event.e();
        if (this.ae.get() && var2.getEntityType() == EntityType.EYE_OF_ENDER) {
            Vec3d var3 = new Vec3d(var2.getX(), var2.getY(), var2.getZ());
            int var4 = var2.getEntityId();
            this.af.put(var4, var3);
        }
    }

    public EnderEyeLog() {
        super("EnderEyeLog");
        this.bindFlag(this.ae);
    }

    static {
        byte var0 = 32;
        byte var1 = 8;

        for (int var2 = 0; var2 < var1; var2++) {
            int var3 = (int) (var0 * (2.75 + 6 * var2)) * 16 - 128;
            int var4 = (int) (var0 * (5.25 + 6 * var2)) * 16 + 128;
            STRONGHOLD_DISTANCE_RANGES.add(var3);
            STRONGHOLD_DISTANCE_RANGES.add(var4);
        }
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.ap().getChannel(EntitySpawnS2CPacket.class), this::onEnderEye);
        this.registerListener(Listener.aU(), this::onEntityRemove);
    }
}
