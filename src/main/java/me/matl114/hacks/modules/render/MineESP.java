package me.matl114.hacks.modules.render;

import java.awt.Color;
import java.util.Comparator;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.hacks.RenderTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.mine.MineSubHelperH;
import me.matl114.hacks.modules.mine.MiningProgressManager;
import me.matl114.hacks.modules.move.MoveSubHelperL;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.config.WidgetPos;
import me.matl114.hacks.utils.config.WrapColor;
import me.matl114.hacks.utils.render.HackUtilHelperB;
import me.matl114.hacks.utils.render.RenderCollectors;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.ColorUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.WorldUtils;
import me.matl114.utils.inventory.ItemStackSample;
import me.matl114.utils.render.RenderCollector;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;

public class MineESP extends BaseModule {
    public final NBTRef<WrapColor> gridAirColor;
    public final IntRef gridGap;
    public final FlagRef renderGrid2d;
    public final NBTRef<WrapColor> frameColor;
    public final IntRef gridCellSize;
    final RenderCollector<HackUtilHelperB> lg;
    private static final int uV = 5;
    public final NBTRef<WrapColor> nameColor;
    public final FlagRef renderBox;
    public final NBTRef<WrapColor> gridDoubleColor;
    public final NBTRef<WrapColor> frameDoubleColor;
    public final FlagRef ghostHandPredict;
    public final NBTRef<WrapColor> progressColor;
    public final NBTRef<WrapColor> gridSolidColor;
    public final ModulePath aD = makePath(Configs.i, "mine-render.mine-esp");
    public final NBTRef<WidgetPos> gridPos;
    final RenderCollector<Box> vm;
    public final NBTRef<WrapColor> gridBreakingColor;
    public final KeyBindRef J;
    public final FlagRef renderName;
    final RenderCollector<Box> vn;
    private static final int uW = 3;
    public final FlagRef ae = this.flagBuilder(this.aD.addEnable()).build();

    private int GG(MineSubHelperH tracker) {
        return tracker.e == null ? -1 : Math.clamp((long) (tracker.f * 10), 0, 100);
    }

    private int Gu() {
        return this.getGridPixelSize(5);
    }

    private void renderProjectedGrid(VDrawContext vdraw, RenderSubHelperJ[][] states, double centerX, double centerY) {
        int var7 = this.gridCellSize.get();
        int var8 = this.gridGap.get();
        int var9 = ColorUtils.getColorInt(255, 255, 255, 96);
        int var10 = ColorUtils.getColorInt(0, 0, 0, 96);
        int var11 = this.Gu();
        int var12 = this.Gw();
        int var13 = (int) (centerX - this.getGridLimitSize());
        int var14 = (int) (centerX + this.getGridLimitSize());
        int var15 = (int) (centerY - this.getGridLimitSize());
        int var16 = (int) (centerY + this.getGridLimitSize());
        this.fillLimited(
                vdraw, var12 - 2, var12 - 2, var12 + var11 + 2, var12 + var11 + 2, var10, var13, var15, var14, var16);

        for (int var17 = 0; var17 < 5; var17++) {
            for (int var18 = 0; var18 < 5; var18++) {
                int var19 = var12 + var18 * (var7 + var8);
                int var20 = var12 + var17 * (var7 + var8);
                int var21 = var19 + var7;
                int var22 = var20 + var7;
                int var23 = this.GH(states[var17][var18].UE());
                this.fillLimited(vdraw, var19, var20, var21, var22, var23, var13, var15, var14, var16);
                this.fillLimited(vdraw, var19, var20, var21, var20 + 1, var9, var13, var15, var14, var16);
                this.fillLimited(vdraw, var19, var22 - 1, var21, var22, var9, var13, var15, var14, var16);
                this.fillLimited(vdraw, var19, var20, var19 + 1, var22, var9, var13, var15, var14, var16);
                this.fillLimited(vdraw, var21 - 1, var20, var21, var22, var9, var13, var15, var14, var16);
            }
        }
    }

