package me.matl114.managers.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import me.matl114.utils.config.BaseAttrKeyValue;

public class Ref<T> {
   protected Config configReference;
   @Nonnull
   Optional<T> defaultValue = Optional.empty();
   private final List<Consumer<T>> updated = new ArrayList<>();
   private final List<Predicate<T>> validators = new ArrayList<>();

   public boolean hasDefaultValue() {
      return this.defaultValue.isPresent();
   }

   public boolean isValueDifferent() {
      return this.defaultValue.isPresent() && !Objects.equals(this.defaultValue.get(), this.getValue());
   }

   public void resetValue() {
      this.defaultValue.ifPresent(this::setValue);
   }

   public void setDefaultValue(T defaultValue) {
      this.defaultValue = Optional.ofNullable(defaultValue);
   }

   public T getDefaultValue() {
      return this.defaultValue.get();
   }

   public void setConfigReference(Config ref) {
      if (ref != this.configReference) {
         this.configReference = ref;
         if (this.configReference != null) {
            this.configReference.markForSave();
         }
      }
   }

   public T getValue() { }

   public void setValue(T var1) { }

   public void addUpdateListener(Consumer<T> updateListener) {
      if (updateListener != null) {
         this.updated.add(updateListener);
      }
   }

   public void addUpdateListenerWithUpdate(Consumer<T> updateListener) {
      if (updateListener != null) {
         this.updated.add(updateListener);

         try {
            updateListener.accept(this.getValue());
         } catch (Throwable var3) {
         }
      }
   }

   public void removeUpdateListener(Predicate<Consumer<T>> removeListener) {
      this.updated.removeIf(removeListener);
   }

   public void addValidator(Predicate<T> validator) {
      this.validators.add(validator);
   }

   public void removeValidator(Predicate<Predicate<T>> removeListener) {
      this.validators.removeIf(removeListener);
   }

   public boolean validateUpdateValue(T val) {
      try {
         for (Predicate<T> updateListener : this.validators) {
            if (!updateListener.test(val)) {
               return false;
            }
         }

         return true;
      } catch (Throwable var4) {
         return false;
      }
   }

   public void callUpdate() {
      T val = this.getValue();

      try {
         this.updated.forEach(i -> i.accept(val));
      } catch (Throwable var3) {
      }

      if (this.configReference != null) {
         this.configReference.markForSave();
      }
   }

   public Object getAsPrimitive() { }

   public <W> boolean isSameTypeWith(Ref<W> var1) { }

   public <W> boolean copyValueFrom(Ref<W> var1) { }

   public final BaseAttrKeyValue<T> createKeyValue(String key) {
      BaseAttrKeyValue<T> keyValue = this._createKeyValue0(key);
      keyValue.setUpdater(this::getValue);
      this.validators.forEach(keyValue::addValidator);
      keyValue.addListener(this::setValue);
      return keyValue;
   }

   protected abstract BaseAttrKeyValue<T> _createKeyValue0(String var1);
}
