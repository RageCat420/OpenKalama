package me.matl114.mixins.events;

import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.KalamaHelperHelperG;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.ButtonWidget.PressAction;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Environment(EnvType.CLIENT)
@Mixin({InventoryScreen.class})
public class InventoryScreenEvents extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {
   @Shadow
   @Final
   private RecipeBookWidget field_2929;

   public InventoryScreenEvents(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
      super(screenHandler, playerInventory, text);
   }

   @ModifyArg(
      method = {"init"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/widget/TexturedButtonWidget;<init>(IIIILnet/minecraft/client/gui/screen/ButtonTextures;Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;)V"
      ),
      index = 5
   )
   public PressAction modifyPressAction(PressAction pressAction) {
      return button -> {
         pressAction.onPress(button);
         if (!Listener.aj().d()) {
            Event<KalamaHelperHelperG> toggleRecipeBook = new Event<>(new KalamaHelperHelperG(this, this.field_2929, button), false, false);
            Listener.aj().catchEvent(toggleRecipeBook);
         }
      };
   }

   public void drawBackground(Object arg0, Object arg1, Object arg2, Object arg3) { }

}
