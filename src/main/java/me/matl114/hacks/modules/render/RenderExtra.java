package me.matl114.hacks.modules.render;

import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.utils.Debug;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket.Status;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.util.Formatting;

public class RenderExtra extends BaseModule {
   public final ModulePath ow;
   public final FlagRef nightvision;
   public final FlagRef disableWurstHud;
   public static RenderExtra INSTANCE;
   public final FlagRef ignoreServerRequest;
   public final FlagRef noWorldBobView;
   public final ModulePath ou = makePath(Configs.i, "resource");
   public final ModulePath ov = this.ou.add("server");
   public final FlagRef enhancedDebugHud;

   public RenderExtra() {
      super("RenderExtra");
      this.ow = makePath(Configs.i, "render");
      this.ignoreServerRequest = this.flagBuilder(this.ov.add("ignore-server-request")).build();
      this.nightvision = this.builder(this.ow.add("nightvision"), Boolean.class).defaultValue(true).build();
      this.noWorldBobView = this.builder(this.ow.add("no-world-bob-view"), FlagRef.TYPE).defaultValue(true).build();
      this.disableWurstHud = this.flagBuilder(this.ow.add("disable-wurst-hud")).build();
      this.enhancedDebugHud = this.flagBuilder(this.ow.add("enhanced-debug-hud")).build();
      INSTANCE = this;
   }

   public void wY(Event<MatrixStack> event) {
      if (this.noWorldBobView.get()) {
         event.cancel();
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.ap().getChannel(ResourcePackSendS2CPacket.class), this::onResourceRequest);
      this.registerListener(RenderListener.p(), this::wY);
   }

   public void onResourceRequest(Event<ResourcePackSendS2CPacket> resourceEvent) {
      if (this.ignoreServerRequest.get()) {
         ClientConnection var2 = resourceEvent.getArgs(0);
         ResourcePackSendS2CPacket var3 = (ResourcePackSendS2CPacket)resourceEvent.e();
         var2.send(new ResourcePackStatusC2SPacket(var3.id(), Status.ACCEPTED));
         var2.send(new ResourcePackStatusC2SPacket(var3.id(), Status.DOWNLOADED));
         var2.send(new ResourcePackStatusC2SPacket(var3.id(), Status.SUCCESSFULLY_LOADED));
         Debug.chat(Text.literal("Successfully reject server resourcepack").formatted(Formatting.GREEN), var3.id());
         Debug.chat(
            Text.literal("Download url:").formatted(Formatting.GREEN),
            Text.literal(var3.url()).setStyle(Style.EMPTY.withClickEvent(new ClickEvent(Action.OPEN_URL, var3.url()))).formatted(Formatting.YELLOW)
         );
         resourceEvent.cancel();
      }
   }
}
