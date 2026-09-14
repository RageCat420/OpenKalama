package me.matl114.managers.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.BaseAttrKeyValue;

public class ListRef extends ObjectRef<List<String>> {
   public static final Class<List<String>> TYPE = (Class<List<String>>)(Class<?>)List.class;
   public List<Predicate<String>> elementValidator = new ArrayList<>();

   public ListRef(List<String> object) {
      super(object);
      this.addValidator(this::validateInternal);
   }

   public boolean validateElement(String val) {
      for (Predicate<String> validator : this.elementValidator) {
         if (!validator.test(val)) {
            return false;
         }
      }

      return true;
   }

   public void addElementValidator(Predicate<String> validator) {
      this.elementValidator.add(validator);
   }

   public void removeElementValidator(Predicate<Predicate<String>> validator) {
      this.elementValidator.removeIf(validator);
   }

   private boolean validateInternal(List<String> v) {
      return v.stream().allMatch(this::validateElement);
   }

   @Override
   public Object getAsPrimitive() {
      return this.get().stream().map(Object::toString).toList();
   }

   @Override
   public <W> boolean isSameTypeWith(Ref<W> ref) {
      return ref instanceof ListRef;
   }

   @Override
   public <W> boolean copyValueFrom(Ref<W> otherRef) {
      if (otherRef instanceof ListRef listRef) {
         this.set(new ArrayList<>(listRef.get()));
         return true;
      } else {
         return false;
      }
   }

   @Override
   public BaseAttrKeyValue<List<String>> _createKeyValue0(String key) {
      return AttrKeyValue.list(key, this.get());
   }

   protected List<String> validateAndCast(Object val) {
      return (List<String>)val;
   }

   public List<Predicate<String>> getElementValidator() {
      return this.elementValidator;
   }
}
