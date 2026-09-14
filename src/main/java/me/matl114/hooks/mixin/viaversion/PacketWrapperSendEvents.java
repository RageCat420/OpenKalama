package me.matl114.hooks.mixin.viaversion;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.protocol.packet.PacketWrapperImpl;
import me.matl114.events.Event;
import me.matl114.events.PacketManager;
import me.matl114.events.packets.PacketStorage;
import me.matl114.hooks.impl.viaversion.PacketWrapperSendStorageImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Environment(EnvType.CLIENT)
@Pseudo
@Mixin({PacketWrapperImpl.class})
public class PacketWrapperSendEvents implements PacketWrapper {
   @WrapMethod(
      method = {"Lcom/viaversion/viaversion/protocol/packet/PacketWrapperImpl;sendToServer0(Ljava/lang/Class;ZZ)V"},
      remap = false,
      require = 0
   )
   public void onSendToServer(Class<?> protocol, boolean skipCurrentPipeline, boolean currentThread, Operation<Void> operation) {
      if (this.user() != null && this.user().getChannel() != null) {
         if (this.getPacketType() != null && this.getPacketType().state() == State.PLAY && !PacketManager.f) {
            PacketWrapperSendStorageImpl storageImpl = new PacketWrapperSendStorageImpl(
               System.currentTimeMillis(), this, this.user().getChannel(), bl -> operation.call(new Object[]{protocol, skipCurrentPipeline, bl})
            );
            Event<PacketStorage> event = new Event<>(storageImpl, true, false);
            PacketManager.z().b(event);
            if (event.d()) {
               PacketManager.v(storageImpl);
               return;
            }
         }

         operation.call(new Object[]{protocol, skipCurrentPipeline, currentThread});
      }
   }

   public void setId(Object arg0) { }

}
