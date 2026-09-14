package me.matl114.gui.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.function.Predicate;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.basic.KalamaHelperHelperIX;
import me.matl114.gui.basic.RenderHandler;
import me.matl114.gui.complex.BoxElement;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class IconElement extends BoxElement {
   protected Color bj = Color.WHITE;
   private KalamaHelperHelperIX bi;

   @Override
   public ElementHandler ah(Predicate<ElementHandler> handlerPredicate) {
      return new KalamaHelperHelperA(this, handlerPredicate, this);
   }

   public IconElement setActive(boolean var1) { }

   public void renderTexture(VDrawContext context, DrawableWidget element, boolean highlight) {
      Identifier var4 = this.getTextureId(context, element, highlight);
      if (var4 != null) {
         if (this.cq()) {
            context.V(var4, 0, 0, element.getTextureWidth(), element.getTextureHeight());
         } else {
            context.w(var4, 0, element.getTextureWidth(), 0, element.getTextureHeight(), 0, 0.0F, 1.0F, 0.0F, 1.0F);
         }
      }

      Integer var5 = this.bi == null ? (highlight ? -1 : null) : this.bi.toggle(element, highlight);
      if (var5 != null) {
         RenderHandler.K(context, 0, 0, element.getTextureWidth(), element.getTextureHeight(), var5);
      }
   }

   public static IconElement ck(Identifier activeState, Identifier inactiveState, ButtonAction action) {
      return new IconElement$SimpleIconElement(inactiveState, activeState, false, action);
   }

   public boolean bD() { }

   public void renderCentered0(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
      context.setShaderColor(this.bj.getRed() / 255.0F, this.bj.getGreen() / 255.0F, this.bj.getBlue() / 255.0F, alpha);
      RenderSystem.enableBlend();
      RenderSystem.enableDepthTest();
      this.renderTexture(context, element, shouldHighlight);
      context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static IconElement cj(Identifier identifier, ButtonAction action) {
      return new IconElement$SimpleIconElement(identifier, identifier, false, action);
   }

   public IconElement cs(KalamaHelperHelperIX highLightColor) {
      this.bi = highLightColor;
      return this;
   }

   public boolean cq() { }

   public static IconElement$SimpleIconElement cn(Identifier active, Identifier inactive, ButtonAction action) {
      return new IconElement$SimpleIconElement(inactive, active, true, action);
   }

   public IconElement(ButtonAction action) {
      super(action);
   }

   public Color ct() {
      return this.bj;
   }

   public static IconElement cl(Identifier activeState, Identifier inactiveState, ButtonAction action, Predicate<IconElement> activation) {
      return ((IconElement$SimpleIconElement)ck(activeState, inactiveState, action)).cw(activation);
   }

   public KalamaHelperHelperIX cr() {
      return this.bi;
   }

   public IconElement cu(Color shaderColor) {
      this.bj = shaderColor;
      return this;
   }

   public static IconElement cm(Identifier identifier, ButtonAction action) {
      return new IconElement$SimpleIconElement(identifier, identifier, true, action);
   }

   public static IconElement$SimpleIconElement co(Identifier activeState, Identifier inactiveState, ButtonAction action, Predicate<IconElement> activation) {
      return cn(activeState, inactiveState, action).cw(activation);
   }

   @Nullable
   public Identifier getTextureId(VDrawContext var1, DrawableWidget var2, boolean var3) { }
}
