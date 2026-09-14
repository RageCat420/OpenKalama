package me.matl114.gui;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import me.matl114.accessors.gui.ScreenAccess;
import me.matl114.accessors.gui.TextFieldAccess;
import me.matl114.events.impl.BlockUpdate;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.DynamicListWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.KalamaHelperHelperJ;
import me.matl114.gui.basic.KalamaHelperHelperP;
import me.matl114.gui.basic.SubSelectable;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.AdvancedScrollElement;
import me.matl114.gui.elements.ColorLabelTextElement;
import me.matl114.gui.elements.IconElement;
import me.matl114.gui.elements.TextFieldElement;
import me.matl114.gui.presets.lists.StringListModifyScreen;
import me.matl114.managers.config.Ref;
import me.matl114.managers.config.Refs;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.collections.MutableRecord;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.BaseAttrKeyValue;
import me.matl114.utils.config.ValueAccessor;
import me.matl114.utils.config.kv.ListAttrKeyValue;
import net.minecraft.text.Text;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class WidgetUtils {
   public static final KalamaHelperHelperQ a = new KalamaHelperHelperQ(140, 10, 180, 18, 2);

   public static DrawableWidget createValueAccessorsEditScreen(
      Text title,
      Supplier<List<Text>> titleTooltips,
      List<Pair<String, ValueAccessor<?>>> accessors,
      KalamaHelperHelperQ layout,
      BlockUpdate palette,
      boolean filter
   ) {
      int var6 = layout.Nj();
      DynamicListWidget var7 = new DynamicListWidget(0, 0, var6);
      var7.ga(
          ExecutableWidget.instance(0, 0, var6, layout.buttonHeight())
            .eV(
               new ColorLabelTextElement(TextProvider.c(title), () -> palette.titleBackgroundColor().getColorInt(), () -> palette.titleTextColor().getColorInt())
                  .aO(TooltipHandler.ar(titleTooltips))
            )
      );
      ArrayList<Pair> var8 = new ArrayList();
      if (filter) {
         ValueAccessor var9 = ValueAccessor.holder("");
         KalamaHelperHelperCX var10 = FilterService.createFilter(var9, str -> {
            if (str != null && !str.isEmpty()) {
               for (Pair var6x : var8) {
                  String var4 = ChatUtils.H((String)var6x.getFirst());
                  if (FilterService.nameMatch(var4, str)) {
                     ((MutableBoolean)var6x.getSecond()).setValue(true);
                  } else {
                     ((MutableBoolean)var6x.getSecond()).setValue(false);
                  }
               }
            } else {
               for (Pair var3 : var8) {
                  ((MutableBoolean)var3.getSecond()).setValue(true);
               }
            }
         }, 0, layout.buttonBlank(), var6, layout.buttonHeight());
         var7.ga(var10);
      }

      for (Pair var20 : accessors) {
         ValueAccessor var11 = (ValueAccessor)var20.getSecond();
         Object var12 = var11.getValue();
         Ref var13 = Refs.wrapInstance(var12);
         String var14 = (String)var20.getFirst();
         KalamaHelperHelperCX var15 = new KalamaHelperHelperCX(0, 0, var6, layout.buttonHeight() + layout.buttonBlank());
         var15.Q(DisplayWidget.instance(0, 0, var6, layout.buttonBlank() + layout.buttonHeight()));
         BaseAttrKeyValue var16 = var13.createKeyValue(var14);
         var16.setUpdater(var11::getValue);
         var16.addListener(var11::setValue);
         var15.Q(e(Optional.of(var11.getValue()), var16, layout, palette));
         MutableBoolean var17 = new MutableBoolean(true);
         var8.add(Pair.of(var14, var17));
         KalamaHelperHelperJ var18 = new KalamaHelperHelperJ<>(() -> var17.getValue() ? var15 : null, 0, 0);
         var7.ga(var18);
      }

      return var7;
   }

   public static boolean isInputWidget(DrawableWidget widget) {
      return widget instanceof ContentDelegateWidget var1 && var1.ef() instanceof TextFieldAccess
         ? true
         : widget instanceof ExecutableWidget var2 && var2.getHandler() instanceof TextFieldElement;
   }

   public static DrawableWidget a(DrawableWidget drawable) {
      DrawableWidget var1 = drawable;

      while (true) {
         while (var1 instanceof SubSelectable) {
            SubSelectable var2 = (SubSelectable)var1;
            var1 = var2.getSelected();
         }

         if (!(var1 instanceof ContentDelegateWidget var3) || !(var3.ef() instanceof DrawableWidget var5)) {
            return var1;
         }

         var1 = var5;
      }
   }

   public static <T> DrawableWidget createOpenListModifyScreenButton(ListAttrKeyValue<T> s, int x, int y, int dx, int dy) {
      return ExecutableWidget.instance(x, y, dx, dy)
         .eV(
            IconElement.cm(
                  KalamaHelperHelperB.f,
                  ButtonAction.a(
                     () -> ScreenAccess.of(new StringListModifyScreen<T>(s, listAttrKeyValue -> s.setOriginValue(listAttrKeyValue.getOriginValue())))
                        .openFromCurrent()
                  )
               )
               .aO(TooltipHandler.ap(KalamaHelperHelperB.b()))
         );
   }

   public static DrawableWidget createCenterScreenWidget(DrawableWidget widget, int totalX, int totalY) {
      ValueAccessor var3 = ValueAccessor.holder(0);
      ValueAccessor var4 = ValueAccessor.holder(false);
      KalamaHelperHelperCX var5 = new KalamaHelperHelperCX(0, 0, totalX, totalY);
      KalamaHelperHelperH var6 = new KalamaHelperHelperH(
         ValueAccessor.ofIgnore(() -> (totalX - widget.getWidth()) / 2 - widget.getX()), ValueAccessor.of(() -> {
            int var4x = (totalY - widget.getHeight()) / 2 - widget.getY();
            if (var4x < 0) {
               var4.setValue(false);
               return (Integer)var3.getValue();
            } else {
               var4.setValue(true);
               var3.setValue(var4x);
               return var4x;
            }
         }, y -> {
            if (!(Boolean)var4.getValue()) {
               var3.setValue(Math.min(y, 0));
            }
         }), totalX, totalY
      );
      var6.Q(widget);
      var5.Q(var6);
      ExecutableWidget var7 = ExecutableWidget.instance(0, 0, 12, totalY)
         .eV(
            new AdvancedScrollElement(
               ValueAccessor.ofIgnore(totalY), ValueAccessor.ofIgnore(() -> widget.getY() + widget.getHeight()), ValueAccessor.of(() -> {
                  int var3x = widget.getY() + widget.getHeight() - totalY;
                  int var4x = -(Integer)var3.getValue();
                  return Math.clamp((double)var4x / var3x, 0.0, 1.0);
               }, p -> {
                  int var4x = widget.getY() + widget.getHeight() - totalY;
                  int var5x = (int)(var4x * p);
                  var3.setValue(-var5x);
               })
            )
         );
      KalamaHelperHelperJ var8 = new KalamaHelperHelperJ<>(
         () -> widget.getY() + widget.getHeight() > 1.14514 * totalY ? var7 : null,
         ValueAccessor.ofIgnore(() -> (totalX + widget.getWidth()) / 2 - widget.getX()),
         ValueAccessor.ofIgnore(0)
      );
      var5.Q(var8);
      return var5;
   }

   public static DrawableWidget g(
      Text title, Supplier<List<Text>> titleTooltips, List<Pair<String, ValueAccessor<?>>> accessors, KalamaHelperHelperQ layout, BlockUpdate palette
   ) {
      return createValueAccessorsEditScreen(title, titleTooltips, accessors, layout, palette, false);
   }

   public static <T> me.matl114.gui.complex.config.KalamaHelperHelperE<T> d(Ref<T> ref, String keyName, KalamaHelperHelperQ layout, BlockUpdate palette) {
      return e(ref.hasDefaultValue() ? Optional.of((T)ref.getDefaultValue()) : Optional.empty(), ref.createKeyValue(keyName), layout, palette);
   }

   public static DrawableWidget createMutableRecordEditScreen(
      Text title,
      Supplier<List<Text>> titleTooltips,
      MutableRecord configs,
      Function<String, String> translationKeyFunction,
      KalamaHelperHelperQ layout,
      BlockUpdate palette
   ) {
      ArrayList var6 = new ArrayList();

      for (Pair var8 : configs.a()) {
         String var9 = (String)var8.getFirst();
         ValueAccessor var10 = ValueAccessor.of(() -> configs.b(var9), v -> configs.e(var9, v));
         String var11 = (String)translationKeyFunction.apply(var9);
         var6.add(Pair.of(var11, var10));
      }

      return g(title, titleTooltips, var6, layout, palette);
   }

   public static <T> me.matl114.gui.complex.config.KalamaHelperHelperE<T> e(
      Optional<T> ref, AttrKeyValue<T> attrKeyValue, KalamaHelperHelperQ layout, BlockUpdate palette
   ) {
       return new me.matl114.gui.complex.config.KalamaHelperHelperA<T>(
            0, layout.buttonBlank(), layout.Nj(), layout.buttonHeight(), layout.indexWidth(), layout.blankWidth(), layout.buttonWidth(), ref, attrKeyValue
         ) {
            @Override
            public DrawableWidget ab() {
               return ExecutableWidget.instance(0, layout.buttonBlank(), layout.indexWidth(), layout.buttonHeight())
                  .eV(
                     new ColorLabelTextElement(
                           TextProvider.c(this.eK()), () -> palette.keyTextColor().getColorInt(), () -> palette.keyBackgroundColor().getColorInt()
                        )
                        .aO(TooltipHandler.ar(this::eL))
                  );
            }
         };
   }

   public static List<DrawableWidget> b(DrawableWidget drawable) {
      ArrayList var1 = new ArrayList();
      DrawableWidget var2 = drawable;

      while (true) {
         while (var2 instanceof SubSelectable) {
            SubSelectable var3 = (SubSelectable)var2;
            DrawableWidget var4 = var3.getSelected();
            var1.add(var2);
            var2 = var4;
         }

         if (!(var2 instanceof ContentDelegateWidget var5) || !(var5.ef() instanceof DrawableWidget var6)) {
            if (var2 != null) {
               var1.add(var2);
            }

            return var1;
         }

         var1.add(var2);
         var2 = var6;
      }
   }

   public static KalamaHelperHelperP j(IntConsumer xAccessor, IntConsumer yAccessor) {
      return new KalamaHelperHelperF(xAccessor, yAccessor);
   }

   public static <T> DrawableWidget l(Supplier<ListAttrKeyValue<T>> attrCreator, Consumer<List<T>> listConsumer, int x, int y, int dx, int dy) {
      return ExecutableWidget.instance(x, y, dx, dy)
         .eV(
            IconElement.cm(
                  KalamaHelperHelperB.f,
                  ButtonAction.a(
                     () -> {
                        ListAttrKeyValue<T> var2 = attrCreator.get();
                        ScreenAccess.of(new StringListModifyScreen<T>(var2, listAttrKeyValue -> listConsumer.accept(listAttrKeyValue.getOriginValue())))
                           .openFromCurrent();
                     }
                  )
               )
               .aO(TooltipHandler.ap(KalamaHelperHelperB.b()))
         );
   }
}
