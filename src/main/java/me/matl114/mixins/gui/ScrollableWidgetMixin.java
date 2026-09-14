package me.matl114.mixins.gui;

import me.matl114.gui.basic.Draggable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.CLIENT)
@Mixin({ScrollableWidget.class})
public abstract class ScrollableWidgetMixin extends ClickableWidget implements Draggable {
   @Shadow
   private boolean field_39498;

   public ScrollableWidgetMixin(int x, int y, int width, int height, Text message) {
      super(x, y, width, height, message);
   }

   @Shadow
   protected abstract boolean method_44392();

   @Override
   public boolean isDragging() {
      return this.field_39498;
   }

   @Override
   public void releaseDrag(Screen screen, double mouseX, double mouseY) {
      this.field_39498 = false;
   }

   @Override
   public boolean startDrag(Screen screen, double mouseX, double mouseY) {
      if (this.method_44392()
         && mouseX >= this.getX() + this.width
         && mouseX <= this.getX() + this.width + 8
         && mouseY >= this.getY()
         && mouseY < this.getY() + this.height) {
         this.field_39498 = true;
         return true;
      } else {
         return false;
      }
   }
}
