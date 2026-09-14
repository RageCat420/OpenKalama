package me.matl114.jsApi;

import java.util.List;
import java.util.Objects;
import me.matl114.accessors.access.PlayerInteractBlockC2SPacketAccess;
import me.matl114.accessors.hacks.PlayerInteractionAccess;
import me.matl114.events.Listener;
import me.matl114.events.annotations.Modifiable;
import me.matl114.hacks.InvTasks;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.NetworkUtils;
import me.matl114.utils.RaycastUtils;
import me.matl114.versioned.api.VPacket;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket.InteractAtHandler;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket.InteractHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

@Modifiable
public class PacketHelper {
   static MinecraftClient mc = MinecraftClient.getInstance();

   public static void sendInteractEntityAt(Object entity, Object pos, boolean sneaking, boolean offhand) {
      int var5;
      if (entity instanceof Integer var4) {
         var5 = var4;
      } else {
         Entity var6 = JsHelper.a(entity, Entity.class);
         var5 = var6.getId();
      }

      Vec3d var7 = DataHelper.createVec(pos);
      syncHotbar();
      mc.interactionManager
         .sendSequencedPacket(
            mc.world, seq -> new PlayerInteractEntityC2SPacket(var5, sneaking, new InteractAtHandler(offhand ? Hand.OFF_HAND : Hand.MAIN_HAND, var7))
         );
   }

