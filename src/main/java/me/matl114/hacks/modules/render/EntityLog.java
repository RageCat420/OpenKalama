package me.matl114.hacks.modules.render;

import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import me.matl114.commands.MainCommand;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.hacks.utils.config.StringFormat;
import me.matl114.hacks.utils.config.TracingOption;
import me.matl114.hacks.utils.config.WrapColor;
import me.matl114.hacks.utils.render.HackUtilHelperB;
import me.matl114.hacks.utils.render.RenderCollectors;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.ChatUtils$TextBuilder;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.render.RenderCollector;
import me.matl114.versioned.api.VDrawContext;
import me.matl114.versioned.api.VRecord;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Action;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

public class EntityLog extends BaseModule {
    public final NBTRef<TracingOption> renderOptions;
    RenderCollector<Box> pK;
    public final FlagRef renderReasonFaraway;
    public final FlagRef logLogReconnect;
    public final NBTRef<StringFormat> logPlayerReloginFormat;
    public final NBTRef<EntrySet<EntityType<?>>> cN;
    public final NBTRef<StringFormat> logPlayerLogoutFormat;
    public final KeyBindRef hotkey;
    static final String[] LEAVE_REASON = new String[] {"Log", "Faraway", "Teleport", "Queuing", "ReLogin"};
    RenderCollector<Vec3d> pM;
    RenderCollector<HackUtilHelperB> pN;
    public final FlagRef renderReasonLog;
    public final FlagRef renderReasonTeleport;
    public final FlagRef logEntityToChat;
    public final FlagRef enable;
    public final NBTRef<StringFormat> logSpawnFormat;
    RenderCollector<Box> pL;
    public final NBTRef<WrapColor> renderColor;
    public final ModulePath pu = makePath(Configs.i, "detect-entity.entity-log");
    public final NBTRef<StringFormat> logDisappearFormat;
    public final NBTRef<StringFormat> logPlayerJoinQueueFormat;
    public final FlagRef renderLogPlayers;
    public Map<UUID, RenderSubHelperY> pI;
    public final FlagRef logLogPlayerToChat;

    public void handleReLogin(UUID uuid) {
        RenderSubHelperY var2 = this.pI.remove(uuid);
        if (var2 != null && (var2.exitCode == 0 || var2.exitCode == 3)) {
            var2.exitCode = 4;
            if (this.logLogReconnect.get()) {
                this.logSub(
                        "Entity",
                        this.logPlayerReloginFormat
                                .get()
                                .formatText(
                                        var2.f, ChatUtils.getDisplayedLocationDouble(var2.c.x, var2.c.y, var2.c.z)));
            }
        }
    }

    public void xP(UUID playerUUID) {
        this.pI.remove(playerUUID);
    }

    public void onEntitySpawn(Event<EntitySpawnS2CPacket> packetEvent) {
        if (!checkNull()) {
            if (!this.loginServerCheck()) {
                EntitySpawnS2CPacket var2 = (EntitySpawnS2CPacket) packetEvent.e();
                if (this.enable.get() && this.cN.get().test(var2.getEntityType())) {
                    EntityType var3 = var2.getEntityType();
                    if (var3 == EntityType.PLAYER) {
                        if (this.logEntityToChat.get()) {
                            MutableText var4 = null;
                            if (MinecraftClient.getInstance().world != null) {
                                PlayerListEntry var5 = MinecraftClient.getInstance()
                                        .getNetworkHandler()
                                        .getPlayerListEntry(var2.getUuid());
                                if (var5 != null) {
                                    var4 = Text.literal(VRecord.getName(var5.getProfile()))
                                            .formatted(Formatting.GREEN);
                                }
                            }

                            this.logSub(
                                    "Entity",
                                    this.logSpawnFormat
                                            .get()
                                            .formatText(
                                                    "Player",
                                                    var4 == null ? Text.empty() : var4,
                                                    ChatUtils.getDisplayedLocationDouble(
                                                            var2.getX(), var2.getY(), var2.getZ()),
                                                    formatDistance(var2.getX(), var2.getY(), var2.getZ())));
                        }

                        this.xP(var2.getUuid());
                    } else if (this.logEntityToChat.get()) {
                        this.logSub(
                                "Entity",
                                this.logSpawnFormat
                                        .get()
                                        .formatText(
                                                "Entity",
                                                var2.getEntityType().getName(),
                                                ChatUtils.getDisplayedLocationDouble(
                                                        var2.getX(), var2.getY(), var2.getZ()),
                                                formatDistance(var2.getX(), var2.getY(), var2.getZ())));
                    }
                }
            }
        }
    }

