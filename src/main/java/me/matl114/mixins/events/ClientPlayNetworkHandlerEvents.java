package me.matl114.mixins.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.util.Objects;
import me.matl114.accessors.access.PlayerMoveC2SPacketAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.MovTasks$MovInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Action;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin({ClientPlayNetworkHandler.class})
public abstract class ClientPlayNetworkHandlerEvents {
   @Unique
   boolean escapeSendEvent = false;
   @Shadow
   private ClientWorld field_3699;
   @Unique
   private boolean playerRecreateOnJoin = false;
   @Unique
   private boolean worldChangeOnRespawn = false;

   @Inject(
      method = {"onOpenScreen"},
      at = {@At("RETURN")}
   )
   private void onPostInventoryOpen(OpenScreenS2CPacket packet, CallbackInfo ci) {
      if (MinecraftClient.getInstance().currentScreen instanceof HandledScreen<?> screen) {
         Listener.ah().broadcast(screen);
      }
   }

   @Inject(
      method = {"sendChatCommand"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onChat0(String command, CallbackInfo ci, @Local(argsOnly = true) LocalRef<String> commandRef) {
      if (this.escapeSendEvent) {
         this.escapeSendEvent = false;
      } else {
         String chatContent = "/" + command;
         Event<String> value = new Event<>(chatContent, true, true);
         Listener.Z().catchEvent(value);
         if (value.d()) {
            ci.cancel();
         } else {
            String valueChange = value.e();
            if (valueChange != null && !valueChange.isEmpty()) {
               if (!Objects.equals(chatContent, valueChange)) {
                  if (valueChange.startsWith("/")) {
                     commandRef.set(valueChange.substring(1));
                  } else {
                     ci.cancel();
                     this.escapeSendEvent = true;
                     this.method_45729(valueChange);
                  }
               }
            } else {
               ci.cancel();
            }
         }
      }
   }

   @Inject(
      method = {"sendChatMessage"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onChat2(String content, CallbackInfo ci, @Local(argsOnly = true) LocalRef<String> contentRef) {
      if (this.escapeSendEvent) {
         this.escapeSendEvent = false;
      } else {
         Event<String> value = new Event<>(content, true, true);
         Listener.Z().catchEvent(value);
         if (value.d()) {
            ci.cancel();
         } else if (value.e() == null) {
            ci.cancel();
         } else {
            String valueChange = value.e();
            if (!Objects.equals(content, valueChange)) {
               if (!valueChange.startsWith("/")) {
                  contentRef.set(valueChange);
               } else {
                  ci.cancel();
                  this.escapeSendEvent = true;
                  this.method_45730(valueChange.substring(1));
               }
            }
         }
      }
   }

   @Inject(
      method = {"onGameJoin"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;createPlayer(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/stat/StatHandler;Lnet/minecraft/client/recipebook/ClientRecipeBook;)Lnet/minecraft/client/network/ClientPlayerEntity;",
         shift = Shift.AFTER
      )}
   )
   private void onGameJoinCreatePlayer0(GameJoinS2CPacket packet, CallbackInfo ci) {
      this.playerRecreateOnJoin = true;
   }

   @Inject(
      method = {"onGameJoin"},
      at = {@At("RETURN")}
   )
   private void onGameJoinEntryPoint(GameJoinS2CPacket packet, CallbackInfo ci) {
      Listener.M().broadcast(MinecraftClient.getInstance().player);
      Listener.N().broadcast(this.field_3699);
      if (this.playerRecreateOnJoin) {
         this.playerRecreateOnJoin = false;
         Listener.av().broadcast(MinecraftClient.getInstance().player);
      }
   }

   @Inject(
      method = {"onPlayerRespawn"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/world/ClientWorld;<init>(Lnet/minecraft/client/network/ClientPlayNetworkHandler;Lnet/minecraft/client/world/ClientWorld$Properties;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/registry/entry/RegistryEntry;IILjava/util/function/Supplier;Lnet/minecraft/client/render/WorldRenderer;ZJ)V",
         shift = Shift.AFTER
      )}
   )
   private void onPlayerSwitchDimension0(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
      this.worldChangeOnRespawn = true;
   }

   @Inject(
      method = {"onPlayerRespawn"},
      at = {@At("RETURN")}
   )
   private void onPlayerSwitchDimension(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
      if (this.worldChangeOnRespawn) {
         this.worldChangeOnRespawn = false;
         Listener.N().broadcast(this.field_3699);
      }

      Listener.av().broadcast(MinecraftClient.getInstance().player);
   }

   @Shadow
public abstract ClientConnection method_48296() ;

   @Shadow
   public void method_45729(String var1) { }

   @Shadow
   public void method_45730(String var1) { }

   @Inject(
      method = {"onPlayerPositionLook"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/packet/Packet;)V",
         ordinal = 1,
         shift = Shift.BEFORE
      )},
      locals = LocalCapture.CAPTURE_FAILHARD,
      cancellable = true
   )
   private void onTeleportConfirmResponse(PlayerPositionLookS2CPacket packet, CallbackInfo ci, @Local PlayerEntity playerEntity) {
      MovTasks$MovInfo eventContext = new MovTasks$MovInfo(playerEntity.getPos(), false, false, new Vec2f(playerEntity.getPitch(), playerEntity.getYaw()));
      Event<MovTasks$MovInfo> setBackEvent = new Event<>(eventContext, false, true);
      Listener.aA().catchEvent(setBackEvent);
      eventContext = setBackEvent.e();
      Vec2f override = eventContext.rotationOverride();
      float pitch = override == null ? playerEntity.getPitch() : override.x;
      float yaw = override == null ? playerEntity.getYaw() : override.y;
      boolean onGround = eventContext.oGroundOverride() == null ? playerEntity.isOnGround() : eventContext.oGroundOverride();
      this.method_48296()
         .send(
            PlayerMoveC2SPacketAccess.setCause(
               new Full(eventContext.vec3d().x, eventContext.vec3d().y, eventContext.vec3d().z, yaw, pitch, onGround), PlayerMoveC2SPacketAccess.Cause.SET_BACK
            )
         );
      ci.cancel();
   }

