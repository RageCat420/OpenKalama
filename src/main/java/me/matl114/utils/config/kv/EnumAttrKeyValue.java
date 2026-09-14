package me.matl114.utils.config.kv;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.google.common.util.concurrent.Runnables;
import com.mojang.datafixers.util.Pair;
import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import me.matl114.gui.KalamaHelperHelperB;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ColorSampler;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.KalamaHelperHelperJ;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.elements.ColorBoxElement;
import me.matl114.gui.elements.IconElement;
import me.matl114.managers.config.ConfigEnum;
import me.matl114.utils.ReflectUtils;
import me.matl114.utils.commands.params.impl.StringArgumentResult;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.BaseAttrKeyValue;
import me.matl114.utils.config.WrapperFactory;
import net.minecraft.text.Text;
import org.apache.commons.lang3.function.Consumers;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class EnumAttrKeyValue<T> extends BaseAttrKeyValue<T> {
   protected final Map<String, T> finiteValueMap;
   Class<T> identifier;
   public static final AttrKeyValue.CustomWidgetFactory<?> ENUM_WIDGET_FACTORY = (s, x, y, dx, dy) -> s instanceof EnumAttrKeyValue attrKeyValue
      ? attrKeyValue.generateSwitchingButton(x, y, dx, dy, Consumers.nop())
      : BaseAttrKeyValue.generateTextInputValueWidget(s, x, y, dx, dy);

   public static <T> WrapperFactory<String, T> createFiniteMapLookup(Map<String, T> map) {
      Map<T, String> inverseMap = new HashMap<>();

      for (Entry<String, T> re : map.entrySet()) {
         inverseMap.put(re.getValue(), re.getKey());
      }

      return WrapperFactory.of(s -> {
         T rex = map.get(s);
         if (rex != null) {
            return rex;
         } else {
            throw WrapperFactory.PARSE_FAILURE;
         }
      }, inverseMap::get);
   }

   public Map<String, T> getValueMap() {
      return this.finiteValueMap;
   }

   public EnumAttrKeyValue(String key, T value, Class<T> clazz, Map<String, T> finiteValueMap) {
      super(key, value, (AttrKeyValue.CustomWidgetFactory<T>)ENUM_WIDGET_FACTORY, createFiniteMapLookup(finiteValueMap));
      this.finiteValueMap = finiteValueMap;
      this.identifier = clazz;
   }

   public static <T extends Enum<T>> AttrKeyValue.CustomWidgetFactory<T> createEnumWidgetFactory(Class<T> enumClass) {
      Map<String, T> map;
      if (ConfigEnum.class.isAssignableFrom(enumClass)) {
         map = ConfigEnum.getMap(enumClass);
      } else {
         map = ReflectUtils.S(enumClass);
      }

      return createFiniteLookupWidgetFactory(map);
   }

   public static <T> AttrKeyValue.CustomWidgetFactory<T> createFiniteLookupWidgetFactory(Map<String, T> map) {
      Preconditions.checkArgument(!map.isEmpty());
      Class<?> enumClass = map.values().iterator().next().getClass();
      List<Pair<String, Supplier<Text>>> flattenMap;
      if (StringArgumentResult.class.isAssignableFrom(enumClass)) {
         flattenMap = map.entrySet().stream().map(entry -> new Pair<>(entry.getKey(), ((StringArgumentResult)entry.getValue())::resultAsString)).toList();
      } else {
         flattenMap = map.keySet().stream().map(v -> new Pair<>(v, (Supplier<Text>)() -> Text.translatableWithFallback(v, v))).toList();
      }

      return (s, x, y, dx, dy) -> generateSwitchingButton(flattenMap, s, x, y, dx, dy, Runnables.doNothing());
   }

   public DrawableWidget generateSwitchingButton(int x, int y, int dx, int dy, Consumer<EnumAttrKeyValue<T>> callback) {
      List<Pair<String, Supplier<Text>>> flattenMap;
      if (StringArgumentResult.class.isAssignableFrom(this.identifier)) {
         Map<String, StringArgumentResult> valueMap = this.getValueMap();
         flattenMap = valueMap.entrySet().stream().map(entry -> new Pair<>(entry.getKey(), entry.getValue()::resultAsString)).toList();
      } else {
         flattenMap = this.getValueMap().keySet().stream().map(v -> new Pair<>(v, (Supplier<Text>)() -> Text.translatableWithFallback(v, v))).toList();
      }

      return generateSwitchingButton(flattenMap, this, x, y, dx, dy, () -> callback.accept(this));
   }

   public static DrawableWidget generateSwitchingButton(
      List<Pair<String, Supplier<Text>>> flattenMap, AttrKeyValue<?> ex, int x, int y, int dx, int dy, Runnable runnable
   ) {
      int choices = flattenMap.size();
      if (choices > 0) {
         AtomicInteger integer = new AtomicInteger();
         Runnable kvUpdater = () -> {
            String val = ex.getValue();
            int index = -1;

            for (int i = 0; i < choices; i++) {
               if (Objects.equals(val, flattenMap.get(i).getFirst())) {
                  index = i;
                  break;
               }
            }

            if (index == -1) {
               ex.valueChange(ex, (String)flattenMap.get(0).getFirst());
               index = 0;
            } else {
               ex.valueChange(ex, (String)flattenMap.get(index).getFirst());
            }

            integer.set(index);
         };
         kvUpdater.run();
         KalamaHelperHelperCX subScreen = new KalamaHelperHelperCX(x, y, dx, dy);
         Runnable indexUpdater = () -> {
            ex.valueChange(ex, (String)flattenMap.get(integer.get()).getFirst());
            runnable.run();
         };
         boolean needSwitch = dx > 2 * dy;
         int mainDx = needSwitch ? dx - dy : dx;
         subScreen.Q(ExecutableWidget.instance(1, 1, mainDx - 2, dy - 2).eV(new ButtonElement(ign -> {
            kvUpdater.run();
            return (Text)((Supplier)flattenMap.get(integer.get()).getSecond()).get();
         }, ButtonAction.a(() -> {
            kvUpdater.run();
            int index0 = integer.get();
            index0 = (index0 + 1) % choices;
            integer.set(index0);
            indexUpdater.run();
         })).aO(TooltipHandler.ap(List.of(Text.translatable(ex.getKeyName()))))));
         if (needSwitch) {
            MutableBoolean show = new MutableBoolean(false);
            subScreen.Q(
               ExecutableWidget.instance(dx - dy + 2, 2, dy - 4, dy - 4)
                  .eV(
                     IconElement.co(
                        KalamaHelperHelperB.j, KalamaHelperHelperB.k, ButtonAction.a(() -> show.setValue(!show.booleanValue())), eee -> show.booleanValue()
                     )
                  )
            );
            Supplier<KalamaHelperHelperCX> subScreenSupplier = Suppliers.memoize(() -> {
               KalamaHelperHelperCX selectors = new KalamaHelperHelperCX(0, 0, 0, 0).setPriority(1);
               int height = 0;

               for (int i = 0; i < choices; i++) {
                  Pair<String, Supplier<Text>> section = flattenMap.get(i);
                  Supplier<Text> supplier = (Supplier<Text>)section.getSecond();
                  int finalI = i;
                  selectors.Q(ExecutableWidget.instance(0, height, dx - dy - 4, dy).eV(new ColorBoxElement(ButtonAction.a(() -> {
                     integer.set(finalI);
                     indexUpdater.run();
                     show.setValue(false);
                  }), el -> supplier.get(), ColorSampler.of(Color.GRAY.getRGB()), ColorSampler.WHITE, (el, rb) -> {
                     kvUpdater.run();
                     if (integer.get() == finalI) {
                        return -16711936;
                     } else {
                        return rb ? -1 : null;
                     }
                  })));
                  height += dy;
               }

               return selectors;
            });
            ContentDelegateWidget<KalamaHelperHelperCX> dynamicDelegate = new KalamaHelperHelperJ<>(
               () -> show.booleanValue() ? subScreenSupplier.get() : null, 2, dy
            );
            subScreen.Q(dynamicDelegate);
         }

         return subScreen;
      } else {
         return ExecutableWidget.instance(x + 1, y + 1, dx - 2, dy - 2)
            .eV(new ButtonElement(TextProvider.c(Text.empty()), ButtonAction.c()).aO(TooltipHandler.ap(List.of(Text.translatable(ex.getKeyName())))));
      }
   }
}
