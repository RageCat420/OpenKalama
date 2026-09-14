package me.matl114.managers.config;

import javax.annotation.Nonnull;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.BaseAttrKeyValue;

public class FlagRef extends Ref<Boolean> {
   public static final Class<Boolean> TYPE = Boolean.class;
   boolean flag;

   public FlagRef(Object newFlag) {
      this(((Boolean)newFlag).booleanValue());
   }

   public static FlagRef fromString(String value) {
      if ("true".equals(value)) {
         return new FlagRef(true);
      } else {
         return "false".equals(value) ? new FlagRef(false) : null;
      }
   }

   @Nonnull
   public Boolean getValue() {
      return this.get();
   }

   public boolean get() {
      return this.flag;
   }

   public void setValue(Boolean value) {
      this.set(value);
   }

   @Override
   public Object getAsPrimitive() {
      return this.flag;
   }

   @Override
   public <W> boolean isSameTypeWith(Ref<W> ref) {
      return ref instanceof FlagRef;
   }

   @Override
   public <W> boolean copyValueFrom(Ref<W> otherRef) {
      if (otherRef instanceof FlagRef flag) {
         this.set(flag.get());
         return true;
      } else if (otherRef instanceof IntRef intRef) {
         if (intRef.get() == 1) {
            this.set(true);
            return true;
         } else if (intRef.get() == 0) {
            this.set(false);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public BaseAttrKeyValue<Boolean> _createKeyValue0(String key) {
      return AttrKeyValue.bool(key, this.flag);
   }

   public void set(boolean val) {
      if (this.validateUpdateValue(val)) {
         this.flag = val;
         this.callUpdate();
      }
   }

   public void toggle() {
      this.set(!this.get());
   }

   public FlagRef(boolean flag) {
      this.flag = flag;
   }
}
