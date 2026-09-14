package me.matl114.utils;

import java.io.File;
import java.util.List;
import me.matl114.events.annotations.Modifiable;
import me.matl114.utils.world.ChunkIterator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

@Modifiable
public class CommonUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static Iterable<Chunk> chunks(boolean onlyWithLoadedNeighbours) {
        return () -> new ChunkIterator(onlyWithLoadedNeighbours);
    }

    public static List<String> filterString(List<String> str, String str2) {
        return str.stream().filter(s -> s.contains(str2)).toList();
    }

    public static String getServerName() {
        if (mc.isInSingleplayer()) {
            if (mc.world == null) {
                return "";
            } else {
                File var0 = mc.getServer()
                        .session
                        .getWorldDirectory(mc.world.getRegistryKey())
                        .toFile();
                if (var0.toPath().relativize(mc.runDirectory.toPath()).getNameCount() != 2) {
                    var0 = var0.getParentFile();
                }

                return var0.getName();
            }
        } else if (mc.getCurrentServerEntry() != null) {
            return mc.getCurrentServerEntry().isRealm() ? "realms" : mc.getCurrentServerEntry().address;
        } else {
            return "";
        }
    }

    public static Identifier b(String id) {
        return new Identifier("kalama", id);
    }

    public static ChunkPos toChunk(BlockPos blockPos) {
        return new ChunkPos(blockPos.getX() >> 4, blockPos.getZ() >> 4);
    }

    public static String getWorldName() {
        if (mc.isInSingleplayer()) {
            if (mc.world == null) {
                return "";
            } else {
                File var0 = mc.getServer()
                        .session
                        .getWorldDirectory(mc.world.getRegistryKey())
                        .toFile();
                if (var0.toPath().relativize(mc.runDirectory.toPath()).getNameCount() != 2) {
                    var0 = var0.getParentFile();
                }

                return var0.getName() + "|" + mc.world.getRegistryKey().getValue();
            }
        } else if (mc.getCurrentServerEntry() != null) {
            return (mc.getCurrentServerEntry().isRealm() ? "realms" : mc.getCurrentServerEntry().address)
                    + (mc.world == null ? "" : "|" + mc.world.getRegistryKey().getValue());
        } else {
            return mc.world == null ? "" : mc.world.getRegistryKey().getValue().toString();
        }
    }

    public static int parseIntOrDefault(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Throwable var3) {
            return defaultValue;
        }
    }

    public static RegistryKey<DimensionOptions> getCurrentDimensionOption() {
        if (mc.world == null) {
            return DimensionOptions.OVERWORLD;
        } else {
            String var0 = mc.world.getRegistryKey().getValue().getPath();
            switch (var0) {
                case "the_nether":
                    return DimensionOptions.NETHER;
                case "the_end":
                    return DimensionOptions.END;
                case "overworld":
                    return DimensionOptions.OVERWORLD;
                default:
                    DimensionType var2 = mc.world.getDimension();
                    if (var2.ultrawarm() || var2.hasCeiling()) {
                        return DimensionOptions.NETHER;
                    } else if (var2.hasSkyLight()) {
                        return DimensionOptions.OVERWORLD;
                    } else {
                        return var2.bedWorks() ? DimensionOptions.END : DimensionOptions.OVERWORLD;
                    }
            }
        }
    }
}