    private double getGridLimitSize() {
        return 2.0 * (this.gridCellSize.get() + this.gridGap.get());
    }

    public float predictGhostHandBreakSpeed(PlayerEntity player, BlockPos pos) {
        MoveSubHelperL var3 = PlayerStateManager.INSTANCE.nZ(player);
        BlockState var4 = mc.world.getBlockState(pos);
        ItemStack var5 = var3.j.stream()
                .max(Comparator.comparingDouble(
                        s -> WorldUtils.getPlayerBlockBreakingSpeedWithCanMineMultiply(player, var4, s.fS())))
                .map(ItemStackSample::fS)
                .orElse(ItemStack.EMPTY);
        float var6 = WorldUtils.getPlayerBlockBreakingSpeedWithCanMineMultiply(player, var4, var5);
        return WorldUtils.calcBlockBreakingDelta(var4, mc.world, pos, var6);
    }

    private int Gw() {
        return -((int) (2.5 * (this.gridCellSize.get() + this.gridGap.get())));
    }

    private RenderSubHelperD rotatePoint(double x, double y, float rotationRadians) {
        double var6 = Math.sin(rotationRadians);
        double var8 = Math.cos(rotationRadians);
        return new RenderSubHelperD(x * var8 - y * var6, x * var6 + y * var8);
    }

    public void Gn(Event<MatrixStack> event) {
        if (!checkNull()) {
            if (this.ae.get()) {
                RenderUtils.startDrawVirtual((MatrixStack) event.b);

                try {
                    this.vm.a((MatrixStack) event.b);
                    this.vn.a((MatrixStack) event.b);
                    this.lg.a((MatrixStack) event.b);
                } finally {
                    RenderUtils.stopDrawVirtual((MatrixStack) event.b);
                }
            }
        }
    }

    private RenderSubHelperJ projectColumnState(int x, int z, int minY, int maxYExclusive, int[] occupiedBlocks) {
        boolean var6 = false;
        MineSubHelperH var7 = null;
        RenderSubHelperQX var8 = null;
        int var9 = -1;

        for (int var10 = minY; var10 < maxYExclusive; var10++) {
            BlockPos var11 = new BlockPos(x, var10, z);
            BlockState var12 = mc.world.getBlockState(var11);
            if (!var12.isAir() && !var12.isLiquid() && var12.isFullCube(mc.world, var11)) {
                occupiedBlocks[0]++;
                var6 = true;
            }

            for (MineSubHelperH var14 :
                    MiningProgressManager.INSTANCE.getBreakingMap().values()) {
                if (var14.b != null && var14.b.equals(var11)) {
                    int var15 = this.GF(var14);
                    if (var15 > var9) {
                        var9 = var15;
                        var8 = RenderSubHelperQX.mU;
                        var7 = var14;
                    }
                }

                if (var14.e != null && var14.e.equals(var11)) {
                    int var16 = this.GG(var14);
                    if (var16 > var9) {
                        var9 = var16;
                        var8 = RenderSubHelperQX.mX;
                        var7 = var14;
                    }
                }
            }
        }

        return var8 != null
                ? new RenderSubHelperJ(var8, var7, var9)
                : new RenderSubHelperJ(var6 ? RenderSubHelperQX.mV : RenderSubHelperQX.mW, null, -1);
    }

    private float getPlayerCellOffsetX(ClientPlayerEntity player) {
        return (float) ((player.getX() - (MathHelper.floor(player.getX()) + 0.5)) * this.gridCellSize.get());
    }

    private int getGridPixelSize(int size) {
        int var2 = this.gridCellSize.get();
        int var3 = this.gridGap.get();
        return var2 * size + var3 * (size - 1);
    }

