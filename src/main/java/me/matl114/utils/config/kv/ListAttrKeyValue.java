package me.matl114.utils.config.kv;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.BaseAttrKeyValue;
import me.matl114.utils.config.WrapperFactory;

public class ListAttrKeyValue<T> extends BaseAttrKeyValue<List<T>> {
   protected List<Predicate<T>> elementValidators = new ArrayList<>();

   public ListAttrKeyValue(
      String key, List<T> value, AttrKeyValue.CustomWidgetFactory<List<T>> customWidgetFactory, WrapperFactory<String, List<T>> wrapperFactory
   ) {
      super(key, value, customWidgetFactory, wrapperFactory);
   }

   @Override
   public <W extends AttrKeyValue<List<T>>> W copy() {
      ListAttrKeyValue<T> val = super.copy();
      val.elementValidators = new ArrayList<>(val.elementValidators);
      return (W)val;
   }

   public List<AttrKeyValue<T>> createAttrKeyValueForElements() { }

   public AttrKeyValue<T> createNewAttrKeyValueElement() { }

   public List<Predicate<T>> getElementValidators() {
      return this.elementValidators;
   }
}
