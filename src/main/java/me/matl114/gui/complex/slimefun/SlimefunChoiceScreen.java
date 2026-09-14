package me.matl114.gui.complex.slimefun;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import me.matl114.gui.a.e.KalamaHelperHelperA;
import me.matl114.gui.basic.AbstractElement;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.KalamaHelperHelperP;
import me.matl114.gui.basic.RenderHandler;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.elements.PlateElement;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.ScreenUtils;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.ValueAccessor;
import me.matl114.utils.config.kv.EnumAttrKeyValue;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class SlimefunChoiceScreen<T> extends SlimefunScreen {
   List<Text> n;
   public static String o = "";
   Function<T, ItemStack> m;
   private static final EnumAttrKeyValue<KalamaHelperHelperI> p = AttrKeyValue.<KalamaHelperHelperI>enumMap(
      "NBT过滤规则",
      KalamaHelperHelperI.BQ,
      (Map<String, KalamaHelperHelperI>)Arrays.stream(KalamaHelperHelperI.values())
         .collect(Collectors.toMap(i -> i.BX, Function.identity(), (existing, replacement) -> existing, LinkedHashMap::new))
   );
   final KalamaHelperHelperA<T> k;
   private static final KalamaHelperHelperG q = new KalamaHelperHelperG();
   ContentDelegateWidget<KalamaHelperHelperA<T>> l;

   @Override
   protected void l() {
      DisplayWidget.instance(this.x + this.backgroundWidth - 3, this.y + 64, 26, 26).<DrawableWidget>setRenderHandler(PlateElement.cg()).addTo(this);
      p.generateSwitchingButton(this.x + this.backgroundWidth + 1, this.y + 68, 18, 18, attr -> {
         if (ScreenUtils.hasShiftDown() && p.getOriginValue() != KalamaHelperHelperI.BQ) {
            this.f();
         } else {
            this.j();
         }
      }).<DrawableWidget>updateRenderHandler(h -> ((AbstractElement)h).aO(TooltipHandler.ar(() -> {
         Builder var0 = ImmutableList.builder();
         var0.addAll(ChatUtils.parseTranslation("widget.gui.slimefun-choice-screen.nbt-filter.tooltips", ""));
         var0.add(Text.translatable("widget.gui.slimefun-choice-screen.nbt-filter.current-option", new Object[]{p.getOriginValue().BY}));
         return var0.build();
      }))).addTo(this);
      TooltipHandler var1 = TooltipHandler.ar(
         () -> {
            Builder var0 = ImmutableList.builder();
            var0.addAll(ChatUtils.parseTranslation("widget.gui.slimefun-choice-screen.item-type-filter.tooltips", ""));
            var0.add(
               Text.translatable(
                  "widget.gui.slimefun-choice-screen.item-type-filter.current-option",
                  new Object[]{
                     q.blacklist
                        ? Text.translatable("widget.gui.slimefun-choice-screen.item-type-filter.blacklist")
                        : Text.translatable("widget.gui.slimefun-choice-screen.item-type-filter.whitelist")
                  }
               )
            );
            var0.add(Text.translatable("widget.gui.slimefun-choice-screen.item-type-filter.list-content"));

            for (Item var2 : q.items) {
               var0.add(var2.getName());
            }

            return var0.build();
         }
      );
      KalamaHelperHelperCX.H(this.x + this.backgroundWidth - 3, this.y + 90, 26, 26)
         .Q(DisplayWidget.instance(0, 0, 26, 26).setRenderHandler(PlateElement.cg()))
         .Q(
            ExecutableWidget.instance(4, 4, 18, 18)
               .<ExecutableWidget>eT(new ButtonElement(TextProvider.c(Text.empty()), ButtonAction.b(left -> {
                  Runnable var2 = this::j;
                  if (ScreenUtils.hasShiftDown()) {
                     q.b(var2);
                  } else if (left) {
                     q.openModifyItemScreen(var2);
                  } else {
                     q.blacklist = !q.blacklist;
                     var2.run();
                  }
               })))
               .setRenderHandler(
                  new AbstractElement()
                     .cD(RenderHandler.ofSingleItem(() -> q.blacklist ? new ItemStack(Items.BLACK_WOOL) : new ItemStack(Items.WHITE_WOOL), 1, 1, false))
                     .aO(var1)
               )
         )
         .addTo(this);
      super.l();
   }

   public void j() {
      this.k.dH().accept(o);
   }

   public SlimefunChoiceScreen(Text title, List<T> values, Function<T, DrawableWidget> widgetFunction, Function<T, ItemStack> itemFilterFunction) {
      this(title, null, () -> values, widgetFunction, itemFilterFunction);
   }

   @Override
   protected List<Text> provideTitleTooltips(DrawableWidget widget) {
      return this.n;
   }

   private void f() {
      p.valueChange(null, KalamaHelperHelperI.BQ.BX);
      this.j();
   }

   @Override
   protected void init() {
      super.init();
      int var1 = this.backgroundHeight - 32;
      this.k.dE(var1);
      this.l = new ContentDelegateWidget<KalamaHelperHelperA<T>>(this.x, this.y, 0, 0).setContentDelegate(this.k).addTo(this);
      if (this.k.dJ() != null) {
         this.eB.eT(KalamaHelperHelperP.aA(this::j));
      }

      ExecutableWidget.instance(this.x + this.backgroundWidth - 3, this.y + 12, 26, 26)
         .<ExecutableWidget>eT(KalamaHelperHelperP.aA(this::close))
         .<DrawableWidget>setRenderHandler(PlateElement.cg().cD(RenderHandler.z(ai, 4, 4, 18, 18)))
         .addTo(this);
   }

   public Supplier<List<T>> g(Supplier<List<T>> originValue) {
      return () -> ((List)originValue.get())
         .stream()
         .filter(i -> p.getOriginValue().BV.test(this.m.apply((T)i)))
         .filter(i -> q.acceptable(this.m.apply((T)i)))
         .toList();
   }

   public SlimefunChoiceScreen<T> h(BiPredicate<String, T> filter) {
      this.k.dC(filter);
      return this;
   }

   @Override
   protected List<Text> getSearchButtonTooltips() {
      return this.k.dJ() != null ? ChatUtils.parseTranslation("widget.gui.slimefun-choice-screen.search.tooltips", "") : super.getSearchButtonTooltips();
   }

   public SlimefunChoiceScreen(
      Text title,
      List<Text> titleTooltips,
      Supplier<List<T>> originValue,
      Function<T, DrawableWidget> widgetFunction,
      Function<T, ItemStack> itemFilterFunction
   ) {
      super(title);
      this.m = itemFilterFunction;
      this.k = new KalamaHelperHelperA<>(
         0,
         20,
         this.backgroundWidth,
         12,
         0,
         this.backgroundHeight - 32,
         -4,
         16,
         16,
         16,
         this.g(originValue),
         null,
         ValueAccessor.of(() -> o, s -> o = s),
         widgetFunction
      );
      this.n = titleTooltips;
   }
}
