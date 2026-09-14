package me.matl114.gui;

import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.util.PinyinHelper;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.elements.IconElement;
import me.matl114.hacks.utils.recipes.IRecipeEntry;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.config.ValueAccessor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class FilterService {
   public static KalamaHelperHelperK<String> c = (str, i, bl) -> {
      if (bl) {
         try {
            return Pattern.matches(str, i);
         } catch (Throwable var4) {
            return false;
         }
      } else {
         return i.contains(str);
      }
   };
   public static BiPredicate<String, IRecipeEntry> a = (str, i) -> {
      if (str == null || str.isEmpty()) {
         return true;
      } else if (str.startsWith("@")) {
         String var2 = str.substring(1);
         return i.id().toLowerCase(Locale.ROOT).contains(var2.toLowerCase(Locale.ROOT));
      } else {
         return nameMatch(i.Ct().getName().getString().replaceAll("§.", ""), str);
      }
   };
   protected static Identifier d = new Identifier("minecraft", "container/beacon/cancel");
   public static BiPredicate<String, ItemStack> b = (str, i) -> str != null && !str.isEmpty()
      ? nameMatch(i.getName().getString().replaceAll("§.", ""), str)
      : true;

   public static boolean nameMatch(String name, String filter) {
      if (filter != null && !filter.isEmpty()) {
         filter = filter.toLowerCase(Locale.ROOT);
         name = name.toLowerCase(Locale.ROOT);
         if (name.contains(filter)) {
            return true;
         } else {
            String var2 = PinyinHelper.toPinyin(name, PinyinStyleEnum.INPUT, "").toLowerCase(Locale.ROOT);
            if (var2.contains(filter)) {
               return true;
            } else {
               var2 = PinyinHelper.toPinyin(name, PinyinStyleEnum.FIRST_LETTER, "").toLowerCase(Locale.ROOT);
               return var2.contains(filter);
            }
         }
      } else {
         return true;
      }
   }

   public static KalamaHelperHelperCX createFilter(ValueAccessor<String> accessor, Consumer<String> updateListener, int x, int y, int dx, int dy) {
      ContentDelegateWidget var6 = McWidgetHelpers.c(dy, 0, dx - dy, dy, (t, r) -> {
         if (!Objects.equals(accessor.getValue(), r)) {
            accessor.setValue(r);
            updateListener.accept(r);
         }
      }, (String)accessor.getValue());
      DrawableWidget var7 = DisplayWidget.instance(0, 0, dy, dy)
         .setRenderHandler(
            new ButtonElement(TextProvider.c(Text.empty()), ButtonAction.c())
               .aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.gui.filter-service.reset-filter.tooltips", "")))
         );
      ExecutableWidget var8 = ExecutableWidget.instance(0, 0, dy, dy).eV(IconElement.cm(d, ButtonAction.a(() -> {
         if (var6.ef() != null) {
            ((TextFieldWidget)var6.ef()).setText("");
         }
      })).aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.gui.filter-service.reset-filter.tooltips", ""))));
      return new KalamaHelperHelperCX(x, y, dx, dy).Q(var6).Q(var7).Q(var8);
   }

   public static KalamaHelperHelperCX b(ValueAccessor<String> accessor, Runnable updateListener, int x, int y, int dx, int dy) {
      return createFilter(accessor, v -> updateListener.run(), x, y, dx, dy);
   }

   public static KalamaHelperHelperCX d(ValueAccessor<String> accessor, ValueAccessor<Boolean> useRegex, Runnable acceptor, int x, int y, int dx, int dy) {
      return createFilterWithRegex(accessor, useRegex, (str, bl) -> acceptor.run(), x, y, dx, dy);
   }

   public static KalamaHelperHelperCX createFilterWithRegex(
      ValueAccessor<String> accessor, ValueAccessor<Boolean> useRegex, BiConsumer<String, Boolean> acceptor, int x, int y, int dx, int dy
   ) {
      ContentDelegateWidget var7 = McWidgetHelpers.c(dy, 0, dx - 2 * dy, dy, (t, r) -> {
         if (!Objects.equals(accessor.getValue(), r)) {
            accessor.setValue(r);
            acceptor.accept(r, (Boolean)useRegex.getValue());
         }
      }, (String)accessor.getValue());
      DrawableWidget var8 = DisplayWidget.instance(0, 0, dy, dy)
         .setRenderHandler(
            new ButtonElement(TextProvider.c(Text.empty()), ButtonAction.c())
               .aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.gui.filter-service.reset-filter.tooltips", "")))
         );
      ExecutableWidget var9 = ExecutableWidget.instance(0, 0, dy, dy).eV(IconElement.cm(d, ButtonAction.a(() -> {
         if (var7.ef() != null) {
            ((TextFieldWidget)var7.ef()).setText("");
         }
      })).aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.gui.filter-service.reset-filter.tooltips", ""))));
      ExecutableWidget var10 = ExecutableWidget.instance(dx - dy, 0, dy, dy).eV(new ButtonElement(el -> {
         MutableText var2 = Text.literal("(.*)");
         if ((Boolean)useRegex.getValue()) {
            var2 = var2.withColor(-16711936);
         }

         return var2;
      }, ButtonAction.a(() -> {
         useRegex.setValue(!(Boolean)useRegex.getValue());
         acceptor.accept((String)accessor.getValue(), (Boolean)useRegex.getValue());
      })).cw(el -> (Boolean)useRegex.getValue()).aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.gui.filter-service.use-regex.tooltips", ""))));
      return new KalamaHelperHelperCX(x, y, dx, dy).Q(var7).Q(var8).Q(var9).Q(var10);
   }
}
