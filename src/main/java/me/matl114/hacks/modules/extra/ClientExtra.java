package me.matl114.hacks.modules.extra;

import com.google.common.util.concurrent.Runnables;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import me.matl114.accessors.gui.ScreenAccess;
import me.matl114.events.Event;
import me.matl114.events.KalamaHelperHelperE;
import me.matl114.events.Listener;
import me.matl114.events.Listener$ExceptionType;
import me.matl114.gui.presets.choices.KalamaHelperHelperA;
import me.matl114.gui.presets.choices.QuestionScreen;
import me.matl114.hacks.MainTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.StringRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.ItemStackUtils;
import me.matl114.versioned.api.VEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockEntityTickInvoker;

public class ClientExtra extends BaseModule {
    public final FlagRef clientCrashKeepInServer;
    private final Text lZ;
    public final FlagRef noBlockEntityCrash;
    public final ModulePath ad = makePath(Configs.j, "other");
    public final FlagRef keepGuiOpenOnPortal;
    public final KeyBindRef lW;
    public final FlagRef fixPaletteException;
    public final FlagRef logSelfServerLeaving;
    int lastCrashTick;
    public static ClientExtra INSTANCE;
    public final FlagRef noEntityCrash;
    public final FlagRef lN = this.builder(this.ad.add("no-client-crash"), Boolean.class)
            .defaultValue(false)
            .build();
    public final StringRef clientBrandName;
    public final FlagRef noDisconnectOnPacketUnexpected;
    public final FlagRef noDisconnectOnPacketDecode;
    public final FlagRef noDisconnectOnNetworkError;

    public void onDecodeException(Event<KalamaHelperHelperE> event) {
        if (this.noDisconnectOnPacketDecode.get()) {
            KalamaHelperHelperE var2 = (KalamaHelperHelperE) event.e();
            PacketListener var3 = event.getArgs(0);
            Throwable var4 = var2.exception();
            if (var3 instanceof ClientPlayPacketListener var5) {
                if (mc.player != null) {
                    Debug.b(Text.literal("Error while decoding packet: ").formatted(Formatting.RED));
                    Debug.chat(
                            var4.getClass().getSimpleName(),
                            ":",
                            Text.literal(var4.getMessage() == null ? "Exception: null" : var4.getMessage()));
                }

                Debug.a("Exception StackTrace:");
                Debug.f(var4);
                event.cancel();
            }
        }
    }

    public void onCrash(Event<MinecraftClient> event) {
        if (event.g() && ((MinecraftClient) event.e()).isRunning() && this.lN.get()) {
            event.cancel();
            CrashReport var2 = event.getArgs(0);
            String var3 = var2 == null ? "null" : var2.getMessage();
            String var4 = var2 == null ? "null" : var2.getCauseAsString();
            var4 = var4.replace("\t", "");
            String[] var5 = var4.split("\\r?\\n");
            StringBuilder var6 = new StringBuilder();
            int var7 = Math.min(var5.length, 6);

            for (int var8 = 0; var8 < var7; var8++) {
                if (var8 > 0) {
                    var6.append("\n");
                }

                var6.append(var5[var8]);
            }

            if (var7 > 1 && var7 < var5.length) {
                var6.append("\n......(%d行)".formatted(var5.length - var7));
            }

            var4 = var6.toString();
            MutableText var13 = ChatUtils.textFromLegacyString("&c你的游戏刚刚崩溃了,但是Kalama拦截了它\n报错信息: " + var3 + "\n" + var4
                    + "\n如果你须与寻求帮助,请点击下方按钮打开错误报告\n而不是发送这个界面的截图");
            List var9 = List.of(
                    KalamaHelperHelperA.of(
                            Text.literal("我已知晓, 继续游戏").formatted(Formatting.GREEN), Runnables.doNothing()),
                    KalamaHelperHelperA.of(Text.literal("打开报告, 继续游戏").formatted(Formatting.YELLOW), () -> {
                        if (var2 != null) {
                            Path var1 = var2.getFile();
                            if (var1 != null) {
                                Util.getOperatingSystem().open(var2.getFile().getParent());
                                Util.getOperatingSystem().open(var2.getFile());
                            }
                        }
                    }),
                    KalamaHelperHelperA.of(Text.literal("我已知晓, 退出游戏").formatted(Formatting.RED), this::exitGame));
            QuestionScreen var10 = new QuestionScreen(var13, var9);
            this.checkClientData(var10);
        }
    }

