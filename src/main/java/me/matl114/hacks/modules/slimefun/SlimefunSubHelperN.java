package me.matl114.hacks.modules.slimefun;

import it.unimi.dsi.fastutil.objects.Object2ReferenceArrayMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiPredicate;
import me.matl114.hacks.utils.multiblock.BlockMatcher;
import me.matl114.utils.collections.Point;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class SlimefunSubHelperN {
    boolean symm;
    public BlockMatcher[] blockTypes;
    public Map<BiPredicate<World, BlockPos>, Vec3i> d = new Object2ReferenceArrayMap();
    public Point b;

    public Collection<SlimefunSubHelperF> lookup(World world, BlockPos dispensorPos) {
        HashSet var3 = new HashSet();
        if (dispensorPos != null) {
            for (Entry var5 : this.d.entrySet()) {
                if (((BiPredicate) var5.getKey()).test(world, dispensorPos)) {
                    BlockPos var6 = dispensorPos.add(
                            -this.b.a * ((Vec3i) var5.getValue()).getX(),
                            -this.b.b,
                            -this.b.a * ((Vec3i) var5.getValue()).getZ());
                    var3.add(new SlimefunSubHelperF(var6, (Vec3i) var5.getValue()));
                }
            }
        }

        return var3;
    }

    private void generatePredicate(Vec3i axis) {
        ArrayList<BiPredicate<World, BlockPos>> var2 = new ArrayList<>();

        for (int var3 = 0; var3 < 9; var3++) {
            BlockMatcher var4 = this.blockTypes[var3];
            if (var3 != this.b.a + this.b.b * 3 && var4 != BlockMatcher.Wf) {
                int var5 = -this.b.a + var3 % 3;
                int var6 = -this.b.b + var3 / 3;
                Vec3i var7 = new Vec3i(axis.getX() * var5, var6, axis.getZ() * var5);
                var2.add((world, pos) ->
                        var4.match(world.getBlockState(pos.add(var7)).getBlock()));
            }
        }

        this.d.put(
                (clientWorld, blockPos) -> {
                    for (BiPredicate<World, BlockPos> var4x : var2) {
                        if (!var4x.test(clientWorld, blockPos)) {
                            return false;
                        }
                    }

                    return true;
                },
                axis);
    }

    public SlimefunSubHelperN(BlockMatcher[] blockTypes, boolean isSymm) {
        this.blockTypes = blockTypes;
        this.symm = isSymm;
        this.initData();
    }

    private void initData() {
        for (int var1 = 0; var1 < 9; var1++) {
            if (this.blockTypes[var1] != BlockMatcher.Wf && this.blockTypes[var1].match(Blocks.DISPENSER)) {
                this.b = new Point(var1 % 3, var1 / 3);
                break;
            }
        }

        if (this.b != null) {
            for (Direction var4 : this.symm ? RecipeDatabase.io : RecipeDatabase.in) {
                this.generatePredicate(var4.getVector());
            }
        }
    }
}
