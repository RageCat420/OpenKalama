package me.matl114.hacks.modules.combat;

import com.google.common.hash.Hashing;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import me.matl114.accessors.hacks.EntityInternalAccess;
import me.matl114.accessors.hacks.PlayerInternalAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.entity.EntityMovementStatus;
import me.matl114.hacks.utils.entity.Predictor;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ColorUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.entity.PlayerInputUtils$Input;
import me.matl114.versioned.api.VRender;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ShieldItem;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;

public class PositionPredict extends BaseModule {
    Int2ObjectArrayMap<List<Vec3d>> Mw;
    public final FlagRef exactTpAntiShield;
    public final KeyBindRef placeRecorderHotkey;
    public final FlagRef placeRecorder;
    public static PositionPredict INSTANCE;
    public final NBTRef<PositionPredict$PredictArgument> flyPredictArgument;
    public final NBTRef<PositionPredict$PredictArgument> spearPredictArgument;
    public final NBTRef<PositionPredict$PredictArgument> attackPredictArgument;
    public final ModulePath Ju = makePath(Configs.k, "attack");
    public final FlagRef debugRenderPrediction;
    public final ModulePath KE = makePath(Configs.k, "att-bot");

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.ar().getChannel(EntityS2CPacket.class), this::onPostEntity);
        this.registerListener(Listener.ar().getChannel(EntityPositionS2CPacket.class), this::onPostEntityPos);
        this.registerListener(RenderListener.q(), this::onRender);
    }

    public Vec3d predictPlayerMove(PlayerInputUtils$Input input) {
        EntityMovementStatus var2 = new EntityMovementStatus(mc.player);
        if (mc.player.isFallFlying()) {
            return mc.player.getVelocity();
        } else {
            return mc.player.isInFluid()
                    ? mc.player.getVelocity()
                    : var2.calculateLastMoveVelocity(input.ro(), input.rp());
        }
    }

    public void onPostEntity(Event<EntityS2CPacket> event) {
        if (!checkNull()) {
            if (((EntityS2CPacket) event.b).getEntity(mc.world) instanceof PlayerInternalAccess var3) {
                var3.getPredictorImpl().onEntityPositionMove(event);
                this.onPlayerEntityUpdate((PlayerEntity) var3);
            }
        }
    }

    public PositionPredict() {
        super("PositionPredict");
        this.attackPredictArgument = this.builder(
                        this.Ju.add("attack-predict-argument"), PositionPredict$PredictArgument.class)
                .defaultValue(new PositionPredict$PredictArgument(2.0, 5, PositionPredict$Mode.NO_PREDICT))
                .build();
        this.flyPredictArgument = this.builder(
                        this.Ju.add("fly-predict-argument"), PositionPredict$PredictArgument.class)
                .defaultValue(new PositionPredict$PredictArgument(2.0, 5, PositionPredict$Mode.PREDICTOR_NV))
                .build();
        this.spearPredictArgument = this.builder(
                        this.Ju.add("spear-predict-argument"), PositionPredict$PredictArgument.class)
                .defaultValue(new PositionPredict$PredictArgument(2.0, 5, PositionPredict$Mode.PREDICTOR_NV))
                .build();
        this.exactTpAntiShield = this.builder(this.KE.add("exact-tp-anti-shield"), Boolean.class)
                .defaultValue(false)
                .build();
        this.debugRenderPrediction =
                this.flagBuilder(this.Ju.add("debug-render-prediction")).build();
        this.Mw = new Int2ObjectArrayMap();
        this.placeRecorder = this.flagBuilder(this.KE.add("place-recorder"))
                .updateListener(s -> this.Mw.clear())
                .build();
        this.placeRecorderHotkey = this.moduleEntry(
                        this.KE.add("place-recorder-hotkey"), new MultiKeyBind(), this.KE.add("place-recorder"))
                .build();
        INSTANCE = this;
    }

    public void onRender(Event<MatrixStack> event) {
        if (this.debugRenderPrediction.get()) {
            RenderUtils.startDrawVirtual((MatrixStack) event.b);

            try {
                ArrayList<Box> var2 = new ArrayList();
                Vec3d var3 = RenderUtils.getCameraPos().negate();

                for (AbstractClientPlayerEntity var5 : mc.world.getPlayers()) {
                    if (var5 != mc.getCameraEntity()) {
                        Vec3d var6 = this.flyPredictArgument.get().predict(var5);
                        var2.add(mc.player.dimensions.getBoxAt(var6).offset(var3));
                    }
                }

                VRender.getInstance().h((operation, vertexConsumer) -> {
                    for (Box var5x : var2) {
                        operation.a(
                                (MatrixStack) event.b,
                                vertexConsumer,
                                var5x.getMinPos(),
                                var5x.getMaxPos(),
                                Color.MAGENTA.getRGB());
                    }
                });
                ObjectIterator var11 = this.Mw.int2ObjectEntrySet().iterator();

                while (var11.hasNext()) {
                    Entry var12 = (Entry) var11.next();
                    List<Vec3d> var13 = (List) var12.getValue();
                    int var7 = ColorUtils.j(
                            Hashing.sha256().hashInt(var12.getIntKey()).hashCode(), 255);
                    VRender.getInstance().h((operation, vertexConsumer) -> {
                        for (Vec3d var7x : var13) {
                            var7x = var7x.add(var3);
                            operation.a(
                                    (MatrixStack) event.b,
                                    vertexConsumer,
                                    var7x.add(-0.2, -0.2, -0.2),
                                    var7x.add(0.2, 0.2, 0.2),
                                    var7);
                        }
                    });
                    VRender.getInstance()
                            .i((operation, vertexConsumer) -> operation.drawLines(
                                    (MatrixStack) event.b,
                                    vertexConsumer,
                                    var13.stream().map(s -> s.add(var3)).toList(),
                                    var7));
                }
            } finally {
                RenderUtils.stopDrawVirtual((MatrixStack) event.b);
            }
        }
    }

    public boolean considerAntiShield(Entity target) {
        return this.exactTpAntiShield.get()
                && target instanceof LivingEntity var2
                && var2.isUsingItem()
                && var2.getActiveItem().getItem() instanceof ShieldItem;
    }

    public Predictor acJ(Entity entity) {
        return EntityInternalAccess.of(entity).getPositionPredictor();
    }

    public Vec3d getExactAttackPosition(Entity target) {
        if (mc.player == null) {
            return null;
        } else if (target instanceof ShulkerEntity) {
            Vec3d var11 = target.getPos();
            Box var12 = target.getBoundingBox();

            for (Direction var7 : Direction.values()) {
                Vec3d var8 =
                        switch (var7) {
                            case UP -> var11.withAxis(Axis.Y, var12.maxY + 0.1);
                            case DOWN -> var11.withAxis(Axis.Y, var12.minY - 2.0);
                            case NORTH -> var11.withAxis(Axis.Z, var12.minZ - 0.5);
                            case SOUTH -> var11.withAxis(Axis.Z, var12.maxZ + 0.5);
                            case EAST -> var11.withAxis(Axis.X, var12.maxX + 0.5);
                            case WEST -> var11.withAxis(Axis.X, var12.minX - 0.5);
                            default -> throw new MatchException(null, null);
                        };
                if (!MovTasks.h.checkEnvironmentCollision(mc.player, var8, true)) {
                    return var8;
                }
            }

            return null;
        } else {
            boolean var2 = this.considerAntiShield(target);
            Vec3d var3;
            if (var2) {
                var3 = target.getRotationVector().normalize().multiply(-0.2);
            } else if (target instanceof PlayerEntity var4) {
                PositionPredict$PredictArgument var5 = this.attackPredictArgument.get();
                Vec3d var6 = var5.predict(var4);
                var3 = var6.subtract(target.getPos());
            } else {
                Vec3d var15 = mc.player.getPos().subtract(target.getPos());
                Vec3d var18 = new Vec3d(var15.x, 0.0, var15.z);
                double var9 = 0.5;
                var3 = var18.normalize().multiply(var9);
            }

            Vec3d var13 = target.getPos();
            Vec3d var16 = MovTasks.h.simulateMovement(mc.player, var13, var3);
            return var13.add(var16);
        }
    }

    public void onPostEntityPos(Event<EntityPositionS2CPacket> event) {
        if (!checkNull()) {
            if (mc.world.getEntityById(((EntityPositionS2CPacket) event.b).getEntityId())
                    instanceof PlayerInternalAccess var3) {
                var3.getPredictorImpl().onEntityPositionPost(event);
                this.onPlayerEntityUpdate((PlayerEntity) var3);
            }
        }
    }

    public void onPlayerEntityUpdate(PlayerEntity player) {
        if (this.placeRecorder.get()) {
            ((List) (Object) this.Mw.computeIfAbsent(player.getId(), v -> new ArrayList())).add(player.getPos());
        }
    }

    public Vec3d acK(Entity entity) {
        return EntityInternalAccess.of(entity).getPositionPredictor().getKnownDeltaMovement();
    }

    public Vec3d predictAimPositionForEntity(Entity entity, float finalVelocity) {
        Vec3d var3 = entity.getPos().subtract(mc.player.getPos());
        double var4 = var3.length() / finalVelocity;
        int var6;
        if (var4 < 2.0) {
            var6 = 0;
        } else if (var4 > 20.0) {
            var6 = 20;
        } else {
            var6 = (int) (var4 - 2.0);
        }

        return entity.getEyePos()
                .subtract(entity.getPos())
                .multiply(0.75)
                .add(this.flyPredictArgument.get().predictWithExtraTicks(entity, var6));
    }
}
