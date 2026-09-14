package me.matl114.mixins.events;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import java.util.Objects;
import me.matl114.accessors.access.PlayerMoveC2SPacketAccess;
import me.matl114.accessors.events.ClientPlayerEntityAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.hooks.ViaFabricPlusHooks;
import me.matl114.managers.Tasks;
import me.matl114.utils.entity.PlayerInputUtils;
import me.matl114.utils.entity.PlayerInputUtils$Input;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin({ClientPlayerEntity.class})
public abstract class ClientPlayerEntityEvents extends AbstractClientPlayerEntity implements ClientPlayerEntityAccess {
   @Shadow
   private double field_3926;
   @Shadow
   private double field_3924;
   @Shadow
   private double field_3940;
   @Shadow
   private float field_3925;
   @Shadow
   private float field_3941;
   @Shadow
   public Input field_3913;
   @Shadow
   @Final
   public ClientPlayNetworkHandler field_3944;
   @Shadow
   private boolean field_3919;
   @Shadow
   private boolean field_3920;
   @Shadow
   private int field_3923;
   @Shadow
   private boolean field_3936;
   @Unique
   public LegalMovementManager movementManager;
   @Unique
   int lastCancelTick = 0;

   @Shadow
public abstract boolean method_20303() ;

   @Shadow
   public void method_33689() { }

   public ClientPlayerEntityEvents(ClientWorld world, GameProfile profile) {
      super(world, profile);
   }

   @Unique
   @Override
   public LegalMovementManager getLegalMovementManager() {
      return this.movementManager;
   }

   @Unique
   @Override
   public void setLastSprintFlag(boolean lastSprint) {
      this.field_3919 = lastSprint;
   }

   @Override
   public void setLastSneakFlag(boolean lastSprint) {
      this.field_3936 = lastSprint;
   }

   @Unique
   @Override
   public void setLastOnGroundFlag(boolean lastOnGround) {
      this.field_3920 = lastOnGround;
   }

   @Override
   public void setLastPos(Vec3d vec3d) {
      this.field_3926 = vec3d.x;
      this.field_3924 = vec3d.z;
      this.field_3940 = vec3d.y;
   }

   @Override
   public void setLastRot(float pitch, float yaw) {
      this.field_3925 = pitch;
      this.field_3941 = yaw;
   }

   @Override
   public void resyncInput() {
      this.resyncSneak();
      this.resyncSprint();
   }

   @Unique
   @Override
   public void resyncMovementPacket() {
      this.field_3923 = 100;
   }

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void onClientPlayerInitConfiguration(
      MinecraftClient client,
      ClientWorld world,
      ClientPlayNetworkHandler networkHandler,
      StatHandler stats,
      ClientRecipeBook recipeBook,
      boolean lastSneaking,
      boolean lastSprinting,
      CallbackInfo ci
   ) {
      this.movementManager = new LegalMovementManager();
   }

   @Unique
   private static Vec2f compatMovementVectorWithViaFabric(Vec2f vec2f) {
      return ViaFabricPlusHooks.getInstance().getCurrentVersion().c(21, 4) ? vec2f : vec2f.normalize();
   }

