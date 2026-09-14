package me.matl114.gui;

import me.matl114.accessors.gui.ScreenAccess;
import me.matl114.gui.basic.Draggable;
import me.matl114.gui.basic.DrawableWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.Selectable.SelectionType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.Text;

public class GenericScreen extends Screen implements Selectable, Draggable {
   protected int x;
   protected Text titleLabel;
   public static final MinecraftClient mc = MinecraftClient.getInstance();
   protected int backgroundWidth;
   protected int y;
   protected float currentShrink = 1.0F;
   protected int backgroundDefaultHeight;
   protected int backgroundHeight;
   protected Draggable draggingElement = null;

   @Override
   public void releaseDrag(Screen screen, double mouseX, double mouseY) {
      if (this.draggingElement != null) {
         this.draggingElement.releaseDrag(this, mouseX, mouseY);
         this.draggingElement = null;
      }
   }

   public GenericScreen setTitleLabel(Text text) {
      this.titleLabel = text;
      return this;
   }

   public final boolean mouseClicked(double mouseX, double mouseY, int button) {
      boolean val = super.mouseClicked(mouseX, mouseY, button);
      if (button == 0) {
         this.startDrag(this, mouseX, mouseY);
      }

      return val;
   }

   public SelectionType getType() {
      return this.isFocused() ? SelectionType.FOCUSED : SelectionType.NONE;
   }

   public final boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      return this.draggingElement != null && button == 0 && this.draggingElement.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
   }

   public final boolean mouseReleased(double mouseX, double mouseY, int button) {
      if (button == 0) {
         this.releaseDrag(this, mouseX, mouseY);
      }

      return super.mouseReleased(mouseX, mouseY, button);
   }

   @Override
   public boolean startDrag(Screen screen, double mouseX, double mouseY) {
      for (Element iter : this.children()) {
         if (iter instanceof Draggable drag && drag.startDrag(this, mouseX, mouseY)) {
            this.draggingElement = drag;
            return true;
         }
      }

      return false;
   }

   public final boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      for (Element element : this.children()) {
         if (element.isMouseOver(mouseX, mouseY) && element.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            return true;
         }
      }

      return false;
   }

   public Text getTitleLabel(DrawableWidget widget) {
      return this.titleLabel;
   }

   public void resetScreen() {
      this.initTabNavigation();
   }

   public ScreenAccess access() {
      return ScreenAccess.of(this);
   }

   protected GenericScreen(Text title, int backgroundWidth, int backgroundDefaultHeight) {
      super(title);
      this.setTitleLabel(title);
      this.backgroundDefaultHeight = backgroundDefaultHeight;
      this.backgroundWidth = backgroundWidth;
      this.backgroundHeight = backgroundDefaultHeight;
   }

   protected void init() {
      super.init();
      this.init0();
   }

   public void appendNarrations(NarrationMessageBuilder builder) {
   }

   public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
      if (this.client.world == null) {
         super.renderBackground(context, mouseX, mouseY, delta);
      }
   }

   protected void init0() {
      this.x = (this.width - this.backgroundWidth) / 2;
      if (this.height > this.backgroundDefaultHeight + 24) {
         this.backgroundHeight = this.backgroundDefaultHeight;
         this.y = (this.height - this.backgroundHeight) / 2;
      } else {
         this.y = 12;
         this.backgroundHeight = this.height - 24;
      }
   }

   public final boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (super.keyPressed(keyCode, scanCode, modifiers)) {
         return true;
      } else if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
         this.close();
         return true;
      } else {
         return true;
      }
   }
}
