package me.matl114.hacks.modules.inv;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.accessors.access.PlayerInteractBlockC2SPacketAccess;
import me.matl114.accessors.gui.ScreenAccess;
import me.matl114.accessors.interfaces.MetadataHolder;
import me.matl114.accessors.interfaces.TileInventory;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.events.annotations.Modifiable;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.task.ServerStorage;
import me.matl114.hacks.modules.task.TaskSubHelperJ;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.hacks.utils.gui.InventorySelectScreen;
import me.matl114.hacks.utils.world.BlockStorage;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.WorldUtils;
import me.matl114.utils.collections.MutableEntry;
import me.matl114.utils.inventory.KalamaHelperHelperC;
import me.matl114.utils.world.BlockLocation;
import me.matl114.utils.world.ContainerPosition;
import me.matl114.versioned.api.VRender;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity.BillboardMode;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class ChestHistory extends BaseModule {
    private static final List<ItemStack> BL = Collections.nCopies(27, ItemStack.EMPTY);
    private final int BB;
    private static final Text BJ = Text.translatable("container.enderchest");
    private final List<InvSubHelperV> BD;
    public final ModulePath BA = makePath(Configs.l, "inv-cache");
    private static final String BK = "kalama:chest_history/tracked_self_place_shulker";
    private final LinkedHashMap<ContainerPosition, MutableEntry<Block, InvSubHelperV>> BC;
    private static String BN = null;
    public InvSubHelperV BI;
    public final NBTRef<Regex> ignoreContainerWithTitle;
    public final FlagRef enablePersistentStorage;
    private static final int E = VRender.createTextPositionFlag(0, 1);
    private int BO;
    public static ChestHistory INSTANCE;
    public static String BM = "kalama:chesthistory/inventory_content";
    private static final int BP = 40;
    public final KeyBindRef openInvCache;
    public final FlagRef showTitle;
    public InvSubHelperV BH;

    public void Qf(Event<TaskSubHelperJ> metaSave) {
        if (this.enablePersistentStorage.get()) {
            TaskSubHelperJ var2 = (TaskSubHelperJ) metaSave.e();
            WrapperLookup var3 = metaSave.getArgs(1);

            for (Entry var5 : this.BC.entrySet()) {
                InvSubHelperV var6 = (InvSubHelperV) ((MutableEntry) var5.getValue()).b();
                if (var6.b) {
                    var6.b = false;
                    RegistryKey var7 = ((ContainerPosition) var5.getKey()).world();
                    BlockPos var8 = ((ContainerPosition) var5.getKey()).vO().YO();
                    BlockStorage var9 = var2.n(var7, var8, true);
                    var9.put(BM, var6, InvSubHelperV.CODEC, var3);
                    var6.h().ifPresent(var9::setType);
                }
            }
        }
    }

    private void Qd(Event<ClientPlayerEntity> v) {
        String var2 = ServerStorage.Jl();
        if (!Objects.equals(var2, BN)) {
            this.BC.clear();
            this.BD.clear();
            this.BI = null;
            this.BH = null;
        }

        BN = var2;
    }

    public void Qa(ContainerPosition containerPosition) {
        this.BC.remove(containerPosition);
        this.Qb(containerPosition);
    }

    @Modifiable
    public InvSubHelperV PX() {
        return this.BI;
    }

    public boolean openInventoryCacheScreen() {
        if (mc.player != null && mc.world != null) {
            ScreenAccess.of(new InventorySelectScreen(this::PQ)).openFromCurrent();
            return true;
        } else {
            return false;
        }
    }

    public ChestHistory() {
        super("ChestHistory");
        this.BB = 64;
        this.BC = new LinkedHashMap<>();
        this.BD = new ArrayList<>();
        this.openInvCache = this.hotkey(this.BA.add("open-inv-cache"), new MultiKeyBind(341, 74))
                .registerHotkey(HotKeyUtils.c(this::openInventoryCacheScreen))
                .build();
        this.ignoreContainerWithTitle = this.builder(this.BA.add("ignore-container-with-title"), Regex.class)
                .defaultValue(new Regex("^(Slimefun 指南.*|菜单)$"))
                .build();
        this.showTitle =
                this.flagBuilder(this.BA.add("show-title")).defaultValue(false).build();
        this.enablePersistentStorage =
                this.flagBuilder(this.BA.add("enable-persistent-storage")).build();
        this.BO = 0;
        INSTANCE = this;
    }

    public boolean isShulkerBoxPlacedBySelf(BlockPos pos) {
        return mc.world.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity var3
                && var3 instanceof MetadataHolder var4
                && !var4.isMetaEmpty()
                && var4.getMetadata().b(this, "kalama:chest_history/tracked_self_place_shulker") != null;
    }

    @Modifiable
    public InvSubHelperV PU(ContainerPosition containerPosition) {
        MutableEntry var2 = this.BC.get(containerPosition);
        return var2 == null ? null : (InvSubHelperV) var2.b();
    }

    public void onRender(Event<MatrixStack> event) {
        MatrixStack var2 = (MatrixStack) event.e();
        if (this.showTitle.get() && mc.player != null) {
            RenderUtils.startDrawVirtual(var2);

            try {
                BlockLocation var3 = BlockLocation.of(mc.player);
                Vec3d var4 = RenderUtils.getCameraPos();
                HashSet var5 = new HashSet();

                for (Entry var7 : this.BC.entrySet()) {
                    if (((ContainerPosition) var7.getKey()).isInRenderRange(var3, 64.0)) {
                        Vec3d var8 = ((ContainerPosition) var7.getKey()).vF();
                        if (!var5.contains(var8)) {
                            var5.add(var8);
                            Vec3d var9 = var8.add(0.0, 0.25, 0.0).subtract(var4);
                            var2.push();
                            var2.translate(var9.x, var9.y, var9.z);
                            var2.multiply(RenderUtils.getBillboardRotation(BillboardMode.CENTER, 0.0F, 0.0F));
                            var2.scale(0.03125F, 0.03125F, 1.0F);
                            int var10 = (int) InventoryUtils.streamInventory(
                                            ((InvSubHelperV) ((MutableEntry) var7.getValue()).b).d())
                                    .filter(s -> !s.isEmpty())
                                    .count();
                            MutableText var11 = ((InvSubHelperV) ((MutableEntry) var7.getValue()).b())
                                    .g()
                                    .orElse(Text.empty())
                                    .copy()
                                    .append(Text.literal("(x%d)".formatted(var10))
                                            .formatted(Formatting.YELLOW));
                            VRender.getInstance()
                                    .drawTextCameraCoord(
                                            var11.asOrderedText(), var2, Vec3d.ZERO, E, Color.WHITE, VRender.a);
                            var2.pop();
                        }
                    }
                }
            } finally {
                RenderUtils.stopDrawVirtual(var2);
            }
        }
    }

    @Modifiable
    public Inventory PY() {
        return (Inventory) (this.BI != null ? this.BI.d() : new KalamaHelperHelperC(BL));
    }

    public ContainerPosition findDoubleChest(ContainerPosition containerPosition) {
        if (containerPosition.isDouble()) {
            return containerPosition;
        } else {
            BlockPos var2 = containerPosition.vO().YO();

            for (Direction var6 : MathUtils.HORIZONTALS) {
                BlockPos var7 = var2.offset(var6);
                ContainerPosition var8 = ContainerPosition.ofDouble(mc.world, var2, var7);
                if (this.BC.containsKey(var8)) {
                    return var8;
                }
            }

            return null;
        }
    }

    public void onAddEntry(ContainerPosition containerPosition, BlockState state, InvSubHelperV newEntry) {
        if (containerPosition.isDouble()) {
            this.Qa(ContainerPosition.ofPosition(containerPosition.vO()));
            this.Qa(ContainerPosition.ofPosition(containerPosition.vP()));
        }

        MutableEntry<Block, InvSubHelperV> var4 = this.BC.get(containerPosition);
        if (var4 != null) {
            var4.a = state.getBlock();
            var4.b = newEntry;
        } else {
            this.BC.put(containerPosition, new MutableEntry<>(state.getBlock(), newEntry));
        }
    }

    public void Qe(Event<TaskSubHelperJ> metaLoad) {
        List<BlockStorage> var2 = ((TaskSubHelperJ) metaLoad.b).g();
        CompletableFuture.runAsync(() -> {
            for (BlockStorage var3 : var2) {
                if (var3.contains(BM)) {
                    InvSubHelperV var4 = var3.b(BM, InvSubHelperV.CODEC);
                    if (var4 != null) {
                        if (var4.i().isPresent()) {
                            ContainerPosition var5 = var4.i().get();
                            Block var6 = var4.h().orElse(Blocks.AIR);
                            this.BC.put(var5, new MutableEntry<>(var6, var4));
                        } else {
                            this.BD.add(var4);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.ah(), this::onOpenHandledScreen);
        this.registerListener(Listener.ap().getChannel(PlayerInteractBlockC2SPacket.class), this::onPlaceShulkerBox);
        this.registerListener(Listener.M(), this::Qd);
        this.registerListener(Listener.V(), this::onTick);
        this.registerListener(RenderListener.q(), this::onRender);
        this.registerListener(ServerStorage.JP(), this::Qe);
        this.registerListener(ServerStorage.JQ(), this::Qf);
    }

    public void Qb(ContainerPosition containerPosition) {
        this.BC.remove(containerPosition);
        BlockStorage var2 = ServerStorage.IZ()
                .n(containerPosition.world(), containerPosition.vO().YO(), false);
        if (var2 != null) {
            var2.f(BM, null);
            ServerStorage.Jk(var2, true);
        }
    }

    public void onTick(Event<ClientPlayerEntity> event) {
        if (++this.BO >= 40) {
            if (this.BH != null && this.BH.f != null) {
                if (ClientPlayerAccess.of(mc.player).getServerScreenHandler().syncId
                        == this.BH.f.getScreenHandler().syncId) {
                    this.BH.b = true;
                } else {
                    this.BH.b(this.BH.f);
                    this.BH = null;
                }
            }

            this.BO = 0;
            BlockLocation var2 = BlockLocation.of((Entity) event.e());
            Iterator var3 = this.BC.entrySet().iterator();

            while (var3.hasNext()) {
                Entry var4 = (Entry) var3.next();
                if (((ContainerPosition) var4.getKey()).isInRenderRange(var2, 64.0)) {
                    ChunkPos var5 = ((ContainerPosition) var4.getKey()).vR();
                    if (WorldUtils.n(var5.x, var5.z)) {
                        Block var6 = (Block) ((MutableEntry) var4.getValue()).a();
                        if (var6 != null && var6 != Blocks.AIR) {
                            if (!((ContainerPosition) var4.getKey()).isDouble()) {
                                Block var7 = mc.world
                                        .getBlockState(((ContainerPosition) var4.getKey())
                                                .vO()
                                                .YO())
                                        .getBlock();
                                if (var7 != var6) {
                                    var3.remove();
                                    this.Qb((ContainerPosition) var4.getKey());
                                }
                            } else if (var4.getKey() instanceof ContainerPosition var8) {
                                BlockPos var15 = var8.vO().YO();
                                BlockState var9 = mc.world.getBlockState(var15);
                                if (!(var9.getBlock() instanceof ChestBlock)) {
                                    var3.remove();
                                    this.Qb((ContainerPosition) var4.getKey());
                                } else if (var9.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE) {
                                    var3.remove();
                                    this.Qb((ContainerPosition) var4.getKey());
                                } else {
                                    Direction var10 = ChestBlock.getFacing(var9);
                                    BlockPos var11 = var15.offset(var10);
                                    BlockPos var12 = var8.vP().YO();
                                    if (!var12.equals(var11)) {
                                        var3.remove();
                                        this.Qb((ContainerPosition) var4.getKey());
                                    } else {
                                        Block var13 =
                                                mc.world.getBlockState(var12).getBlock();
                                        if (!(var13 instanceof ChestBlock)) {
                                            var3.remove();
                                            this.Qb((ContainerPosition) var4.getKey());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void onOpenHandledScreen(Event<HandledScreen<?>> screenEvent) {
        HandledScreen var2 = (HandledScreen) screenEvent.e();
        if (!(var2 instanceof CreativeInventoryScreen)) {
            String var3 = ChatUtils.l(var2.getTitle());
            String var4 = ChatUtils.l(BJ);
            boolean var5 = Objects.equals(var3, var4);
            if (!var5) {
                var3 = var3.replaceAll("§.", "");
                if (this.ignoreContainerWithTitle.get().test(var3)) {
                    return;
                }
            }

            InvSubHelperV var10;
            if (var2 instanceof TileInventory var6 && !var6.isVirtual()) {
                ContainerPosition var7 = var6.getContainerPosition();
                BlockPos var8 = var6.getPos();
                BlockState var9 = mc.world.getBlockState(var8);
                var10 = new InvSubHelperV(var2, var7);
                this.onAddEntry(var7, var9, var10);
            } else {
                this.BD.add(var10 = new InvSubHelperV(var2));
            }

            this.BH = var10;
            if (var5) {
                this.BI = var10;
            }
        }
    }

    public void onPlaceShulkerBox(Event<PlayerInteractBlockC2SPacket> eventInteract) {
        PlayerInteractBlockC2SPacketAccess var2 =
                PlayerInteractBlockC2SPacketAccess.of((PlayerInteractBlockC2SPacket) eventInteract.b);
        PlayerInteractBlockC2SPacketAccess.UseContext var3 = var2.getUseContext();
        if (var3 != null
                && var3.isAccepted()
                && var3.blockPlace()
                && var3.stack().getItem() instanceof BlockItem var5
                && var5.getBlock() instanceof ShulkerBoxBlock) {
            BlockPos var10 = var3.getPlaceBlockPos(
                    ((PlayerInteractBlockC2SPacket) eventInteract.b).getHand(),
                    ((PlayerInteractBlockC2SPacket) eventInteract.b).getBlockHitResult());
            BlockState var6 = mc.world.getBlockState(var10);
            if (var6.getBlock() == var5.getBlock()
                    && mc.world.getBlockEntity(var10) instanceof ShulkerBoxBlockEntity var8) {
                if (var8 instanceof MetadataHolder var11) {
                    var11.getMetadata().a(this, "kalama:chest_history/tracked_self_place_shulker", true);
                }

                ContainerPosition var12 = ContainerPosition.ofSingle(mc.world, var10);
                InvSubHelperV var9 = new InvSubHelperV(
                        InventoryUtils.getInventoryEntries(var8),
                        var8.size(),
                        Optional.ofNullable(var8.getName()),
                        Optional.of(var5.getBlock()),
                        Optional.of(var12));
                this.onAddEntry(var12, var6, var9);
            }
        }
    }

    public List<InvSubHelperV> PQ() {
        return Stream.concat(this.BC.values().stream().map(MutableEntry::b), this.BD.stream())
                .toList();
    }

    public static boolean isEnderChest(Screen handler) {
        return handler instanceof GenericContainerScreen var1 && Objects.equals(var1.getTitle(), BJ);
    }
}
