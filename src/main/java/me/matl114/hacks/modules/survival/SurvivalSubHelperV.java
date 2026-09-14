package me.matl114.hacks.modules.survival;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import me.matl114.utils.CommonUtils;
import net.minecraft.util.math.BlockPos;

public class SurvivalSubHelperV {
    private final String e;
    private static final Codec<List<BlockPos>> a =
            Codec.LONG.xmap(BlockPos::fromLong, BlockPos::asLong).listOf();
    private final String d;
    public static final Codec<SurvivalSubHelperV> b = RecordCodecBuilder.create(instance -> instance.group(
                    a.fieldOf("pos").forGetter(SurvivalSubHelperV::a),
                    Codec.STRING.fieldOf("world").forGetter(SurvivalSubHelperV::b),
                    Codec.STRING.fieldOf("server").forGetter(SurvivalSubHelperV::c))
            .apply(instance, SurvivalSubHelperV::new));
    public final List<BlockPos> c = new ArrayList<>();

    public String b() {
        return this.d;
    }

    public String c() {
        return this.e;
    }

    public List<BlockPos> a() {
        return this.c;
    }

    public SurvivalSubHelperV(List<BlockPos> bp) {
        this(bp, PathManager.currentWorldKey(), CommonUtils.getServerName());
    }

    public SurvivalSubHelperV(List<BlockPos> bp, String world, String server) {
        for (BlockPos var5 : bp) {
            if (var5 != null) {
                this.c.add(var5.toImmutable());
            }
        }

        this.d = world == null ? "" : world;
        this.e = server == null ? "" : server;
    }
}