    private RenderSubHelperJ[][] buildProjectedGrid(ClientPlayerEntity player) {
        int[] var2 = new int[] {0};
        int var3 = (int) Math.floor(player.getBoundingBox().minY);
        int var4 = MathHelper.floor(player.getX());
        int var5 = MathHelper.floor(player.getZ());
        RenderSubHelperJ[][] var6 = new RenderSubHelperJ[5][5];

        for (int var7 = 0; var7 < 5; var7++) {
            for (int var8 = 0; var8 < 5; var8++) {
                int var9 = var4 + (var8 - 2);
                int var10 = var5 + (var7 - 2);
                var6[var7][var8] = this.projectColumnState(var9, var10, var3, var3 + 1, var2);
            }
        }

        return var2[0] < 3 ? null : var6;
    }

    private void renderGridOverlay(
            VDrawContext vdraw,
            RenderSubHelperJ[][] cells,
            float rotationRadians,
            float playerOffsetX,
            float playerOffsetY) {
        for (int var6 = 0; var6 < 5; var6++) {
            for (int var7 = 0; var7 < 5; var7++) {
                RenderSubHelperJ var8 = cells[var6][var7];
                if (var8.UG() >= 0) {
                    RenderSubHelperD var9 = this.rotatePoint(
                            this.Gy(var7, playerOffsetX), this.Gz(var6, playerOffsetY), rotationRadians);
                    this.drawCellProgress(vdraw, var9.x(), var9.y(), var8.UG());
                }
            }
        }
    }

    private double Gy(int col, float playerOffsetX) {
        int var3 = this.gridCellSize.get();
        int var4 = this.gridGap.get();
        return this.Gw() + col * (var3 + var4) + var3 / 2.0 - playerOffsetX;
    }

    private void drawCellProgress(VDrawContext vdraw, double centerX, double centerY, int progressPercentage) {
        String var7 = progressPercentage + "%";
        int var8 = mc.textRenderer.getWidth(var7);
        byte var9 = 9;
        float var10 = Math.max(1.0F, this.gridCellSize.get() - 2.0F);
        float var11 = Math.min(1.0F, Math.min(var10 / var8, var10 / var9));
        int var12 = this.progressColor.get().withAlpha(255);
        vdraw.b();

        try {
            vdraw.f().translate((float) centerX, (float) centerY);
            vdraw.f().scale(var11, var11);
            vdraw.A(mc.textRenderer, var7, -var8 / 2, -var9 / 2, var12, true);
        } finally {
            vdraw.c();
        }
    }

    private void rotateGridToView(VDrawContext vdraw, float rotationRadians) {
        if (rotationRadians != 0.0F) {
            vdraw.f().multiply3D(new Quaternionf().rotationZ(rotationRadians));
        }
    }

    private float getGridRotationRadians() {
        float var1 = mc.gameRenderer.getCamera().getYaw();
        return (float) Math.toRadians(180.0F - var1);
    }

    private double Gz(int row, float playerOffsetY) {
        int var3 = this.gridCellSize.get();
        int var4 = this.gridGap.get();
        return this.Gw() + row * (var3 + var4) + var3 / 2.0 - playerOffsetY;
    }

    private int GF(MineSubHelperH tracker) {
        if (tracker.b == null) {
            return -1;
        } else if (this.ghostHandPredict.get()) {
            float var2 = this.predictGhostHandBreakSpeed(tracker.a, tracker.b);
            float var3 = var2 * (Tasks.b() - tracker.c);
            return var3 > 0.7F ? 100 : Math.clamp((long) ((int) (var3 * 100.0F)), 0, 100);
        } else {
            return Math.clamp((long) (tracker.d * 10), 0, 100);
        }
    }

    private float getPlayerCellOffsetY(ClientPlayerEntity player) {
        return (float) ((player.getZ() - (MathHelper.floor(player.getZ()) + 0.5)) * this.gridCellSize.get());
    }

