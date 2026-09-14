package me.matl114.mixins.interfaces;

import me.matl114.accessors.interfaces.EntityInventory;
import me.matl114.accessors.interfaces.TileInventory;
import me.matl114.hacks.InteractionTasks;
import me.matl114.hacks.InvTasks;
import me.matl114.utils.world.ContainerPosition;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.entity.vehicle.VehicleInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({GenericContainerScreen.class})
public abstract class ChestScreenMixin extends HandledScreen<GenericContainerScreenHandler>
        implements TileInventory, EntityInventory<VehicleInventory> {
    @Unique
    private BlockPos pos;

    @Unique
    private Block cacheBlockType;

    @Unique
    private ClientWorld world;

    @Unique
    private ContainerPosition containerPosition;

    @Unique
    VehicleInventory vehicleEntity;

    @Unique
    @Override
    public BlockPos getPos() {
        return this.pos;
    }

    public ChestScreenMixin(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super((GenericContainerScreenHandler) handler, inventory, title);
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
    public HandledScreen<?> castHandled() {
        return this;
    }

    @Override
    public ScreenHandler castHandler() {
        return this.handler;
    }

    @Unique
    @Override
    public ContainerPosition getContainerPosition() {
        return this.containerPosition;
    }

    @Nullable
    public VehicleInventory getOwner() {
        return this.vehicleEntity;
    }

    @Inject(
            method = {"<init>"},
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;<init>(Lnet/minecraft/screen/ScreenHandler;Lnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/text/Text;)V",
                        shift = Shift.AFTER)
            })
    private void tryInitBlockPos(
            GenericContainerScreenHandler handler, PlayerInventory inventory, Text title, CallbackInfo ci) {
        this.world = MinecraftClient.getInstance().world;
        this.pos = InvTasks.predictScreenFrom(b -> true);
        if (this.pos != null && this.world != null) {
            BlockState state = this.world.getBlockState(this.pos);
            this.cacheBlockType = state.getBlock();
            if (this.cacheBlockType instanceof ChestBlock && state.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE) {
                this.containerPosition = ContainerPosition.resolveDoubleChest(this.world, this.pos, state);
            } else {
                this.containerPosition = ContainerPosition.ofSingle(this.world, this.pos);
            }
        }

        if (this.handler instanceof TileInventory.Handler handler1) {
            handler1.sync(this);
        }

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        this.vehicleEntity = (VehicleInventory) InteractionTasks.z(ex -> ex instanceof StorageMinecartEntity
                || player != null && player.shouldCancelInteraction() && ex instanceof ChestBoatEntity);
        if (this.vehicleEntity == null
                && player != null
                && player.hasVehicle()
                && player.getVehicle() instanceof ChestBoatEntity ccb) {
            this.vehicleEntity = ccb;
        }

        if (this.handler instanceof EntityInventory.Handler handler1) {
            handler1.sync(this);
        }
    }

    public void drawBackground(Object arg0, Object arg1, Object arg2, Object arg3) {}
}
