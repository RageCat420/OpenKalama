package me.matl114.gui.complex.slimefun;

import java.util.function.Consumer;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.KalamaHelperHelperP;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.elements.PlateElement;
import me.matl114.gui.elements.SlotElement;
import me.matl114.hacks.InvTasks;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.Debug;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SavedItemWidget extends KalamaHelperHelperCX {
   protected static final int x = 144;
   ItemStack v;
   protected static final int y = 64;
   Consumer<ItemStack> w;

   public SavedItemWidget(int x, int y, ItemStack itemStack, Consumer<ItemStack> callback) {
      super(x, y, 144, 64);
      this.v = itemStack;
      this.w = callback == null ? it -> {} : callback;
      this.init0();
   }

   private final void init0() {
      DisplayWidget.instance(0, 0, 144, 64).<DrawableWidget>setRenderHandler(PlateElement.cg()).addToSub(this);
      ExecutableWidget.instance(15, 5, 54, 54)
         .<ExecutableWidget>eV(new SlotElement(this.v).cF(KalamaHelperHelperP.aA(() -> this.w.accept(this.v))))
         .addToSub(this);
      ExecutableWidget.instance(75, 12, 25, 16)
         .<ExecutableWidget>eV(
            new ButtonElement(TextProvider.c(Text.translatable("widget.gui.saved-item-widget.open-editor")), ButtonAction.a(() -> InvTasks.Y(this.v, null)))
               .aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.gui.saved-item-widget.open-editor.tooltips", "")))
         )
         .addToSub(this);
      ExecutableWidget.instance(75, 36, 25, 16)
         .<ExecutableWidget>eV(new ButtonElement(TextProvider.c(Text.translatable("widget.gui.saved-item-widget.creative-give")), ButtonAction.a(() -> {
            if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().interactionManager.getCurrentGameMode().isCreative()) {
               InvTasks.creativeAddItem(this.v, 64);
            } else {
               Debug.b(Text.translatable("widget.gui.saved-item-widget.creative-give.error").formatted(Formatting.YELLOW));
            }
         })).aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.gui.saved-item-widget.creative-give.tooltips", ""))))
         .addToSub(this);
      ExecutableWidget.instance(105, 12, 25, 16)
         .<ExecutableWidget>eV(
            new ButtonElement(
                  TextProvider.c(Text.translatable("widget.gui.saved-item-widget.delete-item")), ButtonAction.a(() -> InvTasks.aq().removeSavedItem(this.v))
               )
               .aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.gui.saved-item-widget.delete-item.tooltips", "")))
         )
         .addToSub(this);
      ExecutableWidget.instance(105, 36, 25, 16)
         .<ExecutableWidget>eV(
            new ButtonElement(
                  TextProvider.c(Text.translatable("widget.gui.saved-item-widget.copy-command")), ButtonAction.a(() -> InvTasks.copyGiveCommand(this.v))
               )
               .aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.gui.saved-item-widget.copy-command.tooltips", "")))
         )
         .addToSub(this);
   }
}
