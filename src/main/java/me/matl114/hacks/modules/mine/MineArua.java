package me.matl114.hacks.modules.mine;

import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.MineTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.interact.InteractExtra;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.Debug;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class MineArua extends BaseModule {
    public final FlagRef ae;
    public KeyBindRef el;
    public final ModulePath ss = makePath(Configs.g, "mine-arua");
    public NBTRef<EntrySet<Block>> V;
    private BlockPos sq;
    public FlagRef autoBreak;
    private int lastRefreshTick;

    public void onMineBlockAction(Event<HitResult> event) {
        if (mc.player != null && this.isActive()) {
            BlockPos var2 = this.refreshMineAruaTarget();
            if (var2 != this.sq) {
                if (var2 != null) {
                    Debug.b(Text.literal("[Mine Arua] Redirect mine target ")
                            .append(ChatUtils.t(Vec3d.of(var2)))
                            .formatted(Formatting.GREEN));
                }

                this.sq = var2;
                this.lastRefreshTick = Tasks.b();
            }

            if (this.sq != null) {
                Direction var3 = Direction.getFacing(this.sq.toCenterPos().subtract(mc.player.getEyePos()))
                        .getOpposite();
                BlockHitResult var4 = new BlockHitResult(Vec3d.of(this.sq), var3, this.sq, false);
                event.context(var4);
            }
        }
    }

    private boolean isMineAruaTarget(World world, BlockPos pos) {
        BlockState var3 = world.getBlockState(pos);
        if (var3 != null && !var3.isAir() && !var3.isLiquid()) {
            Block var4 = var3.getBlock();
            if (var4.getHardness() >= 0.0F && this.V.get().test(var4)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onDisableModule() {
        super.onDisableModule();
        this.sq = null;
    }

    private BlockPos refreshMineAruaTarget() {
        if (mc.player != null && mc.world != null) {
            Vec3d var1 = mc.player.getEyePos();
            if (this.sq != null
                    && this.isMineAruaTarget(mc.world, this.sq)
                    && !MineTasks.distanceOutOfReach(this.sq, var1)) {
                return this.sq;
            }

            if (Tasks.b() >= this.lastRefreshTick + 4) {
                BlockPos var2 = mc.player.getBlockPos();

                for (Vec3i var4 : InteractExtra.INSTANCE.fw()) {
                    BlockPos var5 = var2.add(var4);
                    if (this.isMineAruaTarget(mc.world, var5) && !MineTasks.distanceOutOfReach(var5, var1)) {
                        return var5;
                    }
                }
            }
        }

        return null;
    }

    public MineArua() {
        super("MineArua");
        this.ae = this.flagBuilder(this.ss.addEnable()).build();
        this.el = this.toggleHotkey(this.ss.addHotkey(), new MultiKeyBind(), this.ss.addEnable())
                .build();
        this.V = this.builder(this.ss.add("block-whitelist"), EntrySet.<Block>parameter())
                .defaultValue(new EntrySet<Block>(new Regex("^(.*bed)$"), Registries.BLOCK))
                .build();
        this.autoBreak = this.flagBuilder(this.ss.add("auto-break")).build();
        this.bindFlag(this.ae);
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.bl(), this::onMineBlockAction);
    }
}
