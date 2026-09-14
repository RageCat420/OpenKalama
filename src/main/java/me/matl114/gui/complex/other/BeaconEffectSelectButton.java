package me.matl114.gui.complex.other;

import java.util.List;
import me.matl114.utils.ScreenUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BeaconEffectSelectButton extends PressableWidget {
   RegistryEntry<StatusEffect> ei;
   int currentIndex = 0;
   private static final int SIZE = BeaconEffectSelectButton.ec.size();
   private static Identifier ee = new Identifier("minecraft", "container/beacon/cancel");
   public static final List<RegistryEntry<StatusEffect>> ec = List.of(
      StatusEffects.SPEED, StatusEffects.HASTE, StatusEffects.RESISTANCE, StatusEffects.JUMP_BOOST, StatusEffects.STRENGTH, StatusEffects.REGENERATION
   );
   static final Identifier eg = new Identifier("minecraft", "container/beacon/button");
   Sprite ej;
   static final Identifier ef = new Identifier("minecraft", "container/beacon/button_highlighted");

   public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
      Identifier var5;
      if (this.isSelected()) {
         var5 = ef;
      } else {
         var5 = eg;
      }

      context.drawGuiTexture(var5, this.getX(), this.getY(), this.width, this.height);
      this.renderExtra(context);
   }

   protected void appendClickableNarrations(NarrationMessageBuilder builder) {
      this.appendDefaultNarrations(builder);
   }

   public RegistryEntry<StatusEffect> fL() {
      return this.ei;
   }

   private void updateCurrentEffect() {
      this.currentIndex = this.currentIndex % (SIZE + 1);
      if (this.currentIndex == 0) {
         this.ei = null;
         this.ej = null;
      } else {
         this.ei = ec.get(this.currentIndex - 1);
         this.ej = MinecraftClient.getInstance().getStatusEffectSpriteManager().getSprite(this.ei);
      }

      this.setTooltip(Tooltip.of(this.getNarrationMessage()));
   }

   protected void renderExtra(DrawContext context) {
      if (this.ej != null) {
         context.drawSprite(this.getX() + 2, this.getY() + 2, 0, 18, 18, this.ej);
      } else {
         context.drawGuiTexture(ee, this.getX() + 2, this.getY() + 2, 18, 18);
      }
   }

   protected MutableText getNarrationMessage() {
      return this.getMessage()
         .copy()
         .append(
            this.ei == null
               ? Text.translatable("widget.gui.beacon-effect-select-button.no-selection")
               : Text.translatable(((StatusEffect)(Object)this.ei.value()).getTranslationKey())
         );
   }

   public BeaconEffectSelectButton(int i, int j, int k, int l, Text text) {
      super(i, j, k, l, text);
      this.updateCurrentEffect();
   }

   public void onPress() {
      this.currentIndex = this.currentIndex + ec.size() + 1 + (ScreenUtils.hasShiftDown() ? -1 : 1);
      this.updateCurrentEffect();
   }
}
