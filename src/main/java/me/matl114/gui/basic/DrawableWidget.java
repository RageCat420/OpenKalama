package me.matl114.gui.basic;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import me.matl114.accessors.gui.ScreenAccess;
import me.matl114.versioned.api.VDrawContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.Selectable.SelectionType;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class DrawableWidget implements Element, Drawable, Widget, Selectable, Draggable {
   private int x;
   private boolean subWidget;
   private int textureWidth;
   protected boolean selected;
   protected boolean focused;
   protected int dy;
   private RenderHandler renderHandler;
   private int y;
   private float alpha;
   private int textureHeight;
   protected int priority;
   private float textureScale = 1.0F;
   protected int dx;

   public void mouseMoved(double mouseX, double mouseY) {
   }

   public void setHeight(int height) {
      this.dy = height;
   }

   public boolean isSubWidget() {
      return this.subWidget;
   }

   public <T extends DrawableWidget> T addToSub(KalamaHelperHelperCX screen, int priority) {
      this.setPriority(priority);
      this.addInternal(screen);
      return (T)(Object)this;
   }

   @Override
   public boolean startDrag(Screen screen, double mouseX, double mouseY) {
      return false;
   }

   public <T extends DrawableWidget> T updateRenderHandler(UnaryOperator<RenderHandler> updater) {
      this.renderHandler = updater.apply((RenderHandler)(this.renderHandler == null ? new AbstractElement() : this.renderHandler));
      return (T)(Object)this;
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      return false;
   }

   public final void render(DrawContext context, int mouseX, int mouseY, float delta) {
      VDrawContext vdraw = VDrawContext.P(context);
      this.render0(vdraw, mouseX, mouseY, delta, false);
      vdraw.D();
   }

   public <T extends DrawableWidget> T addTo(Screen screen) {
      ScreenAccess.of(screen).addDrawableChildTo(this);
      return (T)(Object)this;
   }

   public float getAlpha() {
      return this.alpha;
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      return false;
   }

   public void setFocused(boolean focused) {
      this.focused = focused;
   }

   public void setSelected(boolean s) {
      this.selected = s;
   }

   public SelectionType getType() {
      return this.selected ? SelectionType.HOVERED : SelectionType.NONE;
   }

   public int getTextureHeight() {
      return this.textureHeight;
   }

   @Override
   public void releaseDrag(Screen screen, double mouseX, double mouseY) {
   }

   public <T extends DrawableWidget> T addToSub(KalamaHelperHelperCX screen) {
      this.addInternal(screen);
      return (T)(Object)this;
   }

   public void setSubWidget(boolean s) {
      this.subWidget = s;
   }

   public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
      return false;
   }

   public <T extends DrawableWidget> T cast() {
      return (T)(Object)this;
   }

   public void render0(VDrawContext context, int mouseX, int mouseY, float delta, boolean disableSelect) {
      this.selected = !disableSelect && this.isMouseOver(mouseX, mouseY);
      context.f().pushMatrix();
      context.f().translate(this.getX(), this.getY());
      if (this.priority != 0) {
         context.d(this.priority);
      }

      float textureScale = this.getTextureScale();
      if (textureScale != 1.0F) {
         context.f().scale(textureScale, textureScale);
      }

      this.renderInDefaultMatrix(context, mouseX, mouseY, delta, disableSelect);
      if (this.priority != 0) {
         context.e();
      }

      context.f().popMatrix();
      this.renderAbsolute(context, mouseX, mouseY, delta, disableSelect);
   }

   public boolean canSelect() {
      return this.renderHandler != null && this.renderHandler.canBeSelected(this);
   }

   public void setX(int x) {
      this.x = x;
   }

   protected void checkSelect(boolean disableSelect, int mouseX, int mouseY) {
      this.selected = !disableSelect && this.isMouseOver(mouseX, mouseY);
   }

   public int getY() {
      return this.y;
   }

   @Nullable
   public final GuiNavigationPath getFocusedPath() {
      return null;
   }

   public int getWidth() {
      return this.dx;
   }

   public int getTextureWidth() {
      return this.textureWidth;
   }

   public int getExtraDepth() {
      return this.priority;
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) { }

   public void setWidth(int width) {
      this.dx = width;
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) { }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      return false;
   }

   public final <T extends DrawableWidget> T setPriority(int depth) {
      this.priority = depth;
      return (T)(Object)this;
   }

   public void renderInDefaultMatrix(VDrawContext context, int mouseX, int mouseY, float delta, boolean disableSelect) {
      if (this.renderHandler != null) {
         this.renderHandler.renderAtCentered(this, context, mouseX, mouseY, delta, this.alpha, this.selected);
      }
   }

   public boolean charTyped(char chr, int modifiers) {
      return false;
   }

   public RenderHandler getRenderHandler() {
      return this.renderHandler;
   }

   public <T extends DrawableWidget> T setAlpha(float scale) {
      this.alpha = scale;
      return (T)(Object)this;
   }

   public boolean isSelected() {
      return this.selected;
   }

   @Override
   public boolean isDragging() {
      return false;
   }

   private void addInternal(KalamaHelperHelperCX screen) {
      this.subWidget = true;
      screen.Q(this);
   }

   public <T extends DrawableWidget> T setTextureScale(float scale) {
      this.textureScale = scale;
      this.updateScale();
      return (T)(Object)this;
   }

   public boolean isFocused() {
      return this.focused;
   }

   public DrawableWidget(int x, int y, int dx, int dy) {
      this.alpha = 1.0F;
      this.subWidget = false;
      this.x = x;
      this.y = y;
      this.dx = dx;
      this.dy = dy;
      this.updateScale();
   }

   public final ScreenRect getNavigationFocus() {
      return ScreenRect.empty();
   }

   public <T extends DrawableWidget> T setRenderHandler(RenderHandler renderHandler) {
      this.renderHandler = renderHandler;
      return (T)(Object)this;
   }

   public void renderAbsolute(VDrawContext context, int mouseX, int mouseY, float delta, boolean disableSelect) {
      if (this.renderHandler != null) {
         this.renderHandler.f(this, context, mouseX, mouseY, delta, this.alpha, this.selected);
      }
   }

   public void setY(int y) {
      this.y = y;
   }

   public boolean isMouseOver(double mouseX, double mouseY) {
      return mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.textureWidth && mouseY < this.getY() + this.textureHeight;
   }

   public int getHeight() {
      return this.dy;
   }

   public int getX() {
      return this.x;
   }

   private final void updateScale() {
      this.textureWidth = (int)(this.dx / this.textureScale);
      this.textureHeight = (int)(this.dy / this.textureScale);
   }

   public final void appendNarrations(NarrationMessageBuilder builder) {
   }

   @Nullable
   public final GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
      return null;
   }

   public float getTextureScale() {
      return this.textureScale;
   }

   public final void forEachChild(Consumer<ClickableWidget> consumer) {
   }
}