   @Inject(
      method = {"tickMovement"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/input/Input;tick(ZF)V",
         shift = Shift.AFTER
      )}
   )
   public void onPostInputTick(CallbackInfo ci, @Local(ordinal = 0) float f) {
      if (this.checkClientPlayer()) {
         PlayerInputUtils$Input currentInput = PlayerInputUtils.b(this.field_3913);
         if (!Listener.aD().d()) {
            Listener.aD().catchEvent(new Event<>(this.field_3913, false, false));
         }

         this.getLegalMovementManager().postInputTick((ClientPlayerEntity)(Object)this);
         PlayerInputUtils$Input newInput = PlayerInputUtils.b(this.field_3913);
         if (!Objects.equals(currentInput, newInput)) {
            Vec2f movementVector = compatMovementVectorWithViaFabric(new Vec2f(newInput.rp(), newInput.ro()));
            this.field_3913.movementForward = movementVector.y;
            this.field_3913.movementSideways = movementVector.x;
            if (this.method_20303()) {
               this.field_3913.movementForward *= f;
               this.field_3913.movementSideways *= f;
            }
         }
      }
   }

   @Inject(
      method = {"tick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V",
         shift = Shift.BEFORE
      )}
   )
   public void prePlayerTick(CallbackInfo ci) {
      if (this.checkClientPlayer()) {
         this.movementManager.l((ClientPlayerEntity)(Object)this);
      }
   }

   @Inject(
      method = {"tick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V",
         shift = Shift.AFTER
      )},
      order = 100
   )
   public void onAfterTick(CallbackInfo ci) {
      if (this.checkClientPlayer()) {
         Event<ClientPlayerEntity> event = new Event<>((ClientPlayerEntity)(Object)this, true);
         Listener.as().catchEvent(event);
         if (this.hasVehicle()) {
            if (!this.movementManager.p((ClientPlayerEntity)(Object)this) || event.d()) {
               this.lastCancelTick = Tasks.b();
            }
         } else if (!this.movementManager.q((ClientPlayerEntity)(Object)this) || event.d()) {
            this.lastCancelTick = Tasks.b();
         }
      }
   }

   @ModifyExpressionValue(
      method = {"tick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasVehicle()Z"
      )}
   )
   private boolean onTick(boolean original) {
      return Tasks.b() == this.lastCancelTick ? false : original;
   }

   @ModifyExpressionValue(
      method = {"sendMovementPackets"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCamera()Z"
      )}
   )
   private boolean onCancelSendMovementBehaviour(boolean original) {
      if (Tasks.b() == this.lastCancelTick) {
         this.lastCancelTick = 0;
         return false;
      } else {
         return original;
      }
   }

   @Unique
   @Override
   public void onPlayerInputPackets() {
   }

   @Inject(
      method = {"tick"},
      at = {@At(
         value = "INVOKE",
         target = "Ljava/util/List;iterator()Ljava/util/Iterator;"
      )}
   )
   public void postwrapperPlayerMovementSentTick(CallbackInfo ci) {
      if (this.checkClientPlayer()) {
         this.onPostPlayerMovementTick((ClientPlayerEntity)(Object)this);
      }
   }

   @Unique
   private void onPostPlayerMovementTick(ClientPlayerEntity player) {
      Listener.at().broadcast(player);
      this.movementManager.r(player);
   }

   @Unique
   private boolean checkElytra() {
      ItemStack itemStack = this.getEquippedStack(EquipmentSlot.CHEST);
      if (itemStack.isOf(Items.ELYTRA) && ElytraItem.isUsable(itemStack)) {
         this.startFallFlying();
         return true;
      } else {
         return false;
      }
   }

   public boolean checkFallFlying() {
      if (!this.checkClientPlayer()) {
         return super.checkFallFlying();
      } else {
         boolean fallflying = this.isFallFlying();
         boolean shouldSwitch = false;
         if (!fallflying) {
            shouldSwitch = !this.isOnGround()
               && !this.isFallFlying()
               && !this.isTouchingWater()
               && !this.hasStatusEffect(StatusEffects.LEVITATION)
               && this.checkElytra();
         }

         Event<Boolean> switchGliding = new Event<>(shouldSwitch, true, true, fallflying);
         Listener.aG().catchEvent(switchGliding);
         boolean switchFlag;
         if (switchGliding.d()) {
            switchFlag = false;
         } else {
            switchFlag = switchGliding.e();
         }

         if (!fallflying) {
            if (switchFlag) {
               this.startFallFlying();
               return true;
            } else {
               return false;
            }
         } else if (switchFlag) {
            this.stopFallFlying();
            return true;
         } else {
            return false;
         }
      }
   }

   @ModifyExpressionValue(
      method = {"tickMovement"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"
      )}
   )
   private boolean rewriteElytra1(boolean original) {
      return true;
   }

   @ModifyExpressionValue(
      method = {"tickMovement"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/item/ElytraItem;isUsable(Lnet/minecraft/item/ItemStack;)Z"
      )}
   )
   private boolean rewriteElytra2(boolean original) {
      return true;
   }

   @ModifyArg(
      method = {"sendMovementPackets"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"
      )
   )
   private Packet onSendMovementPackets(Packet par1) {
      if (par1 instanceof PlayerMoveC2SPacketAccess acc) {
         acc.setCause(PlayerMoveC2SPacketAccess.Cause.PLAYER_MOVEMENT);
      }

      return par1;
   }

   @Inject(
      method = {"dropSelectedItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onDropSelected(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
      if (this.checkClientPlayer() && !Listener.bf().fireEvent(entireStack)) {
         cir.setReturnValue(false);
      }
   }

   @Inject(
      method = {"closeHandledScreen"},
      at = {@At("HEAD")},
      cancellable = true
   )
    private void onCloseHandledScreen(CallbackInfo ci) {
       if (this.checkClientPlayer() && !Listener.bg().fireEvent(null)) {
          ci.cancel();
       }
    }

}

