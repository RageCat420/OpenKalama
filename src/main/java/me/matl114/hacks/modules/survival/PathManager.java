package me.matl114.hacks.modules.survival;

import com.mojang.serialization.DataResult;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import me.matl114.commands.MainCommand;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hooks.BaritoneHooks;
import me.matl114.managers.Configs;
import me.matl114.managers.FileManager;
import me.matl114.managers.config.ConfigEnum;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.file.FileStorage;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.CommonUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.WorldUtils;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperD;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperH;
import me.matl114.utils.commands.commandGroup.SubCommand;
import me.matl114.utils.commands.commandGroup.TaskSubCommand;
import me.matl114.utils.commands.commandGroup.TreeSubCommand;
import me.matl114.utils.commands.params.ArgumentInputStream;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.KalamaHelperHelperA;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.commands.params.api.KalamaHelperHelperF;
import me.matl114.versioned.api.VRender;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.DisplayEntity.BillboardMode;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.World;

public class PathManager extends BaseModule {
    private String w;
    private static final int p = 200;
    public final FlagRef render;
    public File C;
    boolean D;
    private static final double o = 36.0;
    private static final double j = 25.0;
    private static final double e = 9.0;
    private SurvivalSubHelperL A;
    private static final int c = 20;
    private static final int m = 50;
    private static final double k = 20.0;
    private FileStorage v;
    private static final int i = 10;
    private SurvivalSubHelperK z;
    private static final Vec3d f = new Vec3d(-0.25, -0.25, -0.25);
    List<BlockPos> G;
    private static final String h = "path_storage";
    public final DoubleRef goalDistance;
    private static final Vec3d g = new Vec3d(0.25, 0.25, 0.25);
    private static final int E = VRender.createTextPositionFlag(0, 1);
    private SurvivalSubHelperV x;
    private boolean y;
    private static final double d = 3.0;
    public static PathManager INSTANCE;
    public final EnumRef<PathManager$Mode> rerunMode;
    public final DoubleRef autoWriteDistance;
    private boolean B;
    public final ModulePath q = makePath(Configs.o, "travelling-control.path-manager");
    int lastUpdatedIndex;
    private static final double l = 4.0;
    private static final int n = 40;

    private void ad(List<BlockPos> path) {
        this.G = this.ae(path);
        BaritoneHooks.getInstance().setBaritoneNetherPathSupplier(() -> this.B ? this.G : null);
    }

    private Box makeBodyBox(Vec3d bottomCenter) {
        return new Box(
                bottomCenter.x - 0.4,
                bottomCenter.y,
                bottomCenter.z - 0.4,
                bottomCenter.x + 0.4,
                bottomCenter.y + 1.9,
                bottomCenter.z + 0.4);
    }

    private boolean isDetour(BlockPos point, BlockPos lineStart, BlockPos lineEnd) {
        Vec3d var4 = Vec3d.ofCenter(point);
        Vec3d var5 = Vec3d.ofCenter(lineStart);
        Vec3d var6 = Vec3d.ofCenter(lineEnd);
        Vec3d var7 = var6.subtract(var5);
        double var8 = var7.lengthSquared();
        if (var8 <= 0.0) {
            return false;
        } else {
            double var10 = var4.subtract(var5).dotProduct(var7) / var8;
            if (!(var10 < 0.0) && !(var10 > 1.0)) {
                Vec3d var12 = var5.add(var7.multiply(var10));
                return var4.squaredDistanceTo(var12) < 25.0;
            } else {
                return false;
            }
        }
    }