    public void xV(UUID uid) {}

    public void onPlayerDisappear(PlayerEntity player) {
        UUID var2 = player.getUuid();
        if (!player.isDead() && !(player.getHealth() <= 1.0E-6)) {
            ChunkPos var3 = player.getChunkPos();
            RenderSubHelperY var4 = new RenderSubHelperY(
                    var2,
                    player.getBoundingBox(),
                    player.getPos(),
                    var3,
                    player.getPose(),
                    player.getDisplayName(),
                    player.getNameForScoreboard(),
                    mc.world.getRegistryKey(),
                    0);
            if (mc.player.getPos().subtract(player.getPos()).horizontalLengthSquared() > MathUtils.b(48)) {
                var4.exitCode = 1;
            } else {
                var4.exitCode = 2;
                Tasks.n(
                        () -> {
                            if (checkNull()) {
                                return true;
                            } else {
                                PlayerListEntry var3x = mc.getNetworkHandler().getPlayerListEntry(var2);
                                if (var3x == null) {
                                    var4.exitCode = 0;
                                    if (this.logLogPlayerToChat.get()) {
                                        this.logSub(
                                                "Entity",
                                                this.logPlayerLogoutFormat
                                                        .get()
                                                        .formatText(
                                                                var4.f,
                                                                "logout",
                                                                ChatUtils.getDisplayedLocationDouble(
                                                                        var4.c.x, var4.c.y, var4.c.z),
                                                                this.createTracking(var4.g)));
                                    }

                                    return true;
                                } else if (var3x.getGameMode() == GameMode.SPECTATOR) {
                                    var4.exitCode = 0;
                                    if (this.logLogPlayerToChat.get()) {
                                        this.logSub(
                                                "Entity",
                                                this.logPlayerLogoutFormat
                                                        .get()
                                                        .formatText(
                                                                var4.f,
                                                                "got kicked",
                                                                ChatUtils.getDisplayedLocationDouble(
                                                                        var4.c.x, var4.c.y, var4.c.z),
                                                                this.createTracking(var4.g)));
                                    }

                                    this.handleJoinServer(VRecord.getId(var3x.getProfile()));
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                        },
                        1,
                        1,
                        20);
            }

            this.pI.put(var2, var4);
        } else {
            this.pI.remove(var2);
        }
    }

    private Text createEntityDisplayName(Entity entity) {
        MutableText var2 = Text.empty().append(entity.getType().getName().copy());
        if (entity.hasCustomName() && entity.getCustomName() != null) {
            var2.append(Text.literal(" ")).append(entity.getCustomName().copy());
        }

        return var2;
    }

    public void xR(Event<Void> eventLeave) {
        this.pI.clear();
    }

    public void onPlayerListEntryAdd(Event<PlayerListEntry> event) {
        UUID var2 = VRecord.getId(((PlayerListEntry) event.b).getProfile());
        GameMode var3 = ((PlayerListEntry) event.b).getGameMode();
        if (var3 != GameMode.SPECTATOR) {
            Tasks.l(
                    () -> {
                        if (!checkNull()) {
                            PlayerListEntry var2x = mc.getNetworkHandler().getPlayerListEntry(var2);
                            if (var2x != null) {
                                if (var2x.getGameMode() == GameMode.SPECTATOR) {
                                    this.handleJoinServer(var2);
                                } else {
                                    this.handleReLogin(var2);
                                }
                            }
                        }
                    },
                    5);
        }
    }

    public void xZ(UUID uuid) {
        RenderSubHelperY var2 = this.pI.get(uuid);
        if (var2 != null && var2.exitCode == 3) {
            var2.exitCode = 0;
        }
    }

    public void onUpdate(Event<Void> eventUpdate) {
        this.pK.clear();
        this.pM.clear();
        this.pN.clear();
        if (!checkNull()) {
            if (this.enable.get() && this.renderLogPlayers.get()) {
                RegistryKey var2 = mc.world.getRegistryKey();
                WrapColor var3 = this.renderColor.get();

                for (RenderSubHelperY var5 : this.pI.values()) {
                    switch (var5.exitCode) {
                        case 0:
                        case 3:
                            if (!this.renderReasonLog.get()) {
                                continue;
                            }
                            break;
                        case 1:
                            if (!this.renderReasonFaraway.get()) {
                                continue;
                            }
                            break;
                        case 2:
                            if (!this.renderReasonTeleport.get()) {
                                continue;
                            }
                    }

                    if (Objects.equals(var5.h, var2)) {
                        ChunkPos var6 = var5.d;
                        if (mc.world.getChunkManager().isChunkLoaded(var6.x, var6.z)) {
                            Box var7 = var5.b;
                            TracingOption var8 = this.renderOptions.get();
                            if (var8.line()) {
                                this.pM.submit(var7.getCenter(), var3.withAlpha(255));
                            }

                            if (var8.box()) {
                                this.pK.submit(var7, var3.withAlpha(64));
                                this.pL.submit(var7, var3.withAlpha(255));
                            }

                            ChatUtils$TextBuilder var9 = ChatUtils.builder();
                            var9.withText(var5.f, Style.EMPTY.withBold(true));
                            var9.withColorString("&l %s at ".formatted(LEAVE_REASON[var5.exitCode]));
                            var9.withText(ChatUtils.t(var5.c), Style.EMPTY.withBold(true));
                            var9.end();
                            this.pN.submit(
                                    new HackUtilHelperB(var9.build(), var5.c.add(0.0, 2.0, 0.0), 0.66F),
                                    var3.withAlpha(255));
                        }
                    }
                }
            }
        }
    }

    private Text createTracking(String name) {
        return ChatUtils.textFromLegacyString("&a&l[&aTrack&a&l]").styled(s -> s.withClickEvent(
                        ChatUtils.getSuggestCommand(MainCommand.getMainCommandPrefix() + "pqueue add " + name))
                .withHoverEvent(ChatUtils.G(List.of(Text.literal("Click to track player in queue")))));
    }

    private static double calculateDistance(double x1, double y1, double z1) {
        if (MinecraftClient.getInstance().player != null) {
            ClientPlayerEntity var6 = MinecraftClient.getInstance().player;
            return Math.sqrt(var6.getPos().squaredDistanceTo(x1, y1, z1));
        } else {
            return -1.0;
        }
    }

    public void handleJoinServer(UUID uuid) {
        RenderSubHelperY var2 = this.pI.get(uuid);
        if (var2 != null && var2.exitCode == 0) {
            var2.exitCode = 3;
            if (this.logLogReconnect.get()) {
                int var3 = (int) mc.getNetworkHandler().getPlayerList().stream()
                        .filter(s -> s.getGameMode() == GameMode.SPECTATOR)
                        .count();
                this.logSub(
                        "Entity",
                        this.logPlayerJoinQueueFormat
                                .get()
                                .formatText(
                                        var2.f,
                                        ChatUtils.getDisplayedLocationDouble(var2.c.x, var2.c.y, var2.c.z),
                                        var3,
                                        this.createTracking(var2.g)));
            }
        }
    }

    public EntityLog() {
        super("EntityLog");
        this.enable = this.flagBuilder(this.pu.add("enable")).build();
        this.hotkey = this.moduleEntry(this.pu.add("hotkey"), new MultiKeyBind(), this.pu.add("enable"))
                .build();
        this.cN = this.builder(this.pu.add("whitelist"), EntrySet.<EntityType<?>>parameter())
                .defaultValue(new EntrySet<EntityType<?>>(new Regex("player"), Registries.ENTITY_TYPE))
                .build();
        this.logEntityToChat = this.builder(this.pu.add("log-entity-to-chat"), Boolean.class)
                .defaultValue(true)
                .build();
        this.logLogPlayerToChat =
                this.flagBuilder(this.pu.add("log-log-player-to-chat")).build();
        this.renderLogPlayers =
                this.flagBuilder(this.pu.add("render-log-players")).build();
        this.renderOptions = this.builder(this.pu.add("render-options"), TracingOption.class)
                .defaultValue(new TracingOption(true, false))
                .build();
        this.renderColor = this.builder(this.pu.add("render-color"), WrapColor.class)
                .defaultValue(new WrapColor(Formatting.LIGHT_PURPLE))
                .build();
        this.renderReasonLog = this.builder(this.pu.add("render-reason-log"), Boolean.class)
                .defaultValue(true)
                .build();
        this.renderReasonTeleport = this.builder(this.pu.add("render-reason-teleport"), Boolean.class)
                .defaultValue(true)
                .build();
        this.renderReasonFaraway =
                this.flagBuilder(this.pu.add("render-reason-faraway")).build();
        this.logLogReconnect =
                this.flagBuilder(this.pu.add("log-log-reconnect")).build();
        this.logSpawnFormat = this.builder(this.pu.add("log-spawn-format"), StringFormat.class)
                .defaultValue(new StringFormat(
                        List.of("type", "name", "position", "distance"),
                        "{type} {name} spawn at position {position}, distance: {distance}",
                        true))
                .build();
        this.logDisappearFormat = this.builder(this.pu.add("log-disappear-format"), StringFormat.class)
                .defaultValue(new StringFormat(
                        List.of("type", "name", "position", "distance"),
                        "{type} {name} disappear at position {position}, distance: {distance}",
                        true))
                .build();
        this.logPlayerLogoutFormat = this.builder(this.pu.add("log-player-logout-format"), StringFormat.class)
                .defaultValue(new StringFormat(
                        List.of("name", "action", "position"), "Player {name} {action} at {position}", true))
                .build();
        this.logPlayerJoinQueueFormat = this.builder(this.pu.add("log-player-join-queue-format"), StringFormat.class)
                .defaultValue(new StringFormat(
                        List.of("name", "position", "queue_count"),
                        "Player {name} join queue, last position: {position}, current queue: {queue_count}",
                        true))
                .build();
        this.logPlayerReloginFormat = this.builder(this.pu.add("log-player-relogin-format"), StringFormat.class)
                .defaultValue(new StringFormat(
                        List.of("name", "position", "track_button"),
                        "Player {name} reLogin, last position: {position} {track_button}",
                        true))
                .build();
        this.pI = new LinkedHashMap<>();
        this.pK = RenderCollectors.createBoxCollector(false, true, false);
        this.pL = RenderCollectors.createBoxCollector(true, false, false);
        this.pM = RenderCollectors.d();
        this.pN = RenderCollectors.f();
        this.bindFlag(this.enable);
    }

    public void onPlayerListEntryRemove(Event<PlayerListEntry> event) {
        UUID var2 = VRecord.getId(((PlayerListEntry) event.b).getProfile());
        this.xZ(var2);
    }

    public void sg(Event<VDrawContext> eventVDraw) {
        if (this.enable.get() && this.renderLogPlayers.get()) {
            this.pN.b((VDrawContext) eventVDraw.b);
        }
    }

    public void onPlayerListEntryModify(Event<PlayerListEntry> event) {
        if (event.getArgs(0) == Action.UPDATE_GAME_MODE) {
            UUID var2 = VRecord.getId(((PlayerListEntry) event.b).getProfile());
            GameMode var3 = ((PlayerListEntry) event.b).getGameMode();
            if (var3 != GameMode.SPECTATOR) {
                this.handleReLogin(var2);
            } else {
                this.xV(var2);
            }
        }
    }

    public boolean loginServerCheck() {
        return mc.world.getWorldBorder().getSize() < 100.0;
    }

    private static String formatDistance(double x1, double y1, double z1) {
        return "%.2f".formatted(calculateDistance(x1, y1, z1));
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.ar().getChannel(EntitySpawnS2CPacket.class), this::onEntitySpawn);
        this.registerListener(Listener.aq().getChannel(EntitiesDestroyS2CPacket.class), this::onEntityRemove);
        this.registerListener(Listener.P(), this::xR);
        this.registerListener(Listener.T(), this::onUpdate);
        this.registerListener(RenderListener.q(), this::B);
        this.registerListener(Listener.aK(), this::onPlayerListEntryAdd);
        this.registerListener(Listener.aM(), this::onPlayerListEntryModify);
        this.registerListener(Listener.aL(), this::onPlayerListEntryRemove);
        this.registerListener(RenderListener.r(), this::sg);
    }

    public void B(Event<MatrixStack> eventMatrixStack) {
        if (this.enable.get() && this.renderLogPlayers.get()) {
            RenderUtils.startDrawVirtual((MatrixStack) eventMatrixStack.b);

            try {
                this.pK.a((MatrixStack) eventMatrixStack.b);
                this.pM.a((MatrixStack) eventMatrixStack.b);
            } finally {
                RenderUtils.stopDrawVirtual((MatrixStack) eventMatrixStack.b);
            }
        }
    }

    public void onEntityRemove(Event<EntitiesDestroyS2CPacket> packetEvent) {
        if (!checkNull()) {
            if (!this.loginServerCheck()) {
                if (this.enable.get()) {
                    EntitiesDestroyS2CPacket var2 = (EntitiesDestroyS2CPacket) packetEvent.e();
                    if (mc.world != null) {
                        LinkedHashSet<Entity> var3 = new LinkedHashSet();
                        IntListIterator var4 = var2.getEntityIds().iterator();

                        while (var4.hasNext()) {
                            int var5 = (Integer) var4.next();
                            Entity var6 = mc.world.getEntityById(var5);
                            if (var6 != null && this.cN.get().test(var6.getType())) {
                                var3.add(var6);
                            }
                        }

                        for (Entity var8 : var3) {
                            if (var8 instanceof PlayerEntity var9) {
                                if (this.logEntityToChat.get()) {
                                    this.logSub(
                                            "Entity",
                                            this.logDisappearFormat
                                                    .get()
                                                    .formatText(
                                                            "Player",
                                                            var9.getDisplayName(),
                                                            ChatUtils.getDisplayedLocationDouble(
                                                                    var8.getX(), var8.getY(), var8.getZ()),
                                                            formatDistance(var8.getX(), var8.getY(), var8.getZ())));
                                }

                                this.onPlayerDisappear(var9);
                            } else if (this.logEntityToChat.get()) {
                                this.logSub(
                                        "Entity",
                                        this.logDisappearFormat
                                                .get()
                                                .formatText(
                                                        "Player",
                                                        this.createEntityDisplayName(var8),
                                                        ChatUtils.getDisplayedLocationDouble(
                                                                var8.getX(), var8.getY(), var8.getZ()),
                                                        formatDistance(var8.getX(), var8.getY(), var8.getZ())));
                            }
                        }
                    }
                }
            }
        }
    }
}
