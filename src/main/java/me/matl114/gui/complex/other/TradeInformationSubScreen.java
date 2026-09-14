package me.matl114.gui.complex.other;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.List;
import javax.annotation.Nullable;
import me.matl114.accessors.access.MerchantScreenAccess;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.elements.IconElement;
import me.matl114.gui.elements.LabelElement;
import me.matl114.gui.elements.SlotElement;
import me.matl114.hacks.InvTasks;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.ScreenUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

public class TradeInformationSubScreen extends KalamaHelperHelperCX {
   private static final int ek = 18;
   private static final Text er = Text.translatable("widget.fast-trade.trade");
   private static final int en = 9;
   private static final Text et = Text.translatable("widget.fast-trade.toggle-drop");
   private static final List<Text> eu = List.of(Text.translatable("widget.fast-trade.toggle-drop.tooltips"));
   private static final int em = 10;
   private static final List<Text> es = List.of(Text.translatable("widget.fast-trade.trade.tooltips"));
   private static final int el = 18;
   private MerchantScreen screen;
   private static final Identifier eo = Identifier.ofVanilla("container/villager/out_of_stock");
   private static final Identifier ep = new Identifier("kalama", "gui/trade_arrow");
   private static final MinecraftClient bT = MinecraftClient.getInstance();
   private static final int dG = 8;

   protected void init() {
      ExecutableWidget.instance(1, 1, 18, 18).<ExecutableWidget>eV(new SlotElement(InventoryUtils.b(() -> {
         TradeOffer var1 = this.getCurrentTrade();
         return var1 == null ? ItemStack.EMPTY : var1.getDisplayedFirstBuyItem();
      }), 0, InvTasks.Z()).ag(this::active)).addToSub(this);
      ExecutableWidget.instance(21, 1, 18, 18).<ExecutableWidget>eV(new SlotElement(InventoryUtils.b(() -> {
         TradeOffer var1 = this.getCurrentTrade();
         return var1 == null ? ItemStack.EMPTY : var1.getDisplayedSecondBuyItem();
      }), 0, InvTasks.Z()).ag(this::active)).addToSub(this);
      DisplayWidget.instance(40, 5, 10, 9).<DrawableWidget>setRenderHandler(IconElement.co(ep, eo, ButtonAction.c(), el -> {
         TradeOffer var2 = this.getCurrentTrade();
         return var2 != null && !var2.isDisabled();
      }).aO(TooltipHandler.ar(() -> {
         TradeOffer var1 = this.getCurrentTrade();
         if (var1 == null) {
            return List.of();
         } else {
            Builder var2 = ImmutableList.builder();
            var2.add(Text.translatable("widget.fast-trade.trade-info.0").formatted(Formatting.AQUA));
            var2.add(Text.translatable("widget.fast-trade.trade-info.1").formatted(Formatting.GREEN));
            var2.add(Text.literal("------------------").formatted(Formatting.GREEN));
            var2.add(Text.translatable("widget.fast-trade.trade-info.max-trade", new Object[]{String.valueOf(var1.getMaxUses())}));
            var2.add(Text.translatable("widget.fast-trade.trade-info.current-trade", new Object[]{String.valueOf(var1.getUses())}));
            var2.add(Text.translatable("widget.fast-trade.trade-info.default-count", new Object[]{String.valueOf(var1.getFirstBuyItem().count())}));
            var2.add(Text.translatable("widget.fast-trade.trade-info.price-multiplier", new Object[]{"%.2f".formatted(var1.getPriceMultiplier())}));
            var2.add(Text.translatable("widget.fast-trade.trade-info.demand-bonus", new Object[]{String.valueOf(var1.getDemandBonus())}));
            var2.add(Text.translatable("widget.fast-trade.trade-info.special-price", new Object[]{String.valueOf(var1.getSpecialPrice())}));
            return var2.build();
         }
      })).ag(this::active)).addToSub(this);
      ExecutableWidget.instance(51, 0, 18, 18).<ExecutableWidget>eV(new SlotElement(InventoryUtils.b(() -> {
         TradeOffer var1 = this.getCurrentTrade();
         return var1 == null ? ItemStack.EMPTY : var1.getSellItem();
      }), 0, InvTasks.Z()).ag(this::active)).addToSub(this);
      ExecutableWidget.instance(71, 0, 18, 8).<ExecutableWidget>eV(new ButtonElement(TextProvider.c(er), ButtonAction.a(() -> {
         if (ScreenUtils.hasShiftDown()) {
            this.craft();
         } else {
            this.place();
         }
      })).aO(TooltipHandler.ap(es))).addToSub(this);
      ExecutableWidget.instance(71, 12, 18, 8)
         .<ExecutableWidget>eV(
            new ButtonElement(TextProvider.c(et), ButtonAction.a(HotKeyUtils.wrapFlagAsToggle("fast-craft.drop-craft", InvTasks.ah().dropCraft)))
               .aO(TooltipHandler.ap(eu))
         )
         .addToSub(this);
      DisplayWidget.instance(10, 0, 50, 20)
         .<DrawableWidget>setRenderHandler(LabelElement.instance(Text.translatable("widget.fast-trade.no-select").formatted(Formatting.RED)).ag(this::active))
         .addToSub(this);
   }

   private boolean active(ElementHandler ignored) {
      return this.getCurrentTrade() != null;
   }

   public TradeInformationSubScreen(int x, int y, MerchantScreen screen) {
      super(x + 258 - 90, y + 60, 90, 20);
      this.screen = (MerchantScreen)Preconditions.checkNotNull(screen);
      this.init();
   }

   private void place() {
      if (bT.player != null) {
         MerchantScreenAccess var1 = MerchantScreenAccess.of(this.screen);
         var1.setSelectedIndex(var1.getSelectedIndex());
      }
   }

   private void craft() {
      if (bT.player != null) {
         this.place();
         TradeOffer var1 = this.getCurrentTrade();
         if (var1 != null) {
            ItemStack var2 = var1.getSellItem();
            if (!var2.isEmpty()) {
               int var3 = (int)Math.ceil((float)var2.getMaxCount() / var2.getCount());
               var3 = Math.min(var3, var1.getMaxUses() - var1.getUses());
               InvTasks.ah().craftAtSlotIndex(this.screen, var3, 2);
            }
         }
      }
   }

   @Nullable
   public TradeOffer getCurrentTrade() {
      MerchantScreenAccess var1 = MerchantScreenAccess.of(this.screen);
      int var2 = var1.getSelectedIndex();
      TradeOfferList var3 = ((MerchantScreenHandler)(Object)this.screen.getScreenHandler()).getRecipes();
      return var2 >= 0 && var2 < var3.size() ? (TradeOffer)var3.get(var2) : null;
   }
}
