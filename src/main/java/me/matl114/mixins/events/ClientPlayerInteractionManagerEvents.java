package me.matl114.mixins.events;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.util.ArrayDeque;
import me.matl114.accessors.access.PlayerInteractBlockC2SPacketAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.KalamaHelperHelperH;
import me.matl114.events.impl.SlotClickAction;
import me.matl114.events.impl.UseItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin({ClientPlayerInteractionManager.class})
public class ClientPlayerInteractionManagerEvents {
    @Shadow
    @Final
    private MinecraftClient field_3712;

    @Unique
    private ArrayDeque<MutableBoolean> lastInteractCaptureBlockPlace = new ArrayDeque<>(4);

    @Inject(
            method = {"clickRecipe"},
            at = {@At("HEAD")})
    public void onClickRecipe(int syncId, RecipeEntry<?> recipe, boolean craftAll, CallbackInfo ci) {
        Listener.am().broadcast(recipe);
    }

    @Inject(
            method = {"interactItem"},
            at = {
                @At(
                        value = "INVOKE",
                        target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;syncSelectedSlot()V",
                        shift = Shift.BEFORE)
            },
            cancellable = true)
    private void onCancelSend(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        Event<UseItem> handEvent = new Event<>(new UseItem(ActionResult.PASS, hand), true, true);
        Listener.bh().catchEvent(handEvent);
        if (handEvent.d()) {
            cir.setReturnValue(handEvent.b.b());
        }
    }

    @Inject(
            method = {"method_41929"},
            at = {@At("RETURN")})
    public void onInteractItem(
            Hand hand,
            PlayerEntity playerEntity,
            MutableObject<ActionResult> mutableObject,
            int sequence,
            CallbackInfoReturnable<Packet> cir) {
        ActionResult acc = (ActionResult) mutableObject.getValue();
        Event<UseItem> eventResult = new Event<>(new UseItem(acc, hand), false, true);
        Listener.bi().catchEvent(eventResult);
        mutableObject.setValue(eventResult.b.b());
    }

    @Inject(
            method = {"interactBlock"},
            at = {@At("HEAD")},
            cancellable = true)
    public void onPreInteractBlock(
            ClientPlayerEntity player,
            Hand hand,
            BlockHitResult hitResult,
            CallbackInfoReturnable<ActionResult> cir,
            @Local(argsOnly = true) LocalRef<BlockHitResult> hand2) {
        Event<KalamaHelperHelperH> blockHitResultEvent =
                new Event<>(new KalamaHelperHelperH(hitResult, ActionResult.SUCCESS, hand), true, true);
        Listener.bj().catchEvent(blockHitResultEvent);
        if (blockHitResultEvent.d()) {
            cir.setReturnValue(blockHitResultEvent.b.c());
        } else {
            BlockHitResult hitResult2 = blockHitResultEvent.b.b();
            if (hitResult2 != hitResult) {
                hand2.set(hitResult2);
            }
        }
    }

    @Inject(
            method = {"interactBlock"},
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V",
                        shift = Shift.AFTER)
            })
    public void onPostInteractBlock(
            ClientPlayerEntity player,
            Hand hand,
            BlockHitResult hitResult,
            CallbackInfoReturnable<ActionResult> cir,
            @Local MutableObject<ActionResult> mutableObject) {
        ActionResult acc = (ActionResult) mutableObject.getValue();
        Event<KalamaHelperHelperH> eventResult =
                new Event<>(new KalamaHelperHelperH(hitResult, acc, hand), false, true);
        Listener.bk().catchEvent(eventResult);
        mutableObject.setValue(eventResult.b.c());
    }

    @ModifyArg(
            method = {"interactBlock"},
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V"),
            index = 1)
    public SequencedPacketCreator onModifyArgument(
            SequencedPacketCreator packetCreator,
            @Local(argsOnly = true) Hand hand,
            @Local(argsOnly = true) BlockHitResult hitResult,
            @Local MutableObject<ActionResult> actionResult) {
        ItemStack stackCopy = this.field_3712.player.getStackInHand(hand).copy();
        BlockState state = this.field_3712.world.getBlockState(hitResult.getBlockPos());
        return seq -> {
            MutableBoolean captureBlockPlace = new MutableBoolean(false);
            this.lastInteractCaptureBlockPlace.addLast(captureBlockPlace);

            Packet var12;
            try {
                Packet<ServerPlayPacketListener> packet = packetCreator.predict(seq);
                if (packet instanceof PlayerInteractBlockC2SPacketAccess access) {
                    access.setUseContext(new PlayerInteractBlockC2SPacketAccess.UseContext(
                            stackCopy,
                            state,
                            (ActionResult) actionResult.getValue(),
                            captureBlockPlace.booleanValue()));
                }

                var12 = packet;
            } finally {
                this.lastInteractCaptureBlockPlace.removeLast();
            }

            return var12;
        };
    }

    @Inject(
            method = {"interactBlockInternal"},
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lnet/minecraft/item/ItemStack;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;")
            })
    private void onInteractBlockInternalCaptureBlockPlace(
            ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        MutableBoolean re = this.lastInteractCaptureBlockPlace.peekLast();
        if (re != null) {
            re.setValue(true);
        }
    }

    @Inject(
            method = {"clickSlot"},
            at = {@At("HEAD")},
            cancellable = true)
    public void onClickSlot(
            int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        Event<SlotClickAction> eventClickSlot =
                new Event<>(new SlotClickAction(actionType, syncId, slotId, button), true, false);
        Listener.ak().b(eventClickSlot);
        if (eventClickSlot.d()) {
            ci.cancel();
        }
    }

    @Inject(
            method = {"clickSlot"},
            at = {@At("RETURN")})
    public void onClickSlotPost(
            int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        Listener.al().broadcast(new SlotClickAction(actionType, syncId, slotId, button));
    }

    @Inject(
            method = {
                "createPlayer(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/stat/StatHandler;Lnet/minecraft/client/recipebook/ClientRecipeBook;ZZ)Lnet/minecraft/client/network/ClientPlayerEntity;"
            },
            at = {@At("RETURN")})
    public void onCreatePlayer(
            ClientWorld world,
            StatHandler statHandler,
            ClientRecipeBook recipeBook,
            boolean lastSneaking,
            boolean lastSprinting,
            CallbackInfoReturnable<ClientPlayerEntity> cir) {
        ClientPlayerEntity player = (ClientPlayerEntity) cir.getReturnValue();
        Listener.aE().broadcast(player);
    }
}
