package me.matl114.hacks.modules.extra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import me.matl114.accessors.access.PlayerMoveC2SPacketAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.move.LegacySnapRotManager;
import me.matl114.hooks.ViaFabricPlusHooks;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.entity.PlayerInputUtils;
import me.matl114.utils.entity.PlayerInputUtils$Input;
import me.matl114.versioned.SupportVersion;
import me.matl114.versioned.api.VPacket;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.World;

public class BadPackets extends BaseModule {
    PlayerInputUtils$Input FW;
    float FX;
    public final FlagRef fixFullDupRot;
    boolean FV;
    boolean Ga;
    public final FlagRef fixDupInput;
    public boolean FZ;
    public final ModulePath FH = makePath(Configs.j, "bad-packets");
    public static BadPackets INSTANCE;
    public final FlagRef fixLitematicaTransmit;
    public final FlagRef FI = this.builder(this.FH.add("fix-dup-sprint"), Boolean.class)
            .defaultValue(true)
            .build();
    public final FlagRef fixIllegalServerViewDistance;
    boolean FU;
    public final FlagRef fixBadBlockTags;
    float FY;
    public final FlagRef FJ = this.builder(this.FH.add("fix-dup-sneak"), Boolean.class)
            .defaultValue(true)
            .build();
    public final FlagRef filterDupRot;
    public final FlagRef fixInvalidPlayerEntryUpdate;
    public final FlagRef exemptDupRot;
    public final FlagRef fixFlyPackets;
    public final FlagRef fixDupRot;

    public BadPackets() {
        super("BadPackets");
        this.fixDupInput = this.builder(this.FH.add("fix-dup-input"), Boolean.class)
                .defaultValue(true)
                .build();
        this.exemptDupRot = this.builder(this.FH.add("exempt-dup-rot"), Boolean.class)
                .defaultValue(true)
                .build();
        this.filterDupRot = this.builder(this.FH.add("filter-dup-rot"), Boolean.class)
                .defaultValue(false)
                .build();
        this.fixFlyPackets = this.builder(this.FH.add("fix-fly-packets"), Boolean.class)
                .defaultValue(true)
                .build();
        this.fixDupRot = this.builder(this.FH.add("fix-dup-rot"), Boolean.class)
                .defaultValue(true)
                .build();
        this.fixFullDupRot = this.builder(this.FH.add("fix-full-dup-rot"), Boolean.class)
                .defaultValue(false)
                .build();
        this.fixIllegalServerViewDistance = this.builder(this.FH.add("fix-illegal-server-view-distance"), Boolean.class)
                .defaultValue(true)
                .build();
        this.fixBadBlockTags = this.builder(this.FH.add("fix-bad-block-tags"), Boolean.class)
                .defaultValue(true)
                .build();
        this.fixLitematicaTransmit = this.builder(this.FH.add("fix-litematica-transmit"), Boolean.class)
                .defaultValue(true)
                .build();
        this.fixInvalidPlayerEntryUpdate =
                this.flagBuilder(this.FH.add("fix-invalid-player-entry-update")).build();
        this.FU = false;
        this.FV = false;
        this.FW = PlayerInputUtils.a;
        this.FZ = SupportVersion.CURRENT.b(21, 2);
        this.Ga = false;
        INSTANCE = this;
    }

    public void onWorldChange(Event<World> eventWorldChange) {
        if (this.fixIllegalServerViewDistance.get()
                && eventWorldChange.b != null
                && eventWorldChange.b instanceof ClientWorld var3
                && var3.getChunkManager().chunks.radius > 35) {
            var3.getChunkManager().updateLoadDistance(32);
        }
    }

    public void onSendInput(Event<PlayerInputC2SPacket> inputC2SPacketEvent) {
        PlayerInputUtils$Input var2 = PlayerInputUtils.d((PlayerInputC2SPacket) inputC2SPacketEvent.e());
        if (Objects.equals(var2, this.FW)) {
            if (!mc.player.hasVehicle() && this.FZ && this.fixDupInput.get()) {
                inputC2SPacketEvent.cancel();
            }
        } else {
            this.FW = var2;
        }
    }

    public void fixTagsBadPackets(Event<Map<TagKey<?>, List<RegistryEntry<?>>>> event) {
        Object var2 = event.getArgs(0);
        if (this.fixBadBlockTags.get() && Objects.equals(var2, Registries.BLOCK.getKey())) {
            Map var3 = (Map) event.b;
            HashMap var4 = null;
            List var5 = (List) var3.get(BlockTags.PICKAXE_MINEABLE);
            if (var5 != null) {
                int var6 = var5.indexOf(Blocks.CHEST.getRegistryEntry());
                if (var6 != -1) {
                    if (var4 == null) {
                        var4 = new HashMap(var3);
                    }

                    ArrayList var7 = new ArrayList(var5);
                    var7.remove(var6);
                    var4.put(BlockTags.PICKAXE_MINEABLE, var7);
                }
            }

            if (var4 != null) {
                event.context(var4);
            }
        }
    }

