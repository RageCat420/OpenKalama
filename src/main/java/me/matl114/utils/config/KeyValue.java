package me.matl114.utils.config;

public interface KeyValue<T> {
   String getKeyName();

   T getOriginValue();

   boolean setOriginValue(T var1);

   boolean isValueValid(T var1);

   boolean isValidate();
}