   public static void sendCloseInventory(int syncId) {
      mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(syncId));
   }

   public static void sendMoveOnGround(boolean onGround, boolean horizontalCollision) {
      mc.getNetworkHandler().sendPacket(VPacket.f(onGround, horizontalCollision));
   }

   public static void sendInteractItem(boolean offHand, float pitch, float yaw) {
      syncHotbar();
      mc.interactionManager.sendSequencedPacket(mc.world, seq -> new PlayerInteractItemC2SPacket(offHand ? Hand.OFF_HAND : Hand.MAIN_HAND, seq, yaw, pitch));
   }

   public static void sendInteractBlock(Object pos, Object direction, boolean offhand) {
      Direction var3 = JsHelper.b(direction, Direction.class);
      BlockPos var4 = DataHelper.f(pos);
      BlockHitResult var5 = RaycastUtils.createHitResult(var4, var3);
      syncHotbar();
      mc.interactionManager
         .sendSequencedPacket(
            mc.world,
            seq -> {
               PlayerInteractBlockC2SPacket var3x = new PlayerInteractBlockC2SPacket(offhand ? Hand.OFF_HAND : Hand.MAIN_HAND, var5, seq);
               if (var3x instanceof PlayerInteractBlockC2SPacketAccess var4x) {
                  var4x.setUseContext(
                     new PlayerInteractBlockC2SPacketAccess.UseContext(
                        mc.player.getStackInHand(offhand ? Hand.OFF_HAND : Hand.MAIN_HAND).copy(),
                        mc.world.getBlockState(var5.getBlockPos()),
                        ActionResult.SUCCESS,
                        false
                     )
                  );
               }

               return var3x;
            }
         );
   }

   public static void sendMoveFull(double x, double y, double z, float yaw, float pitch, boolean isOnGround, boolean collision) {
      mc.getNetworkHandler().sendPacket(VPacket.j(x, y, z, yaw, pitch, isOnGround, collision));
   }

   private static void syncHotbar() {
      PlayerInteractionAccess.of(mc.interactionManager).syncSelectedHotbar(InventoryUtils.getSelectedSlot());
   }

   public static void u() {
      PlayerInteractionAccess var0 = PlayerInteractionAccess.of(mc.interactionManager);
      var0.sendBreakPacket();
   }

   public static void sendInventoryPacket(int slotId, int button, Object actionTypeStr) {
      InvTasks.clickSlotAsync(slotId, button, JsHelper.b(actionTypeStr, SlotActionType.class));
   }

   public static int d() {
      return NetworkUtils.generateNextSequence();
   }

   public static List<String> getAllPacketTypes() {
      return Listener.K().keySet().stream().map(PacketType::id).<String>map(Identifier::toString).toList();
   }

   public static Class<? extends Packet<?>> getPacketType(String packetType, boolean s2c) {
      Identifier var2 = Identifier.tryParse(packetType);
      return Listener.c(var2, s2c);
   }

   public static void sendInteractEntity(Object entity, boolean sneaking, boolean offhand) {
      int var4;
      if (entity instanceof Integer var3) {
         var4 = var3;
      } else {
         Entity var5 = JsHelper.a(entity, Entity.class);
         var4 = var5.getId();
      }

      syncHotbar();
      mc.interactionManager
         .sendSequencedPacket(mc.world, seq -> new PlayerInteractEntityC2SPacket(var4, sneaking, new InteractHandler(offhand ? Hand.OFF_HAND : Hand.MAIN_HAND)));
   }

   public static void m(Object entity, boolean offhand) {
      sendInteractEntity(entity, mc.player.isSneaking(), offhand);
   }

   public static void o(boolean offHand) {
      sendInteractItem(offHand, mc.player.getPitch(), mc.player.getYaw());
   }

   public static void sendSwingHand(boolean offhand) {
      mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(offhand ? Hand.OFF_HAND : Hand.MAIN_HAND));
   }

   public static void sendStartMining(Object pos, Object direction) {
      Direction var2 = JsHelper.b(direction, Direction.class);
      BlockPos var3 = DataHelper.f(pos);
      PlayerInteractionAccess.of(mc.interactionManager).sendBreakPacket(var3, var2);
   }

   public static void sendPacket(Entity entity) {
      mc.getNetworkHandler().sendPacket(VPacket.i(JsHelper.a(entity, Entity.class)));
   }

   public static int f() {
      return InvTasks.i;
   }

   public static void syncSelectedHotbar(int x) {
      PlayerInteractionAccess.of(mc.interactionManager).syncSelectedHotbar(x);
   }

   public static void k(int x, int y, int z, Object direction, boolean offhand) {
      sendInteractBlock(new BlockPos(x, y, z), direction, offhand);
   }

   public static void b(Packet<?> packet) {
      mc.getNetworkHandler().sendPacket(packet);
   }

   public static void sendClientCommand(Object cmd) {
      Mode var1 = JsHelper.b(cmd, Mode.class);
      mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, var1));
   }

   public static void s(Object pos, Object direction) {
   }

   public static void h(int x, int y, int z, Object direction) {
      sendAttackBlock(new BlockPos(x, y, z), direction);
   }

   public static void w(int x, int y, int z, Object direction) {
   }

   public static void t(Object pos, Object direction) {
      Direction var2 = JsHelper.b(direction, Direction.class);
      BlockPos var3 = DataHelper.f(pos);
      PlayerInteractionAccess.of(mc.interactionManager).startMiningBlock(var3, var2);
   }

   public static void sendMoveLookAndOnGround(float yaw, float pitch, boolean isOnGround, boolean collision) {
      mc.getNetworkHandler().sendPacket(VPacket.h(yaw, pitch, isOnGround, collision));
   }

   public static void sendPlayerAction(Object action) {
      Action var1 = JsHelper.b(action, Action.class);
      switch (var1) {
         case SWAP_ITEM_WITH_OFFHAND:
         case DROP_ITEM:
         case DROP_ALL_ITEMS:
         case RELEASE_USE_ITEM:
            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(var1, BlockPos.ORIGIN, Direction.DOWN));
            return;
         default:
            throw new IllegalStateException("Unexpected value: " + var1);
      }
   }

   public static void sendMovePositionAndOnGround(double x, double y, double z, boolean isOnGround, boolean collision) {
      mc.getNetworkHandler().sendPacket(VPacket.g(x, y, z, isOnGround, collision));
   }

   public static void sendAttackBlock(Object pos, Object direction) {
      Direction var2 = JsHelper.b(direction, Direction.class);
      BlockPos var3 = DataHelper.f(pos);
      if (mc.player.getAbilities().creativeMode) {
         mc.interactionManager.sendSequencedPacket(mc.world, sequence -> {
            mc.execute(() -> mc.interactionManager.breakBlock(var3));
            return new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, var3, var2, sequence);
         });
      } else {
         BlockState var4 = mc.world.getBlockState(var3);
         float var5 = var4.calcBlockBreakingDelta(mc.player, mc.world, var3);
         boolean var6 = var5 > 1.0F;
         if (var6) {
            mc.interactionManager.sendSequencedPacket(mc.world, sequence -> new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, var3, var2, sequence));
            mc.execute(() -> mc.interactionManager.breakBlock(var3));
         } else if (!Objects.equals(var3, PlayerInteractionAccess.of(mc.interactionManager).getCurrentMiningPos())) {
            if (mc.interactionManager.isBreakingBlock()) {
               mc.getNetworkHandler()
                  .sendPacket(
                     new PlayerActionC2SPacket(Action.ABORT_DESTROY_BLOCK, PlayerInteractionAccess.of(mc.interactionManager).getCurrentMiningPos(), var2)
                  );
            }

            PlayerInteractionAccess.of(mc.interactionManager).startMiningBlock(var3, var2);
         }
      }
   }
}
