package me.matl114.hacks.modules.render;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.hacks.WorldTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.EntryPrimitiveMap;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.config.NBTTypes;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.hacks.utils.config.TracingOption;
import me.matl114.hacks.utils.render.RenderCollectors;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.utils.ColorUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.render.RenderCollector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.chunk.WorldChunk;

public class WorldScanner extends BaseModule {
    boolean pendingRefreshWhenInGame;
    int FF;
    public FlagRef enable;
    public final ModulePath Fw;
    public NBTRef<TracingOption> espOption;
    final RenderCollector<Box> hR;
    final RenderCollector<Box> FD;
    final RenderCollector<Vec3d> FE;
    final int FG;
    public final ModulePath Fv = makePath(Configs.i, "detect-block");
    public Map<ChunkPos, Map<BlockPos, BlockState>> Fz;
    public Set<Block> Fx;
    int FC;
    public IntRef searchRadius;
    public NBTRef<EntryPrimitiveMap<Block, TextColor>> searchColor;
    public NBTRef<EntrySet<Block>> FA;

    @Override
    public void onEnableModule() {
        super.onEnableModule();
        if (!checkNull()) {
            this.pendingRefreshWhenInGame = true;
        }
    }

    public void UI(Event<Void> event) {
        this.Fz.clear();
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.V(), this::onTick);
        this.registerListener(RenderListener.q(), this::B);
        this.registerListener(Listener.aY(), this::UH);
        this.registerListener(Listener.aZ(), this::UI);
        this.registerListener(Listener.ba(), this::UJ);
        this.registerListener(Listener.bc(), this::UK);
        this.registerListener(Listener.bb(), this::onChunkScannResult);
    }

    public void UM(EntrySet<Block> typeFilter) {
        Set var2 = typeFilter.set();
        if (!Objects.equals(var2, this.Fx)) {
            this.Fx = var2;
            if (!checkNull()) {
                this.pendingRefreshWhenInGame = true;
            }
        }
    }

    public void onTick(Event<ClientPlayerEntity> event) {
        if (!checkNull()
                && this.pendingRefreshWhenInGame
                && (mc.currentScreen == null || mc.currentScreen instanceof HandledScreen)) {
            this.pendingRefreshWhenInGame = false;
            WorldTasks.restartWorldScanner();
        }

        this.hR.clear();
        this.FD.clear();
        this.FE.clear();
        if (this.enable.get()) {
            if (this.FC < 50) {
                this.FC++;
                this.validateAndClearSearchResult(false);
            } else {
                this.FC = 0;
                this.validateAndClearSearchResult(true);
            }

            if (!checkNull() && !this.Fz.isEmpty()) {
                int var2 = this.searchRadius.get();
                ChunkPos var3 = mc.player.getChunkPos();
                HashSet<ChunkPos> var4 = new HashSet<>(this.Fz.keySet());
                int var5 = 0;
                TracingOption var6 = this.espOption.get();

                for (ChunkPos var8 : var4) {
                    if (Math.abs(var3.x - var8.x) <= var2 && Math.abs(var3.z - var8.z) <= var2) {
                        Map var9 = this.Fz.get(var8);

                        for (Entry var11 : ((java.util.Set<Entry>) (var9).entrySet())) {
                            BlockState var12 = (BlockState) var11.getValue();
                            TextColor var13 = this.searchColor.get().uJ(var12.getBlock());
                            if (var13 != null) {
                                VoxelShape var14 = ((BlockState) var11.getValue())
                                        .getOutlineShape(mc.world, (BlockPos) var11.getKey());
                                if (!var14.isEmpty()) {
                                    Box var15 = var14.getBoundingBox();
                                    if (var5 < 10000) {
                                        if (var6.box()) {
                                            this.hR.submit(
                                                    var15.offset((BlockPos) var11.getKey()),
                                                    ColorUtils.j(var13.getRgb(), 128));
                                            this.FD.submit(
                                                    var15.offset((BlockPos) var11.getKey()),
                                                    ColorUtils.j(var13.getRgb(), 64));
                                        }

                                        if (var6.line()) {
                                            this.FE.submit(
                                                    var15.offset((BlockPos) var11.getKey())
                                                            .getCenter(),
                                                    ColorUtils.j(var13.getRgb(), 255));
                                        }
                                    }

                                    var5++;
                                }
                            }
                        }
                    }
                }

                if (var5 > 10000 && this.FF < Tasks.b() - 200) {
                    this.FF = Tasks.b();
                    this.logI18N("message.module.world-scanner.too-many-targets", new Object[] {var5, 10000});
                }
            }
        }
    }

    public void B(Event<MatrixStack> event) {
        if (!checkNull()) {
            if (this.enable.get()) {
                MatrixStack var2 = (MatrixStack) event.e();
                RenderUtils.startDrawVirtual(var2);

                try {
                    this.FD.a(var2);
                    this.hR.a(var2);
                    this.FE.a(var2);
                } finally {
                    RenderUtils.stopDrawVirtual(var2);
                }
            }
        }
    }

    public WorldScanner() {
        super("BlockESP");
        this.Fw = this.Fv.add("search");
        this.Fx = new HashSet<>();
        this.pendingRefreshWhenInGame = true;
        this.Fz = new ConcurrentHashMap<>();
        this.enable = this.flagBuilder(this.Fw.add("enable")).build();
        this.FA = this.builder(this.Fw.add("search-type"), EntrySet.<Block>parameter())
                .defaultValue(
                        new EntrySet<Block>(new Regex("^(.*_portal|end_gateway|end_portal_frame)$"), Registries.BLOCK))
                .updateListener(this::UM)
                .build();
        this.searchColor = this.builder(this.Fw.add("search-color"), EntryPrimitiveMap.<Block, TextColor>uA())
                .defaultValue(new EntryPrimitiveMap<Block, TextColor>(
                        Registries.BLOCK,
                        NBTTypes.h,
                        Map.of(
                                Blocks.NETHER_PORTAL,
                                ColorUtils.color(Formatting.RED),
                                Blocks.END_PORTAL,
                                ColorUtils.color(Formatting.YELLOW),
                                Blocks.END_PORTAL_FRAME,
                                ColorUtils.color(Formatting.BLUE),
                                Blocks.END_GATEWAY,
                                ColorUtils.color(Formatting.YELLOW),
                                Blocks.COMMAND_BLOCK,
                                ColorUtils.color(Formatting.WHITE)),
                        ColorUtils.color(Formatting.GREEN)))
                .build();
        this.searchRadius = this.builder(this.Fw.add("search-radius"), IntRef.TYPE)
                .defaultValue(12)
                .build();
        this.espOption = this.builder(this.Fw.add("esp-option"), TracingOption.class)
                .defaultValue(new TracingOption(true, false))
                .build();
        this.FC = 0;
        this.hR = RenderCollectors.b();
        this.FD = RenderCollectors.c();
        this.FE = RenderCollectors.d();
        this.FF = 0;
        this.FG = 10000;
        this.bindFlag(this.enable);
    }

    @Override
    public void onDisableModule() {
        super.onDisableModule();
    }

    public void UH(Event<Boolean> event) {
        if (this.enable.get()) {
            event.context(Boolean.TRUE);
        }
    }

    public void validateAndClearSearchResult(boolean strict) {
        if (!checkNull()) {
            Iterator var2 = this.Fz.entrySet().iterator();

            while (var2.hasNext()) {
                Entry var3 = (Entry) var2.next();
                ChunkPos var4 = (ChunkPos) var3.getKey();
                WorldChunk var5 = mc.world.getChunkManager().getWorldChunk(var4.x, var4.z);
                if (var5 == null) {
                    var2.remove();
                } else {
                    Map var6 = (Map) var3.getValue();
                    if (var6 != null && !var6.isEmpty()) {
                        if (strict) {}
                    } else {
                        var2.remove();
                    }
                }
            }
        }
    }

    public void onChunkScannResult(Event<BlockState> stateUpdate) {
        if (this.enable.get()) {
            BlockState var2 = (BlockState) stateUpdate.b;
            BlockPos var3 = stateUpdate.getArgs(0);
            ChunkPos var4 = stateUpdate.getArgs(1);
            boolean var5 = this.Fx.contains(var2.getBlock());
            if (var5) {
                Map var6 = this.Fz.computeIfAbsent(var4, k -> new ConcurrentHashMap<>());
                var6.put(var3, var2);
            } else {
                Map var7 = this.Fz.get(var4);
                if (var7 != null) {
                    var7.remove(var3);
                }
            }
        }
    }

    public void UK(Event<Map<BlockPos, BlockState>> chunkScannResultEvent) {
        if (this.enable.get()) {
            ChunkPos var2 = chunkScannResultEvent.getArgs(0);
            ConcurrentHashMap var3 = new ConcurrentHashMap(((Map) chunkScannResultEvent.b).size());

            for (Entry var5 : ((java.util.Set<Entry>) (((Map) chunkScannResultEvent.b)).entrySet())) {
                if (this.Fx.contains(((BlockState) var5.getValue()).getBlock())) {
                    var3.put((BlockPos) var5.getKey(), (BlockState) var5.getValue());
                }
            }

            this.Fz.put(var2, var3);
        }
    }

    public void UJ(Event<List<BiPredicate<BlockPos, BlockState>>> event) {
        if (this.enable.get()) {
            ((List<BiPredicate<BlockPos, BlockState>>) event.b).add((s, b) -> this.Fx.contains(b.getBlock()));
        }
    }
}
