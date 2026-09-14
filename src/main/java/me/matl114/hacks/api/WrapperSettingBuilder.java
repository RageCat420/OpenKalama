package me.matl114.hacks.api;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import me.matl114.managers.config.Config;
import me.matl114.managers.config.ListRef;
import me.matl114.managers.config.MapRef;
import me.matl114.managers.config.Ref;
import me.matl114.managers.input.SimpleHotKey;
import me.matl114.managers.input.SimpleHotKey$InputHandler;
import me.matl114.managers.input.SimpleInputManager;

public class WrapperSettingBuilder<W> extends Config.SettingBuilder<W> {
   WrapperConfigRef<W> wrapperConfig;
   BaseModule module;
   SimpleHotKey e;

   public <W1 extends Ref<W>> WrapperSettingBuilder<W> apply(Consumer<W1> va) {
      return (WrapperSettingBuilder<W>)super.apply(va);
   }

   public WrapperSettingBuilder<W> path(String... path) {
      return (WrapperSettingBuilder<W>)super.path(path);
   }

   public WrapperSettingBuilder<W> registerModuleEntry() {
      throw new UnsupportedOperationException();
   }

   @Override
   public <W1 extends Ref<W>> W1 build() {
      Ref var1 = super.build();
      this.module.registerConfigWrapper(this.getWrapper());
      if (this.e != null) {
         this.module.registerHotkey(this.e);
      }

      return (W1)var1;
   }

   public WrapperSettingBuilder(MapRef ref, Config rootConfig, Class<W> clazz, BaseModule module) {
      super(ref, rootConfig, clazz);
      this.module = module;
   }

   public WrapperSettingBuilder<W> show(BooleanSupplier supplier) {
      this.addPost(() -> this.getWrapper().addShowPredicate(supplier));
      return this;
   }

   public WrapperSettingBuilder<W> validator(Predicate<W> va) {
      super.validator(this.module.registerReason(va, "config validator"));
      return this;
   }

   private WrapperConfigRef<W> getWrapper() {
      if (this.wrapperConfig == null) {
         this.wrapperConfig = new WrapperConfigRef<>(this.getRef(), this.rootConfig, this.path);
      }

      return this.wrapperConfig;
   }

   public WrapperSettingBuilder<W> hideConfig() {
      this.addPost(() -> this.getWrapper().hideConfig());
      return this;
   }

   public WrapperSettingBuilder<W> defaultValue(W val) {
      return (WrapperSettingBuilder<W>)super.defaultValue(val);
   }

   public WrapperSettingBuilder<W> registerHotkey(SimpleHotKey$InputHandler path) {
      WrapperSettingBuilder var2 = (WrapperSettingBuilder)super.registerHotkey(path);
      this.e = (SimpleHotKey)SimpleInputManager.h().getHotkey(String.join(".", this.path));
      return var2;
   }

   public WrapperSettingBuilder<W> showConfig() {
      this.addPost(() -> this.getWrapper().showConfig());
      return this;
   }

   public WrapperSettingBuilder<W> listValidator(Predicate<String> va) {
      if (this.getRef() instanceof ListRef var3) {
         var3.addElementValidator(this.module.registerReason(va, "config validator"));
         return this;
      } else {
         throw new UnsupportedOperationException("Not a list");
      }
   }

   public WrapperSettingBuilder<W> updateListener(Consumer<W> va) {
      super.updateListener(this.module.registerReason(va, "config update listener"));
      return this;
   }

   public WrapperSettingBuilder<W> experimental() {
      this.addPost(() -> this.getWrapper().r(true));
      return this;
   }
}
