package me.matl114.hacks.modules.mine;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import me.matl114.accessors.hacks.PlayerInteractionAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.WrapColor;
import me.matl114.hacks.utils.render.RenderCollectors;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.render.RenderCollector;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class QueueMine extends BaseModule {
    public final FlagRef doubleBreakGhostHand;
    public final FlagRef render;
    int lastSumitTick;
    public final KeyBindRef J;
    public final Queue<BlockPos> pm;
    public final FlagRef ae;
    public final NBTRef<WrapColor> renderColor;
    RenderCollector<Box> po;
    public final IntRef queueSize;
    public static QueueMine INSTANCE;
    public ModulePath H = makePath(Configs.g, "queue-mine");
    public final FlagRef doubleBreak;

    public void B(Event<MatrixStack> eventRender) {
        if (this.render.get()) {
            RenderUtils.startDrawVirtual((MatrixStack) eventRender.b);

            try {
                this.po.a((MatrixStack) eventRender.b);
            } finally {
                RenderUtils.stopDrawVirtual((MatrixStack) eventRender.b);
            }
        }
    }

    public boolean sumitMine(BlockPos pos) {
        if (!this.pm.isEmpty() && this.pm.stream().anyMatch(pos::equals)) {
            return false;
        } else if (Objects.equals(
                PlayerInteractionAccess.of(mc.interactionManager).getCurrentFailBreakPos(), pos)) {
            return false;
        } else {
            this.pm.add(pos);
            return true;
        }
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.be(), this::onPostInputEvent);
        this.registerListener(Listener.bl(), this::handleQueueSumbit);
        this.registerListener(RenderListener.q(), this::B);
    }

    public void onPostInputEvent(Event<Void> eventVoid) {
        this.po.clear();
        if (!checkNull()) {
            if (this.lastSumitTick != Tasks.b()) {
                this.xE();
            }

            if (this.doubleBreak.get()
                    && this.doubleBreakGhostHand.get()
                    && PlayerInteractionAccess.of(mc.interactionManager).getCurrentFailBreakPos() != null) {
                PacketMine.INSTANCE.tickGhostHandDoubleBreak(null, false);
            }

            for (BlockPos var3 : this.pm) {
                this.po.submit(
                        new Box(var3).expand(-0.2), this.renderColor.get().withAlpha(255));
            }
        }
    }

    public QueueMine() {
        super("QueueMine");
        this.ae = this.flagBuilder(this.H.addEnable()).build();
        this.J = this.toggleHotkey(this.H.addHotkey(), new MultiKeyBind(), this.H.addEnable())
                .build();
        this.queueSize =
                this.intBuilder(this.H.add("queue-size")).defaultValue(2).build();
        this.doubleBreak = this.flagBuilder(this.H.add("double-break")).build();
        this.doubleBreakGhostHand =
                this.flagBuilder(this.H.add("double-break-ghost-hand")).build();
        this.render = this.flagBuilder(this.H.add("render")).build();
        this.renderColor = this.builder(this.H.add("render-color"), WrapColor.class)
                .defaultValue(new WrapColor(Color.GREEN))
                .build();
        this.pm = new ArrayDeque<>();
        this.lastSumitTick = 0;
        this.po = RenderCollectors.createBoxCollector(true, false, false);
        INSTANCE = this;
        this.bindFlag(this.ae);
    }

    public void handleQueueSumbit(Event<HitResult> event) {
        if (this.ae.get()) {
            HitResult var2 = (HitResult) event.b;
            if (var2.getType() == Type.BLOCK) {
                boolean var3 = event.<Boolean>getArgs(0);
                BlockHitResult var4 = (BlockHitResult) var2;
                BlockPos var5 = var4.getBlockPos().toImmutable();
                event.cancel();
                if (this.sumitMine(var5)) {
                    this.lastSumitTick = Tasks.b();
                    this.xE();
                    if (this.pm.size() > this.queueSize.get()) {
                        this.pm.poll();
                    }
                }
            }
        }
    }

    public void xE() {
        if (MineExtra.INSTANCE.ade() <= 0) {
            PlayerInteractionAccess var1 = PlayerInteractionAccess.of(mc.interactionManager);
            if (var1 != null) {
                if (!this.doubleBreak.get() || MineExtra.INSTANCE.adc(5) || MineExtra.INSTANCE.add(1)) {
                    boolean var2 = var1.getCurrentFailBreakPos() == null && this.doubleBreak.get();

                    while (!this.pm.isEmpty()) {
                        BlockPos var3 = this.pm.peek();
                        if (new Box(var3).squaredMagnitude(mc.player.getEyePos())
                                > MathUtils.a(mc.player.getBlockInteractionRange() + 1.0)) {
                            this.pm.poll();
                        } else {
                            BlockState var4 = mc.world.getBlockState(var3);
                            if (!(var4.getBlock().getHardness() < 0.0F) && !var4.isLiquid() && !var4.isAir()) {
                                BlockPos var5 = var1.getCurrentMiningPos();
                                if (!Objects.equals(var5, var3)) {
                                    var1.sendStartBreakPacket(var3);
                                    if (var2) {
                                        var1.sendFailBreakCurrentPos(null);
                                        var2 = false;
                                        this.pm.poll();
                                    } else {
                                        if (Objects.equals(var1.getCurrentMiningPos(), var3)) {
                                            var1.sendAbortBreakPacket();
                                            break;
                                        }

                                        this.pm.poll();
                                    }
                                } else {
                                    if (!var2) {
                                        break;
                                    }

                                    var1.sendFailBreakCurrentPos(null);
                                    var2 = false;
                                    this.pm.poll();
                                }
                            } else {
                                this.pm.poll();
                            }
                        }
                    }

                    if (!this.pm.isEmpty()) {
                        BlockPos var6 = this.pm.peek();
                        if (Objects.equals(var6, var1.getCurrentMiningPos()) && var1.breakIfComplete()) {
                            this.pm.poll();
                        }
                    }
                }
            }
        }
    }
}
