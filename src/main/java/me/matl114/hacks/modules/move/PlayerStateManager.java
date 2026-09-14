package me.matl114.hacks.modules.move;

import com.google.common.collect.Streams;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.accessors.access.PlayerMoveC2SPacketAccess;
import me.matl114.accessors.interfaces.MetadataHolder;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.channels.EventChannel;
import me.matl114.events.impl.SlotClickAction;
import me.matl114.hacks.ACTasks;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hooks.ViaFabricPlusHooks;
import me.matl114.managers.Tasks;
import me.matl114.utils.CollisionUtil;
import me.matl114.utils.ColorUtils;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.containers.KalamaHelperHelperB;
import me.matl114.utils.entity.PlayerInputUtils;
import me.matl114.utils.entity.PlayerInputUtils$Input;
import me.matl114.utils.inventory.ItemStackSample;
import me.matl114.versioned.api.VPacket;
import me.matl114.versioned.api.VRecord;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.FoodComponent.StatusEffectEntry;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.data.DataTracker.SerializedEntry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class PlayerStateManager extends BaseModule {
    private boolean jF;
    private static final Int2ObjectOpenHashMap<RegistryEntry<StatusEffect>> jW = new Int2ObjectOpenHashMap();
    public boolean jG;
    public Vec3d jq = Vec3d.ZERO;
    public boolean hB;
    public Map<ItemStackSample, Integer> jN;
    public static final EventChannel<PlayerEntity> jU = new EventChannel<>();
    public Vec3d jr = Vec3d.ZERO;
    public boolean jI;
    public Vec3d js = Vec3d.ZERO;
    public double ji;
    public static final EventChannel<PlayerEntity> jV = new EventChannel<>();
    public boolean jA;
    public double jh;
    private static final int MAX_SIZE = 20;
    boolean jw;
    public static PlayerStateManager INSTANCE;
    public BlockPos jL;
    private static final String jY = "kalama:player_manager/tracking_linger_potion_type";
    private static final List<StatusEffectInstance> jX;
    public boolean jx;
    public boolean jv;
    public boolean jH;
    public static final EquipmentSlot[] ARMOR =
            new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    public float jl;
    public boolean jy;
    public Deque<Vec3d> jK;
    public boolean jB;
    public Vec3d jt = Vec3d.ZERO;
    public Vec3d ju = Vec3d.ZERO;
    public boolean jo;
    public boolean jD;
    public Map<ItemStackSample, Integer> jM;
    public static final String jR = "kalama:player_manager/player_status";
    public PlayerInputUtils$Input jJ;
    public float jm;
    public boolean jC;
    public int jO;
    public double jk;
    public boolean jE;
    public boolean jn;
    public double jj;
    private int jQ;
    double jg;
    public boolean jp;
    private final Map<UUID, Integer> jT;
    public boolean jz;

    public StatusEffectInstance withScaledDuration(StatusEffectInstance instance, float durationMultiplier) {
        StatusEffectInstance var3 = new StatusEffectInstance(instance);
        var3.duration = var3.mapDuration(duration -> Math.max(MathHelper.floor(duration * durationMultiplier), 1));
        return var3;
    }

    public void nH() {
        if (this.jh > 1.5) {
            this.jh = 0.0;
        }
    }

    public static void setPlayerYawSafe(PlayerEntity entity, Vec2f vec2f) {
        nT(entity, (float) Math.toDegrees(Math.atan2(-vec2f.x, vec2f.y)));
    }

    public void onEntityConsume(Event<EntityStatusS2CPacket> eventEntityStatusS2CPacket) {
        if (!checkNull()) {
            EntityStatusS2CPacket var2 = (EntityStatusS2CPacket) eventEntityStatusS2CPacket.b;
            if (var2.getEntity(mc.world) instanceof PlayerEntity var4 && var2.getStatus() == 35) {
                MoveSubHelperL var7 = this.nX(var4);

                for (StatusEffectInstance var6 : jX) {
                    var7.i
                            .computeIfAbsent(var6.getEffectType(), MoveSubHelperYX::new)
                            .refresh(var6);
                }
            }
        }
    }

    public void restoreLastRotation(PlayerEntity player) {
        if (this.nQ(player.getPitch(), player.getYaw())) {
            mc.player.setPitch(this.jl);
            mc.player.setYaw(this.jm);
        }
    }

    public void nL(Event<CloseHandledScreenC2SPacket> event) {
        this.jQ = 100;
    }

    public void handleY(double y, boolean onGround) {
        if (!mc.player.isTouchingWater()) {
            if (this.jz = this.checkRegionFluid(FluidTags.WATER)) {
                this.jh = 0.0;
            }
        } else {
            this.jh = 0.0;
        }

        if (this.jk > y && !mc.player.isTouchingWater()) {
            this.jh = this.jh + (this.jk - y);
        }

        if (onGround) {
            this.nw();
        }

        if (this.jk < y) {
            this.jg = y;
            this.jh = 0.0;
        }

        this.nv();
    }

    static {
        for (StatusEffect var1 : Registries.STATUS_EFFECT) {
            RegistryEntry var2 = Registries.STATUS_EFFECT.getEntry(var1);
            int var3 = var1.getColor();
            jW.put(var3, var2);
        }

        jX = List.of(
                new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1),
                new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1),
                new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
    }

    public void nv() {
        if (this.jh < 0.0) {
            this.jh = 0.0;
        }

        if (this.jh > 0.0) {}
    }

    public void nK(Event<ScreenHandlerSlotUpdateS2CPacket> event) {
        this.jQ = 100;
    }

    public void onOtherPlayerRemoveDeath(Event<Entity> eventRemoval) {
        if (eventRemoval.b instanceof PlayerEntity var3 && var3.getHealth() <= 0.0F && var3 != mc.player) {
            this.onDeath(var3);
        }
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.ap().getChannel(PlayerMoveC2SPacket.class), this::onMove, Integer.MAX_VALUE);
        this.registerListener(
                Listener.ar().getChannel(EntityVelocityUpdateS2CPacket.class),
                this::onPostPlayerVelocityUpdate,
                Integer.MAX_VALUE);
        this.registerListener(
                Listener.ar().getChannel(ExplosionS2CPacket.class), this::onPostPlayerExplosion, Integer.MAX_VALUE);
        this.registerListener(Listener.ap().getChannel(PlayerInputC2SPacket.class), this::no, Integer.MAX_VALUE);
        this.registerListener(
                Listener.ar().getChannel(PlayerPositionLookS2CPacket.class),
                this::onPostPlayerPositionLook,
                Integer.MAX_VALUE);
        this.registerListener(Listener.ay(), this::ny);
        this.registerListener(Listener.az(), this::handleInFluid);
        this.registerListener(Listener.U(), this::nA);
        this.registerListener(Listener.as(), this::np, Integer.MAX_VALUE);
        this.registerListener(
                Listener.ap().getChannel(EntityDamageS2CPacket.class), this::onEntityAttackEvent, Integer.MAX_VALUE);
        this.registerListener(
                Listener.ap().getChannel(ClientCommandC2SPacket.class), this::onPlayerCommand, Integer.MAX_VALUE);
        this.registerListener(Listener.aE(), this::nq);
        this.registerListener(Listener.V(), this::onTickEnd, Integer.MAX_VALUE);
        this.registerListener(Listener.U(), this::updateOtherPlayers);
        this.registerListener(Listener.ap().getChannel(EntityStatusS2CPacket.class), this::onTotemPop);
        this.registerListener(Listener.O(), this::oe);
        this.registerListener(Listener.aU().c(EntityType.PLAYER), this::onOtherPlayerRemoveDeath);
        this.registerListener(Listener.al(), this::nI);
        this.registerListener(Listener.ap().getChannel(InventoryS2CPacket.class), this::nJ);
        this.registerListener(Listener.ap().getChannel(ScreenHandlerSlotUpdateS2CPacket.class), this::nK);
        this.registerListener(Listener.ap().getChannel(CloseHandledScreenC2SPacket.class), this::nL);
        this.registerListener(Listener.ap().getChannel(PlayerRespawnS2CPacket.class), this::onPrePlayerSendMovePacket);
        this.registerListener(Listener.au().c(EntityType.PLAYER), this::onEntityTrackedDataUpdate);
        this.registerListener(Listener.ap().getChannel(EntityStatusS2CPacket.class), this::onEntityConsume);
        this.registerListener(Listener.aU().c(EntityType.POTION), this::onSplashedPotionHit);
        this.registerListener(Listener.aU().c(EntityType.POTION), this::onLingerPotionHit);
        this.registerListener(Listener.aO().c(EntityType.AREA_EFFECT_CLOUD), this::onAreaEffectCloudTick);
        this.registerListener(Listener.ar().getChannel(EntityStatusEffectS2CPacket.class), this::onEntityEffect);
        this.registerListener(
                Listener.ar().getChannel(EntityEquipmentUpdateS2CPacket.class), this::onEntityEquipmentUpdate);
        this.registerListener(Listener.aL(), this::onPlayerLeave);
    }

    private Stream<ItemStack> streamItems(ItemStack stack) {
        return Streams.concat(
                new Stream[] {Stream.of(stack), this.streamInvContent(stack).flatMap(this::streamItems)});
    }

    public void onLingerPotionHit(Event<PotionEntity> eventLinger) {
        if (!checkNull()) {
            RemovalReason var2 = eventLinger.getArgs(0);
            if (var2.shouldDestroy()) {
                PotionEntity var3 = (PotionEntity) eventLinger.b;
                ItemStack var4 = var3.getStack();
                if (var4.isEmpty() || !var4.isOf(Items.LINGERING_POTION)) {
                    return;
                }

                PotionContentsComponent var5 = (PotionContentsComponent) var4.get(DataComponentTypes.POTION_CONTENTS);
                if (var5 == null || Objects.equals(var5, PotionContentsComponent.DEFAULT)) {
                    return;
                }

                float var6 = 0.25F;
                Vec3d var7 = var3.getPos();
                int var8 = Tasks.b();
                Box var9 = new Box(var7.add(-0.2, -0.2, -0.2), var7.add(0.2, 0.2, 0.2));
                Tasks.m(
                        () -> {
                            if (checkNull()) {
                                return true;
                            } else if (Tasks.b() > var8 + 20) {
                                return true;
                            } else {
                                List<AreaEffectCloudEntity> var5x =
                                        mc.world.getNonSpectatingEntities(AreaEffectCloudEntity.class, var9);
                                if (var5x.isEmpty()) {
                                    return false;
                                } else {
                                    for (AreaEffectCloudEntity var7x : var5x) {
                                        if (var7x instanceof MetadataHolder var8x) {
                                            var8x.getMetadata()
                                                    .a(
                                                            this,
                                                            "kalama:player_manager/tracking_linger_potion_type",
                                                            Pair.of(var5, var6));
                                        }
                                    }

                                    return true;
                                }
                            }
                        },
                        1,
                        1);
            }
        }
    }

    public static float getToleranceMargin(Entity entity) {
        return Math.max(0.0F, Math.min(0.3F, (entity.age - 2) / 20.0F));
    }

    public void sendSprintStatus(boolean sprint) {
        if (sprint != this.jp) {
            if (sprint) {
                mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.START_SPRINTING));
            } else {
                mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.STOP_SPRINTING));
            }

            ClientPlayerAccess.of(mc.player).setLastSprintFlag(sprint);
        }
    }

    public void onPlayerCommand(Event<ClientCommandC2SPacket> event) {
        if (!event.d()) {
            switch (((ClientCommandC2SPacket) event.b).getMode()) {
                case START_SPRINTING:
                    this.jp = true;
                    break;
                case STOP_SPRINTING:
                    this.jp = false;
            }
        }
    }

    private MoveSubHelperL nX(PlayerEntity pl) {
        KalamaHelperHelperB var2 = ((MetadataHolder) pl).getMetadata();
        return var2.c(this, "kalama:player_manager/player_status", MoveSubHelperL::new);
    }

    public void nu() {}

    public void nJ(Event<InventoryS2CPacket> event) {
        this.jQ = 100;
    }

    public void nq(Event<ClientPlayerEntity> event) {
        this.onPlayerReset();
    }

    public static EventChannel<PlayerEntity> or() {
        return jU;
    }

    private BlockPos calculateVelocityAffectingPos() {
        BlockPos var1 = mc.player.getVelocityAffectingPos();
        BlockState var2 = mc.world.getBlockState(var1);
        if (!var2.isAir() && !var2.isLiquid()) {
            return var1;
        } else {
            Box var3 = mc.player.getBoundingBox();
            int var4 = (int) Math.floor(var3.minX);
            int var5 = (int) Math.floor(var3.maxX - 1.0E-7);
            int var6 = (int) Math.floor(var3.minZ);
            int var7 = (int) Math.floor(var3.maxZ - 1.0E-7);
            int var8 = var1.getY();
            boolean var9 = false;

            label44:
            for (int var10 = var4; var10 <= var5; var10++) {
                for (int var11 = var6; var11 <= var7; var11++) {
                    BlockPos var12 = new BlockPos(var10, var8, var11);
                    BlockState var13 = mc.world.getBlockState(var12);
                    if (!var13.isAir() && !var13.isLiquid()) {
                        var9 = true;
                        break label44;
                    }
                }
            }

            if (!var9) {
                return var1;
            } else {
                Box var14 = var3.offset(0.0, 0.500001F, 0.0);

                for (BlockPos var17 : CollisionUtil.getIntersectingBlockPositions(mc.world, var14, false)) {
                    if (var1.getY() == var17.getY()) {
                        return var17;
                    }
                }

                return var1;
            }
        }
    }

    public void handleInFluid(Event<Vec3d> vec3dEvent) {
        TagKey var2 = vec3dEvent.getArgs(0);
        if (Objects.equals(FluidTags.WATER, var2)) {
            this.jB = true;
            this.jA = true;
        } else if (Objects.equals(FluidTags.LAVA, var2)) {
            this.jC = true;
            this.jD = true;
        }
    }

    private MoveSubHelperL nY(PlayerEntity entity) {
        return entity instanceof MetadataHolder var2 && !var2.isMetaEmpty()
                ? var2.getMetadata().b(this, "kalama:player_manager/player_status")
                : null;
    }

    public void onEntityTrackedDataUpdate(Event<SerializedEntry<?>> eventDataUpdate) {
        if (eventDataUpdate.getArgs(0) instanceof PlayerEntity var3) {
            if (((SerializedEntry) eventDataUpdate.b).id() == 10
                    && ((SerializedEntry) eventDataUpdate.b).value() instanceof List<?> var14) {
                MoveSubHelperL var17 = this.nX(var3);
                HashSet<RegistryEntry> var18 = new HashSet<>(var17.i.keySet());

                for (Object var21 : var14) {
                    if (var21 instanceof EntityEffectParticleEffect var9) {
                        int var10 = ColorUtils.j(var9.color, 0);
                        RegistryEntry var11 = (RegistryEntry) jW.get(var10);
                        if (var11 != null) {
                            var18.remove(var11);
                            MoveSubHelperYX var12 = var17.i.computeIfAbsent(var11, MoveSubHelperYX::new);
                            if (!var12.hasInitialized()) {
                                var12.a = Tasks.b();
                            }

                            var12.visible = true;
                        }
                    }
                }

                for (RegistryEntry var22 : var18) {
                    var17.i.remove(var22);
                }
            } else if (((SerializedEntry) eventDataUpdate.b).id() == 8
                    && ((SerializedEntry) eventDataUpdate.b).value() instanceof Number var13) {
                byte var16 = var13.byteValue();
                MoveSubHelperL var5 = this.nX(var3);
                boolean var6 = (var16 & 0) > 0;
                if (!var6 && var3.isUsingItem()) {
                    ItemStack var7 = var5.e;
                    Hand var8 = var5.f;
                    if (var7 != null && var8 != null && !var7.isEmpty()) {
                        Tasks.l(
                                () -> {
                                    ItemStack var4 = var3.getStackInHand(var8);
                                    if (var7.getCount() > 1 && ItemStack.areItemsAndComponentsEqual(var7, var4)
                                            || var7.getCount() <= 1 && var4.getItem() != var7.getItem()) {
                                        FoodComponent var6x = (FoodComponent) var7.get(DataComponentTypes.FOOD);
                                        if (var6x != null) {
                                            PotionContentsComponent var7x = (PotionContentsComponent)
                                                    var7.get(DataComponentTypes.POTION_CONTENTS);
                                            if (var7x != null) {
                                                var7x.forEachEffect(instance -> {
                                                    if (!((StatusEffect) instance.getEffectType()
                                                                    .value())
                                                            .isInstant()) {
                                                        var5.i
                                                                .computeIfAbsent(
                                                                        instance.getEffectType(), MoveSubHelperYX::new)
                                                                .refresh(instance);
                                                    }
                                                });
                                            }

                                            if (!var6x.effects().isEmpty()) {
                                                for (StatusEffectEntry var9x : var6x.effects()) {
                                                    if (var9x.probability() > 0.0F) {
                                                        var5.i
                                                                .computeIfAbsent(
                                                                        var9x.effect()
                                                                                .getEffectType(),
                                                                        MoveSubHelperYX::new)
                                                                .refresh(var9x.effect());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                },
                                1);
                    }
                }
            }
        }
    }

    public void onPostPlayerPositionLook(Event<PlayerPositionLookS2CPacket> eventPositionLook) {
        if (!checkNull()) {
            if (!mc.player.hasVehicle()) {
                if (((PlayerPositionLookS2CPacket) eventPositionLook.b).getTeleportId() >= 0
                        && !ViaFabricPlusHooks.isSupportEndTick()) {
                    Set var2 = ((PlayerPositionLookS2CPacket) eventPositionLook.b).getFlags();
                    double var3 = var2.contains(PositionFlag.X) ? this.js.x : 0.0;
                    double var5 = var2.contains(PositionFlag.Y) ? this.js.y : 0.0;
                    double var7 = var2.contains(PositionFlag.Z) ? this.js.z : 0.0;
                    this.js = new Vec3d(var3, var5, var7);
                }
            }
        }
    }

    public int getPlayerPopCount(PlayerEntity entity) {
        Integer var2 = this.jT.get(entity.getUuid());
        return var2 == null ? 0 : var2;
    }

    public void onPrePlayerSendMovePacket(Event<PlayerRespawnS2CPacket> eventRespawn) {
        if (!checkNull()) {
            this.onDeath(mc.player);
        }
    }

    public void np(Event<ClientPlayerEntity> eventPre) {
        if (this.isRotationDifferent()) {
            ClientPlayerAccess.of(mc.player).setLastRot(this.jl, this.jm);
        }
    }

    public void handleMove(Vec3d pos, boolean onGround) {
        this.handleY(pos.getY(), onGround);
        this.jk = pos.getY();
        this.ji = pos.getX();
        this.jj = pos.getZ();
        this.jn = onGround;
    }

    public boolean isRotationDifferent() {
        return EntityUtils.isRotationDifferent(this.jl, mc.player.getPitch(), this.jm, mc.player.getYaw());
    }

    private void onDeath(PlayerEntity entity) {
        Integer var2 = this.jT.remove(entity.getUuid());
        MoveSubHelperL var3 = this.nZ(entity);
        jV.h(entity, var3, var2);
    }

    public static EventChannel<PlayerEntity> os() {
        return jV;
    }

    public void onTickEnd(Event<ClientPlayerEntity> tickEndPacket) {
        if (!this.jw) {
            this.jq = Vec3d.ZERO;
            this.jv = false;
        }

        this.jw = false;
    }

    public void onPostPlayerExplosion(Event<ExplosionS2CPacket> eventBoom) {
        if (!checkNull()) {
            ExplosionS2CPacket var2 = (ExplosionS2CPacket) eventBoom.b;
            Vec3d var3 = new Vec3d(var2.getPlayerVelocityX(), var2.getPlayerVelocityY(), var2.getPlayerVelocityZ());
            if (var3.lengthSquared() > 1.0E-4) {
                ACTasks.c(s -> this.js = this.js.add(var3));
            }
        }
    }

    private void onEntityEquipmentUpdate(Event<EntityEquipmentUpdateS2CPacket> eventUpdate) {
        if (!checkNull()) {
            if (mc.world.getEntityById(((EntityEquipmentUpdateS2CPacket) eventUpdate.b).getEntityId())
                    instanceof PlayerEntity var3) {
                for (Pair var4 : ((EntityEquipmentUpdateS2CPacket) eventUpdate.b).getEquipmentList()) {
                    if (!((ItemStack) var4.getSecond()).isEmpty()) {
                        ItemStack var5 =
                                ItemStackUtils.getCleanedItem((ItemStack) var4.getSecond(), 1, true, false, true);
                        this.nX(var3).j.add(new ItemStackSample(var5));
                    }
                }
            }
        }
    }

    public MoveSubHelperL nZ(PlayerEntity entity) {
        MoveSubHelperL var2 = this.nY(entity);
        if (var2 != null && var2.a < Tasks.b() - 10) {
            var2 = null;
        }

        return var2;
    }

    public Vec3d nR() {
        return EntityUtils.pitchYawToRotation(this.jl, this.jm);
    }

    public void no(Event<PlayerInputC2SPacket> eventInput) {
        if (!eventInput.d()) {
            if (ViaFabricPlusHooks.isSupportEndTick()) {
                this.jJ = PlayerInputUtils.d((PlayerInputC2SPacket) eventInput.b);
            }
        }
    }

    public void nM() {
        this.jh = 0.0;
    }

    public void onEntityAttackEvent(Event<EntityDamageS2CPacket> eventS2C) {
        if (mc.player != null && ((EntityDamageS2CPacket) eventS2C.b).sourceCauseId() == mc.player.getId()) {
            RegistryKey var2 = (RegistryKey)
                    ((EntityDamageS2CPacket) eventS2C.b).sourceType().getKey().orElse(null);
            if (me.matl114.utils.KalamaHelperHelperB.a(var2, "mace_smash")) {
                this.nH();
            }
        }

        if (mc.player != null && ((EntityDamageS2CPacket) eventS2C.b).entityId() == mc.player.getId()) {
            RegistryKey var3 = (RegistryKey)
                    ((EntityDamageS2CPacket) eventS2C.b).sourceType().getKey().orElse(null);
            if (me.matl114.utils.KalamaHelperHelperB.a(var3, "ender_pearl")) {
                this.nM();
            }
        }
    }

    public void updateOtherPlayers(Event<ClientPlayerEntity> eventUpdate) {
        for (AbstractClientPlayerEntity var3 : mc.world.getPlayers()) {
            if (var3 instanceof MetadataHolder var4) {
                MoveSubHelperL var5 = this.nX(var3);
                var5.tickUpdate(var3);
            }
        }
    }

    public void onAreaEffectCloudTick(Event<AreaEffectCloudEntity> eventCloud) {
        if (!checkNull()) {
            if (Tasks.b() % 5 == 0) {
                AreaEffectCloudEntity var2 = (AreaEffectCloudEntity) eventCloud.b;
                float var3 = var2.getRadius();
                this.performCloudUpdate(var2, var3);
            }
        }
    }

    public boolean nQ(float pitch, float yaw) {
        return EntityUtils.isRotationDifferent(this.jl, pitch, this.jm, yaw);
    }

    public static void setPlayerRotationSafe(PlayerEntity entity, Vec3d vec) {
        vec = vec.normalize();
        EntityUtils.setEntityPitchSafe(entity, (float) Math.toDegrees(Math.asin(-vec.y)));
        float var2 = (float) Math.toDegrees(Math.atan2(-vec.x, vec.z));
        nT(entity, var2);
    }

    public void onEntityEffect(Event<EntityStatusEffectS2CPacket> event) {
        if (!checkNull()) {
            if (mc.world.getEntityById(((EntityStatusEffectS2CPacket) event.b).getEntityId())
                    instanceof PlayerEntity var3) {
                MoveSubHelperL var6 = this.nX(var3);

                for (StatusEffectInstance var5 : var3.getStatusEffects()) {
                    var6.i
                            .computeIfAbsent(var5.getEffectType(), MoveSubHelperYX::new)
                            .refresh(var5);
                }
            }
        }
    }

    public void onPlayerReset() {
        this.jg = Double.MIN_VALUE;
        this.jh = 0.0;
        this.jq = new Vec3d(0.0, 0.0, 0.0);
        this.jt = new Vec3d(0.0, 0.0, 0.0);
        this.ju = new Vec3d(0.0, 0.0, 0.0);
        this.jK.clear();

        for (int var1 = 0; var1 < 20; var1++) {
            this.jK.add(Vec3d.ZERO);
        }

        this.ji = 0.0;
        this.jk = 0.0;
        this.jj = 0.0;
        this.jn = false;
        this.jo = false;
        this.jl = 0.0F;
        this.jm = 0.0F;
        this.jp = false;
        this.jJ = PlayerInputUtils.a.rw();
        this.jL = BlockPos.ORIGIN;
        this.jN = null;
        this.jM = null;
        this.jO = 0;
    }

    public void onPlayerLeave(Event<PlayerListEntry> eventRemove) {
        this.jT.remove(VRecord.getId(((PlayerListEntry) eventRemove.b).getProfile()));
    }

    public void onTotemPop(Event<EntityStatusS2CPacket> event) {
        if (!checkNull()) {
            EntityStatusS2CPacket var2 = (EntityStatusS2CPacket) event.b;
            if (var2.getEntity(mc.world) instanceof PlayerEntity var4) {
                if (var2.getStatus() == 35) {
                    UUID var6 = var4.getUuid();
                    int var5 = this.jT.merge(var6, 1, Integer::sum);
                    jU.h(var4, var5);
                }

                if (var2.getStatus() == 3) {
                    this.onDeath(var4);
                }
            }
        }
    }

    public static void nT(PlayerEntity player, float yaw) {
        float var2 = EntityUtils.i(INSTANCE.jm, yaw);
        player.setYaw(var2);
    }

    public void handleTick() {
        this.jy = mc.player.isInLava();
        this.jz = this.checkRegionFluid(FluidTags.WATER);
        this.jx = mc.player.isClimbing();
        this.jE = this.jF;
        this.jF = false;
        this.jA = this.jB;
        this.jB = false;
        if (!this.jz && mc.player.isTouchingWater()) {
            mc.player.touchingWater = false;
        }

        this.jD = this.jC;
        this.jC = false;
        this.jG = MovTasks.u(mc.player);
        Box var1 = mc.player.getBoundingBox();
        this.jH = MovTasks.isCollidingWithEnvironment(
                mc.player, var1.withMinY(var1.maxY).withMaxY(var1.maxY + 0.42));
        this.jI = CollisionUtil.isEntitySupported(mc.player, 0.001);
        this.jL = this.calculateVelocityAffectingPos();
        if (++this.jQ > 10 || this.jM == null || this.jN == null) {
            this.jQ = 0;
            LinkedHashMap<ItemStackSample, Integer> var2 = new LinkedHashMap<>();
            mc.player.getInventory().main.stream()
                    .filter(v -> !v.isEmpty())
                    .forEach(s -> var2.merge(ItemStackSample.of(s), s.getCount(), Integer::sum));
            this.jM = var2;
            LinkedHashMap<ItemStackSample, Integer> var3 = new LinkedHashMap<>(var2.size());

            for (Entry<ItemStackSample, Integer> var5 : var2.entrySet()) {
                int var6 = var5.getValue();
                this.streamItems(var5.getKey().fS())
                        .filter(v -> !v.isEmpty())
                        .forEach(s -> var3.merge(ItemStackSample.of(s), s.getCount() * var6, Integer::sum));
            }

            this.jN = var3;
        }

        Vec3d var7 = new Vec3d(this.ji, this.jk, this.jj);
        this.jK.addLast(var7);
        Vec3d var8 = null;

        while (this.jK.size() > 20) {
            var8 = this.jK.removeFirst();
        }

        if (var8 != null) {
            this.jt = var7.subtract(var8).multiply(0.05);
        }

        if (this.jy) {
            this.jh *= 0.5;
        }

        if (this.jz) {
            this.jh = 0.0;
        }

        if (mc.player.hasVehicle()) {
            this.jh = 0.0;
        }

        if (mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING)
                || mc.player.hasStatusEffect(StatusEffects.LEVITATION)) {
            this.jh = 0.0;
        }

        if (this.jx) {
            this.jh = 0.0;
        }

        if (mc.player.isFallFlying()) {
            this.jO++;
        } else {
            this.jO = 0;
        }
    }

    public boolean checkRegionFluid(TagKey<Fluid> tag) {
        if (mc.player.isRegionUnloaded()) {
            return false;
        } else {
            Box var2 = mc.player.getBoundingBox().contract(0.001);
            int var3 = MathHelper.floor(var2.minX);
            int var4 = MathHelper.ceil(var2.maxX);
            int var5 = MathHelper.floor(var2.minY);
            int var6 = MathHelper.ceil(var2.maxY);
            int var7 = MathHelper.floor(var2.minZ);
            int var8 = MathHelper.ceil(var2.maxZ);
            double var9 = 0.0;
            boolean var11 = false;
            Mutable var12 = new Mutable();

            for (int var13 = var3; var13 < var4; var13++) {
                for (int var14 = var5; var14 < var6; var14++) {
                    for (int var15 = var7; var15 < var8; var15++) {
                        var12.set(var13, var14, var15);
                        FluidState var16 = mc.world.getFluidState(var12);
                        if (var16.isIn(tag)) {
                            double var17 = var14 + var16.getHeight(mc.world, var12);
                            if (var17 >= var2.minY) {
                                var11 = true;
                                return var11;
                            }
                        }
                    }
                }
            }

            return var11;
        }
    }

    private Stream<ItemStack> streamInvContent(ItemStack stack) {
        ContainerComponent var2 = (ContainerComponent) stack.get(DataComponentTypes.CONTAINER);
        return var2 == null ? Stream.empty() : var2.stream();
    }

    public void onMove(Event<PlayerMoveC2SPacket> event) {
        if (!event.d()) {
            PlayerMoveC2SPacket var2 = (PlayerMoveC2SPacket) event.b;
            if (PlayerMoveC2SPacketAccess.of(var2).getCause() != PlayerMoveC2SPacketAccess.Cause.TRIGGER_SIMULATION) {
                this.jv = var2.changesPosition();
                Vec3d var3 = new Vec3d(this.ji, this.jk, this.jj);
                if (!var2.changesPosition()) {
                    if (var2.isOnGround()) {
                        this.nw();
                    }
                } else {
                    Vec3d var4 = new Vec3d(var2.getX(this.ji), var2.getY(this.jk), var2.getZ(this.jj));
                    if (!containsInvalidValues(var4.x, var4.y, var4.z)) {
                        this.handleMove(var4, var2.isOnGround());
                    }
                }

                this.jn = var2.isOnGround();
                if (PlayerMoveC2SPacketAccess.of(var2).getCause() != PlayerMoveC2SPacketAccess.Cause.SET_BACK) {
                    this.jo = this.jn;
                }

                if (var2.changesLook()) {
                    this.jl = var2.getPitch(this.jl);
                    this.jm = var2.getYaw(this.jm);
                }

                this.jq = new Vec3d(this.ji - var3.x, this.jk - var3.y, this.jj - var3.z);
                if (PlayerMoveC2SPacketAccess.of(var2).getCause() != PlayerMoveC2SPacketAccess.Cause.LEGACY_SNAP) {
                    if (PlayerMoveC2SPacketAccess.of(var2).getCause() == PlayerMoveC2SPacketAccess.Cause.SET_BACK) {
                        this.jr = Vec3d.ZERO;
                    } else {
                        this.jr = this.jq;
                        this.js = this.jr;
                    }
                }

                this.jw = true;
            } else if (var2.changesLook()) {
                this.jl = var2.getPitch(this.jl);
                this.jm = var2.getYaw(this.jm);
            }

            if (!ViaFabricPlusHooks.isSupportEndTick()) {
                this.jJ = PlayerInputUtils.a(mc.player);
            }
        }
    }

    public void nA(Event<ClientPlayerEntity> event) {
        this.handleTick();
    }

    public void nI(Event<SlotClickAction> eventClick) {
        this.jQ = 100;
    }

    public void oe(Event<Void> event) {
        this.jT.clear();
    }

    public void ny(Event<Vec3d> vec3dEvent) {
        this.jh = 0.0;
        this.jE = true;
        this.jF = true;
    }

    public PlayerStateManager() {
        super("PlayerStateManager");
        this.jv = false;
        this.jw = false;
        this.jJ = PlayerInputUtils.a.rw();
        this.jK = new ArrayDeque<>();
        this.jL = BlockPos.ORIGIN;

        for (int var1 = 0; var1 < 20; var1++) {
            this.jK.add(Vec3d.ZERO);
        }

        this.jQ = 0;
        this.jT = new ConcurrentHashMap<>();
        INSTANCE = this;
    }

    public void nw() {
        if (!this.jn) {
            this.nu();
            this.jn = true;
            this.jo = true;
        }

        this.jh = 0.0;
    }

    public void onPostPlayerVelocityUpdate(Event<EntityVelocityUpdateS2CPacket> eventVC) {
        if (!checkNull()) {
            if (((EntityVelocityUpdateS2CPacket) eventVC.b).getEntityId() == mc.player.getId()) {
                Vec3d var2 = VPacket.getVelocity((EntityVelocityUpdateS2CPacket) eventVC.b);
                ACTasks.c(s -> this.js = var2);
            }
        }
    }

    public void onSplashedPotionHit(Event<PotionEntity> eventPotionEntity) {
        if (!checkNull()) {
            RemovalReason var2 = eventPotionEntity.getArgs(0);
            if (var2.shouldDestroy()) {
                PotionEntity var3 = (PotionEntity) eventPotionEntity.b;
                ItemStack var4 = var3.getStack();
                if (var4.isEmpty() || var4.isOf(Items.LINGERING_POTION)) {
                    return;
                }

                PotionContentsComponent var5 = (PotionContentsComponent) var4.get(DataComponentTypes.POTION_CONTENTS);
                if (var5 == null || Objects.equals(var5, PotionContentsComponent.DEFAULT)) {
                    return;
                }

                float var6 = 1.0F;
                Box var7 = var3.getBoundingBox();
                var7 = var7.expand(4.0, 2.0, 4.0);
                List<PlayerEntity> var8 = mc.world.getNonSpectatingEntities(PlayerEntity.class, var7);
                if (!var8.isEmpty()) {
                    float var9 = getToleranceMargin(var3);

                    for (PlayerEntity var11 : var8) {
                        if (!var11.isDead()) {
                            double var12 = MathUtils.squaredMagnitude(
                                    var7, var11.getBoundingBox().expand(var9));
                            if (!(var12 >= 16.0)) {
                                double var14 = Math.sqrt(var12);
                                double var16 = 1.0 - var14 / 4.0;

                                for (StatusEffectInstance var19 : var5.getEffects()) {
                                    RegistryEntry var20 = var19.getEffectType();
                                    StatusEffect var21 = (StatusEffect) var20.value();
                                    if (!var21.isInstant()) {
                                        int var22 = var19.getDuration();
                                        int var23 = (int) (var16 * var22 * var6 + 0.5);
                                        if (var23 >= 20) {
                                            StatusEffectInstance var24 = new StatusEffectInstance(
                                                    var20,
                                                    var23,
                                                    var19.getAmplifier(),
                                                    var19.isAmbient(),
                                                    var19.shouldShowParticles());
                                            this.nX(var11)
                                                    .i
                                                    .computeIfAbsent(var24.getEffectType(), MoveSubHelperYX::new)
                                                    .refresh(var24);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static CommandExecution oq() {
        return MoveSubHelperC.instance;
    }

    private void performCloudUpdate(AreaEffectCloudEntity cloud, float currentRadius) {
        if (cloud instanceof MetadataHolder var3 && !var3.isMetaEmpty()) {
            KalamaHelperHelperB var4 = var3.getMetadata();
            Pair var5 = var4.b(this, "kalama:player_manager/tracking_linger_potion_type");
            if (var5 != null) {
                List<PlayerEntity> var6 = mc.world.getNonSpectatingEntities(PlayerEntity.class, cloud.getBoundingBox());
                if (var6.isEmpty()) {
                    return;
                }

                ArrayList<StatusEffectInstance> var7 = new ArrayList();
                ((PotionContentsComponent) var5.getFirst())
                        .forEachEffect(v -> var7.add(this.withScaledDuration(v, (Float) var5.getSecond())));

                for (PlayerEntity var9 : var6) {
                    if (!var9.isDead()) {
                        double var10 = var9.getX() - cloud.getX();
                        double var12 = var9.getZ() - cloud.getZ();
                        if (!(var10 * var10 + var12 * var12 > currentRadius * currentRadius)) {
                            for (StatusEffectInstance var15 : var7) {
                                StatusEffect var16 =
                                        (StatusEffect) var15.getEffectType().value();
                                if (!var16.isInstant()) {
                                    this.nX(var9)
                                            .i
                                            .computeIfAbsent(var15.getEffectType(), MoveSubHelperYX::new)
                                            .refresh(var15);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean containsInvalidValues(double x, double y, double z) {
        return Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z);
    }
}
