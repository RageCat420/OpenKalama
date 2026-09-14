package me.matl114.hacks.modules.extra;

import java.util.Arrays;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.elements.IconElement$SimpleIconElement;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.client.gui.screen.world.WorldIcon;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

class ExtraSubHelperM extends IconElement$SimpleIconElement {
   private final ServerScanner bx;
   private final Identifier bw;

   ServerInfo bt;
   WorldIcon bu;
   @Nullable
   private byte[] favicon;

   private boolean uploadFavicon(@Nullable byte[] bytes) {
      if (bytes == null) {
         this.bu.destroy();
      } else {
         try {
            this.bu.load(NativeImage.read(bytes));
         } catch (Throwable var3) {
            return false;
         }
      }

      return true;
   }

   public WorldIcon getWorldIcon() {
      return this.bx.wu.computeIfAbsent(this.bt.address, s -> WorldIcon.forServer(mc.getTextureManager(), s));
   }

   ExtraSubHelperM(final ServerScanner this$0, Identifier inactiveId, Identifier activeId, boolean gui, ButtonAction action, final ServerInfo param6) {
      super(inactiveId, activeId, gui, action);
      this.bx = this$0;
      this.bw = inactiveId;
      this.bt = param6;
      this.bu = this.getWorldIcon();
   }

   @Nullable
   @Override
   public Identifier getTextureId(VDrawContext context, DrawableWidget element, boolean highlight) {
      byte[] var4 = this.bt.getFavicon();
      if (!Arrays.equals(var4, this.favicon)) {
         if (this.uploadFavicon(var4)) {
            this.favicon = var4;
         } else {
            this.bt.setFavicon(null);
         }
      }

      return this.bu.getTextureId();
   }
}
