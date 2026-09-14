package me.matl114.mixins.interfaces;

import me.matl114.accessors.interfaces.TileInventory;
import me.matl114.utils.world.ContainerPosition;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin({ShulkerBoxScreenHandler.class})
public class ShulkerBoxScreenHandlerMixin implements TileInventory.Handler {
    @Unique
    private BlockPos pos;

    @Unique
    private Block cacheBlockType;

    @Unique
    private ClientWorld world;

    @Unique
    private ContainerPosition containerPosition;

    @Unique
    @Override
    public BlockPos getPos() {
        return this.pos;
    }

    @Unique
    @Override
    public Block getBlockType() {
        return this.cacheBlockType;
    }

    @Unique
    @Override
    public ClientWorld getWorld() {
        return this.world;
    }

    @Unique
    @Override
    public ContainerPosition getContainerPosition() {
        return this.containerPosition;
    }

    @Override
    public void sync(TileInventory tileInventory) {
        this.pos = tileInventory.getPos();
        this.cacheBlockType = tileInventory.getBlockType();
        this.world = tileInventory.getWorld();
        this.containerPosition = tileInventory.getContainerPosition();
    }
}
