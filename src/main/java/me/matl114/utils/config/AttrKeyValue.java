package me.matl114.utils.config;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.complex.RawTextElement;
import me.matl114.gui.complex.config.KalamaHelperHelperE;
import me.matl114.gui.elements.LabelElement;
import me.matl114.utils.ReflectUtils;
import me.matl114.utils.config.kv.AttrKeyValues;
import me.matl114.utils.config.kv.EnumAttrKeyValue;
import me.matl114.utils.config.kv.RegistryAttrKeyValue;
import me.matl114.utils.config.kv.StringListAttrKeyValue;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public interface AttrKeyValue<T> extends KeyValue<T>, PropertyTracker<Object, String> {
   public default WrapperFactory<String, T> getStringifyFactory() { }

   public default String getValue() { }

   public default boolean validateAndUpdate() { }

   default String updateValue(T val) {
      return this.getStringifyFactory().get(val);
   }

   default void valueChangeInternal(Object selectable, T val) {
      this.valueChange(selectable, this.updateValue(val));
   }

   void addListener(Consumer<T> var1);

   void addValidator(Predicate<T> var1);

   default KalamaHelperHelperCX generateKeyValueInput(int x, int y, int keyDx, int blankDx, int inputDx, int dy) {
      return new KalamaHelperHelperE<>(x, y, keyDx + blankDx + inputDx, dy, keyDx, blankDx, inputDx, this);
   }

   default DrawableWidget generateValueWidget(int x, int y, int inputDx, int dy) {
      return this.getCustomWidgetFactory().generateWidget(this, x, y, inputDx, dy);
   }

   public default AttrKeyValue.CustomWidgetFactory<T> getCustomWidgetFactory() { }

   <W extends AttrKeyValue<T>> W copy();

   public static BaseAttrKeyValue<Boolean> bool(String key) {
      return bool(key, false);
   }

   public static BaseAttrKeyValue<Boolean> bool(String key, boolean value) {
      return new BaseAttrKeyValue<>(key, value, AttrKeyValues.BOOLEAN_WIDGET_FACTORY, AttrKeyValues.BOOL_FACTORY);
   }

   public static BaseAttrKeyValue<Integer> integer(String key, int val) {
      return new BaseAttrKeyValue<>(key, val, AttrKeyValues.INT_FACTORY);
   }

   public static BaseAttrKeyValue<Integer> clampedInt(String key, int val, int from, int to) {
      return new AttrKeyValues.ClampedIntAttrKeyValue(key, val, from, to);
   }

   public static BaseAttrKeyValue<Float> floatVal(String keyName, float val) {
      return new BaseAttrKeyValue<>(keyName, val, AttrKeyValues.FLOAT_FACTORY);
   }

   public static BaseAttrKeyValue<Double> doubleVal(String keyName, double val) {
      return new BaseAttrKeyValue<>(keyName, val, AttrKeyValues.DOUBLE_FACTORY);
   }

   public static <T> BaseAttrKeyValue<T> registry(String key, Registry<T> registry, T val) {
      return new RegistryAttrKeyValue<>(key, val, registry);
   }

   public static <T> BaseAttrKeyValue<T> openRegistry(String key, Registry<T> registry, String val) {
      Identifier identifier = Identifier.tryParse(val);
      T val0;
      return identifier != null && (val0 = (T)registry.getOrEmpty(identifier).orElse(null)) != null
         ? new RegistryAttrKeyValue<>(key, val0, registry)
         : new RegistryAttrKeyValue<>(key, val, registry, null);
   }

   public static BaseAttrKeyValue<String> str(String key, String val) {
      return new BaseAttrKeyValue<>(key, val, AttrKeyValues.STRING_FACTORY);
   }

   public static <T> EnumAttrKeyValue<T> enumMap(String key, T val, Map<String, T> finiteValueMap) {
      return new EnumAttrKeyValue<>(key, val, (Class<T>)(val == null ? Enum.class : val.getClass()), finiteValueMap);
   }

   public static <T extends Enum<T>> EnumAttrKeyValue<T> enumMap(String key, T val, Class<T> enumClass) {
      Map<String, T> map = ReflectUtils.S(enumClass);
      return new EnumAttrKeyValue<>(key, val, enumClass, map);
   }

   public static BaseAttrKeyValue<List<String>> list(String key, List<String> list) {
      return new StringListAttrKeyValue(key, list);
   }

   public static BaseAttrKeyValue<Identifier> identifier(String key, Identifier id) {
      return new BaseAttrKeyValue<>(key, id, AttrKeyValues.IDENTIFIER_FACTORY);
   }

   public interface CustomWidgetFactory<T> extends WidgetFactory<AttrKeyValue<T>> {
      public static <T> AttrKeyValue.CustomWidgetFactory<T> cutSizeXLeft(AttrKeyValue.CustomWidgetFactory<T> factory, double portion) {
         return (s1, x, y, dx, dy) -> factory.generateWidget(s1, x, y, (int)(dx * portion), dy);
      }

      public static <T> UnaryOperator<AttrKeyValue.CustomWidgetFactory<T>> cutSizeXLeft(double portion) {
         return w -> cutSizeXLeft(w, portion);
      }

      public static <T> AttrKeyValue.CustomWidgetFactory<T> cutSizeXRight(AttrKeyValue.CustomWidgetFactory<T> factory, double portion) {
         return (s1, x, y, dx, dy) -> {
            int val = (int)(dx * portion) + 1;
            return factory.generateWidget(s1, x + val, y, dx - val, dy);
         };
      }

      public static <T> UnaryOperator<AttrKeyValue.CustomWidgetFactory<T>> cutSizeXRight(double portion) {
         return w -> cutSizeXRight(w, portion);
      }

      public static <T> UnaryOperator<AttrKeyValue.CustomWidgetFactory<T>> withLabel(Text label) {
         return w -> (s111, x, y, dx, dy) -> {
            KalamaHelperHelperCX subScreenWidget = new KalamaHelperHelperCX(x, y, dx, dy);
            subScreenWidget.Q(DisplayWidget.instance(1, 1, dy * 2 - 2, dy - 2).setRenderHandler(new LabelElement(Text.empty(), -1)));
            subScreenWidget.Q(DisplayWidget.instance(0, 0, dy * 2, dy).setRenderHandler(new RawTextElement(label, -1)));
            subScreenWidget.Q(w.generateWidget(s111, dy * 2, 0, dx - dy * 2, dy));
            return subScreenWidget;
         };
      }
   }
}
