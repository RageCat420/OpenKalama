package me.matl114.utils.config.kv;

import java.util.function.Consumer;
import java.util.function.Predicate;
import me.matl114.managers.config.NBTType;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.WrapperFactory;

public class WrapperAttrKeyValue<W, T> implements AttrKeyValue<T>, Cloneable {
   public final WrapperFactory<T, W> wrapperFactory;
   public final AttrKeyValue<W> delegate;
   public final AttrKeyValue.CustomWidgetFactory<T> factoryOverride;
   public final WrapperFactory<String, T> stringifyFactory;

   public WrapperAttrKeyValue(AttrKeyValue<W> attrKeyValue, WrapperFactory<T, W> wrapperFactory) {
      this(attrKeyValue, wrapperFactory, (AttrKeyValue.CustomWidgetFactory<T>)null);
   }

   public WrapperAttrKeyValue(AttrKeyValue<W> attrKeyValue, WrapperFactory<T, W> wrapperFactory, NBTType<T> nbtType) {
      this(attrKeyValue, wrapperFactory, nbtType.customWidgetFactory());
   }

   public WrapperAttrKeyValue(AttrKeyValue<W> attrKeyValue, WrapperFactory<T, W> wrapperFactory, AttrKeyValue.CustomWidgetFactory<T> factoryOverride) {
      this.wrapperFactory = wrapperFactory;
      this.delegate = attrKeyValue;
      this.factoryOverride = factoryOverride;
      WrapperFactory<String, W> factory = attrKeyValue.getStringifyFactory();
      this.stringifyFactory = factory.concat(wrapperFactory.inverse());
   }

   @Override
   public String getKeyName() {
      return this.delegate.getKeyName();
   }

   @Override
   public WrapperFactory<String, T> getStringifyFactory() {
      return this.stringifyFactory;
   }

   @Override
   public String getValue() {
      return this.delegate.getValue();
   }

   @Override
   public T getOriginValue() {
      return this.wrapperFactory.get(this.delegate.getOriginValue());
   }

   @Override
   public boolean isValueValid(T val) {
      W wp;
      try {
         wp = this.wrapperFactory.create(val);
      } catch (Throwable var4) {
         return false;
      }

      return this.delegate.isValueValid(wp);
   }

   @Override
   public boolean setOriginValue(T val) {
      W wp;
      try {
         wp = this.wrapperFactory.create(val);
      } catch (Throwable var4) {
         return false;
      }

      return this.delegate.setOriginValue(wp);
   }

   @Override
   public boolean validateAndUpdate() {
      return this.delegate.validateAndUpdate();
   }

   @Override
   public String updateValue(T val) {
      return this.delegate.updateValue(this.wrapperFactory.create(val));
   }

   @Override
   public boolean isValidate() {
      return this.delegate.isValidate();
   }

   @Override
   public void addListener(Consumer<T> li) {
      this.delegate.addListener(va -> li.accept(this.wrapperFactory.get(va)));
   }

   @Override
   public void addValidator(Predicate<T> validator) {
      this.delegate.addValidator(va -> validator.test(this.wrapperFactory.get(va)));
   }

   @Override
   public AttrKeyValue.CustomWidgetFactory<T> getCustomWidgetFactory() {
      return this.factoryOverride != null ? this.factoryOverride : (AttrKeyValue.CustomWidgetFactory<T>)(Object)this.delegate.getCustomWidgetFactory();
   }

   @Override
   public <W extends AttrKeyValue<T>> W copy() {
      return (W)(Object)this.clone();
   }

   public void valueChange(Object object, String string) {
      this.delegate.valueChange(object, string);
   }

   public WrapperAttrKeyValue<W, T> clone() {
      try {
         return (WrapperAttrKeyValue<W, T>)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new AssertionError();
      }
   }
}