    public void onRender2D(Event<VDrawContext> event) {
        if (!checkNull()) {
            if (this.ae.get() && this.renderGrid2d.get() && !event.<Boolean>getArgs(1)) {
                ClientPlayerEntity var2 = mc.player;
                RenderSubHelperJ[][] var3 = this.buildProjectedGrid(var2);
                if (var3 != null) {
                    WidgetPos var4 = this.gridPos.get();
                    float var5 = (float) var4.getWindowXFloat(mc.getWindow());
                    float var6 = (float) var4.getWindowYFloat(mc.getWindow());
                    float var7 = this.getPlayerCellOffsetX(var2);
                    float var8 = this.getPlayerCellOffsetY(var2);
                    VDrawContext var9 = (VDrawContext) event.b;
                    var9.b();

                    try {
                        var9.f().translate(var5, var6);
                        float var10 = this.getGridRotationRadians();
                        var9.b();

                        try {
                            this.rotateGridToView(var9, var10);
                            var9.f().translate(-var7, -var8);
                            this.renderProjectedGrid(var9, var3, var7, var8);
                        } finally {
                            var9.c();
                        }

                        var9.fill(-2, -2, 2, 2, -65536);
                        this.renderGridOverlay(var9, var3, var10, var7, var8);
                    } finally {
                        var9.c();
                    }
                }
            }
        }
    }

    public void onUpdate(Event<ClientPlayerEntity> eventUpdate) {
        this.lg.clear();
        this.vm.clear();
        this.vn.clear();
        if (!checkNull()) {
            if (this.ae.get()) {
                for (MineSubHelperH var3 :
                        MiningProgressManager.INSTANCE.getBreakingMap().values()) {
                    if (var3.b != null) {
                        BlockPos var4 = var3.b;
                        int var5 = var3.c;
                        int var6 = var3.d;
                        int var9;
                        String var10;
                        if (this.ghostHandPredict.get()) {
                            float var7 = this.predictGhostHandBreakSpeed(var3.a, var4);
                            float var8 = var7 * (Tasks.b() - var5);
                            if (var8 > 0.7F) {
                                var9 = 100;
                                var10 = "&cInstant";
                            } else {
                                var9 = (int) (var8 * 100.0F);
                                var10 = "&e%d%%".formatted(var9);
                            }
                        } else if (var6 > 0) {
                            var9 = var6 * 10;
                            var10 = "&e%d%%".formatted(var9);
                        } else {
                            var9 = 0;
                            var10 = "&aStop";
                        }

                        if (this.renderName.get()) {
                            MutableText var14 =
                                    ChatUtils.textFromLegacyString(var3.a.getNameForScoreboard() + "\n" + var10);
                            this.lg.submit(
                                    new HackUtilHelperB(
                                            var14, var4.toCenterPos().add(0.0, 0.2, 0.0), 0.5F),
                                    this.nameColor.get().withAlpha(255));
                        }

                        if (this.renderBox.get()) {
                            this.vm.submit(new Box(var4), this.frameColor.get().withAlpha(255));
                            float var15 = Math.clamp(var9 / 100.0F, 0.0F, 1.0F);
                            this.vn.submit(
                                    new Box(
                                            var4.toCenterPos().add(RenderTasks.l.multiply(var15)),
                                            var4.toCenterPos().add(RenderTasks.n.multiply(var15))),
                                    this.progressColor.get().withAlpha(64));
                        }
                    }

                    if (var3.e != null) {
                        BlockPos var11 = var3.e;
                        String var12 = "Double";
                        int var13 = var3.f * 10;
                        if (this.renderName.get()) {
                            MutableText var16 = Text.literal(var3.a.getNameForScoreboard() + "\n" + var12);
                            this.lg.submit(
                                    new HackUtilHelperB(
                                            var16, var11.toCenterPos().add(0.0, 0.2, 0.0), 0.5F),
                                    this.nameColor.get().withAlpha(255));
                        }

                        if (this.renderBox.get()) {
                            this.vm.submit(new Box(var11), this.frameColor.get().withAlpha(255));
                            float var17 = Math.clamp(var13 / 100.0F, 0.0F, 1.0F);
                            this.vn.submit(
                                    new Box(
                                            var11.toCenterPos().add(RenderTasks.l.multiply(var17)),
                                            var11.toCenterPos().add(RenderTasks.n.multiply(var17))),
                                    this.progressColor.get().withAlpha(64));
                        }
                    }
                }
            }
        }
    }

