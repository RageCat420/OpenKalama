package me.matl114.utils.config;

import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.commons.lang3.function.Consumers;

public interface ValueAccessor<T> {
   T getValue();

   void setValue(T var1);

   static <T> ValueAccessor<T> holder() {
      return new ValueAccessor<T>() {
         T val;

         @Override
         public T getValue() {
            return this.val;
         }

         @Override
         public void setValue(T value) {
            this.val = value;
         }
      };
   }

   static <T> ValueAccessor<T> holder(T val) {
      ValueAccessor<T> holder = holder();
      holder.setValue(val);
      return holder;
   }

   static <T> ValueAccessor<T> of(Supplier<T> supplier, Consumer<T> consumer) {
      return new ValueAccessor<T>() {
         @Override
         public T getValue() {
            return supplier.get();
         }

         @Override
         public void setValue(T value) {
            consumer.accept(value);
         }
      };
   }

   static <T> ValueAccessor<T> of(AttrKeyValue<T> keyValue) {
      return of(keyValue::getOriginValue, keyValue::setOriginValue);
   }

   static <T> void unsupportWrite(T val) {
      throw new UnsupportedOperationException("Write");
   }

   static <T> ValueAccessor<T> of(Supplier<T> supplier) {
      return of(supplier, ValueAccessor::unsupportWrite);
   }

   static <T> ValueAccessor<T> ofIgnore(Supplier<T> supplier) {
      return of(supplier, Consumers.nop());
   }

   static <T> ValueAccessor<T> of(T value) {
      return of(() -> value);
   }

   static <T> ValueAccessor<T> ofIgnore(T value) {
      return ofIgnore(() -> value);
   }

   static <T extends Number> ValueAccessor<T> numberHolder(T value) {
      if (value instanceof Integer intValue) {
         return (ValueAccessor<T>) new ValueAccessor.Int(intValue);
      } else if (value instanceof Long longValue) {
         return (ValueAccessor<T>) new ValueAccessor.LongValue(longValue);
      } else if (value instanceof Double doubleValue) {
         return (ValueAccessor<T>) new ValueAccessor.DoubleValue(doubleValue);
      } else if (value instanceof Float floatValue) {
         return (ValueAccessor<T>) new ValueAccessor.FloatValue(floatValue);
      } else if (value instanceof Short shortValue) {
         return (ValueAccessor<T>) new ValueAccessor.ShortValue(shortValue);
      } else {
         return (ValueAccessor<T>)(value instanceof Byte byteValue ? new ValueAccessor.ByteValue(byteValue) : holder(value));
      }
   }

   public static class ByteValue implements ValueAccessor<Number> {
      byte value;

      public ByteValue() {
         this((byte)0);
      }

      public ByteValue(byte value) {
         this.value = value;
      }

      public Byte getValue() {
         return this.value;
      }

      public void setValue(Number value) {
         this.value = value.byteValue();
      }
   }

   public static class DoubleValue implements ValueAccessor<Number> {
      double value;

      public DoubleValue() {
         this(0.0);
      }

      public DoubleValue(double value) {
         this.value = value;
      }

      public Double getValue() {
         return this.value;
      }

      public void setValue(Number value) {
         this.value = value.doubleValue();
      }
   }

   public static class FloatValue implements ValueAccessor<Number> {
      float value;

      public FloatValue() {
         this(0.0F);
      }

      public FloatValue(float value) {
         this.value = value;
      }

      public Float getValue() {
         return this.value;
      }

      public void setValue(Number value) {
         this.value = value.floatValue();
      }
   }

   public static class Int implements ValueAccessor<Number> {
      int value;

      public Int() {
         this(0);
      }

      public Int(int value) {
         this.value = value;
      }

      public Integer getValue() {
         return this.value;
      }

      public void setValue(Number value) {
         this.value = value.intValue();
      }
   }

   public static class LongValue implements ValueAccessor<Number> {
      long value;

      public LongValue() {
         this(0L);
      }

      public LongValue(long value) {
         this.value = value;
      }

      public Long getValue() {
         return this.value;
      }

      public void setValue(Number value) {
         this.value = value.longValue();
      }
   }

   public static class ShortValue implements ValueAccessor<Number> {
      short value;

      public ShortValue() {
         this((short)0);
      }

      public ShortValue(short value) {
         this.value = value;
      }

      public Short getValue() {
         return this.value;
      }

      public void setValue(Number value) {
         this.value = value.shortValue();
      }
   }
}
