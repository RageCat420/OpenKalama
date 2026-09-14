package me.matl114.hacks.modules.extra;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.combat.CombatExtra;
import me.matl114.hacks.utils.entity.HackUtilHelperD;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.CollisionUtil;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.InteractUtils;
import me.matl114.utils.NetworkUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class BoatVClip extends BaseModule implements HackUtilHelperJ {
    public final KeyBindRef J;
    Set<BlockPos> RQ;
    int cd;
    private static HackUtilHelperD instance;
    Map<BlockPos, BlockState> RP;
    Entity RR;
    public final ModulePath RO = makePath(Configs.j, "other.boat-vclip");
    public final FlagRef ae = this.flagBuilder(this.RO.addEnable()).build();
    Vec3d xz;

    public void onBlockUpdate(Event<BlockUpdateS2CPacket> update) {
        if (this.ae.get()) {
            BlockPos var2 = ((BlockUpdateS2CPacket) update.b).getPos();
            if (this.RP.containsKey(var2)) {
                update.cancel();
            }
        }
    }

    @Override
    public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
        if (this.ae.get()) {}

        return true;
    }

    public BoatVClip() {
        super("BoatVClip");
        this.J = this.toggleHotkey(this.RO.addHotkey(), new MultiKeyBind(), this.RO.addEnable())
                .build();
        this.RP = new HashMap<>();
        this.RQ = new HashSet<>();
        this.cd = 0;
        this.xz = Vec3d.ZERO;
        if (instance == null) {
            instance = new HackUtilHelperD(this::cast);
            MovTasks.j.SJ(() -> instance);
        }

        instance.mN(this::cast);
        this.bindFlag(this.ae);
    }

    public void applyPreTickModify(Event<LegalMovementManager> movementManagerEvent) {
        if (this.ae.get()) {
            if (!EntityUtils.isEntityValid(this.RR)
                    || this.RR.getBoundingBox().squaredMagnitude(mc.player.getEyePos())
                            > CombatExtra.INSTANCE.getAttackRange()) {
                this.RR = null;
            }

            if (this.RR == null) {
                this.RR =
                        mc
                                .world
                                .getOtherEntities(
                                        mc.player, mc.player.getBoundingBox().expand(1.5, 1.5, 1.5))
                                .stream()
                                .filter(s -> s instanceof VehicleEntity)
                                .findAny()
                                .orElse(null);
            }

            if (this.RR != null) {
                if (mc.player.hasVehicle()) {
                    this.xz = null;
                } else {
                    if (this.xz == null || this.xz.squaredDistanceTo(this.RR.getPos()) > 0.25) {
                        this.xz = this.RR.getPos();
                        this.RP.clear();
                        Box var2 = this.RR.getBoundingBox();
                        Box var3 = var2.withMinY(var2.minY - 0.5).withMaxY(var2.minY + 0.5);

                        for (BlockPos var6 : CollisionUtil.getIntersectingBlockPositions(mc.world, var3, false)) {
                            BlockState var7 = mc.world.getBlockState(var6);
                            if (!var7.isAir() && !var7.isLiquid()) {
                                this.RP.put(var6, var7);
                            }
                        }

                        for (Entry var14 : this.RP.entrySet()) {
                            BlockPos var15 = (BlockPos) var14.getKey();
                            Listener.sendPacketNoEvents(new PlayerActionC2SPacket(
                                    Action.STOP_DESTROY_BLOCK,
                                    var15,
                                    Direction.UP,
                                    NetworkUtils.generateNextSequence()));
                            this.cd = 0;
                        }
                    }

                    if (this.cd > 4) {
                        this.cd = 0;
                        Box var8 = this.RR.getBoundingBox();
                        EntityHitResult var10 =
                                new EntityHitResult(this.RR, var8.getCenter().add(0.0, var8.getLengthY() / 2.0, 0.0));
                        InteractUtils.simulateInteract(var10);
                    } else {
                        this.cd++;
                    }
                }

                for (Entry var11 : this.RP.entrySet()) {
                    BlockPos var12 = (BlockPos) var11.getKey();
                    mc.world.setBlockState(var12, Blocks.AIR.getDefaultState());
                }
            }
        }
    }

    @Override
    public void onDisableModule() {
        super.onDisableModule();
        this.RQ.clear();
        this.RP.clear();
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.ap().getChannel(BlockUpdateS2CPacket.class), this::onBlockUpdate);
    }
}