    public void onBlockEntityException(Event<KalamaHelperHelperE> event) {
        if (this.noBlockEntityCrash.get()) {
            KalamaHelperHelperE var2 = (KalamaHelperHelperE) event.e();
            BlockEntityTickInvoker var3 = event.getArgs(0);
            World var4 = event.getArgs(1);
            event.cancel();
            if (!var3.isRemoved()) {
                Throwable var5 = var2.exception();
                Debug.chat(
                        "Error while ticking blockEntity at world:",
                        ChatUtils.t(Vec3d.of(var3.getPos())),
                        "World:",
                        var4.getRegistryKey().getValue());
                Debug.chat(
                        var5.getClass().getSimpleName(),
                        ":",
                        Text.literal(var5.getMessage() == null ? "Exception: null" : var5.getMessage()));
                Debug.a("BlockEntity Exception INFO :");
                Debug.e("  World : ", var4.getRegistryKey().getValue());
                Debug.e("  BlockEntityPos : ", var3);

                try {
                    BlockEntity var6 = var4.getBlockEntity(var3.getPos());
                    Debug.e(" BlockEntity : ", var6 == null ? null : var6.getType());
                    if (var6 != null) {
                        Debug.e(" BlockEntityNBT : ", var6.createNbt(ItemStackUtils.registry()));
                    }

                    BlockState var7 = var4.getBlockState(var3.getPos());
                    Debug.e(" BlockState : ", var7);
                } catch (Throwable var8) {
                }

                Debug.a("Exception StackTrace:");
                Debug.f(var5);
            }
        }
    }