   @Inject(
      method = {"onPlayerList"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/SocialInteractionsManager;setPlayerOnline(Lnet/minecraft/client/network/PlayerListEntry;)V",
         shift = Shift.AFTER
      )},
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   private void onOtherPlayerJoin(PlayerListS2CPacket packet, CallbackInfo ci, @Local PlayerListEntry playerListEntry) {
      Listener.aK().broadcast(playerListEntry);
   }

   @Inject(
      method = {"onPlayerRemove"},
      at = {@At(
         value = "INVOKE",
         target = "Ljava/util/Set;remove(Ljava/lang/Object;)Z",
         shift = Shift.AFTER
      )}
   )
   private void onOtherPlayerExit(PlayerRemoveS2CPacket packet, CallbackInfo ci, @Local PlayerListEntry playerListEntry) {
      Listener.aL().broadcast(playerListEntry);
   }

   @Inject(
      method = {"handlePlayerListAction"},
      at = {@At("RETURN")}
   )
   private void onPlayerListUpdate(Action action, Entry receivedEntry, PlayerListEntry currentEntry, CallbackInfo ci) {
      Listener.aM().h(currentEntry, action);
   }

   @WrapOperation(
      method = {"onEntityVelocityUpdate"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/Entity;setVelocityClient(DDD)V"
      )}
   )
   private void onEntityVelocityUpdate(Entity instance, double x, double y, double z, Operation<Void> original) {
      if (!Listener.aN().d()) {
         Vec3d vec3d = new Vec3d(x, y, z);
         Event<Vec3d> vcUpdate = new Event<>(vec3d, true, true, instance);
         Listener.aN().b(vcUpdate);
         if (vcUpdate.d()) {
            return;
         }

         Vec3d vec3d1 = vcUpdate.e();
         original.call(new Object[]{instance, vec3d1.x, vec3d1.y, vec3d1.z});
      } else {
         original.call(new Object[]{instance, x, y, z});
      }
   }

   @WrapOperation(
      method = {"onExplosion"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;"
      )}
   )
   private Vec3d onExplosionVelocityUpdate(Vec3d instance, double x, double y, double z, Operation<Vec3d> original) {
      if (x == 0.0 && y == 0.0 && z == 0.0) {
         return (Vec3d)original.call(new Object[]{instance, x, y, z});
      } else {
         Vec3d vec3d = new Vec3d(x, y, z);
         Event<Vec3d> updateDeltaEvent = new Event<>(vec3d, true, true);
         Listener.aJ().catchEvent(updateDeltaEvent);
         if (!updateDeltaEvent.d()) {
            vec3d = updateDeltaEvent.e();
            return (Vec3d)original.call(new Object[]{instance, vec3d.x, vec3d.y, vec3d.z});
         } else {
            return instance;
         }
      }
   }

   @Inject(
      method = {"onEntitySpawn"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/world/ClientWorld;addEntity(Lnet/minecraft/entity/Entity;)V",
         shift = Shift.BEFORE
      )},
      cancellable = true
   )
   private void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo ci, @Local Entity playerEntity) {
      if (!Listener.aT().d()) {
         Event<Entity> entityAdd = new Event<>(playerEntity, true, false);
         Listener.aT().b(entityAdd);
         if (entityAdd.d()) {
            ci.cancel();
         }
      }
   }

   @WrapOperation(
      method = {"onBundle"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/packet/Packet;apply(Lnet/minecraft/network/listener/PacketListener;)V"
      )}
   )
   private void wrapBundledPacket(Packet instance, PacketListener t, Operation<Void> original) {
      if (t.getSide() == NetworkSide.SERVERBOUND) {
         original.call(new Object[]{instance, t});
      } else {
         Listener.callPacketHandleEvent(instance, t, (xva$0, xva$1) -> {
            Void var10000 = (Void)original.call(new Object[]{xva$0, xva$1});
         });
      }
   }

   @Inject(
      method = {"onChunkData"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;loadChunk(IILnet/minecraft/network/packet/s2c/play/ChunkData;)V",
         shift = Shift.AFTER
      )}
   )
   private void onLoadChunkPost(ChunkDataS2CPacket packet, CallbackInfo ci) {
      ChunkPos pos = new ChunkPos(packet.getChunkX(), packet.getChunkZ());
      Listener.aX().broadcast(pos);
   }
}

