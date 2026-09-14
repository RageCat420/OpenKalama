package me.matl114.managers.config;

import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.BaseAttrKeyValue;

public class IntRef extends Ref<Integer> {
   public static final Class<Integer> TYPE = Integer.class;
   int value;

   public IntRef(Object ref) {
      this(((Integer)ref).intValue());
   }

   public static IntRef fromString(String value) {
      try {
         int val = Integer.parseInt(value);
         return new IntRef(val);
      } catch (NumberFormatException var2) {
         return null;
      }
   }

   public Integer getValue() {
      return this.get();
   }

   public int get() {
      return this.value;
   }

   public void setValue(Integer value) {
      this.set(value);
   }

   @Override
   public Object getAsPrimitive() {
      return this.value;
   }

   @Override
   public <W> boolean isSameTypeWith(Ref<W> ref) {
      return ref instanceof IntRef;
   }

   @Override
   public <W> boolean copyValueFrom(Ref<W> otherRef) {
      if (otherRef instanceof IntRef intRef) {
         this.set(intRef.get());
         return true;
      } else {
         return false;
      }
   }

   @Override
   public BaseAttrKeyValue<Integer> _createKeyValue0(String key) {
      return AttrKeyValue.integer(key, this.value);
   }

   public void set(int value) {
      if (this.validateUpdateValue(value)) {
         this.value = value;
         this.callUpdate();
      }
   }

   public IntRef(int value) {
      this.value = value;
   }
}
