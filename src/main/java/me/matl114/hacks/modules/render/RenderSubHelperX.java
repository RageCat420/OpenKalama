package me.matl114.hacks.modules.render;


import net.minecraft.client.MinecraftClient;
import java.util.Objects;
import me.matl114.hacks.api.ModuleEntry;
import me.matl114.utils.commands.params.impl.StringArgumentResult;
import net.minecraft.text.Text;

public class RenderSubHelperX implements StringArgumentResult {
   Text Sf;
   ModuleEntry Sb;
   Text Se;
   boolean lastState;
   double switchCountDown;

   public boolean ajU() {
      return this.lastState || this.switchCountDown >= 0.0;
   }

   @Override
   public Text resultAsString() {
      return this.Se;
   }

   public boolean tickUpdate() {
      boolean var1 = false;
      if (this.lastState != this.Sb.getActiveState()) {
         this.lastState = this.Sb.getActiveState();
         this.switchCountDown = 10.0;
         var1 = true;
      }

      if (this.switchCountDown >= 0.0) {
         this.switchCountDown--;
      }

      if (!Objects.equals(this.Sf, this.Sb.getMetaData())) {
         this.Sf = this.Sb.getMetaData();
         this.Se = this.Sf != null && MinecraftClient.getInstance().textRenderer.getTextHandler().getWidth(this.Sf) > 0.0F
            ? this.Sb.getDisplay().append(Text.literal("[")).append(this.Sf).append(Text.literal("]"))
            : this.Sb.getDisplay();
         var1 = true;
      }

      return var1;
   }

   public RenderSubHelperX(ModuleEntry moduleEntry) {
      this.Sb = moduleEntry;
      this.lastState = moduleEntry.getActiveState();
      this.switchCountDown = -1.0;
      this.Se = moduleEntry.getDisplay();
   }

   public double getAnimationHeight() {
      return this.switchCountDown < 0.0 ? this.switchCountDown : (this.lastState ? 9.0 - this.switchCountDown : this.switchCountDown);
   }
}
