package me.matl114.hacks;

import me.matl114.events.annotations.Modifiable;
import me.matl114.hacks.api.ModuleGroup;
import me.matl114.hacks.api.ModuleManager;
import me.matl114.hacks.modules.HackModules;
import me.matl114.hacks.modules.interact.InteractExtra;
import me.matl114.hacks.modules.mine.MineArua;
import me.matl114.hacks.modules.mine.MineBot;
import me.matl114.hacks.modules.mine.MineExtra;
import me.matl114.hacks.modules.mine.MiningProgressManager;
import me.matl114.hacks.modules.mine.PacketMine;
import me.matl114.hacks.modules.mine.QueueMine;
import me.matl114.utils.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

@Modifiable
public class MineTasks {
    private static PacketMine g;
    private static QueueMine f;

    @Modifiable
    public static final ModuleGroup b = new ModuleGroup("Mine");

    private static MineExtra c;
    private static final MinecraftClient a = MinecraftClient.getInstance();
    private static MineBot e;
    private static MineArua h;
    private static MiningProgressManager d;

    public static MiningProgressManager f() {
        return d;
    }

    public static MineBot g() {
        return e;
    }

    public static MineArua j() {
        return h;
    }

    private static void initModules(ModuleManager m) {
        c = new MineExtra().register(m);
        d = new MiningProgressManager().register(m);
        e = new MineBot().register(m);
        f = new QueueMine().register(m);
        g = new PacketMine().register(m);
        h = new MineArua().register(m);
    }

    public static PacketMine i() {
        return g;
    }

    public static boolean distanceOutOfReach(BlockPos pos1, Vec3d playerPos) {
        return pos1 != null && playerPos != null
                ? new Box(pos1).squaredMagnitude(playerPos)
                        > MathUtils.a(InteractExtra.INSTANCE.getBlockReachDistance())
                : true;
    }

    public static QueueMine h() {
        return f;
    }

    static {
        b.registerFactories(MineTasks::initModules);
        HackModules.registerModuleGroup(b);
    }

    public static ModuleGroup d() {
        return b;
    }

    public static void init() {}

    public static MineExtra e() {
        return c;
    }
}