    public MineESP() {
        super("MineESP");
        this.J = this.moduleEntry(this.aD.addHotkey(), new MultiKeyBind(), this.aD.addEnable())
                .build();
        this.renderName = this.flagBuilder(this.aD.add("render-name")).build();
        this.renderBox = this.flagBuilder(this.aD.add("render-box")).build();
        this.ghostHandPredict =
                this.flagBuilder(this.aD.add("ghost-hand-predict")).build();
        this.nameColor = this.builder(this.aD.add("name-color"), WrapColor.class)
                .defaultValue(new WrapColor(Formatting.WHITE))
                .build();
        this.frameColor = this.builder(this.aD.add("frame-color"), WrapColor.class)
                .defaultValue(new WrapColor(Formatting.AQUA))
                .build();
        this.frameDoubleColor = this.builder(this.aD.add("frame-double-color"), WrapColor.class)
                .defaultValue(new WrapColor(Formatting.AQUA))
                .build();
        this.progressColor = this.builder(this.aD.add("progress-color"), WrapColor.class)
                .defaultValue(new WrapColor(Formatting.GOLD))
                .build();
        this.renderGrid2d = this.flagBuilder(this.aD.add("render-grid-2d")).build();
        this.gridPos = this.builder(this.aD.add("grid-pos"), WidgetPos.class)
                .defaultValue(new WidgetPos(0, 0.5, 0.7, 0, 0))
                .build();
        this.gridCellSize = this.intBuilder(this.aD.add("grid-cell-size"))
                .defaultValue(12)
                .validator(Configs.intRange(6, 40))
                .build();
        this.gridGap = this.intBuilder(this.aD.add("grid-gap"))
                .defaultValue(1)
                .validator(Configs.intRange(0, 6))
                .build();
        this.gridBreakingColor = this.builder(this.aD.add("grid-breaking-color"), WrapColor.class)
                .defaultValue(new WrapColor(Formatting.RED))
                .build();
        this.gridDoubleColor = this.builder(this.aD.add("grid-double-color"), WrapColor.class)
                .defaultValue(new WrapColor(Formatting.AQUA))
                .build();
        this.gridSolidColor = this.builder(this.aD.add("grid-solid-color"), WrapColor.class)
                .defaultValue(new WrapColor(new Color(96, 96, 96)))
                .build();
        this.gridAirColor = this.builder(this.aD.add("grid-air-color"), WrapColor.class)
                .defaultValue(new WrapColor(new Color(24, 24, 24)))
                .build();
        this.vm = RenderCollectors.createBoxCollector(true, false, false);
        this.vn = RenderCollectors.createBoxCollector(true, true, false);
        this.lg = RenderCollectors.f();
        this.bindFlag(this.ae);
    }

    private void fillLimited(
            VDrawContext vdraw,
            int x1,
            int y1,
            int x2,
            int y2,
            int color,
            int limitX1,
            int limitY1,
            int limitX2,
            int limitY2) {
        x1 = Math.clamp((long) x1, limitX1, limitX2);
        x2 = Math.clamp((long) x2, limitX1, limitX2);
        y1 = Math.clamp((long) y1, limitY1, limitY2);
        y2 = Math.clamp((long) y2, limitY1, limitY2);
        if (x1 != x2 && y1 != y2) {
            vdraw.fill(x1, y1, x2, y2, color);
        }
    }

    private int GH(RenderSubHelperQX state) {
        return switch (state) {
            case mU -> this.gridBreakingColor.get().withAlpha(220);
            case mX -> this.gridDoubleColor.get().withAlpha(220);
            case mV -> this.gridSolidColor.get().withAlpha(220);
            case mW -> this.gridAirColor.get().withAlpha(180);
        };
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.V(), this::onUpdate);
        this.registerListener(RenderListener.q(), this::Gn);
        this.registerListener(RenderListener.r(), this::onRender2D);
    }
}