    public ClientExtra() {
        super("ClientExtra");
        this.clientCrashKeepInServer =
                this.flagBuilder(this.ad.add("client-crash-keep-in-server")).build();
        this.noEntityCrash = this.flagBuilder(this.ad.add("no-entity-crash")).build();
        this.noBlockEntityCrash =
                this.flagBuilder(this.ad.add("no-block-entity-crash")).build();
        this.noDisconnectOnNetworkError = this.builder(this.ad.add("no-disconnect-on-network-error"), Boolean.class)
                .defaultValue(false)
                .build();
        this.noDisconnectOnPacketDecode = this.builder(this.ad.add("no-disconnect-on-packet-decode"), Boolean.class)
                .defaultValue(false)
                .build();
        this.noDisconnectOnPacketUnexpected = this.builder(
                        this.ad.add("no-disconnect-on-packet-unexpected"), Boolean.class)
                .defaultValue(false)
                .build();
        this.keepGuiOpenOnPortal =
                this.flagBuilder(this.ad.add("keep-gui-open-on-portal")).build();
        this.clientBrandName = this.builder(this.ad.add("client-brand-name"), StringRef.TYPE)
                .defaultValue("")
                .build();
        this.lW = this.hotkey(Configs.j, this.ad.add("cursor-switch-hotkey").toPath())
                .defaultValue(new MultiKeyBind())
                .registerHotkey(HotKeyUtils.d(this::onCursorLockSwitch))
                .build();
        this.fixPaletteException =
                this.flagBuilder(this.ad.add("fix-palette-exception")).build();
        this.logSelfServerLeaving =
                this.flagBuilder(this.ad.add("log-self-server-leaving")).build();
        this.lZ = Text.literal("你的游戏刚才因为未知原因崩溃,但是Kalama拦截了它").formatted(Formatting.RED);
        this.lastCrashTick = 0;
        INSTANCE = this;
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.Y(), this::onCrash);
        this.registerListener(Listener.bw().c(Listener$ExceptionType.rJ), this::onNetworkException);
        this.registerListener(Listener.bw().c(Listener$ExceptionType.rN), this::onEntityException);
        this.registerListener(Listener.bw().c(Listener$ExceptionType.rO), this::onBlockEntityException);
        this.registerListener(Listener.bw().c(Listener$ExceptionType.rK), this::onDecodeException);
        this.registerListener(Listener.bw().c(Listener$ExceptionType.rL), this::tE);
        this.registerListener(Listener.O(), this::onServerLeave);
    }

    public void onServerLeave(Event<Void> eventVoid) {
        if (mc.player != null && this.logSelfServerLeaving.get()) {
            Debug.a("Player leaving server log:");
            Debug.e("  - Reconfiguration: ", !eventVoid.<Boolean>getArgs(0));
            Debug.a("  - Name: " + mc.player.getNameForScoreboard());
            Debug.a("  - Pos: " + mc.player.getPos());
            if (mc.world != null) {
                Debug.a("  - World: " + mc.world.getRegistryKey().getValue());
            }

            Debug.a("  - Health: " + mc.player.getHealth());
            Debug.a("  - Hand item: " + mc.player.getMainHandStack());
            Debug.a("  - Offhand item: " + mc.player.getOffHandStack());
            Debug.a("  - FallFlying: " + mc.player.isFallFlying());
            int var2 = (int) InventoryUtils.computePlayerInventory(
                    s -> s.isOf(Items.TOTEM_OF_UNDYING) ? (double) s.getCount() : null, false);
            Debug.a("  - TotemCount: " + var2);
            if (mc.world != null) {
                List<AbstractClientPlayerEntity> var3 = mc.world.getPlayers();
                Debug.a("  - Players in visual range: " + var3.size());

                for (AbstractClientPlayerEntity var6 : var3.stream()
                        .sorted(Comparator.comparingDouble(s -> s.getPos().squaredDistanceTo(mc.player.getPos())))
                        .toList()) {
                    if (var6 != mc.player) {
                        Debug.a("    - Name: "
                                + var6.getNameForScoreboard()
                                + ", Pos: "
                                + var6.getPos()
                                + ", dist: %.2f"
                                        .formatted(var6.getPos()
                                                .subtract(mc.player.getPos())
                                                .length()));
                    }
                }
            }
        }
    }

    public void tE(Event<KalamaHelperHelperE> event) {
        if (this.noDisconnectOnPacketUnexpected.get()) {
            KalamaHelperHelperE var2 = (KalamaHelperHelperE) event.e();
            PacketListener var3 = event.getArgs(0);
            Throwable var4 = var2.exception();
            if (var3 instanceof ClientPlayPacketListener var5) {
                if (mc.player != null) {
                    Debug.b(Text.literal("Error while receiving packet: ").formatted(Formatting.RED));
                    Debug.chat(
                            var4.getClass().getSimpleName(),
                            ":",
                            Text.literal(var4.getMessage() == null ? "Exception: null" : var4.getMessage()));
                }

                Debug.a("Exception StackTrace:");
                Debug.f(var4);
                event.cancel();
            }
        }
    }

    private void exitGame() {
        mc.scheduleStop();
    }

    public void onCursorLockSwitch() {
        if (mc.mouse != null) {
            if (mc.mouse.isCursorLocked()) {
                mc.mouse.unlockCursor();
            } else {
                mc.mouse.lockCursor();
            }
        }
    }

    public void onNetworkException(Event<KalamaHelperHelperE> event) {
        if (this.noDisconnectOnNetworkError.get()) {
            KalamaHelperHelperE var2 = (KalamaHelperHelperE) event.e();
            Packet var3 = event.getArgs(0);
            PacketListener var4 = event.getArgs(1);
            Throwable var5 = var2.exception();
            if (mc.player != null) {
                Debug.b(Text.literal("Error while handling a network packet: ")
                        .formatted(Formatting.RED)
                        .append(Text.literal(var3.getClass().getSimpleName())));
                Debug.chat(
                        var5.getClass().getSimpleName(),
                        ":",
                        Text.literal(var5.getMessage() == null ? "Exception: null" : var5.getMessage()));
            }

            Debug.a("Packet Exception INFO :");
            Debug.e("  PacketListener : ", var4);
            Debug.e("  Packet :", var3);
            Debug.a("Exception StackTrace:");
            Debug.f(var5);
            event.cancel();
        }
    }

    public boolean validVec3d(Vec3d vec3d) {
        return Double.isFinite(vec3d.x) && Double.isFinite(vec3d.y) && Double.isFinite(vec3d.z);
    }

    public void onEntityException(Event<KalamaHelperHelperE> event) {
        if (this.noEntityCrash.get()) {
            KalamaHelperHelperE var2 = (KalamaHelperHelperE) event.e();
            Entity var3 = event.getArgs(0);
            event.cancel();
            if (!var3.isRemoved()) {
                Throwable var4 = var2.exception();
                Debug.chat(
                        "Error while ticking entity:",
                        var3.getDisplayName(),
                        var3 instanceof PlayerEntity var5
                                ? "(%s)".formatted(var5.getNameForScoreboard())
                                : "(%s)".formatted(Registries.ENTITY_TYPE.getId(var3.getType())));
                Debug.chat(
                        var4.getClass().getSimpleName(),
                        ":",
                        Text.literal(var4.getMessage() == null ? "Exception: null" : var4.getMessage()));
                if (!this.validVec3d(var3.getPos())) {
                    Debug.b("Invalid Position detected!");
                    var3.setPosition(Vec3d.ZERO);
                }

                if (!this.validVec3d(var3.getVelocity())) {
                    Debug.b("Invalid Velocity detected!");
                    var3.setVelocity(Vec3d.ZERO);
                }

                if (!Double.isFinite(var3.getPitch()) || !Double.isFinite(var3.getYaw())) {
                    Debug.b("Invalid Rotation detected!");
                    var3.setPitch(0.0F);
                    var3.setYaw(0.0F);
                }

                Debug.a("Entity Exception INFO :");
                Debug.e("  Entity : ", var3);

                try {
                    Debug.e("  EntityNBT : ", VEntity.b(var3));
                } catch (Throwable var6) {
                }

                Debug.a("Exception StackTrace:");
                Debug.f(var4);
            }
        }
    }

    protected void checkClientData(Screen screen) {
        ScreenAccess var2 = ScreenAccess.of(mc.currentScreen);
        Screen var3 = var2 instanceof QuestionScreen ? var2.getParent() : mc.currentScreen;
        boolean var4 = this.clientCrashKeepInServer.get() && this.lastCrashTick < Tasks.b() - 10;
        if (var4
                && mc.player != null
                && mc.world != null
                && mc.inGameHud != null
                && mc.getNetworkHandler() != null
                && mc.interactionManager != null) {
            ScreenAccess.of(screen).openFrom(var3);
        } else {
            MainTasks.disconnectImmediately();
            ScreenAccess.of(screen).openFrom(var3);
        }

        this.lastCrashTick = Tasks.b();
    }
}
