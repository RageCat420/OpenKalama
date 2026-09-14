package me.matl114.hacks.modules.render;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import me.matl114.commands.MainCommand;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.IntPrimitiveList;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.utils.Debug;
import me.matl114.utils.WorldUtils;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperD;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperH;
import me.matl114.utils.commands.commandGroup.SubCommand;
import me.matl114.utils.commands.commandGroup.TaskSubCommand;
import me.matl114.utils.commands.commandGroup.TreeSubCommand;
import me.matl114.utils.commands.params.KalamaHelperHelperA;
import me.matl114.versioned.api.VRecord;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Action;
import net.minecraft.world.GameMode;

public class PlayerQueue extends BaseModule {
    public final FlagRef leaveClearCache;
    public Deque<RenderSubHelperFX> Rz;
    public final NBTRef<IntPrimitiveList> trackedQueueOrders;
    public final ModulePath Ca = makePath(Configs.i, "player-io.player-queue");
    public final FlagRef trackedSkipQueue;
    public final FlagRef removeTrackAfterJoin;
    public final FlagRef certainOrder;
    public final FlagRef ae = this.flagBuilder(this.Ca.addEnable()).build();
    public Set<UUID> RA;
    public Set<String> Ry;

    public void aiX(String string) {
        if (this.Ry.contains(string)) {
            this.logI18N("message.module.player-queue.already-tracked", new Object[0]);
        } else {
            this.Ry.add(string);
            this.logI18N("message.module.player-queue.start-tracking", new Object[] {string});
        }
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.M(), this::aiM);
        this.registerListener(Listener.P(), this::tI);
        this.registerListener(Listener.O(), this::aiL);
        this.registerListener(Listener.aK(), this::onPlayerListAdd);
        this.registerListener(Listener.aL(), this::onPlayerListRemove);
        this.registerListener(Listener.aM(), this::onPlayerListModify);
        this.registerCommandBootstrap(this::ajc);
    }

    public void listTrackedDetails() {
        this.logI18N("message.module.player-queue.current-tracked", new Object[0]);
        LinkedHashMap var1 = new LinkedHashMap();

        for (RenderSubHelperFX var3 : this.Rz) {
            String var4 = VRecord.getName(var3.b.getProfile());
            if (this.Ry.contains(var4)) {
                var1.put(var4, var3);
            }
        }

        for (String var7 : this.Ry) {
            RenderSubHelperFX var8 = (RenderSubHelperFX) var1.get(var7);
            if (var8 != null) {
                Debug.chat("-", var7, "(queuing,", var8.initialize ? "order=unknown)" : "order=" + var8.c + ")");
            } else {
                PlayerListEntry var5 = mc.getNetworkHandler().getPlayerListEntry(var7);
                if (var5 != null) {
                    Debug.chat("-", var7, var5.getGameMode() == GameMode.SURVIVAL ? "(online)" : "(queuing)");
                } else {
                    Debug.chat("-", var7, "(offline)");
                }
            }
        }
    }

    public void onTrackedLeave(RenderSubHelperFX entry, boolean leaveServer) {
        if (!this.Ry.isEmpty()) {
            String var3 = VRecord.getName(entry.b.getProfile());
            if (this.Ry.contains(var3)) {
                if (this.ae.get()) {
                    if (leaveServer) {
                        this.logI18N("message.module.player-queue.leave-server", new Object[] {var3});
                    } else {
                        boolean var4 = this.trackedSkipQueue.get() && !entry.initialize && entry.c > 3;
                        this.logI18N(
                                "message.module.player-queue.finish-queue", new Object[] {var3, var4 ? "(skip?)" : ""});
                    }
                }

                if (!leaveServer && this.removeTrackAfterJoin.get()) {
                    this.Ry.remove(var3);
                    if (this.ae.get()) {
                        this.logI18N("message.module.player-queue.auto-untrack", new Object[] {var3});
                    }
                }
            }
        }
    }

    public void onInitializeQueue() {
        this.Rz.clear();
        this.RA.clear();
        if (!checkNull()) {
            int var1 = 0;

            for (PlayerListEntry var3 : mc.getNetworkHandler().getPlayerList()) {
                if (var3.getGameMode() == GameMode.SPECTATOR) {
                    this.Rz.addLast(new RenderSubHelperFX(
                            VRecord.getId(var3.getProfile()), var3, ++var1, var1, true, Tasks.b()));
                    this.RA.add(VRecord.getId(var3.getProfile()));
                }
            }
        }
    }

    public void onPlayerListAdd(Event<PlayerListEntry> entry) {
        String var2 = VRecord.getName(((PlayerListEntry) entry.b).getProfile());
        if (this.Ry.contains(var2) && this.ae.get()) {
            this.logI18N("message.module.player-queue.join-server", new Object[] {var2});
        }

        if (((PlayerListEntry) entry.b).getGameMode() == GameMode.SPECTATOR) {
            this.aiQ((PlayerListEntry) entry.b);
        }
    }

    public void tI(Event<Void> serverLeave) {
        if (this.leaveClearCache.get()) {
            this.Ry.clear();
        }
    }

    public void ajc(MainCommand mainCommand) {
        TreeSubCommand var2 = mainCommand.bD().a("pqueue").k();
        var2.<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                .u("add")
                .x("message.command.pqueue.add.help")
                .A(KalamaHelperHelperA.a()
                        .B("name")
                        .d(WorldUtils::getPlayerListNames)
                        .v())
                .z(s -> s.executor(KalamaHelperHelperH.i(ar -> this.aiX(ar.o()))))
                .r()
                .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                .u("remove")
                .x("message.command.pqueue.remove.help")
                .A(KalamaHelperHelperA.a()
                        .B("name")
                        .d(WorldUtils::getPlayerListNames)
                        .v())
                .z(s -> s.executor(KalamaHelperHelperH.i(ar -> this.aiY(ar.o()))))
                .r()
                .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                .u("list")
                .x("message.command.pqueue.list.help")
                .z(s -> s.executor(KalamaHelperHelperH.g(this::listTrackedDetails)))
                .r()
                .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                .u("clear")
                .x("message.command.pqueue.clear.help")
                .z(s -> s.executor(KalamaHelperHelperH.g(this::aiZ)))
                .r()
                .subBuilder(SubCommand.bo())
                .u("check")
                .x("message.command.pqueue.check.help")
                .A(KalamaHelperHelperA.a()
                        .B("name")
                        .d(WorldUtils::getPlayerListNames)
                        .v())
                .z(s -> s.executor(KalamaHelperHelperH.i(ar -> this.checkTrackedInfo(ar.o()))))
                .r();
    }

    @Override
    public void addCustomWidgets(Consumer<DrawableWidget> acceptor, int dx, int dy, int dblank) {
        super.addCustomWidgets(acceptor, dx, dy, dblank);
        acceptor.accept(this.createTitleLabel("widget.player-queue.command", 0, dblank, dx, dy));
    }

    public RenderSubHelperFX leaveQueue(UUID uid) {
        if (this.RA.contains(uid)) {
            this.RA.remove(uid);
            Iterator var2 = this.Rz.iterator();

            while (var2.hasNext()) {
                RenderSubHelperFX var3 = (RenderSubHelperFX) var2.next();
                if (Objects.equals(var3.a, uid)) {
                    var2.remove();
                    return var3;
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public void checkTrackedInfo(String name) {
        this.logI18N("message.module.player-queue.tracked-info", new Object[] {name});

        for (RenderSubHelperFX var3 : this.Rz) {
            String var4 = VRecord.getName(var3.b.getProfile());
            if (Objects.equals(var4, name)) {
                Debug.chat("-", name, "(queuing,", var3.initialize ? "order=unknown)" : "order=" + var3.c + ")");
                return;
            }
        }

        PlayerListEntry var5 = mc.getNetworkHandler().getPlayerListEntry(name);
        if (var5 != null) {
            Debug.chat("-", name, var5.getGameMode() == GameMode.SURVIVAL ? "(online)" : "(queuing)");
        } else {
            Debug.chat("-", name, "(offline)");
        }
    }

    public void playerJoinQueue(PlayerListEntry entry) {
        UUID var2 = VRecord.getId(entry.getProfile());
        RenderSubHelperFX var3 = this.leaveQueue(var2);
        if (var3 != null) {
            this.onJoinPositionChange();
            this.onTrackedLeave(var3, true);
        }
    }

    public PlayerQueue() {
        super("PlayerQueue");
        this.certainOrder = this.flagBuilder(this.Ca.add("certain-order")).build();
        this.trackedQueueOrders = this.builder(this.Ca.add("tracked-queue-orders"), IntPrimitiveList.class)
                .defaultValue(new IntPrimitiveList(List.of(1, 2, 3, 4, 5, 10, 20)))
                .show(this.certainOrder::get)
                .build();
        this.trackedSkipQueue = this.builder(this.Ca.add("tracked-skip-queue"), Boolean.class)
                .defaultValue(true)
                .build();
        this.leaveClearCache =
                this.flagBuilder(this.Ca.add("leave-clear-cache")).build();
        this.removeTrackAfterJoin =
                this.flagBuilder(this.Ca.add("remove-track-after-join")).build();
        this.Ry = new LinkedHashSet<>();
        this.Rz = new ArrayDeque<>();
        this.RA = new HashSet<>();
    }

    public void aiS(PlayerListEntry entry) {
        UUID var2 = VRecord.getId(entry.getProfile());
        RenderSubHelperFX var3 = this.leaveQueue(var2);
        if (var3 != null) {
            this.onJoinPositionChange();
            this.onTrackedLeave(var3, false);
        }
    }

    public void onPlayerListModify(Event<PlayerListEntry> entry) {
        if (entry.getArgs(0) == Action.UPDATE_GAME_MODE) {
            if (((PlayerListEntry) entry.b).getGameMode() == GameMode.SPECTATOR) {
                this.aiQ((PlayerListEntry) entry.b);
            } else {
                this.aiS((PlayerListEntry) entry.b);
            }
        }
    }

    public void aiY(String string) {
        if (this.Ry.contains(string)) {
            this.Ry.remove(string);
            this.logI18N("message.module.player-queue.stop-tracking", new Object[] {string});
        } else {
            this.logI18N("message.module.player-queue.not-tracked", new Object[0]);
        }
    }

    public void aiM(Event<ClientPlayerEntity> onGameJoin) {
        this.Rz.clear();
        this.RA.clear();
        Tasks.l(this::onInitializeQueue, 20);
    }

    public void aiL(Event<Void> serverChange) {
        this.Rz.clear();
        this.RA.clear();
    }

    public void aiQ(PlayerListEntry entry) {
        UUID var2 = VRecord.getId(entry.getProfile());
        if (!this.RA.contains(var2)) {
            this.RA.add(var2);
            this.Rz.addLast(new RenderSubHelperFX(var2, entry, this.Rz.size(), 0, false, Tasks.b()));
        }
    }

    public void onPlayerListRemove(Event<PlayerListEntry> entry) {
        if (((PlayerListEntry) entry.b).getGameMode() == GameMode.SPECTATOR) {
            this.playerJoinQueue((PlayerListEntry) entry.b);
        }
    }

    public void onJoinPositionChange() {
        IntOpenHashSet var1 = null;
        int var2 = 0;

        for (RenderSubHelperFX var4 : this.Rz) {
            var2++;
            if (!var4.initialize) {
                var4.d = var4.c;
                var4.c = var2;
                if (this.ae.get() && var4.d != var2) {
                    if (var1 == null) {
                        var1 = new IntOpenHashSet(this.trackedQueueOrders.get().list());
                    }

                    String var5 = VRecord.getName(var4.b.getProfile());
                    if (this.Ry.contains(var5) && (!this.certainOrder.get() || var1.contains(var2))) {
                        this.logI18N("message.module.player-queue.current-order", new Object[] {var5, var2});
                    }
                }
            }
        }
    }

    public void aiZ() {
        this.Ry.clear();
        this.logI18N("message.module.player-queue.clear-all", new Object[0]);
    }
}
