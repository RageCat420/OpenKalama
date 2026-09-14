package me.matl114.managers.config;

import java.util.Objects;

public abstract class LazilyRegisterTypeRef<T, W> extends ObjectRef<T> {
   public final String enumType;
   public W enumValue;
   public boolean resolved;

   public LazilyRegisterTypeRef(String type, T object) {
      super(object);
      this.tryRegisterType(object);
      this.enumType = type;
      this.enumValue = this.toLazy(object);
      this.resolved = true;
   }

   public LazilyRegisterTypeRef(String value) {
      super(null);
      String val = this.prefix() + ":";
      if (value.startsWith(val)) {
         value = value.substring(val.length());
      }

      int idx = value.indexOf(":");
      String first = value.substring(0, idx);
      String second = value.substring(idx + 1);
      this.enumType = first;
      this.enumValue = this.fromStringToLazy(second);
      this.tryResolve();
   }

   @Override
   public void setDefaultValue(T defaultValue) {
      this.tryRegisterType(defaultValue);
      this.tryResolve();
      super.setDefaultValue(defaultValue);
   }

   protected abstract void tryRegisterType(T var1);

   protected abstract W toLazy(T var1);

   protected abstract W fromStringToLazy(String var1);

   protected abstract String fromLazyToString(W var1);

   protected abstract String prefix();

   protected abstract void tryResolve() throws RuntimeException;

   @Override
   public final void set(T val) {
      super.set(val);
      this.enumValue = this.toLazy(val);
   }

   @Override
   public final Object getAsPrimitive() {
      return this.prefix() + ":" + this.enumType + ":" + this.fromLazyToString(this.enumValue);
   }

   @Override
   public <W> boolean isSameTypeWith(Ref<W> ref) {
      if (ref instanceof LazilyRegisterTypeRef what && what.getClass() == this.getClass() && Objects.equals(what.enumType, this.enumType)) {
         try {
            if (!this.resolved && what.resolved) {
               this.tryResolve();
               return this.resolved;
            } else if (!what.resolved && this.resolved) {
               what.tryResolve();
               return what.resolved;
            } else {
               return true;
            }
         } catch (Throwable var4) {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public <R> boolean copyValueFrom(Ref<R> otherRef) {
      if (otherRef instanceof LazilyRegisterTypeRef what
         && what.getClass() == this.getClass()
         && Objects.equals(what.enumType, this.enumType)
         && this.isSameTypeWith(what)) {
         try {
            if (!what.resolved) {
               what.tryResolve();
            }

            if (!this.resolved) {
               this.tryResolve();
            }

            if (this.resolved) {
               this.set((T)what.get());
            } else {
               this.enumValue = (W)what.enumValue;
            }

            return true;
         } catch (Throwable var5) {
         }
      }

      try {
         return this.tryConvert(otherRef);
      } catch (Throwable var4) {
         return false;
      }
   }

   public <R> boolean tryConvert(Ref<R> ref) {
      return false;
   }

   @Override
   public final T get() {
      if (this.resolved) {
         return super.get();
      } else {
         this.tryResolve();
         T val = super.get();
         if (val != null) {
            return val;
         } else {
            throw new IllegalStateException("Access to a lazily registered type instance before it is registered");
         }
      }
   }
}