    public void onSendSprint(Event<ClientCommandC2SPacket> event) {
        if (((ClientCommandC2SPacket) event.e()).getMode() == Mode.START_SPRINTING
                || ((ClientCommandC2SPacket) event.e()).getMode() == Mode.STOP_SPRINTING) {
            boolean var2 = ((ClientCommandC2SPacket) event.e()).getMode() == Mode.START_SPRINTING;
            if (this.FU == var2) {
                if (this.FI.get()) {
                    event.cancel();
                }
            } else {
                this.FU = var2;
            }
        }
    }

    public void onRepackViewDistance(Event<ChunkLoadDistanceS2CPacket> eventChunkLoad) {
        if (this.fixIllegalServerViewDistance.get() && eventChunkLoad.b != null) {
            ChunkLoadDistanceS2CPacket var2 = (ChunkLoadDistanceS2CPacket) eventChunkLoad.e();
            if (var2.getDistance() > 32) {
                eventChunkLoad.context(new ChunkLoadDistanceS2CPacket(32));
            }
        }
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.aE(), this::onPlayerInitialize);
        this.registerListener(Listener.ap().getChannel(ClientCommandC2SPacket.class), this::onSendSprint);
        this.registerListener(Listener.ap().getChannel(PlayerInputC2SPacket.class), this::onSendInput);
        this.registerListener(Listener.ap().getChannel(PlayerAbilitiesS2CPacket.class), this::onServerAbility);
        this.registerListener(Listener.ap().getChannel(UpdatePlayerAbilitiesC2SPacket.class), this::onAbilityUpdate);
        this.registerListener(Listener.ap().getChannel(TeleportConfirmC2SPacket.class), this::UT);
        this.registerListener(Listener.ap().getChannel(PlayerMoveC2SPacket.class), this::onPlayerRotation);
        this.registerListener(Listener.N(), this::onWorldChange);
        this.registerListener(Listener.ap().getChannel(ChunkLoadDistanceS2CPacket.class), this::onRepackViewDistance);
        this.registerListener(Listener.R().c(Registries.BLOCK.getKey()), this::fixTagsBadPackets);
    }

    public void onPlayerRotation(Event<PlayerMoveC2SPacket> packetEvent) {
        PlayerMoveC2SPacket var2 = (PlayerMoveC2SPacket) packetEvent.e();
        float var3 = var2.getPitch(this.FX);
        float var4 = var2.getYaw(this.FY);
        if (var2 instanceof PlayerMoveC2SPacketAccess var5) {
            PlayerMoveC2SPacketAccess.Cause var6 = var5.getCause();
            if (var6 != null) {
                switch (var6) {
                    case SET_BACK:
                    case LEGACY_SNAP:
                        this.Ga = true;
                }
            }
        }

        if (this.exemptDupRot.get() && ViaFabricPlusHooks.isSupportDupRot() && var2 instanceof Full var7) {
            this.Ga = true;
        }

        if (this.filterDupRot.get()
                && ViaFabricPlusHooks.isSupportDupRot()
                && var2 instanceof Full var8
                && LegacySnapRotManager.INSTANCE.betweenViaPacket
                && var8 instanceof PlayerMoveC2SPacketAccess var10
                && var10.getCause() == PlayerMoveC2SPacketAccess.Cause.LEGACY_SNAP
                && !EntityUtils.isRotationDifferent(var3, this.FX, var4, this.FY)) {
            this.FX = var3;
            this.FY = var4;
            this.Ga = false;
            packetEvent.cancel();
        } else if (this.Ga) {
            this.FX = var3;
            this.FY = var4;
            this.Ga = false;
        } else if (var3 == this.FX && var4 == this.FY) {
            if (var2.changesLook() && this.fixDupRot.get()) {
                if (this.fixFullDupRot.get() && var2 instanceof Full var9) {
                    packetEvent.context(PlayerMoveC2SPacketAccess.setCauseFrom(
                            VPacket.g(
                                    var2.getX(mc.player.getX()),
                                    var2.getY(mc.player.getY()),
                                    var2.getZ(mc.player.getX()),
                                    var2.isOnGround(),
                                    VPacket.getCollisionFlag(var9)),
                            var9));
                } else if (var2 instanceof LookAndOnGround var11 && !mc.player.hasVehicle()) {
                    packetEvent.context(PlayerMoveC2SPacketAccess.setCauseFrom(
                            VPacket.f(var11.isOnGround(), VPacket.getCollisionFlag(var11)), var11));
                }
            }
        } else {
            this.FX = var3;
            this.FY = var4;
        }
    }

    public void onServerAbility(Event<PlayerAbilitiesS2CPacket> event) {
        this.FV = ((PlayerAbilitiesS2CPacket) event.b).allowFlying();
    }

    public void onAbilityUpdate(Event<UpdatePlayerAbilitiesC2SPacket> event) {
        if (!event.d()
                && this.fixFlyPackets.get()
                && !this.FV
                && ((UpdatePlayerAbilitiesC2SPacket) event.e()).isFlying()) {
            event.cancel();
        }
    }

    public void UT(Event<TeleportConfirmC2SPacket> packetEvent) {
        this.Ga = true;
    }

    public void onPlayerInitialize(Event<ClientPlayerEntity> event) {
        ClientPlayerEntity var2 = (ClientPlayerEntity) event.e();
        this.FU = var2.isSprinting();
        this.FW = PlayerInputUtils.a;
        this.FV = var2.getAbilities().allowFlying;
        this.FX = var2.getPitch();
        this.FY = var2.getYaw();
    }
}
