package me.matl114.hacks.api;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import me.matl114.managers.config.Config;
import me.matl114.managers.config.Ref;
import me.matl114.utils.config.AttrKeyValue;

public class WrapperConfigRef<T> {
   String keyName;
   String[] path;
   static BooleanSupplier ALWAYS_TRUE = () -> true;
   static BooleanSupplier ALWAYS_FALSE = () -> false;
   Ref<T> ref;
   Config config;
   boolean experimental;
   BooleanSupplier showPredicate = ALWAYS_TRUE;

   public AttrKeyValue<T> createKeyValue() {
      return this.ref.createKeyValue(this.keyName);
   }

   public String[] getPath() {
      return this.path;
   }

   public String getKeyName() {
      return this.keyName;
   }

   public WrapperConfigRef<T> keyName(String keyName) {
      this.keyName = keyName;
      return this;
   }

   public void showConfig() {
      this.showPredicate = ALWAYS_TRUE;
   }

   public WrapperConfigRef<T> config(Config config) {
      this.config = config;
      return this;
   }

   public void hideConfig() {
      this.showPredicate = ALWAYS_FALSE;
   }

   public boolean isExperimental() {
      return this.experimental;
   }

   public BooleanSupplier showPredicate() {
      return this.showPredicate;
   }

   public Config getConfig() {
      return this.config;
   }

   public void addShowPredicate(BooleanSupplier supplier) {
      if (this.showPredicate == ALWAYS_TRUE) {
         this.showPredicate = supplier;
      } else {
         if (this.showPredicate == ALWAYS_FALSE) {
            return;
         }

         this.showPredicate = () -> this.showPredicate.getAsBoolean() && supplier.getAsBoolean();
      }
   }

   public boolean shouldShow() {
      return this.showPredicate.getAsBoolean();
   }

   public WrapperConfigRef<T> m(Ref<T> ref) {
      this.ref = ref;
      return this;
   }

   public Ref<T> h() {
      return this.ref;
   }

   public WrapperConfigRef(Ref<T> ref, Config config, String[] path) {
      this.experimental = false;
      this.ref = Objects.requireNonNull(ref);
      this.path = path;
      this.config = config;
      this.keyName = String.join(".", path);
   }

   public WrapperConfigRef<T> n(BooleanSupplier showPredicate) {
      this.showPredicate = showPredicate;
      return this;
   }

   public WrapperConfigRef<T> p(String[] path) {
      this.path = path;
      return this;
   }

   public boolean f(WrapperConfigRef<?> ref) {
      return this.config == ref.config && Arrays.equals((Object[])this.path, (Object[])ref.path);
   }

   public WrapperConfigRef<T> r(boolean experimental) {
      this.experimental = experimental;
      return this;
   }

   public boolean isEditable() {
      return this.config.getRegistryKey() != null;
   }
}
