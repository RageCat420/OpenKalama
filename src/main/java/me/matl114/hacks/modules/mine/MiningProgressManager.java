package me.matl114.hacks.modules.mine;

import java.util.HashMap;
import java.util.Map;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MiningProgressManager extends BaseModule {
    final Map<Integer, MineSubHelperH> trackedMap = new HashMap<>();
    public static MiningProgressManager INSTANCE;

    public void onEntityRemove(Event<Entity> event) {
        this.trackedMap.remove(((Entity) event.b).getId());
    }

    public void onBlockProgressUpdate(Event<BlockBreakingProgressS2CPacket> eventProgress) {
        int var2 = ((BlockBreakingProgressS2CPacket) eventProgress.b).getEntityId();
        BlockPos var3 = ((BlockBreakingProgressS2CPacket) eventProgress.b).getPos();
        if (mc.world.getEntityById(var2) instanceof PlayerEntity var5) {
            int var7 = ((BlockBreakingProgressS2CPacket) eventProgress.b).getProgress();
            if (var7 == 255) {
                var7 = -1;
            }

            MineSubHelperH var6 = this.trackedMap.computeIfAbsent(var2, v -> new MineSubHelperH(var5));
            var6.pushBreakingProgress(var3, var7);
        }
    }

    public MiningProgressManager() {
        super("MiningProgressManager");
        INSTANCE = this;
    }

    public void lh(Event<World> world) {
        this.trackedMap.clear();
    }

    public void onUpdate(Event<ClientPlayerEntity> eventUpdate) {
        if (!checkNull()) {
            for (MineSubHelperH var3 : this.trackedMap.values()) {
                var3.tickWorld(mc.world);
            }
        }
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.aU().c(EntityType.PLAYER), this::onEntityRemove);
        this.registerListener(Listener.N(), this::lh);
        this.registerListener(
                Listener.ar().getChannel(BlockBreakingProgressS2CPacket.class), this::onBlockProgressUpdate);
        this.registerListener(Listener.U(), this::onUpdate);
    }

    public Map<Integer, MineSubHelperH> getBreakingMap() {
        return this.trackedMap;
    }
}
