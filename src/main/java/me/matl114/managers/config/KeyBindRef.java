package me.matl114.managers.config;

import me.matl114.gui.presets.single.KeyBindConfigurateWidget;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.BaseAttrKeyValue;
import me.matl114.utils.config.WrapperFactory;

public class KeyBindRef extends ObjectRef<MultiKeyBind> {
   public static final Class<MultiKeyBind> TYPE = MultiKeyBind.class;
   public static final AttrKeyValue.CustomWidgetFactory<MultiKeyBind> WIDGET_FACTORY = (s, x, y, dx, dy) -> new KeyBindConfigurateWidget(x, y, dx, dy, s);
   public static final WrapperFactory<String, MultiKeyBind> FACTORY = WrapperFactory.of(s -> {
      if (s.startsWith("hotkey:")) {
         return new MultiKeyBind(s);
      } else {
         throw WrapperFactory.PARSE_FAILURE;
      }
   }, MultiKeyBind::b);

   public KeyBindRef(MultiKeyBind object) {
      super(object);
   }

   @Override
   public Object getAsPrimitive() {
      return this.get().b();
   }

   @Override
   public <W> boolean isSameTypeWith(Ref<W> ref) {
      return ref instanceof KeyBindRef;
   }

   @Override
   public <W> boolean copyValueFrom(Ref<W> otherRef) {
      if (otherRef instanceof KeyBindRef stringRef) {
         this.set(stringRef.get());
         return true;
      } else {
         return false;
      }
   }

   @Override
   public BaseAttrKeyValue<MultiKeyBind> _createKeyValue0(String key) {
      return new BaseAttrKeyValue<>(key, this.get(), WIDGET_FACTORY, FACTORY);
   }

   protected MultiKeyBind validateAndCast(Object val) {
      return (MultiKeyBind)val;
   }

   public static KeyBindRef fromString(String val) {
      if (val.startsWith("hotkey:")) {
         try {
            return new KeyBindRef(new MultiKeyBind(val));
         } catch (Throwable var2) {
         }
      }

      return null;
   }
}