    public void onTickRunningBaritone(Event<ClientPlayerEntity> event) {
        if (this.B && this.A != null && !checkNull()) {
            if (this.rerunMode.get().isIn(new ConfigEnum[] {PathManager$Mode.BARITONE})) {
                if (!BaritoneHooks.getInstance().isEnabled()) {
                    this.ac();
                } else {
                    SurvivalSubHelperJ var2 = this.A.a();
                    if (var2 == null) {
                        this.ac();
                    } else {
                        BlockPos var3 = ((ClientPlayerEntity) event.e()).getBlockPos();
                        if (!var2.started()) {
                            this.lastUpdatedIndex = 0;
                            this.aj(var2.b());
                            var2.l();
                            if (var2.b().getSquaredDistance(var3) < 10000.0) {
                                var2.updateIndex(var3);
                                this.ad(var2.f());
                                this.aj(var2.c());
                                var2.m();
                            }
                        } else {
                            if (var2.updateIndex(var3)) {
                                this.ad(var2.f());
                                if (this.lastUpdatedIndex + 350 < var2.stuckTicks) {
                                    this.lastUpdatedIndex = var2.stuckTicks;
                                    this.aj(var2.c());
                                }
                            }

                            if (!var2.tickStuck(var3) || !this.Z(var2)) {
                                if (var3.getSquaredDistance(var2.c()) <= 10000.0) {
                                    this.ak();
                                    this.A.c();
                                    SurvivalSubHelperJ var4 = this.A.a();
                                    if (var4 == null) {
                                        this.ac();
                                        this.aj(var2.c());
                                        this.logI18N("message.module.path-manager.rerun-finished", new Object[0]);
                                        MovTasks.ay().enableControl.set(true);
                                    } else {
                                        this.aj(var4.b());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean o(CommandExecution context, ArgumentInputStream args, ArgumentReader rest) {
        if (this.v == null) {
            context.sm("&c当前没有正在录制的路径");
            return true;
        } else {
            int var4 = this.finishPath("手动停止", context);
            context.sm("&a路径录制已停止，已保存点数: " + var4);
            return true;
        }
    }

    public void g(MainCommand mainCommand) {
        TreeSubCommand var2 = mainCommand.bD().a("pathm").k();
        var2.<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                .u("start")
                .x("message.command.pathm.start.help")
                .A(KalamaHelperHelperA.a().B("path_file").v())
                .A(KalamaHelperHelperA.a().B("force").b("").l("force").v())
                .z(e -> e.executor(this::i))
                .r()
                .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                .u("restart")
                .x("message.command.pathm.restart.help")
                .A(KalamaHelperHelperA.a()
                        .B("path_file")
                        .c(KalamaHelperHelperF.g(me.matl114.utils.a.KalamaHelperHelperA.z(
                                this.C, ex -> ex.endsWith(".nbt") || ex.endsWith(".dat"))))
                        .v())
                .z(e -> e.executor(this::j))
                .r()
                .<KalamaHelperHelperD, TreeSubCommand>subBuilder(SubCommand.bp())
                .u("modify")
                .x("message.command.pathm.modify.help")
                .z(s -> s.<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                        .u("push")
                        .x("message.command.pathm.modify.push.help")
                        .z(s1 -> s1.executor(KalamaHelperHelperH.l(this::onPush)))
                        .r()
                        .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                        .u("pop")
                        .x("message.command.pathm.modify.pop.help")
                        .z(s1 -> s1.executor(KalamaHelperHelperH.l(this::l)))
                        .r()
                        .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                        .u("pause")
                        .x("message.command.pathm.modify.pause.help")
                        .z(s1 -> s1.executor(KalamaHelperHelperH.l(this::onPause)))
                        .r()
                        .subBuilder(SubCommand.bo())
                        .u("continue")
                        .x("message.command.pathm.modify.continue.help")
                        .z(s1 -> s1.executor(KalamaHelperHelperH.l(this::n)))
                        .r())
                .r()
                .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                .u("stop")
                .x("message.command.pathm.stop.help")
                .z(e -> e.executor(this::o))
                .r()
                .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                .u("load")
                .x("message.command.pathm.load.help")
                .A(KalamaHelperHelperA.a()
                        .B("path_file")
                        .c(KalamaHelperHelperF.g(me.matl114.utils.a.KalamaHelperHelperA.z(
                                this.C, ex -> ex.endsWith(".nbt") || ex.endsWith(".dat"))))
                        .v())
                .A(KalamaHelperHelperA.a().B("reverse").n(false).v())
                .z(e -> e.executor(this::p))
                .r()
                .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                .u("unload")
                .x("message.command.pathm.unload.help")
                .z(e -> e.executor(this::q))
                .r()
                .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                .u("trim")
                .x("message.command.pathm.trim.help")
                .A(KalamaHelperHelperA.a()
                        .B("path_file")
                        .c(KalamaHelperHelperF.g(me.matl114.utils.a.KalamaHelperHelperA.z(
                                this.C, ex -> ex.endsWith(".nbt") || ex.endsWith(".dat"))))
                        .v())
                .A(KalamaHelperHelperA.a().B("outfile").a(null).v())
                .z(e -> e.executor(this::t))
                .r()
                .subBuilder(SubCommand.bp())
                .u("rerun")
                .z(s -> s.<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
                        .u("start")
                        .x("message.command.pathm.rerun.start.help")
                        .z(e -> e.executor(this::r))
                        .r()
                        .subBuilder(SubCommand.bo())
                        .u("stop")
                        .x("message.command.pathm.rerun.stop.help")
                        .z(e -> e.executor(this::s))
                        .r())
                .r();
    }

    private void writePath(FileStorage storage, SurvivalSubHelperV path) {
        DataResult<?> var3 = storage.f(SurvivalSubHelperV.b, path);
        if (var3.isError()) {
            throw new IllegalArgumentException(
                    var3.error().map(error -> error.message()).orElse("未知编码错误"));
        }
    }

    private List<BlockPos> trimPath(List<BlockPos> path) {
        if (path.size() <= 2) {
            return List.copyOf(path);
        } else {
            ArrayList var2 = new ArrayList();
            int var3 = 0;

            while (var3 < path.size()) {
                BlockPos var4 = (BlockPos) path.get(var3);
                var2.add(var4.toImmutable());
                int var5 = var3 + 1;
                int var6 = Math.min(path.size() - 1, var3 + 10);

                for (int var7 = var6; var7 >= var3 + 2; var7--) {
                    if (this.isDetour(var4, (BlockPos) path.get(var7 - 1), (BlockPos) path.get(var7))) {
                        var5 = var7;
                        break;
                    }
                }

                var3 = var5;
            }

            return var2;
        }
    }

    private void onRender(Event<MatrixStack> event) {
        if (this.render.get() && !checkNull()) {
            SurvivalSubHelperJ var2 = this.A == null ? null : this.A.a();
            List var3 = var2 == null ? List.of() : var2.f();
            if (this.z != null || !var3.isEmpty()) {
                Vec3d var4 = RenderUtils.getCameraPos();
                RenderUtils.startDrawVirtual((MatrixStack) event.e());

                try {
                    if (this.z != null) {
                        Vec3d var5 = this.z.d().toCenterPos();
                        RenderUtils.drawOutlinedBox((MatrixStack) event.e(), var5.add(f), var5.add(g), Color.CYAN);
                        Vec3d var6 = var5.subtract(var4);
                        RenderUtils.m((MatrixStack) event.e(), var6, RenderUtils.getTracerOrigin(0.0F), Color.CYAN);
                        MatrixStack var7 = (MatrixStack) event.b;
                        var7.push();
                        var7.translate(var6.x, var6.y + 0.25, var6.z);
                        float var8 = (float) var6.length();
                        var7.multiply(RenderUtils.getBillboardRotation(BillboardMode.CENTER, 0.0F, 0.0F));
                        var7.scale(0.002F * var8, 0.002F * var8, 1.0F);
                        VRender.getInstance()
                                .drawTextCameraCoord(
                                        Text.literal("距离: %.1f".formatted(var8)).asOrderedText(),
                                        var7,
                                        Vec3d.ZERO,
                                        VRender.createTextPositionFlag(0, 1),
                                        Color.WHITE,
                                        VRender.a);
                        var7.pop();
                        Vec3d var9 = var5;
                        if (this.x != null) {
                            List var10 = this.x.a();
                            int var11 = var10.size();

                            for (int var12 = var11 - 1; var12 >= 0; var12--) {
                                BlockPos var13 = (BlockPos) var10.get(var12);
                                Vec3d var14 = var13.toCenterPos();
                                RenderUtils.drawOutlinedBox(
                                        (MatrixStack) event.e(), var14.add(f), var14.add(g), Color.CYAN);
                                RenderUtils.l((MatrixStack) event.e(), var14, var9, Color.CYAN);
                                var9 = var14;
                                if (var13.getSquaredDistance(var4) > MathUtils.a(this.autoWriteDistance.get() * 2.0)) {
                                    break;
                                }
                            }
                        }
                    }

                    if (!var3.isEmpty()) {
                        this.renderPathSegment((MatrixStack) event.e(), var3, Color.GREEN);
                    }
                } finally {
                    RenderUtils.stopDrawVirtual((MatrixStack) event.e());
                }
            }
        }
    }

    private boolean isChunkLoaded(BlockPos pos) {
        return mc.world != null && WorldUtils.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4);
    }

    private void trimPathFile(
            CommandExecution context, String pathFile, File file, File outputPathFile, File outputFile) {
        SurvivalSubHelperV var7;
        try {
            label82:
            {
                try (FileStorage var6 = FileManager.getInstance().j(file, true, false)) {
                    if (var6 != null) {
                        var6.h();
                        var7 = this.readPath(var6);
                        break label82;
                    }

                    context.sm("&c路径文件不存在: " + pathFile);
                }

                return;
            }
        } catch (Throwable var16) {
            Debug.a("PathManager failed to read path for trim: " + pathFile);
            Debug.f(var16);
            context.sm("&c路径读取失败: " + pathFile);
            return;
        }

        if (var7 != null && !var7.a().isEmpty()) {
            List var17 = this.trimPath(var7.a());

            try (FileStorage var8 =
                    FileManager.getInstance().j(outputFile, false, true).t()) {
                this.writePath(var8, new SurvivalSubHelperV(var17, var7.b(), var7.c()));
                context.sm("&a路径修剪完成: " + pathFile + " -> " + outputPathFile + "，原点数: "
                        + var7.a().size() + "，现点数: " + var17.size());
            } catch (Throwable var14) {
                Debug.a("PathManager failed to write trimmed path: " + outputPathFile);
                Debug.f(var14);
                context.sm("&c路径写入失败: " + outputPathFile);
            }
        } else {
            context.sm("&c路径文件为空或格式不正确: " + pathFile);
        }
    }

    private boolean t(CommandExecution context, ArgumentInputStream args, ArgumentReader rest) {
        String var4 = args.o();
        String var5 = args.f();
        if (var5 == null || var5.isBlank()) {
            var5 = var4;
        }

        File var6 = new File(this.C, var4);
        File var7 = new File(this.C, var5);
        if (!var6.exists()) {
            context.sm("&c路径文件不存在: " + var4);
            return true;
        } else if (this.v == null || !this.v.q().equals(var6) && !this.v.q().equals(var7)) {
            context.sm("&a开始修剪路径: " + var4 + " -> " + var5);
            CompletableFuture.runAsync(() -> this.trimPathFile(context, var4, var6, var7, var7));
            return true;
        } else {
            context.sm("&c该路径正在录制中，无法修剪: " + this.w);
            return true;
        }
    }

    private int findFarthestUnloadedIndex(SurvivalSubHelperJ currentSeg) {
        int var2 = currentSeg.currentIndex();
        int var3 = Math.min(var2 + 20, currentSeg.a().size() - 1);

        for (int var4 = var2; var4 <= var3; var4++) {
            if (!this.isChunkLoaded(currentSeg.a().get(var4))) {
                return var4;
            }
        }

        return -1;
    }

    private BlockPos adjustPathPointByEnvironment(BlockPos pos) {
        if (pos != null && mc.world != null) {
            int var2 = this.firstNonAirDistance(pos, 1);
            int var3 = this.firstNonAirDistance(pos, -1);
            if (var2 > var3 && var3 < 3) {
                return pos.add(0, var2, 0).toImmutable();
            } else {
                return var3 > var2 && var2 < 3 ? pos.add(0, -var3, 0).toImmutable() : pos.toImmutable();
            }
        } else {
            return pos;
        }
    }

    private void ac() {
        if (this.B) {
            this.B = false;
            this.ak();
            BaritoneHooks.getInstance().cancelBaritone();
            if (this.A != null) {
                this.A.f();
            }
        }
    }

    public void onTickRunningBaritoneGoal(Event<ClientPlayerEntity> event) {
        if (this.B && this.A != null && !checkNull()) {
            if (this.rerunMode.get().isIn(new ConfigEnum[] {PathManager$Mode.BARITONE_GOAL})) {
                if (!BaritoneHooks.getInstance().isEnabled()) {
                    this.ac();
                } else {
                    SurvivalSubHelperJ var2 = this.A.a();
                    if (var2 == null) {
                        this.ac();
                    } else {
                        BlockPos var3 = ((ClientPlayerEntity) event.e()).getBlockPos();
                        if (!var2.started()) {
                            this.aj(var2.b());
                            var2.l();
                            if (var2.b().getSquaredDistance(var3) < 10000.0) {
                                var2.updateGoalIndex(var3, this.goalDistance.get());
                                this.aj(var2.g());
                                var2.m();
                            }
                        } else {
                            if (var3.getSquaredDistance(var2.g()) < 10000.0) {
                                var2.updateGoalIndex(var3, this.goalDistance.get());
                                this.aj(var2.g());
                            }

                            if (var3.getSquaredDistance(var2.c()) <= 10000.0) {
                                this.ak();
                                this.A.c();
                                SurvivalSubHelperJ var4 = this.A.a();
                                if (var4 == null) {
                                    this.ac();
                                    this.aj(var2.c());
                                    this.logI18N("message.module.path-manager.rerun-finished", new Object[0]);
                                    MovTasks.ay().enableControl.set(true);
                                } else {
                                    this.aj(var4.b());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void addCustomWidgets(Consumer<DrawableWidget> acceptor, int dx, int dy, int dblank) {
        super.addCustomWidgets(acceptor, dx, dy, dblank);
        acceptor.accept(this.createTitleLabel("widget.path-manager.command", 0, dblank, dx, dy));
    }

    @Override
    public void unregisterAll() {
        super.unregisterAll();
        this.finishPath("模块卸载", null);
        this.ac();
    }

    private List<BlockPos> ae(List<BlockPos> path) {
        if (path != null && !path.isEmpty()) {
            int var2 = Math.min(path.size(), 400);
            ArrayList var3 = new ArrayList(var2);

            for (BlockPos var5 : path.subList(0, var2)) {
                var3.add(this.adjustPathPointByEnvironment(var5));
            }

            return List.copyOf(var3);
        } else {
            return List.of();
        }
    }

    public void n(CommandExecution context) {
        if (this.v != null && this.x != null) {
            this.D = false;
            context.sm("&a当前记录已继续");
        } else {
            context.sm("&c当前没有正在录制的路径");
        }
    }

    private void onPreTick(Event<ClientPlayerEntity> event) {
        if (this.v != null) {
            if (checkNull()) {
                this.finishPath("录制任务意外退出", null);
            } else {
                ClientPlayerEntity var2 = mc.player;
                if (!this.y) {
                    if (!var2.isFallFlying()) {
                        return;
                    }

                    this.startSnapshot(var2.getBlockPos());
                    this.y = true;
                    return;
                }

                this.recordCurrentPosition(var2.getBlockPos());
            }
        }
    }

    public void onPause(CommandExecution context) {
        if (this.v != null && this.x != null) {
            this.D = true;
            context.sn(Text.literal("&a当前记录已暂停, 输入!!pathm modify continue (点击该文本以补全)继续录制")
                    .styled(s -> s.withClickEvent(ChatUtils.getSuggestCommand(
                            MainCommand.getMainCommandPrefix() + "pathm modify continue"))));
        } else {
            context.sm("&c当前没有正在录制的路径");
        }
    }

    public List<BlockPos> S() {
        return this.A == null ? List.of() : this.A.e();
    }

    private SurvivalSubHelperV readPath(FileStorage storage) {
        DataResult<?> var2 = storage.e(SurvivalSubHelperV.b);
        if (var2.isError()) {
            Debug.a("PathManager failed to decode path: "
                    + var2.error().map(error -> error.message()).orElse("未知解码错误"));
            return null;
        } else {
            return (SurvivalSubHelperV) var2.result().orElse(null);
        }
    }

    private boolean q(CommandExecution context, ArgumentInputStream args, ArgumentReader rest) {
        if (this.A == null) {
            context.sm("&c当前没有正在加载的路径");
            return true;
        } else {
            this.A = null;
            this.ac();
            context.sm("&c已卸载当前路径");
            return true;
        }
    }

    private void onPush(CommandExecution context) {
        if (this.v != null && this.x != null) {
            this.startSnapshot(mc.player.getBlockPos());
            context.sm("&a当前位置以添加");
        } else {
            context.sm("&c当前没有正在录制的路径");
        }
    }

    private boolean hasClearLine(Vec3d from, Vec3d to) {
        BlockHitResult var3 =
                mc.world.raycast(new RaycastContext(from, to, ShapeType.COLLIDER, FluidHandling.NONE, mc.player));
        return var3.getType() != Type.BLOCK;
    }

    private boolean s(CommandExecution context, ArgumentInputStream args, ArgumentReader rest) {
        if (this.A == null) {
            context.sm("&c当前没有正在加载的路径");
            return true;
        } else if (!this.B) {
            context.sm("&c当前没有正在执行的路径");
            return true;
        } else {
            this.ac();
            context.sm("&c当前执行路径已终止");
            return true;
        }
    }

    private boolean p(CommandExecution context, ArgumentInputStream args, ArgumentReader rest) {
        if (checkNull()) {
            context.sm("&c当前不在游戏内，无法加载路径");
            return true;
        } else {
            String var4 = args.o();
            boolean var5 = args.h();

            SurvivalSubHelperV var8;
            try (FileStorage var6 = FileManager.getInstance().j(new File(this.C, var4), true, false)) {
                if (var6 == null) {
                    context.sm("&c路径文件不存在: " + var4);
                    return true;
                }

                var6.h();
                var8 = this.readPath(var6);
            }

            if (var8 != null && !var8.a().isEmpty()) {
                String var14 = CommonUtils.getServerName();
                String var7 = currentWorldKey();
                if (!Objects.equals(var8.c(), var14)) {
                    context.sm("&e路径服务器不一致: 文件=" + var8.c() + " 当前=" + var14 + "，仍继续加载");
                }

                if (!Objects.equals(var8.b(), var7)) {
                    context.sm("&c路径维度不一致: 文件=" + var8.b() + " 当前=" + var7 + "，已取消加载");
                    return true;
                } else {
                    ArrayList var9 = new ArrayList<>(var8.a());
                    if (var5) {
                        Collections.reverse(var9);
                    }

                    List var10 = this.alignPathToNearest(var9);
                    boolean var11 = this.rerunMode.get().isIn(new ConfigEnum[] {PathManager$Mode.BARITONE});
                    this.A = this.createRerunContext(var4, var10, var11);
                    this.ac();
                    context.sm("&a已加载路径: " + var4 + ", 当前位置 %d / %d".formatted(var9.size() - var10.size(), var9.size())
                            + "，剩余段数: " + this.A.remainingSegments());
                    return true;
                }
            } else {
                context.sm("&c路径文件为空或格式不正确: " + var4);
                return true;
            }
        }
    }

    private int firstNonAirDistance(BlockPos pos, int direction) {
        for (int var3 = 1; var3 <= 3; var3++) {
            BlockPos var4 = pos.add(0, direction * var3, 0);
            if (this.isLoadedNonAir(var4)) {
                return var3;
            }
        }

        return 0;
    }

    private SurvivalSubHelperL createRerunContext(String pathFile, List<BlockPos> path, boolean cut) {
        return new SurvivalSubHelperL(pathFile, this.splitPathSegments(path, cut));
    }

    private boolean Z(SurvivalSubHelperJ currentSeg) {
        int var2 = this.findFarthestUnloadedIndex(currentSeg);
        if (var2 < 0) {
            currentSeg.i();
            return false;
        } else {
            BlockPos var3 = currentSeg.a().get(var2);
            this.ak();
            this.aj(var3);
            this.A.replaceCurrentSegment(new SurvivalSubHelperJ(
                    currentSeg.a().subList(var2, currentSeg.a().size()), currentSeg.i));
            return true;
        }
    }

    private boolean shouldWriteBeforeCurrent(SurvivalSubHelperK snapshot, BlockPos current) {
        if (this.D) {
            return false;
        } else {
            double var3 = this.autoWriteDistance.get();
            double var5 = snapshot.d().getSquaredDistance(current);
            return var5 < MathUtils.a(var3) ? !this.canSee(snapshot.d(), current) : true;
        }
    }

    public static String currentWorldKey() {
        return mc.world == null ? "" : mc.world.getRegistryKey().getValue().toString();
    }

    private void startSnapshot(BlockPos pos) {
        if (this.x != null && pos != null) {
            List var2 = this.x.a();
            if (var2.isEmpty() || !((BlockPos) var2.getLast()).equals(pos)) {
                var2.add(pos.toImmutable());
            }

            if (this.v != null) {
                this.v.f(SurvivalSubHelperV.b, this.x);
            }

            this.z = new SurvivalSubHelperK(pos);
        }
    }

    private void aj(BlockPos pos) {
        BaritoneHooks.getInstance().setBaritoneCurrentElytraDestination(pos);
    }

    private void ai(BlockPos pos) {}

    private boolean isLoadedNonAir(BlockPos pos) {
        if (!this.isChunkLoaded(pos)) {
            return false;
        } else {
            BlockState var2 = mc.world.getBlockState(pos);
            return !var2.isAir() && !var2.isLiquid();
        }
    }

    private void recordCurrentPosition(BlockPos current) {
        if (this.z == null) {
            this.startSnapshot(current);
        } else {
            SurvivalSubHelperK var2 = this.z;
            var2.tick();
            if (this.K(var2, current)) {
                if (this.z != null) {
                    this.z.updateLastPosition(current);
                }
            } else if (!current.equals(var2.e())) {
                if (this.shouldWriteBeforeCurrent(var2, current)) {
                    BlockPos var3 = var2.e();
                    this.startSnapshot(var3);
                    this.z.updateLastPosition(current);
                } else {
                    var2.updateLastPosition(current);
                }
            }
        }
    }

    private boolean r(CommandExecution context, ArgumentInputStream args, ArgumentReader rest) {
        if (this.A == null) {
            context.sm("&c当前没有正在加载的路径");
            return true;
        } else if (this.B) {
            context.sm("&c当前有正在执行的路径, 请使用指令!!rerun stop终止");
            return true;
        } else {
            this.B = true;
            if (this.rerunMode.get().isIn(new ConfigEnum[] {PathManager$Mode.ELYTRA_FLIGHT})) {
                context.sm("&c当前执行类型为 ELYTRA_FLIGHT, 暂时不支持,请切换为 BARITONE 模式以使用");
                this.B = false;
                return true;
            } else {
                context.sm("&a开始重新执行当前路径");
                return true;
            }
        }
    }

    private List<BlockPos> alignPathToNearest(List<BlockPos> path) {
        if (checkNull()) {
            return List.copyOf(path);
        } else {
            BlockPos var2 = mc.player.getBlockPos();
            int var3 = 0;
            double var4 = Double.MAX_VALUE;

            for (int var6 = 0; var6 < path.size(); var6++) {
                double var7 = ((BlockPos) path.get(var6)).getSquaredDistance(var2);
                if (var7 < var4) {
                    var4 = var7;
                    var3 = var6;
                }
            }

            return List.copyOf(path.subList(var3, path.size()));
        }
    }

    private boolean j(CommandExecution context, ArgumentInputStream args, ArgumentReader rest) {
        if (checkNull()) {
            context.sm("&c当前不在游戏内，无法开始路径录制");
            return true;
        } else if (this.v != null) {
            context.sm("&c当前已经在录制路径: " + this.w);
            return true;
        } else {
            String var4 = args.o();
            FileStorage var5 = FileManager.getInstance().j(new File(this.C, var4), true, false);
            if (var5 == null) {
                context.sm("&c路径文件不存在: " + var4);
                return true;
            } else {
                SurvivalSubHelperV var6 = this.readPath(var5);
                if (var6 != null && !var6.a().isEmpty()) {
                    String var7 = CommonUtils.getServerName();
                    String var8 = currentWorldKey();
                    if (!Objects.equals(var6.c(), var7)) {
                        context.sm("&e路径服务器不一致: 文件=" + var6.c() + " 当前=" + var7 + "，仍继续加载");
                    }

                    if (!Objects.equals(var6.b(), var8)) {
                        context.sm("&c路径维度不一致: 文件=" + var6.b() + " 当前=" + var8 + "，已取消加载");
                        var5.n(true);
                        return true;
                    } else {
                        this.E(var4, var5, var6.a());
                        context.sm("&a已载入历史路线记录，路径文件: " + var4);
                        this.onPause(context);
                        return true;
                    }
                } else {
                    context.sm("&c路径文件为空或格式不正确: " + var4);
                    var5.n(true);
                    return true;
                }
            }
        }
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerCommandBootstrap(this::g);
        this.registerListener(Listener.U(), this::onPreTick);
        this.registerListener(Listener.U(), this::onTickRunningBaritone);
        this.registerListener(Listener.N(), this::y);
        this.registerListener(Listener.O(), this::z);
        this.registerListener(Listener.ap().getChannel(PlayerRespawnS2CPacket.class), this::A);
        this.registerListener(RenderListener.q(), this::onRender);
        this.registerListener(Listener.U(), this::onTickRunningBaritoneGoal);
    }

    private List<SurvivalSubHelperJ> splitPathSegments(List<BlockPos> path, boolean cut) {
        if (path.isEmpty()) {
            return List.of();
        } else {
            double var3 = MathUtils.a(this.autoWriteDistance.get() + 20.0);
            ArrayList var5 = new ArrayList();
            ArrayList var6 = new ArrayList();
            BlockPos var7 = null;

            for (BlockPos var9 : path) {
                if (var9 != null) {
                    BlockPos var10 = var9.toImmutable();
                    if (var6.isEmpty()) {
                        var6.add(var10);
                        var7 = var10;
                    } else {
                        if (var7.getSquaredDistance(var10) > var3) {
                            var5.add(new SurvivalSubHelperJ(var6, cut));
                            var6 = new ArrayList();
                        }

                        var6.add(var10);
                        var7 = var10;
                    }
                }
            }

            if (!var6.isEmpty()) {
                var5.add(new SurvivalSubHelperJ(var6, cut));
            }

            return List.copyOf(var5);
        }
    }

    private void F(FileStorage storage) {
        if (storage != null) {
            storage.n(true);
        }

        this.v = null;
        this.w = null;
        this.x = null;
        this.y = false;
        this.z = null;
    }

    private void A(Event<PlayerRespawnS2CPacket> event) {
        this.finishPath("玩家重生", null);
        this.A = null;
        this.ac();
    }

    private void y(Event<World> event) {
        this.finishPath("切换世界", null);
        this.A = null;
        this.ac();
    }

    private void ak() {
        this.G = null;
        BaritoneHooks.getInstance().setBaritoneNetherPathSupplier(null);
    }

    private void z(Event<Void> event) {
        this.finishPath("断开连接", null);
        this.A = null;
        this.ac();
    }

    private Vec3d[] makeBodyCorners(Box box) {
        return new Vec3d[] {
            new Vec3d(box.minX, box.minY, box.minZ),
            new Vec3d(box.maxX, box.minY, box.minZ),
            new Vec3d(box.minX, box.maxY, box.minZ),
            new Vec3d(box.maxX, box.maxY, box.minZ),
            new Vec3d(box.minX, box.minY, box.maxZ),
            new Vec3d(box.maxX, box.minY, box.maxZ),
            new Vec3d(box.minX, box.maxY, box.maxZ),
            new Vec3d(box.maxX, box.maxY, box.maxZ)
        };
    }

    private void renderPathSegment(MatrixStack matrices, List<BlockPos> path, Color color) {
        Vec3d var4 = null;

        for (BlockPos var6 : path) {
            Vec3d var7 = var6.toCenterPos();
            RenderUtils.drawOutlinedBox(matrices, var7.add(f), var7.add(g), color);
            if (var4 != null) {
                RenderUtils.l(matrices, var4, var7, color);
            }

            var4 = var7;
            if (var7.squaredDistanceTo(mc.player.getPos()) > MathUtils.a(this.autoWriteDistance.get() * 8.0)) {
                break;
            }
        }
    }

    private int finishPath(String reason, CommandExecution context) {
        if (this.v == null) {
            return 0;
        } else {
            FileStorage var3 = this.v;
            String var4 = this.w;
            int var5 = this.x == null ? 0 : this.x.a().size();

            try {
                var5 = this.x == null ? 0 : this.x.a().size();
                this.writePath(var3, this.x == null ? new SurvivalSubHelperV(List.of()) : this.x);
                var3.g();
                if (context != null) {
                    context.sm("&a路径已保存: " + var4 + "，原因: " + reason);
                }
            } catch (Throwable var10) {
                Debug.a("PathManager failed to save path: " + var4);
                Debug.f(var10);
                if (context != null) {
                    context.sm("&c路径保存失败: " + var4);
                }
            } finally {
                this.F(var3);
            }

            return var5;
        }
    }

    public void l(CommandExecution context) {
        if (this.v == null || this.x == null) {
            context.sm("&c当前没有正在录制的路径");
        } else if (this.x.c.isEmpty()) {
            context.sm("&c当前没有多余的路径点");
        } else {
            this.x.c.remove(this.x.c.size() - 1);
            this.restartSnapshot();
            context.sm("&a当前位置以添加");
        }
    }

    private boolean K(SurvivalSubHelperK snapshot, BlockPos current) {
        if (this.x != null && this.x.a().size() > 1 && snapshot.shouldRollback(current)) {
            List var3 = this.x.a();
            BlockPos var4 = (BlockPos) var3.get(var3.size() - 2);
            if (!this.canSee(var4, current)) {
                return false;
            } else {
                var3.remove(var3.size() - 1);
                this.z = new SurvivalSubHelperK((BlockPos) var3.getLast());
                return true;
            }
        } else {
            return false;
        }
    }

    private boolean i(CommandExecution context, ArgumentInputStream args, ArgumentReader rest) {
        if (checkNull()) {
            context.sm("&c当前不在游戏内，无法开始路径录制");
            return true;
        } else if (this.v != null) {
            context.sm("&c当前已经在录制路径: " + this.w);
            return true;
        } else {
            String var4 = args.o();
            String var5 = args.o();
            if (!var4.endsWith(".nbt")) {
                var4 = var4 + ".nbt";
            }

            File var6 = new File(this.C, var4);
            if (!"force".equals(var5) && var6.exists()) {
                context.sm("&c路径文件已存在: " + var4 + "，请输入 force 或更换文件名");
                return true;
            } else {
                this.D(var4, FileManager.getInstance().n(var6));
                context.sm("&a开始等待鞘翅飞行，路径文件: " + var4);
                return true;
            }
        }
    }

    private void D(String pathFile, FileStorage storage) {
        this.v = storage;
        this.w = pathFile;
        this.x = new SurvivalSubHelperV(new ArrayList<>());
        this.y = false;
        this.z = null;
        this.D = false;
    }

    private boolean canSee(BlockPos from, BlockPos to) {
        if (mc.world != null && mc.player != null) {
            if (from.getSquaredDistance(to) > MathUtils.a(this.autoWriteDistance.get() + 10.0)) {
                return false;
            } else {
                Vec3d[] var3 = this.makeBodyCorners(this.makeBodyBox(Vec3d.ofBottomCenter(from)));
                Vec3d[] var4 = this.makeBodyCorners(this.makeBodyBox(Vec3d.ofBottomCenter(to)));

                for (int var5 = 0; var5 < var3.length; var5++) {
                    if (!this.hasClearLine(var3[var5], var4[var5])) {
                        return false;
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public PathManager() {
        super("PathManager");
        this.autoWriteDistance = this.doubleBuilder(this.q.add("auto-write-distance"))
                .defaultValue(80.0)
                .validator(value -> value > 0.0)
                .build();
        this.render = this.flagBuilder(this.q.add("render")).build();
        this.rerunMode = this.builder(this.q.add("rerun-mode"), PathManager$Mode.class)
                .defaultValue(PathManager$Mode.ELYTRA_FLIGHT)
                .build();
        this.goalDistance = this.doubleBuilder(this.q.add("goal-distance"))
                .defaultValue(400.0)
                .validator(Configs.doubleRange(100.0, 4.0E10))
                .show(() -> this.rerunMode.get().isIn(new ConfigEnum[] {PathManager$Mode.BARITONE_GOAL}))
                .build();
        this.B = false;
        this.C = FileManager.getInstance().c("path_storage");
        this.D = false;
        this.lastUpdatedIndex = 0;
    }

    private void restartSnapshot() {
        if (this.x != null && !this.x.c.isEmpty()) {
            BlockPos var1 = this.x.a().get(this.x.c.size() - 1);
            this.z = new SurvivalSubHelperK(var1);
            this.z.b = mc.player.getBlockPos();
        } else {
            this.startSnapshot(mc.player.getBlockPos());
        }
    }

    private void E(String pathFile, FileStorage storage, List<BlockPos> blockPos) {
        this.v = storage;
        this.w = pathFile;
        this.x = new SurvivalSubHelperV(new ArrayList<>(blockPos));
        this.y = true;
        this.z = null;
        this.restartSnapshot();
    }
}
